package com.example.aplicationpaw.views.ui.detalle_paseador

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.example.aplicationpaw.R
import com.example.aplicationpaw.views.ui.mapa.MapFragment

class detalles_paseador : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

            // Agregar MapFragment al contenedor
            childFragmentManager.commit {
                replace<MapFragment>(R.id.map_fragment_container)
            }
        }
    }
}
