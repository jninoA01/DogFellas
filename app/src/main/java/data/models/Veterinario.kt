package data.models

data class Veterinario(
    val nombre: String,
    val direccion: String,
    val rating: Float? = null,
    val latLng: Pair<Double, Double>? = null
)