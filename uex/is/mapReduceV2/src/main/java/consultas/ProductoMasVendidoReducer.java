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
 * @Description Clase Reducer para la consulta del producto más vendido. Extiende MapReduceBase
 * e implementa la interfaz Reducer de Hadoop. Esta clase suma la cantidad total vendida para
 * cada producto y emite el nombre del producto y la cantidad total vendida como un par clave-valor.
 * @author Jose Luis Obiang Ela Nanguang
 * @version 1.0
 * {@link MapReduceBase}
 * {@link Reducer}
 */
public class ProductoMasVendidoReducer extends MapReduceBase implements Reducer<Text, IntWritable, Text, IntWritable> {

    @Override
    public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
        int totalOrden = 0;
        // Suma las cantidades de cada producto
        while (values.hasNext()) {
            totalOrden += values.next().get();
        }
        // Emite el nombre del producto y la cantidad total vendida
        output.collect(key, new IntWritable(totalOrden));
    }
}
