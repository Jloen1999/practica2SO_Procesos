package practica2_SecuencialExcepciones;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Clase que simula la carrera de un piloto individual en la simulación del Gran Premio.
 * <ul>
 *     <li>Incluye la lógica para manejar las paradas en boxes según la estrategia de cada piloto y calcula el tiempo total de carrera.</li>
 *     <li>Los pilotos son comparables entre sí basados en su tiempo total de carrera, lo que permite determinar el ganador al final de la simulación.</li>
 * </ul>
 * @author Jose Luis Obiang Ela Nanguang
 * @version 1.0
 * @see PitLane
 */
public class Piloto implements Comparable<Piloto> {
    // Nombre del piloto.
    private String nombre;
    // Estrategia de paradas en boxes del piloto (lista de vueltas para parar).
    private List<Integer> pitStopStrategy;
    // Tiempos registrados en cada parada en boxes.
    private List<Integer> pitStopTimes;
    // Generador de números aleatorios (podría usarse para más variabilidad en el futuro).
    private Random random;
    // Referencia al Pit Lane para acceder a sus métodos.
    private PitLane pitLane;
    // Tiempo total de la carrera del piloto.
    private long tiempoTotal;

    // Constructor del piloto.
    public Piloto(String nombre, List<Integer> pitStopStrategy, PitLane pitLane) {
        this.nombre = nombre; // Nombre del piloto.
        this.pitStopStrategy = pitStopStrategy; // Estrategia de paradas en boxes.
        this.pitStopTimes = new ArrayList<>(); // Lista para registrar los tiempos de parada.
        this.random = new Random(); // Inicializa el generador de números aleatorios.
        this.pitLane = pitLane; // Referencia al Pit Lane.
        this.tiempoTotal = 0; // Inicializa el tiempo total de la carrera a 0.
    }

    // Método para simular la carrera del piloto.
    public void correrCarrera(int totalLaps) throws PitLaneCompletoException {
        long inicio = System.currentTimeMillis(); // Tiempo de inicio de la carrera.
        for (int lap = 1; lap <= totalLaps; lap++) {
            // Comprueba si el piloto debe hacer una parada en esta vuelta.
            try {
                if (pitStopStrategy.contains(lap)) {
                    // El piloto entra al Pit Lane y se registra el tiempo de parada.
                    int pitStopTime = pitLane.entrar(nombre, lap);
                    pitStopTimes.add(pitStopTime); // Añade el tiempo de parada a la lista.
                    setTiempoTotal(getTiempoTotal() + pitStopTime); // Suma al tiempo total de la carrera.
                    pitLane.salir(nombre); // El piloto sale del Pit Lane.
                }
            } catch (PitLaneCompletoException e) {
                System.out.println(e.getMessage());
            }
            // Verifica si el piloto ha completado la última vuelta.
            if (lap == totalLaps) {
                System.out.println("\t🏁 " + nombre + " ha cruzado la meta!");
            }
        }
        long fin = System.currentTimeMillis(); // Tiempo de finalización de la carrera.
        setTiempoTotal(getTiempoTotal() + (fin - inicio)); // Actualiza el tiempo total de la carrera.
    }

    // Getter para obtener los tiempos de parada en boxes.
    public List<Integer> getPitStopTimes() {
        return pitStopTimes;
    }

    // Getter para obtener el nombre del piloto.
    public String getNombre() {
        return nombre;
    }

    // Getter para obtener el tiempo total de la carrera del piloto.
    public long getTiempoTotal() {
        return tiempoTotal;
    }

    // Setter para establecer el tiempo total de la carrera del piloto.
    public void setTiempoTotal(long tiempoTotal) {
        this.tiempoTotal = tiempoTotal;
    }

    // Método para comparar pilotos basado en su tiempo total de carrera.
    @Override
    public int compareTo(Piloto otro) {
        return Long.compare(this.tiempoTotal, otro.tiempoTotal);
    }
}