package com.example.deber01

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        // Nombre y versión de la base de datos (incrementa la versión para que se invoque onUpgrade)
        const val DATABASE_NAME = "jardinFlor.db"
        const val DATABASE_VERSION = 2

        // Constantes para la tabla JARDIN
        const val TABLE_JARDIN = "JARDIN"
        const val COLUMN_JARDIN_ID = "id"
        const val COLUMN_JARDIN_NOMBRE = "nombre"
        const val COLUMN_JARDIN_UBICACION = "ubicacion"
        const val COLUMN_JARDIN_FECHA = "fechaCreacion"
        const val COLUMN_JARDIN_TAMANO = "tamano"
        const val COLUMN_JARDIN_SUELLO = "tipoSuelo"
        // Nuevos campos para coordenadas:
        const val COLUMN_JARDIN_LATITUD = "latitud"
        const val COLUMN_JARDIN_LONGITUD = "longitud"

        // Constantes para la tabla FLOR
        const val TABLE_FLOR = "FLOR"
        const val COLUMN_FLOR_ID = "id"
        const val COLUMN_FLOR_NOMBRE = "nombre"
        const val COLUMN_FLOR_COLOR = "color"
        const val COLUMN_FLOR_DIAMETRO = "diametro"
        const val COLUMN_FLOR_FRAGANTE = "fragante"  // se almacenará 0 o 1
        const val COLUMN_FLOR_TEMPORADA = "temporadaFloracion"
        // Relacion: cada flor pertenece a un jardín
        const val COLUMN_FLOR_JARDIN_ID = "jardinId"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // Crear la tabla JARDIN con las nuevas columnas para latitud y longitud
        val createJardinTable = """
            CREATE TABLE $TABLE_JARDIN (
                $COLUMN_JARDIN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_JARDIN_NOMBRE TEXT,
                $COLUMN_JARDIN_UBICACION TEXT,
                $COLUMN_JARDIN_FECHA TEXT,
                $COLUMN_JARDIN_TAMANO REAL,
                $COLUMN_JARDIN_SUELLO TEXT,
                $COLUMN_JARDIN_LATITUD REAL,
                $COLUMN_JARDIN_LONGITUD REAL
            );
        """.trimIndent()
        db?.execSQL(createJardinTable)

        // Crear la tabla FLOR
        val createFlorTable = """
            CREATE TABLE $TABLE_FLOR (
                $COLUMN_FLOR_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_FLOR_NOMBRE TEXT,
                $COLUMN_FLOR_COLOR TEXT,
                $COLUMN_FLOR_DIAMETRO REAL,
                $COLUMN_FLOR_FRAGANTE INTEGER,
                $COLUMN_FLOR_TEMPORADA TEXT,
                $COLUMN_FLOR_JARDIN_ID INTEGER,
                FOREIGN KEY($COLUMN_FLOR_JARDIN_ID) REFERENCES $TABLE_JARDIN($COLUMN_JARDIN_ID)
            );
        """.trimIndent()
        db?.execSQL(createFlorTable)
    }

    // onUpgrade se invoca al aumentar DATABASE_VERSION o al actualizar la aplicación.
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Elimina las tablas antiguas y vuelve a crearlas (en producción se recomienda migrar los datos)
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_FLOR")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_JARDIN")
        onCreate(db)
    }

    // MÉTODOS PARA LA TABLA JARDIN

    // Inserta un nuevo jardín y retorna el ID insertado o -1 en caso de error.
    fun insertarJardin(jardin: Jardin): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_JARDIN_NOMBRE, jardin.nombre)
            put(COLUMN_JARDIN_UBICACION, jardin.ubicacion)
            put(COLUMN_JARDIN_FECHA, jardin.fechaCreacion)
            put(COLUMN_JARDIN_TAMANO, jardin.tamano)
            put(COLUMN_JARDIN_SUELLO, jardin.tipoSuelo)
            put(COLUMN_JARDIN_LATITUD, jardin.latitud)
            put(COLUMN_JARDIN_LONGITUD, jardin.longitud)
        }
        val id = db.insert(TABLE_JARDIN, null, values)
        db.close()
        return id
    }

    // Actualiza un jardín existente, retornando el número de filas afectadas.
    fun actualizarJardin(id: Int, jardin: Jardin): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_JARDIN_NOMBRE, jardin.nombre)
            put(COLUMN_JARDIN_UBICACION, jardin.ubicacion)
            put(COLUMN_JARDIN_FECHA, jardin.fechaCreacion)
            put(COLUMN_JARDIN_TAMANO, jardin.tamano)
            put(COLUMN_JARDIN_SUELLO, jardin.tipoSuelo)
            put(COLUMN_JARDIN_LATITUD, jardin.latitud)
            put(COLUMN_JARDIN_LONGITUD, jardin.longitud)
        }
        val rowsAffected = db.update(TABLE_JARDIN, values, "$COLUMN_JARDIN_ID = ?", arrayOf(id.toString()))
        db.close()
        return rowsAffected
    }

    // Elimina un jardín a partir de su ID y retorna el número de filas eliminadas.
    fun eliminarJardin(id: Int): Int {
        val db = writableDatabase
        val rowsDeleted = db.delete(TABLE_JARDIN, "$COLUMN_JARDIN_ID = ?", arrayOf(id.toString()))
        db.close()
        return rowsDeleted
    }

    // Retorna una lista de parejas (ID, Jardin) con todos los jardines.
    fun obtenerTodosJardines(): MutableList<Pair<Int, Jardin>> {
        val lista = mutableListOf<Pair<Int, Jardin>>()
        val query = "SELECT * FROM $TABLE_JARDIN"
        val db = readableDatabase
        val cursor: Cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_JARDIN_ID))
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_JARDIN_NOMBRE))
                val ubicacion = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_JARDIN_UBICACION))
                val fecha = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_JARDIN_FECHA))
                val tamano = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_JARDIN_TAMANO))
                val suelo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_JARDIN_SUELLO))
                val latitud = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_JARDIN_LATITUD))
                val longitud = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_JARDIN_LONGITUD))
                val jardin = Jardin(nombre, ubicacion, fecha, tamano, suelo, latitud, longitud)
                lista.add(Pair(id, jardin))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return lista
    }

    // MÉTODOS PARA LA TABLA FLOR

    // Inserta una nueva flor asociada a un jardín y retorna el ID insertado o -1 en caso de error.
    fun insertarFlor(flor: Flor, jardinId: Int): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_FLOR_NOMBRE, flor.nombre)
            put(COLUMN_FLOR_COLOR, flor.color)
            put(COLUMN_FLOR_DIAMETRO, flor.diametro)
            put(COLUMN_FLOR_FRAGANTE, if (flor.fragante) 1 else 0)
            put(COLUMN_FLOR_TEMPORADA, flor.temporadaFloracion)
            put(COLUMN_FLOR_JARDIN_ID, jardinId)
        }
        val id = db.insert(TABLE_FLOR, null, values)
        db.close()
        return id
    }

    // Actualiza una flor existente, retornando el número de filas afectadas.
    fun actualizarFlor(id: Int, flor: Flor, jardinId: Int): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_FLOR_NOMBRE, flor.nombre)
            put(COLUMN_FLOR_COLOR, flor.color)
            put(COLUMN_FLOR_DIAMETRO, flor.diametro)
            put(COLUMN_FLOR_FRAGANTE, if (flor.fragante) 1 else 0)
            put(COLUMN_FLOR_TEMPORADA, flor.temporadaFloracion)
            put(COLUMN_FLOR_JARDIN_ID, jardinId)
        }
        val rowsAffected = db.update(TABLE_FLOR, values, "$COLUMN_FLOR_ID = ?", arrayOf(id.toString()))
        db.close()
        return rowsAffected
    }

    // Elimina una flor a partir de su ID y retorna el número de filas eliminadas.
    fun eliminarFlor(id: Int): Int {
        val db = writableDatabase
        val rowsDeleted = db.delete(TABLE_FLOR, "$COLUMN_FLOR_ID = ?", arrayOf(id.toString()))
        db.close()
        return rowsDeleted
    }

    // Retorna una lista de triples (ID de la flor, ID del jardín asociado, Flor) con todas las flores.
    fun obtenerTodasFlores(): MutableList<Triple<Int, Int, Flor>> {
        val lista = mutableListOf<Triple<Int, Int, Flor>>()
        val query = "SELECT * FROM $TABLE_FLOR"
        val db = readableDatabase
        val cursor: Cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FLOR_ID))
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FLOR_NOMBRE))
                val color = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FLOR_COLOR))
                val diametro = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_FLOR_DIAMETRO))
                val fragante = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FLOR_FRAGANTE)) == 1
                val temporada = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FLOR_TEMPORADA))
                val jardinId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FLOR_JARDIN_ID))
                val flor = Flor(nombre, color, diametro, fragante, temporada)
                lista.add(Triple(id, jardinId, flor))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return lista
    }

    // Retorna una lista de triples (ID de la flor, ID del jardín, Flor) filtradas por el ID del jardín
    fun obtenerFloresPorJardin(jardinId: Int): MutableList<Triple<Int, Int, Flor>> {
        val lista = mutableListOf<Triple<Int, Int, Flor>>()
        val query = "SELECT * FROM $TABLE_FLOR WHERE $COLUMN_FLOR_JARDIN_ID = ?"
        val db = readableDatabase
        val cursor: Cursor = db.rawQuery(query, arrayOf(jardinId.toString()))
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FLOR_ID))
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FLOR_NOMBRE))
                val color = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FLOR_COLOR))
                val diametro = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_FLOR_DIAMETRO))
                val fragante = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FLOR_FRAGANTE)) == 1
                val temporada = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FLOR_TEMPORADA))
                val flor = Flor(nombre, color, diametro, fragante, temporada)
                lista.add(Triple(id, jardinId, flor))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return lista
    }
}
