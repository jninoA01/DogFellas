
package com.dogfellas.app.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.DELETE
import retrofit2.http.Query

data class RegistrarDispositivoRequest(
    val token_fcm: String,
    val plataforma: String = "ANDROID"
)

interface NotificacionesApi {

    @POST("notificaciones/registrar-dispositivo")
    suspend fun registrarDispositivo(
        @Header("Authorization") authorization: String,
        @Body datos: RegistrarDispositivoRequest
    ): Response<Unit>

    @DELETE("notificaciones/desactivar-dispositivo")
    suspend fun desactivarDispositivo(
        @Header("Authorization") authorization: String,
        @Query("token_fcm") tokenFCM: String
    ): Response<Unit>
}
