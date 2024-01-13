/* módulo definido en Slice. Contiene la definición de la interfaz ServicioUsuario, 
* que es un contrato que describe qué métodos están disponibles para ser llamados 
* de forma remota (en este caso, obtenerDatosUsuario).
*/

module Usuario {

    // Define una interfaz llamada 'ServicioUsuario'.
    interface ServicioUsuario {

        // Declara un método llamado 'obtenerDatosUsuario' que acepta un entero (userId) y devuelve una cadena (string).
        // Este método será implementado por una clase en el servidor y será invocable por los clientes a través de la red.
        string obtenerDatosUsuario(int userId);
        string obtenerTodosLosUsuarios(); // Permite obtener todos los usuarios que nos da la API placeholder
        string obtenerUsuariosPorNombre(string nombre); // Permite mostrar los datos de los usuarios dado un nombre.
    };
};
