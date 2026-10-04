package com.dogfellas.app.repository

import com.google.firebase.firestore.FirebaseFirestore
import data.models.Emergencia
import data.models.Medicamento
import kotlinx.coroutines.tasks.await

class AtencionMedicaRepository {

    private val db = FirebaseFirestore.getInstance()

    // ── Emergencias ───────────────────────────────────────────────────────
    suspend fun obtenerEmergencias(): Result<List<Emergencia>> = runCatching {
        db.collection("emergencias").get().await().documents
            .mapNotNull { it.toObject(Emergencia::class.java)?.copy(id = it.id) }
    }

    suspend fun obtenerEmergenciasPorEspecie(especie: String): Result<List<Emergencia>> = runCatching {
        db.collection("emergencias")
            .whereArrayContains("especie", especie)
            .get().await().documents
            .mapNotNull { it.toObject(Emergencia::class.java)?.copy(id = it.id) }
    }

    // ── Medicamentos ──────────────────────────────────────────────────────
    suspend fun obtenerMedicamentos(): Result<List<Medicamento>> = runCatching {
        db.collection("medicamentos").get().await().documents
            .mapNotNull { it.toObject(Medicamento::class.java)?.copy(id = it.id) }
    }

    suspend fun obtenerMedicamentosPorEspecie(especie: String): Result<List<Medicamento>> = runCatching {
        db.collection("medicamentos")
            .whereArrayContains("especie", especie)
            .get().await().documents
            .mapNotNull { it.toObject(Medicamento::class.java)?.copy(id = it.id) }
    }
}