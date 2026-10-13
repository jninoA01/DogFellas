package com.dogfellas.app.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

data class AnimalCercanoApiResponse(
    val id: Int,
    val especie: String,
    val condicion: String,
    val urgencia: String,
    val descripcion: String,
    val foto_uri: String?,
    val latitud: Double?,
    val longitud: Double?,
    val usuario_id: Int?,
    val usuario_reporta: String?,
    val estado: String,
    val fecha_reporte: String,
    val distancia_km: Double
)

interface AnimalesCercanosApi {

    @GET("animales-reportados/")
    suspend fun obtenerReportes(
        @Header("Authorization")
        authorization: String
    ): Response<List<AnimalCercanoApiResponse>>

    @GET("animales-reportados/cercanos")
    suspend fun obtenerAnimalesCercanos(
        @Header("Authorization")
        authorization: String,
        @Query("latitud")
        latitud: Double,
        @Query("longitud")
        longitud: Double,
        @Query("radio_km")
        radioKm: Double = 5.0
    ): Response<List<AnimalCercanoApiResponse>>
}
