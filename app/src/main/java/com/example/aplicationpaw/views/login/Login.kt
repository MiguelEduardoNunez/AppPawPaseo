package com.example.aplicationpaw.views.login

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.aplicationpaw.MainActivity
import com.example.aplicationpaw.R
import com.example.aplicationpaw.views.register.Register
import com.example.aplicationpaw.views.register.RegisterPaseador
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
    private lateinit var progressDialog: ProgressDialog

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

        setContentView(R.layout.activity_login)

        findViewById<View>(R.id.ViewLogin).setOnTouchListener { v, _ ->
            hideKeyboard(v)
            false
        }

        val retrofit = Retrofit.Builder()
            .baseUrl("https://pawpaseo-backend-phi.vercel.app/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        val btnInicioSesion = findViewById<MaterialButton>(R.id.btnInicioSesion)
        val txtCorreo = findViewById<TextInputEditText>(R.id.txtCorreo)
        val txtPassword = findViewById<TextInputEditText>(R.id.txtPassword)

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Validando credenciales...")
        progressDialog.setCancelable(false)

        btnInicioSesion.setOnClickListener {
            val email = txtCorreo.text.toString()
            val password = txtPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            val credenciales = CredencialesLogin(email, password)
            progressDialog.show()

            apiService.login(credenciales).enqueue(object : Callback<UsuarioResponse> {
                override fun onResponse(
                    call: Call<UsuarioResponse>,
                    response: retrofit2.Response<UsuarioResponse>
                ) {
                    progressDialog.dismiss()
                    if (response.isSuccessful && response.body() != null) {
                        // Inicio de sesión exitoso
                        val usuario = response.body()!!
                        Toast.makeText(this@Login, "Inicio de sesión exitoso", Toast.LENGTH_SHORT)
                            .show()

                        // Guarda el indicador de sesión iniciada en las preferencias compartidas
                        val editor = sharedPreferences.edit()
                        editor.putBoolean("is_logged_in", true)
                        val role = if (usuario.services != null) "walker" else "user"
                        editor.putString("user_role", role)
                        editor.putString("user_id", usuario.id)
                        editor.putString("foto_perfil_url", usuario.imagen) // Guarda la URL de la imagen de perfil
                        editor.putString("nombre_usuario", usuario.nombre) // Guarda el nombre de usuario
                        editor.putString("telefono_usuario", usuario.telefono) // Guarda el teléfono del usuario
                        editor.putString("ciudad_usuario", usuario.ciudad) // Guarda la ciudad del usuario
                        editor.putString("correo_usuario", usuario.email) // Guarda el correo del usuario
                        editor.apply()



                        val intent = Intent(this@Login, MainActivity::class.java)
                        startActivity(intent)
                        //mostrar el id del usuario
                        finish()

                    } else {
                        // Error al iniciar sesión
                        Toast.makeText(this@Login, "Credenciales incorrectas", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onFailure(call: Call<UsuarioResponse>, t: Throwable) {
                    progressDialog.dismiss()
                    // Error al realizar la solicitud
                    Toast.makeText(
                        this@Login,
                        "Error al iniciar sesión: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }

        val btnRegistrarse = findViewById<TextView>(R.id.btnRegistrarse)
        btnRegistrarse.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }

        val btnRegistrarsePaseador = findViewById<TextView>(R.id.btnTrabajaConNosotros)
        btnRegistrarsePaseador.setOnClickListener {
            val intent = Intent(this, RegisterPaseador::class.java)
            startActivity(intent)
        }
    }

    private fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
