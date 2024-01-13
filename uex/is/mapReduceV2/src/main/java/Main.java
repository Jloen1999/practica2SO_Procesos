import consultas.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

public class Main {

    private static List<String> opcionesList;
    private static int opcion = 0;
    private static boolean opcionValida = false;
    private static Scanner in = new Scanner(System.in);
    private static String inputDir = "", outputDir = "";

    public static void main(String[] args) throws Exception {

        System.out.println("ooo        ooooo                              ooooooooo.                   .o8                                  \n" +
                "`88.       .888'                              `888   `Y88.                \"888                                  \n" +
                " 888b     d'888   .oooo.   oo.ooooo.           888   .d88'  .ooooo.   .oooo888  oooo  oooo   .ooooo.   .ooooo.  \n" +
                " 8 Y88. .P  888  `P  )88b   888' `88b          888ooo88P'  d88' `88b d88' `888  `888  `888  d88' `\"Y8 d88' `88b \n" +
                " 8  `888'   888   .oP\"888   888   888 8888888  888`88b.    888ooo888 888   888   888   888  888       888ooo888 \n" +
                " 8    Y     888  d8(  888   888   888          888  `88b.  888    .o 888   888   888   888  888   .o8 888    .o \n" +
                "o8o        o888o `Y888\"\"8o  888bod8P'         o888o  o888o `Y8bod8P' `Y8bod88P\"  `V88V\"V8P' `Y8bod8P' `Y8bod8P' \n" +
                "                            888                                                                                 \n" +
                "                           o888o                                                                                \n" +
                "                                                                                                                ");

        System.out.println("\t\t\t╔══════════════════════════════════════════════════════════╗\n" +
                "\t\t\t║ 👨🏾‍🎓 Alumno: Jose Luis Obiang Ela Nanguang            ║\n" +
                "\t\t\t║ 📓 Profesor: Francisco Chávez de la O                    ║\n" +
                "\t\t\t║ 📘 Asignatura: Sistema de Información (Sinf)             ║\n" +
                "\t\t\t║ 📅 Fecha de entrega: 15-01-2024 23:55                    ║\n" +
                "\t\t\t╚══════════════════════════════════════════════════════════╝\n");

        System.out.println("======================================================================================================");
        ;
        if (args.length != 2) {
            System.err.println("Uso: VentasAnalysis <input path> <output path>");
            System.exit(-1);
        }

        try {

            inputDir = args[0];
            outputDir = args[1];

            executeQueries();

        } catch (IOException ex) {
            System.err.println("Error en la ejecución del trabajo de MapReduce " + ex.getMessage());
        }
    }

