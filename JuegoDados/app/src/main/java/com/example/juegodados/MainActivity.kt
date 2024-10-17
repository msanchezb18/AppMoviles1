package com.example.juegodados

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etNombre = findViewById<EditText>(R.id.etNombre)
        val etFechaNacimiento = findViewById<EditText>(R.id.etFechaNacimiento)
        val etMonto = findViewById<EditText>(R.id.etMonto)
        val spinnerDados = findViewById<Spinner>(R.id.spinnerDados)
        val btnJugar = findViewById<Button>(R.id.btnJugar)

        btnJugar.setOnClickListener {
            val nombre = etNombre.text.toString()
            val fechaNacimiento = etFechaNacimiento.text.toString()
            val monto = etMonto.text.toString().toIntOrNull()
            val dadosSeleccionados = spinnerDados.selectedItem.toString()

            // Validar los datos ingresados
            if (nombre.isEmpty() || fechaNacimiento.isEmpty() || monto == null) {
                Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validar edad mínima
            if (!validarEdad(fechaNacimiento)) {
                Toast.makeText(this, "Debe ser mayor de 21 años para jugar", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validar monto mínimo
            if (monto < 2000000) {
                Toast.makeText(this, "El monto mínimo para jugar es de 2 millones de colones", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Enviar datos a la actividad de juego
            val intent = Intent(this, JuegoActivity::class.java)
            intent.putExtra("nombre", nombre)
            intent.putExtra("monto", monto)
            intent.putExtra("dados", dadosSeleccionados)
            startActivity(intent)
        }
    }

    // Función para validar que la edad sea mayor a 21 años
    private fun validarEdad(fechaNacimiento: String): Boolean {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        val fecha = sdf.parse(fechaNacimiento)
        val calendarioNacimiento = Calendar.getInstance()
        calendarioNacimiento.time = fecha!!

        val hoy = Calendar.getInstance()
        val edad = hoy.get(Calendar.YEAR) - calendarioNacimiento.get(Calendar.YEAR)
        return edad >= 21
    }
}
