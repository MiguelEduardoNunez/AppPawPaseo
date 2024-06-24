package com.example.aplicationpaw.views.ui.detalle_paseador

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.aplicationpaw.R
import com.example.aplicationpaw.modelos.PeticionPaseo
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
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.model.TravelMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetallesPaseadorFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var startLatLng: LatLng? = null
    private var endLatLng: LatLng? = null
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var database: DatabaseReference

    interface OnRouteDrawnListener {
        fun onRouteDrawn(startLatLng: LatLng, endLatLng: LatLng)
    }
    var routeListener: OnRouteDrawnListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detalles_paseador, container, false)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
        return view;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //firebase guardar datos
        database = Firebase.database.reference;

        sharedPreferences =
            requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE)

        // Inicializar el mapa
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapa) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //iniciar logica
        initLogid(view);
    }

    fun initLogid(view: View) {
        val latitudInicial = getArguments()?.getDouble("latitudInicial") ?: 0.0;
        val longitudInicial = getArguments()?.getDouble("longitudInicial") ?: 0.0;
        val latitudFinal = getArguments()?.getDouble("latitudFinal") ?: 0.0;
        val longitudFinal = getArguments()?.getDouble("longitudFinal") ?: 0.0;
        val user = getArguments()?.getString("user") ?: "";
        val precio = getArguments()?.getString("precio") ?: "";

        val precioFinal = getString(R.string.aceptar_por_cop_valor).replace("COP", precio);

        startLatLng = LatLng(latitudInicial, longitudInicial);
        endLatLng = LatLng(latitudFinal, longitudFinal);
        view.findViewById<TextView>(R.id.nick_usuario).text = user;

        val btnAcept = view.findViewById<Button>(R.id.btn_valor);
        val btn500 = view.findViewById<Button>(R.id.btn_valor500);
        val btn1000 = view.findViewById<Button>(R.id.btn_valor1000);
        val btnOmitir = view.findViewById<TextView>(R.id.btn_omitir);

        btnAcept.text = precioFinal;
        btnAcept.setOnClickListener(View.OnClickListener {
            database.child(user).child("status").setValue("ACEPTADO")
                .addOnSuccessListener {
                    Toast.makeText(context, "Guardado", Toast.LENGTH_LONG).show();

                    btnAcept.isEnabled = false;
                    btn500.isEnabled = false;
                    btn1000.isEnabled = false;
                    btnOmitir.isEnabled = false;
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Fallo", Toast.LENGTH_LONG).show();
                }
        });
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
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12f));


        //agregar marcadore y ruta
        mMap.addMarker(
            MarkerOptions().position(startLatLng!!).title("Inicio").icon(
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
            )
        )
        mMap.addMarker(
            MarkerOptions().position(endLatLng!!).title("Destino")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
        )
        drawRoute(startLatLng, endLatLng);
    }

    private fun drawRoute(startLatLng: LatLng?, endLatLng: LatLng?) {
        if (startLatLng == null || endLatLng == null) {
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

                    // Notificar al listener (PaseosFragment) que la ruta ha sido dibujada
                    routeListener?.onRouteDrawn(startLatLng, endLatLng)
                } else {
                    Toast.makeText(requireContext(), "No se encontraron rutas", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    requireContext(),
                    "Error al obtener la ruta: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onMapClick(p0: LatLng) {

    }

    companion object {
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    }
}
