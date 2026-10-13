package com.dogfellas.app.data.api

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

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

interface AtencionMedicaApi {

    @GET("atencion-medica/emergencias")
    suspend fun obtenerEmergencias(
        @Query("especie") especie: String? = null
    ): Response<List<EmergenciaApiResponse>>

    @GET("atencion-medica/medicamentos")
    suspend fun obtenerMedicamentos(
        @Query("especie") especie: String? = null
    ): Response<List<MedicamentoApiResponse>>
}
