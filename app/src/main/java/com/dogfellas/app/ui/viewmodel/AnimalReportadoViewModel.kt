package com.dogfellas.app.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dogfellas.app.model.AnimalReportado
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import repository.AnimalReportadoRepository

class AnimalReportadoViewModel : ViewModel() {

    private val repository =
        AnimalReportadoRepository()

    private val auth =
        FirebaseAuth.getInstance()

    private val _guardando =
        MutableStateFlow(false)

    val guardando: StateFlow<Boolean> =
        _guardando.asStateFlow()

    private val _mensaje =
        MutableStateFlow<String?>(null)

    val mensaje: StateFlow<String?> =
        _mensaje.asStateFlow()

    private val _reporteExitoso =
        MutableStateFlow(false)

    val reporteExitoso: StateFlow<Boolean> =
        _reporteExitoso.asStateFlow()


    fun guardarReporte(
        especie: String,
        condicion: String,
        urgencia: String,
        descripcion: String,
        fotoUri: String,
        ubicacion: GeoPoint?,
        context: Context
    ) {

        val usuario =
            auth.currentUser

        if (usuario == null) {

            _mensaje.value =
                "Debes iniciar sesión para realizar un reporte."

            return
        }

        if (especie.isBlank()) {

            _mensaje.value =
                "Selecciona la especie del animal."

            return
        }

        if (condicion.isBlank()) {

            _mensaje.value =
                "Selecciona la condición del animal."

            return
        }

        if (descripcion.isBlank()) {

            _mensaje.value =
                "Escribe una descripción del animal."

            return
        }

        if (ubicacion == null) {

            _mensaje.value =
                "Debes obtener la ubicación del animal."

            return
        }

        viewModelScope.launch {

            _guardando.value = true
            _mensaje.value = null
            _reporteExitoso.value = false

            try {

                val animal =
                    AnimalReportado(
                        especie = especie,
                        condicion = condicion,
                        urgencia = urgencia,
                        descripcion = descripcion,
                        fotoUri = fotoUri,
                        ubicacion = ubicacion,
                        usuarioReporta = usuario.uid,
                        estado = "REPORTADO",
                        fechaReporte =
                            System.currentTimeMillis()
                    )

                val resultado =
                    repository.guardarReporte(
                        animal = animal,
                        context = context
                    )

                resultado.onSuccess {

                    _mensaje.value =
                        "¡Reporte registrado correctamente!"

                    _reporteExitoso.value = true

                }.onFailure { error ->

                    _mensaje.value =
                        error.message
                            ?: "No fue posible guardar el reporte."

                    _reporteExitoso.value = false
                }

            } catch (e: Exception) {

                _mensaje.value =
                    e.message
                        ?: "Ocurrió un error al guardar el reporte."

                _reporteExitoso.value = false

            } finally {

                _guardando.value = false
            }
        }
    }


    fun limpiarEstado() {

        _mensaje.value = null
        _reporteExitoso.value = false
    }
}