
package com.dogfellas.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.dogfellas.app.navigation.DogFellasNavGraph
import com.dogfellas.app.service.NotificacionesMedicas
import com.dogfellas.app.ui.theme.DogFellasTheme
import com.google.android.libraries.places.api.Places
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : ComponentActivity() {

    // Solicitud del permiso para mostrar notificaciones
    private val solicitarPermisoNotificaciones =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { concedido ->

            if (concedido) {
                Log.d(
                    "DogFellasNotificaciones",
                    "Permiso de notificaciones concedido."
                )
            } else {
                Log.w(
                    "DogFellasNotificaciones",
                    "El usuario no concedió el permiso de notificaciones."
                )
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        // Crear el canal de notificaciones médicas
        NotificacionesMedicas.crearCanal(applicationContext)

        // Solicitar permiso en Android 13 o superior
        solicitarPermisoDeNotificaciones()

        // Inicializar Google Places
        if (!Places.isInitialized()) {
            Places.initialize(
                applicationContext,
                "AIzaSyBLwewMx6UCGOsZIr_Asrnhc14S7ztYLU8"
            )
        }

        // Obtener token de Firebase Cloud Messaging
        obtenerTokenFCM()

        // Interfaz principal
        setContent {
            DogFellasTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val navController = rememberNavController()

                    DogFellasNavGraph(
                        navController = navController
                    )
                }
            }
        }
    }

    // SOLICITAR PERMISO DE NOTIFICACIONES

    private fun solicitarPermisoDeNotificaciones() {

        // Android 12 o inferior no necesita este permiso en tiempo de ejecución
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return
        }

        val permisoConcedido = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

        if (!permisoConcedido) {
            solicitarPermisoNotificaciones.launch(
                Manifest.permission.POST_NOTIFICATIONS
            )
        }
    }

    // OBTENER TOKEN DE FIREBASE CLOUD MESSAGING

    private fun obtenerTokenFCM() {

        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { tarea ->

                if (!tarea.isSuccessful) {
                    Log.e(
                        "DogFellasFCM",
                        "Error al obtener el token FCM",
                        tarea.exception
                    )
                    return@addOnCompleteListener
                }

                val token = tarea.result

                Log.d(
                    "DogFellasFCM",
                    "Token FCM obtenido correctamente: $token"
                )
            }
    }
}
