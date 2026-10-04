package repository

import com.dogfellas.app.model.AnimalReportado
import com.dogfellas.app.model.EstadoAnimal
import com.dogfellas.app.model.HistorialEstadoAnimal
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class HistorialAnimalRepository {

    private val db = FirebaseFirestore.getInstance()

    private fun historialCollection(
        reporteId: String
    ) =
        db.collection("animales_reportados")
            .document(reporteId)
            .collection("historial_estados")

    /**
     * Obtiene el historial real del animal.
     */
    suspend fun obtenerHistorial(
        reporteId: String
    ): Result<List<HistorialEstadoAnimal>> {

        return try {

            val snapshot = historialCollection(reporteId)
                .orderBy("fecha")
                .get()
                .await()

            val historial =
                snapshot.documents.mapNotNull { documento ->

                    documento
                        .toObject(HistorialEstadoAnimal::class.java)
                        ?.copy(
                            id = documento.id
                        )
                }

            Result.success(historial)

        } catch (e: Exception) {

            Result.failure(
                Exception(
                    e.message
                        ?: "No fue posible cargar el historial."
                )
            )
        }
    }

    /**
     * Registra un nuevo estado en el historial.
     */
    suspend fun registrarCambioEstado(
        reporteId: String,
        nuevoEstado: String,
        usuarioId: String,
        observacion: String
    ): Result<Unit> {

        return try {

            val ahora = System.currentTimeMillis()

            val historial = hashMapOf(
                "estado" to nuevoEstado,
                "fecha" to ahora,
                "usuarioId" to usuarioId,
                "observacion" to observacion
            )

            historialCollection(reporteId)
                .add(historial)
                .await()

            db.collection("animales_reportados")
                .document(reporteId)
                .update(
                    "estado",
                    nuevoEstado
                )
                .await()

            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(
                Exception(
                    e.message
                        ?: "No fue posible actualizar el estado."
                )
            )
        }
    }

    /**
     * Crea el primer registro del historial si el animal
     * todavía no tiene historial.
     */
    suspend fun crearEstadoInicialSiEsNecesario(
        animal: AnimalReportado
    ): Result<Unit> {

        return try {

            val historialExistente =
                historialCollection(animal.id)
                    .limit(1)
                    .get()
                    .await()

            if (!historialExistente.isEmpty) {
                return Result.success(Unit)
            }

            val usuarioId =
                animal.usuarioReporta

            val estado =
                if (animal.estado.isBlank()) {
                    EstadoAnimal.REPORTADO.valor
                } else {
                    animal.estado
                }

            val registroInicial = hashMapOf(
                "estado" to estado,
                "fecha" to if (animal.fechaReporte > 0L) {
                    animal.fechaReporte
                } else {
                    System.currentTimeMillis()
                },
                "usuarioId" to usuarioId,
                "observacion" to "Reporte inicial del animal."
            )

            historialCollection(animal.id)
                .add(registroInicial)
                .await()

            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(
                Exception(
                    e.message
                        ?: "No fue posible crear el historial inicial."
                )
            )
        }
    }
}