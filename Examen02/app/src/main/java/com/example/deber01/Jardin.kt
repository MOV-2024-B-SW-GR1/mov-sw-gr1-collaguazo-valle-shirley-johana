package com.example.deber01

data class Jardin(
    var nombre: String,
    var ubicacion: String,
    var fechaCreacion: String,
    var tamano: Double,
    var tipoSuelo: String,
    var latitud: Double = 0.0,
    var longitud: Double = 0.0
)

