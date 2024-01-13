package practica1_Secuencial;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import static org.fusesource.jansi.Ansi.Color.*;
import static org.fusesource.jansi.Ansi.ansi;

/**
 * Representa un piloto en la simulación del Gran Premio de Fórmula 1.
 *
 * @author Jose Luis Obiang Ela Nanguang
 * @version 1.0
 * @since 1.0
 * @see PitLane
 */
public class Piloto implements Comparable<Piloto> {
    /**
     * Representa el nombre del piloto. Es una cadena de texto que identifica al piloto en la simulación.
     */
    private String nombre;

    /**
     * Es una lista de enteros que define la estrategia de paradas en boxes del piloto.
     * Cada entero representa la vuelta en la que el piloto hará una parada en boxes.
     */
    private List<Integer> pitStopStrategy;

    /**
     * Es una lista de enteros que almacena los tiempos que el piloto ha tardado en
     * cada una de sus paradas en boxes durante la carrera.
     */
    private List<Integer> pitStopTimes;


    /**
     * Es una instancia de la clase Random utilizada para generar números aleatorios.
     * Puede ser útil para simular variaciones en los tiempos de parada en boxes o en otros aspectos de la simulación.
     */
    private Random random;

    /**
     * Es una referencia a la instancia de PitLane que se utiliza en la simulación.
     * PitLane gestiona el acceso de los pilotos a los boxes y los tiempos de parada.
     */
    private PitLane pitLane;

    private long tiempoTotal; // Tiempo total de la carrera del piloto


    /**
     * Constructor para crear un piloto.
     *
     * @param nombre Nombre del piloto.
     * @param pitStopStrategy Estrategia de paradas en boxes del piloto.
     * @param pitLane Instancia de PitLane para la carrera.
     */
    public Piloto(String nombre, List<Integer> pitStopStrategy, PitLane pitLane) {
        this.nombre = nombre;
        this.pitStopStrategy = pitStopStrategy;
        this.pitStopTimes = new ArrayList<>();
        this.random = new Random();
        this.pitLane = pitLane;
        this.tiempoTotal = 0;
    }

    /**
     * Simula la carrera para el piloto.
     */
    public void correrCarrera(int totalLaps) {
        // Se obtiene el tiempo inicial de la carrera
        long inicio = System.currentTimeMillis();
        for (int lap = 1; lap <= totalLaps; lap++) {
            if (pitStopStrategy.contains(lap)) {
                // El piloto entra al Pit Lane, obtener tiempo de parada en el box
                int pitStopTime = pitLane.entrar(nombre);
                pitStopTimes.add(pitStopTime); // Agrega el tiempo de parada en la lista de tiempos de paradas del piloto
                setTiempoTotal(getTiempoTotal()+pitStopTime); // Acumular el tiempo total de parada en el box del piloto
                // El piloto sale del Pit Lane
                pitLane.salir(nombre);

            }

            if (lap == totalLaps) {
                System.out.println(ansi().fg(YELLOW).a("\t🏁 " + nombre + " ha cruzado la meta!").reset());
            }
        }

        long fin = System.currentTimeMillis();
        long bootTime = fin - inicio;
        setTiempoTotal(getTiempoTotal()+bootTime);
    }

    /**
     * Devuelve los tiempos de parada en boxes del piloto.
     *
     * @return Lista de tiempos de parada en boxes.
     */
    public List<Integer> getPitStopTimes() {
        return pitStopTimes;
    }


    /**
     * Devuelve el nombre del piloto.
     *
     * @return Nombre del piloto.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Devuelve el tiempo total que el piloto ha tardado en la carrera.
     *
     * @return Tiempo total en milisegundos.
     */
    public long getTiempoTotal() {
        return tiempoTotal;
    }

    // Método que actualiza el tiempo total de la carrera del piloto
    public void setTiempoTotal(long tiempoTotal) {
        this.tiempoTotal = tiempoTotal;
    }

    // Método que compara dos pilotos por su tiempo total de la carrera
    public int compareTo(Piloto otro) {
        return Long.compare(this.tiempoTotal, otro.tiempoTotal);
    }

}
