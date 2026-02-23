package com.exemplo.alertacoleta.dataLayer.model.api

import com.exemplo.alertacoleta.dataLayer.dados.ColetaJSONAPI
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("coletas/{cidade}/{bairro}")
    suspend fun getColeta(
        @Path("cidade") cidade: String,
        @Path("bairro") bairro: String
    ): Response<ColetaJSONAPI>
}