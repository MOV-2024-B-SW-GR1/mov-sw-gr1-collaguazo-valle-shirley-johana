package com.example.deber01

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class JardinActivity : AppCompatActivity() {

    // Lista de jardines, cada elemento es un Pair(id, Jardin)
    private lateinit var listaJardines: MutableList<Pair<Int, Jardin>>
    private lateinit var adapter: JardinAdapter
    private lateinit var recyclerViewJardines: RecyclerView
    private lateinit var jardinDao: JardinDAO

    // Variable para almacenar el jardín seleccionado (para usarlo al ver el mapa)
    private var jardinActual: Pair<Int, Jardin>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jardin)

        // Inicializamos el DAO que utiliza DatabaseHelper
        jardinDao = JardinDAO(this)

        // Configuramos el RecyclerView
        recyclerViewJardines = findViewById(R.id.recyclerViewJardines)
        recyclerViewJardines.layoutManager = LinearLayoutManager(this)
        recyclerViewJardines.visibility = View.GONE  // Inicialmente oculto

        // Cargamos los datos desde SQLite
        cargarJardines()

        // Inicializamos el adaptador; al hacer clic en un jardín se guarda en jardinActual y se muestran las opciones
        adapter = JardinAdapter(this, listaJardines) { jardinPair ->
            jardinActual = jardinPair
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
            cargarJardines()
            recyclerViewJardines.visibility = View.VISIBLE
            adapter.notifyDataSetChanged()
        }
        btnEditarJardin.setOnClickListener { mostrarDialogoEditarJardin() }
        btnEliminarJardin.setOnClickListener { mostrarDialogoEliminarJardin() }
    }

    // Carga la lista de jardines desde la base de datos
    private fun cargarJardines() {
        listaJardines = jardinDao.obtenerTodos().toMutableList()
        if (::adapter.isInitialized) {
            adapter.updateData(listaJardines)
        }
    }

    // Muestra un diálogo con las opciones: Editar, Eliminar y Ver mapa para el jardín seleccionado
    private fun mostrarDialogoOpciones(jardinPair: Pair<Int, Jardin>) {
        val (_, jardin) = jardinPair
        val opciones = arrayOf("Editar", "Eliminar", "Ver mapa")
        AlertDialog.Builder(this)
            .setTitle("Opciones para ${jardin.nombre}")
            .setItems(opciones) { _, which ->
                when (which) {
                    0 -> mostrarDialogoEditarJardin(jardinPair)
                    1 -> confirmarEliminarJardin(jardinPair)
                    2 -> verMapa(jardin)
                }
            }
            .show()
    }

    // Función que lanza MapActivity pasando las coordenadas del jardín
    private fun verMapa(jardin: Jardin) {
        if (jardin.latitud != 0.0 && jardin.longitud != 0.0) {
            val intent = Intent(this, MapActivity::class.java)
            intent.putExtra("latitud", jardin.latitud)
            intent.putExtra("longitud", jardin.longitud)
            intent.putExtra("nombreJardin", jardin.nombre)
            startActivity(intent)
        } else {
            Toast.makeText(this, "El jardín no tiene coordenadas definidas", Toast.LENGTH_SHORT).show()
        }
    }

    // Confirma y elimina el jardín seleccionado
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

    // Diálogo para agregar un nuevo jardín; calcula latitud y longitud según la ubicación ingresada
    private fun mostrarDialogoAgregarJardin() {
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.dialogo_agregar_jardin, null)

        val etNombre = view.findViewById<EditText>(R.id.etNombreJardin)
        val etUbicacion = view.findViewById<EditText>(R.id.etUbicacion)
        val etFecha = view.findViewById<EditText>(R.id.etFechaCreacion)
        val etTamano = view.findViewById<EditText>(R.id.etTamano)
        val etTipoSuelo = view.findViewById<EditText>(R.id.etTipoSuelo)

        AlertDialog.Builder(this)
            .setTitle("Agregar Jardín")
            .setView(view)
            .setPositiveButton("Guardar") { _, _ ->
                val nombre = etNombre.text.toString()
                // Convertir la ubicación a minúsculas y eliminar espacios al inicio y final
                val ubicacionTexto = etUbicacion.text.toString().toLowerCase().trim()
                val fecha = etFecha.text.toString()
                val tamano = etTamano.text.toString().toDoubleOrNull() ?: 0.0
                val tipoSuelo = etTipoSuelo.text.toString()

                // Asignar coordenadas según el valor de ubicación
                val (lat, lon) = when (ubicacionTexto) {
                    "nayon" -> Pair(-0.1620081826918916, -78.43737278894837)
                    "recreo" -> Pair(-0.235854452395613, -78.51572090475261)
                    "quitumbe" -> Pair(-0.28763229699768506, -78.54753304523491)
                    else -> Pair(0.0, 0.0)
                }

                val nuevoJardin = Jardin(nombre, ubicacionTexto, fecha, tamano, tipoSuelo, lat, lon)
                val newId = jardinDao.insertar(nuevoJardin)
                if (newId > 0) {
                    Toast.makeText(this, "Jardín agregado", Toast.LENGTH_SHORT).show()
                    cargarJardines()
                } else {
                    Toast.makeText(this, "Error al agregar jardín", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // Diálogo para seleccionar un jardín a editar (si no se pasa uno directamente)
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

    // Diálogo para editar el jardín; recalcula las coordenadas según el valor actualizado de ubicación
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
                jardin.nombre = etNombre.text.toString()
                val ubicacionTexto = etUbicacion.text.toString().toLowerCase().trim()
                jardin.ubicacion = ubicacionTexto
                jardin.fechaCreacion = etFecha.text.toString()
                jardin.tamano = etTamano.text.toString().toDoubleOrNull() ?: 0.0
                jardin.tipoSuelo = etTipoSuelo.text.toString()

                // Recalcular coordenadas basadas en la ubicación actualizada
                val (lat, lon) = when (ubicacionTexto) {
                    "nayon" -> Pair(-0.1620081826918916, -78.43737278894837)
                    "recreo" -> Pair(-0.235854452395613, -78.51572090475261)
                    "quitumbe" -> Pair(-0.28763229699768506, -78.54753304523491)
                    else -> Pair(0.0, 0.0)
                }
                jardin.latitud = lat
                jardin.longitud = lon

                val rowsAffected = jardinDao.actualizar(id, jardin)
                if (rowsAffected > 0) {
                    Toast.makeText(this, "Jardín actualizado", Toast.LENGTH_SHORT).show()
                    cargarJardines()
                } else {
                    Toast.makeText(this, "Error al actualizar jardín", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // Diálogo para eliminar un jardín seleccionándolo de una lista
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
