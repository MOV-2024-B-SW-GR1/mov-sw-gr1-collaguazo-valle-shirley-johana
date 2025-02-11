package com.example.deber01

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class JardinAdapter(
    private val context: Context,
    private val listaJardines: MutableList<Jardin>,
    private val onItemClick: (Jardin) -> Unit
) : RecyclerView.Adapter<JardinAdapter.JardinViewHolder>() {

    class JardinViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombreJardin: TextView = view.findViewById(R.id.tvNombreJardin)
        val ubicacionJardin: TextView = view.findViewById(R.id.tvUbicacionJardin)
        val fechaCreacion: TextView = view.findViewById(R.id.tvFechaJardin)
        val tamanoJardin: TextView = view.findViewById(R.id.tvTamanoJardin)
        val tipoSueloJardin: TextView = view.findViewById(R.id.tvTipoSueloJardin)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JardinViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_jardin, parent, false)
        return JardinViewHolder(view)
    }

    override fun onBindViewHolder(holder: JardinViewHolder, position: Int) {
        val jardin = listaJardines[position]
        holder.nombreJardin.text = jardin.nombre
        holder.ubicacionJardin.text = "Ubicación: ${jardin.ubicacion}"
        holder.fechaCreacion.text = "Fecha: ${jardin.fechaCreacion}"
        holder.tamanoJardin.text = "Tamaño: ${jardin.tamano} m²"
        holder.tipoSueloJardin.text = "Suelo: ${jardin.tipoSuelo}"

        // Manejo de clic en el elemento
        holder.itemView.setOnClickListener {
            onItemClick(jardin)
        }
    }

    override fun getItemCount(): Int = listaJardines.size
}
