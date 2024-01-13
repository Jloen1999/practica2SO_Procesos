package consultas;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * @Description Clase Reducer para determinar el mejor mes en términos de ganancias. Extiende
 * MapReduceBase e implementa la interfaz Reducer de Hadoop. Su tarea es sumar las ganancias
 * totales de cada mes y emitir el mes junto con las ganancias totales formateadas.
 * @author Jose Luis Obiang Ela Nanguang
 * @version 1.0
 * {@link Reducer}
 */
public class MejorMesReducer extends Reducer<Text, DoubleWritable, Text, Text> {

    private double maxGananciasPorMes = 0.0;
    private final Text maxKey = new Text();

    @Override
    public void reduce(Text key, Iterable<DoubleWritable> values, Context context) throws IOException, InterruptedException {
        double gananciasTotalesPorMes = 0.0;
        // Suma las ganancias totales por mes
        for (DoubleWritable val : values) {
            double gananciasPorMes = val.get();
            gananciasTotalesPorMes += gananciasPorMes;
            if (gananciasTotalesPorMes > maxGananciasPorMes) {
                maxGananciasPorMes = gananciasTotalesPorMes;
                maxKey.set("El mes de " + key + " es el mejor mes ya que tiene el mayor total de ganancias, con un total de:");
            }
        }
        // Formatea las ganancias totales a dos decimales
        String formattedSum = String.format("%.2f", gananciasTotalesPorMes);
        // Emite el mes y las ganancias totales
        context.write(key, new Text(formattedSum + "\n-------------------------------------------------"));
    }

    @Override
    public void setup(Context context) throws IOException, InterruptedException {
        context.write(new Text(""), new Text("2.¿Cuál fue el mejor mes para las ventas? ¿Cuánto se ganó ese mes?"));
    }

    @Override
    public void cleanup(Context context) throws IOException, InterruptedException {
        context.write(maxKey, new Text(String.format("%.2f€", maxGananciasPorMes)));
    }


}
