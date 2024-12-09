package org.example

import kotlinx.serialization.Serializable

@Serializable
data class Flor(
    val id: Int,
    var nombre: String,
    var color: String,
    var diametro: Double,
    var esFragante: Boolean,
    var temporadaFloracion: String
)


