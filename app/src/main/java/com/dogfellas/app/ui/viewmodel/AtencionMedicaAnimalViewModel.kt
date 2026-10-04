package com.dogfellas.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dogfellas.app.model.CitaVeterinariaAnimal
import com.dogfellas.app.model.DesparasitacionAnimal
import com.dogfellas.app.model.EmergenciaAnimal
import com.dogfellas.app.model.HistorialMedicoAnimal
import com.dogfellas.app.model.MedicamentoAnimal
import com.dogfellas.app.model.VacunacionAnimal
import com.dogfellas.app.repository.AtencionMedicaAnimalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AtencionMedicaAnimalViewModel : ViewModel() {

    private val repository =
        AtencionMedicaAnimalRepository()

    // ============================================================
    // HISTORIAL MÉDICO
    // ============================================================

    private val _historialMedico =
        MutableStateFlow<List<HistorialMedicoAnimal>>(emptyList())

    val historialMedico: StateFlow<List<HistorialMedicoAnimal>> =
        _historialMedico.asStateFlow()

    // ============================================================
    // MEDICAMENTOS
    // ============================================================

    private val _medicamentos =
        MutableStateFlow<List<MedicamentoAnimal>>(emptyList())

    val medicamentos: StateFlow<List<MedicamentoAnimal>> =
        _medicamentos.asStateFlow()

    // ============================================================
    // VACUNACIONES
    // ============================================================

    private val _vacunaciones =
        MutableStateFlow<List<VacunacionAnimal>>(emptyList())

    val vacunaciones: StateFlow<List<VacunacionAnimal>> =
        _vacunaciones.asStateFlow()

    // ============================================================
    // DESPARASITACIONES
    // ============================================================

    private val _desparasitaciones =
        MutableStateFlow<List<DesparasitacionAnimal>>(emptyList())

    val desparasitaciones: StateFlow<List<DesparasitacionAnimal>> =
        _desparasitaciones.asStateFlow()

    // ============================================================
    // CITAS
    // ============================================================

    private val _citas =
        MutableStateFlow<List<CitaVeterinariaAnimal>>(emptyList())

    val citas: StateFlow<List<CitaVeterinariaAnimal>> =
        _citas.asStateFlow()

    // ============================================================
    // EMERGENCIAS
    // ============================================================

    private val _emergencias =
        MutableStateFlow<List<EmergenciaAnimal>>(emptyList())

    val emergencias: StateFlow<List<EmergenciaAnimal>> =
        _emergencias.asStateFlow()

    // ============================================================
    // ESTADOS GENERALES
    // ============================================================

    private val _cargando =
        MutableStateFlow(false)

    val cargando: StateFlow<Boolean> =
        _cargando.asStateFlow()

    private val _guardando =
        MutableStateFlow(false)

    val guardando: StateFlow<Boolean> =
        _guardando.asStateFlow()

    private val _mensaje =
        MutableStateFlow<String?>(null)

    val mensaje: StateFlow<String?> =
        _mensaje.asStateFlow()

    // ============================================================
    // CARGAR TODA LA INFORMACIÓN DEL ANIMAL
    // ============================================================

    fun cargarInformacionMedica(
        reporteId: String
    ) {

        if (reporteId.isBlank()) {
            _mensaje.value =
                "No se encontró el animal."
            return
        }

        viewModelScope.launch {

            _cargando.value = true
            _mensaje.value = null

            repository.obtenerHistorialMedico(reporteId)
                .onSuccess {
                    _historialMedico.value = it
                }
                .onFailure {
                    _mensaje.value = it.message
                }

            repository.obtenerMedicamentosAnimal(reporteId)
                .onSuccess {
                    _medicamentos.value = it
                }

            repository.obtenerVacunaciones(reporteId)
                .onSuccess {
                    _vacunaciones.value = it
                }

            repository.obtenerDesparasitaciones(reporteId)
                .onSuccess {
                    _desparasitaciones.value = it
                }

            repository.obtenerCitasVeterinarias(reporteId)
                .onSuccess {
                    _citas.value = it
                }

            repository.obtenerEmergenciasAnimal(reporteId)
                .onSuccess {
                    _emergencias.value = it
                }

            _cargando.value = false
        }
    }

    // ============================================================
    // REGISTRAR EVALUACIÓN / TRATAMIENTO / SEGUIMIENTO
    // ============================================================

    fun registrarHistorialMedico(
        historial: HistorialMedicoAnimal
    ) {

        viewModelScope.launch {

            _guardando.value = true
            _mensaje.value = null

            repository.registrarHistorialMedico(
                historial
            )
                .onSuccess {
                    cargarInformacionMedica(
                        historial.reporteId
                    )
                }
                .onFailure {
                    _mensaje.value =
                        it.message
                            ?: "No fue posible registrar la atención."
                }

            _guardando.value = false
        }
    }

    // ============================================================
    // REGISTRAR MEDICAMENTO
    // ============================================================

    fun registrarMedicamento(
        medicamento: MedicamentoAnimal
    ) {

        viewModelScope.launch {

            _guardando.value = true
            _mensaje.value = null

            repository.registrarMedicamentoAnimal(
                medicamento
            )
                .onSuccess {
                    cargarInformacionMedica(
                        medicamento.reporteId
                    )
                }
                .onFailure {
                    _mensaje.value =
                        it.message
                            ?: "No fue posible registrar el medicamento."
                }

            _guardando.value = false
        }
    }

    // ============================================================
    // REGISTRAR VACUNACIÓN
    // ============================================================

    fun registrarVacunacion(
        vacunacion: VacunacionAnimal
    ) {

        viewModelScope.launch {

            _guardando.value = true
            _mensaje.value = null

            repository.registrarVacunacion(
                vacunacion
            )
                .onSuccess {
                    cargarInformacionMedica(
                        vacunacion.reporteId
                    )
                }
                .onFailure {
                    _mensaje.value =
                        it.message
                            ?: "No fue posible registrar la vacunación."
                }

            _guardando.value = false
        }
    }

    // ============================================================
    // REGISTRAR DESPARASITACIÓN
    // ============================================================

    fun registrarDesparasitacion(
        desparasitacion: DesparasitacionAnimal
    ) {

        viewModelScope.launch {

            _guardando.value = true
            _mensaje.value = null

            repository.registrarDesparasitacion(
                desparasitacion
            )
                .onSuccess {
                    cargarInformacionMedica(
                        desparasitacion.reporteId
                    )
                }
                .onFailure {
                    _mensaje.value =
                        it.message
                            ?: "No fue posible registrar la desparasitación."
                }

            _guardando.value = false
        }
    }

    // ============================================================
    // REGISTRAR CITA
    // ============================================================

    fun registrarCita(
        cita: CitaVeterinariaAnimal
    ) {

        viewModelScope.launch {

            _guardando.value = true
            _mensaje.value = null

            repository.registrarCitaVeterinaria(
                cita
            )
                .onSuccess {
                    cargarInformacionMedica(
                        cita.reporteId
                    )
                }
                .onFailure {
                    _mensaje.value =
                        it.message
                            ?: "No fue posible registrar la cita."
                }

            _guardando.value = false
        }
    }

    // ============================================================
    // REGISTRAR EMERGENCIA
    // ============================================================

    fun registrarEmergencia(
        emergencia: EmergenciaAnimal
    ) {

        viewModelScope.launch {

            _guardando.value = true
            _mensaje.value = null

            repository.registrarEmergenciaAnimal(
                emergencia
            )
                .onSuccess {
                    cargarInformacionMedica(
                        emergencia.reporteId
                    )
                }
                .onFailure {
                    _mensaje.value =
                        it.message
                            ?: "No fue posible registrar la emergencia."
                }

            _guardando.value = false
        }
    }

    // ============================================================
    // LIMPIAR MENSAJE
    // ============================================================

    fun limpiarMensaje() {
        _mensaje.value = null
    }
}