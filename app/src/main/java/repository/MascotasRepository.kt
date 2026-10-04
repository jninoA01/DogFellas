package com.dogfellas.app.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import data.models.Mascota
import kotlinx.coroutines.tasks.await

class MascotasRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val TAG = "MascotasRepo"

    private fun uid(): String {
        val uid = auth.currentUser?.uid
        Log.d(TAG, "UID actual: $uid")
        return uid ?: throw IllegalStateException("Usuario no autenticado")
    }

    private fun col() = db.collection("usuarios").document(uid()).collection("mascotas")

    // ── DIAGNÓSTICO ────────────────────────────────────────────────────────
    suspend fun diagnostico(): String {
        return try {
            val uid = auth.currentUser?.uid
                ?: return "❌ No hay usuario autenticado.\nAsegúrate de iniciar sesión antes de guardar mascotas."
            val testRef = db.collection("usuarios").document(uid)
                .collection("_test").document("ping")
            testRef.set(mapOf("ok" to true)).await()
            testRef.delete().await()
            "✅ Firebase OK\nUID: $uid\nEscritura y lectura exitosas en Firestore."
        } catch (e: Exception) {
            "❌ Error Firestore: ${e.message}\n\nPosible causa: reglas de seguridad bloqueando.\n" +
                    "Ve a Firebase Console → Firestore → Reglas y usa:\n" +
                    "match /usuarios/{uid}/{document=**} {\n  allow read, write: if request.auth.uid == uid;\n}"
        }
    }

    // ── CRUD ───────────────────────────────────────────────────────────────
    suspend fun obtenerMascotas(): Result<List<Mascota>> = runCatching {
        Log.d(TAG, "Cargando mascotas...")
        val snap = col().get().await()
        snap.documents.mapNotNull { it.toObject(Mascota::class.java)?.copy(id = it.id) }
            .also { Log.d(TAG, "Mascotas cargadas: ${it.size}") }
    }

    suspend fun agregarMascota(mascota: Mascota): Result<Unit> = runCatching {
        Log.d(TAG, "Guardando: ${mascota.nombre}")
        col().add(mascota).await()
        Log.d(TAG, "Guardado OK")
    }

    suspend fun actualizarMascota(mascota: Mascota): Result<Unit> = runCatching {
        col().document(mascota.id).set(mascota).await()
    }

    suspend fun eliminarMascota(id: String): Result<Unit> = runCatching {
        col().document(id).delete().await()
    }
}