package com.example.deber01

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// Usamos Triple: first = ID de la flor, second = ID del jardín, third = objeto Flor
class FlorAdapter(
    private val context: Context,
    private var listaFlores: MutableList<Triple<Int, Int, Flor>>,
    private val onItemClick: (Triple<Int, Int, Flor>) -> Unit
) : RecyclerView.Adapter<FlorAdapter.FlorViewHolder>() {

    inner class FlorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombreFlor: TextView = view.findViewById(R.id.tvNombreFlor)
        val colorFlor: TextView = view.findViewById(R.id.tvColorFlor)
        val diametroFlor: TextView = view.findViewById(R.id.tvDiametroFlor)
        val fraganteFlor: TextView = view.findViewById(R.id.tvFraganteFlor)
        val temporadaFlor: TextView = view.findViewById(R.id.tvTemporadaFlor)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlorViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_flor, parent, false)
        return FlorViewHolder(view)
    }

    override fun onBindViewHolder(holder: FlorViewHolder, position: Int) {
        val (_, _, flor) = listaFlores[position]
        holder.nombreFlor.text = flor.nombre
        holder.colorFlor.text = "Color: ${flor.color}"
        holder.diametroFlor.text = "Diámetro: ${flor.diametro} cm"
        holder.fraganteFlor.text = "Fragante: ${if (flor.fragante) "Sí" else "No"}"
        holder.temporadaFlor.text = "Temporada: ${flor.temporadaFloracion}"

        holder.itemView.setOnClickListener {
            onItemClick(listaFlores[position])
        }
    }

    override fun getItemCount(): Int = listaFlores.size

    fun updateData(newLista: List<Triple<Int, Int, Flor>>) {
        listaFlores.clear()
        listaFlores.addAll(newLista)
        notifyDataSetChanged()
    }
}
