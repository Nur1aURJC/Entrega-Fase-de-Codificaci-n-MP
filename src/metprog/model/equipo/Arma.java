package metprog.model.equipo;

/**
 * Representa un Arma. Puede ser de 1 o 2 manos.
 * Un personaje puede tener activas 2 armas de 1 mano,
 * o solo 1 arma de 2 manos.
 */
public class Arma extends Equipo {

    private static final long serialVersionUID = 1L;

    public enum TipoManos {
        UNA_MANO, DOS_MANOS
    }

    private TipoManos tipoManos;

    public Arma(String nombre, int modAtaque, int modDefensa, TipoManos tipoManos) {
        super(nombre, modAtaque, modDefensa);
        this.tipoManos = tipoManos;
    }

    public TipoManos getTipoManos() { return tipoManos; }
    public void setTipoManos(TipoManos tipoManos) { this.tipoManos = tipoManos; }

    public boolean esDosManost() { return tipoManos == TipoManos.DOS_MANOS; }

    @Override
    public String toString() {
        return "Arma[" + getNombre() + ", " + tipoManos + ", ATK+" + getModAtaque() + ", DEF+" + getModDefensa() + "]";
    }
}