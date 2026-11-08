package com.dogfellas.app.service

import android.util.Log
import com.dogfellas.app.data.api.NotificacionesRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class DogFellasMessagingService : FirebaseMessagingService() {

    private val serviceScope =
        CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val notificacionesRepository =
        NotificacionesRepository()

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        Log.d(
            "DogFellasFCM",
            "Firebase generó un nuevo token FCM."
        )

        serviceScope.launch {

            notificacionesRepository
                .registrarDispositivo(token)
                .onSuccess {
                    Log.d(
                        "DogFellasFCM",
                        "Nuevo token registrado en FastAPI."
                    )
                }
                .onFailure { error ->
                    Log.e(
                        "DogFellasFCM",
                        "No se pudo registrar el nuevo token.",
                        error
                    )
                }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Log.d(
            "DogFellasFCM",
            "Notificación recibida: ${message.notification?.title}"
        )

        Log.d(
            "DogFellasFCM",
            "Mensaje: ${message.notification?.body}"
        )
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }
}
