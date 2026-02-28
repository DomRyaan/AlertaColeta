package com.exemplo.alertacoleta.dataLayer.model.localizacao

import android.widget.EditText
import com.exemplo.alertacoleta.dataLayer.dados.LocalizacaoData

class FormularioEndereco(
   private val cidade: String,
   private val bairro: String
){

    fun nuloOuVazio(): Boolean {
        return this.cidade.isNullOrBlank() || this.bairro.isNullOrBlank()
    }

    fun montandoDados(): LocalizacaoData {
        return LocalizacaoData(
            isSuccess = true,
            cidade = this.cidade,
            bairro = this.bairro
        )
    }
}