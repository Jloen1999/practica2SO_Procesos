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
 * @Description Clase Mapper para identificar el producto más vendido. Extiende MapReduceBase
 * e implementa la interfaz Mapper de Hadoop. Esta clase procesa cada línea de entrada, extrayendo
 * el nombre del producto y la cantidad vendida, y emite el nombre del producto como clave y la
 * cantidad como valor.
 * @author Jose Luis Obiang Ela Nanguang
 * @version 1.0
 * {@link MapReduceBase}
 * {@link Mapper}
 */
public class ProductoMasVendidoMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, IntWritable> {

    @Override
    public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
        String line = value.toString();
        String[] fields = line.split(",");

        if (fields.length > 3) {
            try {
                // Extrae el nombre del producto y la cantidad vendida
                String nombreProducto = fields[1];
                int cantidadVentas = Integer.parseInt(fields[2]);
                // Emite el nombre del producto y la cantidad de ventas como un par clave-valor
                output.collect(new Text(nombreProducto), new IntWritable(cantidadVentas));
            } catch (NumberFormatException e) {
                reporter.setStatus("Error al parsear la cantidad: " + line);
            } catch (ArrayIndexOutOfBoundsException e) {
                reporter.setStatus("Formato de línea incorrecto: " + line);
            }
        }
    }
}
