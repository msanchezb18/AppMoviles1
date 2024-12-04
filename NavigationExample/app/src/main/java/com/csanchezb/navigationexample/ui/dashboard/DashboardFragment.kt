package com.csanchezb.navigationexample.ui.dashboard

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.csanchezb.navigationexample.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import android.widget.Toast

class DashboardFragment : Fragment() {

    private lateinit var selectFileButton: Button
    private lateinit var uploadButton: Button
    private lateinit var selectedFileTextView: TextView
    private lateinit var areaSpinner: Spinner
    private lateinit var cicloSpinner: Spinner
    private lateinit var conclusionEditText: EditText
    private lateinit var recommendationsEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var titleEditText: EditText
    private lateinit var selectedImages: MutableList<Uri> // Para almacenar las imágenes seleccionadas

    private var pdfUri: Uri? = null

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)

        selectFileButton = root.findViewById(R.id.selectFileButton)
        uploadButton = root.findViewById(R.id.submitButton)
        selectedFileTextView = root.findViewById(R.id.selectedFileTextView)
        areaSpinner = root.findViewById(R.id.areaSpinner)
        cicloSpinner = root.findViewById(R.id.cicloSpinner)
        conclusionEditText = root.findViewById(R.id.conclusionsEditText)
        recommendationsEditText = root.findViewById(R.id.recommendationsEditText)
        descriptionEditText = root.findViewById(R.id.descriptionEditText)
        titleEditText = root.findViewById(R.id.titleEditText)

        // Configurar los Spinners
        setupSpinners()

        selectedImages = mutableListOf()  // Inicializa la lista en onCreateView


        selectFileButton.setOnClickListener { selectFile() }
        uploadButton.setOnClickListener { uploadForm() }

        return root
    }

    private fun setupSpinners() {
        // Configurar el adaptador para el Spinner de áreas de interés
        val areaAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.areas,
            android.R.layout.simple_spinner_item
        )
        areaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        areaSpinner.adapter = areaAdapter

        // Configurar el adaptador para el Spinner de ciclos escolares
        val cicloAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.grades,
            android.R.layout.simple_spinner_item
        )
        cicloAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        cicloSpinner.adapter = cicloAdapter
    }

    private fun selectFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*" // Permite seleccionar cualquier tipo de archivo
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        val mimeTypes = arrayOf("application/pdf", "image/*") // Permite solo PDFs o imágenes
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        startActivityForResult(Intent.createChooser(intent, "Seleccionar archivo"), 1001)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == Activity.RESULT_OK) {
            val selectedUri = data?.data
            val mimeType = context?.contentResolver?.getType(selectedUri!!) // Obtiene el tipo MIME del archivo seleccionado

            if (mimeType == "application/pdf") { // Si es un PDF
                pdfUri = selectedUri
                selectedFileTextView.text = "PDF seleccionado"
            } else if (mimeType?.startsWith("image/") == true) { // Si es una imagen
                if (selectedUri != null) {
                    selectedImages.add(selectedUri)
                }
                selectedFileTextView.text = "${selectedImages.size} imagen(es) seleccionada(s)"
            } else {
                Toast.makeText(context, "Archivo no soportado", Toast.LENGTH_SHORT).show() // Si el archivo no es ni PDF ni imagen
            }
        }
    }


    private fun uploadForm() {
        if (pdfUri == null || selectedImages.isEmpty()) {
            Toast.makeText(context, "Por favor, selecciona un PDF y al menos una imagen.", Toast.LENGTH_SHORT).show()
            return
        }

        val user = firebaseAuth.currentUser
        if (user == null) {
            Toast.makeText(context, "Usuario no autenticado.", Toast.LENGTH_SHORT).show()
            return
        }

        val userEmail = user.email ?: "Desconocido"  // Obtener el correo del usuario autenticado

        val storageRef = storage.reference
        val fileName = "${UUID.randomUUID()}"
        val fileRef = storageRef.child("doc/$fileName")

        val uploadTask = fileRef.putFile(pdfUri!!)
        uploadTask
            .addOnSuccessListener {
                fileRef.downloadUrl.addOnSuccessListener { uri ->
                    uploadImages(uri.toString(), userEmail)  // Pasa el correo ya obtenido aquí
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error al subir el archivo: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun uploadImages(pdfUrl: String, userEmail: String) {
        val storageRef = storage.reference
        val imageUrls = mutableListOf<String>()
        var uploadCount = 0 // Contador para rastrear cuántas imágenes se han subido
        val totalImages = selectedImages.size

        selectedImages.forEach { imageUri ->
            val imageName = "${UUID.randomUUID()}.jpg"
            val imageRef = storageRef.child("images/$imageName")
            imageRef.putFile(imageUri).addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    imageUrls.add(uri.toString())
                    uploadCount++
                    if (uploadCount == totalImages) {
                        // Si todas las imágenes están subidas, guarda los datos
                        saveDataToFirestore(pdfUrl, userEmail, imageUrls)
                    }
                }
            }
        }
    }


    private fun saveDataToFirestore(pdfUrl: String, userEmail: String, imageUrls: List<String>) {
        val documentData = mutableMapOf(
            "titulo" to titleEditText.text.toString(),
            "area" to areaSpinner.selectedItem.toString(),
            "ciclo" to cicloSpinner.selectedItem.toString(),
            "descripcion" to descriptionEditText.text.toString(),
            "Conclusion" to conclusionEditText.text.toString(),
            "Recomendaciones" to recommendationsEditText.text.toString(),
            "PDF" to pdfUrl,
            "publicationDate" to System.currentTimeMillis(),
            "usuario" to userEmail
        )

        imageUrls.forEachIndexed { index, url ->
            documentData["Imagen_${index + 1}"] = url
        }

        firestore.collection("Pruebas")
            .add(documentData)
            .addOnSuccessListener {
                Toast.makeText(context, "Archivo subido y datos guardados.", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.navigation_home)
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error al guardar los metadatos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
