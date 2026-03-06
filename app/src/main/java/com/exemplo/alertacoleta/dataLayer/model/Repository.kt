package com.exemplo.alertacoleta.dataLayer.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import com.exemplo.alertacoleta.global.LogsDebug
import com.exemplo.alertacoleta.dataLayer.dados.AppDataStoreManager
import com.exemplo.alertacoleta.dataLayer.dados.ColetaJSONAPI
import com.exemplo.alertacoleta.dataLayer.dados.DiasSemanas
import com.exemplo.alertacoleta.dataLayer.dados.LocalizacaoData
import com.exemplo.alertacoleta.dataLayer.model.api.RetrofitClient
import com.exemplo.alertacoleta.dataLayer.model.localizacao.FormularioEndereco
import com.exemplo.alertacoleta.dataLayer.model.localizacao.LocalizacaoGPS
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

    fun processSucess(resultado: ColetaJSONAPI?): String {
        if (resultado.toString().isNullOrBlank()) {
            return "Respota vazia do servidor"
        }

        resultado?.let {
            repositoryScope.launch {
                saveColeta(it.dias, it.horario)
            }
        }

        return "Requeste para a API foi um sucesso"
    }

    suspend fun saveColeta(dia: String, horario: String){
        dataStoreManager.salvarDadosColeta(
            dia,
            horario
        )
    }

    fun salvarLocalizacao(cidade: String, bairro: String) {
        repositoryScope.launch {
            if (cidade.isNotEmpty() && bairro.isNotEmpty()) {
                dataStoreManager.salvarDadosLocalizacao(cidade, bairro)
            }
        }
    }

    override fun onChanged(value: LocalizacaoData) {
        LogsDebug.log("Mudança no Data")

        if (value.isSuccess) {
            LogsDebug.log("Respondeu com sucesso")
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
                LogsDebug.log("Não foi possível salvar. Cidade ou bairro inválidos. Cidade='${value.cidade}', Bairro='${value.bairro}'")
            }
        }
    }

    suspend fun requestColeta(
        cidade: String,
        bairro: String
    ): String {
        return try {
                val response = retrofit.getColeta(cidade, bairro)

                when (response.code()) {
                    200 -> {
                        val resultado = response.body()

                        processSucess(resultado)
                    }
                    404 -> {
                        "Essa região não é atendida"
                    }
                    500 -> {
                       "Houve um error no Servidor"
                    }
                    else -> {
                        "Erro desconhecido"
                    }
                }
        } catch (e: Exception) {
             return "Falha na conexão: ${e.message}"
        }
    }

    fun onCleared(){
        repositoryScope.cancel()
    }
}