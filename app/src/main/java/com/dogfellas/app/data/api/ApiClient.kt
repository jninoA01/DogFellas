package com.dogfellas.app.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private const val BASE_URL =
        "http://10.0.2.2:8000/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
    }

    val authApi: AuthApi by lazy {
        retrofit.create(AuthApi::class.java)
    }

    val animalReportadoApi: AnimalReportadoApi by lazy {
        retrofit.create(AnimalReportadoApi::class.java)
    }

    val animalesCercanosApi: AnimalesCercanosApi by lazy {
        retrofit.create(AnimalesCercanosApi::class.java)
    }

    val adopcionesApi: AdopcionesApi by lazy {
        retrofit.create(AdopcionesApi::class.java)
    }

    val solicitudAdopcionApi: SolicitudAdopcionApi by lazy {
        retrofit.create(SolicitudAdopcionApi::class.java)
    }
    val atencionMedicaApi: AtencionMedicaApi by lazy {
        retrofit.create(AtencionMedicaApi::class.java)
    }
}
