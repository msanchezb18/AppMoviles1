package com.csanchezb.a12_firebaseaccess.ui.users

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.EditText
import android.widget.Button
import android.widget.Toast
import com.csanchezb.a12_firebaseaccess.R
import com.csanchezb.a12_firebaseaccess.entities.cls_Customer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date
import android.util.Log

class SignupActivity : AppCompatActivity() {

    private var auth = FirebaseAuth.getInstance()
    private var db = FirebaseFirestore.getInstance()

    private lateinit var txtRNombre: EditText
    private lateinit var txtREmail: EditText
    private lateinit var txtRContra: EditText
    private lateinit var txtRreContra: EditText
    private lateinit var btnRegistrarU: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signup)

        txtRNombre = findViewById(R.id.txtRNombre)
        txtREmail = findViewById(R.id.txtREmail)
        txtRContra = findViewById(R.id.txtRContra)
        txtRreContra = findViewById(R.id.txtRreContra)
        btnRegistrarU = findViewById(R.id.btnRegistrarU)

        btnRegistrarU.setOnClickListener {
            registrarUsuario()
        }

        // Paso 1: Código de prueba para listar todos los CustomerID y datos en Logcat
        db.collection("Customers")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d("SignupActivity", "Found CustomerID: ${document.id} with data: ${document.data}")
                }
            }
            .addOnFailureListener { e ->
                Log.e("SignupActivity", "Error retrieving customers: ${e.message}")
            }
    }

    private fun registrarUsuario() {
        val nombre = txtRNombre.text.toString()
        val email = txtREmail.text.toString()
        val contra = txtRContra.text.toString()
        val reContra = txtRreContra.text.toString()
        val customerId = "GROSR" // Ejemplo: puedes cambiar esto al valor correcto

        if (nombre.isEmpty() || email.isEmpty() || contra.isEmpty() || reContra.isEmpty()) {
            Toast.makeText(this, "Favor de llenar todos los campos", Toast.LENGTH_SHORT).show()
        } else {
            if (contra == reContra) {
                auth.createUserWithEmailAndPassword(email, contra)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val userID = task.result?.user?.uid
                            val dt: Date = Date()

                            // Buscar el cliente en Firestore usando whereEqualTo
                            db.collection("Customers")
                                .whereEqualTo("CustomerID", customerId)
                                .get()
                                .addOnSuccessListener { documents ->
                                    if (documents.isEmpty) {
                                        Toast.makeText(this, "Cliente con ID $customerId no encontrado en Firestore", Toast.LENGTH_SHORT).show()
                                    } else {
                                        // Procesar el primer documento que cumple con la condición
                                        val document = documents.first()
                                        val customerData = document.data.toMutableMap()

                                        // Obtener el valor del campo CustomerID del documento actual
                                        val customerIDValue = customerData["CustomerID"] as? String

                                        // Actualizar los campos ContactName y ContactTitle
                                        customerData["ContactName"] = nombre
                                        customerData["ContactTitle"] = "Nuevo Contacto"

                                        // Guardar el cliente actualizado en Firestore
                                        db.collection("Customers").document(document.id).set(customerData)
                                            .addOnSuccessListener {
                                                // Crear el usuario en datosUsuarios con el valor de CustomerID
                                                val user = hashMapOf(
                                                    "idemp" to userID,
                                                    "usuario" to nombre,
                                                    "email" to email,
                                                    "ultAcceso" to dt.toString(),
                                                    "CustomerID" to customerIDValue // Valor del campo CustomerID
                                                )
                                                db.collection("datosUsuarios")
                                                    .add(user)
                                                    .addOnSuccessListener {
                                                        // Guardar datos adicionales en localStorage
                                                        val prefe = this.getSharedPreferences("appData", Context.MODE_PRIVATE)
                                                        val editor = prefe.edit()
                                                        editor.putString("email", email)
                                                        editor.putString("contra", contra)
                                                        editor.putString("CustomerID", customerIDValue)
                                                        editor.putString("ShipVia", customerData["ShipVia"].toString())
                                                        editor.putString("ShipName", customerData["CompanyName"].toString())
                                                        editor.putString("ShipAddress", customerData["Address"].toString())
                                                        editor.putString("ShipCity", customerData["City"].toString())
                                                        editor.putString("ShipRegion", customerData["Region"].toString())
                                                        editor.putString("ShipPostalCode", customerData["PostalCode"].toString())
                                                        editor.putString("ShipCountry", customerData["Country"].toString())
                                                        editor.apply()

                                                        Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show()
                                                        Intent().let {
                                                            setResult(Activity.RESULT_OK)
                                                            finish()
                                                        }
                                                    }
                                                    .addOnFailureListener { e ->
                                                        Toast.makeText(this, "Error al registrar usuario", Toast.LENGTH_SHORT).show()
                                                    }
                                            }
                                    }
                                }
                                .addOnFailureListener { e ->
                                    Log.e("SignupActivity", "Error retrieving customer: ${e.message}")
                                    Toast.makeText(this, "Error al consultar el cliente", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            Toast.makeText(this, "Error al registrar usuario", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            }
        }
    }



}
