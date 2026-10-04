package com.dogfellas.app.model

data class VacunacionAnimal(
    val id: String = "",

    // Animal al que pertenece la vacunación
    val reporteId: String = "",

    // Información de la vacuna
    val vacuna: String = "",

    // Fecha en que se aplicó
    val fecha: Long = 0L,

    // Fecha prevista para la siguiente dosis
    val proximaDosis: Long = 0L,

    // Profesional responsable
    val veterinario: String = "",

    // Información adicional
    val observaciones: String = ""
)