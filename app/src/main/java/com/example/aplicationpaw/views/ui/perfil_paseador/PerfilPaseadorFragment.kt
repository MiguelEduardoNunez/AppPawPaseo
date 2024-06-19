package com.example.aplicationpaw.views.ui.perfil_paseador

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import com.example.aplicationpaw.R
import com.example.aplicationpaw.views.login.Login

class PerfilPaseadorFragment : Fragment() {
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val perfilPaseador = inflater.inflate(R.layout.fragment_perfil_paseador, container, false)

        // Obtener una referencia al botón de cerrar sesión
        val btnCerrarSesion = perfilPaseador.findViewById<Button>(R.id.btnCerrarSesion)

        sharedPreferences = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE)


        // Establecer un clic en el botón de cerrar sesión
        btnCerrarSesion.setOnClickListener {
            // Eliminar indicador de sesión iniciada
            sharedPreferences.edit().putBoolean("is_logged_in", false).apply()
            // Vaciar cualquier otro dato de sesión guardado
            sharedPreferences.edit().remove("user_role").apply()

            // Redirigir al usuario a la pantalla de inicio de sesión
            val intent = Intent(requireContext(), Login::class.java)
            startActivity(intent)
            requireActivity().finish() // Finalizar la actividad actual
        }

        // Obtener una referencia al LinearLayout para editar perfil
        //val btnEditarPerfil = perfilPaseador.findViewById<LinearLayout>(R.id.btnEditarPerfil)

        // Establecer un clic en el LinearLayout de editar perfil
        //btnEditarPerfil.setOnClickListener {
            // Redirigir al usuario a la pantalla de editar perfil de paseador
            //val intent = Intent(requireContext(), EditarPerfilPaseador::class.java)
            //startActivity(intent)
        //}


        return perfilPaseador
    }


}