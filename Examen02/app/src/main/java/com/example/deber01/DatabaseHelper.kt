package com.example.deber01

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        // Nombre y versión de la base de datos
        const val DATABASE_NAME = "jardinFlor.db"
        const val DATABASE_VERSION = 1

        // Constantes para la tabla JARDIN
        const val TABLE_JARDIN = "JARDIN"
        const val COLUMN_JARDIN_ID = "id"
        const val COLUMN_JARDIN_NOMBRE = "nombre"
        const val COLUMN_JARDIN_UBICACION = "ubicacion"
        const val COLUMN_JARDIN_FECHA = "fechaCreacion"
        const val COLUMN_JARDIN_TAMANO = "tamano"
        const val COLUMN_JARDIN_SUELLO = "tipoSuelo"

        // Constantes para la tabla FLOR
        const val TABLE_FLOR = "FLOR"
        const val COLUMN_FLOR_ID = "id"
        const val COLUMN_FLOR_NOMBRE = "nombre"
        const val COLUMN_FLOR_COLOR = "color"
        const val COLUMN_FLOR_DIAMETRO = "diametro"
        const val COLUMN_FLOR_FRAGANTE = "fragante" // Se almacena 0 (false) o 1 (true)
        const val COLUMN_FLOR_TEMPORADA = "temporadaFloracion"
        const val COLUMN_FLOR_JARDIN_ID = "jardinId" // Relaciona la flor con su jardín
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // Creación de la tabla JARDIN
        val crearTablaJardin = """
            CREATE TABLE $TABLE_JARDIN (
                $COLUMN_JARDIN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_JARDIN_NOMBRE TEXT,
                $COLUMN_JARDIN_UBICACION TEXT,
                $COLUMN_JARDIN_FECHA TEXT,
                $COLUMN_JARDIN_TAMANO REAL,
                $COLUMN_JARDIN_SUELLO TEXT
            );
        """.trimIndent()
        db?.execSQL(crearTablaJardin)

        // Creación de la tabla FLOR
        val crearTablaFlor = """
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
        db?.execSQL(crearTablaFlor)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Para efectos de desarrollo se eliminan y recrean las tablas
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_FLOR")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_JARDIN")
        onCreate(db)
    }

    //////////////// MÉTODOS PARA JARDINES ////////////////

    // Insertar un nuevo jardín.
    // Retorna el id del nuevo registro o -1 en caso de error.
    fun insertarJardin(jardin: Jardin): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_JARDIN_NOMBRE, jardin.nombre)
            put(COLUMN_JARDIN_UBICACION, jardin.ubicacion)
            put(COLUMN_JARDIN_FECHA, jardin.fechaCreacion)
            put(COLUMN_JARDIN_TAMANO, jardin.tamano)
            put(COLUMN_JARDIN_SUELLO, jardin.tipoSuelo)
        }
        val id = db.insert(TABLE_JARDIN, null, values)
        db.close()
        return id
    }

    // Actualizar un jardín existente. Se requiere el id del registro a actualizar.
    // Retorna la cantidad de registros actualizados (idealmente 1 o 0 si no se encontró).
    fun actualizarJardin(id: Int, jardin: Jardin): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_JARDIN_NOMBRE, jardin.nombre)
            put(COLUMN_JARDIN_UBICACION, jardin.ubicacion)
            put(COLUMN_JARDIN_FECHA, jardin.fechaCreacion)
            put(COLUMN_JARDIN_TAMANO, jardin.tamano)
            put(COLUMN_JARDIN_SUELLO, jardin.tipoSuelo)
        }
        val rowsAffected = db.update(TABLE_JARDIN, values, "$COLUMN_JARDIN_ID = ?", arrayOf(id.toString()))
        db.close()
        return rowsAffected
    }

    // Eliminar un jardín a partir de su id.
    // Retorna la cantidad de registros eliminados.
    fun eliminarJardin(id: Int): Int {
        val db = writableDatabase
        val rowsDeleted = db.delete(TABLE_JARDIN, "$COLUMN_JARDIN_ID = ?", arrayOf(id.toString()))
        db.close()
        return rowsDeleted
    }

    // Obtener todos los jardines.
    // Se retorna una lista de pares (id, Jardin) para conservar el identificador.
    fun obtenerTodosJardines(): MutableList<Pair<Int, Jardin>> {
        val lista = mutableListOf<Pair<Int, Jardin>>()
        val selectQuery = "SELECT * FROM $TABLE_JARDIN"
        val db = readableDatabase
        val cursor: Cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_JARDIN_ID))
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_JARDIN_NOMBRE))
                val ubicacion = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_JARDIN_UBICACION))
                val fecha = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_JARDIN_FECHA))
                val tamano = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_JARDIN_TAMANO))
                val suelo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_JARDIN_SUELLO))
                val jardin = Jardin(nombre, ubicacion, fecha, tamano, suelo)
                lista.add(Pair(id, jardin))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return lista
    }

    //////////////// MÉTODOS PARA FLORES ////////////////

    // Insertar una nueva flor.
    // Se recibe el objeto Flor y el id del jardín al que pertenece.
    // Retorna el id del nuevo registro o -1 en caso de error.
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

    // Actualizar una flor existente.
    // Se requiere el id de la flor y el id del jardín (en caso se desee actualizar la asociación).
    // Retorna la cantidad de registros actualizados.
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

    // Eliminar una flor a partir de su id.
    // Retorna la cantidad de registros eliminados.
    fun eliminarFlor(id: Int): Int {
        val db = writableDatabase
        val rowsDeleted = db.delete(TABLE_FLOR, "$COLUMN_FLOR_ID = ?", arrayOf(id.toString()))
        db.close()
        return rowsDeleted
    }

    // Obtener todas las flores (sin filtrar por jardín).
    // Se retorna una lista de Triple: (id de la flor, id del jardín asociado, objeto Flor)
    fun obtenerTodasFlores(): MutableList<Triple<Int, Int, Flor>> {
        val lista = mutableListOf<Triple<Int, Int, Flor>>()
        val selectQuery = "SELECT * FROM $TABLE_FLOR"
        val db = readableDatabase
        val cursor: Cursor = db.rawQuery(selectQuery, null)
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

    // Obtener las flores asociadas a un jardín en particular.
    // Se retorna una lista de Triple: (id de la flor, id del jardín, objeto Flor)
    fun obtenerFloresPorJardin(jardinId: Int): MutableList<Triple<Int, Int, Flor>> {
        val lista = mutableListOf<Triple<Int, Int, Flor>>()
        val selectQuery = "SELECT * FROM $TABLE_FLOR WHERE $COLUMN_FLOR_JARDIN_ID = ?"
        val db = readableDatabase
        val cursor: Cursor = db.rawQuery(selectQuery, arrayOf(jardinId.toString()))
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
