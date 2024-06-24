package com.example.aplicationpaw.views.mascotas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.aplicationpaw.R
import com.example.aplicationpaw.modelos.Mascota

class MascotaAdapter(private val mascotas: List<Mascota>) :
    RecyclerView.Adapter<MascotaAdapter.MascotaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MascotaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_card_mascotas, parent, false)
        return MascotaViewHolder(view)
    }

    override fun onBindViewHolder(holder: MascotaViewHolder, position: Int) {
        val mascota = mascotas[position]
        holder.nombreTextView.text = mascota.nombre
        // Asigna la imagen por defecto
        holder.fotoImageView.setImageResource(R.drawable.fotomascota)
    }

    override fun getItemCount(): Int {
        return mascotas.size
    }

    class MascotaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fotoImageView: ImageView = itemView.findViewById(R.id.fotoImageView)
        val nombreTextView: TextView = itemView.findViewById(R.id.nombreTextView)
    }
}
