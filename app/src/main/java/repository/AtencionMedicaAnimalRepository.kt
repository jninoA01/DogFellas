package com.dogfellas.app.repository

import com.dogfellas.app.model.CitaVeterinariaAnimal
import com.dogfellas.app.model.DesparasitacionAnimal
import com.dogfellas.app.model.EmergenciaAnimal
import com.dogfellas.app.model.HistorialMedicoAnimal
import com.dogfellas.app.model.MedicamentoAnimal
import com.dogfellas.app.model.VacunacionAnimal
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AtencionMedicaAnimalRepository {

    private val db = FirebaseFirestore.getInstance()

    private fun animalCollection(reporteId: String) =
        db.collection("animales_reportados")
            .document(reporteId)

    // ============================================================
    // HISTORIAL MÉDICO
    // ============================================================

    suspend fun obtenerHistorialMedico(
        reporteId: String
    ): Result<List<HistorialMedicoAnimal>> {

        return try {

            val snapshot = animalCollection(reporteId)
                .collection("historial_medico")
                .orderBy("fecha")
                .get()
                .await()

            val lista = snapshot.documents.mapNotNull { documento ->

                documento
                    .toObject(HistorialMedicoAnimal::class.java)
                    ?.copy(
                        id = documento.id,
                        reporteId = reporteId
                    )
            }

            Result.success(lista)

        } catch (e: Exception) {

            Result.failure(
                Exception(
                    e.message
                        ?: "No fue posible cargar el historial médico."
                )
            )
        }
    }

    suspend fun registrarHistorialMedico(
        historial: HistorialMedicoAnimal
    ): Result<Unit> {

        return try {

            val documento = animalCollection(
                historial.reporteId
            )
                .collection("historial_medico")
                .document()

            documento
                .set(
                    historial.copy(
                        id = documento.id
                    )
                )
                .await()

            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(
                Exception(
                    e.message
                        ?: "No fue posible registrar la atención médica."
                )
            )
        }
    }

    // ============================================================
    // MEDICAMENTOS
    // ============================================================

    suspend fun obtenerMedicamentosAnimal(
        reporteId: String
    ): Result<List<MedicamentoAnimal>> {

        return try {

            val snapshot = animalCollection(reporteId)
                .collection("medicamentos")
                .orderBy("fechaInicio")
                .get()
                .await()

            val lista = snapshot.documents.mapNotNull { documento ->

                documento
                    .toObject(MedicamentoAnimal::class.java)
                    ?.copy(
                        id = documento.id,
                        reporteId = reporteId
                    )
            }

            Result.success(lista)

        } catch (e: Exception) {

            Result.failure(
                Exception(
                    e.message
                        ?: "No fue posible cargar los medicamentos."
                )
            )
        }
    }

    suspend fun registrarMedicamentoAnimal(
        medicamento: MedicamentoAnimal
    ): Result<Unit> {

        return try {

            val documento = animalCollection(
                medicamento.reporteId
            )
                .collection("medicamentos")
                .document()

            documento
                .set(
                    medicamento.copy(
                        id = documento.id
                    )
                )
                .await()

            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(
                Exception(
                    e.message
                        ?: "No fue posible registrar el medicamento."
                )
            )
        }
    }

    // ============================================================
    // VACUNACIONES
    // ============================================================

    suspend fun obtenerVacunaciones(
        reporteId: String
    ): Result<List<VacunacionAnimal>> {

        return try {

            val snapshot = animalCollection(reporteId)
                .collection("vacunaciones")
                .orderBy("fecha")
                .get()
                .await()

            val lista = snapshot.documents.mapNotNull { documento ->

                documento
                    .toObject(VacunacionAnimal::class.java)
                    ?.copy(
                        id = documento.id,
                        reporteId = reporteId
                    )
            }

            Result.success(lista)

        } catch (e: Exception) {

            Result.failure(
                Exception(
                    e.message
                        ?: "No fue posible cargar las vacunaciones."
                )
            )
        }
    }

    suspend fun registrarVacunacion(
        vacunacion: VacunacionAnimal
    ): Result<Unit> {

        return try {

            val documento = animalCollection(
                vacunacion.reporteId
            )
                .collection("vacunaciones")
                .document()

            documento
                .set(
                    vacunacion.copy(
                        id = documento.id
                    )
                )
                .await()

            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(
                Exception(
                    e.message
                        ?: "No fue posible registrar la vacunación."
                )
            )
        }
    }

    // ============================================================
    // DESPARASITACIONES
    // ============================================================

    suspend fun obtenerDesparasitaciones(
        reporteId: String
    ): Result<List<DesparasitacionAnimal>> {

        return try {

            val snapshot = animalCollection(reporteId)
                .collection("desparasitaciones")
                .orderBy("fecha")
                .get()
                .await()

            val lista = snapshot.documents.mapNotNull { documento ->

                documento
                    .toObject(DesparasitacionAnimal::class.java)
                    ?.copy(
                        id = documento.id,
                        reporteId = reporteId
                    )
            }

            Result.success(lista)

        } catch (e: Exception) {

            Result.failure(
                Exception(
                    e.message
                        ?: "No fue posible cargar las desparasitaciones."
                )
            )
        }
    }

    suspend fun registrarDesparasitacion(
        desparasitacion: DesparasitacionAnimal
    ): Result<Unit> {

        return try {

            val documento = animalCollection(
                desparasitacion.reporteId
            )
                .collection("desparasitaciones")
                .document()

            documento
                .set(
                    desparasitacion.copy(
                        id = documento.id
                    )
                )
                .await()

            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(
                Exception(
                    e.message
                        ?: "No fue posible registrar la desparasitación."
                )
            )
        }
    }

    // ============================================================
    // CITAS VETERINARIAS
    // ============================================================

    suspend fun obtenerCitasVeterinarias(
        reporteId: String
    ): Result<List<CitaVeterinariaAnimal>> {

        return try {

            val snapshot = animalCollection(reporteId)
                .collection("citas_veterinarias")
                .orderBy("fecha")
                .get()
                .await()

            val lista = snapshot.documents.mapNotNull { documento ->

                documento
                    .toObject(CitaVeterinariaAnimal::class.java)
                    ?.copy(
                        id = documento.id,
                        reporteId = reporteId
                    )
            }

            Result.success(lista)

        } catch (e: Exception) {

            Result.failure(
                Exception(
                    e.message
                        ?: "No fue posible cargar las citas veterinarias."
                )
            )
        }
    }

    suspend fun registrarCitaVeterinaria(
        cita: CitaVeterinariaAnimal
    ): Result<Unit> {

        return try {

            val documento = animalCollection(
                cita.reporteId
            )
                .collection("citas_veterinarias")
                .document()

            documento
                .set(
                    cita.copy(
                        id = documento.id
                    )
                )
                .await()

            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(
                Exception(
                    e.message
                        ?: "No fue posible registrar la cita veterinaria."
                )
            )
        }
    }

    // ============================================================
    // EMERGENCIAS
    // ============================================================

    suspend fun obtenerEmergenciasAnimal(
        reporteId: String
    ): Result<List<EmergenciaAnimal>> {

        return try {

            val snapshot = animalCollection(reporteId)
                .collection("emergencias_atencion")
                .orderBy("fecha")
                .get()
                .await()

            val lista = snapshot.documents.mapNotNull { documento ->

                documento
                    .toObject(EmergenciaAnimal::class.java)
                    ?.copy(
                        id = documento.id,
                        reporteId = reporteId
                    )
            }

            Result.success(lista)

        } catch (e: Exception) {

            Result.failure(
                Exception(
                    e.message
                        ?: "No fue posible cargar las emergencias."
                )
            )
        }
    }

    suspend fun registrarEmergenciaAnimal(
        emergencia: EmergenciaAnimal
    ): Result<Unit> {

        return try {

            val documento = animalCollection(
                emergencia.reporteId
            )
                .collection("emergencias_atencion")
                .document()

            documento
                .set(
                    emergencia.copy(
                        id = documento.id
                    )
                )
                .await()

            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(
                Exception(
                    e.message
                        ?: "No fue posible registrar la emergencia."
                )
            )
        }
    }
}