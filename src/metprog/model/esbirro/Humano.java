package metprog.model.esbirro;

/**
 * Esbirro de tipo Humano.
 * Tiene un nivel de lealtad: ALTA, NORMAL o BAJA.
 * Los Vampiros NO pueden tener esbirros humanos.
 */
public class Humano extends Esbirro {

    private static final long serialVersionUID = 1L;

    public enum Lealtad {
        ALTA, NORMAL, BAJA
    }

    private Lealtad lealtad;

    public Humano(String nombre, int salud, Lealtad lealtad) {
        super(nombre, salud);
        this.lealtad = lealtad;
    }

    public Lealtad getLealtad() { return lealtad; }
    public void setLealtad(Lealtad lealtad) { this.lealtad = lealtad; }

    @Override
    public String toString() {
        return "Humano[" + getNombre() + ", salud=" + getSalud() + ", lealtad=" + lealtad + "]";
    }
}