package metprog.model.habilidad;

/**
 * Habilidad especial de los Licántropos.
 * Requiere un nivel mínimo de Rabia para poder usarse.
 */
public class Don extends HabilidadEspecial {

    private static final long serialVersionUID = 1L;

    private int rabiaMínima; // valor mínimo de rabia requerido

    public Don(String nombre, int valorAtaque, int valorDefensa, int rabiaMínima) {
        super(nombre, valorAtaque, valorDefensa);
        this.rabiaMínima = rabiaMínima;
    }

    public int getRabiaMinima() { return rabiaMínima; }
    public void setRabiaMinima(int rabiaMínima) { this.rabiaMínima = rabiaMínima; }

    @Override
    public String toString() {
        return "Don[" + getNombre() + ", ATK=" + getValorAtaque() + ", DEF=" + getValorDefensa()
                + ", rabiaMin=" + rabiaMínima + "]";
    }
}