package com.example.aplicationpaw.views.ui.paseos

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.example.aplicationpaw.R
import com.example.aplicationpaw.views.ui.mapa.MapFragment
import com.google.android.gms.maps.model.LatLng


class PaseosFragment : Fragment() {


    private var startLatLng: LatLng? = null
    private var endLatLng: LatLng? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_paseos, container, false)

        val btnMarcarRuta = rootView.findViewById<Button>(R.id.editPersonalizacion)
        btnMarcarRuta.setOnClickListener {
            if (startLatLng == null || endLatLng == null) {
                // Mostrar un mensaje indicando que se necesitan puntos de inicio y destino
                Toast.makeText(requireContext(), "Selecciona un punto de inicio y un punto final en el mapa", Toast.LENGTH_SHORT).show()
            } else {
                // Obtener el MapFragment y dibujar la ruta
                val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment_container) as? MapFragment
                mapFragment?.drawRoute(startLatLng, endLatLng)
            }
        }

        return rootView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        startLatLng = null
        endLatLng = null
    }

    fun setStartLatLng(latlng: LatLng) {
        startLatLng = latlng
    }

    fun setEndLatLng(latlng: LatLng) {
        endLatLng = latlng
    }
}