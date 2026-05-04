package metprog.model.modificador;

import java.io.Serializable;

/**
 * Representa una Fortaleza o Debilidad de un Personaje.
 * Valor entre 1 y 5.
 */
public class Modificador implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nombre;
    private int valor; // entre 1 y 5

    public Modificador(String nombre, int valor) {
        if (valor < 1 || valor > 5) {
            throw new IllegalArgumentException("El valor del modificador debe estar entre 1 y 5.");
        }
        this.nombre = nombre;
        this.valor = valor;
    }

    public String getNombre() { return nombre; }
    public int getValor() { return valor; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setValor(int valor) {
        if (valor < 1 || valor > 5) throw new IllegalArgumentException("Valor entre 1 y 5.");
        this.valor = valor;
    }

    @Override
    public String toString() {
        return nombre + " (valor=" + valor + ")";
    }
}