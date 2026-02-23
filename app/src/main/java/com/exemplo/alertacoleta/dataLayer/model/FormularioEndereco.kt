package com.exemplo.alertacoleta.dataLayer.model

import android.widget.EditText
import com.exemplo.alertacoleta.dataLayer.dados.LocalizacaoData

class FormularioEndereco(
    cidade: EditText,
    bairro: EditText
){
    val cidade: String
    val bairro: String

    init {
        this.cidade = cidade.text.toString()
        this.bairro = bairro.text.toString()
    }
    fun validar(): Boolean {
        return this.cidade.isNotEmpty() && this.bairro.isNotEmpty()
    }

    fun montandoDados(): LocalizacaoData {
        return LocalizacaoData(
            isSuccess = true,
            cidade = this.cidade,
            bairro = this.bairro
        )
    }
}