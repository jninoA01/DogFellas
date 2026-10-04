package com.dogfellas.app.model

data class HistorialMedicoAnimal(
    val id: String = "",

    // Relación con el animal reportado/rescatado
    val reporteId: String = "",

    // Usuario que registra la atención
    val usuarioId: String = "",

    // Información de la atención
    val veterinario: String = "",
    val fecha: Long = 0L,

    // EVALUACION, TRATAMIENTO o SEGUIMIENTO
    val tipo: String = "EVALUACION",

    // Información clínica
    val diagnostico: String = "",
    val tratamiento: String = "",
    val observaciones: String = ""
)