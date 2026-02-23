package com.exemplo.alertacoleta.dataLayer.dados

import com.exemplo.alertacoleta.dataLayer.dados.DiasSemanas

data class Coleta(
    val dia: DiasSemanas,
    val horario: String,
    val vaiTer: Boolean
)