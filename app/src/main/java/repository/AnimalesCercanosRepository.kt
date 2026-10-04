package repository

import android.util.Log
import com.dogfellas.app.model.AnimalReportado
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AnimalesCercanosRepository {

    private val db =
        FirebaseFirestore.getInstance()

    private val collection =
        db.collection("animales_reportados")

    suspend fun obtenerReportes():
            Result<List<AnimalReportado>> {

        return try {

            val snapshot =
                collection
                    .get()
                    .await()

            Log.d(
                "AnimalesCercanos",
                "Documentos encontrados: ${snapshot.documents.size}"
            )

            val reportes =
                snapshot.documents.mapNotNull { documento ->

                    try {

                        Log.d(
                            "AnimalesCercanos",
                            "Leyendo documento: ${documento.id}"
                        )

                        Log.d(
                            "AnimalesCercanos",
                            "Datos: ${documento.data}"
                        )

                        val animal =
                            documento.toObject(
                                AnimalReportado::class.java
                            )

                        animal?.copy(
                            id = documento.id
                        )

                    } catch (e: Exception) {

                        Log.e(
                            "AnimalesCercanos",
                            "Error leyendo ${documento.id}",
                            e
                        )

                        null
                    }
                }

            Log.d(
                "AnimalesCercanos",
                "Reportes convertidos: ${reportes.size}"
            )

            Result.success(reportes)

        } catch (e: Exception) {

            Log.e(
                "AnimalesCercanos",
                "Error consultando Firestore",
                e
            )

            Result.failure(
                Exception(
                    e.message
                        ?: "No fue posible cargar los reportes."
                )
            )
        }
    }
}