package practica5_Metaforas;

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
    private List<Integer> pitStopStrategy; // Estrategia de paradas en boxes
    private List<Integer> pitStopTimes; // Tiempos registrados en cada parada en boxes
    private PitLaneMonitor pitLane; // Referencia al Pit Lane con monitor
    private long tiempoTotal; // Tiempo total de la carrera para el piloto

    public Piloto(String nombre, List<Integer> pitStopStrategy, PitLaneMonitor pitLane) {
        this.nombre = nombre;
        this.pitStopStrategy = new ArrayList<>(pitStopStrategy);
        this.pitStopTimes = new ArrayList<>();
        this.pitLane = pitLane;
        this.tiempoTotal = 0;
    }

    // Método para simular la carrera del piloto
    public void correrCarrera(int totalLaps) {
        long inicio = System.currentTimeMillis();
        for (int lap = 1; lap <= totalLaps; lap++) {
            realizarParadaSiNecesario(lap);
            if (lap == totalLaps) {
                System.out.println("\t🏁 " + nombre + " ha cruzado la meta!");
            }
        }
        long fin = System.currentTimeMillis();
        setTiempoTotal(getTiempoTotal() + (fin - inicio));
    }

    // Método para realizar una parada en boxes si es necesario
    private void realizarParadaSiNecesario(int lap) {
        if (pitStopStrategy.contains(lap)) {
            pitLane.entrar(nombre, lap); // Entrar al Pit Lane
            int pitStopTime = pitLane.cambiarNeumaticos(nombre, lap); // Cambiar neumáticos
            pitStopTimes.add(pitStopTime);
            setTiempoTotal(getTiempoTotal() + pitStopTime);
            pitLane.salir(nombre); // Salir del Pit Lane
            pitStopStrategy.remove(Integer.valueOf(lap));
        } else {
            reprogramarParadaPerdida(lap);
        }
    }

    // Método para reprogramar paradas perdidas
    private void reprogramarParadaPerdida(int lapActual) {
        for (int i = 0; i < pitStopStrategy.size(); i++) {
            if (pitStopStrategy.get(i) < lapActual) {
                pitStopStrategy.set(i, lapActual + 1);
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
        return Long.compare(this.tiempoTotal, otro.tiempoTotal);
    }
}
