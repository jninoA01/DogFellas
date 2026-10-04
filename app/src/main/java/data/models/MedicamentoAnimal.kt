package com.dogfellas.app.model

data class MedicamentoAnimal(
    val id: String = "",

    // Animal al que pertenece el tratamiento
    val reporteId: String = "",

    // Información del medicamento
    val nombre: String = "",
    val dosis: String = "",
    val frecuencia: String = "",

    // Fechas del tratamiento
    val fechaInicio: Long = 0L,
    val fechaFin: Long = 0L,

    // Profesional responsable
    val veterinario: String = "",

    // Información adicional
    val observaciones: String = "",

    // Permite saber si el tratamiento continúa activo
    val activo: Boolean = true
)