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
import com.csanchezb.navigationexample.entities.cls_Proyectos
import com.csanchezb.navigationexample.ui.proyectos.ProyectosAdapter

class ProjectFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var firestore: FirebaseFirestore
    private lateinit var projectAdapter: ProyectosAdapter
    private var projectList: ArrayList<cls_Proyectos> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        firestore = FirebaseFirestore.getInstance()

        // Inicializar el adaptador
        projectAdapter = ProyectosAdapter(requireContext(), projectList)
        binding.listViewProjects.adapter = projectAdapter

        updateSpinners()
        binding.btnResetArea.setOnClickListener { resetFilters() }

        return binding.root
    }

    private fun updateSpinners() {
        firestore.collection("Pruebas").get()
            .addOnSuccessListener { querySnapshot ->
                val areas = mutableSetOf("Todos")
                val ciclos = mutableSetOf("Todos")

                for (doc in querySnapshot) {
                    val area = doc["area"] as? String
                    val ciclo = doc["ciclo"] as? String

                    area?.let { areas.add(it) }
                    ciclo?.let { ciclos.add(it) }
                }

                Log.d("Firebase", "Areas: $areas")  // Verificar áreas obtenidas
                Log.d("Firebase", "ciclo: $ciclos")  // Verificar grados obtenidos

                setSpinnerOptions(binding.spinnerArea, areas.toList())
                setSpinnerOptions(binding.spinnerGrado, ciclos.toList())

                binding.spinnerArea.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        fetchProjects()
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }

                binding.spinnerGrado.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        Log.d("Spinner", "Item seleccionado: ${parent?.getItemAtPosition(position)}")  // Verificar qué se seleccionó
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
        Log.d("Spinner", "Opciones: $options")  // Verificar las opciones que estás pasando al spinner
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }


    private fun fetchProjects() {
        var query: Query = firestore.collection("Pruebas")

        val selectedArea = binding.spinnerArea.selectedItem.toString()
        val selectedCiclo = binding.spinnerGrado.selectedItem.toString()

        if (selectedArea != "Todos") {
            query = query.whereEqualTo("area", selectedArea)
        }
        if (selectedCiclo != "Todos") {
            query = query.whereEqualTo("grado", selectedCiclo)
        }

        query.get()
            .addOnSuccessListener { querySnapshot ->
                projectList.clear()
                for (doc in querySnapshot) {
                    val data = doc.data
                    val project = cls_Proyectos(
                        titulo = data["titulo"].toString(),
                        area = data["area"].toString(),
                        usuario = data["usuario"].toString(),
                        descripcion = data["descripcion"].toString(),
                        pdfLink = data["PDF"].toString()
                    )
                    projectList.add(project)
                }
                projectAdapter.notifyDataSetChanged()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}