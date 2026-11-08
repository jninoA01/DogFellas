package com.dogfellas.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dogfellas.app.model.SolicitudAdopcion
import com.dogfellas.app.repository.SolicitudAdopcionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SolicitudesRecibidasViewModel(
    private val repository: SolicitudAdopcionRepository =
        SolicitudAdopcionRepository()
) : ViewModel() {

    private val _solicitudes =
        MutableStateFlow<List<SolicitudAdopcion>>(emptyList())
    val solicitudes: StateFlow<List<SolicitudAdopcion>> =
        _solicitudes.asStateFlow()

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    private val _actualizandoSolicitud = MutableStateFlow(false)
    val actualizandoSolicitud: StateFlow<Boolean> =
        _actualizandoSolicitud.asStateFlow()

    private val _adopcionFinalizada = MutableStateFlow(false)
    val adopcionFinalizada: StateFlow<Boolean> =
        _adopcionFinalizada.asStateFlow()


    // =========================================================
    // CARGAR SOLICITUDES RECIBIDAS
    // =========================================================

    fun cargarSolicitudesRecibidas(reporteId: String) {

        val id = reporteId.toIntOrNull()

        if (id == null || id <= 0) {
            _mensaje.value = "El identificador del animal no es válido."
            _solicitudes.value = emptyList()
            return
        }

        viewModelScope.launch {
            _cargando.value = true
            _mensaje.value = null

            repository.obtenerSolicitudesRecibidas(id)
                .onSuccess { lista ->
                    _solicitudes.value = lista
                }
                .onFailure { error ->
                    _mensaje.value = error.message
                        ?: "No fue posible cargar las solicitudes recibidas."
                }

            _cargando.value = false
        }
    }


    // =========================================================
    // ACEPTAR SOLICITUD
    // =========================================================

    fun aceptarSolicitud(solicitudId: String) {
        actualizarEstado(solicitudId, "ACEPTADA")
    }


    // =========================================================
    // RECHAZAR SOLICITUD
    // =========================================================

    fun rechazarSolicitud(solicitudId: String) {
        actualizarEstado(solicitudId, "RECHAZADA")
    }


    // =========================================================
    // ACTUALIZAR ESTADO
    // =========================================================

    private fun actualizarEstado(
        solicitudId: String,
        nuevoEstado: String
    ) {

        val id = solicitudId.toIntOrNull()

        if (id == null || id <= 0) {
            _mensaje.value = "El identificador de la solicitud no es válido."
            return
        }

        viewModelScope.launch {
            _actualizandoSolicitud.value = true
            _mensaje.value = null

            repository.actualizarEstadoSolicitud(
                solicitudId = id,
                estado = nuevoEstado
            )
                .onSuccess { solicitudActualizada ->

                    _solicitudes.value = _solicitudes.value.map { solicitud ->
                        if (solicitud.id == solicitudActualizada.id) {
                            solicitudActualizada
                        } else {
                            solicitud
                        }
                    }

                    _mensaje.value = if (nuevoEstado == "ACEPTADA") {
                        "La solicitud fue aceptada."
                    } else {
                        "La solicitud fue rechazada."
                    }

                    // El backend rechaza automáticamente las demás
                    // solicitudes pendientes cuando una es aceptada.
                    if (nuevoEstado == "ACEPTADA") {
                        cargarSolicitudesRecibidas(
                            solicitudActualizada.reporteId
                        )
                    }
                }
                .onFailure { error ->
                    _mensaje.value = error.message
                        ?: "No fue posible actualizar la solicitud."
                }

            _actualizandoSolicitud.value = false
        }
    }


    // =========================================================
    // FINALIZAR ADOPCIÓN
    // =========================================================

    fun finalizarAdopcion(reporteId: String) {

        val id = reporteId.toIntOrNull()

        if (id == null || id <= 0) {
            _mensaje.value = "El identificador del animal no es válido."
            return
        }

        viewModelScope.launch {
            _actualizandoSolicitud.value = true
            _mensaje.value = null

            repository.finalizarAdopcion(id)
                .onSuccess {
                    _adopcionFinalizada.value = true
                    _mensaje.value = "La adopción se finalizó correctamente."
                }
                .onFailure { error ->
                    _mensaje.value = error.message
                        ?: "No fue posible finalizar la adopción."
                }

            _actualizandoSolicitud.value = false
        }
    }


    // =========================================================
    // LIMPIAR MENSAJE
    // =========================================================

    fun limpiarMensaje() {
        _mensaje.value = null
    }
}