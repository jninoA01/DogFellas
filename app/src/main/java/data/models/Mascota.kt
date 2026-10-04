package data.models

data class Mascota(
    val id: String = "",
    val nombre: String = "",
    val tipo: String = "",          // Perro, Gato, Conejo, etc.
    val raza: String = "",
    val edad: Int = 0,
    val sexo: String = "",          // Macho / Hembra
    val color: String = "",
    val peso: Double = 0.0,
    val vacunado: Boolean = false,
    val descripcion: String = ""
)