package com.exemplo.alertacoleta.dataLayer.model.formatter

import com.exemplo.alertacoleta.global.LogsDebug

/**
 * Essa classe serve para formatar os dados vindo do armazenamento
 */
class DataFormatter {
    companion object {
        fun stringToList(diasColeta: String): List<String> {
            return diasColeta.replace("/", ",")
                .split(",")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
        }

        private fun getDigito(texto: String): String{
            return texto.filter { char -> char.isDigit()}
        }

        fun naoTemHorario(texto: String): Boolean {
            return if (getDigito(texto).length < 2) true else false
        }

        fun getHora(texto: String): Int {
            return getDigito(texto).substring(0, 2).toIntOrNull() ?: 18
        }

        fun getMin(texto: String): Int {
            val apenasNumero = getDigito(texto)
            return if (apenasNumero.length >= 4) apenasNumero.substring(2, 4).toIntOrNull() ?: 0 else 0
        }

        fun temColetaHoje(
            dias: List<String>
        ): Boolean {
            val hoje = TempoFormatter().obterDiaDaSemanaFormatado()
            return dias.contains(hoje.uppercase())
        }
    }
}