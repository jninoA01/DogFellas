package com.dogfellas.app.data.api

import com.dogfellas.app.data.AuthRepository

class AuthApiRepository {

    private val authRepository = AuthRepository()

    suspend fun probarConexion(): Result<AuthTestResponse> {

        return try {

            val token = obtenerToken()

            if (token == null) {
                return Result.failure(
                    Exception(
                        "No fue posible obtener el token de Firebase."
                    )
                )
            }

            val respuesta = ApiClient.authApi
                .probarAutenticacion(
                    authorization = "Bearer $token"
                )

            if (respuesta.isSuccessful) {

                val datos = respuesta.body()

                if (datos != null) {
                    Result.success(datos)
                } else {
                    Result.failure(
                        Exception(
                            "FastAPI respondió correctamente, pero no devolvió datos."
                        )
                    )
                }

            } else {

                Result.failure(
                    Exception(
                        "FastAPI respondió con código ${respuesta.code()}: ${respuesta.errorBody()?.string()}"
                    )
                )
            }

        } catch (e: Exception) {

            Result.failure(
                Exception(
                    "Error conectando con FastAPI: ${e.message}",
                    e
                )
            )
        }
    }

    private suspend fun obtenerToken(): String? {

        return kotlinx.coroutines.suspendCancellableCoroutine { continuation ->

            authRepository.getFirebaseIdToken { token, error ->

                if (token != null) {
                    continuation.resume(token) {}
                } else {
                    continuation.resume(null) {}
                }
            }
        }
    }
}
