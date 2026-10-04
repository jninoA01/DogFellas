package repository

import com.dogfellas.app.model.ComentarioAnimal
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ComentariosAnimalRepository {

    private val db = FirebaseFirestore.getInstance()

    private fun comentariosCollection(reporteId: String) =
        db.collection("animales_reportados")
            .document(reporteId)
            .collection("comentarios")

    suspend fun obtenerComentarios(
        reporteId: String
    ): Result<List<ComentarioAnimal>> {

        return try {

            val snapshot = comentariosCollection(reporteId)
                .orderBy("fecha")
                .get()
                .await()

            val comentarios = snapshot.documents.mapNotNull { documento ->

                documento
                    .toObject(ComentarioAnimal::class.java)
                    ?.copy(id = documento.id)
            }

            val comentariosConNombre = comentarios.map { comentario ->

                val nombre = obtenerNombreUsuario(
                    comentario.usuarioId
                )

                comentario.copy(
                    nombreUsuario = nombre
                )
            }

            Result.success(comentariosConNombre)

        } catch (e: Exception) {

            Result.failure(
                Exception(
                    e.message
                        ?: "No fue posible cargar los comentarios."
                )
            )
        }
    }

    private suspend fun obtenerNombreUsuario(
        usuarioId: String
    ): String {

        if (usuarioId.isBlank()) {
            return "Usuario"
        }

        return try {

            val documento = db.collection("usuarios")
                .document(usuarioId)
                .get()
                .await()

            val nombre = documento.getString("nombre")

            if (!nombre.isNullOrBlank()) {
                nombre
            } else {
                "Usuario"
            }

        } catch (e: Exception) {

            "Usuario"
        }
    }

    suspend fun agregarComentario(
        reporteId: String,
        usuarioId: String,
        comentario: String
    ): Result<Unit> {

        return try {

            val nuevoComentario = hashMapOf(
                "usuarioId" to usuarioId,
                "comentario" to comentario,
                "fecha" to System.currentTimeMillis()
            )

            comentariosCollection(reporteId)
                .add(nuevoComentario)
                .await()

            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(
                Exception(
                    e.message
                        ?: "No fue posible guardar el comentario."
                )
            )
        }
    }
}