package com.exemplo.alertacoleta.presentationLayer.home.recycleColeta
import android.content.res.ColorStateList
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.exemplo.alertacoleta.R
import com.exemplo.alertacoleta.databinding.ItemScheduleBinding
import com.exemplo.alertacoleta.dataLayer.model.formatter.TempoFormatter

val tempoFormatter: TempoFormatter = TempoFormatter()

class ColetaViewHolder(
    private val binding: ItemScheduleBinding
) :  ViewHolder(binding.root){

    fun bind(diasSemanas: String, condicao: Boolean) {
        val colorViews = setColorViews(condicao)

        val hoje = tempoFormatter.obterDiaDaSemanaFormatado().uppercase()

        if (diasSemanas in hoje) {
            binding.cardMain.strokeColor = itemView.context.getColor(R.color.eco_forest_green)
            binding.cardMain.strokeWidth = 3
        }else{
            binding.cardMain.strokeWidth= 0
        }

        if (condicao) {
            binding.cardMain.cardElevation = 10f
        }

        binding.cardMain.setCardBackgroundColor(ColorStateList.valueOf(setBackgroundColor(condicao)))
        binding.textDia.setTextColor(colorViews)
        binding.textDia.text = diasSemanas

        binding.iconView.setColorFilter(colorViews)
        binding.iconView.setImageResource(setIcon(condicao))
    }

    private fun setColorViews(condicao: Boolean): Int {
        return if (condicao) itemView.context.getColor(R.color.eco_forest_green)  else  itemView.context.getColor(R.color.eco_text_secondary_dark)

    }

    private fun setIcon(condicao: Boolean): Int{
        return if (condicao) R.drawable.check_square_svgrepo_com else R.drawable.error_box_svgrepo_com
    }


    private fun setBackgroundColor(condicao: Boolean): Int {
        return if (condicao) itemView.context.getColor(R.color.eco_surface_white) else itemView.context.getColor(R.color.eco_background_light)
    }
}