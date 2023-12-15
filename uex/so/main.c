#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <sys/ipc.h>
#include <sys/shm.h>
#include <fcntl.h>
#include <signal.h>
#include <time.h>
#include <stdarg.h>
#include <stdbool.h>
#include <dirent.h>

#include "getPrimesTotal.h"

// Declarar constantes propias del programa
#define FILAS 15 // Número de filas de la matriz
#define COLUMNAS 5 // Número de columnas de la matriz
#define MAXVALUE 30001 // Valor máximo de los elementos de la matriz
#define NUMPROCESOSNIVEL2 3
#define NUMPROCESOSNIVEL3 5
int tuberia[NUMPROCESOSNIVEL2];  // Tuberías para comunicación con procesos de nivel 2

// Constantes para colores ANSI
#define ROJO "\x1b[31m"
#define VERDE "\x1b[32m"
#define AMARILLO "\x1b[33m"
#define AZUL "\x1b[34m"
#define VIOLETA "\x1b[35m"
#define RESET "\x1b[0m"

// Emojis
#define EMOJI_RESULTADOS "📊"

// Rutas de los archivos
#define NIVEL1_FILE "./files/nivel1/N1_%d.primos"
#define NIVEL2_FILE "./files/nivel2/N2_%d.primos"
#define NIVEL3_FILE "./files/nivel3/N3_%d.primos"

// Declarar funciones
void eliminarArchivosEnCarpeta(const char *carpeta);

void manejador_senial(int senial);

void crearMatrizAleatoria(int matriz[FILAS][COLUMNAS]);

void imprimirMatriz(int matriz[FILAS][COLUMNAS]);

void asignarTareas(int inicio, int fin, int procesos[], int matriz[FILAS][COLUMNAS], pid_t idNivel2, int tuberia);

void procesarFila(int fila, int resultados[], int matriz[FILAS][COLUMNAS], pid_t idNivel2);

void cribaDeEratostenes(bool esPrimo[], int limite);

void mostrarResultados(int resultados[], int total);

void escribirEnArchivo(char *nombreArchivo, char *formato, ...);

/**
 * @Description Clase que muestra la cantidad de números primos presentes
 * en una matriz de 15 filas por 1000 columnas, donde cada número es elegido
 * aleatoriamente entre 0 y 30000. La tarea se lleva a cabo * mediante la creación
 * de procesos jerárquicos en tres niveles, cada uno encargado de analizar una parte
 * específica de la matriz y devolver los resultados a su proceso padre.
 * @author Jose Luis Obiang Ela Nanguang
 * @brief Funcion principal del programa
 * @date 2021-01-15
 * @version 1.0
 * @return 0
 */
