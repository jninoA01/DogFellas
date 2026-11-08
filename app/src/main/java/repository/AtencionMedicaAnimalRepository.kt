package com.dogfellas.app.repository

import com.dogfellas.app.data.AuthRepository
import com.dogfellas.app.data.api.ApiClient
import com.dogfellas.app.data.api.CitaVeterinariaAnimalApiDto
import com.dogfellas.app.data.api.CitaVeterinariaAnimalCreateApiDto
import com.dogfellas.app.data.api.DesparasitacionAnimalApiDto
import com.dogfellas.app.data.api.DesparasitacionAnimalCreateApiDto
import com.dogfellas.app.data.api.EmergenciaAnimalApiDto
import com.dogfellas.app.data.api.EmergenciaAnimalCreateApiDto
import com.dogfellas.app.data.api.HistorialMedicoApiDto
import com.dogfellas.app.data.api.HistorialMedicoCreateApiDto
import com.dogfellas.app.data.api.MedicamentoAnimalApiDto
import com.dogfellas.app.data.api.MedicamentoAnimalCreateApiDto
import com.dogfellas.app.data.api.VacunacionAnimalApiDto
import com.dogfellas.app.data.api.VacunacionAnimalCreateApiDto
import com.dogfellas.app.model.CitaVeterinariaAnimal
import com.dogfellas.app.model.DesparasitacionAnimal
import com.dogfellas.app.model.EmergenciaAnimal
import com.dogfellas.app.model.HistorialMedicoAnimal
import com.dogfellas.app.model.MedicamentoAnimal
import com.dogfellas.app.model.VacunacionAnimal
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Response
import kotlin.coroutines.resume

class AtencionMedicaAnimalRepository {

    private val api = ApiClient.atencionMedicaApi
    private val authRepository = AuthRepository()

    // ============================================================
    // AUTENTICACIÓN Y SOLICITUDES HTTP
    // ============================================================

    private suspend fun obtenerToken(): String? =
        suspendCancellableCoroutine { continuation ->
            authRepository.getFirebaseIdToken { token, _ ->
                if (continuation.isActive) {
                    continuation.resume(token)
                }
            }
        }

