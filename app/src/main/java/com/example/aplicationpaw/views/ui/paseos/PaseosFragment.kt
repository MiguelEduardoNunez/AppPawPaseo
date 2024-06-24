package com.example.aplicationpaw.views.ui.paseos

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.aplicationpaw.R
import com.example.aplicationpaw.modelos.CrearPeticionRequest
import com.example.aplicationpaw.views.ui.mapa.MapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import java.util.Date

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

        // Mostrar la longitud y latitud en el Log
        Log.d("PaseosFragment", "Longitud: ${startLatLng.longitude}")
        Log.d("PaseosFragment", "Latitud: ${startLatLng.latitude}")

    }
}