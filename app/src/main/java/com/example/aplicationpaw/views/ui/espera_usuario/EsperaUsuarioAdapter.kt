package com.example.aplicationpaw.views.ui.espera_usuario

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.aplicationpaw.R
import com.example.aplicationpaw.modelos.PeticionPaseador

class EsperaUsuarioAdapter (private val peticionPaseadores: List<PeticionPaseador>, private val onClickItem: OnClickItem) : RecyclerView.Adapter<EsperaUsuarioAdapter.EsperaUsuarioViewHolder>() {

    private lateinit var view: View

    interface OnClickItem {
        fun aceptar(peticionPaseador: PeticionPaseador);
        fun omitir(peticionPaseador: PeticionPaseador);
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EsperaUsuarioAdapter.EsperaUsuarioViewHolder {
        view = LayoutInflater.from(parent.context).inflate(R.layout.activity_card_esperausuario, parent, false)
        return EsperaUsuarioViewHolder(view)
    }

    override fun onBindViewHolder(holder: EsperaUsuarioViewHolder, position: Int) {
        val peticionPaseo = peticionPaseadores[position];

        val valor = view.context.getString(R.string.aceptar_por_cop_valor).replace("COP", peticionPaseo.precio);

        holder.usuario.text = peticionPaseo.user;
        holder.btnAceptar.text = valor

        //click
        holder.btnAceptar.setOnClickListener {
            onClickItem.aceptar(peticionPaseo);
        };
        holder.omitir.setOnClickListener {
            onClickItem.omitir(peticionPaseo);
        };
    }

    override fun getItemCount(): Int {
        return peticionPaseadores.size;
    }

    class EsperaUsuarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val usuario: TextView = itemView.findViewById(R.id.paseador_usuario);
        val btnAceptar: Button = itemView.findViewById(R.id.btn_Aceptar);
        val omitir: Button = itemView.findViewById(R.id.btn_Rechazar);
    }
}