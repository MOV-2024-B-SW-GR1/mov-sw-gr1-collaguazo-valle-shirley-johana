package com.example.deber01

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SeleccionarJardinParaFloresActivity : AppCompatActivity() {

    private lateinit var listaJardines: MutableList<Pair<Int, Jardin>>
    private lateinit var adapter: JardinAdapter
    private lateinit var recyclerViewJardines: RecyclerView
    private lateinit var jardinDao: JardinDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seleccionar_jardin)

        // Inicializamos el DAO para jardines
        jardinDao = JardinDAO(this)

        recyclerViewJardines = findViewById(R.id.recyclerViewJardines)
        recyclerViewJardines.layoutManager = LinearLayoutManager(this)

        // Cargamos los jardines desde SQLite
        cargarJardines()

        // Configuramos el adaptador. El callback recibe un Pair<Int, Jardin>
        adapter = JardinAdapter(this, listaJardines) { jardinPair ->
            abrirFloresActivity(jardinPair)
        }
        recyclerViewJardines.adapter = adapter
    }

    private fun cargarJardines() {
        // Obtenemos la lista de jardines desde la BD; cada elemento es Pair(id, Jardin)
        listaJardines = jardinDao.obtenerTodos().toMutableList()
    }

    private fun abrirFloresActivity(jardinPair: Pair<Int, Jardin>) {
        val (id, jardin) = jardinPair
        val intent = Intent(this, FloresActivity::class.java)
        // Enviamos el ID y nombre del jardín para que la actividad de flores filtre correctamente
        intent.putExtra("jardinId", id)
        intent.putExtra("nombreJardin", jardin.nombre)
        startActivity(intent)
    }
}
