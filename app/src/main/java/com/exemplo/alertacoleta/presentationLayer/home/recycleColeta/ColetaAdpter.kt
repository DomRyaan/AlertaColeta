package com.exemplo.alertacoleta.presentationLayer.home.recycleColeta

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.exemplo.alertacoleta.global.LogsDebug
import com.exemplo.alertacoleta.dataLayer.dados.DiasSemanas
import com.exemplo.alertacoleta.databinding.ItemScheduleBinding

class ColetaAdpter(
    private val diasSemanas: List<DiasSemanas>,
    private var diasComColeta: List<String>
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
        val diaItem = diasSemanas[position]

        val teraColeta = diasComColeta.contains(diaItem.name)
        holder.bind(diaItem.name, teraColeta)
    }

    override fun getItemCount(): Int {
        return diasSemanas.size
    }

    fun atualizarDiasComColeta(novaLista: List<String>) {
        this.diasComColeta = novaLista
        notifyDataSetChanged() // Informa ao adapter que os dados mudaram!
    }
}