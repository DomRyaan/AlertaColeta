package com.exemplo.alertacoleta.dataLayer.model.localizacao

import android.view.View
import android.widget.EditText
import com.exemplo.alertacoleta.dataLayer.dados.LocalizacaoData

class FormularioEndereco(
   private val cidade: EditText,
   private val bairro: EditText
){

    fun nuloOuVazio(): Boolean {
        val cidadeString = cidade.getString()
        val bairroString = bairro.getString()
        return cidadeString.isNullOrBlank() || bairroString.isNullOrBlank()
    }

    fun EditText.getString(): String{
        return text.toString().trim()
    }

    fun montandoDados(): LocalizacaoData {
        return LocalizacaoData(
            isSuccess = true,
            cidade = cidade.getString(),
            bairro = bairro.getString()
        )
    }
}