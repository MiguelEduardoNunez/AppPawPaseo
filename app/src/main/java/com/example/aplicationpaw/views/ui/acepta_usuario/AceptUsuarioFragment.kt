package com.example.aplicationpaw.views.ui.acepta_usuario

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.aplicationpaw.R
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class AceptUsuarioFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var database: DatabaseReference

    private lateinit var view: View;
    private lateinit var userLogin: String
    private lateinit var user: String
    private lateinit var precio: String

    private lateinit var btnValor: Button
    private lateinit var btnCancelar: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_acept_usuario, container, false)
        return view;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //firebase guardar datos
        database = Firebase.database.reference;

        sharedPreferences =
            requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE)
        // Obtener el nombre de usuario de las preferencias compartidas
        userLogin = sharedPreferences.getString("nombre_usuario", "") ?: ""

        //buscar elementos en la vista
        btnValor = view.findViewById(R.id.btn_valor);
        btnCancelar = view.findViewById(R.id.btn_omitir);

        //iniciar logica
        init(view);
        //iniciar funciones onclick
        initOnClick();
    }

    fun init(view: View){
        user = getArguments()?.getString("user") ?: "";
        precio = getArguments()?.getString("precio") ?: "";

        view.findViewById<TextView>(R.id.nick_usuario).text = user
        btnValor.text = precio;
    }

    fun initOnClick(){
        btnCancelar.setOnClickListener {
            database.child(userLogin).child(getString(R.string.paseadores)).child(user).child("status")
                .setValue(view.context?.getString(R.string.cancelado)).addOnSuccessListener {

                    database.child(userLogin).child("status")
                        .setValue(view.context?.getString(R.string.nuevo)).addOnSuccessListener {
                            Toast.makeText(context, "Se cancelo al servicio.", Toast.LENGTH_LONG).show();
                            findNavController().popBackStack();
                        }.addOnFailureListener {
                            Toast.makeText(context, "Fallo", Toast.LENGTH_LONG).show();
                        }

                }.addOnFailureListener {
                    Toast.makeText(context, "Fallo", Toast.LENGTH_LONG).show();
                }
        }
    }
}