package com.dogfellas.app.model

data class ComentarioAnimal(
    val id: String = "",
    val usuarioId: String = "",
    val comentario: String = "",
    val fecha: Long = 0L,
    val nombreUsuario: String = ""
)