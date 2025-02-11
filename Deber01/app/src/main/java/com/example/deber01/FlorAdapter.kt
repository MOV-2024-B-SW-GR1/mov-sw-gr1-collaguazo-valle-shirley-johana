package com.example.deber01

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FlorAdapter(private val flores: List<Flor>) :
    RecyclerView.Adapter<FlorAdapter.FlorViewHolder>() {

    class FlorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombre: TextView = itemView.findViewById(R.id.nombreFlor)
        val color: TextView = itemView.findViewById(R.id.colorFlor)
        val diametro: TextView = itemView.findViewById(R.id.diametroFlor)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_flor, parent, false)
        return FlorViewHolder(view)
    }

    override fun onBindViewHolder(holder: FlorViewHolder, position: Int) {
        val flor = flores[position]
        holder.nombre.text = flor.nombre
        holder.color.text = "Color: ${flor.color}"
        holder.diametro.text = "Diámetro: ${flor.diametro} cm"
    }

    override fun getItemCount(): Int = flores.size
}
