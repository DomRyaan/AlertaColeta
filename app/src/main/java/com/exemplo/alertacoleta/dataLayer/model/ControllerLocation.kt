package com.exemplo.alertacoleta.dataLayer.model

import android.widget.EditText
import com.exemplo.alertacoleta.dataLayer.dados.LocalizacaoData
import com.exemplo.alertacoleta.dataLayer.model.localizacao.FormularioEndereco
import com.exemplo.alertacoleta.dataLayer.model.localizacao.LocalizacaoGPS
import com.exemplo.alertacoleta.global.LogsDebug
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException

class ControllerLocation {
    companion object {
        /**
         * Inicia a busca pela localização
         */
        suspend fun fetchLocationRepository(localizacaoGPSManager: LocalizacaoGPS): LocalizacaoData {
            return try {
                val resultado: String? = localizacaoGPSManager.obterLocalizacao()

                if (validarResultado(resultado)) {
                    val localizacaoData = LocalizacaoData(
                        isSuccess = false,
                        null,
                        null,
                        error = resultado ?: "Erro desconhecido"
                    )
                    localizacaoData

                } else {
                    val areaLocal = resultado!!.split(",").map { it.trim() }
                    val localizacaoSucess = LocalizacaoData(
                        isSuccess = true,
                        cidade = areaLocal.getOrNull(0),
                        bairro = areaLocal.getOrNull(1)
                    )
                    localizacaoSucess
                }
            } catch (e: Exception) {
                LogsDebug.log("Erro: ${e.message}")
                throw Exception("Houve um erro no GPS")
            }
        }

        /*
   Pega os Dados do formulario
    */
        fun processarFormulario(cidade: EditText, bairro: EditText): LocalizacaoData {
            val form = FormularioEndereco(cidade, bairro)

            if (form.nuloOuVazio()) {
                throw IllegalArgumentException("Preencha os campos corretamente")
            }

            return form.montandoDados()
        }

        fun validarResultado(resultado: String?): Boolean {
            return resultado.isNullOrBlank() || resultado.contains("não conseguiu encontrar") || resultado.contains(
                "Falha"
            )
        }
    }
}