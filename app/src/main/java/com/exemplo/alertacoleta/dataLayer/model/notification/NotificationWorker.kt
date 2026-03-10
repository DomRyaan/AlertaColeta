package com.exemplo.alertacoleta.dataLayer.model.notification

import android.content.Context
import android.icu.util.TimeUnit
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.exemplo.alertacoleta.global.LogsDebug
import com.exemplo.alertacoleta.dataLayer.dados.AppDataStoreManager
import com.exemplo.alertacoleta.dataLayer.model.formatter.DataFormatter
import com.exemplo.alertacoleta.dataLayer.model.formatter.TempoFormatter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.CancellationException
import java.util.Calendar

class NotificationWorker(
    private val appContext: Context,
    private var workerParameters: WorkerParameters
) : CoroutineWorker(appContext, workerParameters) {

    private val dataStoreManager = AppDataStoreManager.Companion.getInstance(appContext)

    override suspend fun doWork(): Result {
        try {
            val diasDeColetaString = getDiasColetas()
            val horarioString = getHorario()


            if (diasDeColetaString.isNullOrBlank() || horarioString.isNullOrBlank()){
                return Result.success()
            }

            val diasDeColeta = formatarLista(diasDeColetaString)

            val hoje = TempoFormatter().obterDiaDaSemanaFormatado().uppercase()

            if (DataFormatter.temColetaHoje(diasDeColeta)) {
                dispararNotificacao(hoje, diasDeColeta)

                val hora = DataFormatter.getHora(horarioString)
                val minuto = DataFormatter.getMin(horarioString)

                val atrasoParaAmanha =  calcularAtrasoParaProximoAlvo(hora, minuto)

                val proximaTarefa = OneTimeWorkRequestBuilder<NotificationWorker>()
                    .setInitialDelay(atrasoParaAmanha, java.util.concurrent.TimeUnit.MILLISECONDS)
                    .build()

                WorkManager.getInstance(appContext).enqueueUniqueWork(
                    "lembreteColetaDiaria",
                    ExistingWorkPolicy.REPLACE,
                    proximaTarefa
                    )
                }

            return Result.success()
        } catch (e: CancellationException) {
            LogsDebug.log("O worker foi cancelado pelo sistema.")
            throw e
        } catch (e: Exception) {
            LogsDebug.log("Houve um erro ao executar o worker: ${e.message}")
            return Result.failure()
        }
    }

    fun dispararNotificacao(hoje: String, diasDeColeta: List<String>) {
        if (diasDeColeta.contains(hoje)) {
            val chanelNoti = NotificationHelper(appContext)
            val notificationHelper = chanelNoti.NotificationBuilder("Coleta de Lixo", "Haverá coleta hoje! Lembre-se de por o lixo para fora")
            notificationHelper.show()
        }
    }

    private suspend fun getDiasColetas(): String? {
        return withTimeoutOrNull(900) {
            dataStoreManager.diasColetaFlow
                .filterNotNull()
                .filter { it.isNotBlank() }
                .first()
        }
    }

    private suspend fun getHorario(): String? {
        return withTimeoutOrNull(900) {
            dataStoreManager.horarioColetaFlow
                .filterNotNull()
                .filter { it.isNotBlank() }
                .first()
        }
    }

    fun formatarLista(diasDeColetaString: String): List<String>{
        return diasDeColetaString.replace("/", ",").split(",").map { it.trim().uppercase() }
    }

    private fun calcularAtrasoParaProximoAlvo(hora: Int, minuto: Int): Long {
        val agora = Calendar.getInstance()
        val alvoHorario = agora.apply {
            set(Calendar.HOUR_OF_DAY, hora)
            set(Calendar.MINUTE, (minuto - 10))
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (alvoHorario.before(agora)) {
            alvoHorario.add(Calendar.DAY_OF_WEEK, 1)
        }

        return alvoHorario.timeInMillis - agora.timeInMillis
    }
}