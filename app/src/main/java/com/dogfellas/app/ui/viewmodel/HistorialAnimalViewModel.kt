package com.dogfellas.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dogfellas.app.model.AnimalReportado
import com.dogfellas.app.model.EstadoAnimal
import com.dogfellas.app.model.HistorialEstadoAnimal
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import repository.HistorialAnimalRepository

class HistorialAnimalViewModel : ViewModel() {

    private val repository = HistorialAnimalRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _historial =
        MutableStateFlow<List<HistorialEstadoAnimal>>(emptyList())

    val historial: StateFlow<List<HistorialEstadoAnimal>> =
        _historial.asStateFlow()

    private val _cargando =
        MutableStateFlow(false)

    val cargando: StateFlow<Boolean> =
        _cargando.asStateFlow()

    private val _actualizando =
        MutableStateFlow(false)

    val actualizando: StateFlow<Boolean> =
        _actualizando.asStateFlow()

    private val _mensaje =
        MutableStateFlow<String?>(null)

    val mensaje: StateFlow<String?> =
        _mensaje.asStateFlow()

    private val _actualizado =
        MutableStateFlow(false)

    val actualizado: StateFlow<Boolean> =
        _actualizado.asStateFlow()

    fun cargarHistorial(reporteId: String) {
        viewModelScope.launch {

            _cargando.value = true
            _mensaje.value = null

            repository.obtenerHistorial(reporteId)
                .onSuccess { lista ->
                    _historial.value = lista
                }
                .onFailure { error ->
                    _mensaje.value = error.message
                }

            _cargando.value = false
        }
    }

    fun inicializarHistorial(
        reporteId: String,
        estadoActual: String,
        usuarioReporta: String,
        fechaReporte: Long
    ) {
        viewModelScope.launch {

            val resultado =
                repository.crearEstadoInicialSiEsNecesario(
                    AnimalReportado(
                        id = reporteId,
                        estado = estadoActual,
                        usuarioReporta = usuarioReporta,
                        fechaReporte = fechaReporte
                    )
                )

            resultado.onSuccess {
                cargarHistorial(reporteId)
            }

            resultado.onFailure { error ->
                _mensaje.value = error.message
            }
        }
    }

    fun cambiarEstado(
        reporteId: String,
        nuevoEstado: String,
        observacion: String
    ) {

        val usuario = auth.currentUser

        if (usuario == null) {
            _mensaje.value = "Debes iniciar sesión."
            return
        }

        viewModelScope.launch {

            _actualizando.value = true
            _actualizado.value = false
            _mensaje.value = null

            repository.registrarCambioEstado(
                reporteId = reporteId,
                nuevoEstado = nuevoEstado,
                usuarioId = usuario.uid,
                observacion = observacion
            )
                .onSuccess {

                    _actualizado.value = true

                    cargarHistorial(reporteId)
                }
                .onFailure { error ->

                    _mensaje.value =
                        error.message
                            ?: "No fue posible actualizar el estado."
                }

            _actualizando.value = false
        }
    }

    fun limpiarActualizado() {
        _actualizado.value = false
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }

    fun siguienteEstado(
        estadoActual: String
    ): String? {

        val estados = EstadoAnimal.values().toList()

        val indice =
            estados.indexOfFirst {
                it.valor == estadoActual
            }

        if (indice == -1) return null

        if (indice >= estados.lastIndex) {
            return null
        }

        return estados[indice + 1].valor
    }
}