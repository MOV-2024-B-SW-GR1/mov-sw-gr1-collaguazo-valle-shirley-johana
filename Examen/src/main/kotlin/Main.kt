package org.example

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import java.io.File

fun main() {
    val jardines = cargarDatos<Jardin>("jardines.txt")

    inicializarArchivo("jardines.txt")
    while (true) {
        println("\n**********Menú Principal:**********")
        println("1. CRUD Jardines")
        println("2. CRUD Flores en un Jardín")
        println("3. Salir")
        println("**********************************")
        print("Seleccione una opción: ")


        when (readLine()?.toIntOrNull()) {
            1 -> crudJardines(jardines)
            2 -> crudFloresEnJardin(jardines)
            3 -> {
                guardarDatos(jardines, "jardines.txt")
                println("Datos guardados. Saliendo del programa.")
                break
            }
            else -> println("Opción inválida.")
        }
    }
}

fun inicializarArchivo(archivo: String) {
    val file = File(archivo)
    if (!file.exists()) {
        file.createNewFile()
        println("Archivo creado: $archivo")
    }
}

inline fun <reified T> cargarDatos(archivo: String): MutableList<T> {
    val file = File(archivo)
    return if (file.exists() && file.readText().isNotBlank()) {
        Json.decodeFromString(file.readText())
    } else {
        mutableListOf()
    }
}

inline fun <reified T> guardarDatos(lista: List<T>, archivo: String) {
    val file = File(archivo)
    file.writeText(Json.encodeToString(lista))
}

// CRUD para Jardines
fun crudJardines(jardines: MutableList<Jardin>) {
    while (true) {
        println("******************************")
        println("\nGestión de Jardines:")
        println("1. Crear Jardín")
        println("2. Leer Jardines")
        println("3. Actualizar Jardín")
        println("4. Eliminar Jardín")
        println("5. Volver al Menú Principal")
        println("******************************")
        print("Seleccione una opción: ")


        when (readLine()?.toIntOrNull()) {
            1 -> crearJardin(jardines)
            2 -> leerJardines(jardines)
            3 -> actualizarJardin(jardines)
            4 -> eliminarJardin(jardines)
            5 -> break
            else -> println("Opción inválida.")
        }
    }
}

fun crearJardin(jardines: MutableList<Jardin>) {
    println("Ingrese el nombre del jardín:")
    val nombre = readLine()!!

    println("Ingrese la ubicación del jardín:")
    val ubicacion = readLine()!!

    println("Ingrese la fecha de creación del jardín (YYYY-MM-DD):")
    val fechaCreacion = readLine()!!

    println("Ingrese el tamaño del jardín (m²):")
    val tamano = readLine()!!.toDouble()

    println("Ingrese el tipo de suelo del jardín:")
    val tipoSuelo = readLine()!!

    val nuevoJardin = Jardin(
        id = if (jardines.isEmpty()) 1 else jardines.maxOf { it.id } + 1,
        nombre = nombre,
        ubicacion = ubicacion,
        fechaCreacion = fechaCreacion,
        tamano = tamano,
        tipoSuelo = tipoSuelo
    )

    jardines.add(nuevoJardin)
    guardarDatos(jardines, "jardines.txt")
    println("Jardín creado con éxito.")
}

fun leerJardines(jardines: List<Jardin>) {
    if (jardines.isEmpty()) {
        println("No hay jardines registrados.")
        return
    }

    jardines.forEach { jardin ->
        println("\nJardín ID: ${jardin.id}")
        println("Nombre: ${jardin.nombre}")
        println("Ubicación: ${jardin.ubicacion}")
        println("Fecha de creación: ${jardin.fechaCreacion}")
        println("Tamaño: ${jardin.tamano} m²")
        println("Tipo de suelo: ${jardin.tipoSuelo}")
        println("Cantidad de flores: ${jardin.flores.size}")
    }
}

fun actualizarJardin(jardines: MutableList<Jardin>) {
    println("Ingrese el ID del jardín a actualizar:")
    val id = readLine()!!.toInt()

    val jardin = jardines.find { it.id == id }
    if (jardin != null) {
        println("Ingrese el nuevo nombre del jardín (${jardin.nombre}):")
        jardin.nombre = readLine()!!

        println("Ingrese la nueva ubicación (${jardin.ubicacion}):")
        jardin.ubicacion = readLine()!!

        println("Ingrese la nueva fecha de creación (${jardin.fechaCreacion}):")
        jardin.fechaCreacion = readLine()!!

        println("Ingrese el nuevo tamaño (${jardin.tamano}):")
        jardin.tamano = readLine()!!.toDouble()

        println("Ingrese el nuevo tipo de suelo (${jardin.tipoSuelo}):")
        jardin.tipoSuelo = readLine()!!

        guardarDatos(jardines, "jardines.txt")
        println("Jardín actualizado con éxito.")
    } else {
        println("Jardín no encontrado.")
    }
}

