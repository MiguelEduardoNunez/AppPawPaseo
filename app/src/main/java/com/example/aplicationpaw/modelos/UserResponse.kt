package com.example.pawpaseo.model

data class UserResponse(
    val id: String,
    val nombre: String,
    val telefono: String,
    val ciudad: String,
    val email: String,
    val createdAt: String,
    val updatedAt: String,
    val foto_perfil: String? // Haciendo la foto_perfil opcional
)
