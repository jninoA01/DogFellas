package com.dogfellas.app.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dogfellas.app.model.AnimalReportado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import repository.AdopcionesRepository

class AdopcionesViewModel : ViewModel() {

    private val repository = AdopcionesRepository()

    private val _animales =
        MutableStateFlow<List<AnimalReportado>>(emptyList())

    val animales: StateFlow<List<AnimalReportado>> =
        _animales.asStateFlow()

    private val _cargando =
        MutableStateFlow(false)

    val cargando: StateFlow<Boolean> =
        _cargando.asStateFlow()

    private val _mensaje =
        MutableStateFlow<String?>(null)

    val mensaje: StateFlow<String?> =
        _mensaje.asStateFlow()

    fun cargarAnimales() {

        Log.d(
            "ADOPCIONES_VM",
            "Iniciando carga de animales..."
        )

        viewModelScope.launch {

            _cargando.value = true
            _mensaje.value = null

            Log.d(
                "ADOPCIONES_VM",
                "cargando = true"
            )

            repository.obtenerAnimalesEnAdopcion()

                .onSuccess { lista ->

                    Log.d(
                        "ADOPCIONES_VM",
                        "Animales recibidos: ${lista.size}"
                    )

                    _animales.value = lista
                }

                .onFailure { error ->

                    Log.e(
                        "ADOPCIONES_VM",
                        "Error: ${error.message}",
                        error
                    )

                    _mensaje.value =
                        error.message
                            ?: "No fue posible cargar los animales."
                }

            _cargando.value = false

            Log.d(
                "ADOPCIONES_VM",
                "cargando = false"
            )
        }
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }
}