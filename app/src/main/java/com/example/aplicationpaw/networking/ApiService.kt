package com.example.vfragment.networking

import com.example.vfragment.modelos.CredencialesLogin
import com.example.vfragment.modelos.UsuarioResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("login")
    fun login(@Body credenciales: CredencialesLogin): Call<UsuarioResponse>
}