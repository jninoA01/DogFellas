package repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class HistorialMedicoMascotaRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Obtener la colección médica de una mascota
    private fun obtenerColeccion(
        mascotaId: String,
        tipoRegistro: String
    ) = db.collection("usuarios")
        .document(auth.currentUser?.uid
            ?: throw Exception("No hay un usuario autenticado."))
        .collection("mascotas")
        .document(mascotaId)
        .collection(tipoRegistro)

    // Guardar un registro médico
    suspend fun guardarRegistro(
        mascotaId: String,
        tipoRegistro: String,
        datos: Any
    ): Result<String> {
        return try {
            require(mascotaId.isNotBlank()) {
                "El identificador de la mascota está vacío."
            }

            val documento = obtenerColeccion(
                mascotaId,
                tipoRegistro
            ).document()

            documento.set(datos).await()

            documento.set(
                mapOf("id" to documento.id),
                SetOptions.merge()
            ).await()

            Result.success(documento.id)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Consultar los registros médicos
    suspend fun <T : Any> obtenerRegistros(
        mascotaId: String,
        tipoRegistro: String,
        clase: Class<T>
    ): Result<List<T>> {
        return try {
            val documentos = obtenerColeccion(
                mascotaId,
                tipoRegistro
            ).get().await()

            val registros = documentos.documents.mapNotNull {
                it.toObject(clase)
            }

            Result.success(registros)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Actualizar un registro médico
    suspend fun actualizarRegistro(
        mascotaId: String,
        tipoRegistro: String,
        registroId: String,
        datos: Any
    ): Result<Unit> {
        return try {
            obtenerColeccion(mascotaId, tipoRegistro)
                .document(registroId)
                .set(datos, SetOptions.merge())
                .await()

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Eliminar un registro médico
    suspend fun eliminarRegistro(
        mascotaId: String,
        tipoRegistro: String,
        registroId: String
    ): Result<Unit> {
        return try {
            obtenerColeccion(mascotaId, tipoRegistro)
                .document(registroId)
                .delete()
                .await()

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
