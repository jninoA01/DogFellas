
package data.models

data class HistorialMedicoMascota(
    val id: String = "",
    val veterinario: String = "",
    val fecha: Long = System.currentTimeMillis(),
    val tipo: String = "",
    val diagnostico: String = "",
    val tratamiento: String = "",
    val observaciones: String = "",
    val fechaCreacion: Long = System.currentTimeMillis()
)

data class CitaVeterinariaMascota(
    val id: String = "",
    val veterinario: String = "",
    val direccionVeterinaria: String = "",
    val fecha: Long = System.currentTimeMillis(),
    val motivo: String = "",
    val estado: String = "PENDIENTE",
    val observaciones: String = "",
    val fechaCreacion: Long = System.currentTimeMillis()
)

data class VacunacionMascota(
    val id: String = "",
    val vacuna: String = "",
    val fecha: Long = System.currentTimeMillis(),
    val proximaDosis: Long? = null,
    val veterinario: String = "",
    val observaciones: String = "",
    val fechaCreacion: Long = System.currentTimeMillis()
)

data class MedicamentoMascota(
    val id: String = "",
    val nombre: String = "",
    val dosis: String = "",
    val frecuencia: String = "",
    val fechaInicio: Long = System.currentTimeMillis(),
    val fechaFin: Long? = null,
    val veterinario: String = "",
    val observaciones: String = "",
    val activo: Boolean = true,
    val fechaCreacion: Long = System.currentTimeMillis()
)

data class DesparasitacionMascota(
    val id: String = "",
    val producto: String = "",
    val fecha: Long = System.currentTimeMillis(),
    val proximaFecha: Long? = null,
    val veterinario: String = "",
    val observaciones: String = "",
    val fechaCreacion: Long = System.currentTimeMillis()
)

data class EmergenciaMascota(
    val id: String = "",
    val fecha: Long = System.currentTimeMillis(),
    val motivo: String = "",
    val atencionRealizada: String = "",
    val veterinario: String = "",
    val resultado: String = "",
    val observaciones: String = "",
    val fechaCreacion: Long = System.currentTimeMillis()
)
