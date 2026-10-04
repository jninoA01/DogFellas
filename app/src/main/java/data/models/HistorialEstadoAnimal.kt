package com.dogfellas.app.model

data class HistorialEstadoAnimal(
    val id: String = "",
    val estado: String = "",
    val fecha: Long = 0L,
    val usuarioId: String = "",
    val observacion: String = ""
)