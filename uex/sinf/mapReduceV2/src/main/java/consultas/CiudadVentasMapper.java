package consultas;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * @Description Esta clase Mapper de MapReduce analiza las ventas por ciudad.
 *              Extiende MapReduceBase e implementa la interfaz Mapper de Hadoop.
 *              Toma LongWritable y Text como entradas (clave y valor respectivamente) y
 *              produce Text (ciudad) e IntWritable (cantidad de ventas) como salida.
 * @author Jose Luis Obiang Ela Nanguang
 * @version 1.0
 * {@link Mapper}
 */

public class CiudadVentasMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

    private final Text ciudad = new Text();
    private final IntWritable cantidad = new IntWritable();

    @Override
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        // Convierte el valor de entrada (una línea de texto) en una cadena de caracteres.
        String line = value.toString();
        // Divide la línea usando comas como separador para obtener los campos.
        String[] fields = line.split(",");

        try {

            if (fields.length > 6) { // Campo dirección <calle, ciudad, C-Postal>
                ciudad.set(fields[6].trim() + "    "); // Extrae la ciudad
            } else if (fields.length == 6) { // Campo dirección <calle; ciudad; C-Postal>
                ciudad.set(fields[5].split(";")[1].trim()); // Extrae la ciudad
            }

            // Extrae la cantidad vendida (se asume que está en el tercer campo).
            cantidad.set(Integer.parseInt(fields[2]));
            if(ciudad.getLength() <= 7) {
                ciudad.set(ciudad + "    ");
                context.write(ciudad, cantidad);
            }
        } catch (NumberFormatException e) {
            context.setStatus("Error al parsear la cantidad: " + line);
        } catch (ArrayIndexOutOfBoundsException e) {
            context.setStatus("Formato de línea incorrecto: " + line);
        }
    }


}
