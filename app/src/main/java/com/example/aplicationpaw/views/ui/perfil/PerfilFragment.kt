package com.example.aplicationpaw.views.ui.perfil

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
import android.widget.TextView
import com.example.aplicationpaw.R
import com.example.aplicationpaw.views.editar_mascotas.EditarMascotas
import com.example.aplicationpaw.views.editar_perfil.EditarPerfil
import com.example.aplicationpaw.views.login.Login

class PerfilFragment : Fragment() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val perfil = inflater.inflate(R.layout.fragment_perfil, container, false)

        // Inicializar las preferencias compartidas
        sharedPreferences = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE)

        // Obtener el nombre de usuario de las preferencias compartidas
        val nombreUsuario = sharedPreferences.getString("nombre_usuario", "")

        // Obtener una referencia al TextView donde se mostrará el nombre de usuario
        val textViewNombreUsuario = perfil.findViewById<TextView>(R.id.textViewNombreUsuario)

        // Establecer el nombre de usuario en el TextView
        textViewNombreUsuario.text = nombreUsuario ?: "Usuario"

        // Obtener una referencia al botón de cerrar sesión
        val btnCerrarSesion = perfil.findViewById<Button>(R.id.btnCerrarSesion)

        // Establecer un clic en el botón de cerrar sesión
        btnCerrarSesion.setOnClickListener {
            // Eliminar indicador de sesión iniciada
            sharedPreferences.edit().putBoolean("is_logged_in", false).apply()
            // Vaciar cualquier otro dato de sesión guardado
            sharedPreferences.edit().remove("nombre_usuario").apply()
            sharedPreferences.edit().remove("correo_usuario").apply()

            // Redirigir al usuario a la pantalla de inicio de sesión
            val intent = Intent(requireContext(), Login::class.java)
            startActivity(intent)
            requireActivity().finish() // Finalizar la actividad actual
        }

        // Obtener una referencia al LinearLayout para editar perfil
        val btnEditarPerfil = perfil.findViewById<LinearLayout>(R.id.btnEditarPerfil)

        // Establecer un clic en el LinearLayout de editar perfil
        btnEditarPerfil.setOnClickListener {
            // Redirigir al usuario a la pantalla de editar perfil
            val intent = Intent(requireContext(), EditarPerfil::class.java)
            startActivity(intent)
        }

        val btnMostrarMascota = perfil.findViewById<LinearLayout>(R.id.btnMisMascotas)

        btnMostrarMascota.setOnClickListener {
            val intent = Intent(requireContext(), EditarMascotas::class.java)
            startActivity(intent)
        }

        return perfil
    }
}
