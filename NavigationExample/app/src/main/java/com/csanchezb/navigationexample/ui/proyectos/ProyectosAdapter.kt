package com.csanchezb.navigationexample.ui.proyectos

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.csanchezb.navigationexample.R
import com.csanchezb.navigationexample.entities.cls_Proyectos


class ProyectosAdapter(
    context: Context,
    private val proyectosList: ArrayList<cls_Proyectos>
) : ArrayAdapter<cls_Proyectos>(context, 0, proyectosList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var listItemView = convertView
        if (listItemView == null) {
            listItemView = LayoutInflater.from(context).inflate(R.layout.item_proyecto, parent, false)
        }

        val proyecto = getItem(position)

        // Vistas en el diseño de item_proyecto.xml
        val tituloView = listItemView!!.findViewById<TextView>(R.id.tituloProyecto)
        val areaView = listItemView.findViewById<TextView>(R.id.areaProyecto)
        val correoView = listItemView.findViewById<TextView>(R.id.correoProyecto)
        val descripcionView = listItemView.findViewById<TextView>(R.id.descripcionProyecto)
        val pdfButton = listItemView.findViewById<Button>(R.id.pdfButtonProyecto)

        // Asignar valores del objeto cls_Proyectos
        tituloView.text = proyecto?.titulo
        areaView.text = proyecto?.area
        correoView.text = proyecto?.correo
        descripcionView.text = proyecto?.descripcion

        // Manejar el botón para abrir el PDF
        pdfButton.setOnClickListener {
            proyecto?.pdfLink?.let { pdfUrl ->
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(pdfUrl))
                context.startActivity(intent)
            }
        }

        // Listener opcional para el clic en el ítem
        listItemView.setOnClickListener {
            Toast.makeText(context, "Proyecto seleccionado: ${proyecto?.titulo}", Toast.LENGTH_SHORT).show()
        }

        return listItemView
    }
}