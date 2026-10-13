
package repository

import android.content.Context
import android.net.Uri
import com.dogfellas.app.data.AuthRepository
import com.dogfellas.app.data.api.ActualizarAnimalReportadoRequest
import com.dogfellas.app.data.api.ApiClient
import com.dogfellas.app.data.api.CrearAnimalReportadoRequest
import com.dogfellas.app.model.AnimalReportado
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.time.Instant
import kotlin.coroutines.resume

class AnimalReportadoRepository {

    private val authRepository = AuthRepository()
    private val httpClient = OkHttpClient()

    companion object {
        private const val CLOUD_NAME = "dlhofq5i"
        private const val UPLOAD_PRESET = "dogfellas_upload"

        private const val UPLOAD_URL =
            "https://api.cloudinary.com/v1_1/$CLOUD_NAME/image/upload"
    }

    // =====================================================
    // CONSULTAR UN REPORTE EN POSTGRESQL
    // =====================================================

    suspend fun obtenerAnimalPorId(
        reporteId: String
    ): Result<AnimalReportado> {
        return try {
            val id = reporteId.toIntOrNull()
                ?: return Result.failure(
                    Exception("El identificador del reporte no es numérico.")
                )

            val respuesta = withContext(Dispatchers.IO) {
                ApiClient.animalReportadoApi.obtenerAnimalPorId(id)
            }

            if (!respuesta.isSuccessful) {
                return Result.failure(
                    Exception(
                        "No se pudo consultar el reporte. " +
                            "Código: ${respuesta.code()}\n" +
                            (respuesta.errorBody()?.string() ?: "")
                    )
                )
            }

            val datos = respuesta.body()
                ?: return Result.failure(
                    Exception("La API no devolvió información del animal.")
                )

            val ubicacion =
                if (datos.latitud != null && datos.longitud != null) {
                    GeoPoint(datos.latitud, datos.longitud)
                } else {
                    null
                }

            val fechaMillis = try {
                Instant.parse(datos.fecha_reporte).toEpochMilli()
            } catch (_: Exception) {
                0L
            }

            Result.success(
                AnimalReportado(
                    id = datos.id.toString(),
                    especie = datos.especie,
                    condicion = datos.condicion,
                    urgencia = datos.urgencia,
                    descripcion = datos.descripcion,
                    fotoUri = datos.foto_uri.orEmpty(),
                    ubicacion = ubicacion,
                    usuarioReporta = datos.usuario_reporta.orEmpty(),
                    estado = datos.estado,
                    fechaReporte = fechaMillis
                )
            )
        } catch (e: Exception) {
            Result.failure(
                Exception(
                    e.message ?: "No fue posible consultar el reporte.",
                    e
                )
            )
        }
    }

    // =====================================================
    // ACTUALIZAR EL ESTADO EN POSTGRESQL
    // =====================================================

    suspend fun actualizarEstadoAnimal(
        reporteId: String,
        nuevoEstado: String
    ): Result<AnimalReportado> {
        return try {
            val id = reporteId.toIntOrNull()
                ?: return Result.failure(
                    Exception("El identificador del reporte no es numérico.")
                )

            val respuesta = withContext(Dispatchers.IO) {
                ApiClient.animalReportadoApi.actualizarAnimalReportado(
                    animalId = id,
                    datos = ActualizarAnimalReportadoRequest(
                        estado = nuevoEstado
                    )
                )
            }

            if (!respuesta.isSuccessful) {
                val detalle = respuesta.errorBody()?.string()

                return Result.failure(
                    Exception(
                        "No se pudo actualizar el estado. " +
                            "Código: ${respuesta.code()}\n" +
                            (detalle ?: "")
                    )
                )
            }

            val datos = respuesta.body()
                ?: return Result.failure(
                    Exception("La API no devolvió el animal actualizado.")
                )

            val ubicacion =
                if (datos.latitud != null && datos.longitud != null) {
                    GeoPoint(datos.latitud, datos.longitud)
                } else {
                    null
                }

            val fechaMillis = try {
                Instant.parse(datos.fecha_reporte).toEpochMilli()
            } catch (_: Exception) {
                0L
            }

            Result.success(
                AnimalReportado(
                    id = datos.id.toString(),
                    especie = datos.especie,
                    condicion = datos.condicion,
                    urgencia = datos.urgencia,
                    descripcion = datos.descripcion,
                    fotoUri = datos.foto_uri.orEmpty(),
                    ubicacion = ubicacion,
                    usuarioReporta = datos.usuario_reporta.orEmpty(),
                    estado = datos.estado,
                    fechaReporte = fechaMillis
                )
            )
        } catch (e: Exception) {
            Result.failure(
                Exception(
                    e.message ?: "No fue posible actualizar el estado.",
                    e
                )
            )
        }
    }

