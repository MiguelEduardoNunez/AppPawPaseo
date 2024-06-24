package com.example.aplicationpaw.views.ui.home_paseador

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aplicationpaw.R
import com.example.aplicationpaw.modelos.PeticionPaseo
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.gson.Gson

class HomePaseadorFragment : Fragment(), HomePaseadorAdapter.OnClickItem {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HomePaseadorAdapter
    private val peticionesPaseos = mutableListOf<PeticionPaseo>()
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home_paseador, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewPaseadores)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = HomePaseadorAdapter(peticionesPaseos, this)
        recyclerView.adapter = adapter
        return view;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = requireActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val usuario = sharedPreferences.getString("nombre_usuario", null) ?: ""

        database = Firebase.database.reference;
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.getValue() != null) {
                    val datas = dataSnapshot.getValue() as HashMap<*, *>
                    val gson = Gson()

                    peticionesPaseos.clear();
                    for (data in datas) {
                        val json = Gson().toJson(data.value)
                        val peticionPaseo = gson.fromJson(json, PeticionPaseo::class.java)
                        peticionesPaseos.add(peticionPaseo)
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
        database.addValueEventListener(postListener)
    }

    override fun click(peticionPaseo: PeticionPaseo) {
        //click de la carta
        //remplaza el fragmento por el detalle del paseador
        val bundle = Bundle();
        bundle.putDouble("latitudInicial", peticionPaseo.latitudInicial);
        bundle.putDouble("longitudInicial", peticionPaseo.longitudInicial);
        bundle.putDouble("latitudFinal", peticionPaseo.latitudFinal);
        bundle.putDouble("longitudFinal", peticionPaseo.longitudFinal);
        bundle.putString("user", peticionPaseo.user);
        bundle.putString("precio", peticionPaseo.precio);
        findNavController().navigate(R.id.detallePaseadorFragment, bundle);
    }

}