package com.example.aplicationpaw.views.mascotas

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aplicationpaw.R
import com.example.aplicationpaw.modelos.Mascota
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
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = MascotaAdapter(mascotas)
        recyclerView.adapter = adapter
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPreferences = requireActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("user_id", null)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://pawpaseo-backend-phi.vercel.app/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        if (userId != null) {
            apiService.getMascotasUsuario(userId).enqueue(object : Callback<List<Mascota>> {
                override fun onResponse(call: Call<List<Mascota>>, response: Response<List<Mascota>>) {
                    if (response.isSuccessful) {
                        val mascotasResponse = response.body() ?: emptyList()
                        mascotas.clear()
                        mascotas.addAll(mascotasResponse)
                        adapter.notifyDataSetChanged()
                    }
                }

                override fun onFailure(call: Call<List<Mascota>>, t: Throwable) {
                    // Manejo del error
                }
            })
        }
    }
}


