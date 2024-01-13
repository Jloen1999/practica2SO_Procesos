package consultas;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * @Description Esta clase Reducer de MapReduce procesa los ingresos totales de ventas del año 2019.
 *              Extiende MapReduceBase e implementa la interfaz Reducer de Hadoop.
 *              Toma Text como clave (año) y DoubleWritable como valor (monto de venta) y produce
 *              Text (año) y Text (ganancias totales formateadas) como salida.
 * @author Jose Luis Obiang Ela Nanguang
 * @version 1.0
 * {@link Reducer}
 */
public class Ingresos2019Reducer extends Reducer<Text, DoubleWritable, Text, Text> {

    private double maxGanancias = 0.0;
    private final Text maxKey = new Text();

    @Override
    public void reduce(Text key, Iterable<DoubleWritable> values, Context context) throws IOException, InterruptedException {
        double gananciasTotales = 0;
        // Suma todas las ganancias del año
        for (DoubleWritable val : values) {
            double ganancias = val.get();
            gananciasTotales += ganancias;
            if (gananciasTotales > maxGanancias) {
                maxGanancias = gananciasTotales;
                maxKey.set("En el año " + key + " se ganó: ");
            }
        }
        // Emite el año y las ganancias totales formateadas
        context.write(key, new Text(String.format("%.2f", gananciasTotales)));
    }


    /**
     * Función que se invoca una vez al inicio de la tarea del Reducer,
     * antes de procesar cualquier clave/valor.
     * @param context
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    public void setup(Context context) throws IOException, InterruptedException {
        context.write(new Text(""), new Text("1.¿Cuánto se ganó en 2019? "));
    }

    /**
     * Se llama al final de la tarea del Reducer, después de procesar todas las claves/valores.
     * @param context
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    public void cleanup(Context context) throws IOException, InterruptedException {
        context.write(maxKey, new Text(String.format("%.2f€", maxGanancias)));
    }
}
