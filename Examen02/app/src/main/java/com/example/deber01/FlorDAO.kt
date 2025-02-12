package com.example.deber01

import android.content.Context

class FlorDAO(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    // Inserta una nueva flor asociándola a un jardín (usando el ID del jardín)
    fun insertar(flor: Flor, jardinId: Int): Long {
        return dbHelper.insertarFlor(flor, jardinId)
    }

    // Actualiza una flor existente (requiere el ID de la flor, los nuevos datos y el ID del jardín asociado)
    fun actualizar(id: Int, flor: Flor, jardinId: Int): Int {
        return dbHelper.actualizarFlor(id, flor, jardinId)
    }

    // Elimina una flor a partir de su ID
    fun eliminar(id: Int): Int {
        return dbHelper.eliminarFlor(id)
    }

    // Obtiene todas las flores (sin filtrar por jardín)
    fun obtenerTodas(): List<Triple<Int, Int, Flor>> {
        return dbHelper.obtenerTodasFlores()
    }

    // Obtiene las flores asociadas a un jardín en particular
    fun obtenerPorJardin(jardinId: Int): List<Triple<Int, Int, Flor>> {
        return dbHelper.obtenerFloresPorJardin(jardinId)
    }
}
