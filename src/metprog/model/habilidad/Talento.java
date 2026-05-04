package metprog.model.habilidad;

/**
 * Habilidad especial de los Cazadores.
 * No añade propiedades adicionales más allá de las de HabilidadEspecial.
 */
public class Talento extends HabilidadEspecial {

    private static final long serialVersionUID = 1L;

    public Talento(String nombre, int valorAtaque, int valorDefensa) {
        super(nombre, valorAtaque, valorDefensa);
    }

    @Override
    public String toString() {
        return "Talento[" + getNombre() + ", ATK=" + getValorAtaque() + ", DEF=" + getValorDefensa() + "]";
    }
}