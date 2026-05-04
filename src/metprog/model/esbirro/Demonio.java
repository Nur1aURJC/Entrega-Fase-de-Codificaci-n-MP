package metprog.model.esbirro;

import java.util.ArrayList;
import java.util.List;

/**
 * Esbirro de tipo Demonio.
 * Implementa el patrón Composite: puede contener otros esbirros de cualquier tipo,
 * incluidos otros demonios con sus propios esbirros (recursividad).
 * Tiene un Pacto con su amo (descripción textual).
 */
public class Demonio extends Esbirro {

    private static final long serialVersionUID = 1L;

    private String pacto;
    private List<Esbirro> subesbirros;

    public Demonio(String nombre, int salud, String pacto) {
        super(nombre, salud);
        this.pacto = pacto;
        this.subesbirros = new ArrayList<>();
    }

    /** Salud total = salud propia + salud total de todos los subesbirros (recursivo). */
    @Override
    public int getSaludTotal() {
        int total = getSalud();
        for (Esbirro e : subesbirros) {
            total += e.getSaludTotal();
        }
        return total;
    }

    public void addSubEsbirro(Esbirro e) {
        subesbirros.add(e);
    }

    public void removeSubEsbirro(Esbirro e) {
        subesbirros.remove(e);
    }

    public List<Esbirro> getSubesbirros() { return subesbirros; }
    public String getPacto()              { return pacto; }
    public void setPacto(String pacto)    { this.pacto = pacto; }

    @Override
    public String toString() {
        return "Demonio[" + getNombre() + ", salud=" + getSalud()
                + ", pacto='" + pacto + "', subesbirros=" + subesbirros.size() + "]";
    }
}