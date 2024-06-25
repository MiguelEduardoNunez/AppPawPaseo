package com.example.aplicationpaw.views.editar_mascotas

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.example.aplicationpaw.R
import com.example.vfragment.networking.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class EditarMascotas : AppCompatActivity() {

    private var mascotaId: String? = null

    private lateinit var idEditText: EditText
    private lateinit var nombreEditText: EditText
    private lateinit var descripcionEditText: EditText
    private lateinit var generoEditText: EditText
    private lateinit var fechaNacimientoEditText: EditText
    private lateinit var razaEditText: EditText
    private lateinit var vacunasEditText: EditText
    private lateinit var alergiasEditText: EditText
    private lateinit var pesoEditText: EditText
    private lateinit var guardarButton: Button

    private lateinit var apiService: ApiService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_mascotas)

        mascotaId = intent.getStringExtra("mascota_id")
        Log.d("EditarMascotas", "ID recibido: $mascotaId")

        inicializarVistas()
        configurarRetrofit()
        configurarBotonGuardar()

        // Llenar los campos con los datos recibidos
        nombreEditText.setText(intent.getStringExtra("mascota_nombre"))
        descripcionEditText.setText(intent.getStringExtra("mascota_descripcion"))
        generoEditText.setText(intent.getStringExtra("mascota_genero"))
        fechaNacimientoEditText.setText(intent.getStringExtra("mascota_nacimiento"))
        razaEditText.setText(intent.getStringExtra("mascota_raza"))
        vacunasEditText.setText(intent.getStringExtra("mascota_vacunas"))
        alergiasEditText.setText(intent.getStringExtra("mascota_alergias"))
        pesoEditText.setText(intent.getStringExtra("mascota_peso"))
    }

    private fun inicializarVistas() {
        nombreEditText = findViewById(R.id.nombreTextView)
        descripcionEditText = findViewById(R.id.descriptionTextView)
        generoEditText = findViewById(R.id.generoTextView)
        fechaNacimientoEditText = findViewById(R.id.fechaNacimientoTextView)
        razaEditText = findViewById(R.id.razaTextView)
        vacunasEditText = findViewById(R.id.vacunacionTextView)
        alergiasEditText = findViewById(R.id.alergiasTextView)
        pesoEditText = findViewById(R.id.peso_mascota)
        guardarButton = findViewById(R.id.actualizarmascota)
    }

    private fun configurarRetrofit() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://pawpaseo-backend-phi.vercel.app/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)
    }

    private fun configurarBotonGuardar() {
        guardarButton.setOnClickListener {
            Log.d("EditarMascotas", "Botón guardar presionado")
            actualizarMascota()
        }
    }

    private fun actualizarMascota(mascotaId: String? = this.mascotaId) {
        Log.d("EditarMascotas", "Intentando actualizar mascota. ID: $mascotaId")
        if (mascotaId.isNullOrEmpty()) {
            Log.e("EditarMascotas", "Error: ID de mascota es nulo o vacío")
            Toast.makeText(this, "Error: ID de mascota no proporcionado", Toast.LENGTH_SHORT).show()
            return
        }

        val mascotaMap = mapOf(
            "nombre" to nombreEditText.text.toString(),
            "descripcion" to descripcionEditText.text.toString(),
            "genero" to generoEditText.text.toString(),
            "nacimiento" to fechaNacimientoEditText.text.toString(),
            "raza" to razaEditText.text.toString(),
            "vacunas" to vacunasEditText.text.toString(),
            "alergias" to alergiasEditText.text.toString(),
            "peso" to pesoEditText.text.toString()
        )

        Log.d("EditarMascotas", "Datos a enviar: $mascotaMap")
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Actualizando mascota...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        apiService.actualizarMascota(mascotaId!!, mascotaMap).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {

                if (response.isSuccessful) {
                    Log.d("EditarMascotas", "Mascota actualizada correctamente")
                    Toast.makeText(
                        this@EditarMascotas,
                        "Mascota actualizada correctamente",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("EditarMascotas", "Error: ${response.code()}, Body: $errorBody")
                    Toast.makeText(
                        this@EditarMascotas,
                        "Error al actualizar la mascota: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("EditarMascotas", "Error de red", t)
                Toast.makeText(
                    this@EditarMascotas,
                    "Error de red: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

}