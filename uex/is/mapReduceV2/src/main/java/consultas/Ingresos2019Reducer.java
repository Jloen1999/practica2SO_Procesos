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
 * @Description Esta clase Reducer de MapReduce procesa los ingresos totales de ventas del año 2019.
 *              Extiende MapReduceBase e implementa la interfaz Reducer de Hadoop.
 *              Toma Text como clave (año) y DoubleWritable como valor (monto de venta) y produce
 *              Text (año) y Text (ganancias totales formateadas) como salida.
 * @author Jose Luis Obiang Ela Nanguang
 * @version 1.0
 * {@link MapReduceBase}
 * {@link Reducer}
 */
public class Ingresos2019Reducer extends MapReduceBase implements Reducer<Text, DoubleWritable, Text, Text> {

    @Override
    public void reduce(Text key, Iterator<DoubleWritable> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
        double gananciasTotales = 0;
        // Suma todas las ganancias del año
        while (values.hasNext()) {
            gananciasTotales += values.next().get();
        }
        // Emite el año y las ganancias totales formateadas
        output.collect(key, new Text(String.format("%.2f", gananciasTotales)));
    }
}
