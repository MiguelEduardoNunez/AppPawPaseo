package com.example.aplicationpaw.views.mascotas

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aplicationpaw.R
import com.example.aplicationpaw.modelos.Mascota
import com.example.aplicationpaw.views.editar_mascotas.EditarMascotas
import com.example.vfragment.networking.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MascotasFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var apiService: ApiService
    private lateinit var adapter: MascotaAdapter
    private val mascotas = mutableListOf<Mascota>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_mascotas, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewMascotas)
        setupRecyclerView()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPreferences =
            requireActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("user_id", null)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://pawpaseo-backend-phi.vercel.app/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        userId?.let { fetchMascotas(it) }
    }

    private fun fetchMascotas(userId: String) {
        apiService.getMascotasUsuario(userId).enqueue(object : Callback<List<Mascota>> {
            override fun onResponse(call: Call<List<Mascota>>, response: Response<List<Mascota>>) {
                if (response.isSuccessful) {
                    val mascotasResponse = response.body() ?: emptyList()
                    mascotas.clear()
                    mascotas.addAll(mascotasResponse)
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(requireContext(), "Error al obtener las mascotas", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Mascota>>, t: Throwable) {
                Toast.makeText(requireContext(), "Error de red: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupRecyclerView() {
        adapter = MascotaAdapter(mascotas) { mascota ->
            val intent = Intent(requireContext(), EditarMascotas::class.java).apply {
                putExtra("mascota_id", mascota._id)
                putExtra("mascota_nombre", mascota.nombre)
                putExtra("mascota_descripcion", mascota.descripcion)
                putExtra("mascota_genero", mascota.genero)
                putExtra("mascota_nacimiento", mascota.nacimiento)
                putExtra("mascota_raza", mascota.raza)
                putExtra("mascota_vacunas", mascota.vacunas)
                putExtra("mascota_alergias", mascota.alergias)
                putExtra("mascota_peso", mascota.peso)
            }
            startActivity(intent)
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }
}