package com.dogfellas.app.model

data class CitaVeterinariaAnimal(
    val id: String = "",

    // Animal rescatado relacionado con la cita
    val reporteId: String = "",

    // Veterinaria o profesional seleccionado
    val veterinario: String = "",
    val direccionVeterinaria: String = "",

    // Fecha y hora de la cita
    val fecha: Long = 0L,

    // Motivo de la consulta
    val motivo: String = "",

    // PROGRAMADA, ATENDIDA, CANCELADA
    val estado: String = "PROGRAMADA",

    // Resultado o información adicional
    val observaciones: String = ""
)