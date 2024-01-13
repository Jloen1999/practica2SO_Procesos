import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class VentasAnalysis {
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Uso: VentasAnalysis <input path> <output path>");
            System.exit(-1);
        }

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "Ventas Analysis");
        job.setJarByClass(VentasAnalysis.class);

        // Establecer clases Mapper y Reducer
        job.setMapperClass(VentasMapper.class);
        job.setReducerClass(VentasReducer.class);

        // Establecer tipos de salida del Mapper
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        // Establecer tipos de salida del trabajo completo
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        // Rutas de archivos de entrada y salida
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
