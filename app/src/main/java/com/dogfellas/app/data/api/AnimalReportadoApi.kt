package com.dogfellas.app.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.POST
import retrofit2.http.Path

// =========================================================
// CREAR ANIMAL REPORTADO
// =========================================================

data class CrearAnimalReportadoRequest(
    val especie: String,
    val condicion: String,
    val urgencia: String,
    val descripcion: String,
    val foto_uri: String? = null,
    val latitud: Double? = null,
    val longitud: Double? = null,
    val estado: String = "REPORTADO"
)

// =========================================================
// ACTUALIZAR ANIMAL
// Se conserva para compatibilidad con el código existente.
// =========================================================

data class ActualizarAnimalReportadoRequest(
    val estado: String
)

// =========================================================
// RESPUESTA DEL ANIMAL
// =========================================================

data class AnimalReportadoApiResponse(
    val id: Int,
    val especie: String,
    val condicion: String,
    val urgencia: String,
    val descripcion: String,
    val foto_uri: String?,
    val latitud: Double?,
    val longitud: Double?,
    val estado: String,
    val fecha_reporte: String,
    val usuario_id: Int?,
    val usuario_reporta: String?
)

// =========================================================
// REGISTRAR CAMBIO EN EL HISTORIAL
// =========================================================

data class CrearHistorialEstadoRequest(
    val estado: String,
    val observacion: String = ""
)

// =========================================================
// RESPUESTA DEL HISTORIAL
// =========================================================

data class HistorialEstadoApiResponse(
    val id: Int,
    val animal_id: Int,
    val estado: String,
    val fecha: String,
    val usuario_id: Int?,
    val observacion: String
)

// =========================================================
// API DE ANIMALES REPORTADOS
// =========================================================

interface AnimalReportadoApi {

    @POST("animales-reportados/")
    suspend fun crearAnimalReportado(
        @Header("Authorization") authorization: String,
        @Body datos: CrearAnimalReportadoRequest
    ): Response<AnimalReportadoApiResponse>

    @GET("animales-reportados/{animal_id}")
    suspend fun obtenerAnimalPorId(
        @Path("animal_id") animalId: Int
    ): Response<AnimalReportadoApiResponse>

    @PUT("animales-reportados/{animal_id}")
    suspend fun actualizarAnimalReportado(
        @Path("animal_id") animalId: Int,
        @Body datos: ActualizarAnimalReportadoRequest
    ): Response<AnimalReportadoApiResponse>

    // -----------------------------------------------------
    // CONSULTAR HISTORIAL EN POSTGRESQL
    // -----------------------------------------------------

    @GET("animales-reportados/{animal_id}/historial")
    suspend fun obtenerHistorial(
        @Path("animal_id") animalId: Int
    ): Response<List<HistorialEstadoApiResponse>>

    // -----------------------------------------------------
    // REGISTRAR CAMBIO DE ESTADO EN POSTGRESQL
    // Requiere el token de Firebase Authentication.
    // -----------------------------------------------------

    @POST("animales-reportados/{animal_id}/historial")
    suspend fun registrarCambioEstado(
        @Path("animal_id") animalId: Int,
        @Header("Authorization") authorization: String,
        @Body datos: CrearHistorialEstadoRequest
    ): Response<HistorialEstadoApiResponse>
}
