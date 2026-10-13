package com.dogfellas.app.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

data class AuthTestResponse(
    val autenticado: Boolean,
    val firebase_uid: String?,
    val correo: String?
)

data class SincronizarUsuarioRequest(
    val nombre: String,
    val telefono: String? = null
)

data class UsuarioApiResponse(
    val id: Int,
    val firebase_uid: String,
    val nombre: String,
    val correo: String,
    val telefono: String?,
    val fecha_registro: String
)

interface AuthApi {

    @GET("auth-test/")
    suspend fun probarAutenticacion(
        @Header("Authorization")
        authorization: String
    ): Response<AuthTestResponse>

    @POST("usuarios/sincronizar")
    suspend fun sincronizarUsuario(
        @Header("Authorization")
        authorization: String,
        @Body
        datos: SincronizarUsuarioRequest
    ): Response<UsuarioApiResponse>
}
