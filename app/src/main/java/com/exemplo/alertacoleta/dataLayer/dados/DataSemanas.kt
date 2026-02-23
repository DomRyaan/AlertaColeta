package com.exemplo.alertacoleta.dataLayer.dados

import com.exemplo.alertacoleta.dataLayer.dados.DiasSemanas

val listaDeColetas = listOf(
    Coleta(DiasSemanas.DOM, "Não haverá coleta", false),
    Coleta(DiasSemanas.SEG, "18:45", true),
    Coleta(DiasSemanas.TER, "Não haverá coleta", false),
    Coleta(DiasSemanas.QUA, "18:45", true),
    Coleta(DiasSemanas.QUI, "Não haverá coleta", false),
    Coleta(DiasSemanas.SEX, "18:45", true),
    Coleta(DiasSemanas.SAB, "Não Haverá coleta", false)
)