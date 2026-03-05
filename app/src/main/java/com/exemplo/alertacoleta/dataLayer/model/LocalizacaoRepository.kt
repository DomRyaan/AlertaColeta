package com.exemplo.alertacoleta.dataLayer.model

import android.widget.EditText
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.exemplo.alertacoleta.dataLayer.dados.LocalizacaoData
import com.exemplo.alertacoleta.dataLayer.model.localizacao.FormularioEndereco
import com.exemplo.alertacoleta.dataLayer.model.localizacao.LocalizacaoGPS
import com.exemplo.alertacoleta.global.LogsDebug
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class LocalizacaoRepository {

    private val repositoryLocationScope = CoroutineScope(SupervisorJob())


    fun onCleared(){
        repositoryLocationScope.cancel()
    }
}