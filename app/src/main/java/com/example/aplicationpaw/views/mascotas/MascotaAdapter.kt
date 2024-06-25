package com.example.aplicationpaw.views.mascotas

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.aplicationpaw.R
import com.example.aplicationpaw.modelos.Mascota

class MascotaAdapter(
    private val mascotas: List<Mascota>,
    private val onItemClick: (Mascota) -> Unit
) : RecyclerView.Adapter<MascotaAdapter.MascotaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MascotaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_card_mascotas, parent, false)
        return MascotaViewHolder(view)
    }

    override fun onBindViewHolder(holder: MascotaViewHolder, position: Int) {
        val mascota = mascotas[position]
        holder.nombreTextView.text = mascota.nombre
        holder.fotoImageView.setImageResource(R.drawable.fotomascota)
        holder.generoTextView.text = mascota.genero
        holder.razaTextView.text = mascota.raza
        holder.pesoTextView.text = mascota.peso.toString()
        holder.itemView.setOnClickListener {
            Log.d("MascotaAdapter", "Click en mascota: ${mascota.nombre}, ID: ${mascota._id}")
            onItemClick(mascota)
        }
    }

    override fun getItemCount(): Int = mascotas.size

    class MascotaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fotoImageView: ImageView = itemView.findViewById(R.id.fotoImageView)
        val nombreTextView: TextView = itemView.findViewById(R.id.nombreTextView)
        val generoTextView: TextView = itemView.findViewById(R.id.generoTextView)
        val razaTextView: TextView = itemView.findViewById(R.id.razaTextView)
        val pesoTextView: TextView = itemView.findViewById(R.id.pesoTextView)
    }
}