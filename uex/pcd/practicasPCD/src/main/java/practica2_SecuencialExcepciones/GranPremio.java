package practica1_Secuencial.V2;

import com.github.javafaker.Faker;
import java.util.*;

/**
 * Ejecuta la simulación del Gran Premio de Fórmula 1.
 * <ul>Incluye
 *     <li>la generación de una estrategia de paradas en boxes</li>
 *     <li>la creación de pilotos con nombres aleatorios</li>
 *     <li>la simulación de la carrera para cada piloto</li>
 *     <li>Al final, determina y muestra el ganador de la carrera basado en el tiempo total.</li>
 * </ul>
 *
 * @author Jose Luis Obiang Ela Nanguang
 * @version 1.0
 * @see Piloto
 * @see PitLane
 * @see Faker
 */
public class GranPremio {
    // Número total de vueltas en la carrera.
    private static final int totalLaps = 75;
    // Capacidad máxima del Pit Lane.
    private static final int capacity = 5;
    // Generador de nombres aleatorios para los pilotos.
    private static final Faker faker = new Faker();

    public static void main(String[] args) {
        // Genera una estrategia de paradas en boxes.
        List<Integer> estrategia = generarEstrategia();
        // Ordena la estrategia para asegurarse de que las paradas están en orden.
        Collections.sort(estrategia);
        // Crea los pilotos para la carrera con la estrategia generada.
        List<Piloto> pilotos = getPilotos(estrategia);
        // Muestra la estrategia común de paradas para todos los pilotos.
        System.out.println("PIT STOP STRATEGY: " + estrategia);

        // Simula la carrera para cada piloto.
        for (Piloto piloto : pilotos) {
            piloto.correrCarrera(totalLaps);
        }

        // Ordena los pilotos por su tiempo total en la carrera para determinar el ganador.
        Collections.sort(pilotos);
        // El primer piloto en la lista ordenada es el ganador.
        Piloto ganador = pilotos.get(0);
        // Muestra el nombre y el tiempo total del piloto ganador.
        System.out.println("Ganador: " + ganador.getNombre() + " con tiempo total: " + ganador.getTiempoTotal());
    }

    // Método para generar una estrategia de paradas en boxes.
    private static List<Integer> generarEstrategia() {
        Random random = new Random();
        List<Integer> estrategia = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            // Añade una vuelta aleatoria a la estrategia de paradas.
            estrategia.add(random.nextInt(totalLaps) + 1);
        }
        return estrategia;
    }

    // Método para crear los pilotos de la carrera.
    private static List<Piloto> getPilotos(List<Integer> estrategia) {
        // Crea una instancia de PitLane con la capacidad especificada.
        PitLane pitLane = new PitLane(capacity);
        List<Piloto> pilotos = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            // Añade un nuevo piloto a la lista con un nombre generado aleatoriamente y la estrategia de paradas.
            pilotos.add(new Piloto(faker.name().fullName(), estrategia, pitLane));
        }
        return pilotos;
    }
}
