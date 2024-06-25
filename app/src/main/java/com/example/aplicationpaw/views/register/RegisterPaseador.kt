package com.example.aplicationpaw.views.register

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.aplicationpaw.R
import com.example.aplicationpaw.modelos.Walker
import com.example.vfragment.networking.ApiService
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RegisterPaseador : AppCompatActivity() {

    private lateinit var edtNombre: TextInputEditText
    private lateinit var edtTelefono: TextInputEditText
    private lateinit var edtCiudad: TextInputEditText
    private lateinit var edtEmail: TextInputEditText
    private lateinit var edtContrasena: TextInputEditText
    private lateinit var edtCertificado: TextInputEditText
    private lateinit var btnRegistrarse: MaterialButton
    private val FILE_SELECT_CODE = 0

    private val BASE_URL = "https://pawpaseo-backend-phi.vercel.app/api/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(ApiService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register_paseador)

        edtNombre = findViewById(R.id.edt_nombre)
        edtTelefono = findViewById(R.id.edt_telefono)
        edtCiudad = findViewById(R.id.edt_ciudad)
        edtEmail = findViewById(R.id.edt_email)
        edtContrasena = findViewById(R.id.edt_contrasena)
        edtCertificado = findViewById(R.id.edt_Certificado)
        btnRegistrarse = findViewById(R.id.btnRegistrarse)

        edtCertificado.setOnClickListener {
            openFileChooser()
        }

        btnRegistrarse.setOnClickListener {
            registrarPaseador()
        }
    }

    private fun openFileChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*" // You can set the MIME type to limit file types
        startActivityForResult(Intent.createChooser(intent, "Select a file"), FILE_SELECT_CODE)
    }

    private fun registrarPaseador() {
        val nombre = edtNombre.text.toString()
        val telefono = edtTelefono.text.toString()
        val ciudad = edtCiudad.text.toString()
        val email = edtEmail.text.toString()
        val contrasena = edtContrasena.text.toString()
        val certificado = edtCertificado.text.toString() // Debes obtener la URL de la imagen seleccionada

        val walker = Walker(
            email = email,
            password = contrasena,
            nombre = nombre,
            telefono = telefono,
            ciudad = ciudad,
            services = emptyList(), // Debes definir los servicios que ofrece el paseador
            calificacion = 0, // Debes definir la calificación inicial
            foto_perfil = certificado // URL de la imagen seleccionada
        )

        val call = apiService.registerWalker(walker)
        call.enqueue(object : Callback<Walker> {
            override fun onResponse(call: Call<Walker>, response: Response<Walker>) {
                if (response.isSuccessful) {
                    val registeredWalker = response.body()
                    // Registro exitoso
                    Toast.makeText(this@RegisterPaseador, "Registro exitoso", Toast.LENGTH_SHORT).show()
                } else {
                    // Error en el registro
                    Toast.makeText(this@RegisterPaseador, "Error en el registro", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Walker>, t: Throwable) {
                // Error en la solicitud
                Toast.makeText(this@RegisterPaseador, "Error en la solicitud", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FILE_SELECT_CODE && resultCode == Activity.RESULT_OK) {
            val uri: Uri? = data?.data
            uri?.let {
                edtCertificado.setText(it.toString()) // Aquí debes obtener la URL de la imagen seleccionada
                Toast.makeText(this, "File Selected: ${it.path}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
