package repository

import com.dogfellas.app.data.AuthRepository
import com.dogfellas.app.data.api.ApiClient
import com.dogfellas.app.model.AnimalReportado
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.suspendCancellableCoroutine

class AnimalesCercanosRepository {

    private val authRepository =
        AuthRepository()

    // =====================================================
    // OBTENER TODOS LOS REPORTES
    // =====================================================

    suspend fun obtenerReportes():
            Result<List<AnimalReportado>> {

        return try {

            val token =
                obtenerToken()
                    ?: return Result.failure(
                        Exception(
                            "No fue posible obtener el token de Firebase."
                        )
                    )

            val respuesta =
                ApiClient
                    .animalesCercanosApi
                    .obtenerReportes(
                        authorization =
                            "Bearer $token"
                    )

            if (!respuesta.isSuccessful) {

                val error =
                    respuesta
                        .errorBody()
                        ?.string()

                return Result.failure(
                    Exception(
                        "FastAPI respondió con código ${respuesta.code()}.\n" +
                                "${error ?: "Sin detalle"}"
                    )
                )
            }

            val datos =
                respuesta.body()

            if (datos == null) {

                return Result.failure(
                    Exception(
                        "FastAPI no devolvió los reportes."
                    )
                )
            }

            val animales =
                datos.map { reporte ->

                    convertirAnimal(
                        reporte
                    )
                }

            Result.success(
                animales
            )

        } catch (e: Exception) {

            Result.failure(
                Exception(
                    e.message
                        ?: "No fue posible cargar los reportes.",
                    e
                )
            )
        }
    }

    // =====================================================
    // OBTENER ANIMALES CERCANOS
    // =====================================================

    suspend fun obtenerAnimalesCercanos(
        latitud: Double,
        longitud: Double,
        radioKm: Double = 5.0
    ): Result<List<AnimalReportado>> {

        return try {

            val token =
                obtenerToken()
                    ?: return Result.failure(
                        Exception(
                            "No fue posible obtener el token de Firebase."
                        )
                    )

            val respuesta =
                ApiClient
                    .animalesCercanosApi
                    .obtenerAnimalesCercanos(
                        authorization =
                            "Bearer $token",

                        latitud =
                            latitud,

                        longitud =
                            longitud,

                        radioKm =
                            radioKm
                    )

            if (!respuesta.isSuccessful) {

                val error =
                    respuesta
                        .errorBody()
                        ?.string()

                return Result.failure(
                    Exception(
                        "FastAPI respondió con código ${respuesta.code()}.\n" +
                                "${error ?: "Sin detalle"}"
                    )
                )
            }

            val datos =
                respuesta.body()

            if (datos == null) {

                return Result.failure(
                    Exception(
                        "FastAPI no devolvió los animales cercanos."
                    )
                )
            }

            val animales =
                datos.map { reporte ->

                    convertirAnimal(
                        reporte
                    )
                }

            Result.success(
                animales
            )

        } catch (e: Exception) {

            Result.failure(
                Exception(
                    e.message
                        ?: "No fue posible cargar los animales cercanos.",
                    e
                )
            )
        }
    }

    // =====================================================
    // CONVERTIR RESPUESTA API → MODELO ANDROID
    // =====================================================

    private fun convertirAnimal(
        reporte:
        com.dogfellas.app.data.api.AnimalCercanoApiResponse
    ): AnimalReportado {

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

        return AnimalReportado(

            id =
                reporte.id.toString(),

            especie =
                reporte.especie,

            condicion =
                reporte.condicion,

            urgencia =
                reporte.urgencia,

            descripcion =
                reporte.descripcion,

            fotoUri =
                reporte.foto_uri ?: "",

            ubicacion =
                ubicacion,

            usuarioReporta =
                reporte.usuario_reporta ?: "",

            estado =
                reporte.estado,

            fechaReporte =
                convertirFecha(
                    reporte.fecha_reporte
                )
        )
    }

    // =====================================================
    // CONVERTIR FECHA ISO → TIMESTAMP
    // =====================================================

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
                            if (
                                it.endsWith("Z")
                            ) {
                                it
                            } else {
                                "${it}Z"
                            }
                        }
                )
                .toEpochMilli()

        } catch (e: Exception) {

            0L
        }
    }

    // =====================================================
    // OBTENER TOKEN FIREBASE
    // =====================================================

    private suspend fun obtenerToken():
            String? {

        return suspendCancellableCoroutine { continuation ->

            authRepository
                .getFirebaseIdToken { token, _ ->

                    if (
                        continuation.isActive
                    ) {

                        continuation.resume(
                            token
                        ) {}
                    }
                }
        }
    }
}
