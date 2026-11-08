package com.dogfellas.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dogfellas.app.model.SolicitudAdopcion
import com.dogfellas.app.repository.SolicitudAdopcionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DetalleSolicitudRecibidaViewModel(
    private val repository: SolicitudAdopcionRepository = SolicitudAdopcionRepository()
) : ViewModel() {

    private val _solicitud = MutableStateFlow<SolicitudAdopcion?>(null)
    val solicitud: StateFlow<SolicitudAdopcion?> = _solicitud

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando

    private val _procesando = MutableStateFlow(false)
    val procesando: StateFlow<Boolean> = _procesando

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje

    private val _adopcionFinalizada = MutableStateFlow(false)
    val adopcionFinalizada: StateFlow<Boolean> = _adopcionFinalizada

    fun cargarSolicitud(solicitudId: String) {
        val id = solicitudId.toIntOrNull()

        if (id == null) {
            _mensaje.value = "El identificador de la solicitud no es válido."
            return
        }

        viewModelScope.launch {
            _cargando.value = true
            _mensaje.value = null

            repository.obtenerSolicitudPorId(id)
                .onSuccess {
                    _solicitud.value = it
                }
                .onFailure {
                    _mensaje.value = it.message ?: "No fue posible cargar la solicitud."
                }

            _cargando.value = false
        }
    }

    fun aceptarSolicitud() {
        actualizarEstado("ACEPTADA")
    }

    fun rechazarSolicitud() {
        actualizarEstado("RECHAZADA")
    }

    private fun actualizarEstado(nuevoEstado: String) {
        val solicitudActual = _solicitud.value ?: return
        val id = solicitudActual.id.toIntOrNull() ?: return

        viewModelScope.launch {
            _procesando.value = true
            _mensaje.value = null

            repository.actualizarEstadoSolicitud(id, nuevoEstado)
                .onSuccess {
                    _solicitud.value = it
                    _mensaje.value = if (nuevoEstado == "ACEPTADA") {
                        "Solicitud aceptada correctamente."
                    } else {
                        "Solicitud rechazada correctamente."
                    }
                }
                .onFailure {
                    _mensaje.value = it.message ?: "No fue posible actualizar la solicitud."
                }

            _procesando.value = false
        }
    }

    fun finalizarAdopcion() {
        val solicitudActual = _solicitud.value ?: return
        val reporteId = solicitudActual.reporteId.toIntOrNull()

        if (reporteId == null) {
            _mensaje.value = "El identificador del reporte no es válido."
            return
        }

        if (solicitudActual.estado != "ACEPTADA") {
            _mensaje.value = "Primero debes aceptar la solicitud."
            return
        }

        viewModelScope.launch {
            _procesando.value = true
            _mensaje.value = null

            repository.finalizarAdopcion(reporteId)
                .onSuccess {
                    _adopcionFinalizada.value = true
                    _mensaje.value = it.mensaje
                }
                .onFailure {
                    _mensaje.value = it.message ?: "No fue posible finalizar la adopción."
                }

            _procesando.value = false
        }
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }
}