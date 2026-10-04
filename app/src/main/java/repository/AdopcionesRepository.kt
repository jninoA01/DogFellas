package repository

import android.util.Log
import com.dogfellas.app.model.AnimalReportado
import com.dogfellas.app.model.EstadoAnimal
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AdopcionesRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun obtenerAnimalesEnAdopcion():
            Result<List<AnimalReportado>> {

        return try {

            Log.d(
                "ADOPCIONES_REPO",
                "Consultando Firestore..."
            )

            val snapshot =
                db.collection("animales_reportados")
                    .whereEqualTo(
                        "estado",
                        EstadoAnimal.EN_ADOPCION.valor
                    )
                    .get()
                    .await()

            Log.d(
                "ADOPCIONES_REPO",
                "Documentos encontrados: ${snapshot.documents.size}"
            )

            snapshot.documents.forEach { documento ->

                Log.d(
                    "ADOPCIONES_REPO",
                    "Documento: ${documento.id}"
                )

                Log.d(
                    "ADOPCIONES_REPO",
                    "Datos: ${documento.data}"
                )
            }

            val animales =
                snapshot.documents.mapNotNull { documento ->

                    documento
                        .toObject(
                            AnimalReportado::class.java
                        )
                        ?.copy(
                            id = documento.id
                        )
                }

            Log.d(
                "ADOPCIONES_REPO",
                "Animales convertidos: ${animales.size}"
            )

            Result.success(animales)

        } catch (e: Exception) {

            Log.e(
                "ADOPCIONES_REPO",
                "Error consultando Firestore",
                e
            )

            Result.failure(
                Exception(
                    e.message
                        ?: "No fue posible cargar los animales en adopción."
                )
            )
        }
    }
}