    public static void executeQueries() throws IOException {

        opcionesList = getConsultas();

        while (true) {
            // Imprimir las opciones
            System.out.println("╔═══════════════════════════════════════════════════════════════════════════════════════════════════════════════╗\n" +
                    "\t\t\t\t\tMENÚ DE CONSULTAS: \t\t\t\n" +
                            "╠═══════════════════════════════════════════════════════════════════════════════════════════════════════════════╣\n");

            showOptions(opcionesList);

            opcionValida = false;

            while (!opcionValida) {
                System.out.print("==>Seleccione una opción: ");
                // Verificar si la entrada es un número
                if (in.hasNextInt()) {
                    opcion = in.nextInt();
                    opcionValida = true;
                } else {
                    // La entrada no es un número
                    System.out.println("La opcion introducida no es un número.\n");
                    in.next();
                }
            }

            in.nextLine(); // Consumir la nueva línea después de leer un entero

            JobConf conf = new JobConf(Main.class); // Configura un nuevo trabajo de MapReduce
            conf.setJobName("Análisis de Ventas 2019"); // Asigna un nombre al trabajo

            switch (opcion) {
                case 1:
                    query1(conf);
                    break;
                case 2:
                    query2(conf);
                    break;
                case 3:
                    query3(conf);
                    break;
                case 4:
                    query4(conf);
                    break;
                case 5:
                    query5(conf);
                    break;
                case 6:
                    salir();
                    break;
                default:
                    System.out.println("Opción no válida. Por favor, seleccione una opción válida.");
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

    private static void query5(JobConf conf) {
        try {
            // Configura las clases de Mapper, Reducer, y Combiner
            conf = confMapperAndReduce(conf, ProductoMasVendidoMapper.class, ProductoMasVendidoReducer.class, 5);

            // Configura los formatos de entrada/salida y los caminos de los archivos de entrada y salida
            conf = configFormatFileAndRouteInputOutput(conf, 5);

            // Configura los tipos de clave y valor de salida del Mapper
            conf.setMapOutputKeyClass(Text.class);
            conf.setMapOutputValueClass(IntWritable.class);

            // Configura los tipos de clave y valor de salida del Reducer
            conf.setOutputKeyClass(Text.class);
            conf.setOutputValueClass(IntWritable.class);

            JobClient.runJob(conf); // Ejecuta el trabajo

        } catch (IOException ex) {
            System.err.println("Error en la ejecución del trabajo de MapReduce " + ex.getMessage());
        }
    }

    private static void query4(JobConf conf) {
        try {
            // Configura las clases de Mapper, Reducer, y Combiner
            conf = confMapperAndReduce(conf, HoraPublicidadMapper.class, HoraPublicidadReducer.class, 4);

            // Configura los formatos de entrada/salida y los caminos de los archivos de entrada y salida
            conf = configFormatFileAndRouteInputOutput(conf, 4);

            // Configura los tipos de clave y valor de salida del Mapper
            conf.setMapOutputKeyClass(Text.class);
            conf.setMapOutputValueClass(IntWritable.class);

            // Configura los tipos de clave y valor de salida del Reducer
            conf.setOutputKeyClass(Text.class);
            conf.setOutputValueClass(IntWritable.class);

            JobClient.runJob(conf); // Ejecuta el trabajo

        } catch (IOException ex) {
            System.err.println("Error en la ejecución del trabajo de MapReduce " + ex.getMessage());
        }
    }

    private static void query3(JobConf conf) {
        try {
            // Configura las clases de Mapper, Reducer, y Combiner
            conf = confMapperAndReduce(conf, CiudadVentasMapper.class, CiudadVentasReducer.class, 3);

            // Configura los formatos de entrada/salida y los caminos de los archivos de entrada y salida
            conf = configFormatFileAndRouteInputOutput(conf, 3);

            // Configura los tipos de clave y valor de salida del Mapper
            conf.setMapOutputKeyClass(Text.class);
            conf.setMapOutputValueClass(IntWritable.class);

            // Configura los tipos de clave y valor de salida del Reducer
            conf.setOutputKeyClass(Text.class);
            conf.setOutputValueClass(IntWritable.class);

            JobClient.runJob(conf); // Ejecuta el trabajo

        } catch (IOException ex) {
            System.err.println("Error en la ejecución del trabajo de MapReduce " + ex.getMessage());
        }
    }

    private static void query2(JobConf conf) {
        try {
            // Configura las clases de Mapper, Reducer, y Combiner
            conf = confMapperAndReduce(conf, VentasPorMesMapper.class, MejorMesReducer.class, 1);

            // Configura los formatos de entrada/salida y los caminos de los archivos de entrada y salida
            conf = configFormatFileAndRouteInputOutput(conf, 2);

            // Configura los tipos de clave y valor de salida del Mapper
            conf.setMapOutputKeyClass(Text.class);
            conf.setMapOutputValueClass(DoubleWritable.class);

            // Configura los tipos de clave y valor de salida del Reducer
            conf.setOutputKeyClass(Text.class);
            conf.setOutputValueClass(Text.class);

            JobClient.runJob(conf); // Ejecuta el trabajo

        } catch (IOException ex) {
            System.err.println("Error en la ejecución del trabajo de MapReduce " + ex.getMessage());
        }
    }

    private static void query1(JobConf conf) {
        try {
            // Configura las clases de Mapper, Reducer. Combiner no necesario por el tipo Text del valor Reducer
            conf = confMapperAndReduce(conf, Ingresos2019Mapper.class, Ingresos2019Reducer.class, 1);

            // Configura los formatos de entrada/salida y los caminos de los archivos de entrada y salida
            conf = configFormatFileAndRouteInputOutput(conf, 1);

            // Tipos de clave y valor de salida del Mapper debe ser Text y DoubleWritable
            conf.setMapOutputKeyClass(Text.class);
            conf.setMapOutputValueClass(DoubleWritable.class);

            // Tipos de clave y valor de salida del Reducer debe ser Text y Text
            conf.setOutputKeyClass(Text.class);
            conf.setOutputValueClass(Text.class);

            JobClient.runJob(conf); // Ejecuta el trabajo

        } catch (IOException ex) {
            System.err.println("Error en la ejecución del trabajo de MapReduce " + ex.getMessage());
        }

    }

    private static JobConf configFormatFileAndRouteInputOutput(JobConf conf, int query) throws IOException {
        // Configura los formatos de entrada/salida
        conf.setInputFormat(TextInputFormat.class);
        conf.setOutputFormat(TextOutputFormat.class);
        // Configura los caminos de los archivos de entrada y salida
        org.apache.hadoop.mapred.FileInputFormat.setInputPaths(conf, new Path(inputDir));


        FileSystem fs = FileSystem.get(conf);
        if(outputDir.contains("_Jloeln_2023")){
            System.out.println("La ruta " + outputDir + " ya existe\nCrear nueva ruta de salida: " + outputDir + "_" + query);
            outputDir = outputDir + "_" + query;
        }else{
            System.out.println("Creando ruta de salida ...");
            outputDir = outputDir + query + "_Jloeln_2023";
        }

        org.apache.hadoop.mapred.FileOutputFormat.setOutputPath(conf, new Path(outputDir));

        return conf;
    }

    private static JobConf confMapperAndReduce(JobConf conf, Class<? extends org.apache.hadoop.mapred.Mapper> mapperClass, Class<? extends org.apache.hadoop.mapred.Reducer> reducerClass, int query) {
        conf.setMapperClass(mapperClass);
        if(query != 1){
            conf.setCombinerClass(reducerClass);
        }
        conf.setReducerClass(reducerClass);

        return conf;
    }

    public static void showOptions(List<String> opciones) {
        for (int i = 0; i < opciones.size(); i++) {
            System.out.println("==>" + (i + 1) + ". " + opcionesList.get(i));
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
