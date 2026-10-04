package repository

import android.content.Context
import android.net.Uri
import com.dogfellas.app.model.AnimalReportado
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File

class AnimalReportadoRepository {

    private val db =
        FirebaseFirestore.getInstance()

    private val animalesCollection =
        db.collection("animales_reportados")

    private val httpClient =
        OkHttpClient()

    companion object {

        private const val CLOUD_NAME =
            "dlhofq5i"

        private const val UPLOAD_PRESET =
            "dogfellas_upload"

        private const val UPLOAD_URL =
            "https://api.cloudinary.com/v1_1/$CLOUD_NAME/image/upload"
    }


    suspend fun guardarReporte(
        animal: AnimalReportado,
        context: Context
    ): Result<String> {

        return try {

            val documento =
                animalesCollection.document()

            var fotoUrl = ""

            // ========================================================
            // SUBIR FOTO A CLOUDINARY
            // ========================================================

            if (animal.fotoUri.isNotBlank()) {

                val uri =
                    Uri.parse(animal.fotoUri)

                fotoUrl =
                    subirFotoCloudinary(
                        uri = uri,
                        context = context
                    )
            }

            // ========================================================
            // OBJETO FINAL
            // ========================================================

            val animalConId =
                animal.copy(
                    id = documento.id,
                    fotoUri = fotoUrl
                )

            // ========================================================
            // FIRESTORE
            // ========================================================

            documento
                .set(animalConId)
                .await()

            Result.success(documento.id)

        } catch (e: Exception) {

            Result.failure(
                Exception(
                    e.message
                        ?: "No fue posible guardar el reporte."
                )
            )
        }
    }


    private suspend fun subirFotoCloudinary(
        uri: Uri,
        context: Context
    ): String = withContext(Dispatchers.IO) {

        val archivoTemporal =
            File.createTempFile(
                "dogfellas_",
                ".jpg",
                context.cacheDir
            )

        try {

            // ========================================================
            // LEER FOTO DEL TELÉFONO
            // ========================================================

            val inputStream =
                context.contentResolver.openInputStream(uri)
                    ?: throw Exception(
                        "No se pudo abrir la imagen seleccionada."
                    )

            inputStream.use { input ->

                archivoTemporal
                    .outputStream()
                    .use { output ->

                        input.copyTo(output)
                    }
            }

            // ========================================================
            // PREPARAR PETICIÓN
            // ========================================================

            val requestFile =
                archivoTemporal.asRequestBody(
                    "image/*".toMediaType()
                )

            val requestBody =
                MultipartBody.Builder()
                    .setType(
                        MultipartBody.FORM
                    )
                    .addFormDataPart(
                        "file",
                        archivoTemporal.name,
                        requestFile
                    )
                    .addFormDataPart(
                        "upload_preset",
                        UPLOAD_PRESET
                    )
                    .build()

            // ========================================================
            // ENVIAR A CLOUDINARY
            // ========================================================

            val request =
                Request.Builder()
                    .url(UPLOAD_URL)
                    .post(requestBody)
                    .build()

            httpClient
                .newCall(request)
                .execute()
                .use { response ->

                    val responseBody =
                        response.body
                            ?.string()
                            .orEmpty()

                    if (!response.isSuccessful) {

                        throw Exception(
                            "Cloudinary rechazó la imagen.\n" +
                                    "Código: ${response.code}\n" +
                                    responseBody
                        )
                    }

                    // =================================================
                    // LEER secure_url
                    // =================================================

                    val json =
                        JSONObject(responseBody)

                    val secureUrl =
                        json.optString(
                            "secure_url"
                        )

                    if (secureUrl.isBlank()) {

                        throw Exception(
                            "Cloudinary no devolvió la URL de la imagen."
                        )
                    }

                    secureUrl
                }

        } finally {

            if (archivoTemporal.exists()) {
                archivoTemporal.delete()
            }
        }
    }
}