
package com.dogfellas.app.service

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.dogfellas.app.R

class NotificacionMedicaWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {

        // Obtener información de la notificación
        val titulo = inputData.getString("titulo")
            ?: "Recordatorio médico"

        val descripcion = inputData.getString("descripcion")
            ?: "Tienes un evento médico pendiente para tu mascota."

        val notificationId = inputData.getInt(
            "notificationId",
            id.hashCode()
        )

        // Verificar permiso de notificaciones en Android 13 o superior
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return Result.failure()
        }

        return try {

            // Crear canal de notificaciones
            NotificacionesMedicas.crearCanal(applicationContext)

            // Construir notificación
            val notification = NotificationCompat.Builder(
                applicationContext,
                NotificacionesMedicas.CHANNEL_ID
            )
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(titulo)
                .setContentText(descripcion)
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(descripcion)
                )
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build()

            // Mostrar notificación
            val manager = applicationContext.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

            manager.notify(notificationId, notification)

            Result.success()

        } catch (e: Exception) {
            Result.failure()
        }
    }
}
