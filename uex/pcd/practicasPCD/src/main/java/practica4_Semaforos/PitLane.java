package practica3_Sincronización;

import java.util.Random;

/**
 * Clase PitLane que gestiona el acceso concurrente de los pilotos al Pit Lane durante la carrera.
 *
 * <ul>Mejoras:
 *      <li>Sincronización para manejar el acceso concurrente al Pit Lane.</li>
 * </ul>
 *
 * @author Jose Luis Obiang Ela Nanguang
 * @version 1.0
 */
public class PitLane {
    private int capacity; // Capacidad máxima del Pit Lane
    private int ocupacion; // Número actual de pilotos en el Pit Lane
    private Random random; // Generador de números aleatorios para simular tiempos de parada

    public PitLane(int capacity) {
        this.capacity = capacity;
        this.ocupacion = 0;
        this.random = new Random();
    }

    /**
     * Método sincronizado para manejar la entrada de pilotos al Pit Lane.
     * @param pilotoName Nombre del piloto.
     * @param vueltaActual Vuelta actual en la que el piloto solicita entrar.
     * @return Tiempo que el piloto pasa en el Pit Lane.
     */
    public synchronized int entrar(String pilotoName, int vueltaActual) {
        while (ocupacion == capacity) {
            // Si el Pit Lane está lleno, el piloto debe esperar
            System.out.println("\t*Solicita acceso PIT STOP " + pilotoName + " en vuelta " + vueltaActual + " (Esperando)");
            try {
                wait(); // El piloto espera hasta que haya espacio en el Pit Lane
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // El piloto entra al Pit Lane y aumenta la ocupación
        ocupacion++;
        System.out.println("\t🟢 +Entra PIT STOP " + pilotoName + " en vuelta " + vueltaActual);
        int pitStopTime = cambiarNeumaticos(pilotoName, vueltaActual);
        return pitStopTime;
    }

    /**
     * Método sincronizado para manejar la salida de pilotos del Pit Lane.
     * @param pilotoName Nombre del piloto que sale del Pit Lane.
     */
    public synchronized void salir(String pilotoName) {
        ocupacion--;
        System.out.println("\t🔴 -Sale PIT STOP " + pilotoName);
        notifyAll(); // Notifica a los pilotos que esperan que hay espacio disponible
    }

    /**
     * Método privado para simular el cambio de neumáticos.
     * @param pilotoName Nombre del piloto.
     * @param vueltaActual Vuelta actual en la que ocurre el cambio.
     * @return Tiempo que toma cambiar los neumáticos.
     */
    private int cambiarNeumaticos(String pilotoName, int vueltaActual) {
        int pitStopTime = random.nextInt(400) + 200; // Tiempo aleatorio entre 200ms y 600ms
        System.out.println(pilotoName + " cambiando neumáticos en vuelta " + vueltaActual + " por " + pitStopTime + " ms");
        try {
            Thread.sleep(pitStopTime); // Simula el tiempo de cambio de neumáticos
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return pitStopTime;
    }
}
