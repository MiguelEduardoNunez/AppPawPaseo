package com.example.aplicationpaw.views.ui.detalle_paseador

import android.Manifest
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
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.aplicationpaw.R
import com.example.aplicationpaw.modelos.PeticionPaseador
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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.gson.Gson
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.model.TravelMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.reflect.Type


class DetallesPaseadorFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var startLatLng: LatLng? = null
    private var endLatLng: LatLng? = null
    private lateinit var sharedPreferences: SharedPreferences

    private var isFristLoad: Boolean = true;
    private lateinit var database: DatabaseReference

    private lateinit var view: View

    private lateinit var userLogin: String
    private lateinit var user: String
    private lateinit var precio: String

    private lateinit var btnAcept: Button
    private lateinit var btn500: Button
    private lateinit var btn1000: Button
    private lateinit var btnOmitir: Button

    interface OnRouteDrawnListener {
        fun onRouteDrawn(startLatLng: LatLng, endLatLng: LatLng)
    }
    var routeListener: OnRouteDrawnListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view = inflater.inflate(R.layout.fragment_detalles_paseador, container, false)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
        return view;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //firebase guardar datos
        database = Firebase.database.reference;

        sharedPreferences =
            requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE)
        // Obtener el nombre de usuario de las preferencias compartidas
        userLogin = sharedPreferences.getString("nombre_usuario", "") ?: ""

        // Inicializar el mapa
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapa) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //iniciar logica
        init(view);
        //iniciar funciones onclick
        initOnClick();
        //para estar pendiente a si lo aceptan o declinan
        initFirebase();
    }

    fun init(view: View) {
        //get arguments
        val latitudInicial = getArguments()?.getDouble("latitudInicial") ?: 0.0;
        val longitudInicial = getArguments()?.getDouble("longitudInicial") ?: 0.0;
        val latitudFinal = getArguments()?.getDouble("latitudFinal") ?: 0.0;
        val longitudFinal = getArguments()?.getDouble("longitudFinal") ?: 0.0;
        user = getArguments()?.getString("user") ?: "";
        precio = getArguments()?.getString("precio") ?: "";

        //guardar datos
        startLatLng = LatLng(latitudInicial, longitudInicial);
        endLatLng = LatLng(latitudFinal, longitudFinal);
        view.findViewById<TextView>(R.id.nick_usuario).text = user;

        //buscar elementos en la vista
        btnAcept = view.findViewById(R.id.btn_valor);
        btn500 = view.findViewById(R.id.btn_valor500);
        btn1000 = view.findViewById(R.id.btn_valor1000);
        btnOmitir = view.findViewById(R.id.btn_omitir);

        //llamar para establercer precio
        changePrice();
    }

    fun changePrice(){
        //inicializar precio
        val precioFinal = getString(R.string.aceptar_por_cop_valor).replace("COP", precio);
        btnAcept.text = precioFinal;
    }

    fun initOnClick(){
        btnAcept.setOnClickListener {
            val paseador = PeticionPaseador(1.0, 1.0, precio, userLogin, getString(R.string.nuevo));

            //se guarda el nuevo status, si no modifico el precio queda como ACEPTADO, si modifica el precio queda como MODIFICADO
            database.child(user).child(getString(R.string.paseadores)).child(userLogin).setValue(paseador)
                .addOnSuccessListener {
                    Toast.makeText(context, "Se envio la notificación al usuario en espera de confirmación.", Toast.LENGTH_LONG).show();
                    enableBtns(false);
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Fallo", Toast.LENGTH_LONG).show();
                }
        };

        btn500.setOnClickListener {
            //se aumenta el precio, se cambia de estatus
            precio = (precio.toInt() + 500).toString();
            changePrice();
        };

        btn1000.setOnClickListener {
            //se aumenta el precio, se cambia de estatus
            precio = (precio.toInt() + 1000).toString();
            changePrice();
        }
    }

    fun enableBtns(enable: Boolean){
        btnAcept.isEnabled = enable;
        btn500.isEnabled = enable;
        btn1000.isEnabled = enable;
        btnOmitir.isEnabled = enable;
    }

    fun initFirebase(){
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    val datas = dataSnapshot.getValue() as HashMap<*, *>
                    val data = Gson().toJson(datas);
                    val paseador = Gson().fromJson(data, PeticionPaseador::class.java)

                    if(!isFristLoad && paseador.status != view.context.getString(R.string.nuevo)) {
                        if(paseador?.status == view.context.getString(R.string.aceptado)){
                            //lo acepto el cliente el precio nuevo
                            Toast.makeText(context, "Se acepto la solicitud por parte del usuario.", Toast.LENGTH_LONG).show();
                        }else{
                            //no acepto el nuevo precio se declina solicitud
                            Toast.makeText(context, "Se declino la solicitud por parte del usuario.", Toast.LENGTH_LONG).show();
                            findNavController().navigate(R.id.homePaseadorFragment);
                        }
                    }else{
                        //se pone en false cuando pase la 1ra ves
                        //para que solo cuando se modifique se lance el flujo
                        //de lo contrario cuando cargue por 1ra ves el fragmen dira que se modifico
                        isFristLoad = false;
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("FIREBASE", "loadPost:onCancelled", databaseError.toException())
            }
        }
        //buscar solo por el usuario los cambios
        database.child(user).child(getString(R.string.paseadores)).child(userLogin).addValueEventListener(postListener);
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
