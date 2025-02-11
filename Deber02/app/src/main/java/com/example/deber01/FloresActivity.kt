package com.example.deber01

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FloresActivity : AppCompatActivity() {

    private lateinit var listaFlores: MutableList<Flor>
    private lateinit var adapter: FlorAdapter
    private lateinit var recyclerViewFlores: RecyclerView
    private lateinit var nombreJardin: String
    private var floresVisibles = false  // Variable para alternar la visibilidad

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flores)

        // Recibir el nombre del jardín seleccionado
        nombreJardin = intent.getStringExtra("nombreJardin") ?: "Sin nombre"

        recyclerViewFlores = findViewById(R.id.recyclerViewFlores)
        val btnAgregarFlor: Button = findViewById(R.id.btnAgregarFlor)
        val btnVerFlores: Button = findViewById(R.id.btnVerFlores)
        val btnEditarFlor: Button = findViewById(R.id.btnEditarFlor)
        val btnEliminarFlor: Button = findViewById(R.id.btnEliminarFlor)

        // Inicializar lista de flores
        listaFlores = mutableListOf(
            Flor("Peonía", "Rosa", 5.0, true, "Verano"),
            Flor("Girasol", "Amarillo", 20.0, false, "Verano")
        )

        // Configurar RecyclerView pero INICIALMENTE OCULTO
        recyclerViewFlores.layoutManager = LinearLayoutManager(this)
        adapter = FlorAdapter(this, listaFlores) { florSeleccionada ->
            mostrarDialogoOpciones(florSeleccionada)
        }
        recyclerViewFlores.adapter = adapter
        recyclerViewFlores.visibility = View.GONE // OCULTAR INICIALMENTE

        // Eventos de botones
        btnAgregarFlor.setOnClickListener { mostrarDialogoAgregarFlor() }
        btnVerFlores.setOnClickListener { alternarVisibilidadFlores() }
        btnEditarFlor.setOnClickListener { mostrarDialogoEditarFlor() }
        btnEliminarFlor.setOnClickListener { mostrarDialogoEliminarFlor() }
    }

    private fun alternarVisibilidadFlores() {
        if (floresVisibles) {
            recyclerViewFlores.visibility = View.GONE
            floresVisibles = false
        } else {
            recyclerViewFlores.visibility = View.VISIBLE
            floresVisibles = true
        }
    }

    private fun mostrarDialogoOpciones(flor: Flor) {
        val opciones = arrayOf("Editar", "Eliminar")

        AlertDialog.Builder(this)
            .setTitle("Opciones para ${flor.nombre}")
            .setItems(opciones) { _, which ->
                when (which) {
                    0 -> mostrarDialogoEditarFlor(flor) // Editar
                    1 -> confirmarEliminarFlor(flor)  // Eliminar
                }
            }
            .show()
    }

    private fun confirmarEliminarFlor(flor: Flor) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Flor")
            .setMessage("¿Seguro que quieres eliminar la flor '${flor.nombre}'?")
            .setPositiveButton("Sí") { _, _ ->
                listaFlores.remove(flor)
                actualizarLista()
                Toast.makeText(this, "Flor eliminada", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun actualizarLista() {
        adapter.notifyDataSetChanged()
    }

    private fun mostrarDialogoAgregarFlor() {
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.dialogo_agregar_flor, null)

        val etNombre = view.findViewById<EditText>(R.id.etNombreFlor)
        val etColor = view.findViewById<EditText>(R.id.etColorFlor)
        val etDiametro = view.findViewById<EditText>(R.id.etDiametroFlor)
        val switchFragante = view.findViewById<Switch>(R.id.switchFragante)
        val etTemporada = view.findViewById<EditText>(R.id.etTemporadaFlor)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Agregar Flor")
            .setView(view)
            .setPositiveButton("Guardar") { _, _ ->
                val nuevaFlor = Flor(
                    etNombre.text.toString(),
                    etColor.text.toString(),
                    etDiametro.text.toString().toDoubleOrNull() ?: 0.0,
                    switchFragante.isChecked,
                    etTemporada.text.toString()
                )
                listaFlores.add(nuevaFlor)
                actualizarLista()
                Toast.makeText(this, "Flor agregada", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.show()
    }

    private fun mostrarDialogoEditarFlor() {
        if (listaFlores.isEmpty()) {
            Toast.makeText(this, "No hay flores para editar", Toast.LENGTH_SHORT).show()
            return
        }

        val nombres = listaFlores.map { it.nombre }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Seleccionar Flor para Editar")
            .setItems(nombres) { _, position ->
                mostrarDialogoEditarFlor(listaFlores[position])
            }
            .show()
    }

    private fun mostrarDialogoEditarFlor(flor: Flor) {
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.dialogo_agregar_flor, null)

        val etNombre = view.findViewById<EditText>(R.id.etNombreFlor)
        val etColor = view.findViewById<EditText>(R.id.etColorFlor)
        val etDiametro = view.findViewById<EditText>(R.id.etDiametroFlor)
        val switchFragante = view.findViewById<Switch>(R.id.switchFragante)
        val etTemporada = view.findViewById<EditText>(R.id.etTemporadaFlor)

        etNombre.setText(flor.nombre)
        etColor.setText(flor.color)
        etDiametro.setText(flor.diametro.toString())
        switchFragante.isChecked = flor.fragante
        etTemporada.setText(flor.temporadaFloracion)

        AlertDialog.Builder(this)
            .setTitle("Editar Flor")
            .setView(view)
            .setPositiveButton("Guardar") { _, _ ->
                flor.nombre = etNombre.text.toString()
                flor.color = etColor.text.toString()
                flor.diametro = etDiametro.text.toString().toDoubleOrNull() ?: 0.0
                flor.fragante = switchFragante.isChecked
                flor.temporadaFloracion = etTemporada.text.toString()
                actualizarLista()
                Toast.makeText(this, "Flor actualizada", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .create()
            .show()
    }

    private fun mostrarDialogoEliminarFlor() {
        if (listaFlores.isEmpty()) {
            Toast.makeText(this, "No hay flores para eliminar", Toast.LENGTH_SHORT).show()
            return
        }

        val nombres = listaFlores.map { it.nombre }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Seleccionar Flor para Eliminar")
            .setItems(nombres) { _, position ->
                listaFlores.removeAt(position)
                actualizarLista()
                Toast.makeText(this, "Flor eliminada", Toast.LENGTH_SHORT).show()
            }
            .show()
    }
}
