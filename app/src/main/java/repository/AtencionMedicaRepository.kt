package com.dogfellas.app.repository

import com.dogfellas.app.data.api.ApiClient
import data.models.Emergencia
import data.models.Medicamento

class AtencionMedicaRepository {

    // Emergencias
    suspend fun obtenerEmergencias(): Result<List<Emergencia>> =
        runCatching {
            val respuesta =
                ApiClient.atencionMedicaApi.obtenerEmergencias()

            if (!respuesta.isSuccessful) {
                throw IllegalStateException(
                    "Error al obtener emergencias: HTTP ${respuesta.code()}"
                )
            }

            respuesta.body().orEmpty().map {
                Emergencia(
                    id = it.id,
                    titulo = it.titulo,
                    icono = it.icono,
                    nivel = it.nivel,
                    queHacer = it.queHacer,
                    cuandoIrAlVet = it.cuandoIrAlVet,
                    especie = it.especie,
                    sintomas = it.sintomas
                )
            }
        }

    suspend fun obtenerEmergenciasPorEspecie(
        especie: String
    ): Result<List<Emergencia>> =
        runCatching {
            val respuesta =
                ApiClient.atencionMedicaApi.obtenerEmergencias(especie)

            if (!respuesta.isSuccessful) {
                throw IllegalStateException(
                    "Error al filtrar emergencias: HTTP ${respuesta.code()}"
                )
            }

            respuesta.body().orEmpty().map {
                Emergencia(
                    id = it.id,
                    titulo = it.titulo,
                    icono = it.icono,
                    nivel = it.nivel,
                    queHacer = it.queHacer,
                    cuandoIrAlVet = it.cuandoIrAlVet,
                    especie = it.especie,
                    sintomas = it.sintomas
                )
            }
        }

    // Medicamentos
    suspend fun obtenerMedicamentos(): Result<List<Medicamento>> =
        runCatching {
            val respuesta =
                ApiClient.atencionMedicaApi.obtenerMedicamentos()

            if (!respuesta.isSuccessful) {
                throw IllegalStateException(
                    "Error al obtener medicamentos: HTTP ${respuesta.code()}"
                )
            }

            respuesta.body().orEmpty().map {
                Medicamento(
                    id = it.id,
                    nombre = it.nombre,
                    descripcion = it.descripcion,
                    especie = it.especie,
                    dosis = it.dosis,
                    precio = it.precio,
                    busqueda = it.busqueda,
                    icono = it.icono,
                    categoria = it.categoria
                )
            }
        }

    suspend fun obtenerMedicamentosPorEspecie(
        especie: String
    ): Result<List<Medicamento>> =
        runCatching {
            val respuesta =
                ApiClient.atencionMedicaApi.obtenerMedicamentos(especie)

            if (!respuesta.isSuccessful) {
                throw IllegalStateException(
                    "Error al filtrar medicamentos: HTTP ${respuesta.code()}"
                )
            }

            respuesta.body().orEmpty().map {
                Medicamento(
                    id = it.id,
                    nombre = it.nombre,
                    descripcion = it.descripcion,
                    especie = it.especie,
                    dosis = it.dosis,
                    precio = it.precio,
                    busqueda = it.busqueda,
                    icono = it.icono,
                    categoria = it.categoria
                )
            }
        }
}
