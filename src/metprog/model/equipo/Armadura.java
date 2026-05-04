package metprog.model.equipo;

/**
 * Representa una Armadura.
 * Todas las armaduras añaden modificador de defensa.
 * Puede también añadir modificador de ataque.
 */
public class Armadura extends Equipo {

    private static final long serialVersionUID = 1L;

    public Armadura(String nombre, int modAtaque, int modDefensa) {
        super(nombre, modAtaque, modDefensa);
    }

    @Override
    public String toString() {
        return "Armadura[" + getNombre() + ", ATK+" + getModAtaque() + ", DEF+" + getModDefensa() + "]";
    }
}