package com.example.aplicationpaw.views.ui.home_paseador

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.aplicationpaw.R
import com.example.aplicationpaw.modelos.PeticionPaseo

class HomePaseadorAdapter(private val peticionesPaseos: List<PeticionPaseo>, private val onClickItem: OnClickItem ) : RecyclerView.Adapter<HomePaseadorAdapter.HomePaseadorViewHolder>() {

    interface OnClickItem {
        fun click(peticionPaseo: PeticionPaseo);
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomePaseadorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_card_paseador, parent, false)
        return HomePaseadorViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomePaseadorViewHolder, position: Int) {
        val peticionPaseo = peticionesPaseos[position];
        holder.usuario.text = peticionPaseo.user;
        holder.valor.text = peticionPaseo.precio;

        //click
        holder.card.setOnClickListener(View.OnClickListener {
            onClickItem.click(peticionPaseo);
        });
    }

    override fun getItemCount(): Int {
        return peticionesPaseos.size;
    }

    class HomePaseadorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val card : CardView = itemView.findViewById(R.id.paseador_card);
        val usuario: TextView = itemView.findViewById(R.id.paseador_usuario);
        val valor: Button = itemView.findViewById(R.id.btn_valor);
    }
}