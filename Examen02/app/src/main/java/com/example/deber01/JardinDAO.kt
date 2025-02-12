package com.example.deber01

import android.content.Context

class JardinDAO(context: Context) {

    // Instanciamos nuestro DatabaseHelper
    private val dbHelper = DatabaseHelper(context)

    /**
     * Inserta un nuevo jardín en la base de datos.
     *
     * @param jardin Objeto Jardin que se desea insertar.
     * @return El id del registro insertado o -1 en caso de error.
     */
    fun insertar(jardin: Jardin): Long {
        return dbHelper.insertarJardin(jardin)
    }

    /**
     * Actualiza un jardín existente.
     *
     * @param id El identificador del jardín a actualizar.
     * @param jardin Objeto Jardin con los nuevos datos.
     * @return La cantidad de registros actualizados (idealmente 1 o 0 si no se encontró el registro).
     */
    fun actualizar(id: Int, jardin: Jardin): Int {
        return dbHelper.actualizarJardin(id, jardin)
    }

    /**
     * Elimina un jardín de la base de datos.
     *
     * @param id El identificador del jardín a eliminar.
     * @return La cantidad de registros eliminados (idealmente 1 o 0 si no se encontró el registro).
     */
    fun eliminar(id: Int): Int {
        return dbHelper.eliminarJardin(id)
    }

    /**
     * Obtiene todos los jardines almacenados en la base de datos.
     *
     * @return Una lista de pares, donde el primer valor es el id del jardín y el segundo el objeto Jardin.
     */
    fun obtenerTodos(): List<Pair<Int, Jardin>> {
        return dbHelper.obtenerTodosJardines()
    }

    /**
     * (Opcional) Obtiene un jardín específico a partir de su id.
     *
     * @param id El identificador del jardín.
     * @return El objeto Jardin correspondiente o null si no se encuentra.
     */
    fun obtenerPorId(id: Int): Jardin? {
        val jardines = dbHelper.obtenerTodosJardines()
        // Buscamos en la lista el jardín cuyo id coincida
        return jardines.find { it.first == id }?.second
    }
}
