
package com.dogfellas.app.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dogfellas.app.data.AuthRepository
import com.dogfellas.app.data.api.NotificacionesRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val repo = AuthRepository()
    private val notificacionesRepository = NotificacionesRepository()

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var isSuccess by mutableStateOf(false)
        private set

    var isLoggingOut by mutableStateOf(false)
        private set

    // ============================================================
    // INICIAR SESIÓN
    // ============================================================

    fun login(email: String, password: String) {
        isLoading = true
        errorMessage = null

        repo.login(email, password) { success, error ->
            isLoading = false
            isSuccess = success
            errorMessage = error

            if (success) registrarDispositivoFCM()
        }
    }

    // ============================================================
    // REGISTRAR USUARIO
    // ============================================================

    fun register(nombre: String, email: String, password: String) {
        isLoading = true
        errorMessage = null

        repo.register(nombre, email, password) { success, error ->
            isLoading = false
            isSuccess = success
            errorMessage = error

            if (success) registrarDispositivoFCM()
        }
    }

    // ============================================================
    // REGISTRAR DISPOSITIVO FCM
    // ============================================================

    private fun registrarDispositivoFCM() {
        viewModelScope.launch {
            notificacionesRepository.registrarDispositivo()
                .onSuccess {
                    Log.d(
                        "DogFellasFCM",
                        "Dispositivo registrado en FastAPI."
                    )
                }
                .onFailure { error ->
                    Log.e(
                        "DogFellasFCM",
                        "No fue posible registrar el dispositivo.",
                        error
                    )
                }
        }
    }

    // ============================================================
    // CERRAR SESIÓN
    // ============================================================

    fun cerrarSesion(onComplete: () -> Unit) {

        if (isLoggingOut) return

        isLoggingOut = true

        viewModelScope.launch {

            try {

                notificacionesRepository.desactivarDispositivo()
                    .onSuccess {
                        Log.d(
                            "DogFellasFCM",
                            "Dispositivo desactivado en FastAPI."
                        )
                    }
                    .onFailure { error ->
                        Log.e(
                            "DogFellasFCM",
                            "No se pudo desactivar el dispositivo.",
                            error
                        )
                    }

            } catch (error: CancellationException) {
                throw error

            } catch (error: Exception) {
                Log.e(
                    "DogFellasFCM",
                    "Error inesperado al desactivar el dispositivo.",
                    error
                )

            } finally {

                repo.logout()
                isLoggingOut = false
                onComplete()
            }
        }
    }

    // ============================================================
    // RECUPERAR CONTRASEÑA
    // ============================================================

    fun recoverPassword(email: String) {
        isLoading = true
        errorMessage = null

        repo.recoverPassword(email) { success, error ->
            isLoading = false
            isSuccess = success
            errorMessage = error
        }
    }

    // ============================================================
    // REINICIAR ESTADO
    // ============================================================

    fun resetState() {
        isSuccess = false
        errorMessage = null
    }

    // ============================================================
    // VERIFICAR SESIÓN
    // ============================================================

    fun isUserLoggedIn(): Boolean {
        return repo.getCurrentUser() != null
    }
}
