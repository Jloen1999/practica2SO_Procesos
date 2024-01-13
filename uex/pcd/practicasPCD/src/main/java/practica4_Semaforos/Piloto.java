package practica3_Sincronización;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Clase Piloto que implementa la interfaz Comparable para poder comparar pilotos
 * basándose en su tiempo total de carrera.
 * Simula la carrera de un piloto individual en la simulación del Gran Premio.
 *
 * <ul>Mejoras:
 *     <li>Manejo de estrategias únicas de parada para cada piloto.</li>
 *     <li>Reprogramación de paradas perdidas.</li>
 *     <li>Control de las vueltas completadas por cada piloto.</li>
 * </ul>
 *
 * @author Jose Luis Obiang Ela Nanguang
 * @version 1.0
 * @see PitLane
 */
public class Piloto implements Comparable<Piloto> {
    private String nombre; // Nombre del piloto
    private List<Integer> pitStopStrategy; // Estrategia de paradas en boxes (vueltas en las que el piloto debe entrar)
    private List<Integer> pitStopTimes; // Tiempos registrados en cada parada en boxes
    private Random random; // Generador de números aleatorios (no se utiliza en el código actual)
    private PitLane pitLane; // Referencia al Pit Lane
    private long tiempoTotal; // Tiempo total de la carrera para el piloto

    public Piloto(String nombre, List<Integer> pitStopStrategy, PitLane pitLane) {
        this.nombre = nombre;
        this.pitStopStrategy = new ArrayList<>(pitStopStrategy); // Copia de la estrategia para permitir modificaciones
        this.pitStopTimes = new ArrayList<>();
        this.random = new Random();
        this.pitLane = pitLane;
        this.tiempoTotal = 0;
    }

    // Método para simular la carrera del piloto
    public void correrCarrera(int totalLaps) {
        long inicio = System.currentTimeMillis(); // Tiempo de inicio de la carrera
        for (int lap = 1; lap <= totalLaps; lap++) {
            realizarParadaSiNecesario(lap); // Realizar parada en boxes si es necesario
            if (lap == totalLaps) {
                System.out.println("\t🏁 " + nombre + " ha cruzado la meta!"); // Anunciar el fin de la carrera para el piloto
            }
        }
        long fin = System.currentTimeMillis(); // Tiempo de fin de la carrera
        setTiempoTotal(getTiempoTotal() + (fin - inicio)); // Actualizar el tiempo total de carrera
    }

    // Método para realizar una parada en boxes si es necesario
    private void realizarParadaSiNecesario(int lap) {
        if (pitStopStrategy.contains(lap)) {
            int pitStopTime = pitLane.entrar(nombre, lap); // Entrar al Pit Lane y obtener tiempo de parada
            pitStopTimes.add(pitStopTime); // Registrar el tiempo de parada
            setTiempoTotal(getTiempoTotal() + pitStopTime); // Añadir tiempo de parada al tiempo total
            pitLane.salir(nombre); // Salir del Pit Lane
            pitStopStrategy.remove(Integer.valueOf(lap)); // Eliminar la vuelta de la estrategia de parada
        } else {
            // Reprogramar paradas perdidas para la siguiente vuelta si es necesario
            reprogramarParadaPerdida(lap);
        }
    }

    // Método para reprogramar paradas perdidas
    private void reprogramarParadaPerdida(int lapActual) {
        for (int i = 0; i < pitStopStrategy.size(); i++) {
            if (pitStopStrategy.get(i) < lapActual) {
                pitStopStrategy.set(i, lapActual + 1); // Reprogramar la parada perdida para la siguiente vuelta
            }
        }
    }

    // Métodos getters y setters
    public List<Integer> getPitStopTimes() {
        return pitStopTimes;
    }

    public String getNombre() {
        return nombre;
    }

    public long getTiempoTotal() {
        return tiempoTotal;
    }

    public void setTiempoTotal(long tiempoTotal) {
        this.tiempoTotal = tiempoTotal;
    }

    public List<Integer> getPitStopStrategy() {
        return new ArrayList<>(pitStopStrategy);
    }

    @Override
    public int compareTo(Piloto otro) {
        return Long.compare(this.tiempoTotal, otro.tiempoTotal); // Comparar pilotos por su tiempo total de carrera
    }
}
