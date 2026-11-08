
package com.dogfellas.app.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dogfellas.app.service.ProgramadorRecordatoriosMedicos
import data.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import repository.HistorialMedicoMascotaRepository

data class RecordatorioMedico(
    val id: String,
    val titulo: String,
    val descripcion: String,
    val fecha: Long,
    val categoria: String,
    val estado: String
)

class HistorialMedicoMascotaViewModel(
    application: Application,
    private val repository: HistorialMedicoMascotaRepository =
        HistorialMedicoMascotaRepository()
) : AndroidViewModel(application) {

    private val contexto: Context
        get() = getApplication<Application>().applicationContext

    private val _registros =
        MutableStateFlow<List<Map<String, Any>>>(emptyList())
    val registros: StateFlow<List<Map<String, Any>>> = _registros

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje

    private val _recordatorios =
        MutableStateFlow<List<RecordatorioMedico>>(emptyList())
    val recordatorios: StateFlow<List<RecordatorioMedico>> =
        _recordatorios

    private val _cargandoRecordatorios = MutableStateFlow(false)
    val cargandoRecordatorios: StateFlow<Boolean> =
        _cargandoRecordatorios

    private val colecciones = listOf(
        "historial_medico",
        "citas",
        "vacunaciones",
        "medicamentos",
        "desparasitaciones",
        "emergencias"
    )

    private fun categoriaValida(tipoRegistro: String): Boolean {
        if (tipoRegistro !in colecciones) {
            _mensaje.value = "Categoría médica no válida."
            return false
        }
        return true
    }

    private fun <T : Any> convertirAMapa(
        elemento: T
    ): Map<String, Any> {
        return elemento.javaClass.declaredFields
            .filterNot { it.isSynthetic }
            .associate { campo ->
                campo.isAccessible = true
                campo.name to (campo.get(elemento) ?: "")
            }
    }

    private suspend fun actualizarLista(
        mascotaId: String,
        tipoRegistro: String
    ) {
        val resultado = when (tipoRegistro) {

            "historial_medico" -> repository.obtenerRegistros(
                mascotaId,
                tipoRegistro,
                HistorialMedicoMascota::class.java
            )

            "citas" -> repository.obtenerRegistros(
                mascotaId,
                tipoRegistro,
                CitaVeterinariaMascota::class.java
            )

            "vacunaciones" -> repository.obtenerRegistros(
                mascotaId,
                tipoRegistro,
                VacunacionMascota::class.java
            )

            "medicamentos" -> repository.obtenerRegistros(
                mascotaId,
                tipoRegistro,
                MedicamentoMascota::class.java
            )

            "desparasitaciones" -> repository.obtenerRegistros(
                mascotaId,
                tipoRegistro,
                DesparasitacionMascota::class.java
            )

            else -> repository.obtenerRegistros(
                mascotaId,
                tipoRegistro,
                EmergenciaMascota::class.java
            )
        }

        resultado.onSuccess { lista ->
            _registros.value = lista.map {
                convertirAMapa(it)
            }
        }.onFailure {
            _mensaje.value =
                it.message ?: "Error al cargar los registros."
        }
    }

    fun cargarRegistros(
        mascotaId: String,
        tipoRegistro: String
    ) {
        if (!categoriaValida(tipoRegistro)) return

        viewModelScope.launch {
            _cargando.value = true

            try {
                actualizarLista(mascotaId, tipoRegistro)
            } catch (e: Exception) {
                _mensaje.value =
                    e.message ?: "No se pudieron cargar los registros."
            } finally {
                _cargando.value = false
            }
        }
    }

    // PROGRAMAR NOTIFICACIONES DE UN REGISTRO

    private fun programarNotificacion(
        mascotaId: String,
        tipoRegistro: String,
        registroId: String,
        titulo: String,
        descripcion: String,
        fecha: Long
    ) {
        if (tipoRegistro !in listOf(
                "citas",
                "vacunaciones",
                "desparasitaciones"
            )
        ) return

        ProgramadorRecordatoriosMedicos.programar(
            context = contexto,
            mascotaId = mascotaId,
            categoria = tipoRegistro,
            registroId = registroId,
            titulo = titulo,
            descripcion = descripcion,
            fecha = fecha
        )
    }

    // CANCELAR NOTIFICACIONES DE UN REGISTRO

    private fun cancelarNotificacion(
        mascotaId: String,
        tipoRegistro: String,
        registroId: String
    ) {
        if (tipoRegistro !in listOf(
                "citas",
                "vacunaciones",
                "desparasitaciones"
            )
        ) return

        ProgramadorRecordatoriosMedicos.cancelar(
            context = contexto,
            mascotaId = mascotaId,
            categoria = tipoRegistro,
            registroId = registroId
        )
    }

    // CARGAR Y SINCRONIZAR RECORDATORIOS

    fun cargarRecordatorios(mascotaId: String) {
        if (mascotaId.isBlank()) {
            _mensaje.value = "No se encontró la mascota."
            return
        }

        viewModelScope.launch {
            _cargandoRecordatorios.value = true

            try {
                val resultado = mutableListOf<RecordatorioMedico>()
                val ahora = System.currentTimeMillis()

                // CITAS VETERINARIAS

                repository.obtenerRegistros(
                    mascotaId,
                    "citas",
                    CitaVeterinariaMascota::class.java
                ).onSuccess { citas ->

                    citas.forEach { cita ->

                        if (
                            cita.estado.equals(
                                "PENDIENTE",
                                ignoreCase = true
                            )
                        ) {
                            val recordatorio = RecordatorioMedico(
                                id = cita.id,
                                titulo = "Cita veterinaria",
                                descripcion = cita.motivo,
                                fecha = cita.fecha,
                                categoria = "citas",
                                estado = if (cita.fecha < ahora) {
                                    "VENCIDO"
                                } else {
                                    "PENDIENTE"
                                }
                            )

                            resultado.add(recordatorio)

                            if (cita.fecha > ahora) {
                                programarNotificacion(
                                    mascotaId,
                                    "citas",
                                    cita.id,
                                    recordatorio.titulo,
                                    recordatorio.descripcion,
                                    cita.fecha
                                )
                            } else {
                                cancelarNotificacion(
                                    mascotaId,
                                    "citas",
                                    cita.id
                                )
                            }
                        } else {
                            cancelarNotificacion(
                                mascotaId,
                                "citas",
                                cita.id
                            )
                        }
                    }
                }.onFailure {
                    throw it
                }

                // VACUNACIONES

                repository.obtenerRegistros(
                    mascotaId,
                    "vacunaciones",
                    VacunacionMascota::class.java
                ).onSuccess { vacunaciones ->

                    vacunaciones.forEach { vacuna ->

                        vacuna.proximaDosis?.let { fecha ->

                            val recordatorio = RecordatorioMedico(
                                id = vacuna.id,
                                titulo = "Próxima dosis de vacuna",
                                descripcion = vacuna.vacuna,
                                fecha = fecha,
                                categoria = "vacunaciones",
                                estado = if (fecha < ahora) {
                                    "VENCIDO"
                                } else {
                                    "PENDIENTE"
                                }
                            )

                            resultado.add(recordatorio)

                            if (fecha > ahora) {
                                programarNotificacion(
                                    mascotaId,
                                    "vacunaciones",
                                    vacuna.id,
                                    recordatorio.titulo,
                                    recordatorio.descripcion,
                                    fecha
                                )
                            } else {
                                cancelarNotificacion(
                                    mascotaId,
                                    "vacunaciones",
                                    vacuna.id
                                )
                            }
                        } ?: run {
                            cancelarNotificacion(
                                mascotaId,
                                "vacunaciones",
                                vacuna.id
                            )
                        }
                    }
                }.onFailure {
                    throw it
                }

                // DESPARASITACIONES

                repository.obtenerRegistros(
                    mascotaId,
                    "desparasitaciones",
                    DesparasitacionMascota::class.java
                ).onSuccess { desparasitaciones ->

                    desparasitaciones.forEach { registro ->

                        registro.proximaFecha?.let { fecha ->

                            val recordatorio = RecordatorioMedico(
                                id = registro.id,
                                titulo = "Próxima desparasitación",
                                descripcion = registro.producto,
                                fecha = fecha,
                                categoria = "desparasitaciones",
                                estado = if (fecha < ahora) {
                                    "VENCIDO"
                                } else {
                                    "PENDIENTE"
                                }
                            )

                            resultado.add(recordatorio)

                            if (fecha > ahora) {
                                programarNotificacion(
                                    mascotaId,
                                    "desparasitaciones",
                                    registro.id,
                                    recordatorio.titulo,
                                    recordatorio.descripcion,
                                    fecha
                                )
                            } else {
                                cancelarNotificacion(
                                    mascotaId,
                                    "desparasitaciones",
                                    registro.id
                                )
                            }
                        } ?: run {
                            cancelarNotificacion(
                                mascotaId,
                                "desparasitaciones",
                                registro.id
                            )
                        }
                    }
                }.onFailure {
                    throw it
                }

                // MEDICAMENTOS ACTIVOS
                // No se programan avisos porque no tenemos
                // una hora específica para cada dosis.

                repository.obtenerRegistros(
                    mascotaId,
                    "medicamentos",
                    MedicamentoMascota::class.java
                ).onSuccess { medicamentos ->

                    medicamentos.forEach { medicamento ->

                        if (
                            medicamento.activo &&
                            (
                                    medicamento.fechaFin == null ||
                                            medicamento.fechaFin >= ahora
                                    )
                        ) {
                            resultado.add(
                                RecordatorioMedico(
                                    id = medicamento.id,
                                    titulo = "Medicamento activo",
                                    descripcion =
                                        "${medicamento.nombre} - " +
                                                "${medicamento.dosis} - " +
                                                medicamento.frecuencia,
                                    fecha = medicamento.fechaInicio,
                                    categoria = "medicamentos",
                                    estado = "ACTIVO"
                                )
                            )
                        }
                    }
                }.onFailure {
                    throw it
                }

                _recordatorios.value = resultado.sortedBy {
                    it.fecha
                }

            } catch (e: Exception) {
                _mensaje.value =
                    e.message ?: "No se pudieron cargar los recordatorios."
            } finally {
                _cargandoRecordatorios.value = false
            }
        }
    }

    // GUARDAR REGISTRO

    fun guardarRegistro(
        mascotaId: String,
        tipoRegistro: String,
        datos: Any
    ) {
        if (!categoriaValida(tipoRegistro)) return

        viewModelScope.launch {
            _cargando.value = true

            try {
                repository.guardarRegistro(
                    mascotaId,
                    tipoRegistro,
                    datos
                ).onSuccess { registroId ->

                    actualizarLista(mascotaId, tipoRegistro)

                    cargarRecordatorios(mascotaId)

                    _mensaje.value =
                        "Registro guardado correctamente."

                }.onFailure {
                    _mensaje.value =
                        it.message ?: "No se pudo guardar el registro."
                }

            } catch (e: Exception) {
                _mensaje.value =
                    e.message ?: "Ocurrió un error al guardar."
            } finally {
                _cargando.value = false
            }
        }
    }

    // ACTUALIZAR REGISTRO

    fun actualizarRegistro(
        mascotaId: String,
        tipoRegistro: String,
        registroId: String,
        datos: Any
    ) {
        if (!categoriaValida(tipoRegistro)) return

        if (registroId.isBlank()) {
            _mensaje.value =
                "El identificador del registro está vacío."
            return
        }

        viewModelScope.launch {
            _cargando.value = true

            try {
                repository.actualizarRegistro(
                    mascotaId,
                    tipoRegistro,
                    registroId,
                    datos
                ).onSuccess {

                    actualizarLista(mascotaId, tipoRegistro)

                    cargarRecordatorios(mascotaId)

                    _mensaje.value =
                        "Registro actualizado correctamente."

                }.onFailure {
                    _mensaje.value =
                        it.message ?: "No se pudo actualizar el registro."
                }

            } catch (e: Exception) {
                _mensaje.value =
                    e.message ?: "Ocurrió un error al actualizar."
            } finally {
                _cargando.value = false
            }
        }
    }

    // ELIMINAR REGISTRO

    fun eliminarRegistro(
        mascotaId: String,
        tipoRegistro: String,
        registroId: String
    ) {
        if (!categoriaValida(tipoRegistro)) return

        if (registroId.isBlank()) {
            _mensaje.value =
                "El identificador del registro está vacío."
            return
        }

        viewModelScope.launch {
            _cargando.value = true

            try {
                repository.eliminarRegistro(
                    mascotaId,
                    tipoRegistro,
                    registroId
                ).onSuccess {

                    cancelarNotificacion(
                        mascotaId,
                        tipoRegistro,
                        registroId
                    )

                    actualizarLista(mascotaId, tipoRegistro)

                    cargarRecordatorios(mascotaId)

                    _mensaje.value =
                        "Registro eliminado correctamente."

                }.onFailure {
                    _mensaje.value =
                        it.message ?: "No se pudo eliminar el registro."
                }

            } catch (e: Exception) {
                _mensaje.value =
                    e.message ?: "Ocurrió un error al eliminar."
            } finally {
                _cargando.value = false
            }
        }
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }
}
