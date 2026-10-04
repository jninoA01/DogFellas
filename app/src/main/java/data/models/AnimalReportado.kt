package com.dogfellas.app.model

import com.google.firebase.firestore.GeoPoint

data class AnimalReportado(
    val id: String = "",
    val especie: String = "",
    val condicion: String = "",
    val urgencia: String = "Media",
    val descripcion: String = "",
    val fotoUri: String = "",
    val ubicacion: GeoPoint? = null,
    val usuarioReporta: String = "",
    val estado: String = "REPORTADO",
    val fechaReporte: Long = 0L
)