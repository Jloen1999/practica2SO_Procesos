/**
 * Header que contiene funciones para determinar el total de
 * números primos sin el uso de procesos, el cual usamos en el
 * programa principal para comprobar que el total ha sido calculado
 * correctamente por los procesos.
 * @author Jose Luis Obiang Ela Nanguang
 * @brief Funcion principal del programa
 * @version 2.0
 * @return 0
 */

#ifndef COMPROBAR_TOTAL_PRIMOS_H
#define COMPROBAR_TOTAL_PRIMOS_H

#include <stdio.h>
#include <stdbool.h>
#include <stdbool.h>

#define COLUMNAS 5 // Asegúrate de que coincida con el valor en tu programa principal

// Estructura para la clase getPrimesTotal
struct getPrimesTotal {
    int totalPrimos;
    int columnas;
};

// Función para verificar si un número es primo
bool esPrimo(int numero) {
    if (numero < 2) {
        return false;
    }

    for (int i = 2; i * i <= numero; i++) {
        if (numero % i == 0) {
            return false;
        }
    }

    return true;
}

// Función para contar números primos en una matriz y actualizar el total en la instancia de la clase
void contarPrimosEnMatriz(struct getPrimesTotal *comprobador, int matriz[][COLUMNAS], int filas) {
    int contadorPrimos = 0;

    for (int i = 0; i < filas; i++) {
        for (int j = 0; j < comprobador->columnas; j++) {
            if (esPrimo(matriz[i][j])) {
                contadorPrimos++;
            }
        }
    }

    comprobador->totalPrimos = contadorPrimos;
}

// Función para obtener el total de primos desde un objeto getPrimesTotal
int getTotalPrimos(struct getPrimesTotal *comprobador) {
    return comprobador->totalPrimos;
}

// Función para inicializar un objeto getPrimesTotal
void _construct_getPrimesTotal(struct getPrimesTotal *comprobador, int col) {
    comprobador->totalPrimos = 0;
    comprobador->columnas = col;
}


#endif // COMPROBAR_TOTAL_PRIMOS_H
