package practica2_SecuencialExcepciones;

import java.util.Random;

/**
 * Esta clase proporciona una simulación básica de las operaciones en el Pit Lane de un Gran Premio de Fórmula 1.
 * Incluye métodos para simular la entrada y salida de los pilotos del Pit Lane, así como el tiempo adicional
 * que pasan recibiendo servicios como el cambio de neumáticos. Los tiempos son simulados utilizando un generador
 * de números aleatorios y el método Thread.sleep para representar la espera.
 * @author Jose Luis Obiang Ela Nanguang
 * @version 1.0
 */
public class PitLane {
    // Capacidad máxima del Pit Lane.
    private int capacity;
    // Ocupación actual del Pit Lane.
    private int ocupacion;
    // Generador de números aleatorios para simular tiempos de parada.
    private Random random;

    // Constructor de PitLane.
    public PitLane(int capacity) {
        this.capacity = capacity; // Establece la capacidad máxima del Pit Lane.
        this.ocupacion = 0;       // Inicializa la ocupación actual a 0.
        this.random = new Random(); // Inicializa el generador de números aleatorios.
    }

    // Método para simular la entrada de un piloto al Pit Lane.
    public int entrar(String pilotoName, int vueltaActual) throws PitLaneCompletoException {
        // Espera si el Pit Lane está lleno (ocupación igual a la capacidad).
        while (ocupacion == capacity) {
            System.out.println("\t*Solicita acceso PIT STOP " + pilotoName);
            throw new PitLaneCompletoException("Pit Lane completo en la vuelta " + vueltaActual + " para " + pilotoName);
        }

        // Incrementa la ocupación al entrar un piloto.
        ocupacion++;
        System.out.println("\t🟢 +Entra PIT STOP " + pilotoName);

        // Simula un tiempo aleatorio que el piloto pasa en el Pit Lane.
        int pitStopTime = random.nextInt(250) + 50; // Tiempo entre 50 y 300 ms.
        try {
            Thread.sleep(pitStopTime); // Hace que el hilo espere por el tiempo simulado.
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Agrega el tiempo adicional por cambiar neumáticos.
        pitStopTime += cambiarNeumaticos(pilotoName, vueltaActual);

        // Retorna el tiempo total que el piloto ha pasado en el Pit Lane.
        return pitStopTime;
    }

    // Método para simular la salida de un piloto del Pit Lane.
    public void salir(String pilotoName) {
        // Disminuye la ocupación al salir un piloto.
        ocupacion--;
        System.out.println("\t🔴 -Sale PIT STOP " + pilotoName);
    }

    // Método que simula el tiempo adicional por el servicio de cambiar neumáticos.
    public int cambiarNeumaticos(String pilotoName, int vueltaActual) {
        // Genera un tiempo aleatorio para el cambio de neumáticos.
        int pitStopTimeTotal = random.nextInt(400) + 200; // Tiempo entre 200 y 600 ms.
        try {
            // Simula el tiempo que el piloto pasa recibiendo el servicio en el box.
            System.out.println(pilotoName + " recibe el servicio en el box durante " + pitStopTimeTotal +
                    " segundos en la vuelta " + vueltaActual);
            Thread.sleep(pitStopTimeTotal);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Retorna el tiempo total del servicio en el box.
        return pitStopTimeTotal;
    }
}
