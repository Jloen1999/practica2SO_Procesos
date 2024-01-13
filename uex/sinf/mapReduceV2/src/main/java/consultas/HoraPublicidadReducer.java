package consultas;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.Iterator;

/**
 * @Description Esta clase es un Reducer para MapReduce que procesa datos de pedidos
 *              para calcular la suma de pedidos realizados en cada hora. La clase extiende
 *              MapReduceBase e implementa la interfaz Reducer proporcionada por Hadoop.
 *              La clave de entrada es Text (hora), y el valor de entrada es IntWritable (contador de pedidos).
 *              Produce un Text (hora) y un Text (suma de pedidos) como salida.
 * @author Jose Luis Obiang Ela Nanguang
 * @version 1.0
 * {@link Reducer}
 */
public class HoraPublicidadReducer extends Reducer<Text, IntWritable, Text, Text> {
    private int maxPedidosHora = 0;
    private final Text maxKey = new Text();

    @Override
    public void reduce(Text key, Iterable<IntWritable> values, Context context) throws InterruptedException, IOException {
        int totalPedidosPorHora = 0;
        // Suma los pedidos de la misma hora
        for (IntWritable val : values) {
            int ventas = val.get();
            totalPedidosPorHora += ventas;
            if (totalPedidosPorHora > maxPedidosHora) {
                maxPedidosHora = totalPedidosPorHora;
                maxKey.set("La hora para maximizar la probabilidad de que el cliente compre el producto es: " + key + "\nya que en esa hora se hizo un total de ");
            }
        }
        // Emite la hora y el total de pedidos en esa hora
        context.write(key, new Text(totalPedidosPorHora + ""));
    }

    @Override
    public void setup(Context context) throws IOException, InterruptedException {
        context.write(new Text(""), new Text("4.¿A qué hora debemos mostrar publicidad para maximizar la probabilidad de que el cliente compre el producto?"));
    }

    @Override
    public void cleanup(Context context) throws IOException, InterruptedException {
        context.write(maxKey, new Text(maxPedidosHora + " pedidos."));
    }

}