int main() {

    // Eliminar archivos existentes en la carpeta para poder crear nuevos archivos
    eliminarArchivosEnCarpeta("./files/nivel1");
    eliminarArchivosEnCarpeta("./files/nivel2");
    eliminarArchivosEnCarpeta("./files/nivel3");

    // Establece el manejador_senial como el manejador de señal para la señal SIGINT (Interrupción desde el teclado)
    signal(SIGINT, manejador_senial);

    // Crear la tubería
    if (pipe(tuberia) == -1) {
        perror("Error al crear la tubería");
        exit(1);
    }

    int matriz[FILAS][COLUMNAS]; // Matriz de enteros
    crearMatrizAleatoria(matriz); // Crea una matriz aleatoria

    /* Llamar a la función getPrimesTotal de la clase getPrimesTotal para obtener el total de primos
    * y verificar que coincide con el valor esperado del total obtenido por los procesos hijos.
    */
    struct getPrimesTotal miComprobador; // Instancia de la clase getPrimesTotal
    // Inicializar el comprobador
    _construct_getPrimesTotal(&miComprobador, COLUMNAS); // Inicializar clase getPrimesTotal
    // Contar primos en la matriz
    contarPrimosEnMatriz(&miComprobador, matriz, FILAS);
    // Obtener el total de primos
    int totalPrimos = getTotalPrimos(&miComprobador);

    // Imprimir la matriz generada aleatoriamente
    imprimirMatriz(matriz);

    // Crear memoria compartida para almacenar resultados
    key_t clave = ftok("/tmp", 'A');
    int shmid = shmget(clave, sizeof(int) * FILAS, IPC_CREAT | 0666);
    int *resultados = (int *) shmat(shmid, NULL, 0);

    // Variables para procesos y estado
    pid_t pid[3]; // Array de procesos, almacena el pid de cada proceso de nivel 2
    int i, estado; // estado-> estado de finalización de cada proceso de nivel 2 (0: finalizado, 1: no finalizado)

    pid_t idProcesoPadre = getpid();  // Almacena el ID del proceso principal

    printf("%s🚀 ==> Proceso principal (%d): Comienzo%s\n", AMARILLO, idProcesoPadre, RESET);

    // Crear procesos de nivel 2
    for (i = 0; i < NUMPROCESOSNIVEL2; i++) {

        pid[i] = fork(); // Crea un nuevo proceso hijo.->Retorna 0 o -1

        if (pid[i] == 0) { // 0->proceso hijo creado correctamente,

            printf("%s🔄 ==> Proceso hijo de nivel 2 (%d): Inicio de ejecución\n", VIOLETA, getpid());
            // Distribuir filas a cada proceso de nivel 2
            int inicio, fin; // Determinan el rango de tareas a asignar a cada proceso de nivel 2
            if (i == 0) { // Proceso de nivel 2-1
                inicio = 0;
                fin = 4;
            } else if (i == 1) { // Proceso de nivel 2-2
                inicio = 5;
                fin = 9;
            } else { // Proceso de nivel 2-3
                inicio = 10;
                fin = 14;
            }

            asignarTareas(inicio, fin, resultados, matriz, getpid(), tuberia[1]);
            exit(0);
        } else if (pid[i] == -1) { // -1->Error al crear el proceso hijo
            perror("Error al crear el proceso hijo");
            exit(1);
        }
    }

    // Esperar a que terminen los procesos de nivel 2
    for (i = 0; i < NUMPROCESOSNIVEL2; i++) {
        waitpid(pid[i], &estado, 0);
    }

    // Calcular y mostrar resultados
    int total = 0;
    for (i = 0; i < FILAS; i++) {
        total += resultados[i];
    }

    mostrarResultados(resultados, total); // Mostrar total de primos

    // Comprobar que el total de primos coincide con el valor esperado
    if (total == totalPrimos) {
        printf("%s==>Los totales de números primos coinciden->%d = %d\n", VERDE, total, totalPrimos);
    } else {
        printf("%s==>Los totales de números primos NO coinciden>%d = %d\n", ROJO, total, totalPrimos);
    }

    // Almacenar resultados del proceso de nivel 1 en nuevos ficheros en la carpeta files/nivel1
    char nombreArchivoN1[50];
    sprintf(nombreArchivoN1, NIVEL1_FILE, idProcesoPadre);
    escribirEnArchivo(nombreArchivoN1, "Comienzo\n");
    escribirEnArchivo(nombreArchivoN1, "ID del proceso padre: ");
    escribirEnArchivo(nombreArchivoN1, "%d\n", getpid());
    escribirEnArchivo(nombreArchivoN1, "ID de cada uno de los procesos hijos que crea: ");

    for (int j = 0; j < NUMPROCESOSNIVEL2; j++) {
        escribirEnArchivo(nombreArchivoN1, "%d", pid[j]);
        if (j < 2) {
            escribirEnArchivo(nombreArchivoN1, ",");
        }
    }
    escribirEnArchivo(nombreArchivoN1, "\n");
    escribirEnArchivo(nombreArchivoN1, "Total de números primos: ");
    escribirEnArchivo(nombreArchivoN1, "%d\n\n", total);


    // Liberar memoria compartida
    shmdt(resultados);
    shmctl(shmid, IPC_RMID, 0);

    printf("%s==>Proceso principal (%d): Finalización\n", AMARILLO, getpid());
    printf("\t%s==> Fin del Programa 🏁%s\n", AMARILLO, RESET);

    return 0;
}

/**
 * Manejador de señales
 * @param senial Variable de tipo entero que contiene el código de la señal recibida
 */
void manejador_senial(int senial) {
    printf("%s==>Señal recibida por el proceso principal (%d): %d\n", AMARILLO, getpid(), senial);
    exit(0);
}

/**
 * Genera una matriz aleatoria
 * @param matriz Variable de tipo entero que contiene una matriz
 */
void crearMatrizAleatoria(int matriz[FILAS][COLUMNAS]) {
    srand(time(NULL));

    printf("\t%s==> Creando Matriz Aleatoria%s\n", AMARILLO, RESET);
    for (int i = 0; i < FILAS; i++) {
        for (int j = 0; j < COLUMNAS; j++) {
            matriz[i][j] = rand() % MAXVALUE;
        }
    }
    printf("\t%s==> Matriz Aleatoria creada ✅%s\n", AMARILLO, RESET);
}

/**
 * Asigna tareas a los procesos hijos de nivel 3
 * @param inicio Variable de tipo entero que contiene el inicio de la fila a procesar
 * @param fin Variable de tipo entero que contiene el final de la fila a procesar
 * @param procesos Array de tipo entero que contiene el ID de cada uno de los procesos hijos
 * @param matriz Matriz de tipo entero que contiene la matriz a procesar
 * @param idNivel2 ID del proceso padre
 */
