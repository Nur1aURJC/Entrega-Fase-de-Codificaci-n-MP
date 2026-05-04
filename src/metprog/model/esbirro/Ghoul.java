package metprog.model.esbirro;

/**
 * Esbirro de tipo Ghoul.
 * Tiene un nivel de dependencia con su amo (entre 1 y 5).
 */
public class Ghoul extends Esbirro {

    private static final long serialVersionUID = 1L;

    private int dependencia; // entre 1 y 5

    public Ghoul(String nombre, int salud, int dependencia) {
        super(nombre, salud);
        if (dependencia < 1 || dependencia > 5) {
            throw new IllegalArgumentException("Dependencia del Ghoul debe estar entre 1 y 5.");
        }
        this.dependencia = dependencia;
    }

    public int getDependencia() { return dependencia; }
    public void setDependencia(int dependencia) {
        if (dependencia < 1 || dependencia > 5) throw new IllegalArgumentException("Dependencia entre 1 y 5.");
        this.dependencia = dependencia;
    }

    @Override
    public String toString() {
        return "Ghoul[" + getNombre() + ", salud=" + getSalud() + ", dependencia=" + dependencia + "]";
    }
}