fun eliminarJardin(jardines: MutableList<Jardin>) {
    println("Ingrese el ID del jardín a eliminar:")
    val id = readLine()!!.toInt()

    val jardin = jardines.find { it.id == id }
    if (jardin != null) {
        jardines.remove(jardin)
        guardarDatos(jardines, "jardines.txt")
        println("Jardín eliminado con éxito.")
    } else {
        println("Jardín no encontrado.")
    }
}

// CRUD para Flores en un Jardín
fun crudFloresEnJardin(jardines: MutableList<Jardin>) {
    if (jardines.isEmpty()) {
        println("No hay jardines registrados. Primero cree un jardín.")
        return
    }

    println("Ingrese el ID del jardín donde desea gestionar flores:")
    val jardinId = readLine()!!.toInt()
    val jardin = jardines.find { it.id == jardinId }

    if (jardin == null) {
        println("Jardín no encontrado.")
        return
    }

    while (true) {
        println("******************************")
        println("\nGestión de Flores en el Jardín '${jardin.nombre}':")
        println("1. Crear Flor")
        println("2. Leer Flores")
        println("3. Actualizar Flor")
        println("4. Eliminar Flor")
        println("5. Volver al Menú Principal")
        println("******************************")
        print("Seleccione una opción: ")


        when (readLine()?.toIntOrNull()) {
            1 -> crearFlor(jardin)
            2 -> leerFlores(jardin)
            3 -> actualizarFlor(jardin)
            4 -> eliminarFlor(jardin)
            5 -> break
            else -> println("Opción inválida.")
        }
    }
}

fun crearFlor(jardin: Jardin) {
    println("Ingrese el nombre de la flor:")
    val nombre = readLine()!!

    println("Ingrese el color de la flor:")
    val color = readLine()!!

    println("Ingrese el diámetro de la flor (cm):")
    val diametro = readLine()!!.toDouble()

    println("¿La flor es fragante? (true/false):")
    val esFragante = readLine()!!.toBoolean()

    println("Ingrese la temporada de floración de la flor (Ejemplo: Primavera):")
    val temporadaFloracion = readLine()!!

    val nuevaFlor = Flor(
        id = if (jardin.flores.isEmpty()) 1 else jardin.flores.maxOf { it.id } + 1,
        nombre = nombre,
        color = color,
        diametro = diametro,
        esFragante = esFragante,
        temporadaFloracion = temporadaFloracion
    )

    jardin.flores.add(nuevaFlor)
    println("Flor creada con éxito.")
}

fun leerFlores(jardin: Jardin) {
    if (jardin.flores.isEmpty()) {
        println("No hay flores registradas en este jardín.")
        return
    }

    jardin.flores.forEach { flor ->
        println("\nFlor ID: ${flor.id}")
        println("Nombre: ${flor.nombre}")
        println("Color: ${flor.color}")
        println("Diámetro: ${flor.diametro} cm")
        println("Es fragante: ${flor.esFragante}")
        println("Temporada de floración: ${flor.temporadaFloracion}")
    }
}

fun actualizarFlor(jardin: Jardin) {
    println("Ingrese el ID de la flor a actualizar:")
    val id = readLine()!!.toInt()

    val flor = jardin.flores.find { it.id == id }
    if (flor != null) {
        println("Ingrese el nuevo nombre de la flor (${flor.nombre}):")
        flor.nombre = readLine()!!

        println("Ingrese el nuevo color (${flor.color}):")
        flor.color = readLine()!!

        println("Ingrese el nuevo diámetro (${flor.diametro} cm):")
        flor.diametro = readLine()!!.toDouble()

        println("¿La flor es fragante? (${flor.esFragante}):")
        flor.esFragante = readLine()!!.toBoolean()

        println("Ingrese la nueva temporada de floración (${flor.temporadaFloracion}):")
        flor.temporadaFloracion = readLine()!!

        println("Flor actualizada con éxito.")
    } else {
        println("Flor no encontrada.")
    }
}

fun eliminarFlor(jardin: Jardin) {
    println("Ingrese el ID de la flor a eliminar:")
    val id = readLine()!!.toInt()

    val flor = jardin.flores.find { it.id == id }
    if (flor != null) {
        jardin.flores.remove(flor)
        println("Flor eliminada con éxito.")
    } else {
        println("Flor no encontrada.")
    }
}
