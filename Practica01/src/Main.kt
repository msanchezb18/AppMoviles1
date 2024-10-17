fun main() {
    print("Ejercicio 1")
    println()
    // Fórmula del problema planteado
    println("Formula planteada: [(x + 9) * 3 - 5] / 4 = 7")

    // Fórmula aplicada para resolver el problema
    println("Formula para resolver: x + 9 = 11, entonces x = 2")

    // El número que se pensó al principio
    val numeroPensado = 2
    println("El número pensado al principio fue: $numeroPensado")

    println()

    println("Ejercicio 2")

    // Ejercicio 2

    // Fórmula aplicada
    println("Formula aplicada: Diferencia de horas * 30° por hora = 7 * 30 = 210°")

    // Cálculo de los grados girados
    val gradosGirados = 7 * 30
    println("La cantidad de grados que giró la aguja es: $gradosGirados°")

    println()

    println("Ejercicio 3")

    //Ejercicio 3

    val a= 12
    val b= 6

    fun numMenor1(a: Int, b: Int): Int {
        return minOf(a, b)
    }

    fun numMenor2(a: Int, b: Int): Int {
        return (a + b - Math.abs(a - b)) / 2
    }

    // Imprimimos los dos números
    println("Los dos números son: $a y $b")

    println("El menor es (numMenor1): ${numMenor1(a, b)}")
    println("El menor es (numMenor2): ${numMenor2(a, b)}")

    println()

    println("Ejercicio 4")

    // Definir dos números enteros
    val c = 10
    val d = 10


    fun sonIguales(c: Int, d: Int): Boolean {
        return c != 0 && d != 0 && c / d == 1 && d / c == 1
    }

    // Imprimir los dos números
    println("Los dos números son: $c y $d")

    // Imprimir si son iguales
    println("¿Son los dos números iguales? ${sonIguales(c,d)}")

    println()

    println("Ejercicio 5")

    val e = 25
    val f = 25

    fun sumarsinSumaEnteros(e: Int, f: Int): Int {
        var x = e
        var y = f
        while (y != 0) {
            val acarreo = x and y
            x = x xor y
            y = acarreo shl 1
        }
        return x
    }

    // Imprimir los dos números
    println("Los dos números son: $e y $f")


    val resultado = sumarsinSumaEnteros(e, f)
    println("El resultado de la suma sin usar el operador '+' es: $resultado")

    println()

    println("Ejercicio 6")

    // Valores de n y k
    val n = 9
    val k = 4


    println("Cálculo del coeficiente binomial C($n, $k)")

    // Invocar la subrutina coeficienteBinomial
    val result = subrutinas().coeficienteBinomial(n, k)

    // Imprimir el resultado de la subrutina
    println("El número de subconjuntos es: $result")

    println()

    println("Ejercicio 7")

    // Crear un vector asociativo  con las coordenadas de los puntos
    val puntos = mapOf(
        "x1" to 3.0,
        "y1" to 4.0,
        "x2" to 7.0,
        "y2" to 1.0
    )

    // Llamar a la rutina para calcular la distancia
    val distancia = subrutinas().calcularDistancia(puntos)

    // Imprimir el resultado
    println("La distancia entre los puntos P1(${puntos["x1"]}, ${puntos["y1"]}) y P2(${puntos["x2"]}, ${puntos["y2"]}) es: $distancia")

    println()

    println("Ejercicio 8")

// Solicitar al usuario que ingrese el texto
    println("Por favor, ingrese un número en texto (ejemplo: Seiscientos Treinta y Uno):")
    val texto = readLine() ?: ""

    // Llamar a la rutina para convertir el texto a número
    val numero = subrutinas().convertirTextoANumero(texto)

    // Imprimir el resultado
    println("El número es: $numero")
}