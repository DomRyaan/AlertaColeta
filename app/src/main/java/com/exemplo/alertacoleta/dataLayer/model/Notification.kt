package com.exemplo.alertacoleta.dataLayer.model

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.exemplo.alertacoleta.R

const val CHANNEL_ID = "alerta_coleta_channel"
const val NOTIFICATION_ID = 123


object NotificationHelper {

    private val NOTIFICATION_PERMISSION_REQUEST_CODE = 101
    /**
     * Cria o canal de notificação
     * @param context O contexto necessário para acessar os serviços do sistema
     */
    fun createCanalNotification(context: Context) {
        // Só é necessário para Android Oreo (API 26) e superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.name_chanel)
            val descriptionText = context.getString(R.string.chanel_descrip)
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

        fun with(context: Context): NotificationBuilder {
            return NotificationBuilder(context)
        }

}


/**
 * Classe que gerencia a construção e exibição da notificação.
 */
class NotificationBuilder(private val context: Context){
    private val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.bell_svgrepo_com)
        .setContentTitle("Coleta de Lixo")
        .setContentText("Hoje tem coleta!!")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)

    @SuppressLint("MissingPermission")
    fun show(id: Int = NOTIFICATION_ID){
        with(NotificationManagerCompat.from(context)) {
            notify(id, builder.build())
        }
    }

}