package metprog.vista;

import java.util.Scanner;

/**
 * Vista de consola. Maneja toda la entrada/salida del sistema.
 */
public class ConsoleView {

    private final Scanner scanner;

    public ConsoleView() {
        this.scanner = new Scanner(System.in);
    }

    public void mostrarMensaje(String mensaje) {
        System.out.println(mensaje);
    }

    public void mostrarError(String mensaje) {
        System.out.println("[ERROR] " + mensaje);
    }

    public String leerPantalla(String prompt) {
        System.out.print(prompt + ": ");
        return scanner.nextLine().trim();
    }

    public int leerEntero(String prompt) {
        while (true) {
            try {
                System.out.print(prompt + ": ");
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                mostrarError("Introduce un número entero válido.");
            }
        }
    }

    public double leerDecimal(String prompt) {
        while (true) {
            try {
                System.out.print(prompt + ": ");
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                mostrarError("Introduce un número decimal válido.");
            }
        }
    }

    public boolean leerBooleano(String prompt) {
        while (true) {
            String r = leerPantalla(prompt + " (s/n)").toLowerCase();
            if (r.equals("s")) return true;
            if (r.equals("n")) return false;
            mostrarError("Introduce 's' o 'n'.");
        }
    }

    public void mostrarSeparador() {
        mostrarMensaje("─".repeat(60));
    }

    public void mostrarTitulo(String titulo) {
        mostrarSeparador();
        mostrarMensaje("  " + titulo.toUpperCase());
        mostrarSeparador();
    }

    public void limpiarPantalla() {
        for (int i = 0; i < 3; i++) mostrarMensaje("");
    }

    public void cerrar() {
        scanner.close();
    }
}