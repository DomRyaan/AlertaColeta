package com.exemplo.alertacoleta.dataLayer.model

import android.widget.EditText
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
import kotlinx.coroutines.suspendCancellableCoroutine


class Repository(
    internal var dataStoreManager: AppDataStoreManager,
    private val localizacaoRepository: LocalizacaoRepository
) : Observer<LocalizacaoData> {
    private val retrofit = RetrofitClient.apiService


    // Escopo de aplicação que viverá enquanto o app estiver rodando
    val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        localizacaoRepository.locationResult.observeForever(this)
    }

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
                dataStoreManager?.salvarDadosLocalizacao(cidade, bairro)
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

    /**
     * Inicia a busca pela localização
     */
    fun fetchLocationRepository(localizacaoGPSManager: LocalizacaoGPS) {
        repositoryLocationScope.launch {
            try {
                val resultado: String? = localizacaoGPSManager.obterLocalizacao()

                if (resultado.isNullOrBlank() || resultado.contains("não conseguiu encontrar") || resultado.contains(
                        "Falha"
                    )
                ) {
                    _locationResult.value = LocalizacaoData(
                        isSuccess = false,
                        null,
                        null,
                        error = resultado ?: "Erro desconhecido"
                    )
                    throw Exception(resultado)
                } else {
                    val areaLocal = resultado.split(",").map { it.trim() }
                    LogsDebug.log("ENDEREÇO PEGO: ${areaLocal}")
                    _locationResult.postValue(
                        LocalizacaoData (
                            isSuccess = true,
                            cidade = areaLocal.getOrNull(0),
                            bairro = areaLocal.getOrNull(1)
                        )
                    )
                }
            } catch (e: Exception) {
                LogsDebug.log("Erro: ${e.message}")
            }
        }
    }

    /*
   Pega os Dados do formulario
    */
    fun processarFormulario(cidade: EditText, bairro: EditText): String{
        val form = FormularioEndereco(cidade, bairro)

        if (form.nuloOuVazio()){
            return "Preencha os campos corretamente"
        }

        _locationResult.value = form.montandoDados()
        return "Salvo com Sucesso!"
    }

    fun onCleared(){
        localizacaoRepository.locationResult.removeObserver(this)
        repositoryScope.cancel()
    }
}