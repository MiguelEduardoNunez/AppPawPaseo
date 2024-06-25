package com.example.vfragment.networking

import com.example.aplicationpaw.modelos.CrearPeticionRequest
import com.example.aplicationpaw.modelos.Mascota
import com.example.aplicationpaw.modelos.MascotaResponse
import com.example.aplicationpaw.modelos.RespuestaServidor
import com.example.aplicationpaw.modelos.Walker
import com.example.vfragment.modelos.CredencialesLogin
import com.example.vfragment.modelos.UsuarioResponse
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
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

    @POST("peticion")
    fun crearPeticion(@Body request: CrearPeticionRequest): Call<RespuestaServidor>

    @GET("mascotasUsuario/{id}")
    fun getMascotasUsuario(@Path("id") userId: String): Call<List<Mascota>>

    @POST("/paseador")
    fun registerWalker(@Body walker: Walker): Call<Walker>

    @GET("mascota/{id}")
    fun getMascotaById(@Path("id") id: String): Call<MascotaResponse>

    @PUT("mascota/{id}")
    fun actualizarMascota(@Path("id") mascotaId: String, @Body body: Map<String, String>): Call<Void>
}