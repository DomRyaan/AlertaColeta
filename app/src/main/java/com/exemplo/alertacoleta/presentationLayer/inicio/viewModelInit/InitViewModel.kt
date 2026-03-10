package com.exemplo.alertacoleta.presentationLayer.inicio.viewModelInit

import android.widget.EditText
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exemplo.alertacoleta.dataLayer.dados.LocalizacaoData
import com.exemplo.alertacoleta.dataLayer.model.ControllerLocation
import com.exemplo.alertacoleta.dataLayer.model.Repository
import com.exemplo.alertacoleta.dataLayer.model.localizacao.LocalizacaoGPS
import com.exemplo.alertacoleta.global.LogsDebug
import kotlinx.coroutines.launch

class InitViewModel(private val repository: Repository) : ViewModel() {

    private val _locationResult = MutableLiveData<LocalizacaoData>()

    var locationResult: LiveData<LocalizacaoData> = _locationResult


    /**
     * Inicia a busca pela localização
     */
    fun fetchLocation(localizacaoGPSManager: LocalizacaoGPS) {
        viewModelScope.launch {
            try {
                _locationResult.value = ControllerLocation.Companion.fetchLocationRepository(localizacaoGPSManager)
            } catch (e: Exception) {
                _locationResult.value = LocalizacaoData(
                    isSuccess = false,
                    error = "Falha ao iniciar busca: ${e.message}"
                )
                LogsDebug.log("Erro na ViewModel: ${e.message}")
            }
        }
    }

    /*
    Pega os Dados do formulario
     */
    fun processarFormulario(cidade: EditText, bairro: EditText): String{
        try {
            _locationResult.value = ControllerLocation.Companion.processarFormulario(cidade, bairro)
            return "Salvo com Sucesso"
        } catch (e: IllegalArgumentException) {
            return "Preencha os campos corretamente"
        }
    }
}