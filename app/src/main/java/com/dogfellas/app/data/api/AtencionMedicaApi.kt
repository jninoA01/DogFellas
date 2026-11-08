
package com.dogfellas.app.data.api

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

// ============================================================
// CATÁLOGOS EXISTENTES: SE CONSERVAN
// ============================================================

data class EmergenciaApiResponse(
    val id: String = "",
    val titulo: String = "",
    val icono: String = "",
    val nivel: String = "",
    @SerializedName("que_hacer")
    val queHacer: String = "",
    @SerializedName("cuando_ir_al_vet")
    val cuandoIrAlVet: String = "",
    val especie: List<String> = emptyList(),
    val sintomas: List<String> = emptyList()
)

data class MedicamentoApiResponse(
    val id: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val especie: List<String> = emptyList(),
    val dosis: String = "",
    val precio: Double = 0.0,
    val busqueda: String = "",
    val icono: String = "💊",
    val categoria: String = ""
)

// ============================================================
// DTO DE REGISTROS MÉDICOS
// ============================================================

data class HistorialMedicoApiDto(
    val id: String = "",
    @SerializedName("reporte_id") val reporteId: Int = 0,
    @SerializedName("usuario_id") val usuarioId: Int? = null,
    val veterinario: String = "",
    val fecha: Long = 0L,
    val tipo: String = "EVALUACION",
    val diagnostico: String = "",
    val tratamiento: String = "",
    val observaciones: String = ""
)

data class HistorialMedicoCreateApiDto(
    val veterinario: String = "",
    val fecha: Long = 0L,
    val tipo: String = "EVALUACION",
    val diagnostico: String = "",
    val tratamiento: String = "",
    val observaciones: String = ""
)

data class MedicamentoAnimalApiDto(
    val id: String = "",
    @SerializedName("reporte_id") val reporteId: Int = 0,
    @SerializedName("usuario_id") val usuarioId: Int? = null,
    val nombre: String = "",
    val dosis: String = "",
    val frecuencia: String = "",
    @SerializedName("fecha_inicio") val fechaInicio: Long = 0L,
    @SerializedName("fecha_fin") val fechaFin: Long = 0L,
    val veterinario: String = "",
    val observaciones: String = "",
    val activo: Boolean = true
)

data class MedicamentoAnimalCreateApiDto(
    val nombre: String = "",
    val dosis: String = "",
    val frecuencia: String = "",
    @SerializedName("fecha_inicio") val fechaInicio: Long = 0L,
    @SerializedName("fecha_fin") val fechaFin: Long = 0L,
    val veterinario: String = "",
    val observaciones: String = "",
    val activo: Boolean = true
)

data class VacunacionAnimalApiDto(
    val id: String = "",
    @SerializedName("reporte_id") val reporteId: Int = 0,
    @SerializedName("usuario_id") val usuarioId: Int? = null,
    val vacuna: String = "",
    val fecha: Long = 0L,
    @SerializedName("proxima_dosis") val proximaDosis: Long = 0L,
    val veterinario: String = "",
    val observaciones: String = ""
)

data class VacunacionAnimalCreateApiDto(
    val vacuna: String = "",
    val fecha: Long = 0L,
    @SerializedName("proxima_dosis") val proximaDosis: Long = 0L,
    val veterinario: String = "",
    val observaciones: String = ""
)

data class DesparasitacionAnimalApiDto(
    val id: String = "",
    @SerializedName("reporte_id") val reporteId: Int = 0,
    @SerializedName("usuario_id") val usuarioId: Int? = null,
    val producto: String = "",
    val fecha: Long = 0L,
    @SerializedName("proxima_fecha") val proximaFecha: Long = 0L,
    val veterinario: String = "",
    val observaciones: String = ""
)

data class DesparasitacionAnimalCreateApiDto(
    val producto: String = "",
    val fecha: Long = 0L,
    @SerializedName("proxima_fecha") val proximaFecha: Long = 0L,
    val veterinario: String = "",
    val observaciones: String = ""
)

data class CitaVeterinariaAnimalApiDto(
    val id: String = "",
    @SerializedName("reporte_id") val reporteId: Int = 0,
    @SerializedName("usuario_id") val usuarioId: Int? = null,
    val veterinario: String = "",
    @SerializedName("direccion_veterinaria")
    val direccionVeterinaria: String = "",
    val fecha: Long = 0L,
    val motivo: String = "",
    val estado: String = "PROGRAMADA",
    val observaciones: String = ""
)

data class CitaVeterinariaAnimalCreateApiDto(
    val veterinario: String = "",
    @SerializedName("direccion_veterinaria")
    val direccionVeterinaria: String = "",
    val fecha: Long = 0L,
    val motivo: String = "",
    val estado: String = "PROGRAMADA",
    val observaciones: String = ""
)

