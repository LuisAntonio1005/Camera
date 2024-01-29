package com.example.camera

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MarcasAdapter(private val data: List<MarcasView.marcasData>) :
    RecyclerView.Adapter<MarcasAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val telar: TextView = itemView.findViewById(R.id.textViewTelar)
        val marca: TextView = itemView.findViewById(R.id.textViewMarca)
        val planta: TextView = itemView.findViewById(R.id.textViewPlanta)
        val turno: TextView = itemView.findViewById(R.id.textViewTurno)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_marcas, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.telar.text = "tel: ${item.telar}"
        holder.marca.text = "Marca: ${item.marca}"
        holder.planta.text = "Planta: ${item.planta}"
        holder.turno.text = "Turno: ${item.turno}"
    }

    override fun getItemCount() = data.size
}
