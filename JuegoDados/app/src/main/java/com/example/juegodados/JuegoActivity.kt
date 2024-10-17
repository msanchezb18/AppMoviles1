package com.example.juegodados

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class JuegoActivity : AppCompatActivity() {

    private var saldo: Int = 0
    private var dadosSeleccionados: Int = 2
    private var victoriasConsecutivas: Int = 0
    private var numeroSeleccionado: Int = 0
    private var victorias: Int = 0
    private var derrotas: Int = 0

    // Declarar los botones de selección de número
    private lateinit var btnNumero2: Button
    private lateinit var btnNumero3: Button
    private lateinit var btnNumero4: Button
    private lateinit var btnNumero5: Button
    private lateinit var btnNumero6: Button
    private lateinit var btnNumero7: Button
    private lateinit var btnNumero8: Button
    private lateinit var btnNumero9: Button
    private lateinit var btnNumero10: Button
    private lateinit var btnNumero11: Button
    private lateinit var btnNumero12: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_juego)

        // Recibir los datos del jugador
        val nombre = intent.getStringExtra("nombre")
        saldo = intent.getIntExtra("monto", 0)
        val dados = intent.getStringExtra("dados")

        // Asignar la cantidad de dados seleccionados
        dadosSeleccionados = if (dados == "2 dados") 2 else 3

        // Elementos UI
        val tvInfo = findViewById<TextView>(R.id.tvInfo)
        val tvPuntaje = findViewById<TextView>(R.id.tvPuntaje)
        val tvNumeroElegido = findViewById<TextView>(R.id.tvNumeroElegido)
        val dado1 = findViewById<ImageView>(R.id.dado1)
        val dado2 = findViewById<ImageView>(R.id.dado2)
        val dado3 = findViewById<ImageView>(R.id.dado3)
        val btnLanzarDados = findViewById<Button>(R.id.btnLanzarDados)
        val etApuesta = findViewById<EditText>(R.id.etApuesta)
        val tvResultado = findViewById<TextView>(R.id.tvResultado)
        val emoticono = findViewById<ImageView>(R.id.emoticono)
        val btnSalir = findViewById<Button>(R.id.btnSalir)

        // Mostrar los datos del jugador
        tvInfo.text = "Jugador: $nombre\nSaldo: $saldo"
        tvPuntaje.text = "Victorias: $victorias\nDerrotas: $derrotas"


        if (dadosSeleccionados == 3) dado3.visibility = ImageView.VISIBLE


        btnNumero2 = findViewById(R.id.btnNumero2)
        btnNumero3 = findViewById(R.id.btnNumero3)
        btnNumero4 = findViewById(R.id.btnNumero4)
        btnNumero5 = findViewById(R.id.btnNumero5)
        btnNumero6 = findViewById(R.id.btnNumero6)
        btnNumero7 = findViewById(R.id.btnNumero7)
        btnNumero8 = findViewById(R.id.btnNumero8)
        btnNumero9 = findViewById(R.id.btnNumero9)
        btnNumero10 = findViewById(R.id.btnNumero10)
        btnNumero11 = findViewById(R.id.btnNumero11)
        btnNumero12 = findViewById(R.id.btnNumero12)

        // Asignar los listeners para los botones de selección de número
        btnNumero2.setOnClickListener { numeroSeleccionado = 2 }
        btnNumero3.setOnClickListener { numeroSeleccionado = 3 }
        btnNumero4.setOnClickListener { numeroSeleccionado = 4 }
        btnNumero5.setOnClickListener { numeroSeleccionado = 5 }
        btnNumero6.setOnClickListener { numeroSeleccionado = 6 }
        btnNumero7.setOnClickListener { numeroSeleccionado = 7 }
        btnNumero8.setOnClickListener { numeroSeleccionado = 8 }
        btnNumero9.setOnClickListener { numeroSeleccionado = 9 }
        btnNumero10.setOnClickListener { numeroSeleccionado = 10 }
        btnNumero11.setOnClickListener { numeroSeleccionado = 11 }
        btnNumero12.setOnClickListener { numeroSeleccionado = 12 }

        // Acción del botón para lanzar los dados
        btnLanzarDados.setOnClickListener {
            val apuesta = etApuesta.text.toString().toIntOrNull()

            // Validar apuesta
            if (apuesta == null || apuesta <= 0 || apuesta > saldo || numeroSeleccionado == 0) {
                Toast.makeText(this, "Seleccione un número válido para apostar y un monto", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Mostrar el número elegido
            tvNumeroElegido.text = "Número elegido: $numeroSeleccionado"
            tvNumeroElegido.visibility = TextView.VISIBLE

            // Tirar los dados
            val resultadoDados = lanzarDados(dadosSeleccionados)

            // Actualizar las imágenes de los dados
            dado1.setImageResource(getDrawableForDado(resultadoDados[0]))
            dado2.setImageResource(getDrawableForDado(resultadoDados[1]))
            if (dadosSeleccionados == 3) {
                dado3.setImageResource(getDrawableForDado(resultadoDados[2]))
            }

            // Calcular la sumatoria
            val sumatoria = resultadoDados.sum()

            // Determinar si el jugador ganó o perdió
            val gano = (sumatoria == numeroSeleccionado)
            if (gano) {
                saldo += apuesta // Ganó, se suma el monto
                victoriasConsecutivas++
                victorias++ // Incrementar victorias
                tvResultado.text = "¡Ganaste! Saldo actualizado: $saldo\nVictorias: $victorias\nDerrotas: $derrotas"
                emoticono.setImageResource(R.drawable.feliz) // Emoticono feliz
            } else {
                saldo -= apuesta // Perdió, se resta el monto
                victoriasConsecutivas = 0
                derrotas++ // Incrementar derrotas
                tvResultado.text = "Perdiste... Saldo actualizado: $saldo\nVictorias: $victorias\nDerrotas: $derrotas"
                emoticono.setImageResource(R.drawable.triste) // Emoticono triste
            }

            // Mostrar resultado
            tvResultado.visibility = TextView.VISIBLE

            // Actualizar puntaje
            tvPuntaje.text = "Victorias: $victorias\nDerrotas: $derrotas"

            // Verificar condiciones de fin del juego
            verificarFinDeJuego()
        }

        // Configurar el botón de salida
        btnSalir.setOnClickListener {
            // Volver a la actividad de inicio
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Cerrar la actividad actual
        }
    }

    // Función para lanzar los dados
    private fun lanzarDados(cantidad: Int): List<Int> {
        val dados = mutableListOf<Int>()
        for (i in 1..cantidad) {
            dados.add(Random.nextInt(1, 7)) // Generar un número aleatorio entre 1 y 6
        }
        return dados
    }

    // Función para obtener la imagen del dado según el valor
    private fun getDrawableForDado(valor: Int): Int {
        return when (valor) {
            1 -> R.drawable.dice_1
            2 -> R.drawable.dice_2
            3 -> R.drawable.dice_3
            4 -> R.drawable.dice_4
            5 -> R.drawable.dice_5
            else -> R.drawable.dice_6
        }
    }

    // Función para verificar si el juego ha terminado
    private fun verificarFinDeJuego() {
        when {
            saldo <= 0 -> {
                mostrarMensajeFinal("Lo perdiste todo…. No vuelvas a jugar!")
            }
            victoriasConsecutivas >= 3 -> {
                mostrarMensajeFinal("¡Ganaste tres veces consecutivas! Eres un ganador")
            }
            else -> {
                Toast.makeText(this, "Sigue jugando", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Función para mostrar un mensaje final y deshabilitar el juego
    private fun mostrarMensajeFinal(mensaje: String) {
        val tvResultado = findViewById<TextView>(R.id.tvResultado)
        val btnLanzarDados = findViewById<Button>(R.id.btnLanzarDados)

        tvResultado.text = mensaje
        btnLanzarDados.isEnabled = false // Deshabilitar el botón para finalizar el juego
    }
}