    // =====================================================
    // GUARDAR REPORTE
    // =====================================================

    suspend fun guardarReporte(
        animal: AnimalReportado,
        context: Context
    ): Result<String> {
        return try {
            val token = obtenerToken()

            if (token == null) {
                return Result.failure(
                    Exception("No fue posible obtener el token de Firebase.")
                )
            }

            var fotoUrl = ""

            if (animal.fotoUri.isNotBlank()) {
                fotoUrl = subirFotoCloudinary(
                    uri = Uri.parse(animal.fotoUri),
                    context = context
                )
            }

            val ubicacion = animal.ubicacion

            val datos = CrearAnimalReportadoRequest(
                especie = animal.especie,
                condicion = animal.condicion,
                urgencia = animal.urgencia,
                descripcion = animal.descripcion,
                foto_uri = fotoUrl.ifBlank { null },
                latitud = ubicacion?.latitude,
                longitud = ubicacion?.longitude,
                estado = animal.estado
            )

            val respuesta = ApiClient.animalReportadoApi
                .crearAnimalReportado(
                    authorization = "Bearer $token",
                    datos = datos
                )

            if (respuesta.isSuccessful) {
                val resultado = respuesta.body()
                    ?: return Result.failure(
                        Exception(
                            "FastAPI respondió correctamente, " +
                                "pero no devolvió el reporte."
                        )
                    )

                Result.success(resultado.id.toString())
            } else {
                val error = respuesta.errorBody()?.string()

                Result.failure(
                    Exception(
                        "FastAPI no pudo guardar el reporte.\n" +
                            "Código: ${respuesta.code()}\n" +
                            "Detalle: ${error ?: "Sin detalle"}"
                    )
                )
            }
        } catch (e: Exception) {
            Result.failure(
                Exception(
                    e.message ?: "No fue posible guardar el reporte.",
                    e
                )
            )
        }
    }

    // =====================================================
    // OBTENER TOKEN DE FIREBASE
    // =====================================================

    private suspend fun obtenerToken(): String? {
        return withContext(Dispatchers.IO) {
            suspendCancellableCoroutine { continuation ->
                authRepository.getFirebaseIdToken { token, _ ->
                    if (continuation.isActive) {
                        continuation.resume(token)
                    }
                }
            }
        }
    }

    // =====================================================
    // SUBIR FOTO A CLOUDINARY
    // =====================================================

    private suspend fun subirFotoCloudinary(
        uri: Uri,
        context: Context
    ): String = withContext(Dispatchers.IO) {

        val archivoTemporal = File.createTempFile(
            "dogfellas_",
            ".jpg",
            context.cacheDir
        )

        try {
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: throw Exception(
                    "No se pudo abrir la imagen seleccionada."
                )

            inputStream.use { input ->
                archivoTemporal.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            val requestFile = archivoTemporal.asRequestBody(
                "image/*".toMediaType()
            )

            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
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

            val request = Request.Builder()
                .url(UPLOAD_URL)
                .post(requestBody)
                .build()

            httpClient.newCall(request).execute().use { response ->
                val contenido = response.body?.string().orEmpty()

                if (!response.isSuccessful) {
                    throw Exception(
                        "Cloudinary no pudo subir la foto. " +
                            "Código: ${response.code}"
                    )
                }

                val json = JSONObject(contenido)
                json.getString("secure_url")
            }
        } finally {
            archivoTemporal.delete()
        }
    }
}
