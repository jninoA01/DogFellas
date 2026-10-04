package com.dogfellas.app.model

data class DesparasitacionAnimal(
    val id: String = "",

    // Animal al que pertenece la desparasitación
    val reporteId: String = "",

    // Producto utilizado
    val producto: String = "",

    // Fecha en que se realizó
    val fecha: Long = 0L,

    // Próxima desparasitación recomendada
    val proximaFecha: Long = 0L,

    // Profesional responsable
    val veterinario: String = "",

    // Información adicional
    val observaciones: String = ""
)