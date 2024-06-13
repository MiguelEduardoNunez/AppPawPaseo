package com.example.aplicationpaw.views.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.aplicationpaw.MainActivity
import com.example.aplicationpaw.R
import com.example.vfragment.modelos.CredencialesLogin
import com.example.vfragment.modelos.UsuarioResponse
import com.example.vfragment.networking.ApiService
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Login : AppCompatActivity() {
    private lateinit var apiService: ApiService


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Verifica si el usuario ya está autenticado
        val sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)

        if (isLoggedIn) {
            // Si el usuario ya está autenticado, inicia MainActivity y finaliza esta actividad
            startActivity(Intent(this@Login, MainActivity::class.java))
            finish()
            return
        }

        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://prueba-backend-phi.vercel.app/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        val btnInicioSesion = findViewById<MaterialButton>(R.id.btnInicioSesion)
        val txtCorreo = findViewById<TextInputEditText>(R.id.txtCorreo)
        val txtPassword = findViewById<TextInputEditText>(R.id.txtPassword)


        btnInicioSesion.setOnClickListener {
            val email = txtCorreo.text.toString()
            val password = txtPassword.text.toString()

            val credenciales = CredencialesLogin(email, password)

            apiService.login(credenciales).enqueue(object : Callback<UsuarioResponse> {
                override fun onResponse(
                    call: Call<UsuarioResponse>,
                    response: retrofit2.Response<UsuarioResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        // Inicio de sesión exitoso
                        val usuario = response.body()!!
                        Toast.makeText(this@Login, "Inicio de sesión exitoso", Toast.LENGTH_SHORT)
                            .show()

                        // Guarda el indicador de sesión iniciada en las preferencias compartidas
                        val editor = sharedPreferences.edit()
                        editor.putBoolean("is_logged_in", true)
                        editor.apply()

                        val intent = Intent(this@Login, MainActivity::class.java)
                        startActivity(intent)

                    } else {
                        // Error al iniciar sesión
                        Toast.makeText(this@Login, "Credenciales incorrectas", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onFailure(call: Call<UsuarioResponse>, t: Throwable) {
                    // Error al realizar la solicitud
                    Toast.makeText(
                        this@Login,
                        "Error al iniciar sesión: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
    }

}