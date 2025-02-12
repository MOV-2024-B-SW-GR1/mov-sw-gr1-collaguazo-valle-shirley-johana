package com.example.deber01

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class JardinAdapter(
    private val context: Context,
    private var listaJardines: MutableList<Pair<Int, Jardin>>,
    private val onItemClick: (Pair<Int, Jardin>) -> Unit
) : RecyclerView.Adapter<JardinAdapter.JardinViewHolder>() {

    inner class JardinViewHolder(view: View) : RecyclerView.ViewHolder(view) {
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
        // Obtenemos el par (id, objeto Jardin)
        val (id, jardin) = listaJardines[position]
        holder.nombreJardin.text = jardin.nombre
        holder.ubicacionJardin.text = "Ubicación: ${jardin.ubicacion}"
        holder.fechaCreacion.text = "Fecha: ${jardin.fechaCreacion}"
        holder.tamanoJardin.text = "Tamaño: ${jardin.tamano} m²"
        holder.tipoSueloJardin.text = "Suelo: ${jardin.tipoSuelo}"

        // Se pasa el par completo al callback
        holder.itemView.setOnClickListener {
            onItemClick(listaJardines[position])
        }
    }

    override fun getItemCount(): Int = listaJardines.size

    /**
     * Actualiza la lista interna del adaptador y refresca la vista.
     *
     * @param newLista La nueva lista de jardines (con sus respectivos ID).
     */
    fun updateData(newLista: List<Pair<Int, Jardin>>) {
        listaJardines.clear()
        listaJardines.addAll(newLista)
        notifyDataSetChanged()
    }
}
