package com.dogfellas.app.model

data class EmergenciaAnimal(
    val id: String = "",

    // Animal rescatado relacionado con la emergencia
    val reporteId: String = "",

    // Fecha de la emergencia
    val fecha: Long = 0L,

    // Motivo de la emergencia
    val motivo: String = "",

    // Atención realizada
    val atencionRealizada: String = "",

    // Veterinario que atendió al animal
    val veterinario: String = "",

    // Resultado de la atención
    val resultado: String = "",

    // Información adicional
    val observaciones: String = ""
)