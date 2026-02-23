package com.exemplo.alertacoleta.presentationLayer.inicio

import android.widget.EditText
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exemplo.alertacoleta.LogsDebug
import kotlinx.coroutines.launch
import com.exemplo.alertacoleta.dataLayer.dados.LocalizacaoData
import com.exemplo.alertacoleta.dataLayer.model.FormularioEndereco
import com.exemplo.alertacoleta.dataLayer.model.LocalizacaoGPS


class InitViewModel : ViewModel() {
    private val _locationResult = MutableLiveData<LocalizacaoData>()

    var locationResult: LiveData<LocalizacaoData> = _locationResult

    /**
     * Inicia a busca pela localização
     */
    fun fetchLocation(localizacaoGPSManager: LocalizacaoGPS){
        viewModelScope.launch {
            val resultado: String? = localizacaoGPSManager.obterLocalizacao()

            if (resultado.isNullOrBlank() || resultado.contains("não encontrada") || resultado.contains("Falha")) {
                _locationResult.value = LocalizacaoData(isSuccess = false, null, null, error = resultado ?: "Erro desconhecido")
            } else {
                val areaLocal = resultado.split(",").map{ it.trim() }
                LogsDebug.log("ENDEREÇO PEGO: ${areaLocal}")
                _locationResult.value = LocalizacaoData(
                    isSuccess = true,
                    cidade = areaLocal.getOrNull(0),
                    bairro = areaLocal.getOrNull(1)
                )
            }
        }
    }

    /*
    Pega os Dados do formulario
     */
    fun processarFormulario(cidade: EditText, bairro: EditText): String{

        val form = FormularioEndereco(cidade, bairro)

        if (form.validar()){
            return "Preencha os campos corretamente"
        }

        _locationResult.value = form.montandoDados()
        println(_locationResult.value)
        return "Salvo com Sucesso!"
    }
}