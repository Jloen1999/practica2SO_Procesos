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
 * @Description Esta clase es un Reducer para MapReduce que procesa datos de pedidos
 *              para calcular la suma de pedidos realizados en cada hora. La clase extiende
 *              MapReduceBase e implementa la interfaz Reducer proporcionada por Hadoop.
 *              La clave de entrada es Text (hora), y el valor de entrada es IntWritable (contador de pedidos).
 *              Produce un Text (hora) y un IntWritable (suma de pedidos) como salida.
 * @author Jose Luis Obiang Ela Nanguang
 * @version 1.0
 * {@link MapReduceBase}
 * {@link Reducer}
 */
public class HoraPublicidadReducer extends MapReduceBase implements Reducer<Text, IntWritable, Text, IntWritable> {
    @Override
    public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
        int sumaPedidosPorHora = 0;
        // Suma los pedidos de la misma hora
        while (values.hasNext()) {
            sumaPedidosPorHora += values.next().get();
        }
        // Emite la hora y el total de pedidos en esa hora
        output.collect(key, new IntWritable(sumaPedidosPorHora));
    }
}
