package com.example.deber01

data class Jardin(
    var nombre: String,
    var ubicacion: String,
    var tamano: Double,
    var tipoSuelo: String,
    var flores: MutableList<Flor> = mutableListOf()
)
