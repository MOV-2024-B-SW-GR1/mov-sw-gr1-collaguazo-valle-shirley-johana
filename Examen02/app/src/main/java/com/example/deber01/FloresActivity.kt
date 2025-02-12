package com.example.deber01

import android.view.View

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FloresActivity : AppCompatActivity() {

    // Se recibirá el ID y el nombre del jardín desde el intent
    private var jardinId: Int = 0
    private lateinit var nombreJardin: String

    // Ahora usamos una lista de Triple: (florId, jardinId, Flor)
    private lateinit var listaFlores: MutableList<Triple<Int, Int, Flor>>
    private lateinit var adapter: FlorAdapter
    private lateinit var recyclerViewFlores: RecyclerView

    private lateinit var florDao: FlorDAO

    // Variable para alternar la visibilidad del RecyclerView
    private var floresVisibles = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flores)

        // Recuperar datos enviados (asegúrate de enviarlos al iniciar esta actividad)
        jardinId = intent.getIntExtra("jardinId", 0)
        nombreJardin = intent.getStringExtra("nombreJardin") ?: "Sin nombre"

        florDao = FlorDAO(this)

        recyclerViewFlores = findViewById(R.id.recyclerViewFlores)
        recyclerViewFlores.layoutManager = LinearLayoutManager(this)

        // Cargamos las flores asociadas al jardín desde SQLite
        cargarFlores()

        // Inicializamos el adaptador usando la lista obtenida
        adapter = FlorAdapter(this, listaFlores) { florTriple ->
            mostrarDialogoOpciones(florTriple)
        }
        recyclerViewFlores.adapter = adapter
        recyclerViewFlores.visibility = if (floresVisibles) View.VISIBLE else View.GONE

        // Referencias a botones
        val btnAgregarFlor: Button = findViewById(R.id.btnAgregarFlor)
        val btnVerFlores: Button = findViewById(R.id.btnVerFlores)
        val btnEditarFlor: Button = findViewById(R.id.btnEditarFlor)
        val btnEliminarFlor: Button = findViewById(R.id.btnEliminarFlor)

        // Eventos de botones
        btnAgregarFlor.setOnClickListener { mostrarDialogoAgregarFlor() }
        btnVerFlores.setOnClickListener { alternarVisibilidadFlores() }
        btnEditarFlor.setOnClickListener { mostrarDialogoEditarFlor() }
        btnEliminarFlor.setOnClickListener { mostrarDialogoEliminarFlor() }
    }

    // Carga las flores desde la BD filtrando por el ID del jardín
    private fun cargarFlores() {
        listaFlores = florDao.obtenerPorJardin(jardinId).toMutableList()
        if (::adapter.isInitialized) {
            adapter.updateData(listaFlores)
        }
    }

    private fun alternarVisibilidadFlores() {
        floresVisibles = !floresVisibles
        recyclerViewFlores.visibility = if (floresVisibles) View.VISIBLE else View.GONE
    }

    private fun mostrarDialogoOpciones(florTriple: Triple<Int, Int, Flor>) {
        val (_, _, flor) = florTriple
        val opciones = arrayOf("Editar", "Eliminar")
        AlertDialog.Builder(this)
            .setTitle("Opciones para ${flor.nombre}")
            .setItems(opciones) { _, which ->
                when (which) {
                    0 -> mostrarDialogoEditarFlor(florTriple)
                    1 -> confirmarEliminarFlor(florTriple)
                }
            }
            .show()
    }

    private fun confirmarEliminarFlor(florTriple: Triple<Int, Int, Flor>) {
        val (florId, _, flor) = florTriple
        AlertDialog.Builder(this)
            .setTitle("Eliminar Flor")
            .setMessage("¿Seguro que quieres eliminar la flor '${flor.nombre}'?")
            .setPositiveButton("Sí") { _, _ ->
                val rowsDeleted = florDao.eliminar(florId)
                if (rowsDeleted > 0) {
                    Toast.makeText(this, "Flor eliminada", Toast.LENGTH_SHORT).show()
                    cargarFlores()
                } else {
                    Toast.makeText(this, "Error al eliminar la flor", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // Muestra un diálogo para seleccionar cuál flor editar, en caso de no pasar una flor directa
    private fun mostrarDialogoEditarFlor() {
        if (listaFlores.isEmpty()) {
            Toast.makeText(this, "No hay flores para editar", Toast.LENGTH_SHORT).show()
            return
        }
        val nombres = listaFlores.map { it.third.nombre }.toTypedArray()
        AlertDialog.Builder(this)
            .setTitle("Selecciona Flor para Editar")
            .setItems(nombres) { _, position ->
                mostrarDialogoEditarFlor(listaFlores[position])
            }
            .show()
    }

    private fun mostrarDialogoEditarFlor(florTriple: Triple<Int, Int, Flor>) {
        val (florId, _, flor) = florTriple
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

                val rowsUpdated = florDao.actualizar(florId, flor, jardinId)
                if (rowsUpdated > 0) {
                    Toast.makeText(this, "Flor actualizada", Toast.LENGTH_SHORT).show()
                    cargarFlores()
                } else {
                    Toast.makeText(this, "Error al actualizar la flor", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // Muestra un diálogo para seleccionar cuál flor eliminar
    private fun mostrarDialogoEliminarFlor() {
        if (listaFlores.isEmpty()) {
            Toast.makeText(this, "No hay flores para eliminar", Toast.LENGTH_SHORT).show()
            return
        }
        val nombres = listaFlores.map { it.third.nombre }.toTypedArray()
        AlertDialog.Builder(this)
            .setTitle("Selecciona Flor para Eliminar")
            .setItems(nombres) { _, position ->
                val florTriple = listaFlores[position]
                val rowsDeleted = florDao.eliminar(florTriple.first)
                if (rowsDeleted > 0) {
                    Toast.makeText(this, "Flor eliminada", Toast.LENGTH_SHORT).show()
                    cargarFlores()
                } else {
                    Toast.makeText(this, "Error al eliminar la flor", Toast.LENGTH_SHORT).show()
                }
            }
            .show()
    }

    private fun mostrarDialogoAgregarFlor() {
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.dialogo_agregar_flor, null)

        val etNombre = view.findViewById<EditText>(R.id.etNombreFlor)
        val etColor = view.findViewById<EditText>(R.id.etColorFlor)
        val etDiametro = view.findViewById<EditText>(R.id.etDiametroFlor)
        val switchFragante = view.findViewById<Switch>(R.id.switchFragante)
        val etTemporada = view.findViewById<EditText>(R.id.etTemporadaFlor)

        AlertDialog.Builder(this)
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
                val newId = florDao.insertar(nuevaFlor, jardinId)
                if (newId > 0) {
                    Toast.makeText(this, "Flor agregada", Toast.LENGTH_SHORT).show()
                    cargarFlores()
                } else {
                    Toast.makeText(this, "Error al agregar la flor", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
