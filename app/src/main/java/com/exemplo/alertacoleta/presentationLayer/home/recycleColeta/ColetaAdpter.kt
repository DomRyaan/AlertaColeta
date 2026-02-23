package com.exemplo.alertacoleta.presentationLayer.home.recycleColeta

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.exemplo.alertacoleta.dataLayer.dados.Coleta
import com.exemplo.alertacoleta.databinding.ItemScheduleBinding

class ColetaAdpter(
    private val coletas: List<Coleta>
) : RecyclerView.Adapter<ColetaViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ColetaViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        val binding = ItemScheduleBinding.inflate(inflater, parent, false)

        return ColetaViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ColetaViewHolder,
        position: Int
    ) {
        val coletaItem = coletas[position]

        holder.bind((coletaItem))
    }

    override fun getItemCount(): Int {
        return coletas.size
    }
}