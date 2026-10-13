package com.dogfellas.app.ui.viewmodel

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dogfellas.app.model.AnimalReportado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import repository.AnimalesCercanosRepository

data class AnimalReporteUI(
    val animal: AnimalReportado,
    val distanciaMetros: Float? = null
)

class AnimalesCercanosViewModel : ViewModel() {

    private val repository =
        AnimalesCercanosRepository()

    private val _reportes =
        MutableStateFlow<List<AnimalReporteUI>>(
            emptyList()
        )

    val reportes: StateFlow<List<AnimalReporteUI>> =
        _reportes.asStateFlow()

    private val _cargando =
        MutableStateFlow(false)

    val cargando: StateFlow<Boolean> =
        _cargando.asStateFlow()

    private val _mensaje =
        MutableStateFlow<String?>(null)

    val mensaje: StateFlow<String?> =
        _mensaje.asStateFlow()

    private val _ubicacionUsuario =
        MutableStateFlow<Location?>(null)

    val ubicacionUsuario: StateFlow<Location?> =
        _ubicacionUsuario.asStateFlow()


    // =====================================================
    // ACTUALIZAR UBICACIÓN
    // =====================================================

    fun actualizarUbicacion(
        location: Location?
    ) {

        _ubicacionUsuario.value =
            location

        if (location != null) {

            cargarAnimalesCercanos(
                location
            )

        } else {

            recalcularDistancias()
            ordenarPorCercania()
        }
    }


    // =====================================================
    // CARGAR REPORTES
    // =====================================================

    fun cargarReportes() {

        viewModelScope.launch {

            _cargando.value = true
            _mensaje.value = null

            val resultado =
                repository.obtenerReportes()

            resultado
                .onSuccess { lista ->

                    _reportes.value =
                        lista.map { animal ->

                            AnimalReporteUI(
                                animal =
                                    animal,

                                distanciaMetros =
                                    calcularDistancia(
                                        animal
                                    )
                            )
                        }

                    ordenarPorCercania()
                }

                .onFailure { error ->

                    _mensaje.value =
                        error.message
                            ?: "No fue posible cargar los reportes."
                }

            _cargando.value = false
        }
    }


    // =====================================================
    // CARGAR ANIMALES CERCANOS DESDE FASTAPI
    // =====================================================

    private fun cargarAnimalesCercanos(
        location: Location
    ) {

        viewModelScope.launch {

            _cargando.value = true
            _mensaje.value = null

            val resultado =
                repository.obtenerAnimalesCercanos(

                    latitud =
                        location.latitude,

                    longitud =
                        location.longitude,

                    radioKm =
                        5.0
                )

            resultado
                .onSuccess { lista ->

                    _reportes.value =
                        lista.map { animal ->

                            AnimalReporteUI(
                                animal =
                                    animal,

                                distanciaMetros =
                                    calcularDistancia(
                                        animal
                                    )
                            )
                        }

                    ordenarPorCercania()
                }

                .onFailure { error ->

                    _mensaje.value =
                        error.message
                            ?: "No fue posible cargar los animales cercanos."
                }

            _cargando.value = false
        }
    }


    // =====================================================
    // CALCULAR DISTANCIA LOCAL
    // =====================================================

    private fun calcularDistancia(
        animal: AnimalReportado
    ): Float? {

        val usuario =
            _ubicacionUsuario.value

        val ubicacionAnimal =
            animal.ubicacion

        if (
            usuario == null ||
            ubicacionAnimal == null
        ) {
            return null
        }

        val resultado =
            FloatArray(1)

        Location.distanceBetween(

            usuario.latitude,
            usuario.longitude,

            ubicacionAnimal.latitude,
            ubicacionAnimal.longitude,

            resultado
        )

        return resultado[0]
    }


    // =====================================================
    // RECALCULAR DISTANCIAS
    // =====================================================

    private fun recalcularDistancias() {

        _reportes.value =
            _reportes.value.map { item ->

                item.copy(
                    distanciaMetros =
                        calcularDistancia(
                            item.animal
                        )
                )
            }
    }


    // =====================================================
    // ORDENAR POR CERCANÍA
    // =====================================================

    fun ordenarPorCercania() {

        _reportes.value =
            _reportes.value.sortedWith(

                compareBy(

                    {
                        it.distanciaMetros == null
                    },

                    {
                        it.distanciaMetros
                            ?: Float.MAX_VALUE
                    }
                )
            )
    }


    // =====================================================
    // ORDENAR POR URGENCIA
    // =====================================================

    fun ordenarPorUrgencia() {

        val prioridad =
            mapOf(

                "Alta" to 0,

                "Media" to 1,

                "Baja" to 2
            )

        _reportes.value =
            _reportes.value.sortedWith(

                compareBy(

                    {
                        prioridad[
                            it.animal.urgencia
                        ] ?: 3
                    },

                    {
                        it.distanciaMetros
                            ?: Float.MAX_VALUE
                    }
                )
            )
    }


    // =====================================================
    // ORDENAR POR FECHA
    // =====================================================

    fun ordenarPorFecha() {

        _reportes.value =
            _reportes.value.sortedByDescending {

                it.animal.fechaReporte
            }
    }
}
