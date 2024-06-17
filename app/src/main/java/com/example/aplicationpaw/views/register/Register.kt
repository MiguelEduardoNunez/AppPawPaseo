package com.example.aplicationpaw.views.register

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.aplicationpaw.R
import com.example.aplicationpaw.views.login.Login
import com.example.pawpaseo.model.RetrofitClient
import com.example.pawpaseo.model.UserResponse
import com.example.pawpaseo.model.Usuario
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Register : AppCompatActivity() {

    private lateinit var edtNombre: EditText
    private lateinit var edtTelefono: EditText
    private lateinit var edtCiudad: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtContrasena: EditText
    private lateinit var btnRegistrarse: Button
    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        findViewById<View>(R.id.Viewregister).setOnTouchListener { v, _ ->
            hideKeyboard(v)
            false
        }

        edtNombre = findViewById(R.id.edt_nombre)
        edtTelefono = findViewById(R.id.edt_telefono)
        edtCiudad = findViewById(R.id.edt_ciudad)
        edtEmail = findViewById(R.id.edt_email)
        edtContrasena = findViewById(R.id.edt_contrasena)
        btnRegistrarse = findViewById(R.id.btnRegistrarse)

        setupListeners()

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Validando credenciales...")
        progressDialog.setCancelable(false)

    }
    private fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
    private fun setupListeners() {
        btnRegistrarse.setOnClickListener {
            val nombre = edtNombre.text.toString().trim()
            val telefono = edtTelefono.text.toString().trim()
            val ciudad = edtCiudad.text.toString().trim()
            val email = edtEmail.text.toString().trim()
            val password = edtContrasena.text.toString().trim()

            if (validarCampos(nombre, telefono, ciudad, email, password)) {
                val usuario = Usuario(nombre, telefono, ciudad, email, password)
                registrarUsuario(usuario)
            }
        }
    }

    private fun validarCampos(
        nombre: String, telefono: String, ciudad: String, email: String, password: String
    ): Boolean {
        if (nombre.isEmpty() || telefono.isEmpty() || ciudad.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Formato de correo electrónico inválido", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun registrarUsuario(usuario: Usuario) {
        progressDialog.show()
        val llamadaRegistro = RetrofitClient.instance.register(usuario)
        llamadaRegistro.enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                progressDialog.dismiss()
                if (response.isSuccessful) {
                    val respuesta = response.body()
                    // Maneja la respuesta exitosa aquí
                    Toast.makeText(this@Register, "Registro exitoso", Toast.LENGTH_SHORT).show()
                    // Puedes navegar a otra actividad o realizar otra acción después del registro exitoso
                    val intent = Intent(this@Register, Login::class.java)
                    startActivity(intent)
                } else {
                    // Maneja el error de registro aquí
                    val errorBody = response.errorBody()?.string() // Obtiene el cuerpo de la respuesta de error
                    Toast.makeText(this@Register, "Error en el registro: $errorBody", Toast.LENGTH_SHORT).show()
                }

            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                // Maneja el error de conexión o cualquier otro error aquí
                Toast.makeText(this@Register, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }
}