import consultas.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import static org.fusesource.jansi.Ansi.Color.*;
import static org.fusesource.jansi.Ansi.ansi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Clase principal que ejecuta cada una de las consultas.
 *
 * @author Jose Luis Obiang Ela Nanguang
 * @version 1.0
 */

public class Main {

    private static List<String> opcionesList;
    private static int opcion = 0;
    private static Scanner in = new Scanner(System.in);
    private static String inputDir = "", outputDir = "";

    public static void main(String[] args) throws Exception {

        System.out.println(ansi().fg(GREEN).a("ooo        ooooo                              ooooooooo.                   .o8                                  \n" +
                "`88.       .888'                              `888   `Y88.                \"888                                  \n" +
                " 888b     d'888   .oooo.   oo.ooooo.           888   .d88'  .ooooo.   .oooo888  oooo  oooo   .ooooo.   .ooooo.  \n" +
                " 8 Y88. .P  888  `P  )88b   888' `88b          888ooo88P'  d88' `88b d88' `888  `888  `888  d88' `\"Y8 d88' `88b \n" +
                " 8  `888'   888   .oP\"888   888   888 8888888  888`88b.    888ooo888 888   888   888   888  888       888ooo888 \n" +
                " 8    Y     888  d8(  888   888   888          888  `88b.  888    .o 888   888   888   888  888   .o8 888    .o \n" +
                "o8o        o888o `Y888\"\"8o  888bod8P'         o888o  o888o `Y8bod8P' `Y8bod88P\"  `V88V\"V8P' `Y8bod8P' `Y8bod8P' \n" +
                "                            888                                                                                 \n" +
                "                           o888o                                                                                \n" +
                "                                                                                                                ").reset());

        System.out.println("\t\t\t╔══════════════════════════════════════════════════════════╗\n" +
                "\t\t\t║ 👨🏾‍🎓 Alumno: Jose Luis Obiang Ela Nanguang            ║\n" +
                "\t\t\t║ 📓 Profesor: Francisco Chávez de la O                    ║\n" +
                "\t\t\t║ 📘 Asignatura: Sistema de Información (Sinf)             ║\n" +
                "\t\t\t║ 📅 Fecha de entrega: 15-01-2024 23:55                    ║\n" +
                "\t\t\t╚══════════════════════════════════════════════════════════╝\n");

        System.out.println("======================================================================================================");
        ;
        if (args.length != 2) {
            System.err.println(ansi().fg(RED).a("Uso: VentasAnalysis <input path> <output path>").reset());
            System.exit(-1);
        }

        try {

            inputDir = args[0];
            outputDir = args[1];

            executeQueries();

        } catch (IOException ex) {
            System.err.println(ansi().fg(RED).a("Error en la ejecución del trabajo de MapReduce " + ex.getMessage()).reset());
        }
    }

    public static void executeQueries() throws IOException {

        opcionesList = getConsultas();

        while (true) {
            // Imprimir las opciones
            System.out.println(ansi().fg(BLUE).a("╔═══════════════════════════════════════════════════════════════════════════════════════════════════════════════╗\n" +
                    "\t\t\t\t\tMENÚ DE CONSULTAS: \t\t\t\n" +
                    "╠═══════════════════════════════════════════════════════════════════════════════════════════════════════════════╣\n").reset());

            showOptions(opcionesList);

            boolean opcionValida = false;

            while (!opcionValida) {
                System.out.print(ansi().fg(BLUE).a("==>Seleccione una opción: ").reset());
                // Verificar si la entrada es un número
                if (in.hasNextInt()) {
                    opcion = in.nextInt();
                    opcionValida = true;
                } else {
                    // La entrada no es un número
                    System.out.println(ansi().fg(RED).a("La opcion introducida no es un número.\n").reset());
                    in.next();
                }
            }

            in.nextLine(); // Consumir la nueva línea después de leer un entero

            try {
                Configuration conf = new Configuration();
                Job job = Job.getInstance(conf, "Ventas Analysis");
                job.setJarByClass(Main.class);

                switch (opcion) {
                    case 1:
                        query1(job);
                        break;
                    case 2:
                        query2(job);
                        break;
                    case 3:
                        query3(job);
                        break;
                    case 4:
                        query4(job);
                        break;
                    case 5:
                        query5(job);
                        break;
                    case 6:
                        salir();
                        break;
                    default:
                        System.out.println("Opción no válida. Por favor, seleccione una opción válida.");
                        break;
                }

                boolean jobCompletedSuccessfully = job.waitForCompletion(true);
                if (!jobCompletedSuccessfully) {
                    throw new IOException("Error en la ejecución del trabajo de MapReduce");
                }

            } catch (IOException | InterruptedException | ClassNotFoundException ex) {
                System.err.println("Error en la ejecución del trabajo de MapReduce " + ex.getMessage());
            }
        }


    }

