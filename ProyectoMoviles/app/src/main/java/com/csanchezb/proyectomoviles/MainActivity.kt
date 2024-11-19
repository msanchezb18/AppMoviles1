package com.csanchezb.proyectomoviles

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query

class MainActivity : AppCompatActivity() {

    private lateinit var spinnerArea: Spinner
    private lateinit var spinnerGrado: Spinner
    private lateinit var btnReset: Button
    private lateinit var tableLayout: TableLayout
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Inicializa Firestore
        firestore = FirebaseFirestore.getInstance()

        // Conecta las vistas
        spinnerArea = findViewById(R.id.spinnerArea)
        spinnerGrado = findViewById(R.id.spinnerGrado)
        btnReset = findViewById(R.id.btnResetArea)
        tableLayout = findViewById(R.id.investigationsTable)

        // Llama a las funciones principales
        updateSpinners()
        btnReset.setOnClickListener { resetFilters() }

    }

    private fun updateSpinners() {
        firestore.collection("Pruebas").get()
            .addOnSuccessListener { querySnapshot ->
                val areas = mutableSetOf("Todos")
                val grados = mutableSetOf("Todos")

                for (doc in querySnapshot) {
                    val data = doc.data
                    val area = data["area"] as? String // Safe cast to String
                    val grado = data["grado"] as? String // Safe cast to String

                    // Agregar valores no nulos al conjunto
                    area?.let { areas.add(it) }
                    grado?.let { grados.add(it) }
                }

                // Configurar los spinners con las opciones
                setSpinnerOptions(spinnerArea, areas.toList())
                setSpinnerOptions(spinnerGrado, grados.toList())

                // Configurar listeners para los spinners
                spinnerArea.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        fetchProjects()
                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }

                spinnerGrado.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
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
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun fetchProjects() {
        var query: Query = firestore.collection("Pruebas")

        val selectedArea = spinnerArea.selectedItem.toString()
        val selectedGrado = spinnerGrado.selectedItem.toString()

        if (selectedArea != "Todos") {
            query = query.whereEqualTo("area", selectedArea)
        }
        if (selectedGrado != "Todos") {
            query = query.whereEqualTo("grado", selectedGrado)
        }

        query.get()
            .addOnSuccessListener { querySnapshot ->
                tableLayout.removeAllViews() // Limpia la tabla

                if (querySnapshot.isEmpty) {
                    val row = TableRow(this)
                    val noData = TextView(this).apply { text = "No se encontraron proyectos." }
                    row.addView(noData)
                    tableLayout.addView(row)
                    return@addOnSuccessListener
                }

                for (doc in querySnapshot) {
                    val data = doc.data
                    val row = TableRow(this)

                    val title = TextView(this).apply { text = data["titulo"].toString() }
                    val area = TextView(this).apply { text = data["area"].toString() }
                    val author = TextView(this).apply { text = data["Correo"].toString() }
                    val pdfButton = Button(this).apply {
                        text = "Descargar PDF"
                        setOnClickListener {
                            val pdfUrl = data["PDF"].toString()
                            downloadPdf(pdfUrl)
                        }
                    }

                    row.addView(title)
                    row.addView(area)
                    row.addView(author)
                    row.addView(pdfButton)

                    tableLayout.addView(row)
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Error al buscar proyectos", e)
            }
    }

    private fun resetFilters() {
        spinnerArea.setSelection(0)
        spinnerGrado.setSelection(0)
        fetchProjects()
    }

    private fun downloadPdf(pdfUrl: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(pdfUrl))
        startActivity(intent)
    }
}