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

        return rootView
    }

    override fun onRouteDrawn(startLatLng: LatLng, endLatLng: LatLng) {
        this.startLatLng = startLatLng
        this.endLatLng = endLatLng
    }
}