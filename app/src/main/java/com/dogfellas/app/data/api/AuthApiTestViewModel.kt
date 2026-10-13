package com.dogfellas.app.data.api

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthApiTestViewModel : ViewModel() {

    private val repository = AuthApiRepository()

    private val _resultado =
        MutableStateFlow<String>("Sin probar")

    val resultado: StateFlow<String> = _resultado

    fun probar() {

        viewModelScope.launch {

            _resultado.value = "Conectando con FastAPI..."

            val resultado =
                repository.probarConexion()

            resultado
                .onSuccess { respuesta ->

                    _resultado.value =
                        """
                        AUTENTICACIÓN CORRECTA

                        Firebase UID:
                        ${respuesta.firebase_uid}

                        Correo:
                        ${respuesta.correo}
                        """.trimIndent()
                }
                .onFailure { error ->

                    _resultado.value =
                        """
                        ERROR

                        ${error.message}
                        """.trimIndent()
                }
        }
    }
}
