package com.example.aplicationpaw.networking

import com.example.pawpaseo.model.UserResponse
import com.example.pawpaseo.model.Usuario
import retrofit2.http.Body
import retrofit2.http.POST

interface RegisterService {
    @POST("usuario")
    fun register(@Body usuario: Usuario):retrofit2.Call<UserResponse>
}