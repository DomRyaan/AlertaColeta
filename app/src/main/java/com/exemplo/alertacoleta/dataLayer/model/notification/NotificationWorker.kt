package com.exemplo.alertacoleta.dataLayer.model.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.exemplo.alertacoleta.global.LogsDebug
import com.exemplo.alertacoleta.dataLayer.dados.AppDataStoreManager
import com.exemplo.alertacoleta.dataLayer.model.formatter.TempoFormatter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.coroutines.flow.first

class NotificationWorker(
    private val appContext: Context,
    private val workerParameters: WorkerParameters
) : CoroutineWorker(appContext, workerParameters) {

    private val dataStoreManager = AppDataStoreManager.Companion.getInstance(appContext)

    override suspend fun doWork(): Result {
        try {
            val diasDeColetaString = withTimeoutOrNull(5000) {
                                        dataStoreManager.diasColetaFlow
                                            .filterNotNull()
                                            .filter { it.isNotBlank() }
                                            .first()
                                    }

            if (diasDeColetaString.isNullOrBlank()){
                LogsDebug.log("O dias de coleta está retornando valores nulo")
                return Result.success()
            }
            LogsDebug.log("Dias de coleta retonando: $diasDeColetaString")
            val diasDeColeta = diasDeColetaString.replace("/", ",").split(",").map { it.trim() }

            val hoje = TempoFormatter().obterDiaDaSemanaFormatado().uppercase()

            if (diasDeColeta.contains(hoje)) {
                LogsDebug.log("Hoje é dia de coleta! Disparando notificação")
                NotificationHelper.with(appContext).show()
            }

            return Result.success()
        } catch (e: Exception) {
            LogsDebug.log("Houve um erro ao executar o worker: ${e.message}")
            return Result.failure()
        }
    }

}