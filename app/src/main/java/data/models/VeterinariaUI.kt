package com.dogfellas.app.model

import com.google.android.gms.maps.model.LatLng

data class VeterinariaUI(

    val nombre: String,

    val direccion: String,

    val posicion: LatLng,

    val rating: Double = 0.0,

    val abierto: Boolean = false,

    val telefono: String? = null,

    val fotoUrl: String? = null
)