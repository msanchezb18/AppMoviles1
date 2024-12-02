package com.csanchezb.navigationexample.ui.proyectos

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.csanchezb.navigationexample.R
import com.csanchezb.navigationexample.entities.cls_Galeria
import com.csanchezb.navigationexample.ui.proyectos.ImagenesAdapter


class GaleriaAdapter (
    private val investigaciones: List<cls_Galeria>
) : RecyclerView.Adapter<GaleriaAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titulo: TextView = view.findViewById(R.id.titulo)
        val descripcion: TextView = view.findViewById(R.id.descripcion)
        val imagenesRecyclerView: RecyclerView = view.findViewById(R.id.imagenesRecyclerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_galeria, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val investigacion = investigaciones[position]
        holder.titulo.text = investigacion.tittle
        holder.descripcion.text = investigacion.descrip

        // Configura el RecyclerView horizontal para las imágenes
        holder.imagenesRecyclerView.layoutManager =
            LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)
        holder.imagenesRecyclerView.adapter = ImagenesAdapter(investigacion.imagenesUrl)
    }


    override fun getItemCount() = investigaciones.size
}