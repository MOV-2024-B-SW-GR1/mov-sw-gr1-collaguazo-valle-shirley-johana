package org.example

import kotlinx.serialization.Serializable

@Serializable
data class Jardin(
    val id: Int,
    var nombre: String,
    var ubicacion: String,
    var fechaCreacion: String,
    var tamano: Double,
    var tipoSuelo: String,
    val flores: MutableList<Flor> = mutableListOf()
)

