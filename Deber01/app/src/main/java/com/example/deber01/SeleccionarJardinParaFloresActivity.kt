package com.example.deber01

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SeleccionarJardinParaFloresActivity : AppCompatActivity() {

    private lateinit var listaJardines: MutableList<Jardin>
    private lateinit var adapter: JardinAdapter
    private lateinit var recyclerViewJardines: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seleccionar_jardin)

        // Inicializar lista de jardines
        listaJardines = mutableListOf(
            Jardin("Jardín Peonías", "Nayón", "2021-05-12", 50.0, "Húmedo"),
            Jardin("Jardín Rosas", "Cumbayá", "2020-08-20", 30.0, "Arenoso")
        )

        recyclerViewJardines = findViewById(R.id.recyclerViewJardines)
        recyclerViewJardines.layoutManager = LinearLayoutManager(this)

        // Configurar el adaptador
        adapter = JardinAdapter(this, listaJardines) { jardinSeleccionado ->
            abrirFloresActivity(jardinSeleccionado)
        }
        recyclerViewJardines.adapter = adapter
    }

    private fun abrirFloresActivity(jardin: Jardin) {
        val intent = Intent(this, FloresActivity::class.java)
        intent.putExtra("nombreJardin", jardin.nombre)
        startActivity(intent)
    }
}
