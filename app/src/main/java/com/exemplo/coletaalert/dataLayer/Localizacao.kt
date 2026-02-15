package com.exemplo.coletaalert.dataLayer

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.Locale
import com.exemplo.coletaalert.LogsDebug

/*
    Essa classe tem a responsabilidade de lidar com a localização do dispositivo (GPS).
    Ela é assíncrona, usando callbacks para retornar os resultados.
*/

class Localizacao(private val context: Activity) {

    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)


    /**
     * Pega a localização atual e a retorna através de um callback.
     *
     * @param onResult Callback que será chamado com a string "Bairro - Cidade" em caso de sucesso
     *                 ou com uma mensagem de erro em caso de falha.
     */
    fun obterLocalizacao(onResult: (String) -> Unit) {


        pegarCoordenadas { coordenadas ->
            if (coordenadas != null) {
                try {
                    val endereco = pegarEndereco(coordenadas)
                    if (endereco != null) {
                        val bairro = getBairro(endereco)
                        val cidade = getCidade(endereco)

                        onResult("$bairro - $cidade")
                    } else {
                        onResult("Endereço não encontrado")
                    }
                } catch (e: Exception) {
                    LogsDebug.log("Falha ao processar endereço $e")
                    onResult("Falha ao descobrir Localização")
                }
            } else {
                onResult("Localização não encontrada")
            }
        }
    }


        /**
         * Função assíncrona que obtém as coordenadas e as passa para um callback.
         * @param onCoordenadasResult Callback que recebe a lista de coordenadas ou null.
         */
        @SuppressLint("MissingPermission")
        private fun pegarCoordenadas(onCoordenadasResult: (List<Double>?) -> Unit) {

            // O Android exige que verifiquemos a permissão novamente antes de chamar esta função (segurança do compilador)
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                onCoordenadasResult(null)
                return
            }

            // Tarefa Assíncrona
             fusedLocationClient.lastLocation
                 .addOnSuccessListener { location ->
                     if(location != null) {
                         val coordenadas = listOf(location.latitude, location.longitude)
                         onCoordenadasResult(coordenadas)
                     } else{
                         onCoordenadasResult(null)
                     }
                 }.addOnFailureListener {
                        onCoordenadasResult(null)
                }
        }
        /**
         * Converte coordenadas (Latitude, Longitude) em uma lista de endereços.
         * Esta é uma operação síncrona, mas pode bloquear a UI.
         */
        private fun pegarEndereco(coordenadas: List<Double>): List<Address>? {
            if (coordenadas.size < 2) return null

            val geocoder = Geocoder(context, Locale.getDefault())
            return try {
                geocoder.getFromLocation(coordenadas[0], coordenadas[1], 1)
            } catch (e: Exception) {
                null
            }
        }

        private fun getBairro(
            endereco: List<Address?>
        ): String {
                return endereco.firstOrNull()?.subLocality ?: "Bairro Desconhecido"
        }

        private fun getCidade(endereco: List<Address?>): String {
            return  endereco.firstOrNull()?.subAdminArea ?: "Cidade Desconhecida"
        }
}
