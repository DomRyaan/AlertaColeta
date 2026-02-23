package com.exemplo.alertacoleta.presentationLayer.home.recycleColeta
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.exemplo.alertacoleta.LogsDebug
import com.exemplo.alertacoleta.R
import com.exemplo.alertacoleta.dataLayer.dados.Coleta
import com.exemplo.alertacoleta.databinding.ItemScheduleBinding
import com.exemplo.alertacoleta.dataLayer.model.Tempo

val tempo: Tempo = Tempo()

class ColetaViewHolder(
    private val binding: ItemScheduleBinding
) :  ViewHolder(binding.root){

    fun bind(coleta: Coleta) {
        val condicao = coleta.vaiTer

        val colorViews = setColorViews(condicao)

        val dia = coleta.dia.toString()
        val hoje = tempo.obterDiaDaSemanaFormatado().uppercase()

        if (dia in hoje) {
            binding.cardMain.strokeColor = itemView.context.getColor(R.color.eco_forest_green)
            binding.cardMain.strokeWidth = 3
        }

        if (condicao) {
            binding.cardMain.cardElevation = 10f
        }

        binding.cardMain.setCardBackgroundColor(ColorStateList.valueOf(setBackgroundColor(condicao)))
        binding.textDia.setTextColor(colorViews)
        val diaFormatado = coleta.dia.toString()
        binding.textDia.text = diaFormatado

        binding.iconView.setColorFilter(colorViews)
        binding.iconView.setImageResource(setIcon(condicao))
    }

    private fun setColorViews(condicao: Boolean): Int {
        return if (condicao) itemView.context.getColor(R.color.eco_forest_green)  else  itemView.context.getColor(R.color.eco_text_secondary_dark)

    }

    private fun setIcon(condicao: Boolean): Int{
        return if (condicao) R.drawable.check_square_svgrepo_com else R.drawable.error_box_svgrepo_com
    }


    private fun setStrokeWidth(condicao: Boolean): Int{
        return if (condicao) 2 else 0
    }

    private fun setBackgroundColor(condicao: Boolean): Int {
        return if (condicao) itemView.context.getColor(R.color.eco_surface_white) else itemView.context.getColor(R.color.eco_background_light)
    }
}