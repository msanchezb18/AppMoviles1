import java.util.*
import kotlin.math.sqrt

class subrutinas {

    fun factorial(n: Int): Long {
        var resultado: Long = 1
        for (i in 1..n) {
            resultado *= i
        }
        return resultado
    }

    fun coeficienteBinomial(n: Int, k: Int): Long {
        val numerador = factorial(n)
        val denominador = factorial(k) * factorial(n - k)
        return numerador / denominador
    }

    fun calcularDistancia(puntos: Map<String, Double>): Double {
        val x1 = puntos["x1"] ?: 0.0
        val y1 = puntos["y1"] ?: 0.0
        val x2 = puntos["x2"] ?: 0.0
        val y2 = puntos["y2"] ?: 0.0

        // fórmula de distancia
        return sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1))
    }

    fun convertirTextoANumero(texto: String): Int {
        // Mapa para las centenas
        val centenas = mapOf(
            "cien" to 100, "doscientos" to 200, "trescientos" to 300, "cuatrocientos" to 400,
            "quinientos" to 500, "seiscientos" to 600, "setecientos" to 700, "ochocientos" to 800,
            "novecientos" to 900
        )

        // Mapa para las decenas
        val decenas = mapOf(
            "diez" to 10, "once" to 11, "doce" to 12, "trece" to 13, "catorce" to 14, "quince" to 15,
            "veinte" to 20, "treinta" to 30, "cuarenta" to 40, "cincuenta" to 50, "sesenta" to 60,
            "setenta" to 70, "ochenta" to 80, "noventa" to 90
        )

        // Mapa para las unidades
        val unidades = mapOf(
            "uno" to 1, "dos" to 2, "tres" to 3, "cuatro" to 4, "cinco" to 5,
            "seis" to 6, "siete" to 7, "ocho" to 8, "nueve" to 9
        )

        // Dividir la cadena en partes
        val partes = texto.trim().lowercase(Locale.getDefault()).split(" ")

        var numero = 0
        var actual = 0

        for (parte in partes) {
            // Verificar si la palabra es una centena
            if (centenas.containsKey(parte)) {
                actual = centenas[parte] ?: 0
            }
            // Verificar si es una decena
            else if (decenas.containsKey(parte)) {
                numero += actual + (decenas[parte] ?: 0)
                actual = 0
            }
            // Verificar si es una unidad
            else if (unidades.containsKey(parte)) {
                numero += actual + (unidades[parte] ?: 0)
                actual = 0
            }
            // Ignorar la palabra "y"
            else if (parte == "y") {
                continue
            }
        }

        // Sumar cualquier valor restante de 'actual'
        numero += actual

        return numero
    }
}