package data.models

// IMPORTANTE: Todos los campos tienen un valor por defecto (= "")
// Esto crea automáticamente el "constructor vacío" que Firebase necesita.

data class Emergencia(
    val id: String = "",
    val titulo: String = "",
    val icono: String = "",
    val nivel: String = "",
    val queHacer: String = "",
    val cuandoIrAlVet: String = "",
    // Usamos List<String> porque en Firestore son "Arrays"
    val especie: List<String> = emptyList(),
    val sintomas: List<String> = emptyList()
)