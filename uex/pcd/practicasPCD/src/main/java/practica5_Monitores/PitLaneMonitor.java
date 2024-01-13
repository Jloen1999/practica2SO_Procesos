package practica5_Metaforas;

import java.util.Random;

public class PitLaneMonitor {
    private final int capacity;
    private int ocupacion;
    private Random random;

    public PitLaneMonitor(int capacity) {
        this.capacity = capacity;
        this.ocupacion = 0;
        this.random = new Random();
    }

    public synchronized void entrar(String pilotoName, int vueltaActual) {
        while (ocupacion >= capacity) {
            System.out.println("\t*Solicita acceso PIT STOP " + pilotoName + " en vuelta " + vueltaActual + " (Esperando)");
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrupción en el Pit Lane", e);
            }
        }
        ocupacion++;
        System.out.println("\t🟢 +Entra PIT STOP " + pilotoName + " en vuelta " + vueltaActual);
    }

    public synchronized void salir(String pilotoName) {
        ocupacion--;
        System.out.println("\t🔴 -Sale PIT STOP " + pilotoName);
        notifyAll(); // Notificar a los pilotos que esperan
    }

    public synchronized int cambiarNeumaticos(String pilotoName, int vueltaActual) {
        int pitStopTime = random.nextInt(400) + 200;
        System.out.println(pilotoName + " cambiando neumáticos en vuelta " + vueltaActual + " por " + pitStopTime + " ms");
        try {
            Thread.sleep(pitStopTime);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupción al cambiar neumáticos", e);
        }
        return pitStopTime;
    }
}
