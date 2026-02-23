package com.exemplo.alertacoleta.dataLayer.dados

import com.google.gson.annotations.SerializedName

data class ColetaJSONAPI(
    @SerializedName("id")
    val id: Int,

    @SerializedName("id_bairro")
    val idBairro: Int,

    @SerializedName("id_cidade")
    val idCidade: Int,

    @SerializedName("dias")
    val dias: String,

    @SerializedName("turno")
    val turno: String,

    @SerializedName("horario")
    val horario: String,

    @SerializedName("custom_dias")
    val customDias: Any,

    @SerializedName("custom_turno")
    val customTurno: Any,

    @SerializedName("custom_horario")
    val customHorario: String,

    @SerializedName("nome_cidade")
    val nomeCidade: String,

    @SerializedName("nome_bairro")
    val nomeBairro: String
)