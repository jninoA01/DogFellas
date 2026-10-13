package com.dogfellas.app.repository

import android.util.Log

import com.dogfellas.app.data.AuthRepository
import com.dogfellas.app.data.api.ApiClient
import com.dogfellas.app.data.api.CrearSolicitudAdopcionRequest
import com.dogfellas.app.model.SolicitudAdopcion

import kotlinx.coroutines.suspendCancellableCoroutine

import kotlin.coroutines.resume

class SolicitudAdopcionRepository {

    private val authRepository = AuthRepository()

    /**
     * Crea una solicitud de adopción utilizando:
     *
     * Android
     *    ↓
     * Firebase Authentication
     *    ↓
     * Token Firebase
     *    ↓
     * FastAPI
     *    ↓
     * PostgreSQL
     */
    suspend fun crearSolicitud(
        solicitud: SolicitudAdopcion
    ): Result<Unit> {

        return try {

            Log.d(
                "SOLICITUD_REPO",
                "Iniciando creación de solicitud..."
            )

            // ---------------------------------------------------------
            // 1. Obtener token de Firebase
            // ---------------------------------------------------------

            val token = obtenerToken()

            if (token.isNullOrBlank()) {

                Log.e(
                    "SOLICITUD_REPO",
                    "No se pudo obtener el token de Firebase."
                )

                return Result.failure(
                    Exception(
                        "No fue posible obtener el token de Firebase."
                    )
                )
            }

            // ---------------------------------------------------------
            // 2. Convertir reporteId de String a Int
            // ---------------------------------------------------------

            val reporteId = solicitud.reporteId.toIntOrNull()

            if (reporteId == null) {

                Log.e(
                    "SOLICITUD_REPO",
                    "reporteId inválido: ${solicitud.reporteId}"
                )

                return Result.failure(
                    Exception(
                        "El identificador del animal no es válido."
                    )
                )
            }

            // ---------------------------------------------------------
            // 3. Construir petición para FastAPI
            // ---------------------------------------------------------

            val datos = CrearSolicitudAdopcionRequest(

                reporte_id = reporteId,

                nombre_completo =
                    solicitud.nombreCompleto,

                tipo_documento =
                    solicitud.tipoDocumento,

                numero_documento =
                    solicitud.numeroDocumento,

                fecha_nacimiento =
                    solicitud.fechaNacimiento,

                telefono =
                    solicitud.telefono,

                correo =
                    solicitud.correo,

                ciudad =
                    solicitud.ciudad,

                direccion =
                    solicitud.direccion,

                tipo_vivienda =
                    solicitud.tipoVivienda,

                vivienda =
                    solicitud.vivienda,

                autorizacion_propietario =
                    solicitud.autorizacionPropietario,

                con_quien_vive =
                    solicitud.conQuienVive,

                personas_hogar =
                    solicitud.personasHogar,

                acuerdo_familia =
                    solicitud.acuerdoFamilia,

                ha_tenido_mascotas =
                    solicitud.haTenidoMascotas,

                experiencia_animales =
                    solicitud.experienciaAnimales,

                mascotas_actuales =
                    solicitud.mascotasActuales,

                cantidad_mascotas =
                    solicitud.cantidadMascotas,

                mascotas_vacunadas =
                    solicitud.mascotasVacunadas,

                mascotas_esterilizadas =
                    solicitud.mascotasEsterilizadas,

                situacion_mascotas_anteriores =
                    solicitud.situacionMascotasAnteriores,

                lugar_animal =
                    solicitud.lugarAnimal,

                tiempo_solo =
                    solicitud.tiempoSolo,

                responsable_alimentacion =
                    solicitud.responsableAlimentacion,

                responsable_gastos =
                    solicitud.responsableGastos,

                capacidad_veterinaria =
                    solicitud.capacidadVeterinaria,

                motivo =
                    solicitud.motivo,

                por_que_animal =
                    solicitud.porQueAnimal,

                expectativas =
                    solicitud.expectativas,

                compromiso_adopcion =
                    solicitud.compromisoAdopcion,

                estado =
                    solicitud.estado
            )

            // ---------------------------------------------------------
            // 4. Enviar solicitud a FastAPI
            // ---------------------------------------------------------

            val respuesta =
                ApiClient
                    .solicitudAdopcionApi
                    .crearSolicitud(
                        authorization = "Bearer $token",
                        datos = datos
                    )

            // ---------------------------------------------------------
            // 5. Revisar respuesta
            // ---------------------------------------------------------

            if (!respuesta.isSuccessful) {

                val error =
                    respuesta
                        .errorBody()
                        ?.string()

                Log.e(
                    "SOLICITUD_REPO",
                    "FastAPI respondió ${respuesta.code()}: $error"
                )

                return Result.failure(
                    Exception(
                        when (respuesta.code()) {

                            400 ->
                                "El animal no está disponible para adopción."

                            401 ->
                                "La sesión de Firebase no es válida."

                            404 ->
                                "El usuario o el animal no fueron encontrados."

                            409 ->
                                "Ya existe una solicitud para este animal."

                            else ->
                                "No fue posible registrar la solicitud. " +
                                    "Código: ${respuesta.code()}"
                        }
                    )
                )
            }

            // ---------------------------------------------------------
            // 6. Confirmación
            // ---------------------------------------------------------

            val resultado = respuesta.body()

            if (resultado == null) {

                Log.e(
                    "SOLICITUD_REPO",
                    "FastAPI no devolvió la solicitud creada."
                )

                return Result.failure(
                    Exception(
                        "El servidor no devolvió la solicitud creada."
                    )
                )
            }

            Log.d(
                "SOLICITUD_REPO",
                "Solicitud creada correctamente. " +
                    "ID PostgreSQL: ${resultado.id}"
            )

            Result.success(Unit)

        } catch (e: Exception) {

            Log.e(
                "SOLICITUD_REPO",
                "Error creando solicitud",
                e
            )

            Result.failure(
                Exception(
                    e.message
                        ?: "No fue posible registrar la solicitud.",
                    e
                )
            )
        }
    }

