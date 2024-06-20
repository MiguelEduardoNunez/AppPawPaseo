package com.example.aplicationpaw.views.editar_perfil

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.aplicationpaw.R
import com.example.vfragment.modelos.UsuarioResponse
import com.example.vfragment.networking.ApiService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class EditarPerfil : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_perfil)

        sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE)

        val idUser = sharedPreferences.getString("user_id", "")
        val nombreUsuario = sharedPreferences.getString("nombre_usuario", "")
        val telefonoUsuario = sharedPreferences.getString("telefono_usuario", "")
        val ciudadUsuario = sharedPreferences.getString("ciudad_usuario", "")
        val correoUsuario = sharedPreferences.getString("correo_usuario", "")
        val fotoPerfilUrl = sharedPreferences.getString("foto_perfil_url", "")

        val nombreEditText = findViewById<EditText>(R.id.nameUser)
        val telefonoEditText = findViewById<EditText>(R.id.phoneUser)
        val ciudadEditText = findViewById<EditText>(R.id.cityUser)
        val correoEditText = findViewById<EditText>(R.id.emailUser)
        val imageUser = findViewById<ImageView>(R.id.imageUser)
        val saveButton = findViewById<Button>(R.id.saveButton)

        nombreEditText.setText(nombreUsuario)
        telefonoEditText.setText(telefonoUsuario)
        ciudadEditText.setText(ciudadUsuario)
        correoEditText.setText(correoUsuario)

        // Cargar la imagen de perfil utilizando Glide
        if (!fotoPerfilUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(fotoPerfilUrl)
                .into(imageUser)
        }

        val retrofit = Retrofit.Builder()
            .baseUrl("https://pawpaseo-backend-phi.vercel.app/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        saveButton.setOnClickListener {
            Log.d("EditarPerfil", "ID del usuario: $idUser")
            updateProfile()

        }
    }

    private fun updateProfile() {
        val id = sharedPreferences.getString("user_id", "") ?: ""
        val nombre = findViewById<EditText>(R.id.nameUser).text.toString()
        val telefono = findViewById<EditText>(R.id.phoneUser).text.toString()
        val ciudad = findViewById<EditText>(R.id.cityUser).text.toString()
        val correo = findViewById<EditText>(R.id.emailUser).text.toString()

        if (nombre.isEmpty() || telefono.isEmpty() || ciudad.isEmpty() || correo.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val requestBodyMap = mapOf(
            "nombre" to RequestBody.create("text/plain".toMediaTypeOrNull(), nombre),
            "telefono" to RequestBody.create("text/plain".toMediaTypeOrNull(), telefono),
            "ciudad" to RequestBody.create("text/plain".toMediaTypeOrNull(), ciudad),
            "email" to RequestBody.create("text/plain".toMediaTypeOrNull(), correo)
        )

        apiService.updateUser(id, requestBodyMap).enqueue(object : Callback<UsuarioResponse> {
            override fun onResponse(call: Call<UsuarioResponse>, response: retrofit2.Response<UsuarioResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    Toast.makeText(this@EditarPerfil, "Perfil actualizado exitosamente", Toast.LENGTH_SHORT).show()
                    // Actualiza SharedPreferences con los nuevos datos
                    val editor = sharedPreferences.edit()
                    editor.putString("nombre_usuario", nombre)
                    editor.putString("telefono_usuario", telefono)
                    editor.putString("ciudad_usuario", ciudad)
                    editor.putString("correo_usuario", correo)
                    editor.apply()
                    // Finaliza la actividad para regresar al perfil o a la actividad anterior
                    finish()
                } else {
                    Toast.makeText(this@EditarPerfil, "Error al actualizar perfil", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UsuarioResponse>, t: Throwable) {
                Toast.makeText(this@EditarPerfil, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
