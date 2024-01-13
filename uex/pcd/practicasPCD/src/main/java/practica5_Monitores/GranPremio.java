package practica5_Metaforas;

import com.github.javafaker.Faker;

import java.util.*;

/**
 * Ejecuta la simulación del Gran Premio de Fórmula 1.
 * <ul>Mejoras:
 *     <li>Personalización del número de vueltas y paradas del Gran Premio.</li>
 *     <li>Estrategias únicas de parada para cada piloto.</li>
 *     <li>Mostrar la estrategia inicial y final de cada piloto.</li>
 *     <li>Determinar y anunciar el ganador al final de la carrera.</li>
 * </ul>
 * @author Jose Luis Obiang Ela Nanguang
 * @version 1.0
 * @see practica4_Semaforos.Piloto
 * @see practica4_Semaforos.PitLane
 * @see Faker
 *
 */
public class GranPremio {
    private static final Faker faker = new Faker();
    private static int capacity = 5; // Capacidad del Pit Lane
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // Solicitar al usuario el número total de vueltas y paradas por piloto
        System.out.print("Ingrese el número total de vueltas: ");
        int totalLaps = scanner.nextInt();
        System.out.print("Ingrese el número total de paradas por piloto: ");
        int totalParadas = scanner.nextInt();

        // Crear lista de pilotos con estrategias de paradas
        List<Piloto> pilotos = getPilotos(totalLaps, totalParadas);
        mostrarEstrategiasIniciales(pilotos);

        // Crear y ejecutar hilos para simular la carrera de cada piloto
        List<Thread> hilos = new ArrayList<>();
        for (Piloto piloto : pilotos) {
            Thread hilo = new Thread(() -> piloto.correrCarrera(totalLaps));
            hilos.add(hilo);
            hilo.start();
        }

        // Esperar a que todos los hilos terminen
        for (Thread hilo : hilos) {
            try {
                hilo.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Mostrar estrategias finales y anunciar el ganador
        mostrarEstrategiasFinales(pilotos);
        anunciarGanador(pilotos);
    }

    private static List<Piloto> getPilotos(int totalLaps, int totalParadas) {
        PitLaneMonitor pitLane = new PitLaneMonitor(capacity);
        List<Piloto> pilotos = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            List<Integer> estrategia = generarEstrategia(totalLaps, totalParadas);
            pilotos.add(new Piloto(faker.name().fullName(), estrategia, pitLane));
        }
        return pilotos;
    }

    private static List<Integer> generarEstrategia(int totalLaps, int totalParadas) {
        Random random = new Random();
        Set<Integer> estrategia = new HashSet<>();
        while (estrategia.size() < totalParadas) {
            estrategia.add(random.nextInt(totalLaps) + 1);
        }
        return new ArrayList<>(estrategia);
    }

    private static void mostrarEstrategiasIniciales(List<Piloto> pilotos) {
        System.out.println("Estrategias iniciales de los pilotos:");
        for (Piloto piloto : pilotos) {
            System.out.println(piloto.getNombre() + ": " + piloto.getPitStopStrategy());
        }
    }

    private static void mostrarEstrategiasFinales(List<Piloto> pilotos) {
        System.out.println("Estrategias finales de los pilotos:");
        for (Piloto piloto : pilotos) {
            System.out.println(piloto.getNombre() + ": " + piloto.getPitStopStrategy());
        }
    }

    private static void anunciarGanador(List<Piloto> pilotos) {
        Collections.sort(pilotos);
        Piloto ganador = pilotos.get(0);
        System.out.println("El ganador es: " + ganador.getNombre() + " con un tiempo total de: " + ganador.getTiempoTotal() + " ms");
    }
}
