package com.example.aplicationpaw.views.ui.biblioteca

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aplicationpaw.R
import com.example.aplicationpaw.views.ui.biblioteca.adapter.CardAdapter
import com.example.aplicationpaw.views.ui.biblioteca.data.CardData

class BibliotecaFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_biblioteca, container, false)
        val recyclerView: RecyclerView = rootView.findViewById(R.id.recyclerView)

        val dataList = listOf(
            CardData("Programas de entrenamiento", R.drawable.perro1),
            CardData("Juegos", R.drawable.perro2),
            CardData("Ejercicios", R.drawable.perro3),
            CardData("Artículos", R.drawable.perro4)
        )

        val adapter = CardAdapter(requireContext(), dataList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        return rootView
    }
}
