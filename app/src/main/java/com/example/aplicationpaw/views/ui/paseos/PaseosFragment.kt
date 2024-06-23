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
import com.example.aplicationpaw.views.ui.mapa.MapFragment
import com.google.android.gms.maps.model.LatLng

class PaseosFragment : Fragment(), MapFragment.OnRouteDrawnListener {

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var priceEditText: EditText
    private lateinit var buscarButton: Button
    private var mapFragment: MapFragment? = null

    private var startLatLng: LatLng? = null
    private var endLatLng: LatLng? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_paseos, container, false)

        priceEditText = rootView.findViewById(R.id.inputPrecio)
        buscarButton = rootView.findViewById(R.id.btnBuscarPaseo)

        // Inicializar SharedPreferences
        sharedPreferences = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE)

        // Obtener instancia de MapFragment
        mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment_container) as? MapFragment

        // Establecer este fragmento como listener para la ruta dibujada
        mapFragment?.routeListener = this

        // Configurar botón de buscar
        buscarButton.setOnClickListener {
            val precio = priceEditText.text.toString()
            val usuario = sharedPreferences.getString("user_id", "") ?: ""

            if (startLatLng != null && endLatLng != null && precio.isNotEmpty() && usuario.isNotEmpty()) {
                createRequest(startLatLng!!.longitude.toString(), startLatLng!!.latitude.toString(), precio, usuario)
            } else {
                //mostrar la longitud y latitud en el Log
                Log.d("PaseosFragment", "Longitud: ${startLatLng?.longitude}")
                Log.d("PaseosFragment", "Latitud: ${startLatLng?.latitude}")
                Log.d("PaseosFragment", "Precio: $precio")
                Log.d("PaseosFragment", "Usuario: $usuario")
                Toast.makeText(context, "Faltan datos para crear la petición", Toast.LENGTH_SHORT).show()
            }
        }

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

    private fun createRequest(longitud: String, latitud: String, precio: String, usuario: String) {
        // Aquí deberías implementar la lógica para enviar la solicitud usando Retrofit o tu método actual
        // Para simplificar el ejemplo, se imprime la información en el Log
        Log.d("PaseosFragment", "Longitud: $longitud")
        Log.d("PaseosFragment", "Latitud: $latitud")
        Log.d("PaseosFragment", "Precio: $precio")
        Log.d("PaseosFragment", "Usuario: $usuario")

        Toast.makeText(context, "Petición creada exitosamente", Toast.LENGTH_SHORT).show()
    }
    override fun onRouteDrawn(startLatLng: LatLng, endLatLng: LatLng) {
        this.startLatLng = startLatLng
        this.endLatLng = endLatLng

        // Mostrar la longitud y latitud en el Log
        Log.d("PaseosFragment", "Longitud: ${startLatLng.longitude}")
        Log.d("PaseosFragment", "Latitud: ${startLatLng.latitude}")

    }
}