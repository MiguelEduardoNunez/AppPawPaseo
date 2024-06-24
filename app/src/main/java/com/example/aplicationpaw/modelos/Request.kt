package com.example.aplicationpaw.modelos

import java.util.Date

data class Coordenada(
    val latitud: Double,
    val longitud: Double
)

data class CrearPeticionRequest(
    val longitud: Double,
    val latitud: Double,
    val precio: String,
    val user: String
)

data class RespuestaServidor(
    val longitud: String,
    val latitud: String,
    val precio: Int,
    val date: String,
    val user: String,
    val estado: String,
    val paseador: String?,
    val completado: Boolean,
    val _id: String,
    val createdAt: String,
    val updatedAt: String,
    val __v: Int
)
