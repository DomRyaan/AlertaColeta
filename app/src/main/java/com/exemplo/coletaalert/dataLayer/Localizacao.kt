package com.exemplo.coletaalert.dataLayer

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.Locale

/*
    Essa classe tem a responsabilidade de lidar com o GPS
*/

class Localizacao(
    context: Activity
){
    lateinit var fusedLocationClient: FusedLocationProviderClient

    lateinit var context: Activity

    init {
        this.context = context
        fusedLocationClient =   LocationServices.getFusedLocationProviderClient(context)
    }

    fun obterLocalizacao(){
        // O Android exige que verifiquemos a permissão novamente antes de chamar esta função (segurança do compilador)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            val coordenadas = pegarCodernadas(location)


        }.addOnFailureListener {
            Toast.makeText(context, "Falha ao pegar localização!", Toast.LENGTH_SHORT).show()
        }

    }
    /*
    A função vai retornar uma array com as coordenadas do dispositvo.
    A estrutura do array retornada é [latitude, logintude], se não for encontrada, retorna null
     */
    private fun pegarCodernadas(location: Location): List<Double>?{
        if (location != null) {
            val latitude = location.latitude
            val longitude = location.longitude

            return arrayListOf(latitude, longitude)
        } else {
            return null
        }
    }



    fun descobrirBairro(latitude: Double,
                        longitude: Double
    ): String {
        val geocoder = Geocoder(context, Locale.getDefault())

        try {
            val endereco = geocoder.getFromLocation(latitude, longitude, 1)

            if (endereco != null && endereco.isNotEmpty()) {
                val enderecoEncontrado = endereco[0]

                val bairro = enderecoEncontrado.subLocality

                return bairro ?: "Bairro Desconhecido"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return "Erro ao buscar bairro"
        }
        return "Endereço não encontrado"
    }


}