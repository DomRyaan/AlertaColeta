package com.exemplo.alertacoleta.dataLayer.model

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.exemplo.alertacoleta.LogsDebug
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.resume

/*
    Essa classe tem a responsabilidade de lidar com a localização do dispositivo (GPS).
    Ela é assíncrona, usando callbacks para retornar os resultados.
*/

class LocalizacaoGPS(private val context: Context) {
    val LOCATION_PERMISSION_REQUEST_CODE = 100
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)


    /**
     * Pega a localização atual
     */
    @SuppressLint("MissingPermission")
    suspend fun obterLocalizacao(): String {
        if (!verificarPermissao()) {
            return "Permissão de localização não concedida"
        }

        val location = suspendCancellableCoroutine { continuation ->
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    continuation.resume(location)
                }
                .addOnFailureListener { exception ->
                    if (continuation.isActive) {
                        Log.i(Log.ERROR.toString(), "Falha ao obter localização: $exception")
                        continuation.resumeWithException(exception)
                    }
                }
            continuation.invokeOnCancellation {
                LogsDebug.log("Coroutine de localização foi cancelada")
            }
        }
        if (location == null) {
            return "O GPS não conseguiu achar sua localização"
        }

        val endereco =  pegarEndereco(location.latitude, location.longitude)

        return if (endereco != null) {
            val bairro = getBairro(endereco)
            val cidade = getCidade(endereco)
            "$cidade,$bairro"
        } else {
            "O endereço não foi encontrado"
        }
    }


        /**
         * Converte coordenadas (Latitude, Longitude) em uma lista de endereços.
         */
        private suspend fun pegarEndereco(latitude: Double, longitude: Double): List<Address?>? {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    obterLoactionModerno(latitude, longitude)
                }else {
                    obterLocationDepreciado(latitude, longitude)
            }
        }

        private fun getBairro(
            endereco: List<Address?>
        ): String {
            return endereco.firstOrNull()?.subLocality ?: "Bairro Desconhecido"
        }

        private fun getCidade(endereco: List<Address?>): String {
            return endereco.firstOrNull()?.subAdminArea ?: "Cidade Desconhecida"
        }

        fun verificarPermissao(): Boolean {
            var permitidoFineLocation =
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            var permitidoCoaseLocation =
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )

            if (permitidoFineLocation == PackageManager.PERMISSION_GRANTED && permitidoCoaseLocation == PackageManager.PERMISSION_GRANTED) {
                return true
            }
            return false
        }
    // ---- SOLUÇÃO NOVA (Android 13+) ---
    // Usa o GeocoderListener
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("MissingPermission")
    private suspend fun obterLoactionModerno(latitude: Double, longitude: Double): List<Address?> {
        val geocoder = Geocoder(context, Locale.getDefault())

        return suspendCancellableCoroutine { continuation ->
            geocoder.getFromLocation(latitude, longitude, 1, object: Geocoder.GeocodeListener{
                override fun onGeocode(addresses: List<Address?>) {
                    continuation.resume(addresses)
                }
                override fun onError(errorMessage: String?) {
                    continuation.resumeWithException(Exception("Erro durante o geocoder: $errorMessage"))
                }
            })
        }
    }


    // ---- Solução Antiga (Android 12 ou Inferior) ----
    private suspend fun obterLocationDepreciado(latitude: Double, longitude: Double): List<Address>? {

        val geocoder = Geocoder(context, Locale.getDefault())

        @Suppress("DEPRECATION")
        return try {
            geocoder.getFromLocation(latitude, longitude, 1)
        } catch (e: Exception) {
            println("Erro ao buscar endereço: $e")
            return null
        }
    }
}