data class EmergenciaAnimalApiDto(
    val id: String = "",
    @SerializedName("reporte_id") val reporteId: Int = 0,
    @SerializedName("usuario_id") val usuarioId: Int? = null,
    val fecha: Long = 0L,
    val motivo: String = "",
    @SerializedName("atencion_realizada")
    val atencionRealizada: String = "",
    val veterinario: String = "",
    val resultado: String = "",
    val observaciones: String = ""
)

data class EmergenciaAnimalCreateApiDto(
    val fecha: Long = 0L,
    val motivo: String = "",
    @SerializedName("atencion_realizada")
    val atencionRealizada: String = "",
    val veterinario: String = "",
    val resultado: String = "",
    val observaciones: String = ""
)

// ============================================================
// API
// ============================================================

interface AtencionMedicaApi {

    // Catálogos existentes
    @GET("atencion-medica/emergencias")
    suspend fun obtenerEmergencias(
        @Query("especie") especie: String? = null
    ): Response<List<EmergenciaApiResponse>>

    @GET("atencion-medica/medicamentos")
    suspend fun obtenerMedicamentos(
        @Query("especie") especie: String? = null
    ): Response<List<MedicamentoApiResponse>>

    // Historial médico
    @GET("atencion-medica/animales/{animal_id}/historial-medico")
    suspend fun obtenerHistorialMedicoAnimal(
        @Header("Authorization") token: String,
        @Path("animal_id") animalId: Int
    ): Response<List<HistorialMedicoApiDto>>

    @POST("atencion-medica/animales/{animal_id}/historial-medico")
    suspend fun crearHistorialMedicoAnimal(
        @Header("Authorization") token: String,
        @Path("animal_id") animalId: Int,
        @Body datos: HistorialMedicoCreateApiDto
    ): Response<HistorialMedicoApiDto>

    // Medicamentos del animal
    @GET("atencion-medica/animales/{animal_id}/medicamentos")
    suspend fun obtenerMedicamentosAnimal(
        @Header("Authorization") token: String,
        @Path("animal_id") animalId: Int
    ): Response<List<MedicamentoAnimalApiDto>>

    @POST("atencion-medica/animales/{animal_id}/medicamentos")
    suspend fun crearMedicamentoAnimal(
        @Header("Authorization") token: String,
        @Path("animal_id") animalId: Int,
        @Body datos: MedicamentoAnimalCreateApiDto
    ): Response<MedicamentoAnimalApiDto>

    // Vacunaciones
    @GET("atencion-medica/animales/{animal_id}/vacunaciones")
    suspend fun obtenerVacunacionesAnimal(
        @Header("Authorization") token: String,
        @Path("animal_id") animalId: Int
    ): Response<List<VacunacionAnimalApiDto>>

    @POST("atencion-medica/animales/{animal_id}/vacunaciones")
    suspend fun crearVacunacionAnimal(
        @Header("Authorization") token: String,
        @Path("animal_id") animalId: Int,
        @Body datos: VacunacionAnimalCreateApiDto
    ): Response<VacunacionAnimalApiDto>

    // Desparasitaciones
    @GET("atencion-medica/animales/{animal_id}/desparasitaciones")
    suspend fun obtenerDesparasitacionesAnimal(
        @Header("Authorization") token: String,
        @Path("animal_id") animalId: Int
    ): Response<List<DesparasitacionAnimalApiDto>>

    @POST("atencion-medica/animales/{animal_id}/desparasitaciones")
    suspend fun crearDesparasitacionAnimal(
        @Header("Authorization") token: String,
        @Path("animal_id") animalId: Int,
        @Body datos: DesparasitacionAnimalCreateApiDto
    ): Response<DesparasitacionAnimalApiDto>

    // Citas veterinarias
    @GET("atencion-medica/animales/{animal_id}/citas")
    suspend fun obtenerCitasVeterinariasAnimal(
        @Header("Authorization") token: String,
        @Path("animal_id") animalId: Int
    ): Response<List<CitaVeterinariaAnimalApiDto>>

    @POST("atencion-medica/animales/{animal_id}/citas")
    suspend fun crearCitaVeterinariaAnimal(
        @Header("Authorization") token: String,
        @Path("animal_id") animalId: Int,
        @Body datos: CitaVeterinariaAnimalCreateApiDto
    ): Response<CitaVeterinariaAnimalApiDto>

    // Emergencias registradas para el animal
    @GET("atencion-medica/animales/{animal_id}/emergencias")
    suspend fun obtenerEmergenciasAnimal(
        @Header("Authorization") token: String,
        @Path("animal_id") animalId: Int
    ): Response<List<EmergenciaAnimalApiDto>>

    @POST("atencion-medica/animales/{animal_id}/emergencias")
    suspend fun crearEmergenciaAnimal(
        @Header("Authorization") token: String,
        @Path("animal_id") animalId: Int,
        @Body datos: EmergenciaAnimalCreateApiDto
    ): Response<EmergenciaAnimalApiDto>
}
