package com.example.aplicationpaw.views.ui.home


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.navigation.fragment.NavHostFragment
import com.example.aplicationpaw.R
import com.example.aplicationpaw.databinding.FragmentHomeBinding
import com.example.aplicationpaw.views.ui.mapa.MapFragment
import com.example.aplicationpaw.views.ui.paseos.PaseosFragment

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Agregar MapFragment al contenedor
        childFragmentManager.commit {
            replace<MapFragment>(R.id.map_fragment_container)
        }

        // Configurar la navegación entre fragmentos
        //setupNavigation()
    }

    /*private fun setupNavigation() {
        val btnPaseos = binding.btnPaseos
        val btnGuarderia = binding.btnGuarderia
        val btnVeterinaria = binding.btnVeterinaria

        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.fragmentViewContainer) as NavHostFragment
        val navController = navHostFragment.navController

        val colorNoSeleccionado = ContextCompat.getColor(requireContext(), R.color.azulClaro)
        val colorSeleccionado = ContextCompat.getColor(requireContext(), R.color.amarillo)

        btnPaseos.setBackgroundColor(colorSeleccionado)

        btnPaseos.setOnClickListener {
            navController.navigate(R.id.paseosFragment)
            btnPaseos.setBackgroundColor(colorSeleccionado)
            btnGuarderia.setBackgroundColor(colorNoSeleccionado)
            btnVeterinaria.setBackgroundColor(colorNoSeleccionado)
        }
        btnVeterinaria.setOnClickListener {
            navController.navigate(R.id.veterinariaFragment)
            btnVeterinaria.setBackgroundColor(colorSeleccionado)
            btnPaseos.setBackgroundColor(colorNoSeleccionado)
            btnGuarderia.setBackgroundColor(colorNoSeleccionado)
        }
        btnGuarderia.setOnClickListener {
            navController.navigate(R.id.guarderiaFragment)
            btnGuarderia.setBackgroundColor(colorSeleccionado)
            btnPaseos.setBackgroundColor(colorNoSeleccionado)
            btnVeterinaria.setBackgroundColor(colorNoSeleccionado)
        }
    }*/

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
