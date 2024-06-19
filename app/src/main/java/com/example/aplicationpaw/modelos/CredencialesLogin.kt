package com.example.vfragment.modelos

data class CredencialesLogin(
    val identifier: String,
    val password: String
)
data class UsuarioResponse (
    val id: String,
    val nombre: String,
    val email: String,
    val createdAt: String,
    val updatedAt: String,
    val services: String? = null
)
