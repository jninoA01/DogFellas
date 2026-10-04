package data.models

// ── Medicamento ────────────────────────────────────────────────────────────
data class Medicamento(
    val id: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val especie: List<String> = emptyList(),
    val dosis: String = "",
    val precio: Double = 0.0,
    val busqueda: String = "",        // keyword para buscar en MercadoLibre
    val icono: String = "💊",
    val categoria: String = ""        // "antipulgas" | "antibiotico" | "vitamina" | etc
)

// ── Articulo de noticias (NewsAPI) ─────────────────────────────────────────
data class Articulo(
    val titulo: String = "",
    val descripcion: String = "",
    val urlImagen: String = "",
    val url: String = "",
    val fuente: String = "",
    val fecha: String = ""
)