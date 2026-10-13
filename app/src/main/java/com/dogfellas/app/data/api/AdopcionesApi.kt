package com.dogfellas.app.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface AdopcionesApi {

    @GET("animales-reportados/adopcion")
    suspend fun obtenerAnimalesEnAdopcion(
        @Header("Authorization")
        authorization: String
    ): Response<List<AnimalReportadoApiResponse>>
}
