package com.example.deber01

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class JardinActivity : AppCompatActivity() {

    // Ahora la lista contiene pares: (id, objeto Jardin)
    private lateinit var listaJardines: MutableList<Pair<Int, Jardin>>
    private lateinit var adapter: JardinAdapter
    private lateinit var recyclerViewJardines: RecyclerView
    private lateinit var jardinDao: JardinDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jardin)

        // Inicializamos el DAO (que usa DatabaseHelper internamente)
        jardinDao = JardinDAO(this)

        // Configuramos el RecyclerView
        recyclerViewJardines = findViewById(R.id.recyclerViewJardines)
        recyclerViewJardines.layoutManager = LinearLayoutManager(this)
        recyclerViewJardines.visibility = View.GONE  // Se muestra al presionar "Ver Jardines"

        // Cargamos los jardines desde SQLite
        cargarJardines()

        // Inicializamos el adaptador. Se asume que el adaptador ha sido modificado para trabajar con Pair<Int, Jardin>
        adapter = JardinAdapter(this, listaJardines) { jardinPair ->
            mostrarDialogoOpciones(jardinPair)
        }
        recyclerViewJardines.adapter = adapter

        // Referencias a botones
        val btnAgregarJardin: Button = findViewById(R.id.btnAgregarJardin)
        val btnVerJardines: Button = findViewById(R.id.btnVerJardines)
        val btnEditarJardin: Button = findViewById(R.id.btnEditarJardin)
        val btnEliminarJardin: Button = findViewById(R.id.btnEliminarJardin)

        // Eventos de botones
        btnAgregarJardin.setOnClickListener { mostrarDialogoAgregarJardin() }
        btnVerJardines.setOnClickListener {
            cargarJardines()  // Refrescar la lista desde la BD
            recyclerViewJardines.visibility = View.VISIBLE
            adapter.notifyDataSetChanged()
        }
        btnEditarJardin.setOnClickListener { mostrarDialogoEditarJardin() }
        btnEliminarJardin.setOnClickListener { mostrarDialogoEliminarJardin() }
    }

    // Función para cargar la lista de jardines desde la base de datos
    private fun cargarJardines() {
        listaJardines = jardinDao.obtenerTodos().toMutableList()
        // Si el adaptador ya fue inicializado, actualizamos su data.
        if (::adapter.isInitialized) {
            adapter.updateData(listaJardines)
        }
    }

    // Muestra un diálogo con las opciones (Editar o Eliminar) para el jardín seleccionado.
    private fun mostrarDialogoOpciones(jardinPair: Pair<Int, Jardin>) {
        val (id, jardin) = jardinPair
        val opciones = arrayOf("Editar", "Eliminar")
        AlertDialog.Builder(this)
            .setTitle("Opciones para ${jardin.nombre}")
            .setItems(opciones) { _, which ->
                when (which) {
                    0 -> mostrarDialogoEditarJardin(jardinPair)
                    1 -> confirmarEliminarJardin(jardinPair)
                }
            }
            .show()
    }

    // Pregunta al usuario si desea eliminar el jardín y, de confirmar, lo elimina en la BD.
    private fun confirmarEliminarJardin(jardinPair: Pair<Int, Jardin>) {
        val (id, jardin) = jardinPair
        AlertDialog.Builder(this)
            .setTitle("Eliminar Jardín")
            .setMessage("¿Estás seguro de eliminar el jardín '${jardin.nombre}'?")
            .setPositiveButton("Sí") { _, _ ->
                val rowsDeleted = jardinDao.eliminar(id)
                if (rowsDeleted > 0) {
                    Toast.makeText(this, "Jardín eliminado", Toast.LENGTH_SHORT).show()
                    cargarJardines()
                } else {
                    Toast.makeText(this, "Error al eliminar jardín", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // Muestra el diálogo para agregar un nuevo jardín y lo inserta en la BD.
    private fun mostrarDialogoAgregarJardin() {
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.dialogo_agregar_jardin, null)

        val etNombre = view.findViewById<EditText>(R.id.etNombreJardin)
        val etUbicacion = view.findViewById<EditText>(R.id.etUbicacion)
        val etFecha = view.findViewById<EditText>(R.id.etFechaCreacion)
        val etTamano = view.findViewById<EditText>(R.id.etTamano)
        val etTipoSuelo = view.findViewById<EditText>(R.id.etTipoSuelo)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Agregar Jardín")
            .setView(view)
            .setPositiveButton("Guardar") { _, _ ->
                val nuevoJardin = Jardin(
                    etNombre.text.toString(),
                    etUbicacion.text.toString(),
                    etFecha.text.toString(),
                    etTamano.text.toString().toDoubleOrNull() ?: 0.0,
                    etTipoSuelo.text.toString()
                )
                val newId = jardinDao.insertar(nuevoJardin)
                if (newId > 0) {
                    Toast.makeText(this, "Jardín agregado", Toast.LENGTH_SHORT).show()
                    cargarJardines()
                } else {
                    Toast.makeText(this, "Error al agregar jardín", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.show()
    }

    // Muestra un diálogo para que el usuario seleccione un jardín de la lista para editar.
    private fun mostrarDialogoEditarJardin() {
        if (listaJardines.isEmpty()) {
            Toast.makeText(this, "No hay jardines para editar", Toast.LENGTH_SHORT).show()
            return
        }

        val nombres = listaJardines.map { it.second.nombre }.toTypedArray()
        AlertDialog.Builder(this)
            .setTitle("Seleccionar Jardín para Editar")
            .setItems(nombres) { _, position ->
                mostrarDialogoEditarJardin(listaJardines[position])
            }
            .show()
    }

    // Muestra el diálogo de edición precargado con los datos del jardín seleccionado.
    private fun mostrarDialogoEditarJardin(jardinPair: Pair<Int, Jardin>) {
        val (id, jardin) = jardinPair
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.dialogo_agregar_jardin, null)

        val etNombre = view.findViewById<EditText>(R.id.etNombreJardin)
        val etUbicacion = view.findViewById<EditText>(R.id.etUbicacion)
        val etFecha = view.findViewById<EditText>(R.id.etFechaCreacion)
        val etTamano = view.findViewById<EditText>(R.id.etTamano)
        val etTipoSuelo = view.findViewById<EditText>(R.id.etTipoSuelo)

        etNombre.setText(jardin.nombre)
        etUbicacion.setText(jardin.ubicacion)
        etFecha.setText(jardin.fechaCreacion)
        etTamano.setText(jardin.tamano.toString())
        etTipoSuelo.setText(jardin.tipoSuelo)

        AlertDialog.Builder(this)
            .setTitle("Editar Jardín")
            .setView(view)
            .setPositiveButton("Guardar") { _, _ ->
                // Actualizamos los datos del objeto
                jardin.nombre = etNombre.text.toString()
                jardin.ubicacion = etUbicacion.text.toString()
                jardin.fechaCreacion = etFecha.text.toString()
                jardin.tamano = etTamano.text.toString().toDoubleOrNull() ?: 0.0
                jardin.tipoSuelo = etTipoSuelo.text.toString()

                val rowsUpdated = jardinDao.actualizar(id, jardin)
                if (rowsUpdated > 0) {
                    Toast.makeText(this, "Jardín actualizado", Toast.LENGTH_SHORT).show()
                    cargarJardines()
                } else {
                    Toast.makeText(this, "Error al actualizar jardín", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .create()
            .show()
    }

    // Muestra un diálogo para que el usuario seleccione un jardín para eliminar.
    private fun mostrarDialogoEliminarJardin() {
        if (listaJardines.isEmpty()) {
            Toast.makeText(this, "No hay jardines para eliminar", Toast.LENGTH_SHORT).show()
            return
        }

        val nombres = listaJardines.map { it.second.nombre }.toTypedArray()
        AlertDialog.Builder(this)
            .setTitle("Seleccionar Jardín para Eliminar")
            .setItems(nombres) { _, position ->
                val jardinPair = listaJardines[position]
                val rowsDeleted = jardinDao.eliminar(jardinPair.first)
                if (rowsDeleted > 0) {
                    Toast.makeText(this, "Jardín eliminado", Toast.LENGTH_SHORT).show()
                    cargarJardines()
                } else {
                    Toast.makeText(this, "Error al eliminar jardín", Toast.LENGTH_SHORT).show()
                }
            }
            .show()
    }
}
