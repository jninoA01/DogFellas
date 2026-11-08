
package com.dogfellas.app.service

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

object ProgramadorRecordatoriosMedicos {

    private const val PREFIJO = "recordatorio_medico"

    /**
     * Programa dos notificaciones:
     * 1. Un día antes a las 9:00 a. m.
     * 2. El mismo día a las 8:00 a. m.
     */
    fun programar(
        context: Context,
        mascotaId: String,
        categoria: String,
        registroId: String,
        titulo: String,
        descripcion: String,
        fecha: Long
    ) {

        if (
            mascotaId.isBlank() ||
            categoria.isBlank() ||
            registroId.isBlank()
        ) return

        val workManager = WorkManager.getInstance(context)

        val identificador =
            "${mascotaId}_${categoria}_${registroId}"

        // Un día antes: 9:00 a. m.
        programarAviso(
            workManager = workManager,
            mascotaId = mascotaId,
            identificador = identificador,
            sufijo = "anterior",
            titulo = titulo,
            descripcion = "Mañana: $descripcion",
            fecha = fecha,
            diasAntes = 1,
            hora = 9
        )

        // El mismo día: 8:00 a. m.
        programarAviso(
            workManager = workManager,
            mascotaId = mascotaId,
            identificador = identificador,
            sufijo = "mismo_dia",
            titulo = titulo,
            descripcion = descripcion,
            fecha = fecha,
            diasAntes = 0,
            hora = 8
        )
    }

    /**
     * Programa individualmente cada aviso.
     */
    private fun programarAviso(
        workManager: WorkManager,
        mascotaId: String,
        identificador: String,
        sufijo: String,
        titulo: String,
        descripcion: String,
        fecha: Long,
        diasAntes: Int,
        hora: Int
    ) {

        val calendario = Calendar.getInstance().apply {

            timeInMillis = fecha

            set(Calendar.HOUR_OF_DAY, hora)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            add(Calendar.DAY_OF_MONTH, -diasAntes)
        }

        val demora = calendario.timeInMillis -
            System.currentTimeMillis()

        val nombreTrabajo =
            "${PREFIJO}_${identificador}_$sufijo"

        // Si la fecha del aviso ya pasó, cancelar ese trabajo.
        if (demora <= 0) {
            workManager.cancelUniqueWork(nombreTrabajo)
            return
        }

        // Información que recibirá el Worker.
        val datos = Data.Builder()
            .putString("titulo", titulo)
            .putString("descripcion", descripcion)
            .putInt(
                "notificationId",
                nombreTrabajo.hashCode() and 0x7FFFFFFF
            )
            .build()

        // Crear solicitud de notificación.
        val solicitud =
            OneTimeWorkRequestBuilder<NotificacionMedicaWorker>()
                .setInitialDelay(
                    demora,
                    TimeUnit.MILLISECONDS
                )
                .setInputData(datos)
                .addTag(
                    "${PREFIJO}_mascota_$mascotaId"
                )
                .build()

        // Evitar trabajos duplicados.
        workManager.enqueueUniqueWork(
            nombreTrabajo,
            ExistingWorkPolicy.REPLACE,
            solicitud
        )
    }

    /**
     * Cancela los dos avisos de un registro específico.
     */
    fun cancelar(
        context: Context,
        mascotaId: String,
        categoria: String,
        registroId: String
    ) {

        if (
            mascotaId.isBlank() ||
            categoria.isBlank() ||
            registroId.isBlank()
        ) return

        val identificador =
            "${mascotaId}_${categoria}_${registroId}"

        val workManager = WorkManager.getInstance(context)

        workManager.cancelUniqueWork(
            "${PREFIJO}_${identificador}_anterior"
        )

        workManager.cancelUniqueWork(
            "${PREFIJO}_${identificador}_mismo_dia"
        )
    }

    /**
     * Cancela todas las notificaciones de una mascota.
     */
    fun cancelarTodosDeMascota(
        context: Context,
        mascotaId: String
    ) {

        if (mascotaId.isBlank()) return

        WorkManager.getInstance(context)
            .cancelAllWorkByTag(
                "${PREFIJO}_mascota_$mascotaId"
            )
    }
}
