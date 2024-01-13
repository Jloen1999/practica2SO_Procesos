package practica1_Secuencial;

import java.util.Random;

import static org.fusesource.jansi.Ansi.Color.*;
import static org.fusesource.jansi.Ansi.ansi;

/**
 * Representa el Pit Lane en la simulación del Gran Premio de Fórmula 1.
 * El Pit Lane tiene una capacidad limitada, y los pilotos deben esperar si está lleno.
 *
 * @author Jose Luis Obiang Ela Nanguang
 * @version 1.0
 * @see Piloto
 * @since 1.0
 */
public class PitLane {
    /**
     * Capacidad máxima del Pit Lane. Define cuántos pilotos pueden estar en el Pit Lane al mismo tiempo.
     */
    private int capacity = 5;
    private int ocupacion; // Ocupación actual del Pit Lane

    /**
     * Utilizado para generar números aleatorios, en este caso, para simular el tiempo que un piloto pasa en el Pit Lane.
     */
    private Random random;

    /**
     * Constructor de PitLane. Inicializa el generador de números aleatorios.
     */
    public PitLane(int capacity) {
        this.capacity = capacity;
        this.ocupacion = 0;
        this.random = new Random();
    }

    /**
     * Simula la entrada de un piloto al Pit Lane. Si el Pit Lane está lleno, el piloto debe esperar.
     *
     * @param pilotoName Nombre del piloto que intenta entrar al Pit Lane.
     * @return El tiempo que el piloto pasó en el Pit Lane.
     */
    public int entrar(String pilotoName) {
        // Si el Pit Lane está lleno, el piloto espera
        while (ocupacion == capacity) {
            System.out.println("\t*Solicita acceso PIT STOP " + pilotoName);
//            try {
//                System.out.println("\t*Solicita acceso PIT STOP " + pilotoName);
//                wait(); //Hace que el piloto espere hasta que haya espacio en el PitLane
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
        }

        // El piloto entra al Pit Lane y aumenta la ocupación
        ocupacion++;
        System.out.println("\t🟢 " + ansi().fg(GREEN).a("+Entra PIT STOP " + pilotoName).reset());
        int pitStopTime = random.nextInt(250) + 50; // Tiempo aleatorio entre 50ms y 300ms
        try {
            Thread.sleep(pitStopTime); //Hace que el hilo actual (el piloto) duerma (o espere) durante el tiempo generado anteriormente, simulando el tiempo que el piloto pasa en boxes.
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Actualiza el tiempo que el piloto pasó en el Pit Lane
        pitStopTime += cambiarNeumaticos(pilotoName);

        //notifyAll(); //Notifica a todos los pilotos que estaban esperando para entrar al PitLane
        return pitStopTime;
    }

    // Método que gestiona la salida de un piloto del Pit Lane
    public void salir(String pilotoName) {
        // El piloto sale del Pit Lane y disminuye la ocupación
        ocupacion--;
        System.out.println("\t🔴 " + ansi().fg(CYAN).a( "-Sale PIT STOP " + pilotoName).reset());
//        notifyAll(); //Notifica a todos los pilotos que están esperando para salir del PitLane
    }

    // Método que simula el servicio en el box de un piloto
    public int cambiarNeumaticos(String pilotoName) {
        // Se genera un tiempo de parada aleatorio entre 0.2 y 0.5 segundos
        int pitStopTimeTotal = random.nextInt(400) + 200;
        // Se simula el tiempo de parada con un sleep
        try {
            System.out.println(pilotoName + " recibe el servicio en el box durante " + pitStopTimeTotal + " segundos");
            Thread.sleep(pitStopTimeTotal);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return pitStopTimeTotal;
    }
}
