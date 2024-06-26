package com.example.aplicationpaw.modelos

data class WalkerResponse(
    val id: String,
    val foto_perfil: String?,
    val certificado: String?,
    val nombre: String,
    val telefono: String,
    val ciudad: String,
    val email: String,
    val calificacion: Int,
    val services: String,
    val createdAt: String,
    val updatedAt: String
)
