package com.dogfellas.app.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.dogfellas.app.data.AuthRepository

class AuthViewModel : ViewModel() {

    private val repo = AuthRepository()

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var isSuccess by mutableStateOf(false)
        private set

    fun login(
        email: String,
        password: String
    ) {
        isLoading = true
        errorMessage = null

        repo.login(email, password) { success, error ->

            isLoading = false
            isSuccess = success
            errorMessage = error
        }
    }

    fun register(
        nombre: String,
        email: String,
        password: String
    ) {
        isLoading = true
        errorMessage = null

        repo.register(
            nombre = nombre,
            email = email,
            password = password
        ) { success, error ->

            isLoading = false
            isSuccess = success
            errorMessage = error
        }
    }

    fun recoverPassword(
        email: String
    ) {
        isLoading = true
        errorMessage = null

        repo.recoverPassword(email) { success, error ->

            isLoading = false
            isSuccess = success
            errorMessage = error
        }
    }

    fun resetState() {
        isSuccess = false
        errorMessage = null
    }

    fun isUserLoggedIn(): Boolean {
        return repo.getCurrentUser() != null
    }
}