package com.dogfellas.app.data

import com.dogfellas.app.data.api.ApiClient
import com.dogfellas.app.data.api.SincronizarUsuarioRequest
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthRepository {

    private val auth: FirebaseAuth =
        FirebaseAuth.getInstance()

    private val repositoryScope =
        CoroutineScope(
            SupervisorJob() + Dispatchers.IO
        )

    // =====================================================
    // LOGIN
    // =====================================================

    fun login(
        email: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) {

        auth.signInWithEmailAndPassword(
            email,
            password
        )
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    onResult(
                        true,
                        null
                    )

                } else {

                    onResult(
                        false,
                        task.exception?.message
                    )
                }
            }
    }

    // =====================================================
    // REGISTRO
    // =====================================================

    fun register(
        nombre: String,
        email: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) {

        // 1. Crear usuario en Firebase Authentication

        auth.createUserWithEmailAndPassword(
            email,
            password
        )
            .addOnCompleteListener { task ->

                if (!task.isSuccessful) {

                    onResult(
                        false,
                        task.exception?.message
                            ?: "No fue posible crear la cuenta."
                    )

                    return@addOnCompleteListener
                }

                // 2. Obtener usuario creado

                val usuario =
                    auth.currentUser

                if (usuario == null) {

                    onResult(
                        false,
                        "No fue posible obtener el usuario creado."
                    )

                    return@addOnCompleteListener
                }

                // 3. Obtener token de Firebase

                usuario.getIdToken(true)
                    .addOnCompleteListener { tokenTask ->

                        if (!tokenTask.isSuccessful) {

                            onResult(
                                false,
                                tokenTask.exception?.message
                                    ?: "No fue posible obtener el token de Firebase."
                            )

                            return@addOnCompleteListener
                        }

                        val token =
                            tokenTask.result?.token

                        if (token.isNullOrBlank()) {

                            onResult(
                                false,
                                "Firebase no devolvió un ID Token válido."
                            )

                            return@addOnCompleteListener
                        }

                        // 4. Enviar información a FastAPI

                        repositoryScope.launch {

                            try {

                                val respuesta =
                                    ApiClient.authApi
                                        .sincronizarUsuario(
                                            authorization =
                                                "Bearer $token",

                                            datos =
                                                SincronizarUsuarioRequest(
                                                    nombre = nombre.trim(),
                                                    telefono = null
                                                )
                                        )

                                // 5. Procesar respuesta

                                if (respuesta.isSuccessful) {

                                    val usuarioPostgres =
                                        respuesta.body()

                                    if (usuarioPostgres == null) {

                                        withContext(
                                            Dispatchers.Main
                                        ) {
                                            onResult(
                                                false,
                                                "FastAPI respondió correctamente, pero no devolvió los datos del usuario."
                                            )
                                        }

                                        return@launch
                                    }

                                    // Registro completo

                                    withContext(
                                        Dispatchers.Main
                                    ) {
                                        onResult(
                                            true,
                                            null
                                        )
                                    }

                                } else {

                                    val error =
                                        respuesta
                                            .errorBody()
                                            ?.string()

                                    withContext(
                                        Dispatchers.Main
                                    ) {

                                        onResult(
                                            false,
                                            "El usuario fue creado en Firebase, pero no pudo sincronizarse con PostgreSQL.\n\n" +
                                                    "Código FastAPI: ${respuesta.code()}\n" +
                                                    "Detalle: ${error ?: "Sin detalle"}"
                                        )
                                    }
                                }

                            } catch (e: Exception) {

                                withContext(
                                    Dispatchers.Main
                                ) {

                                    onResult(
                                        false,
                                        "El usuario fue creado en Firebase, pero ocurrió un error al conectar con FastAPI.\n\n" +
                                                "${e.message}"
                                    )
                                }
                            }
                        }
                    }
            }
    }

    // =====================================================
    // RECUPERAR CONTRASEÑA
    // =====================================================

    fun recoverPassword(
        email: String,
        onResult: (Boolean, String?) -> Unit
    ) {

        auth.sendPasswordResetEmail(
            email
        )
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    onResult(
                        true,
                        null
                    )

                } else {

                    onResult(
                        false,
                        task.exception?.message
                    )
                }
            }
    }

    // =====================================================
    // OBTENER FIREBASE ID TOKEN
    // =====================================================

    fun getFirebaseIdToken(
        onResult: (String?, String?) -> Unit
    ) {

        val usuario =
            auth.currentUser

        if (usuario == null) {

            onResult(
                null,
                "No hay ningún usuario autenticado."
            )

            return
        }

        usuario.getIdToken(true)
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    val token =
                        task.result?.token

                    if (token != null) {

                        onResult(
                            token,
                            null
                        )

                    } else {

                        onResult(
                            null,
                            "Firebase no devolvió el ID Token."
                        )
                    }

                } else {

                    onResult(
                        null,
                        task.exception?.message
                            ?: "No fue posible obtener el ID Token."
                    )
                }
            }
    }

    // =====================================================
    // USUARIO ACTUAL
    // =====================================================

    fun getCurrentUser() =
        auth.currentUser

    // =====================================================
    // CERRAR SESIÓN
    // =====================================================

    fun logout() =
        auth.signOut()
}
