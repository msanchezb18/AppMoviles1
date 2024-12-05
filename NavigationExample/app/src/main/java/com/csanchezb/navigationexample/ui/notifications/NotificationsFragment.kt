package com.csanchezb.navigationexample.ui.notifications

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.csanchezb.navigationexample.databinding.FragmentNotificationsBinding
import com.csanchezb.navigationexample.entities.cls_Galeria
import com.csanchezb.navigationexample.ui.proyectos.GaleriaAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    private lateinit var firestore: FirebaseFirestore
    private lateinit var investigacionesAdapter: GaleriaAdapter

    private val investigaciones = mutableListOf<cls_Galeria>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)

        firestore = FirebaseFirestore.getInstance()

        // Configurar RecyclerView
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        investigacionesAdapter = GaleriaAdapter(investigaciones)
        recyclerView.adapter = investigacionesAdapter

        updateSpinners()
        binding.btnResetArea.setOnClickListener { resetFilters() }

        return binding.root
    }

    private fun updateSpinners() {
        firestore.collection("Pruebas").get()
            .addOnSuccessListener { querySnapshot ->
                val areas = mutableSetOf("Todos")
                val grados = mutableSetOf("Todos")

                for (doc in querySnapshot) {
                    val area = doc["area"] as? String
                    val grado = doc["grado"] as? String

                    area?.let { areas.add(it) }
                    grado?.let { grados.add(it) }
                }

                setSpinnerOptions(binding.spinnerArea, areas.toList())
                setSpinnerOptions(binding.spinnerGrado, grados.toList())

                binding.spinnerArea.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        cargarDatos()
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }

                binding.spinnerGrado.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        cargarDatos()
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Error al obtener datos", e)
            }
    }

    private fun setSpinnerOptions(spinner: Spinner, options: List<String>) {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun cargarDatos() {
        val query = firestore.collection("Pruebas")

        query.get().addOnSuccessListener { result ->
            investigaciones.clear()
            investigacionesAdapter.notifyDataSetChanged()

            val storageRef = FirebaseStorage.getInstance().reference

            for (document in result) {
                val titulo = document.getString("titulo") ?: ""
                val descripcion = document.getString("descripcion") ?: ""
                val imagenPaths = listOfNotNull(
                    document.getString("imagenPath1"),
                    document.getString("imagenPath2"),
                    document.getString("imagenPath3"),
                    document.getString("imagenPath4")
                )

                // Crear la investigación y agregarla a la lista inmediatamente
                val investigacion = cls_Galeria(
                    tittle = titulo,
                    descrip = descripcion,
                    imagenesUrl = mutableListOf() // Se llenará más adelante
                )
                investigaciones.add(investigacion)
                investigacionesAdapter.notifyDataSetChanged()

                if (imagenPaths.isEmpty()) {
                    // Si no hay imágenes, simplemente continúa
                    continue
                }

                // Cargar imágenes si hay rutas
                val imagenUrls = mutableListOf<String>()
                imagenPaths.forEach { path ->
                    val fullReference = storageRef.child(path)
                    fullReference.downloadUrl.addOnSuccessListener { uri ->
                        imagenUrls.add(uri.toString())

                        // Actualizar la investigación una vez que todas las imágenes estén listas
                        if (imagenUrls.size == imagenPaths.size) {
                            investigacion.imagenesUrl.addAll(imagenUrls)
                            investigacionesAdapter.notifyDataSetChanged()
                        }
                    }.addOnFailureListener { exception ->
                        Log.e("Firebase", "Error al obtener la URL: ${exception.message}")
                    }
                }
            }
        }.addOnFailureListener { exception ->
            Log.e("Firestore", "Error al cargar datos", exception)
        }
    }



    private fun resetFilters() {
        binding.spinnerArea.setSelection(0)
        binding.spinnerGrado.setSelection(0)
        cargarDatos()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

private fun <E> List<E>.addAll(imagenUrls: MutableList<E>) {

}
