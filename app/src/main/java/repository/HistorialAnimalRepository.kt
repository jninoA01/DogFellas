package repository

import com.dogfellas.app.data.api.ApiClient
import com.dogfellas.app.data.api.CrearHistorialEstadoRequest
import com.dogfellas.app.model.AnimalReportado
import com.dogfellas.app.model.EstadoAnimal
import com.dogfellas.app.model.HistorialEstadoAnimal
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import java.time.Instant
import java.time.OffsetDateTime

class HistorialAnimalRepository {

    private val api = ApiClient.animalReportadoApi
    private val auth = FirebaseAuth.getInstance()

    /**
     * Obtiene el token de Firebase Authentication.
     */
    private suspend fun obtenerToken(): String {
        val usuario = auth.currentUser
            ?: throw Exception("Debes iniciar sesión.")

        val resultado = usuario.getIdToken(false).await()

        return resultado.token
            ?: throw Exception("No fue posible obtener el token de autenticación.")
    }

    /**
     * Convierte la fecha ISO del backend a milisegundos.
     */
    private fun convertirFecha(fecha: String): Long {
        return try {
            Instant.parse(fecha).toEpochMilli()
        } catch (_: Exception) {
            try {
                OffsetDateTime.parse(fecha).toInstant().toEpochMilli()
            } catch (_: Exception) {
                System.currentTimeMillis()
            }
        }
    }

    /**
     * Consulta el historial almacenado en PostgreSQL.
     */
    suspend fun obtenerHistorial(
        reporteId: String
    ): Result<List<HistorialEstadoAnimal>> {
        return try {
            val animalId = reporteId.toIntOrNull()
                ?: throw Exception("El identificador del animal no es válido.")

            val respuesta = api.obtenerHistorial(animalId)

            if (!respuesta.isSuccessful) {
                throw Exception(
                    respuesta.errorBody()?.string()
                        ?: "No fue posible consultar el historial."
                )
            }

            val historial = respuesta.body()
                ?.map { registro ->
                    HistorialEstadoAnimal(
                        id = registro.id.toString(),
                        estado = registro.estado,
                        fecha = convertirFecha(registro.fecha),
                        usuarioId = registro.usuario_id?.toString() ?: "",
                        observacion = registro.observacion
                    )
                }
                ?: emptyList()

            Result.success(historial)
        } catch (e: Exception) {
            Result.failure(
                Exception(
                    e.message ?: "No fue posible cargar el historial."
                )
            )
        }
    }

    /**
     * Actualiza el estado y registra el historial en PostgreSQL.
     */
    suspend fun registrarCambioEstado(
        reporteId: String,
        nuevoEstado: String,
        usuarioId: String,
        observacion: String
    ): Result<Unit> {
        return try {
            val animalId = reporteId.toIntOrNull()
                ?: throw Exception("El identificador del animal no es válido.")

            val token = obtenerToken()

            val respuesta = api.registrarCambioEstado(
                animalId = animalId,
                authorization = "Bearer $token",
                datos = CrearHistorialEstadoRequest(
                    estado = nuevoEstado,
                    observacion = observacion
                )
            )

            if (!respuesta.isSuccessful) {
                throw Exception(
                    respuesta.errorBody()?.string()
                        ?: "No fue posible actualizar el estado."
                )
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(
                Exception(
                    e.message ?: "No fue posible actualizar el estado."
                )
            )
        }
    }

    /**
     * Crea el registro inicial en PostgreSQL si no existe historial.
     */
    suspend fun crearEstadoInicialSiEsNecesario(
        animal: AnimalReportado
    ): Result<Unit> {
        return try {
            val animalId = animal.id.toIntOrNull()
                ?: throw Exception("El identificador del animal no es válido.")

            val respuesta = api.obtenerHistorial(animalId)

            if (!respuesta.isSuccessful) {
                throw Exception(
                    respuesta.errorBody()?.string()
                        ?: "No fue posible consultar el historial inicial."
                )
            }

            val historial = respuesta.body() ?: emptyList()

            if (historial.isNotEmpty()) {
                return Result.success(Unit)
            }

            val estadoInicial = animal.estado.ifBlank {
                EstadoAnimal.REPORTADO.valor
            }

            registrarCambioEstado(
                reporteId = animal.id,
                nuevoEstado = estadoInicial,
                usuarioId = animal.usuarioReporta,
                observacion = "Reporte inicial del animal."
            )
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
