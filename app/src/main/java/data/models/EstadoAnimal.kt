package com.dogfellas.app.model

enum class EstadoAnimal(
    val valor: String,
    val nombre: String
) {

    REPORTADO(
        "REPORTADO",
        "Reportado"
    ),

    EN_ATENCION(
        "EN_ATENCION",
        "En atención"
    ),

    RESCATADO(
        "RESCATADO",
        "Rescatado"
    ),

    EN_RECUPERACION(
        "EN_RECUPERACION",
        "En recuperación"
    ),

    EN_ADOPCION(
        "EN_ADOPCION",
        "En adopción"
    ),

    ADOPTADO(
        "ADOPTADO",
        "Adoptado"
    )
}