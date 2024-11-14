package com.csanchezb.a12_firebaseaccess

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.csanchezb.a12_firebaseaccess.ui.users.LoginActivity
import com.google.firebase.auth.FirebaseAuth

import android.widget.ListView
import android.util.Log
import com.csanchezb.a12_firebaseaccess.entities.cls_Category
import com.csanchezb.a12_firebaseaccess.ui.categories.CategoryAdapter
import com.google.firebase.firestore.FirebaseFirestore


const val valorIntentLogin = 1


class MainActivity : AppCompatActivity() {

    var db = FirebaseFirestore.getInstance()
    var TAG = "SwiftiesTestingApp"


    var auth = FirebaseAuth.getInstance()
    var email: String? = null
    var contra: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // intenta obtener el token del usuario del local storage, sino llama a la ventana de registro
        val prefe = getSharedPreferences("appData", Context.MODE_PRIVATE)
        email = prefe.getString("email","")
        contra = prefe.getString("contra","")

        if(email.toString().trim { it <= ' ' }.length == 0){
            val intent = Intent(this, LoginActivity::class.java)
            startActivityForResult(intent, valorIntentLogin)

        }else {
            val uid: String = auth.uid.toString()
            if (uid == "null"){
                auth.signInWithEmailAndPassword(email.toString(), contra.toString()).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(this,"Autenticación correcta", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            obtenerDatos()
        }



    }

    private fun obtenerDatos() {
        //Toast.makeText(this,"Esperando hacer algo importante", Toast.LENGTH_LONG).show()
        var coleccion: ArrayList<cls_Category?> = ArrayList()
        var listaView: ListView = findViewById(R.id.lstCategories)
        db.collection("Categories").orderBy("CategoryID")
            .get()
            .addOnCompleteListener { docc ->
                if (docc.isSuccessful) {
                    for (document in docc.result!!) {
                        Log.d(TAG, document.id + " => " + document.data)
                        var datos: cls_Category = cls_Category(document.data["CategoryID"].toString().toInt(),
                            document.data["CategoryName"].toString(),
                            document.data["Description"].toString(),
                            document.data["urlImage"].toString())
                        coleccion.add(datos)
                    }
                    var adapter: CategoryAdapter = CategoryAdapter(this, coleccion)
                    listaView.adapter =adapter
                } else {
                    Log.w(TAG, "Error getting documents.", docc.exception)
                }
            }
    }


}