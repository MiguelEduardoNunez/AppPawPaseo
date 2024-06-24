package com.example.aplicationpaw.views.ui.home_paseador

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.aplicationpaw.R
import com.example.aplicationpaw.modelos.Paseador

class HomePaseadorAdapter(private val paseadores: List<Paseador>) : RecyclerView.Adapter<HomePaseadorAdapter.HomePaseadorViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomePaseadorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_card_paseador, parent, false)
        return HomePaseadorViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomePaseadorViewHolder, position: Int) {
        val paseador = paseadores[position];
        holder.usuario.text = paseador.usuario;
        holder.valor.text = paseador.valor.toString();
    }

    override fun getItemCount(): Int {
        return paseadores.size;
    }

    class HomePaseadorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val usuario: TextView = itemView.findViewById(R.id.paseador_usuario);
        val valor: Button = itemView.findViewById(R.id.btn_valor);
    }
}