void asignarTareas(int inicio, int fin, int procesos[], int matriz[FILAS][COLUMNAS], pid_t idNivel2, int tuberia) {
    int primosEncontrados[FILAS * COLUMNAS];
    int contadorPrimos = 0;

    for (int fila = inicio; fila <= fin; fila++) {
        pid_t pidN3 = fork();

        if (pidN3 == 0) {
            // Código de los procesos hijos de nivel 3
            close(tuberia); // Cerrar el extremo de lectura de la tubería en el proceso hijo

            // Código de los procesos hijos de nivel 3
            printf("%s🔍 ==> Proceso hijo de nivel 3 (%d): Inicio de ejecución%s\n", AZUL, getpid(), RESET);
            procesarFila(fila, procesos, matriz, idNivel2);

            exit(0);
        } else if (pidN3 == -1) {
            perror("Error al crear el proceso hijo de nivel 3");
            exit(1);
        } else {
            close(tuberia);
        }

    }

    // Esperar a que terminen los procesos de nivel 3
    for (int i = 0; i < NUMPROCESOSNIVEL3; i++) {
        wait(NULL);
    }
}

/**
 * Marca los números no primos en el array esPrimo[] hasta el límite especificado
 * @param esPrimo Array de tipo booleano que determina si un número es primo o no
 * <ul>
 * <li>Si esPrimo[i] = true, entonces el número i es primo</li>
 * <li>Si esPrimo[i] = false, entonces el número i no es primo</li>
 * <ul>
 * @param limite Variable de tipo entero que determina el límite superior del rango a procesar
 */
void cribaDeEratostenes(bool esPrimo[], int limite) {
    //  itera sobre todos los números desde 2 hasta la raíz cuadrada de MAXVALUE->Optimiza el rendimiento
    for (int p = 2; p * p < MAXVALUE; p++) {
        if (esPrimo[p]) { // Si el número p es marcado como primo
            for (int i = p * p; i <= limite; i += p)
                esPrimo[i] = false; // Marca el número p como no primo
        }
    }
}

/**
 * Procesa cada una de las filas de la matriz
 * @param fila Variable de tipo entero que contiene la fila a procesar
 * @param resultados Array de tipo entero que contiene el total de primos encontrados
 * @param matriz Matriz de tipo entero que contiene la matriz a procesar
 * @param idNivel2 ID del proceso padre(nivel 2) del proceso hijo(nivel 3)
 */
void procesarFila(int fila, int resultados[], int matriz[FILAS][COLUMNAS], pid_t idNivel2) {

    bool esPrimo[MAXVALUE];

    // Inicializar el array 'esPrimo' asumiendo que todos los números son primos
    for (int i = 0; i <= MAXVALUE; i++)
        esPrimo[i] = true;

    // Utilizar la criba de Eratóstenes para marcar los números no primos
    cribaDeEratostenes(esPrimo, MAXVALUE);

    // Contar números primos en la fila
    int contador = 0;
    for (int j = 0; j < COLUMNAS; j++) {
        if (esPrimo[matriz[fila][j]] && matriz[fila][j] >= 2) {
            // Imprimir cada número primo encontrado en la fila
            printf("==> Número primo: %s%d%s %s\n", VERDE, matriz[fila][j], EMOJI_RESULTADOS, RESET);
            contador++;
        }
    }

    // Mostrar y almacenar resultados->total parcial de primos
    printf("%s🔍 ==> Proceso hijo de nivel 3 (%d): Encontrados %d primos en la fila %d (Creado por proceso de nivel 2: %d)%s\n",
           AZUL, getpid(), contador, fila, idNivel2, RESET);
    resultados[fila] = contador;

    char nombreArchivoN3[50];

    for (int j = 0; j < COLUMNAS; j++) {
        if (esPrimo[matriz[fila][j]]) {
            sprintf(nombreArchivoN3, NIVEL3_FILE, getpid());
            // Almacenar resultados del proceso de nivel 3 en nuevos ficheros en la carpeta files/nivel3
            escribirEnArchivo(nombreArchivoN3, "==>Inicio de ejecución del proceso\n");
            escribirEnArchivo(nombreArchivoN3, "Proceso Padre(%d)->Proceso hijo de nivel 3 creado: %d\n", idNivel2,
                              getpid());
            escribirEnArchivo(nombreArchivoN3, "3: id_proceso:%d num_primo:%d\n", getpid(), matriz[fila][j]);
        }
    }

    // Almacenar resultados del proceso de nivel 2 en nuevos ficheros en la carpeta files/nivel2
    char nombreArchivoN2[50];
    sprintf(nombreArchivoN2, NIVEL2_FILE, idNivel2);
    escribirEnArchivo(nombreArchivoN2, "Proceso de nivel 2(%d)->Total enviado por sus hijos: %d\n", idNivel2, contador);

}

