package com.example.deber01

import android.os.Bundle
import com.example.deber01.FlorAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var florAdapter: FlorAdapter
    private val listaFlores = mutableListOf<Flor>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Agregamos datos manualmente a la lista
        listaFlores.add(Flor("Rosa", "Rojo", 5.0, true, "Primavera"))
        listaFlores.add(Flor("Girasol", "Amarillo", 20.0, false, "Verano"))
        listaFlores.add(Flor("Tulipán", "Rosa", 10.0, false, "Invierno"))

        florAdapter = FlorAdapter(listaFlores)
        recyclerView.adapter = florAdapter
    }
}
