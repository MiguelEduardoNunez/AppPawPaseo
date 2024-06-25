package com.example.aplicationpaw.modelos

data class Walker(
    val email: String,
    val password: String,
    val nombre: String,
    val telefono: String,
    val ciudad: String,
    val services: List<String>,
    val calificacion: Int,
    val foto_perfil: String // URL de la foto de perfil
)