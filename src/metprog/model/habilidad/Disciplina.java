package metprog.model.habilidad;

/**
 * Habilidad especial de los Vampiros.
 * Tiene un coste en puntos de sangre (entre 1 y 3).
 */
public class Disciplina extends HabilidadEspecial {

    private static final long serialVersionUID = 1L;

    private int costeSangre; // entre 1 y 3

    public Disciplina(String nombre, int valorAtaque, int valorDefensa, int costeSangre) {
        super(nombre, valorAtaque, valorDefensa);
        if (costeSangre < 1 || costeSangre > 3) {
            throw new IllegalArgumentException("El coste en sangre debe estar entre 1 y 3.");
        }
        this.costeSangre = costeSangre;
    }

    public int getCosteSangre() { return costeSangre; }
    public void setCosteSangre(int costeSangre) {
        if (costeSangre < 1 || costeSangre > 3) throw new IllegalArgumentException("Coste sangre entre 1 y 3.");
        this.costeSangre = costeSangre;
    }

    @Override
    public String toString() {
        return "Disciplina[" + getNombre() + ", ATK=" + getValorAtaque() + ", DEF=" + getValorDefensa()
                + ", coste=" + costeSangre + "]";
    }
}