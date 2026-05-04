package metprog;

/**
 * Clase principal que arranca la aplicación.
 */
public class Main {

    public static void main(String[] args) {
        // Instanciamos el controlador principal del juego
        AplicacionJuego juego = new AplicacionJuego();

        juego.start();
    }
}