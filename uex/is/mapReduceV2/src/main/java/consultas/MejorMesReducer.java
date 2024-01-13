package consultas;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

import java.io.IOException;
import java.util.Iterator;

/**
 * @Description Clase Reducer para determinar el mejor mes en términos de ganancias. Extiende
 * MapReduceBase e implementa la interfaz Reducer de Hadoop. Su tarea es sumar las ganancias
 * totales de cada mes y emitir el mes junto con las ganancias totales formateadas.
 * @author Jose Luis Obiang Ela Nanguang
 * @version 1.0
 * {@link MapReduceBase}
 * {@link Reducer}
 */
public class MejorMesReducer extends MapReduceBase implements Reducer<Text, DoubleWritable, Text, Text> {

    @Override
    public void reduce(Text key, Iterator<DoubleWritable> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
        double gananciasTotalesMes = 0;
        // Suma las ganancias totales por mes
        while (values.hasNext()) {
            gananciasTotalesMes += values.next().get();
        }
        // Formatea las ganancias totales a dos decimales
        String formattedSum = String.format("%.2f", gananciasTotalesMes);
        // Emite el mes y las ganancias totales
        output.collect(key, new Text(formattedSum + "\n-------------------------------------------------"));
    }
}
