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

    private lateinit var listaJardines: MutableList<Jardin>
    private lateinit var adapter: JardinAdapter
    private lateinit var recyclerViewJardines: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jardin)

        // Inicializar lista de jardines
        listaJardines = mutableListOf(
            Jardin("Jardín Peonías", "Nayón", "2021-05-12", 50.0, "Húmedo"),
            Jardin("Jardín Rosas", "Cumbayá", "2020-08-20", 30.0, "Arenoso")
        )

        recyclerViewJardines = findViewById(R.id.recyclerViewJardines) // 🔹 Inicialización correcta
        recyclerViewJardines.layoutManager = LinearLayoutManager(this)
        recyclerViewJardines.visibility = View.GONE // 🔹 Inicialmente oculto hasta presionar "Ver Jardines"

        adapter = JardinAdapter(this, listaJardines) { jardinSeleccionado ->
            mostrarDialogoOpciones(jardinSeleccionado)
        }
        recyclerViewJardines.adapter = adapter

        val btnAgregarJardin: Button = findViewById(R.id.btnAgregarJardin)
        val btnVerJardines: Button = findViewById(R.id.btnVerJardines)
        val btnEditarJardin: Button = findViewById(R.id.btnEditarJardin)
        val btnEliminarJardin: Button = findViewById(R.id.btnEliminarJardin)

        // Eventos de botones
        btnAgregarJardin.setOnClickListener { mostrarDialogoAgregarJardin() }
        btnVerJardines.setOnClickListener { verJardines() }
        btnEditarJardin.setOnClickListener { mostrarDialogoEditarJardin() }
        btnEliminarJardin.setOnClickListener { mostrarDialogoEliminarJardin() }
    }

    private fun verJardines() {
        recyclerViewJardines.visibility = View.VISIBLE // 🔹 Mostrar lista de jardines
        adapter.notifyDataSetChanged()
    }

    private fun mostrarDialogoOpciones(jardin: Jardin) {
        val opciones = arrayOf("Editar", "Eliminar")

        AlertDialog.Builder(this)
            .setTitle("Opciones para ${jardin.nombre}")
            .setItems(opciones) { _, which ->
                when (which) {
                    0 -> mostrarDialogoEditarJardin(jardin) // Editar
                    1 -> confirmarEliminarJardin(jardin)  // Eliminar
                }
            }
            .show()
    }

    private fun confirmarEliminarJardin(jardin: Jardin) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Jardín")
            .setMessage("¿Estás seguro de eliminar el jardín '${jardin.nombre}'?")
            .setPositiveButton("Sí") { _, _ ->
                listaJardines.remove(jardin)
                adapter.notifyDataSetChanged()
                Toast.makeText(this, "Jardín eliminado", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

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
                listaJardines.add(nuevoJardin)
                adapter.notifyDataSetChanged()
                Toast.makeText(this, "Jardín agregado", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.show()
    }

    private fun mostrarDialogoEditarJardin() {
        if (listaJardines.isEmpty()) {
            Toast.makeText(this, "No hay jardines para editar", Toast.LENGTH_SHORT).show()
            return
        }

        val nombres = listaJardines.map { it.nombre }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Seleccionar Jardín para Editar")
            .setItems(nombres) { _, position ->
                mostrarDialogoEditarJardin(listaJardines[position])
            }
            .show()
    }

    private fun mostrarDialogoEditarJardin(jardin: Jardin) {
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
                jardin.ubicacion = etUbicacion.text.toString()
                jardin.fechaCreacion = etFecha.text.toString()
                jardin.tamano = etTamano.text.toString().toDoubleOrNull() ?: 0.0
                jardin.tipoSuelo = etTipoSuelo.text.toString()
                adapter.notifyDataSetChanged()
                Toast.makeText(this, "Jardín actualizado", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .create()
            .show()
    }

    private fun mostrarDialogoEliminarJardin() {
        if (listaJardines.isEmpty()) {
            Toast.makeText(this, "No hay jardines para eliminar", Toast.LENGTH_SHORT).show()
            return
        }

        val nombres = listaJardines.map { it.nombre }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Seleccionar Jardín para Eliminar")
            .setItems(nombres) { _, position ->
                listaJardines.removeAt(position)
                adapter.notifyDataSetChanged()
                Toast.makeText(this, "Jardín eliminado", Toast.LENGTH_SHORT).show()
            }
            .show()
    }
}
