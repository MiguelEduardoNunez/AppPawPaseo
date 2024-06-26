package com.example.aplicationpaw.views.ui.mapa

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.aplicationpaw.MainActivity
import com.example.aplicationpaw.R
import com.example.aplicationpaw.modelos.CrearPeticionRequest
import com.example.aplicationpaw.modelos.PeticionPaseo
import com.example.aplicationpaw.modelos.RespuestaServidor
import com.example.vfragment.networking.ApiService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.model.TravelMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var startLatLng: LatLng? = null
    private var endLatLng: LatLng? = null
    private var price: String? = null
    private var userId: String? = null
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var requestService: ApiService
    private lateinit var database: DatabaseReference

    interface OnRouteDrawnListener {
        fun onRouteDrawn(startLatLng: LatLng, endLatLng: LatLng)
    }

    var routeListener: OnRouteDrawnListener? = null

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
        sharedPreferences = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE)

        // Inicializar el mapa
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapa) as SupportMapFragment
        mapFragment.getMapAsync(this)
        arguments?.let {
            price = it.getString("PRICE")
            userId = it.getString("USER_ID")
        }

        // Configurar botón de marcar ruta
        val btnMarcarRuta = view.findViewById<ImageButton>(R.id.btnMarcarRuta)
        btnMarcarRuta.setOnClickListener {
            if (startLatLng == null || endLatLng == null) {
                Toast.makeText(requireContext(), "Selecciona un punto de inicio y un punto final en el mapa", Toast.LENGTH_SHORT).show()
            } else {
                drawRoute(startLatLng, endLatLng)
            }
        }

        val retrofit = Retrofit.Builder()
            .baseUrl("https://pawpaseo-backend-phi.vercel.app/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        requestService = retrofit.create(ApiService::class.java)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isMyLocationButtonEnabled = true
        mMap.setOnMapClickListener(this)

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val currentLatLng = LatLng(it.latitude, it.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 17f))
                }
            }
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }

        val defaultLocation = LatLng(2.43823, -76.61316)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12f))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    mMap.isMyLocationEnabled = true
                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        location?.let {
                            val currentLatLng = LatLng(it.latitude, it.longitude)
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 17f))
                        }
                    }
                }
            }
        }
    }

    override fun onMapClick(latlng: LatLng) {
        if (startLatLng == null) {
            startLatLng = latlng
            mMap.addMarker(
                MarkerOptions().position(startLatLng!!).title("Inicio").icon(
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                )
            )
            Toast.makeText(requireContext(), "Punto de inicio seleccionado", Toast.LENGTH_SHORT)
                .show()
        } else if (endLatLng == null) {
            endLatLng = latlng
            mMap.addMarker(
                MarkerOptions().position(endLatLng!!).title("Destino")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            )
            Toast.makeText(requireContext(), "Punto de destino seleccionado", Toast.LENGTH_SHORT)
                .show()
            drawRoute(startLatLng, endLatLng)  // Llamar a drawRoute aquí
            showPriceDialog()
        }
    }
    //inicializar lateinit var sharedPreferences


    private fun showPriceDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.activity_dialog_price, null)
        builder.setView(dialogView)

        val input = dialogView.findViewById<EditText>(R.id.inputPrice)

        builder.setPositiveButton("OK") { dialog, which ->
            val price = input.text.toString()
            if (price != "") {
                createRequest(price)
            } else {
                Toast.makeText(
                    requireContext(),
                    "El precio no puede estar vacío",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        builder.setNegativeButton("Cancelar") { dialog, which -> dialog.cancel() }
        builder.show()
    }

    private fun createRequest(precio : String) {
        val userId = sharedPreferences.getString("user_id", "") ?: ""
        if (userId.isEmpty()) {
            Toast.makeText(
                requireContext(),
                "Error: ID de usuario no encontrado",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val request = CrearPeticionRequest(
            longitud = endLatLng?.longitude ?: 0.0,
            latitud = endLatLng?.latitude ?: 0.0,
            precio = precio,
            user = userId
        )

        val call = requestService.crearPeticion(request)
        call.enqueue(object : Callback<RespuestaServidor> {
            override fun onResponse(
                call: Call<RespuestaServidor>,
                response: Response<RespuestaServidor>
            ) {
                if (response.isSuccessful) {
                    //firebase guardar datos
                    database = Firebase.database.reference;
                    val usuario_nombre = sharedPreferences.getString("nombre_usuario", null) ?: ""

                    val newElement = PeticionPaseo(
                        startLatLng?.longitude ?: 0.0,
                        startLatLng?.latitude ?: 0.0,
                        endLatLng?.longitude ?: 0.0,
                        endLatLng?.latitude ?: 0.0,
                        request.precio,
                        usuario_nombre,
                        getString(R.string.nuevo)
                    );
                    database.child(usuario_nombre).setValue(newElement)
                        .addOnSuccessListener {
                            (activity as MainActivity?)?.loadEsperaUsuario()
                        }
                        .addOnFailureListener { error ->
                            Log.e("Database", "Error al guardar datos en Firebase", error)
                        }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Error al crear la petición: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<RespuestaServidor>, t: Throwable) {
                Toast.makeText(
                    requireContext(),
                    "Error en la llamada: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun drawRoute(startLatLng: LatLng?, endLatLng: LatLng?) {
        if (startLatLng == null || endLatLng == null) return
        val path: MutableList<LatLng> = ArrayList()
        val context = GeoApiContext.Builder()
            .apiKey(getString(R.string.google_maps_key))
            .build()
        CoroutineScope(Dispatchers.IO).launch {
            val req = DirectionsApi.getDirections(
                context,
                "${startLatLng.latitude},${startLatLng.longitude}",
                "${endLatLng.latitude},${endLatLng.longitude}"
            )
            req.mode(TravelMode.WALKING)

            try {
                val res = req.await()

                val route = res.routes[0]
                if (route != null && route.legs.isNotEmpty()) {
                    val leg = route.legs[0]
                    for (step in leg.steps) {
                        if (step.steps != null && step.steps.isNotEmpty()) {
                            for (innerStep in step.steps) {
                                val points = innerStep.polyline.decodePath()
                                for (point in points) {
                                    path.add(LatLng(point.lat, point.lng))
                                }
                            }
                        } else {
                            val points = step.polyline.decodePath()
                            for (point in points) {
                                path.add(LatLng(point.lat, point.lng))
                            }
                        }
                    }
                }

                withContext(Dispatchers.Main) {
                    if (path.isNotEmpty()) {
                        val opts = PolylineOptions().addAll(path).color(Color.BLUE).width(5f)
                        mMap.addPolyline(opts)
                        routeListener?.onRouteDrawn(startLatLng, endLatLng)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "No se pudo dibujar la ruta",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Error al obtener la dirección: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    companion object {
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    }
}
