package com.csanchezb.a12_firebaseaccess.ui.users




import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.csanchezb.a12_firebaseaccess.R
import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AlertDialog
import android.content.Intent
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

import com.google.firebase.auth.FirebaseAuth

import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date
import com.google.firebase.firestore.DocumentSnapshot
import com.csanchezb.a12_firebaseaccess.entities.cls_Customer

const val valorIntentSignup = 1


class LoginActivity : AppCompatActivity() {

    var db = FirebaseFirestore.getInstance()
    var auth = FirebaseAuth.getInstance()

    private lateinit var btnAutenticar: Button
    private lateinit var txtEmail: EditText
    private lateinit var txtContra: EditText
    private lateinit var txtRegister: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        btnAutenticar = findViewById(R.id.btnAutenticar)
        txtEmail = findViewById(R.id.txtEmail)
        txtContra = findViewById(R.id.txtContra)
        txtRegister = findViewById(R.id.txtRegister)

        txtRegister.setOnClickListener {
            goToSignup()
        }

        btnAutenticar.setOnClickListener {
            autenticarUsuario()
        }
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
                                    Toast.makeText(this, "Usuario no encontrado en datosUsuarios", Toast.LENGTH_SHORT).show()
                                } else {
                                    for (document in documents) {
                                        db.collection("datosUsuarios").document(document.id).update(userUpdate)
                                    }
                                }

                                // Obtener el CustomerID del usuario y luego obtener los datos del cliente
                                val customerId = documents.first().getString("CustomerID")
                                println("CustomerID recuperado: $customerId")  // Verifica si el CustomerID es correcto
                                customerId?.let {
                                    obtenerCliente(it) // Llamamos la función para obtener el cliente y guardarlo
                                }
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Error al actualizar los datos del usuario", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        showAlert("Error", "Al autenticar el usuario")
                    }
                }
        } else {
            showAlert("Error", "El correo electrónico y contraseña no pueden estar vacíos")
        }
    }

    private fun obtenerCliente(customerId: String) {
        val db = FirebaseFirestore.getInstance()

        // Obtienes el documento de Firestore basado en el CustomerID
        db.collection("Customers")
            .whereEqualTo("CustomerID", customerId)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Toast.makeText(this, "Cliente no encontrado", Toast.LENGTH_SHORT).show()
                } else {
                    // Procesa el primer documento
                    val document = documents.first()
                    val cliente = document.toObject(cls_Customer::class.java)
                    guardarDatosLocal(cliente)
                }
            }
            .addOnFailureListener { exception ->
                // En caso de error al obtener los datos
                Toast.makeText(this, "Error al obtener los datos del cliente: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun guardarDatosLocal(cliente: cls_Customer) {
        val prefe = this.getSharedPreferences("appData", Context.MODE_PRIVATE)
        val editor = prefe.edit()

        // Guardar los datos del cliente en SharedPreferences
        editor.putString("CustomerID", cliente.CustomerID)
        editor.putString("ShipName", cliente.CompanyName)
        editor.putString("ShipAddress", cliente.Address)
        editor.putString("ShipCity", cliente.City)
        editor.putString("ShipRegion", cliente.Region)
        editor.putString("ShipPostalCode", cliente.PostalCode)
        editor.putString("ShipCountry", cliente.Country)

        // Confirmar que los datos se almacenaron correctamente
        editor.apply()

        // Mostrar mensaje de éxito
        Toast.makeText(this, "Datos del cliente guardados exitosamente", Toast.LENGTH_SHORT).show()

        // Aquí puedes navegar a otra actividad si es necesario, o mostrar un mensaje
        Intent().let {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    private fun goToSignup() {
        val intent = Intent(this, SignupActivity::class.java)
        startActivityForResult(intent, valorIntentSignup)
    }

    private fun showAlert(titu: String, mssg: String) {
        val diagMessage = AlertDialog.Builder(this)
        diagMessage.setTitle(titu)
        diagMessage.setMessage(mssg)
        diagMessage.setPositiveButton("Aceptar", null)

        val diagVentana: AlertDialog = diagMessage.create()
        diagVentana.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == valorIntentSignup && resultCode == Activity.RESULT_OK) {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }
}