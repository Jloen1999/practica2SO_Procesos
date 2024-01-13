package practica1_Secuencial;

import com.github.javafaker.Faker;

import java.util.*;

import static org.fusesource.jansi.Ansi.Color.*;
import static org.fusesource.jansi.Ansi.ansi;

/**
 * Simulación secuencial del Gran Premio de Fórmula 1.
 * @author Jose Luis Obiang Ela Nanguang
 * @version 1.0
 * @since 1.0
 * @see Piloto
 * @see PitLane
 */
public class GranPremio {

    /**
     * Representa el número total de vueltas que el piloto debe completar en la carrera.
     * En este caso, está predefinido a 75 vueltas.
     */
    private static final int totalLaps = 10; // Número máximo de pilotos en la carrera
    private static final int capacity = 5; // Capacidad máxima del Pit Lane
    private static Faker faker = new Faker(); // Para la generación de datos aleatorios

    /**
     * Punto de entrada principal del programa.
     *
     * @param args Argumentos de línea de comando (no utilizados en este caso).
     */
    public static void main(String[] args) {
        // Define la estrategia de paradas en boxes para los pilotos.
        List<Integer> estrategia = new ArrayList<>();

        // Llenar la lista con valores únicos
        for (int i = 0; i < capacity; i++) {
            int nuevoValor;
            do {
                nuevoValor = (new Random()).nextInt(1, totalLaps + 1);
            } while (estrategia.contains(nuevoValor)); // Verificar si ya está en la lista
            estrategia.add(nuevoValor);
        }

        // Asegurar que la lista esté ordenada
        Collections.sort(estrategia);

        // Obtiene la lista de pilotos para la carrera.
        List<Piloto> pilotos = getPilotos(estrategia);

        // Muestra la estrategia de paradas en boxes.
        System.out.println(ansi().fg(BLUE).a( "PIT STOP STRATEGY").reset());
        System.out.println("+----+----+----+----+----+");
        System.out.println("| " + ansi().fg(RED).a( + estrategia.get(0)).reset().a(" | ").fg(GREEN).a(estrategia.get(1)).reset().a(" | ").fg(MAGENTA).a(estrategia.get(2)).reset().a(" | ").fg(YELLOW).a(estrategia.get(3)).reset().a(" | ").fg(CYAN).a(estrategia.get(4)).reset().a(" |").reset());
        System.out.println("+----+----+----+----+----+");
        System.out.println("\n" + ansi().fg(BLUE).a("###########\nSTART\n###########").reset());

        // Simula la carrera para cada piloto.
        for (Piloto piloto : pilotos) {
            piloto.correrCarrera(totalLaps);
        }

        // Muestra el final de la carrera.
        // Mostrar el encabezado de la sección de finalización
        System.out.println("\n" +ansi().fg(BLUE).a("###########\nFINISH\n###########").reset());
        System.out.println(ansi().fg(CYAN).a("Clasificación final:").reset());

// Encabezado de la tabla
        System.out.println(ansi().fg(GREEN).a("+-----------------------+-----------------------+-----------------------+---------------------------+"));
        System.out.println("| Piloto                | Estrategia de Paradas | Tiempos de Parada (ms)| Tiempo Total en Boxes     |");
        System.out.println(ansi().a("+-----------------------+-----------------------+-----------------------+---------------------------+").reset());

        for (Piloto piloto : pilotos) {
            String nombre = piloto.getNombre();
            String tiemposParada = piloto.getPitStopTimes().toString();
            int tiempoTotalBox = piloto.getPitStopTimes().stream().mapToInt(i -> i).sum();

            // Mostrar los detalles del piloto en una fila de la tabla
            System.out.printf("| "+YELLOW+"%-21s "+"| "+YELLOW+"%-21s "+ "| "+YELLOW+"%-21s "+"| " + YELLOW+"%-21d "+"|\n", nombre, estrategia, tiemposParada, tiempoTotalBox);
            System.out.println("+-----------------------+-----------------------+-----------------------+---------------------------+");
        }

        // Se ordena la lista de pilotos por su tiempo total de la carrera
        Collections.sort(pilotos, Collections.reverseOrder());
        // El primer piloto es el ganador de la carrera
        Piloto ganador = pilotos.get(0);
        // Se calcula el tiempo ganador
        long tiempoGanador = pilotos.get(0).getTiempoTotal();
        // Se muestra la clasificación final de los pilotos
        System.out.println("\n🏆 " + ansi().fg(BLUE).a("El ganador del Gran Premio es: " + ganador.getNombre() + " con un tiempo total de: " + tiempoGanador + " ms").reset());
    }

    /**
     * Genera y devuelve una lista de pilotos para la carrera.
     *
     * @param estrategia Estrategia de paradas en boxes para los pilotos.
     * @return Lista de pilotos.
     */
    private static List<Piloto> getPilotos(List<Integer> estrategia) {
        // Se crea el objeto que representa el Pit Lane de la carrera con una capacidad maxima de 5 pilotos
        PitLane pitLane = new PitLane(capacity);

        // Define la lista de pilotos con sus respectivos nombres y estrategias.
        List<Piloto> pilotos = new ArrayList<>();
        for (int i = 0; i < totalLaps; i++) {
            pilotos.add(new Piloto(faker.name().fullName(), estrategia, pitLane));
        }

        return pilotos;
    }
}