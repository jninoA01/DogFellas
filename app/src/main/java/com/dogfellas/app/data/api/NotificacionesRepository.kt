package com.dogfellas.app.data.api

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

class NotificacionesRepository {

    private val auth = FirebaseAuth.getInstance()

    // Registrar el token FCM actual
    suspend fun registrarDispositivo(): Result<Unit> {
        return try {
            val tokenFCM = FirebaseMessaging.getInstance()
                .token
                .await()

            registrarDispositivo(tokenFCM)

        } catch (e: Exception) {
            Result.failure(
                Exception("Error obteniendo el token FCM: ${e.message}", e)
            )
        }
    }

    // Registrar un token FCM específico
    suspend fun registrarDispositivo(
        tokenFCM: String
    ): Result<Unit> {

        return try {

            val usuario = auth.currentUser
                ?: return Result.failure(
                    Exception("No hay ningún usuario autenticado.")
                )

            if (tokenFCM.isBlank()) {
                return Result.failure(
                    Exception("El token FCM está vacío.")
                )
            }

            val idToken = usuario.getIdToken(false).await().token

            if (idToken.isNullOrBlank()) {
                return Result.failure(
                    Exception("No fue posible obtener el ID Token.")
                )
            }

            val respuesta = ApiClient.notificacionesApi
                .registrarDispositivo(
                    authorization = "Bearer $idToken",
                    datos = RegistrarDispositivoRequest(
                        token_fcm = tokenFCM,
                        plataforma = "ANDROID"
                    )
                )

            if (respuesta.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(
                    Exception(
                        "Error registrando dispositivo. " +
                        "Código: ${respuesta.code()}. " +
                        "Detalle: ${respuesta.errorBody()?.string()}"
                    )
                )
            }

        } catch (e: Exception) {
            Result.failure(
                Exception(
                    "No fue posible registrar el dispositivo: ${e.message}",
                    e
                )
            )
        }
    }
// Desactivar el dispositivo actual
suspend fun desactivarDispositivo(): Result<Unit> {
    return try {
        val usuario = auth.currentUser
            ?: return Result.failure(
                Exception("No hay ningún usuario autenticado.")
            )

        val tokenFCM = FirebaseMessaging.getInstance()
            .token
            .await()

        val idToken = usuario.getIdToken(false).await().token

        if (idToken.isNullOrBlank()) {
            return Result.failure(
                Exception("No fue posible obtener el ID Token.")
            )
        }

        val respuesta = ApiClient.notificacionesApi
            .desactivarDispositivo(
                authorization = "Bearer $idToken",
                tokenFCM = tokenFCM
            )

        if (respuesta.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(
                Exception(
                    "Error desactivando dispositivo. " +
                    "Código: ${respuesta.code()}. " +
                    "Detalle: ${respuesta.errorBody()?.string()}"
                )
            )
        }
    } catch (e: Exception) {
        Result.failure(
            Exception(
                "No fue posible desactivar el dispositivo: ${e.message}",
                e
            )
        )
    }
}

}
