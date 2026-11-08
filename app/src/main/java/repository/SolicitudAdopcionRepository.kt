package com.dogfellas.app.repository

import com.dogfellas.app.data.AuthRepository
import com.dogfellas.app.data.api.ActualizarEstadoSolicitudRequest
import com.dogfellas.app.data.api.ApiClient
import com.dogfellas.app.data.api.CrearSolicitudAdopcionRequest
import com.dogfellas.app.data.api.FinalizarAdopcionApiResponse
import com.dogfellas.app.data.api.SolicitudAdopcionApiResponse
import com.dogfellas.app.model.SolicitudAdopcion
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Response
import kotlin.coroutines.resume

class SolicitudAdopcionRepository(
    private val authRepository: AuthRepository = AuthRepository()
) {

    private val api = ApiClient.solicitudAdopcionApi

    // =========================================================
    // CREAR SOLICITUD
    // =========================================================

    suspend fun crearSolicitud(
        solicitud: SolicitudAdopcion
    ): Result<SolicitudAdopcion> {

        val reporteId = solicitud.reporteId.toIntOrNull()
            ?: return Result.failure(
                IllegalArgumentException(
                    "El identificador del animal no es válido."
                )
            )

        return ejecutarSolicitud { token ->
            api.crearSolicitud(
                authorization = "Bearer $token",
                datos = CrearSolicitudAdopcionRequest(
                    reporte_id = reporteId,
                    nombre_completo = solicitud.nombreCompleto,
                    tipo_documento = solicitud.tipoDocumento,
                    numero_documento = solicitud.numeroDocumento,
                    fecha_nacimiento = solicitud.fechaNacimiento,
                    telefono = solicitud.telefono,
                    correo = solicitud.correo,
                    ciudad = solicitud.ciudad,
                    direccion = solicitud.direccion,
                    tipo_vivienda = solicitud.tipoVivienda,
                    vivienda = solicitud.vivienda,
                    autorizacion_propietario =
                        solicitud.autorizacionPropietario,
                    con_quien_vive = solicitud.conQuienVive,
                    personas_hogar = solicitud.personasHogar,
                    acuerdo_familia = solicitud.acuerdoFamilia,
                    ha_tenido_mascotas = solicitud.haTenidoMascotas,
                    experiencia_animales = solicitud.experienciaAnimales,
                    mascotas_actuales = solicitud.mascotasActuales,
                    cantidad_mascotas = solicitud.cantidadMascotas,
                    mascotas_vacunadas = solicitud.mascotasVacunadas,
                    mascotas_esterilizadas = solicitud.mascotasEsterilizadas,
                    situacion_mascotas_anteriores =
                        solicitud.situacionMascotasAnteriores,
                    lugar_animal = solicitud.lugarAnimal,
                    tiempo_solo = solicitud.tiempoSolo,
                    responsable_alimentacion =
                        solicitud.responsableAlimentacion,
                    responsable_gastos = solicitud.responsableGastos,
                    capacidad_veterinaria =
                        solicitud.capacidadVeterinaria,
                    motivo = solicitud.motivo,
                    por_que_animal = solicitud.porQueAnimal,
                    expectativas = solicitud.expectativas,
                    compromiso_adopcion = solicitud.compromisoAdopcion,
                    estado = "PENDIENTE"
                )
            )
        }.map { respuesta ->
            respuesta.toModel()
        }
    }

    // =========================================================
    // CONSULTAR SOLICITUD EXISTENTE DEL USUARIO
    // =========================================================

    suspend fun obtenerSolicitudExistente(
        reporteId: Int,
        usuarioId: String
    ): Result<SolicitudAdopcion?> {

        // El backend identifica al usuario mediante el token Firebase.
        // Se conserva usuarioId para no romper las llamadas existentes.
        if (usuarioId.isBlank()) {
            return Result.failure(
                IllegalArgumentException(
                    "El identificador del usuario no es válido."
                )
            )
        }

        return try {
            val tokenResultado = obtenerToken()

            if (tokenResultado.isFailure) {
                return Result.failure(
                    tokenResultado.exceptionOrNull()
                        ?: Exception(
                            "No fue posible autenticar al usuario."
                        )
                )
            }

            val respuesta = api.obtenerSolicitudExistente(
                authorization = "Bearer ${tokenResultado.getOrThrow()}",
                reporteId = reporteId
            )

            if (respuesta.isSuccessful) {
                /*
                 * El backend devuelve:
                 * - Una solicitud cuando ya existe.
                 * - JSON null cuando todavía no existe.
                 *
                 * En este endpoint, null es una respuesta válida.
                 */
                val solicitud = respuesta.body()?.toModel()
                Result.success(solicitud)

            } else {
                Result.failure(
                    crearErrorHttp(respuesta)
                )
            }

        } catch (error: Exception) {
            Result.failure(
                error.message?.let { Exception(it, error) }
                    ?: Exception(
                        "Ocurrió un error al consultar la solicitud.",
                        error
                    )
            )
        }
    }

    // =========================================================
    // CONSULTAR SOLICITUDES RECIBIDAS POR EL PROPIETARIO
    // =========================================================

    suspend fun obtenerSolicitudesRecibidas(
        reporteId: Int
    ): Result<List<SolicitudAdopcion>> {

        return ejecutarSolicitud { token ->
            api.obtenerSolicitudesRecibidas(
                authorization = "Bearer $token",
                reporteId = reporteId
            )
        }.map { solicitudes ->
            solicitudes.map { solicitud ->
                solicitud.toModel()
            }
        }
    }

    // =========================================================
    // ACEPTAR O RECHAZAR SOLICITUD
    // =========================================================

    suspend fun actualizarEstadoSolicitud(
        solicitudId: Int,
        estado: String
    ): Result<SolicitudAdopcion> {

        if (solicitudId <= 0) {
            return Result.failure(
                IllegalArgumentException(
                    "El identificador de la solicitud no es válido."
                )
            )
        }

        if (estado !in listOf("ACEPTADA", "RECHAZADA")) {
            return Result.failure(
                IllegalArgumentException(
                    "El estado debe ser ACEPTADA o RECHAZADA."
                )
            )
        }

        return ejecutarSolicitud { token ->
            api.actualizarEstadoSolicitud(
                authorization = "Bearer $token",
                solicitudId = solicitudId,
                datos = ActualizarEstadoSolicitudRequest(
                    estado = estado
                )
            )
        }.map { respuesta ->
            respuesta.toModel()
        }
    }

    // =========================================================
    // FINALIZAR ADOPCIÓN
    // =========================================================

    suspend fun finalizarAdopcion(
        reporteId: Int
    ): Result<FinalizarAdopcionApiResponse> {

        if (reporteId <= 0) {
            return Result.failure(
                IllegalArgumentException(
                    "El identificador del animal no es válido."
                )
            )
        }

        return ejecutarSolicitud { token ->
            api.finalizarAdopcion(
                authorization = "Bearer $token",
                reporteId = reporteId
            )
        }
    }

    // =========================================================
    // CONSULTAR SOLICITUD POR ID
    // =========================================================

    suspend fun obtenerSolicitudPorId(
        solicitudId: Int
    ): Result<SolicitudAdopcion> {

        if (solicitudId <= 0) {
            return Result.failure(
                IllegalArgumentException(
                    "El identificador de la solicitud no es válido."
                )
            )
        }

        return ejecutarSolicitud { token ->
            api.obtenerSolicitudPorId(
                authorization = "Bearer $token",
                solicitudId = solicitudId
            )
        }.map { respuesta ->
            respuesta.toModel()
        }
    }

    // =========================================================
    // EJECUTAR PETICIÓN AUTENTICADA
    // =========================================================

    private suspend fun <T> ejecutarSolicitud(
        peticion: suspend (String) -> Response<T>
    ): Result<T> {

        return try {
            val tokenResultado = obtenerToken()

            if (tokenResultado.isFailure) {
                return Result.failure(
                    tokenResultado.exceptionOrNull()
                        ?: Exception(
                            "No fue posible autenticar al usuario."
                        )
                )
            }

            val token = tokenResultado.getOrThrow()
            val respuesta = peticion(token)

            if (respuesta.isSuccessful) {
                val cuerpo = respuesta.body()

                if (cuerpo != null) {
                    Result.success(cuerpo)
                } else {
                    Result.failure(
                        Exception(
                            "El servidor devolvió una respuesta vacía."
                        )
                    )
                }

            } else {
                Result.failure(
                    crearErrorHttp(respuesta)
                )
            }

        } catch (error: Exception) {
            Result.failure(
                error.message?.let { Exception(it, error) }
                    ?: Exception(
                        "Ocurrió un error al comunicarse con el servidor.",
                        error
                    )
            )
        }
    }

    // =========================================================
    // OBTENER TOKEN FIREBASE
    // =========================================================

    private suspend fun obtenerToken(): Result<String> {

        return suspendCancellableCoroutine { continuation ->

            authRepository.getFirebaseIdToken { token, error ->

                if (!continuation.isActive) {
                    return@getFirebaseIdToken
                }

                if (!token.isNullOrBlank()) {
                    continuation.resume(
                        Result.success(token)
                    )
                } else {
                    continuation.resume(
                        Result.failure(
                            Exception(
                                error
                                    ?: "No se pudo obtener el token de Firebase."
                            )
                        )
                    )
                }
            }
        }
    }

    // =========================================================
    // CONSTRUIR ERROR HTTP
    // =========================================================

    private fun crearErrorHttp(
        respuesta: Response<*>
    ): Exception {

        val detalle = try {
            respuesta.errorBody()
                ?.string()
                ?.takeIf { it.isNotBlank() }
        } catch (_: Exception) {
            null
        }

        return Exception(
            detalle ?: "Error HTTP ${respuesta.code()}."
        )
    }

    // =========================================================
    // CONVERTIR RESPUESTA API A MODELO ANDROID
    // =========================================================

    private fun SolicitudAdopcionApiResponse.toModel():
            SolicitudAdopcion {

        return SolicitudAdopcion(
            id = id.toString(),
            reporteId = reporte_id.toString(),
            usuarioId = usuario_id.toString(),
            nombreCompleto = nombre_completo,
            tipoDocumento = tipo_documento,
            numeroDocumento = numero_documento,
            fechaNacimiento = fecha_nacimiento,
            telefono = telefono,
            correo = correo,
            ciudad = ciudad,
            direccion = direccion,
            tipoVivienda = tipo_vivienda,
            vivienda = vivienda,
            autorizacionPropietario = autorizacion_propietario,
            conQuienVive = con_quien_vive,
            personasHogar = personas_hogar,
            acuerdoFamilia = acuerdo_familia,
            haTenidoMascotas = ha_tenido_mascotas,
            experienciaAnimales = experiencia_animales,
            mascotasActuales = mascotas_actuales,
            cantidadMascotas = cantidad_mascotas,
            mascotasVacunadas = mascotas_vacunadas,
            mascotasEsterilizadas = mascotas_esterilizadas,
            situacionMascotasAnteriores =
                situacion_mascotas_anteriores,
            lugarAnimal = lugar_animal,
            tiempoSolo = tiempo_solo,
            responsableAlimentacion = responsable_alimentacion,
            responsableGastos = responsable_gastos,
            capacidadVeterinaria = capacidad_veterinaria,
            motivo = motivo,
            porQueAnimal = por_que_animal,
            expectativas = expectativas,
            compromisoAdopcion = compromiso_adopcion,
            estado = estado,
            fechaSolicitud = convertirFecha(fecha_solicitud)
        )
    }

    // =========================================================
    // CONVERTIR FECHA DEL BACKEND A MILISEGUNDOS
    // =========================================================

    private fun convertirFecha(fecha: String): Long {

        return try {
            java.time.Instant.parse(fecha).toEpochMilli()

        } catch (_: Exception) {

            try {
                java.time.OffsetDateTime.parse(fecha)
                    .toInstant()
                    .toEpochMilli()

            } catch (_: Exception) {
                0L
            }
        }
    }
}