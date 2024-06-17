package com.example.aplicationpaw.views.ui.mapa

import RequestService
import android.Manifest
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.aplicationpaw.R
import com.example.aplicationpaw.modelos.Coordenada
import com.example.aplicationpaw.modelos.CrearPeticionRequest
import com.example.aplicationpaw.modelos.RespuestaServidor
import com.example.aplicationpaw.views.ui.dialogo.RequestDialogFragment
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.GeoApiContext
import com.google.maps.DirectionsApi
import com.google.maps.model.TravelMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMapClickListener, RequestDialogFragment.RequestDialogListener {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationCallback: LocationCallback? = null
    private var startLatLng: LatLng? = null
    private var endLatLng: LatLng? = null
    private lateinit var apiService: RequestService
    private val baseUrl = "https://prueba-backend-phi.vercel.app/api/"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_map, container, false)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configuración de Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Crear instancia de la interfaz de servicio
        apiService = retrofit.create(RequestService::class.java)

        // Inicializar el mapa
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapa) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun enviarDatosRutaYPrecio() {
        // Obtener los puntos de ruta y precio
        val puntosRuta = obtenerPuntosRutaDesdeMapa()
        val precio = obtenerPrecioIngresado()

        // Verificar que los puntos de ruta y precio no estén vacíos
        if (puntosRuta.isEmpty() || precio.isEmpty()) {
            Log.e(TAG, "Puntos de ruta o precio están vacíos")
            return
        }

        // Suponiendo que estamos tomando la primera coordenada como ejemplo
        val coordenada = puntosRuta[0]

        // Crear objeto de solicitud
        val request = CrearPeticionRequest(
            longitud = coordenada.longitud,
            latitud = coordenada.latitud,
            precio = precio.toDouble(),
            descripcion = "Descripción de la petición", // Ajusta esto según tu lógica
            estado = "En espera",
            date = Date(),
            user = "user_id", // Reemplaza con el ID del usuario real
            paseador = null // Ajusta esto según tu lógica
        )

        // Realizar la llamada POST a la API
        val call = apiService.crearPeticion(request)
        call.enqueue(object : Callback<RespuestaServidor> {
            override fun onResponse(call: Call<RespuestaServidor>, response: Response<RespuestaServidor>) {
                if (response.isSuccessful) {
                    val respuestaServidor = response.body()
                    respuestaServidor?.let {
                        // Manejar la respuesta del servidor si es necesaria
                        Log.d(TAG, "Respuesta del servidor: ${it.mensaje}")
                    }
                } else {
                    // Manejar errores de respuesta del servidor
                    Log.e(TAG, "Error en la respuesta: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<RespuestaServidor>, t: Throwable) {
                // Manejar error en la llamada
                Log.e(TAG, "Error en la llamada: ${t.message}")
            }
        })
    }

    private fun obtenerPuntosRutaDesdeMapa(): List<Coordenada> {
        // Aquí puedes implementar la lógica para obtener los puntos de la ruta desde el mapa
        // Ejemplo:
        val puntosRuta = mutableListOf<Coordenada>()
        // Obtener los puntos de la ruta y añadirlos a la lista puntosRuta
        // puntosRuta.add(Coordenada(latitud, longitud))
        return puntosRuta
    }

    private fun obtenerPrecioIngresado(): String {
        // Aquí puedes implementar la lógica para obtener el precio ingresado
        // Ejemplo:
        val editTextPrecio: EditText = view?.findViewById(R.id.priceEditText) ?: return ""
        return editTextPrecio.text.toString()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isMyLocationButtonEnabled = true
        mMap.setOnMapClickListener(this)

        val locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 17f))
                }
            }
        }

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null
            )
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }

        val defaultLocation = LatLng(2.43823, -76.61316)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12f))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    val locationRequest = LocationRequest.create().apply {
                        interval = 10000
                        fastestInterval = 5000
                        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                    }
                    fusedLocationClient.requestLocationUpdates(
                        locationRequest,
                        locationCallback,
                        null
                    )
                    mMap.isMyLocationEnabled = true
                }
            }
        }
    }

    fun drawRoute(startLatLng: LatLng?, endLatLng: LatLng?) {
        if (startLatLng == null || endLatLng == null) {
            // Mostrar un mensaje indicando que se necesitan puntos de inicio y destino
            Toast.makeText(
                requireContext(),
                "Marca un punto de inicio y un punto de destino",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Dibujar la ruta en el mapa
        CoroutineScope(Dispatchers.IO).launch {
            requestDirections(startLatLng, endLatLng)
        }
    }

    override fun onMapClick(latlng: LatLng) {
        if (startLatLng == null) {
            startLatLng = latlng
            Toast.makeText(requireContext(), "Punto de inicio seleccionado", Toast.LENGTH_SHORT)
                .show()
        } else if (endLatLng == null) {
            endLatLng = latlng
            Toast.makeText(requireContext(), "Punto de destino seleccionado", Toast.LENGTH_SHORT)
                .show()

            // Si ambos puntos están seleccionados, muestra el diálogo para ingresar el precio
            if (startLatLng != null && endLatLng != null) {
                showRequestDialog(startLatLng!!, endLatLng!!)
            }
        }
    }

    private fun showRequestDialog(startLatLng: LatLng, endLatLng: LatLng) {
        val dialog = RequestDialogFragment.newInstance(startLatLng, endLatLng)
        dialog.setTargetFragment(this, REQUEST_DIALOG_CODE)
        dialog.show(parentFragmentManager, "RequestDialogFragment")
    }

    private suspend fun requestDirections(startLatLng: LatLng, endLatLng: LatLng) {
        try {
            val geoApiContext = GeoApiContext.Builder()
                .apiKey(getString(R.string.google_maps_key))
                .build()

            val directionsResult = DirectionsApi.newRequest(geoApiContext)
                .origin(com.google.maps.model.LatLng(startLatLng.latitude, startLatLng.longitude))
                .destination(com.google.maps.model.LatLng(endLatLng.latitude, endLatLng.longitude))
                .mode(TravelMode.DRIVING)
                .await()

            withContext(Dispatchers.Main) {
                if (directionsResult.routes.isNotEmpty()) {
                    val route = directionsResult.routes[0]
                    val polylineOptions = PolylineOptions()

                    route.legs.forEach { leg ->
                        leg.steps.forEach { step ->
                            val points = step.polyline.decodePath()
                            points.forEach { point ->
                                polylineOptions.add(LatLng(point.lat, point.lng))
                            }
                        }
                    }

                    polylineOptions.width(10f)
                    polylineOptions.color(Color.BLUE)
                    mMap.addPolyline(polylineOptions)
                } else {
                    Toast.makeText(requireContext(), "No se encontraron rutas", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "Error al obtener la ruta: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onPriceEntered(price: String) {
        // Aquí se podría manejar la lógica del precio ingresado si fuera necesario
        drawRoute(startLatLng, endLatLng)
    }

    companion object {
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
        private const val REQUEST_DIALOG_CODE = 2
    }
}
