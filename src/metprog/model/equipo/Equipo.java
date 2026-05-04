package metprog.model.equipo;

import java.io.Serializable;

/**
 * Clase base para el equipo de un personaje (Arma o Armadura).
 * Cada pieza tiene nombre, modificador de ataque y modificador de defensa.
 */
public abstract class Equipo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nombre;
    private int modAtaque;  // entre 1 y 3
    private int modDefensa; // entre 1 y 3 (puede ser 0 si no aplica pero al menos 0)

    public Equipo(String nombre, int modAtaque, int modDefensa) {
        validarModificador(modAtaque, "ataque");
        // defensa puede ser 0 en armas que no la proveen, pero el enunciado dice 1-3 para los que sí
        this.nombre = nombre;
        this.modAtaque = modAtaque;
        this.modDefensa = modDefensa;
    }

    private void validarModificador(int valor, String tipo) {
        if (valor < 0 || valor > 3) {
            throw new IllegalArgumentException("Modificador de " + tipo + " debe estar entre 0 y 3.");
        }
    }

    public String getNombre() { return nombre; }
    public int getModAtaque()  { return modAtaque; }
    public int getModDefensa() { return modDefensa; }

    public void setNombre(String nombre)       { this.nombre = nombre; }
    public void setModAtaque(int modAtaque)    { this.modAtaque = modAtaque; }
    public void setModDefensa(int modDefensa)  { this.modDefensa = modDefensa; }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + nombre + ", ATK+" + modAtaque + ", DEF+" + modDefensa + "]";
    }
}