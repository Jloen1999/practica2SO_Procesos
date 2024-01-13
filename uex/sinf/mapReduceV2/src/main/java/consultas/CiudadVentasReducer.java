package consultas;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * @Description Esta clase Reducer de MapReduce procesa las ventas por ciudad.
 *              Extiende MapReduceBase e implementa la interfaz Reducer de Hadoop.
 *              Toma Text (ciudad) y IntWritable (cantidad de ventas) como entradas
 *              y produce Text (ciudad) e IntWritable (ventas totales) como salida.
 *              Suma todas las ventas por ciudad.
 * @author Jose Luis Obiang Ela Nanguang
 * @version 1.0
 * {@link Reducer}
 */

public class CiudadVentasReducer extends Reducer<Text, IntWritable, Text, Text> {

    private int maxVentas = 0;
    private final Text maxKey = new Text();
    @Override
    public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
        int totalVentas = 0;
        // Suma las cantidades de ventas para cada ciudad. Cada valor representa la cantidad de ventas en una transacción.
        for (IntWritable val : values) {
            int ventas = val.get();
            totalVentas += ventas;
            if (totalVentas > maxVentas) {
                maxVentas = totalVentas;
                maxKey.set("La ciudad de " + key + " tiene el mayor número de ventas con un total de: ");
            }
        }
        // Emite la ciudad (key) y el total acumulado de ventas (totalVentas) como un par clave-valor.
        context.write(key, new Text(totalVentas + ""));
    }

    public void setup(Context context) throws IOException, InterruptedException {
        context.write(new Text(""), new Text("3.¿Qué ciudad tuvo el mayor número de ventas?"));
    }

    @Override
    public void cleanup(Context context) throws IOException, InterruptedException {
        context.write(maxKey, new Text(maxVentas + " ventas"));
    }
}