/**
 * imprime y muestra en consola tanto los totales parciales de primos
 * encontrados por los procesos hijos como el total total de primos
 * @param resultados Array de tipo entero que contiene el total parcial de primos encontrados por los procesos hijos
 * @param total Variable de tipo entero que contiene el total de números primos encontrados
 */
void mostrarResultados(int resultados[], int total) {
    printf("\n\t%s--------------📊 Resultados --------------%s\n", AMARILLO, RESET);
    for (int i = 0; i < FILAS; i++) {
        if (resultados[i] > 0) {
            // Imprimir información de la fila y la cantidad de números primos encontrados por los procesos hijos
            printf("\t%s| Fila %d: %s%d%s números primos %s%s%s|%s\n", AMARILLO, i, AMARILLO, resultados[i], RESET,
                   VERDE, EMOJI_RESULTADOS, RESET, RESET);
        } else {
            printf("\t%s| Fila %d: %s%d%s números primos %s%s|%s\n", AMARILLO, i, AMARILLO, resultados[i], RESET, VERDE,
                   RESET, RESET);
        }

    }

    // Imprimir el total de números primos encontrados
    printf("\t%s| Total de números primos encontrados: %s%d", AMARILLO, AZUL, total);
    for (int i = 0; i < total; i++) {
        printf(" %s%s", AMARILLO, EMOJI_RESULTADOS);
    }
    printf("\n\t%s|--------------------------------------------|%s\n\n", AMARILLO, RESET);
}

// Función que verifica si un archivo existe
int archivoExiste(const char *nombreArchivo) {
    return access(nombreArchivo, F_OK) == 0;
}

/**
 * Almacena información en un fichero
 * @param nombreArchivo Variable de tipo char que contiene el nombre del fichero
 * @param formato Variable de tipo char que contiene la información a almacenar en el fichero
 * @param ... Variable de tipo char que contiene resto de la información a almacenar en el fichero
 */
void escribirEnArchivo(char *nombreArchivo, char *formato, ...) {
    FILE *archivo = fopen(nombreArchivo, "a");
    if (archivo == NULL) {
        perror("==>Error al abrir el archivo");
        exit(1);
    }

    va_list args; // Crear lista para almacenar los argumentos
    va_start(args, formato); // Inicializar lista 'args' con los argumentos
    vfprintf(archivo, formato, args); // Escribir en archivo usando la lista de argumentos
    va_end(args); // Limpiar la lista de argumentos.

    fclose(archivo); // Cerrar el archivo

}

/**
 * Imprime la matriz de enteros
 * @param matriz Matriz de tipo entero que contiene la matriz a imprimir
 */
void imprimirMatriz(int matriz[FILAS][COLUMNAS]) {
    // Imprimir la matriz con colores y emojis
    printf("\n\t%s🔢 Matriz generada aleatoriamente:%s\n", AZUL, RESET);
    printf("\t%s|---------------Matriz-------------|%s\n", AMARILLO, RESET);
    printf("\t%s|  %sFila%s  |  %sColumna%s  |  %sValor%s  |%s\n", AMARILLO, AMARILLO, RESET, AMARILLO, RESET, AMARILLO,
           RESET, RESET);
    for (int i = 0; i < FILAS; i++) {
        for (int j = 0; j < COLUMNAS; j++) {
            printf("\t%s|    %s%d%s    |    %s%d%s     |    %s%d%s    |%s\n", AZUL, AZUL, i, RESET, AZUL, j, RESET,
                   VERDE, matriz[i][j], RESET, RESET);
        }
        printf("\t%s|----------------------------------|%s\n", AMARILLO, RESET);
    }
    printf("\n");
}

/**
 * Elimina todos los ficheros en una carpeta
 * @param carpeta Constante que contiene el nombre de la carpeta
 */
void eliminarArchivosEnCarpeta(const char *carpeta) {
    DIR *dir;
    struct dirent *entrada;

    // Abrir el directorio
    dir = opendir(carpeta);
    if (dir == NULL) {
        perror("==>No se pudo abrir el directorio");
    }

    // Iterar sobre los archivos en el directorio
    while ((entrada = readdir(dir)) != NULL) {
        // Construir la ruta completa del archivo
        char rutaArchivo[PATH_MAX];
        snprintf(rutaArchivo, sizeof(rutaArchivo), "%s/%s", carpeta, entrada->d_name);

        // Verificar si es un archivo y no un directorio
        if (entrada->d_type == DT_REG) {
            // Eliminar el archivo
            if (remove(rutaArchivo) != 0) {
                perror("==>Error al eliminar el archivo");
            } else {
                printf("%s==>Archivo eliminado: %s\n", VERDE, rutaArchivo);
            }
        }
    }

    // Cerrar el directorio
    closedir(dir);
}
