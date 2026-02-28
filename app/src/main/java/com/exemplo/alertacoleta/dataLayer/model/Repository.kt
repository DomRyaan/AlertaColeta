package com.exemplo.alertacoleta.dataLayer.model

import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import com.exemplo.alertacoleta.global.LogsDebug
import com.exemplo.alertacoleta.dataLayer.dados.AppDataStoreManager
import com.exemplo.alertacoleta.dataLayer.dados.DiasSemanas
import com.exemplo.alertacoleta.dataLayer.dados.LocalizacaoData
import com.exemplo.alertacoleta.dataLayer.model.api.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch


class Repository(
    internal var dataStoreManager: AppDataStoreManager
) : Observer<LocalizacaoData> {
    private val retrofit = RetrofitClient.apiService


    // Escopo de aplicação que viverá enquanto o app estiver rodando
    val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun salvarLocalizacao(cidade: String, bairro: String) {
        repositoryScope.launch {
            if (cidade.isNotEmpty() && bairro.isNotEmpty()) {
                dataStoreManager?.salvarDadosLocalizacao(cidade, bairro)
            }
        }
    }

    override fun onChanged(value: LocalizacaoData) {
        if (value.isSuccess) {
            val cidadeNaoNula = value.cidade
            val bairroNaoNula = value.bairro

            if (!cidadeNaoNula.isNullOrEmpty() && !bairroNaoNula.isNullOrEmpty()){
                salvarLocalizacao(cidadeNaoNula, bairroNaoNula)
                repositoryScope.launch {
                    try {
                        requestColeta(cidadeNaoNula, bairroNaoNula)
                    } catch (e: Exception) {
                        LogsDebug.log("Não foi possivel fazer o requeste")
                }
                }
            } else {
                LogsDebug.log("Repository.onChanged: Não foi possível salvar. Cidade ou bairro inválidos. Cidade='${value.cidade}', Bairro='${value.bairro}'")
            }
        }
    }

    suspend fun requestColeta(cidade: String, bairro: String): String {
        return try {
            val response = retrofit.getColeta(cidade, bairro)

            if (response.isSuccessful) {
                val resultado = response.body()
                if (!resultado.toString().isNullOrBlank()) {
                    dataStoreManager.salvarDadosColeta(
                        resultado?.dias.toString(),
                        resultado?.horario.toString()
                    )
                    "Request para a API foi um sucesso"
                } else {
                    "Erro: Respota vazia do servidor"
                }
            } else {
                "Erro no requestColeta: " + response.code()
            }
        } catch (e: Exception) {
            "Falha na conexão: ${e.message}"
        }
    }
    fun onCleared(){
        repositoryScope.cancel()
    }
}