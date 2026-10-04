package com.dogfellas.app.repository

import com.dogfellas.app.model.SolicitudAdopcion
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class SolicitudAdopcionRepository {

    private val db = FirebaseFirestore.getInstance()

    private val collection =
        db.collection("solicitudes_adopcion")


    // =========================================================
    // CREAR SOLICITUD
    // =========================================================

    suspend fun crearSolicitud(
        solicitud: SolicitudAdopcion
    ): Result<Unit> {

        return try {

            val documento = collection.document()

            val solicitudConId =
                solicitud.copy(
                    id = documento.id
                )

            documento
                .set(solicitudConId)
                .await()

            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(
                Exception(
                    e.message
                        ?: "No fue posible registrar la solicitud."
                )
            )
        }
    }


    // =========================================================
    // BUSCAR SOLICITUD EXISTENTE
    // =========================================================

    suspend fun obtenerSolicitudExistente(
        reporteId: String,
        usuarioId: String
    ): Result<SolicitudAdopcion?> {

        return try {

            val snapshot = collection
                .whereEqualTo(
                    "reporteId",
                    reporteId
                )
                .whereEqualTo(
                    "usuarioId",
                    usuarioId
                )
                .limit(1)
                .get()
                .await()


            if (snapshot.isEmpty) {

                Result.success(null)

            } else {

                val documento =
                    snapshot.documents.first()

                val solicitud =
                    documento.toObject(
                        SolicitudAdopcion::class.java
                    )

                Result.success(
                    solicitud?.copy(
                        id = documento.id
                    )
                )
            }

        } catch (e: Exception) {

            Result.failure(
                Exception(
                    e.message
                        ?: "No fue posible consultar la solicitud."
                )
            )
        }
    }
}