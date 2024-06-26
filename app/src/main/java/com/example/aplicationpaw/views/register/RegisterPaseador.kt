package com.example.aplicationpaw.views.register

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.aplicationpaw.R
import com.example.aplicationpaw.modelos.WalkerResponse
import com.example.vfragment.networking.ApiService
import com.example.pawpaseo.model.RetrofitClient
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class RegisterPaseador : AppCompatActivity() {

    private lateinit var edtNombre: TextInputEditText
    private lateinit var edtTelefono: TextInputEditText
    private lateinit var edtCiudad: TextInputEditText
    private lateinit var edtEmail: TextInputEditText
    private lateinit var edtContrasena: TextInputEditText
    private lateinit var edtCertificado: TextInputEditText
    private lateinit var edtServices: TextInputEditText
    private lateinit var btnRegistrarse: MaterialButton
    private lateinit var progressDialog: ProgressDialog
    private val FILE_SELECT_CODE = 0

    private var certificadoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*"
            addCategory(Intent.CATEGORY_OPENABLE)
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf(
                "application/pdf",
                "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "image/*"
            ))
        }
        startActivityForResult(Intent.createChooser(intent, "Selecciona un archivo"), FILE_SELECT_CODE)
    }

    private fun showProgressDialog() {
        progressDialog = ProgressDialog(this).apply {
            setMessage("Registrando paseador...")
            setCancelable(false)
            show()
        }
    }

    private fun hideProgressDialog() {
        if (::progressDialog.isInitialized && progressDialog.isShowing) {
            progressDialog.dismiss()
        }
    }

    private fun registrarPaseador() {
        try {
            val nombre = edtNombre.text.toString()
            val telefono = edtTelefono.text.toString()
            val ciudad = edtCiudad.text.toString()
            val email = edtEmail.text.toString()
            val contrasena = edtContrasena.text.toString()

            if (nombre.isEmpty() || telefono.isEmpty() || ciudad.isEmpty() || email.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
                return
            }

            showProgressDialog()
            // Crear el mapa de datos para enviar al servidor
            val requestBodyMap = mutableMapOf<String, RequestBody>()
            requestBodyMap["nombre"] = nombre.toRequestBody("text/plain".toMediaTypeOrNull())
            requestBodyMap["telefono"] = telefono.toRequestBody("text/plain".toMediaTypeOrNull())
            requestBodyMap["ciudad"] = ciudad.toRequestBody("text/plain".toMediaTypeOrNull())
            requestBodyMap["email"] = email.toRequestBody("text/plain".toMediaTypeOrNull())
            requestBodyMap["password"] = contrasena.toRequestBody("text/plain".toMediaTypeOrNull())
            requestBodyMap["services"] = "Paseador".toRequestBody("text/plain".toMediaTypeOrNull())

            var certificadoPart: MultipartBody.Part? = null
            certificadoUri?.let { uri ->
                val file = createTempFileFromUri(uri)
                val requestFile = file.asRequestBody("application/octet-stream".toMediaTypeOrNull())
                certificadoPart = MultipartBody.Part.createFormData("certificado", file.name, requestFile)
            }

            val apiService = RetrofitClient.getRetrofitInstance().create(ApiService::class.java)

            val call = apiService.registerWalker(requestBodyMap, certificadoPart)

            call.enqueue(object : Callback<WalkerResponse> {
                override fun onResponse(call: Call<WalkerResponse>, response: Response<WalkerResponse>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@RegisterPaseador, "Registro exitoso", Toast.LENGTH_SHORT).show()
                        hideProgressDialog()
                        finish()
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("API_ERROR", "Error response: ${response.code()}, Body: $errorBody")
                        Toast.makeText(this@RegisterPaseador, "Error en el registro: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<WalkerResponse>, t: Throwable) {

                    Log.e("API_ERROR", "Request failed", t)
                    Toast.makeText(this@RegisterPaseador, "Error en la solicitud: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } catch (e: Exception) {
            Log.e("RegisterPaseador", "Error al registrar paseador", e)
            Toast.makeText(this, "Error al registrar: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FILE_SELECT_CODE && resultCode == Activity.RESULT_OK) {
            certificadoUri = data?.data
            certificadoUri?.let {
                val fileName = getFileName(it)
                edtCertificado.setText(fileName)
                Toast.makeText(this, "Archivo seleccionado: $fileName", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("Range")
    private fun getFileName(uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor?.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != -1) {
                result = result?.substring(cut!! + 1)
            }
        }
        return result ?: "Unknown"
    }

    private fun createTempFileFromUri(uri: Uri): File {
        val inputStream = contentResolver.openInputStream(uri)
        val fileName = getFileName(uri)
        val file = File(cacheDir, fileName)
        inputStream.use { input ->
            FileOutputStream(file).use { output ->
                input?.copyTo(output)
            }
        }
        return file
    }
}
