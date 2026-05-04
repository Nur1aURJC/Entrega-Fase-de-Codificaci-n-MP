package metprog.model.habilidad;

import java.io.Serializable;

/**
 * Clase base abstracta para todas las habilidades especiales.
 * Valores de ataque y defensa entre 1 y 3.
 */
public abstract class HabilidadEspecial implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nombre;
    private int valorAtaque;  // entre 1 y 3
    private int valorDefensa; // entre 1 y 3

    public HabilidadEspecial(String nombre, int valorAtaque, int valorDefensa) {
        validar(valorAtaque, "ataque");
        validar(valorDefensa, "defensa");
        this.nombre = nombre;
        this.valorAtaque = valorAtaque;
        this.valorDefensa = valorDefensa;
    }

    private void validar(int v, String tipo) {
        if (v < 1 || v > 3) throw new IllegalArgumentException("Valor de " + tipo + " de habilidad entre 1 y 3.");
    }

    public String getNombre()     { return nombre; }
    public int getValorAtaque()   { return valorAtaque; }
    public int getValorDefensa()  { return valorDefensa; }

    public void setNombre(String nombre)         { this.nombre = nombre; }
    public void setValorAtaque(int valorAtaque)  { validar(valorAtaque, "ataque");  this.valorAtaque = valorAtaque; }
    public void setValorDefensa(int valorDefensa){ validar(valorDefensa, "defensa"); this.valorDefensa = valorDefensa; }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + nombre + ", ATK=" + valorAtaque + ", DEF=" + valorDefensa + "]";
    }
}