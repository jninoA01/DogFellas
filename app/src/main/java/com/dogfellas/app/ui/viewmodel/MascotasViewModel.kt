package com.dogfellas.app.ui.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dogfellas.app.repository.MascotasRepository
import data.models.Mascota
import kotlinx.coroutines.launch

class MascotasViewModel : ViewModel() {

    private val repository = MascotasRepository()

    val mascotas = mutableStateListOf<Mascota>()

    var cargando by mutableStateOf(false)
        private set

    var errorMensaje by mutableStateOf<String?>(null)
        private set

    var mensajeDiagnostico by mutableStateOf<String?>(null)
        private set

    fun limpiarError()       { errorMensaje = null }
    fun limpiarDiagnostico() { mensajeDiagnostico = null }

    fun cargarMascotas() {
        viewModelScope.launch {
            cargando = true
            repository.obtenerMascotas()
                .onSuccess  { mascotas.clear(); mascotas.addAll(it) }
                .onFailure  { errorMensaje = "Error al cargar: ${it.message}" }
            cargando = false
        }
    }

    fun agregarMascota(mascota: Mascota, onExito: () -> Unit) {
        viewModelScope.launch {
            cargando = true
            repository.agregarMascota(mascota)
                .onSuccess { cargarMascotas(); onExito() }
                .onFailure { errorMensaje = "Error al guardar: ${it.message}" }
            cargando = false
        }
    }

    fun actualizarMascota(mascota: Mascota, onExito: () -> Unit) {
        viewModelScope.launch {
            cargando = true
            repository.actualizarMascota(mascota)
                .onSuccess { cargarMascotas(); onExito() }
                .onFailure { errorMensaje = "Error al actualizar: ${it.message}" }
            cargando = false
        }
    }

    fun eliminarMascota(id: String) {
        viewModelScope.launch {
            repository.eliminarMascota(id)
                .onSuccess { mascotas.removeAll { it.id == id } }
                .onFailure { errorMensaje = "Error al eliminar: ${it.message}" }
        }
    }

    fun ejecutarDiagnostico() {
        viewModelScope.launch {
            mensajeDiagnostico = "Verificando conexión..."
            mensajeDiagnostico = repository.diagnostico()
        }
    }
}