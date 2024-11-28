package com.csanchezb.navigationexample.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.csanchezb.navigationexample.databinding.FragmentHomeBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ProjectFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        firestore = FirebaseFirestore.getInstance()

        // Configurar las vistas
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

                binding.spinnerArea.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            fetchProjects()
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {}
                    }

                binding.spinnerGrado.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            fetchProjects()
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

    private fun fetchProjects() {
        var query: Query = firestore.collection("Pruebas")

        val selectedArea = binding.spinnerArea.selectedItem.toString()
        val selectedGrado = binding.spinnerGrado.selectedItem.toString()

        if (selectedArea != "Todos") {
            query = query.whereEqualTo("area", selectedArea)
        }
        if (selectedGrado != "Todos") {
            query = query.whereEqualTo("grado", selectedGrado)
        }

        query.get()
            .addOnSuccessListener { querySnapshot ->
                binding.investigationsTable.removeAllViews() // Limpia la tabla

                if (querySnapshot.isEmpty) {
                    val row = TableRow(requireContext())
                    val noData = TextView(requireContext()).apply { text = "No se encontraron proyectos." }
                    row.addView(noData)
                    binding.investigationsTable.addView(row)
                    return@addOnSuccessListener
                }

                for (doc in querySnapshot) {
                    val data = doc.data
                    val row = TableRow(requireContext())

                    // Crear el título como TextView
                    val title = TextView(requireContext()).apply {
                        text = data["titulo"].toString() // Asignar el valor del título
                        layoutParams = TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                        )
                        setPadding(16, 16, 16, 16) // Añadir relleno
                    }

                    // Crear las demás celdas (área, autor, PDF)
                    val area = TextView(requireContext()).apply { text = data["area"].toString() }
                    val author = TextView(requireContext()).apply { text = data["Correo"].toString() }
                    val pdfButton = Button(requireContext()).apply {
                        text = "Descargar PDF"
                        setOnClickListener {
                            val pdfUrl = data["PDF"].toString()
                            downloadPdf(pdfUrl)
                        }
                    }

                    // Agregar las celdas a la fila
                    row.addView(title)
                    row.addView(area)
                    row.addView(author)
                    row.addView(pdfButton)

                    // Agregar la fila a la tabla
                    binding.investigationsTable.addView(row)
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Error al buscar proyectos", e)
            }
    }

    private fun resetFilters() {
        binding.spinnerArea.setSelection(0)
        binding.spinnerGrado.setSelection(0)
        fetchProjects()
    }

    private fun downloadPdf(pdfUrl: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(pdfUrl))
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}