package com.example.aplicationpaw.views.ui.home

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.navigation.fragment.findNavController
import com.example.aplicationpaw.R
import com.example.aplicationpaw.databinding.FragmentHomeBinding
import com.example.aplicationpaw.modelos.PeticionPaseador
import com.example.aplicationpaw.modelos.PeticionPaseo
import com.example.aplicationpaw.views.ui.mapa.MapFragment
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import com.google.gson.Gson

class HomeFragment : Fragment() {

    private lateinit var view:View
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var userLogin: String
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        view = binding.root;
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Configurar la navegación entre fragmentos
        //setupNavigation()

        validIsExitsSolicitud()
    }

    fun validIsExitsSolicitud() {
        sharedPreferences = view.context.getSharedPreferences("user_session", Context.MODE_PRIVATE) ?: sharedPreferences
        // Obtener el nombre de usuario de las preferencias compartidas
        userLogin = sharedPreferences.getString("nombre_usuario", "") ?: ""

        //firebase guardar datos
        database = Firebase.database.reference;
        database.child(userLogin).get().addOnSuccessListener{ task ->
            if(task.value != null) {
                val datas = task.value as HashMap<*, *>
                val json = Gson().toJson(datas)
                val peticionPaseo = Gson().fromJson(json, PeticionPaseo::class.java)

                if(peticionPaseo.status == view.context.getString(R.string.nuevo)){
                    findNavController().popBackStack();
                    findNavController().popBackStack();
                    findNavController().navigate(R.id.esperaUsuarioFragment);
                }else{
                    // Agregar MapFragment al contenedor
                    childFragmentManager.commit {
                        replace<MapFragment>(R.id.map_fragment_container)
                    }
                }
            }else{
                // Agregar MapFragment al contenedor
                childFragmentManager.commit {
                    replace<MapFragment>(R.id.map_fragment_container)
                }
            }
        };
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
