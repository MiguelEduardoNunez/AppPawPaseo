package com.example.aplicationpaw.views.ui.espera_usuario

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.navigation.fragment.findNavController
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

    private lateinit var view: View
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EsperaUsuarioAdapter
    private var peticionesPaseadores = mutableListOf<PeticionPaseador>()

    private lateinit var database: DatabaseReference
    private lateinit var userLogin: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        view = inflater.inflate(R.layout.fragment_espera_usuario, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewPaseadores)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = EsperaUsuarioAdapter(peticionesPaseadores, this)
        recyclerView.adapter = adapter
        return view;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = Firebase.database.reference;
        val sharedPreferences = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE)
        userLogin = sharedPreferences.getString("nombre_usuario", "") ?: ""

        init();
        loadFirebase();
    }

    private fun init(){
        val cancelar = view.findViewById<Button>(R.id.Cancelar);
        cancelar.setOnClickListener {
            database.child(userLogin).child("status").setValue(view.context.getString(R.string.cancelado)).addOnSuccessListener {
                Toast.makeText(context, "Se cancelo la solicitud correctamente", Toast.LENGTH_LONG).show();
                findNavController().popBackStack();
                findNavController().navigate(R.id.navigation_home);
            }.addOnFailureListener {
                Toast.makeText(context, "Fallo", Toast.LENGTH_LONG).show();
            }
        };
    }

    fun loadFirebase(){
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.value != null) {
                    val datas = dataSnapshot.value as HashMap<*, *>
                    val gson = Gson()

                    peticionesPaseadores.clear();
                    for (data in datas) {
                        val json = Gson().toJson(data.value)
                        val peticionPaseo = gson.fromJson(json, PeticionPaseador::class.java)

                        if(peticionPaseo.status == view.context.getString(R.string.nuevo)){
                            peticionesPaseadores.add(peticionPaseo)
                        }
                    }

                    adapter.notifyDataSetChanged();
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
        database.child(userLogin).child(getString(R.string.paseadores)).child(peticionPaseador.user).child("status")
            .setValue(view.context?.getString(R.string.aceptado)).addOnSuccessListener {

                database.child(userLogin).child("status").setValue(view.context?.getString(R.string.aceptado)).addOnSuccessListener {
                    Toast.makeText(context, "Se acepto al paseador.", Toast.LENGTH_LONG).show();

                    val bundle = Bundle();
                    bundle.putString("user", peticionPaseador.user);
                    bundle.putString("precio", peticionPaseador.precio);
                    findNavController().navigate(R.id.aceptaUsuarioFragment, bundle);
                }.addOnFailureListener {
                    Toast.makeText(context, "Fallo", Toast.LENGTH_LONG).show();
                }

        }.addOnFailureListener {
            Toast.makeText(context, "Fallo", Toast.LENGTH_LONG).show();
        }
    }

    override fun omitir(peticionPaseador: PeticionPaseador) {
        database.child(userLogin).child(getString(R.string.paseadores)).child(peticionPaseador.user).child("status")
            .setValue(view.context?.getString(R.string.rechazado)).addOnSuccessListener {
                Toast.makeText(context, "Se rechazo al paseador.", Toast.LENGTH_LONG).show();
            }.addOnFailureListener {
                Toast.makeText(context, "Fallo", Toast.LENGTH_LONG).show();
            }
    }
}