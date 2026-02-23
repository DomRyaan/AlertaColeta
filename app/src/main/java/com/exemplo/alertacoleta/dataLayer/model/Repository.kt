package com.exemplo.alertacoleta.dataLayer.model

import androidx.lifecycle.Observer
import com.exemplo.alertacoleta.LogsDebug
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
        LogsDebug.log(value.toString())
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
            val response = retrofit.getColeta(cidade, bairro)

            if (response.isSuccessful) {
               val resultado = response.body()
                dataStoreManager?.salvarDadosColeta(resultado!!.dias, resultado!!.horario)
                return "Request para a API foi um sucesso"
            } else {
                return "Erro no requestColeta: " + response.code()
            }
    }


    fun onCleared(){
        repositoryScope.cancel()
    }
}