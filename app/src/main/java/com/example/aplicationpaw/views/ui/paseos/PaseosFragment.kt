package com.example.aplicationpaw.views.ui.paseos

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.aplicationpaw.R
import com.example.aplicationpaw.views.ui.mapa.MapFragment
import com.google.android.gms.maps.model.LatLng

class PaseosFragment : Fragment(), MapFragment.OnRouteDrawnListener {

    private var mapFragment: MapFragment? = null

    private var startLatLng: LatLng? = null
    private var endLatLng: LatLng? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_paseos, container, false)


        // Configurar botón de marcar ruta
        val btnMarcarRuta = rootView.findViewById<Button>(R.id.editPersonalizacion)
        btnMarcarRuta.setOnClickListener {
            if (startLatLng == null || endLatLng == null) {
                Toast.makeText(requireContext(), "Selecciona un punto de inicio y un punto final en el mapa", Toast.LENGTH_SHORT).show()
            } else {
                mapFragment?.drawRoute(startLatLng, endLatLng)
            }
        }
        return rootView
    }

    override fun onRouteDrawn(startLatLng: LatLng, endLatLng: LatLng) {
        this.startLatLng = startLatLng
        this.endLatLng = endLatLng
    }
}