package repository

import android.util.Log
import com.dogfellas.app.data.AuthRepository
import com.dogfellas.app.data.api.ApiClient
import com.dogfellas.app.model.AnimalReportado
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class AdopcionesRepository {

    private val authRepository = AuthRepository()

    suspend fun obtenerAnimalesEnAdopcion():
            Result<List<AnimalReportado>> {

        return try {

            Log.d(
                "ADOPCIONES_REPO",
                "Consultando FastAPI..."
            )

            val token = obtenerToken()

            if (token.isNullOrBlank()) {

                return Result.failure(
                    Exception(
                        "No fue posible obtener el token de Firebase."
                    )
                )
            }

            val respuesta =
                ApiClient.adopcionesApi
                    .obtenerAnimalesEnAdopcion(
                        authorization = "Bearer $token"
                    )

            if (!respuesta.isSuccessful) {

                val error =
                    respuesta.errorBody()?.string()

                Log.e(
                    "ADOPCIONES_REPO",
                    "FastAPI respondió con código ${respuesta.code()}"
                )

                return Result.failure(
                    Exception(
                        "FastAPI respondió con código ${respuesta.code()}.\n" +
                            "${error ?: "Sin detalle"}"
                    )
                )
            }

            val datos = respuesta.body()

            if (datos == null) {

                return Result.failure(
                    Exception(
                        "FastAPI no devolvió los animales en adopción."
                    )
                )
            }

            Log.d(
                "ADOPCIONES_REPO",
                "Animales recibidos: ${datos.size}"
            )

            val animales =
                datos.map { reporte ->

                    val ubicacion =
                        if (
                            reporte.latitud != null &&
                            reporte.longitud != null
                        ) {
                            GeoPoint(
                                reporte.latitud,
                                reporte.longitud
                            )
                        } else {
                            null
                        }

                    AnimalReportado(
                        id = reporte.id.toString(),
                        especie = reporte.especie,
                        condicion = reporte.condicion,
                        urgencia = reporte.urgencia,
                        descripcion = reporte.descripcion,
                        fotoUri = reporte.foto_uri ?: "",
                        ubicacion = ubicacion,
                        usuarioReporta =
                            reporte.usuario_reporta ?: "",
                        estado = reporte.estado,
                        fechaReporte =
                            convertirFecha(
                                reporte.fecha_reporte
                            )
                    )
                }

            Log.d(
                "ADOPCIONES_REPO",
                "Animales convertidos: ${animales.size}"
            )

            Result.success(animales)

        } catch (e: Exception) {

            Log.e(
                "ADOPCIONES_REPO",
                "Error consultando FastAPI",
                e
            )

            Result.failure(
                Exception(
                    e.message
                        ?: "No fue posible cargar los animales en adopción.",
                    e
                )
            )
        }
    }

    private suspend fun obtenerToken(): String? {

        return suspendCancellableCoroutine { continuation ->

            authRepository.getFirebaseIdToken { token, _ ->

                if (continuation.isActive) {
                    continuation.resume(token)
                }
            }
        }
    }

    private fun convertirFecha(
        fecha: String
    ): Long {

        return try {

            java.time.Instant
                .parse(
                    fecha
                        .replace(
                            " ",
                            "T"
                        )
                        .let {
                            if (it.endsWith("Z")) {
                                it
                            } else {
                                "${it}Z"
                            }
                        }
                )
                .toEpochMilli()

        } catch (e: Exception) {

            Log.e(
                "ADOPCIONES_REPO",
                "No fue posible convertir la fecha: $fecha",
                e
            )

            0L
        }
    }
}
