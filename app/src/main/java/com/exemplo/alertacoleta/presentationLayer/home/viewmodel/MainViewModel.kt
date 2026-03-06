package com.exemplo.alertacoleta.presentationLayer.home.viewmodel

import android.widget.EditText
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import com.exemplo.alertacoleta.dataLayer.dados.LocalizacaoData
import com.exemplo.alertacoleta.dataLayer.model.Repository
import com.exemplo.alertacoleta.dataLayer.model.formatter.DataFormatter
import com.exemplo.alertacoleta.dataLayer.model.ControllerLocation

class MainViewModel(private val repository: Repository) : ViewModel() {
    var cidade: LiveData<String?> = repository.dataStoreManager.cidadeFlow.asLiveData()
    var bairro: LiveData<String?> = repository.dataStoreManager.bairroFlow.asLiveData()

    var coletaDias: LiveData<String?> = repository.dataStoreManager.diasColetaFlow.asLiveData()
    var horario: LiveData<String?> = repository.dataStoreManager.horarioColetaFlow.asLiveData()

    private val _locationResult = MutableLiveData<LocalizacaoData>()
    var locationResult: LiveData<LocalizacaoData> = _locationResult


    var localizacao: LiveData<String> = MediatorLiveData<String>().apply {

        fun atualizarLocalizacao() {
            val cidadeAtual = cidade.value
            val bairroAtual = bairro.value

            if (!cidadeAtual.isNullOrBlank() && !bairroAtual.isNullOrBlank()) {
                value = "${bairroAtual}, ${cidadeAtual}"
            } else {
                value = "Localização não definida"
            }
        }

        addSource(cidade) {
            atualizarLocalizacao()
        }

        addSource(bairro) {
            atualizarLocalizacao()
        }
    }

    var listDiasTerao: LiveData<List<String>> = coletaDias.map { diasString ->
        if (diasString.isNullOrBlank()) emptyList() else DataFormatter.stringToList(diasString)
    }

    /*
    Pega os Dados do formulario
     */
    fun processarFormulario(cidade: EditText, bairro: EditText): String {
        try {
            _locationResult.value = ControllerLocation.processarFormulario(cidade, bairro)
            return "Salvo com Sucesso"
        } catch (e: IllegalArgumentException) {
            return "Preencha os campos corretamente"
        }
    }
}