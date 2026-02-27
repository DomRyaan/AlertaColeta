package com.exemplo.alertacoleta.dataLayer.model.formatter

import android.os.Build
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

/**
 * Classe que abstrai a obtenção e formatação de datas,
 * lidando com as diferentes versões de API do Android.
 */
class TempoFormatter {
    internal val LOCAL: Locale = Locale("pt", "BR")

    /**
     * Retorna o nome abreviado do dia da semana atual em português (ex: "seg.").
     *
     * @param padrao O formato desejado. Padrão "E" para o dia abreviado. "EEEE" para o nome completo.
     * @return Uma String com o dia da semana formatado.
     */
    fun obterDiaDaSemanaFormatado(padrao: String = "E"): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Caminho moderno (API 26+)
            val hoje = LocalDate.now()
            val formatador = DateTimeFormatter.ofPattern(padrao, LOCAL)
            hoje.format(formatador)
        } else {
            // Caminho antigo (antes da API 26)
            val hoje = Date()
            val formatador = SimpleDateFormat(padrao, LOCAL)
            formatador.format(hoje)
        }
    }
}