package com.exemplo.alertacoleta.presentationLayer.inicio.viewModelInit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.exemplo.alertacoleta.dataLayer.model.Repository

class InitViewModelFactory(
    private val repository: Repository
    ) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InitViewModel::class.java)) {
            return InitViewModel(repository) as T
        }

        throw IllegalArgumentException("ViewModel class desconhecida")
    }
}