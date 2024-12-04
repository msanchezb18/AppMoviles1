package com.csanchezb.navigationexample.ui.access

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.csanchezb.navigationexample.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class AccessFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private lateinit var btnAutenticar: Button
    private lateinit var txtEmail: EditText
    private lateinit var txtContra: EditText
    private lateinit var txtRegister: TextView
    private lateinit var btnCerrarSesion: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_access, container, false)

        btnAutenticar = root.findViewById(R.id.btnAutenticar)
        txtEmail = root.findViewById(R.id.txtEmail)
        txtContra = root.findViewById(R.id.txtContra)
        txtRegister = root.findViewById(R.id.txtRegister)
        btnCerrarSesion = root.findViewById(R.id.btnCerrarSesion)

        txtRegister.setOnClickListener {
            goToSignup()
        }

        btnAutenticar.setOnClickListener {
            autenticarUsuario()
        }

        // Configurar el botón de cerrar sesión
        btnCerrarSesion.setOnClickListener {
            cerrarSesion()
        }

        return root
    }

    private fun autenticarUsuario() {
        if (txtEmail.text.isNotEmpty() && txtContra.text.isNotEmpty()) {
            auth.signInWithEmailAndPassword(txtEmail.text.toString(), txtContra.text.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val dt = Date()

                        // Actualizar el campo ultAcceso en datosUsuarios
                        val userUpdate = hashMapOf<String, Any>("ultAcceso" to dt.toString())
                        val userId = task.result?.user?.uid.toString()

                        db.collection("datosUsuarios").whereEqualTo("idemp", userId).get()
                            .addOnSuccessListener { documents ->
                                if (documents.isEmpty) {
                                    Toast.makeText(
                                        context,
                                        "Usuario no encontrado en datosUsuarios",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    for (document in documents) {
                                        db.collection("datosUsuarios").document(document.id)
                                            .update(userUpdate)
                                    }
                                    Toast.makeText(
                                        context,
                                        "Usuario autenticado correctamente.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    // Redirigir a otra pantalla si es necesario
                                    findNavController().navigate(R.id.navigation_home)
                                }
                            }
                            .addOnFailureListener {
                                Toast.makeText(
                                    context,
                                    "Error al actualizar los datos del usuario",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    } else {
                        showAlert("Error", "Error al autenticar el usuario")
                    }
                }
        } else {
            showAlert("Error", "El correo electrónico y contraseña no pueden estar vacíos")
        }
    }


    private fun goToSignup() {
        // Navegar al fragmento de registro
        findNavController().navigate(R.id.navigation_dashboard)
    }

    private fun showAlert(titu: String, mssg: String) {
        val diagMessage = AlertDialog.Builder(requireContext())
        diagMessage.setTitle(titu)
        diagMessage.setMessage(mssg)
        diagMessage.setPositiveButton("Aceptar", null)

        val diagVentana: AlertDialog = diagMessage.create()
        diagVentana.show()
    }

    private fun cerrarSesion() {
        // Cerrar sesión en Firebase Auth
        auth.signOut()
        Toast.makeText(context, "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show()

        // Redirigir a la pantalla de inicio de sesión
        findNavController().navigate(R.id.navigation_home)  // Asegúrate de que navigation_access sea el id correcto de tu fragmento de acceso
    }
}