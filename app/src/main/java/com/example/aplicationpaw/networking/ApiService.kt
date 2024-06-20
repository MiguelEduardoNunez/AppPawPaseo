package com.example.vfragment.networking

import com.example.vfragment.modelos.CredencialesLogin
import com.example.vfragment.modelos.UsuarioResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Path

interface ApiService {
    @POST("login")
    fun login(@Body credenciales: CredencialesLogin): Call<UsuarioResponse>

    @Multipart
    @PUT("usuario/{id}")
    fun updateUser(
        @Path("id") id: String,
        @PartMap fields: Map<String, @JvmSuppressWildcards RequestBody>
    ): Call<UsuarioResponse>
}