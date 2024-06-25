package com.example.aplicationpaw.views.ui.espera_usuario

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aplicationpaw.R
import com.example.aplicationpaw.modelos.PeticionPaseador
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.gson.Gson

class EsperaUsuarioFragment : Fragment(), EsperaUsuarioAdapter.OnClickItem {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EsperaUsuarioAdapter
    private var peticionesPaseadores = mutableListOf<PeticionPaseador>()
    private lateinit var database: DatabaseReference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_espera_usuario, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewPaseadores)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = EsperaUsuarioAdapter(peticionesPaseadores, this)
        recyclerView.adapter = adapter
        return view;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = Firebase.database.reference;
        var sharedPreferences = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val userLogin = sharedPreferences.getString("nombre_usuario", "") ?: ""

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.getValue() != null) {
                    val datas = dataSnapshot.getValue() as HashMap<*, *>
                    val gson = Gson()

                    peticionesPaseadores.clear();
                    for (data in datas) {
                        val json = Gson().toJson(data.value)
                        val peticionPaseo = gson.fromJson(json, PeticionPaseador::class.java)

                        if(peticionPaseo.status == getString(R.string.nuevo)){
                            peticionesPaseadores.add(peticionPaseo)
                        }
                    }

                    adapter.notifyDataSetChanged();
                }else{
                    Toast.makeText(context, "No se encuentran registros", Toast.LENGTH_LONG).show();
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("FIREBASE", "loadPost:onCancelled", databaseError.toException())
                adapter.notifyDataSetChanged();
            }
        }
        database.child(userLogin).child(getString(R.string.paseadores)).addValueEventListener(postListener);
    }

    override fun aceptar(peticionPaseador: PeticionPaseador) {
        Toast.makeText(context, "ACEPTAR", Toast.LENGTH_LONG).show();
    }

    override fun omitir(peticionPaseador: PeticionPaseador) {
        Toast.makeText(context, "OMITIR", Toast.LENGTH_LONG).show();
    }
}