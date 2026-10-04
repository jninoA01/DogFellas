package com.dogfellas.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dogfellas.app.model.ComentarioAnimal
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import repository.ComentariosAnimalRepository

class ComentariosAnimalViewModel : ViewModel() {

    private val repository = ComentariosAnimalRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _comentarios =
        MutableStateFlow<List<ComentarioAnimal>>(emptyList())

    val comentarios: StateFlow<List<ComentarioAnimal>> =
        _comentarios.asStateFlow()

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

    fun cargarComentarios(
        reporteId: String
    ) {

        viewModelScope.launch {

            _cargando.value = true
            _mensaje.value = null

            repository.obtenerComentarios(reporteId)

                .onSuccess { lista ->
                    _comentarios.value = lista
                }

                .onFailure { error ->
                    _mensaje.value = error.message
                }

            _cargando.value = false
        }
    }

    fun agregarComentario(
        reporteId: String,
        comentario: String
    ) {

        val texto = comentario.trim()

        if (texto.isBlank()) {

            _mensaje.value =
                "Escribe un comentario."

            return
        }

        val usuario = auth.currentUser

        if (usuario == null) {

            _mensaje.value =
                "Debes iniciar sesión para comentar."

            return
        }

        viewModelScope.launch {

            _guardando.value = true
            _mensaje.value = null

            repository.agregarComentario(
                reporteId = reporteId,
                usuarioId = usuario.uid,
                comentario = texto
            )

                .onSuccess {

                    cargarComentarios(reporteId)
                }

                .onFailure { error ->

                    _mensaje.value =
                        error.message
                }

            _guardando.value = false
        }
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }
}