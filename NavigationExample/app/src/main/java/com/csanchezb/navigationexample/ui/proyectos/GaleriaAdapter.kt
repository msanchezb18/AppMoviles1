package com.csanchezb.navigationexample.ui.proyectos

import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.csanchezb.navigationexample.R
import com.csanchezb.navigationexample.entities.cls_Galeria
import com.google.firebase.storage.FirebaseStorage

class GaleriaAdapter(
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

        // Configurar RecyclerView horizontal para las imágenes
        holder.imagenesRecyclerView.layoutManager =
            LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)
        holder.imagenesRecyclerView.adapter = ImagenesInnerAdapter(investigacion.imagenesUrl)
    }

    override fun getItemCount() = investigaciones.size

    // Adaptador interno para las imágenes
    inner class ImagenesInnerAdapter(
        private val imagenesUrls: List<String>
    ) : RecyclerView.Adapter<ImagenesInnerAdapter.ImagenViewHolder>() {

        inner class ImagenViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val imageView: ImageView = view.findViewById(R.id.imagenView)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagenViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_imagen, parent, false)
            return ImagenViewHolder(view)
        }

        override fun onBindViewHolder(holder: ImagenViewHolder, position: Int) {
            val imagenUrl = imagenesUrls[position]

            val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imagenUrl)
            storageReference.getBytes(5 * 1024 * 1024)
                .addOnSuccessListener { bytes ->
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    holder.imageView.setImageBitmap(bitmap)
                }
                .addOnFailureListener { exception ->
                    Log.e("Firebase", "Error al descargar la imagen: ${exception.message}")
                    holder.imageView.setImageResource(R.drawable.ic_launcher_background)
                }
        }

        override fun getItemCount() = imagenesUrls.size
    }

}
