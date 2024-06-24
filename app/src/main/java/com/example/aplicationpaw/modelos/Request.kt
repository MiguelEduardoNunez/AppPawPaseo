package com.example.aplicationpaw.modelos

import java.util.Date

data class Coordenada(
    val latitud: Double,
    val longitud: Double
)

data class CrearPeticionRequest(
    val longitud: Double,
    val latitud: Double,
    val precio: Double,
    val descripcion: String,
    val estado: String,
    val date: Date?,
    val user: String,
    val paseador: String?
)

data class RespuestaServidor(
    val mensaje: String
)
