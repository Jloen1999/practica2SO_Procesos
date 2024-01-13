package consultas;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * @Description Esta clase Mapper de MapReduce analiza la hora de los pedidos para determinar cuándo mostrar publicidad.
 *              Extiende MapReduceBase e implementa la interfaz Mapper de Hadoop.
 *              Toma LongWritable y Text como entradas (clave y valor respectivamente) y produce
 *              Text (hora) e IntWritable (contador) como salida.
 * @author Jose Luis Obiang Ela Nanguang
 * @version 1.0
 * {@link Mapper}
 */

public class HoraPublicidadMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
    private final static IntWritable one = new IntWritable(1);
    private final Text hora = new Text();

    @Override
    public void map(LongWritable key, Text value, Context context){
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
                    if(horaInt == 1 || horaInt == 2 || horaInt == 3 || horaInt == 4 || horaInt == 5 || horaInt == 6 || horaInt == 7 || horaInt == 8 || horaInt == 9){
                        hora.set(horaInt + "(" + temp + ") " + amPm + "  ");
                    }else{
                        hora.set(horaInt + "(" + temp + ") " + amPm);
                    }
                    context.write(hora, one);
                }
            } catch (Exception e) {
                context.setStatus("Error al parsear la hora del pedido: " + fechaHora);
            }
        }
    }
}
