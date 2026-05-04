package metprog.model.esbirro;

import java.io.Serializable;

/**
 * Clase base abstracta para los esbirros de un personaje.
 * Salud entre 1 y 3.
 * Los esbirros absorben daño antes que el personaje principal.
 */
public abstract class Esbirro implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nombre;
    private int salud; // entre 1 y 3

    public Esbirro(String nombre, int salud) {
        if (salud < 1 || salud > 3) throw new IllegalArgumentException("Salud del esbirro entre 1 y 3.");
        this.nombre = nombre;
        this.salud = salud;
    }

    /**
     * Devuelve la salud total de este esbirro (y sus subesbirros si los tuviera).
     */
    public int getSaludTotal() {
        return salud;
    }

    public String getNombre() { return nombre; }
    public int getSalud()     { return salud; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setSalud(int salud) {
        if (salud < 1 || salud > 3) throw new IllegalArgumentException("Salud del esbirro entre 1 y 3.");
        this.salud = salud;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + nombre + ", salud=" + salud + "]";
    }
}