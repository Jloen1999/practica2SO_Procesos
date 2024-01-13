package consultas;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

import java.io.IOException;
import java.util.Iterator;

/**
 * @Description Esta clase Reducer de MapReduce procesa las ventas por ciudad.
 *              Extiende MapReduceBase e implementa la interfaz Reducer de Hadoop.
 *              Toma Text (ciudad) y IntWritable (cantidad de ventas) como entradas
 *              y produce Text (ciudad) e IntWritable (ventas totales) como salida.
 *              Suma todas las ventas por ciudad.
 * @author Jose Luis Obiang Ela Nanguang
 * @version 1.0
 * {@link MapReduceBase}
 * {@link Reducer}
 */

public class CiudadVentasReducer extends MapReduceBase implements Reducer<Text, IntWritable, Text, IntWritable> {
    @Override
    public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
        int totalVentas = 0;
        // Suma las cantidades de ventas para cada ciudad. Cada valor representa la cantidad de ventas en una transacción.
        while (values.hasNext()) {
            totalVentas += values.next().get();
        }
        // Emite la ciudad (key) y el total acumulado de ventas (totalVentas) como un par clave-valor.
        output.collect(key, new IntWritable(totalVentas));
    }
}
