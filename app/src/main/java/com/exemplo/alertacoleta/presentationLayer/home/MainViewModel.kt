package com.exemplo.alertacoleta.presentationLayer.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.exemplo.alertacoleta.dataLayer.model.Repository

class MainViewModel(private val repository: Repository) : ViewModel() {
    var cidade: LiveData<String?> = repository.dataStoreManager.cidadeFlow.asLiveData()
    var bairro: LiveData<String?> = repository.dataStoreManager.bairroFlow.asLiveData()

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
}