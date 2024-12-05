package com.csanchezb.navigationexample.ui.signup

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.csanchezb.navigationexample.R
import com.csanchezb.navigationexample.databinding.FragmentSignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage


class SignupFragment : Fragment() {

    private lateinit var txtNombre: EditText
    private lateinit var txtEmail: EditText
    private lateinit var txtContra: EditText
    private lateinit var txtAnho: EditText
    private lateinit var txtIntro: EditText
    private lateinit var btnRegistrarU: Button
    private lateinit var btnFoto: Button
    private lateinit var storage: FirebaseStorage
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var imageUri: Uri

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSignupBinding.inflate(inflater, container, false)

        // Inicia Firebase
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Referencias a las vistas
        txtNombre = binding.txtRNombre
        txtEmail = binding.txtREmail
        txtContra = binding.txtRContra
        txtAnho = binding.txtRAnho
        txtIntro = binding.txtIntro
        btnRegistrarU = binding.btnRegistrarU
        btnFoto = binding.btnFoto

        // Listener para el botón de registro
        btnRegistrarU.setOnClickListener {
            registerUser()
        }

        // Listener para el botón de foto (selección de imagen)
        btnFoto.setOnClickListener {
            selectImage()
        }

        return binding.root
    }

    // Función para registrar al usuario
    private fun registerUser() {
        val nombre = txtNombre.text.toString().trim()
        val email = txtEmail.text.toString().trim()
        val password = txtContra.text.toString().trim()
        val anho = txtAnho.text.toString().trim()
        val intro = txtIntro.text.toString().trim()

        if (nombre.isEmpty() || email.isEmpty() || password.isEmpty() || anho.isEmpty() || intro.isEmpty()) {
            Toast.makeText(requireContext(), "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        // Crear usuario con Firebase Authentication
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        uploadImageAndSaveData(user.uid, nombre, email, anho, intro)
                    }
                } else {
                    Toast.makeText(requireContext(), "Error al crear usuario: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Función para subir la imagen y guardar los datos del usuario
    private fun uploadImageAndSaveData(userId: String, nombre: String, email: String, anho: String, intro: String) {
        if (this::imageUri.isInitialized) {
            val userId_ = ""
            val fileName = "$userId_${System.currentTimeMillis()}.jpg"
            val storageRef = storage.reference.child("fotos/$fileName")

            val uploadTask = storageRef.putFile(imageUri)
            uploadTask.addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    val userData = hashMapOf(
                        "idemp" to userId,
                        "usuario" to nombre,
                        "email" to email,
                        "anho" to anho,
                        "introduccion" to intro,
                        "url" to uri.toString()
                    )

                    db.collection("datosUsuarios").add(userData)
                        .addOnSuccessListener { documentReference ->
                            Toast.makeText(requireContext(), "Usuario registrado con éxito", Toast.LENGTH_SHORT).show()
                            limpiarCampos()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(requireContext(), "Error al guardar los datos: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Error al subir la imagen", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Debe seleccionar una imagen", Toast.LENGTH_SHORT).show()
        }
    }

    // Función para seleccionar una imagen de la galería
    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 1001)
    }

    // Recibir el resultado de la selección de imagen
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data ?: return
            // Aquí puedes mostrar la imagen seleccionada si lo deseas
        }
    }

    // Función para limpiar los campos
    private fun limpiarCampos() {
        txtNombre.text.clear()
        txtEmail.text.clear()
        txtContra.text.clear()
        txtAnho.text.clear()
        txtIntro.text.clear()
    }
}
