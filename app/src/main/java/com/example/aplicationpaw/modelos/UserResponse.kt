package com.example.pawpaseo.model

data class UserResponse(
    val id: String,
    val nombre: String,
    val email: String,
    val createdAt: String,
    val updatedAt: String,
    val services: String? = null
)
