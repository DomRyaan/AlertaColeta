package com.exemplo.alertacoleta.presentationLayer.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import com.exemplo.alertacoleta.dataLayer.model.Repository
import com.exemplo.alertacoleta.dataLayer.model.formatter.DataFormatter

class MainViewModel(private val repository: Repository) : ViewModel() {
    var cidade: LiveData<String?> = repository.dataStoreManager.cidadeFlow.asLiveData()
    var bairro: LiveData<String?> = repository.dataStoreManager.bairroFlow.asLiveData()

    var coletaDias: LiveData<String?> = repository.dataStoreManager.diasColetaFlow.asLiveData()
    var horario: LiveData<String?> = repository.dataStoreManager.horarioColetaFlow.asLiveData()

    var localizacao: LiveData<String> = MediatorLiveData<String>().apply {

        fun atualizarLocalizacao() {
            val cidadeAtual = cidade.value
            val bairroAtual = bairro.value
            val diaAtual = coletaDias.value

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
}