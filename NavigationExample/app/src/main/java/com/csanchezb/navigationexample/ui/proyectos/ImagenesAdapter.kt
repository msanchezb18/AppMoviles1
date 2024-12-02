package com.csanchezb.navigationexample.ui.proyectos

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.csanchezb.navigationexample.R
import com.csanchezb.navigationexample.entities.cls_Galeria
import com.google.firebase.storage.FirebaseStorage

class ImagenesAdapter(
    private val imagenesUrls: List<String>
) : RecyclerView.Adapter<ImagenesAdapter.ImagenViewHolder>() {

    class ImagenViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imagenView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagenViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_imagen, parent, false)
        return ImagenViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImagenViewHolder, position: Int) {
        val imagenUrl = imagenesUrls[position]

        // Cargar la imagen desde Firebase Storage
        val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imagenUrl)
        storageReference.getBytes(5 * 1024 * 1024)
            .addOnSuccessListener { bytes ->
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                holder.imageView.setImageBitmap(bitmap)
            }
            .addOnFailureListener {
                holder.imageView.setImageResource(R.drawable.ic_launcher_background)
            }
    }

    override fun getItemCount() = imagenesUrls.size
}