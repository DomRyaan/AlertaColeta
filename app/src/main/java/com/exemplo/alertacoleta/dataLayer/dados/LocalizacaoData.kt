package com.exemplo.alertacoleta.dataLayer.dados

data class LocalizacaoData(
    val isSuccess: Boolean,
    val cidade: String?,
    val bairro: String?,
    val error: String? = null
)
