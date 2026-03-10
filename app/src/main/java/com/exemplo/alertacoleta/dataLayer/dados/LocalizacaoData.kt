package com.exemplo.alertacoleta.dataLayer.dados

data class LocalizacaoData(
    val isSuccess: Boolean,
    val cidade: String? = null,
    val bairro: String? = null,
    val error: String? = null
)
