package consultas;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

import java.io.IOException;

/**
 * @Description Esta clase es un Mapper para MapReduce que procesa datos de ventas
 *              para calcular las ganancias totales en el año 2019. Esta clase extiende
 *              MapReduceBase e implementa la interfaz Mapper proporcionada por Hadoop.
 *              La clave de entrada es LongWritable, el valor de entrada es Text (una línea
 *              de datos), y produce un Text (año) y un DoubleWritable (ganancias totales) como salida.
 * @author Jose Luis Obiang Ela Nanguang
 * @version 1.0
 * {@link MapReduceBase}
 * {@link Mapper}
 */
public class Ingresos2019Mapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, DoubleWritable>{

    @Override
    public void map(LongWritable key, Text value, OutputCollector<Text, DoubleWritable> output, Reporter reporter) throws IOException {
        // Convierte la línea de texto en una cadena de caracteres
        String line = value.toString();
        // Divide la línea en campos separados por comas
        String[] fields = line.split(",");

        // Verifica que la línea tenga suficientes campos
        if (fields.length > 5) {
            // Extrae la fecha del pedido
            String date = fields[4];
            // Separa la fecha en sus componentes
            String[] dateParts = date.split(" ")[0].split("/");

            // Verifica que la fecha esté en el formato correcto
            if (dateParts.length == 3) {
                // Extrae el año y lo convierte a formato '20xx'
                String year = "20" + dateParts[2];

                try {
                    // Calcula el total de ventas, multiplicando la cantidad de ventas por precio
                    int numVentas = Integer.parseInt(fields[2]); // Extraer la cantidad de ventas
                    double precio = Double.parseDouble(fields[3]); // Extraer el precio
                    double gananciaPorVenta = numVentas * precio; // Calcula las ganancias por venta
                    // Emite el año y el monto total de ventas
                    if(year.equals("2019")){
                        output.collect(new Text(year + "✅    ------->"), new DoubleWritable(gananciaPorVenta));
                    }else{
                        output.collect(new Text(year + "    ------->"), new DoubleWritable(gananciaPorVenta));
                    }

                } catch (NumberFormatException e) {
                    // Informa si hay un error en los datos numéricos
                    reporter.setStatus("Error, precio no válido. ");
                }
            }
        }
    }
}
