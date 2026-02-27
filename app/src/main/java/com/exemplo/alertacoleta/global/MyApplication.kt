package com.exemplo.alertacoleta.global

import android.app.Application
import androidx.lifecycle.asLiveData
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.exemplo.alertacoleta.dataLayer.dados.AppDataStoreManager
import com.exemplo.alertacoleta.dataLayer.model.Repository
import com.exemplo.alertacoleta.dataLayer.model.notification.NotificationHelper
import com.exemplo.alertacoleta.dataLayer.model.notification.NotificationWorker
import java.time.Duration
import java.util.Calendar
import com.exemplo.alertacoleta.dataLayer.model.formatter.DataFormatter

class MyApplication : Application() {
    private val dataStoreManager by lazy {
        AppDataStoreManager.Companion.getInstance(this)
    }

    val repository by lazy {
        Repository(dataStoreManager)
    }

    val horario by lazy {
        repository.dataStoreManager.horarioColetaFlow.asLiveData()
    }


    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createCanalNotification(applicationContext)
        horario.observeForever { horarioSalvo ->
            if (!horarioSalvo.isNullOrBlank()) {
                agendarNotificacaoDiaria(horarioSalvo)
            }

        }
    }

    private fun agendarNotificacaoDiaria(horarioString: String) {
        // Previnir erro, quero no minimo hora
        if (DataFormatter.naoTemHorario(horarioString)) return

        val hora = DataFormatter.getHora(horarioString)
        val minuto = DataFormatter.getMin(horarioString)

        val agora = Calendar.getInstance()

        val horarioAlvo = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hora)
            set(Calendar.MINUTE, minuto)
            set(Calendar.SECOND, 0)
        }

        val atrasoInicial = horarioAlvo.timeInMillis - agora.timeInMillis

        // Criação da requisição periodica para rodar a cada 24 horas
        val repeticao = 24
        val workRequest: PeriodicWorkRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
            Duration.ofHours(repeticao.toLong())
        )
            .setInitialDelay(Duration.ofMillis(atrasoInicial))
            .build()

        LogsDebug.log(workRequest.id.toString())

        WorkManager.Companion.getInstance(applicationContext).enqueueUniquePeriodicWork(
        "lembreteColetaDiaria",
        ExistingPeriodicWorkPolicy.REPLACE,
        workRequest
    )
    }

}