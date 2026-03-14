package com.exemplo.alertacoleta.global

import android.app.Application
import java.util.concurrent.TimeUnit
import androidx.lifecycle.asLiveData
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.exemplo.alertacoleta.dataLayer.dados.AppDataStoreManager
import com.exemplo.alertacoleta.dataLayer.model.Repository
import com.exemplo.alertacoleta.dataLayer.model.notification.NotificationHelper
import com.exemplo.alertacoleta.dataLayer.model.notification.NotificationWorker
import java.util.Calendar
import com.exemplo.alertacoleta.dataLayer.model.formatter.DataFormatter
import androidx.work.Constraints
import androidx.work.NetworkType

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

        val channel = NotificationHelper(applicationContext)
        channel.createCanalNotification()

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

        val horarioAlvo = makeCalendarInstance(hora, minuto)

        if (horarioAlvo.before(agora)) {
            horarioAlvo.add(Calendar.DAY_OF_WEEK, 1)
        }

        val constaintBuilder = setConstraintBuilderWork()
        val constraints = constaintBuilder.build()


        val atrasoInicial: Long = horarioAlvo.timeInMillis - agora.timeInMillis

        val workRequest = setPeriodicWorkRequest(atrasoInicial, constraints)

        WorkManager.Companion.getInstance(applicationContext).enqueueUniquePeriodicWork(
        "lembreteColetaDiaria",
        ExistingPeriodicWorkPolicy.KEEP,
        workRequest
    )
    }

    fun setConstraintBuilderWork(): Constraints.Builder{
        return Constraints.Builder()
            .setRequiresBatteryNotLow(false)
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresCharging(false)
            .setRequiresDeviceIdle(false)
    }

    fun makeCalendarInstance(hora: Int, minuto: Int): Calendar {
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hora)
            set(Calendar.MINUTE, minuto)
            set(Calendar.SECOND, 0)
        }
    }

    fun setPeriodicWorkRequest(atrasoInicial: Long, constraints: Constraints): PeriodicWorkRequest {
        return PeriodicWorkRequest.Builder(
            NotificationWorker::class.java,
            24L,
            TimeUnit.HOURS
            )
            .setInitialDelay(atrasoInicial, TimeUnit.MILLISECONDS)
            .setConstraints(constraints)
            .build()
    }
}