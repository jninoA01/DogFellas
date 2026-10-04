package com.dogfellas.app.ui.viewmodel

import android.content.Context
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dogfellas.app.repository.AtencionMedicaRepository
import com.google.android.libraries.places.api.Places
import data.models.Emergencia
import data.models.Medicamento
import data.models.Veterinario
import kotlinx.coroutines.launch

// ... (tus imports se mantienen igual)

class AtencionMedicaViewModel : ViewModel() {
    private val repository = AtencionMedicaRepository()

    // ── ESTADOS DE EMERGENCIAS ────────────────────────────────────
    val emergencias = mutableStateListOf<Emergencia>()
    var cargandoEmergencias by mutableStateOf(false)
        private set

    // ── ESTADOS DE MEDICAMENTOS ───────────────────────────────────
    val medicamentos = mutableStateListOf<Medicamento>()
    var cargandoMedicamentos by mutableStateOf(false)
        private set

    // ── ESTADOS COMUNES ───────────────────────────────────────────
    var filtroEspecie by mutableStateOf("Todos")
    var error by mutableStateOf<String?>(null)

    val especiesFiltro = listOf("Todos", "Perro", "Gato", "Conejo", "Ave", "Otro")

    // ── LÓGICA DE EMERGENCIAS ─────────────────────────────────────
    fun cargarEmergencias() {
        if (emergencias.isNotEmpty()) return
        viewModelScope.launch {
            cargandoEmergencias = true
            repository.obtenerEmergencias()
                .onSuccess {
                    emergencias.clear()
                    emergencias.addAll(it)
                }
                .onFailure { error = "Error emergencias: ${it.message}" }
            cargandoEmergencias = false
        }
    }

    fun emergenciasFiltradas(): List<Emergencia> {
        return if (filtroEspecie == "Todos") {
            emergencias.toList()
        } else {
            emergencias.filter { it.especie.contains(filtroEspecie) }
        }
    }

    // ── LÓGICA DE MEDICAMENTOS ────────────────────────────────────
    fun cargarMedicamentos() {
        // Importante: cargar solo si la lista está vacía
        if (medicamentos.isNotEmpty()) return

        viewModelScope.launch {
            cargandoMedicamentos = true
            repository.obtenerMedicamentos()
                .onSuccess {
                    medicamentos.clear()
                    medicamentos.addAll(it)
                }
                .onFailure { error = "Error medicamentos: ${it.message}" }
            cargandoMedicamentos = false
        }
    }

    fun medicamentosFiltrados(): List<Medicamento> {
        return if (filtroEspecie == "Todos") {
            medicamentos.toList()
        } else {
            medicamentos.filter { it.especie.contains(filtroEspecie) }
        }
    }
}