    /**
     * Busca si el usuario actual ya tiene una solicitud
     * para determinado animal.
     *
     * Se mantiene el parámetro usuarioId únicamente para
     * conservar la compatibilidad con el ViewModel existente.
     *
     * El backend ya identifica al usuario mediante Firebase UID.
     */
    suspend fun obtenerSolicitudExistente(
        reporteId: String,
        usuarioId: String
    ): Result<SolicitudAdopcion?> {

        return try {

            Log.d(
                "SOLICITUD_REPO",
                "Consultando solicitud existente..."
            )

            // ---------------------------------------------------------
            // 1. Convertir reporteId
            // ---------------------------------------------------------

            val reporteIdInt =
                reporteId.toIntOrNull()

            if (reporteIdInt == null) {

                return Result.failure(
                    Exception(
                        "El identificador del animal no es válido."
                    )
                )
            }

            // ---------------------------------------------------------
            // 2. Obtener token Firebase
            // ---------------------------------------------------------

            val token = obtenerToken()

            if (token.isNullOrBlank()) {

                return Result.failure(
                    Exception(
                        "No fue posible obtener el token de Firebase."
                    )
                )
            }

            // ---------------------------------------------------------
            // 3. Consultar FastAPI
            // ---------------------------------------------------------

            val respuesta =
                ApiClient
                    .solicitudAdopcionApi
                    .obtenerSolicitudExistente(
                        authorization = "Bearer $token",
                        reporteId = reporteIdInt
                    )

            // ---------------------------------------------------------
            // 4. Revisar respuesta
            // ---------------------------------------------------------

            if (!respuesta.isSuccessful) {

                val error =
                    respuesta
                        .errorBody()
                        ?.string()

                Log.e(
                    "SOLICITUD_REPO",
                    "Error consultando solicitud: " +
                        "${respuesta.code()} $error"
                )

                return Result.failure(
                    Exception(
                        when (respuesta.code()) {

                            401 ->
                                "La sesión de Firebase no es válida."

                            404 ->
                                "El usuario autenticado no existe."

                            else ->
                                "No fue posible consultar la solicitud. " +
                                    "Código: ${respuesta.code()}"
                        }
                    )
                )
            }

            // ---------------------------------------------------------
            // 5. Obtener respuesta
            // ---------------------------------------------------------

            val datos =
                respuesta.body()

            // No existe solicitud
            if (datos == null) {

                Log.d(
                    "SOLICITUD_REPO",
                    "No existe una solicitud para este animal."
                )

                return Result.success(null)
            }

            // ---------------------------------------------------------
            // 6. Convertir API → modelo Android
            // ---------------------------------------------------------

            val solicitud =
                SolicitudAdopcion(

                    id =
                        datos.id.toString(),

                    reporteId =
                        datos.reporte_id.toString(),

                    usuarioId =
                        datos.usuario_id.toString(),

                    nombreCompleto =
                        datos.nombre_completo,

                    tipoDocumento =
                        datos.tipo_documento,

                    numeroDocumento =
                        datos.numero_documento,

                    fechaNacimiento =
                        datos.fecha_nacimiento,

                    telefono =
                        datos.telefono,

                    correo =
                        datos.correo,

                    ciudad =
                        datos.ciudad,

                    direccion =
                        datos.direccion,

                    tipoVivienda =
                        datos.tipo_vivienda,

                    vivienda =
                        datos.vivienda,

                    autorizacionPropietario =
                        datos.autorizacion_propietario,

                    conQuienVive =
                        datos.con_quien_vive,

                    personasHogar =
                        datos.personas_hogar,

                    acuerdoFamilia =
                        datos.acuerdo_familia,

                    haTenidoMascotas =
                        datos.ha_tenido_mascotas,

                    experienciaAnimales =
                        datos.experiencia_animales,

                    mascotasActuales =
                        datos.mascotas_actuales,

                    cantidadMascotas =
                        datos.cantidad_mascotas,

                    mascotasVacunadas =
                        datos.mascotas_vacunadas,

                    mascotasEsterilizadas =
                        datos.mascotas_esterilizadas,

                    situacionMascotasAnteriores =
                        datos.situacion_mascotas_anteriores,

                    lugarAnimal =
                        datos.lugar_animal,

                    tiempoSolo =
                        datos.tiempo_solo,

                    responsableAlimentacion =
                        datos.responsable_alimentacion,

                    responsableGastos =
                        datos.responsable_gastos,

                    capacidadVeterinaria =
                        datos.capacidad_veterinaria,

                    motivo =
                        datos.motivo,

                    porQueAnimal =
                        datos.por_que_animal,

                    expectativas =
                        datos.expectativas,

                    compromisoAdopcion =
                        datos.compromiso_adopcion,

                    estado =
                        datos.estado,

                    fechaSolicitud =
                        convertirFecha(
                            datos.fecha_solicitud
                        )
                )

            Log.d(
                "SOLICITUD_REPO",
                "Solicitud encontrada: ${solicitud.id}"
            )

            Result.success(solicitud)

        } catch (e: Exception) {

            Log.e(
                "SOLICITUD_REPO",
                "Error consultando solicitud",
                e
            )

            Result.failure(
                Exception(
                    e.message
                        ?: "No fue posible consultar la solicitud.",
                    e
                )
            )
        }
    }

    /**
     * Obtiene el token actual de Firebase Authentication.
     */
    private suspend fun obtenerToken(): String? {

        return suspendCancellableCoroutine { continuation ->

            authRepository.getFirebaseIdToken { token, _ ->

                if (continuation.isActive) {
                    continuation.resume(token)
                }
            }
        }
    }

    /**
     * Convierte la fecha ISO enviada por FastAPI
     * a timestamp en milisegundos.
     */
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
                "SOLICITUD_REPO",
                "No fue posible convertir la fecha: $fecha",
                e
            )

            0L
        }
    }
}