    private static List<String> getConsultas() {

        List<String> opcionesList = new ArrayList<>();
        opcionesList.add("¿Cuánto se ganó en 2019?");
        opcionesList.add("¿Cuál fue el mejor mes para las ventas? ¿Cuánto se ganó ese mes?");
        opcionesList.add("¿Qué ciudad tuvo el mayor número de ventas?");
        opcionesList.add("¿A qué hora debemos mostrar publicidad para maximizar la probabilidad de que el cliente compre el producto?");
        opcionesList.add("¿Qué producto vendió más? ¿Por qué crees que vendió más?");
        opcionesList.add("Salir");

        return opcionesList;
    }

    private static void query5(Job job) throws IOException {
        // Configura las clases de Mapper, Reducer, y Combiner
        job = confMapperAndReduce(job, ProductoMasVendidoMapper.class, ProductoMasVendidoReducer.class);

        // Configura los tipos de clave y valor de salida del Mapper
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        // Configura los tipos de clave y valor de salida del Reducer
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        // Rutas de archivos de entrada y salida
        job = configFormatFileAndRouteInputOutput(job, 5);

    }

    private static void query4(Job job) throws IOException {
        // Configura las clases de Mapper, Reducer, y Combiner
        job = confMapperAndReduce(job, HoraPublicidadMapper.class, HoraPublicidadReducer.class);

        // Configura los tipos de clave y valor de salida del Mapper
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        // Configura los tipos de clave y valor de salida del Reducer
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        // Rutas de archivos de entrada y salida
        job = configFormatFileAndRouteInputOutput(job, 4);


    }

    private static void query3(Job job) throws IOException {
        // Configura las clases de Mapper, Reducer, y Combiner
        job = confMapperAndReduce(job, CiudadVentasMapper.class, CiudadVentasReducer.class);

        // Configura los tipos de clave y valor de salida del Mapper
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        // Configura los tipos de clave y valor de salida del Reducer
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        // Rutas de archivos de entrada y salida
        job = configFormatFileAndRouteInputOutput(job, 3);

    }

    private static void query2(Job job) throws IOException {
        // Configura las clases de Mapper, Reducer, y Combiner
        job = confMapperAndReduce(job, VentasPorMesMapper.class, MejorMesReducer.class);

        // Configura los tipos de clave y valor de salida del Mapper
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(DoubleWritable.class);

        // Configura los tipos de clave y valor de salida del Reducer
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        // Rutas de archivos de entrada y salida
        job = configFormatFileAndRouteInputOutput(job, 2);

    }

    private static void query1(Job job) throws IOException {
        // Configura las clases de Mapper, Reducer. Combiner no necesario por el tipo Text del valor Reducer
        job = confMapperAndReduce(job, Ingresos2019Mapper.class, Ingresos2019Reducer.class);

        // Tipos de clave y valor de salida del Mapper debe ser Text y DoubleWritable
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(DoubleWritable.class);

        // Tipos de clave y valor de salida del Reducer debe ser Text y Text
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        // Rutas de archivos de entrada y salida
        job = configFormatFileAndRouteInputOutput(job, 1);


    }

    private static Job configFormatFileAndRouteInputOutput(Job job, int query) throws IOException {
        // Configura los caminos de los archivos de entrada y salida
        FileInputFormat.addInputPath(job, new Path(inputDir));

        if (outputDir.contains("_Jloeln_2023")) {
            System.out.println(ansi().fg(RED).a("La ruta " + outputDir + " ya existe").reset());
            outputDir = outputDir.replaceFirst("\\d", query +"");
            System.out.println(ansi().fg(GREEN).a("\nCreando nueva ruta de salida: " + outputDir + "...").reset());

        } else {
            outputDir = outputDir + query + "_Jloeln_2023";
            System.out.println(ansi().fg(GREEN).a("Creando ruta de salida " + outputDir + "...").reset());

        }

        FileOutputFormat.setOutputPath(job, new Path(outputDir));

        return job;
    }

    private static Job confMapperAndReduce(Job job, Class<? extends org.apache.hadoop.mapreduce.Mapper> mapperClass, Class<? extends org.apache.hadoop.mapreduce.Reducer> reducerClass) {
        job.setMapperClass(mapperClass);
        job.setReducerClass(reducerClass);

        return job;
    }

    public static void showOptions(List<String> opciones) {
        for (int i = 0; i < opciones.size(); i++) {
            System.out.println(ansi().fg(CYAN).a("==>" + (i + 1) + ". " + opcionesList.get(i)).reset());
        }
        System.out.println("╚═══════════════════════════════════════════════════════════════════════════════════════════════════════════════╝\n");
    }


    public static void salir() {
        System.out.println("  ___ ___ _  _   ___  ___   _      _     ___ ___    __ ___ _____ ___ ___   _   \n" +
                " | __|_ _| \\| | |   \\| __| | |    /_\\   | _ \\ _ \\  /_// __|_   _|_ _/ __| /_\\  \n" +
                " | _| | || .` | | |) | _|  | |__ / _ \\  |  _/   / /--\\ (__  | |  | | (__ / _ \\ \n" +
                " |_| |___|_|\\_| |___/|___| |____/_/ \\_\\ |_| |_|_\\/_/\\_\\___| |_| |___\\___/_/ \\_\\\n" +
                "                                                                               ");
        System.exit(0);
    }
}
