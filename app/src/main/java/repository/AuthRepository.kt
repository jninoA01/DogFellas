package com.dogfellas.app.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun login(
        email: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false, it.exception?.message)
                }
            }
    }

    fun register(
        nombre: String,
        email: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) {

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->

                if (!task.isSuccessful) {
                    onResult(false, task.exception?.message)
                    return@addOnCompleteListener
                }

                val usuario = auth.currentUser

                if (usuario == null) {
                    onResult(false, "No fue posible obtener el usuario creado.")
                    return@addOnCompleteListener
                }

                val uid = usuario.uid

                /*
                 * Documento privado del usuario.
                 * Aquí mantenemos nombre + email.
                 */
                val datosUsuario = hashMapOf(
                    "nombre" to nombre,
                    "email" to email
                )

                /*
                 * Perfil público.
                 * Solo contiene el nombre.
                 */
                val perfilPublico = hashMapOf(
                    "nombre" to nombre
                )

                db.collection("usuarios")
                    .document(uid)
                    .set(datosUsuario)
                    .addOnSuccessListener {

                        db.collection("perfiles_publicos")
                            .document(uid)
                            .set(perfilPublico)
                            .addOnSuccessListener {

                                onResult(true, null)

                            }
                            .addOnFailureListener { error ->

                                onResult(
                                    false,
                                    error.message
                                        ?: "No fue posible crear el perfil público."
                                )
                            }
                    }
                    .addOnFailureListener { error ->

                        onResult(
                            false,
                            error.message
                                ?: "No fue posible guardar los datos del usuario."
                        )
                    }
            }
    }

    fun recoverPassword(
        email: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false, it.exception?.message)
                }
            }
    }

    fun getCurrentUser() = auth.currentUser

    fun logout() = auth.signOut()
}