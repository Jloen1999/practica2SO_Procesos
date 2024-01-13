package consultas;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

import java.io.IOException;

/**
 * @Description Esta clase Mapper de MapReduce analiza la hora de los pedidos para determinar cuándo mostrar publicidad.
 *              Extiende MapReduceBase e implementa la interfaz Mapper de Hadoop.
 *              Toma LongWritable y Text como entradas (clave y valor respectivamente) y produce
 *              Text (hora) e IntWritable (contador) como salida.
 * @author Jose Luis Obiang Ela Nanguang
 * @version 1.0
 * {@link MapReduceBase}
 * {@link Mapper}
 */

public class HoraPublicidadMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, IntWritable> {
    private final static IntWritable one = new IntWritable(1);
    private final Text hora = new Text();

    @Override
    public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
        String[] parts = value.toString().split(",");
        if (parts.length > 5) {
            String fechaHora = parts[4]; // Extrae la fecha y hora
            try {
                // Extrae la hora
                String[] horaMinuto = fechaHora.split(" ")[1].split(":");
                if (!horaMinuto[0].equalsIgnoreCase("date")) { // Si no es un formato de fecha, no se puede convertir a horas.
                    int horaInt = Integer.parseInt(horaMinuto[0]);
                    int temp = horaInt;
                    String amPm = horaInt >= 12 ? "PM" : "AM";
                    // Convertir formato de 24 a 12 horas
                    horaInt = horaInt > 12 ? horaInt - 12 : (horaInt == 0 ? 12 : horaInt);
                    hora.set(horaInt + "(" + temp + ") " + amPm);
                    output.collect(hora, one);
                }
            } catch (Exception e) {
                reporter.setStatus("Error al parsear la hora del pedido: " + fechaHora);
            }
        }
    }
}