    private suspend fun <T : Any> ejecutarSolicitud(
        solicitud: suspend (String) -> Response<T>
    ): Result<T> {
        return try {
            val token = obtenerToken()
                ?: return Result.failure(
                    Exception(
                        "No fue posible obtener el token de Firebase. " +
                            "Inicia sesión nuevamente."
                    )
                )

            val respuesta = solicitud("Bearer $token")

            if (respuesta.isSuccessful) {
                val cuerpo = respuesta.body()

                if (cuerpo != null) {
                    Result.success(cuerpo)
                } else {
                    Result.failure(
                        Exception(
                            "El servidor respondió correctamente, " +
                                "pero no devolvió información."
                        )
                    )
                }
            } else {
                val detalle = respuesta.errorBody()
                    ?.string()
                    ?.takeIf { it.isNotBlank() }

                Result.failure(
                    Exception(
                        "Error de FastAPI (${respuesta.code()}): " +
                            (detalle ?: respuesta.message())
                    )
                )
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(
                Exception(
                    "No fue posible comunicarse con el backend: " +
                        (e.message ?: "error desconocido"),
                    e
                )
            )
        }
    }

    private fun convertirAnimalId(reporteId: String): Int? =
        reporteId.toIntOrNull()

    private fun errorAnimalId(): Exception =
        Exception(
            "El identificador del animal no es válido para PostgreSQL."
        )

    // ============================================================
    // HISTORIAL MÉDICO
    // ============================================================

    suspend fun obtenerHistorialMedico(
        reporteId: String
    ): Result<List<HistorialMedicoAnimal>> {
        val animalId = convertirAnimalId(reporteId)
            ?: return Result.failure(errorAnimalId())

        return ejecutarSolicitud { token ->
            api.obtenerHistorialMedicoAnimal(token, animalId)
        }.map { lista ->
            lista.map { dto ->
                dto.toModelo(reporteId)
            }
        }
    }

    suspend fun registrarHistorialMedico(
        historial: HistorialMedicoAnimal
    ): Result<Unit> {
        val animalId = convertirAnimalId(historial.reporteId)
            ?: return Result.failure(errorAnimalId())

        val datos = HistorialMedicoCreateApiDto(
            veterinario = historial.veterinario,
            fecha = historial.fecha,
            tipo = historial.tipo,
            diagnostico = historial.diagnostico,
            tratamiento = historial.tratamiento,
            observaciones = historial.observaciones
        )

        return ejecutarSolicitud { token ->
            api.crearHistorialMedicoAnimal(token, animalId, datos)
        }.map { Unit }
    }

    // ============================================================
    // MEDICAMENTOS
    // ============================================================

    suspend fun obtenerMedicamentosAnimal(
        reporteId: String
    ): Result<List<MedicamentoAnimal>> {
        val animalId = convertirAnimalId(reporteId)
            ?: return Result.failure(errorAnimalId())

        return ejecutarSolicitud { token ->
            api.obtenerMedicamentosAnimal(token, animalId)
        }.map { lista ->
            lista.map { dto ->
                dto.toModelo(reporteId)
            }
        }
    }

    suspend fun registrarMedicamentoAnimal(
        medicamento: MedicamentoAnimal
    ): Result<Unit> {
        val animalId = convertirAnimalId(medicamento.reporteId)
            ?: return Result.failure(errorAnimalId())

        val datos = MedicamentoAnimalCreateApiDto(
            nombre = medicamento.nombre,
            dosis = medicamento.dosis,
            frecuencia = medicamento.frecuencia,
            fechaInicio = medicamento.fechaInicio,
            fechaFin = medicamento.fechaFin,
            veterinario = medicamento.veterinario,
            observaciones = medicamento.observaciones,
            activo = medicamento.activo
        )

        return ejecutarSolicitud { token ->
            api.crearMedicamentoAnimal(token, animalId, datos)
        }.map { Unit }
    }

    // ============================================================
    // VACUNACIONES
    // ============================================================

    suspend fun obtenerVacunaciones(
        reporteId: String
    ): Result<List<VacunacionAnimal>> {
        val animalId = convertirAnimalId(reporteId)
            ?: return Result.failure(errorAnimalId())

        return ejecutarSolicitud { token ->
            api.obtenerVacunacionesAnimal(token, animalId)
        }.map { lista ->
            lista.map { dto ->
                dto.toModelo(reporteId)
            }
        }
    }

    suspend fun registrarVacunacion(
        vacunacion: VacunacionAnimal
    ): Result<Unit> {
        val animalId = convertirAnimalId(vacunacion.reporteId)
            ?: return Result.failure(errorAnimalId())

        val datos = VacunacionAnimalCreateApiDto(
            vacuna = vacunacion.vacuna,
            fecha = vacunacion.fecha,
            proximaDosis = vacunacion.proximaDosis,
            veterinario = vacunacion.veterinario,
            observaciones = vacunacion.observaciones
        )

        return ejecutarSolicitud { token ->
            api.crearVacunacionAnimal(token, animalId, datos)
        }.map { Unit }
    }

    // ============================================================
    // DESPARASITACIONES
    // ============================================================

    suspend fun obtenerDesparasitaciones(
        reporteId: String
    ): Result<List<DesparasitacionAnimal>> {
        val animalId = convertirAnimalId(reporteId)
            ?: return Result.failure(errorAnimalId())

        return ejecutarSolicitud { token ->
            api.obtenerDesparasitacionesAnimal(token, animalId)
        }.map { lista ->
            lista.map { dto ->
                dto.toModelo(reporteId)
            }
        }
    }

    suspend fun registrarDesparasitacion(
        desparasitacion: DesparasitacionAnimal
    ): Result<Unit> {
        val animalId = convertirAnimalId(desparasitacion.reporteId)
            ?: return Result.failure(errorAnimalId())

        val datos = DesparasitacionAnimalCreateApiDto(
            producto = desparasitacion.producto,
            fecha = desparasitacion.fecha,
            proximaFecha = desparasitacion.proximaFecha,
            veterinario = desparasitacion.veterinario,
            observaciones = desparasitacion.observaciones
        )

        return ejecutarSolicitud { token ->
            api.crearDesparasitacionAnimal(token, animalId, datos)
        }.map { Unit }
    }

    // ============================================================
    // CITAS VETERINARIAS
    // ============================================================

    suspend fun obtenerCitasVeterinarias(
        reporteId: String
    ): Result<List<CitaVeterinariaAnimal>> {
        val animalId = convertirAnimalId(reporteId)
            ?: return Result.failure(errorAnimalId())

        return ejecutarSolicitud { token ->
            api.obtenerCitasVeterinariasAnimal(token, animalId)
        }.map { lista ->
            lista.map { dto ->
                dto.toModelo(reporteId)
            }
        }
    }

    suspend fun registrarCitaVeterinaria(
        cita: CitaVeterinariaAnimal
    ): Result<Unit> {
        val animalId = convertirAnimalId(cita.reporteId)
            ?: return Result.failure(errorAnimalId())

        val datos = CitaVeterinariaAnimalCreateApiDto(
            veterinario = cita.veterinario,
            direccionVeterinaria = cita.direccionVeterinaria,
            fecha = cita.fecha,
            motivo = cita.motivo,
            estado = cita.estado,
            observaciones = cita.observaciones
        )

        return ejecutarSolicitud { token ->
            api.crearCitaVeterinariaAnimal(token, animalId, datos)
        }.map { Unit }
    }

    // ============================================================
    // EMERGENCIAS
    // ============================================================

    suspend fun obtenerEmergenciasAnimal(
        reporteId: String
    ): Result<List<EmergenciaAnimal>> {
        val animalId = convertirAnimalId(reporteId)
            ?: return Result.failure(errorAnimalId())

        return ejecutarSolicitud { token ->
            api.obtenerEmergenciasAnimal(token, animalId)
        }.map { lista ->
            lista.map { dto ->
                dto.toModelo(reporteId)
            }
        }
    }

    suspend fun registrarEmergenciaAnimal(
        emergencia: EmergenciaAnimal
    ): Result<Unit> {
        val animalId = convertirAnimalId(emergencia.reporteId)
            ?: return Result.failure(errorAnimalId())

        val datos = EmergenciaAnimalCreateApiDto(
            fecha = emergencia.fecha,
            motivo = emergencia.motivo,
            atencionRealizada = emergencia.atencionRealizada,
            veterinario = emergencia.veterinario,
            resultado = emergencia.resultado,
            observaciones = emergencia.observaciones
        )

        return ejecutarSolicitud { token ->
            api.crearEmergenciaAnimal(token, animalId, datos)
        }.map { Unit }
    }

    // ============================================================
    // CONVERSIÓN DE RESPUESTAS API A MODELOS ANDROID
    // ============================================================

    private fun HistorialMedicoApiDto.toModelo(
        reporteIdOriginal: String
    ) = HistorialMedicoAnimal(
        id = id,
        reporteId = reporteIdOriginal,
        usuarioId = usuarioId?.toString().orEmpty(),
        veterinario = veterinario,
        fecha = fecha,
        tipo = tipo,
        diagnostico = diagnostico,
        tratamiento = tratamiento,
        observaciones = observaciones
    )

    private fun MedicamentoAnimalApiDto.toModelo(
        reporteIdOriginal: String
    ) = MedicamentoAnimal(
        id = id,
        reporteId = reporteIdOriginal,
        nombre = nombre,
        dosis = dosis,
        frecuencia = frecuencia,
        fechaInicio = fechaInicio,
        fechaFin = fechaFin,
        veterinario = veterinario,
        observaciones = observaciones,
        activo = activo
    )

    private fun VacunacionAnimalApiDto.toModelo(
        reporteIdOriginal: String
    ) = VacunacionAnimal(
        id = id,
        reporteId = reporteIdOriginal,
        vacuna = vacuna,
        fecha = fecha,
        proximaDosis = proximaDosis,
        veterinario = veterinario,
        observaciones = observaciones
    )

    private fun DesparasitacionAnimalApiDto.toModelo(
        reporteIdOriginal: String
    ) = DesparasitacionAnimal(
        id = id,
        reporteId = reporteIdOriginal,
        producto = producto,
        fecha = fecha,
        proximaFecha = proximaFecha,
        veterinario = veterinario,
        observaciones = observaciones
    )

    private fun CitaVeterinariaAnimalApiDto.toModelo(
        reporteIdOriginal: String
    ) = CitaVeterinariaAnimal(
        id = id,
        reporteId = reporteIdOriginal,
        veterinario = veterinario,
        direccionVeterinaria = direccionVeterinaria,
        fecha = fecha,
        motivo = motivo,
        estado = estado,
        observaciones = observaciones
    )

    private fun EmergenciaAnimalApiDto.toModelo(
        reporteIdOriginal: String
    ) = EmergenciaAnimal(
        id = id,
        reporteId = reporteIdOriginal,
        fecha = fecha,
        motivo = motivo,
        atencionRealizada = atencionRealizada,
        veterinario = veterinario,
        resultado = resultado,
        observaciones = observaciones
    )
}