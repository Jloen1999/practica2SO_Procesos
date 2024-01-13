package consultas;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * @Description Clase Reducer para la consulta del producto más vendido. Extiende MapReduceBase
 * e implementa la interfaz Reducer de Hadoop. Esta clase suma la cantidad total vendida para
 * cada producto y emite el nombre del producto y la cantidad total vendida como un par clave-valor.
 * @author Jose Luis Obiang Ela Nanguang
 * @version 1.0
 * {@link Reducer}
 */
public class ProductoMasVendidoReducer extends Reducer<Text, IntWritable, Text, Text> {

    private int maxVentas = 0;
    private final Text maxKey = new Text();

    @Override
    public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
        int totalOrden = 0;
        // Suma las cantidades de cada producto
        for (IntWritable val : values) {
            int ventas = val.get();
            totalOrden += ventas;
            if (totalOrden > maxVentas) {
                maxVentas = totalOrden;
                maxKey.set("El producto" + key + " tiene el mayor número de ventas, ya que se ha vendido más que los demás con un total de: ");
            }
        }
        // Emite el nombre del producto y la cantidad total vendida
        context.write(key, new Text(totalOrden + ""));
    }

    @Override
    public void setup(Context context) throws IOException, InterruptedException {
        context.write(new Text(""), new Text("5.¿Qué producto vendió más? ¿Por qué crees que vendió más?"));
    }

    @Override
    public void cleanup(Context context) throws IOException, InterruptedException {
        context.write(maxKey, new Text(String.format(maxVentas + " ventas")));
    }

}
