package metprog.model.personaje;

import metprog.combate.EstrategiaCombate;
import metprog.model.equipo.Arma;
import metprog.model.equipo.Armadura;
import metprog.model.esbirro.Esbirro;
import metprog.model.habilidad.HabilidadEspecial;
import metprog.model.modificador.Modificador;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase principal que representa un Personaje del juego.
 * Salud: 0-5, Poder: 1-5, Oro: >= 0.
 * Delega el cálculo de combate a EstrategiaCombate (patrón Strategy).
 */
public abstract class Personaje implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nombre;
    private int salud;       // 0-5
    private int poder;       // 1-5
    private int oro;         // >= 0

    private HabilidadEspecial habilidadEspecial;

    // Inventario de equipo
    private List<Arma> armas;
    private List<Armadura> armaduras;

    // Equipo activo
    private List<Arma> armasActivas;   // máx 2 de 1 mano, o 1 de 2 manos
    private Armadura armaduraActiva;

    // Esbirros
    private List<Esbirro> esbirros;

    // Modificadores
    private List<Modificador> debilidades;
    private List<Modificador> fortalezas;

    // Estrategia de combate (inyectada por subclase)
    protected transient EstrategiaCombate estrategiaCombate;

    public Personaje(String nombre, int poder) {
        this.nombre = nombre;
        this.salud = 5;
        setPoder(poder);
        this.oro = 500;
        this.armas = new ArrayList<>();
        this.armaduras = new ArrayList<>();
        this.armasActivas = new ArrayList<>();
        this.esbirros = new ArrayList<>();
        this.debilidades = new ArrayList<>();
        this.fortalezas = new ArrayList<>();
    }

    // ─── Salud ─────────────────────────────────────────────────────────────────
    public int getSalud() { return salud; }
    public void setSalud(int salud) {
        this.salud = Math.max(0, Math.min(5, salud));
    }
    public void reducirSalud(int cantidad) { setSalud(this.salud - cantidad); }
    public boolean estaVivo() { return salud > 0; }

    // ─── Poder ─────────────────────────────────────────────────────────────────
    public int getPoder() { return poder; }
    public void setPoder(int poder) {
        if (poder < 1 || poder > 5) throw new IllegalArgumentException("Poder entre 1 y 5.");
        this.poder = poder;
    }

    // ─── Oro ───────────────────────────────────────────────────────────────────
    public int getOro() { return oro; }
    public void setOro(int oro) {
        if (oro < 0) throw new IllegalArgumentException("El oro no puede ser negativo.");
        this.oro = oro;
    }
    public void añadirOro(int cantidad) { setOro(this.oro + cantidad); }
    public void restarOro(int cantidad) {
        if (this.oro - cantidad < 0) throw new IllegalArgumentException("Oro insuficiente.");
        this.oro -= cantidad;
    }

    // ─── Nombre ────────────────────────────────────────────────────────────────
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    // ─── Habilidad Especial ────────────────────────────────────────────────────
    public HabilidadEspecial getHabilidadEspecial() { return habilidadEspecial; }
    public void setHabilidadEspecial(HabilidadEspecial h) { this.habilidadEspecial = h; }

    // ─── Equipo inventario ─────────────────────────────────────────────────────
    public List<Arma> getArmas() { return armas; }
    public List<Armadura> getArmaduras() { return armaduras; }

    public void addEquipo(Arma arma) { armas.add(arma); }
    public void addEquipo(Armadura armadura) { armaduras.add(armadura); }

    // ─── Equipo activo ─────────────────────────────────────────────────────────
    public List<Arma> getArmasActivas() { return armasActivas; }
    public Armadura getArmaduraActiva() { return armaduraActiva; }

    /**
     * Equipa un arma. Respeta la restricción: máx 2 de 1 mano, o 1 de 2 manos.
     */
    public void equiparArma(Arma arma) {
        if (!armas.contains(arma)) throw new IllegalArgumentException("El personaje no posee esa arma.");
        if (arma.esDosManost()) {
            armasActivas.clear();
            armasActivas.add(arma);
        } else {
            // No combinar con arma de 2 manos
            armasActivas.removeIf(Arma::esDosManost);
            if (armasActivas.size() >= 2) throw new IllegalStateException("Ya tienes 2 armas de 1 mano activas.");
            armasActivas.add(arma);
        }
    }

    public void desequiparArma(Arma arma) { armasActivas.remove(arma); }

    public void equiparArmadura(Armadura armadura) {
        if (!armaduras.contains(armadura)) throw new IllegalArgumentException("El personaje no posee esa armadura.");
        this.armaduraActiva = armadura;
    }

    public boolean tieneEquipoActivo() {
        return !armasActivas.isEmpty() && armaduraActiva != null;
    }

    /** Suma de modificadores de ataque del equipo activo. */
    public int getModAtaqueEquipo() {
        int total = armasActivas.stream().mapToInt(Arma::getModAtaque).sum();
        if (armaduraActiva != null) total += armaduraActiva.getModAtaque();
        return total;
    }

    /** Suma de modificadores de defensa del equipo activo. */
    public int getModDefensaEquipo() {
        int total = armasActivas.stream().mapToInt(Arma::getModDefensa).sum();
        if (armaduraActiva != null) total += armaduraActiva.getModDefensa();
        return total;
    }

    // ─── Esbirros ──────────────────────────────────────────────────────────────
    public List<Esbirro> getEsbirros() { return esbirros; }
    public void addEsbirro(Esbirro e) { esbirros.add(e); }
    public void removeEsbirro(Esbirro e) { esbirros.remove(e); }

    /** Salud total de todos los esbirros (incluyendo subesbirros de demonios). */
    public int getSaludTotalEsbirros() {
        return esbirros.stream().mapToInt(Esbirro::getSaludTotal).sum();
    }

    // ─── Modificadores ─────────────────────────────────────────────────────────
    public List<Modificador> getDebilidades() { return debilidades; }
    public List<Modificador> getFortalezas()  { return fortalezas; }

    public void addDebilidad(Modificador m) { debilidades.add(m); }
    public void addFortaleza(Modificador m) { fortalezas.add(m); }
    public void removeDebilidad(Modificador m) { debilidades.remove(m); }
    public void removeFortaleza(Modificador m) { fortalezas.remove(m); }

    // ─── Combate ───────────────────────────────────────────────────────────────
    public EstrategiaCombate getEstrategiaCombate() { return estrategiaCombate; }

    public int calcularPotenciaAtaque() {
        return estrategiaCombate.calcularAtaque(this);
    }

    public int calcularPotenciaDefensa() {
        return estrategiaCombate.calcularDefensa(this);
    }

    /** Llamado al inicio de cada combate para reiniciar recursos específicos. */
    public abstract void iniciarCombate();

    /** Llamado cuando el personaje pierde 1 punto de salud. */
    public abstract void onPerderSalud();

    /** Llamado cuando el ataque tiene éxito, para aplicar efectos post-ataque. */
    public abstract void onAtaqueExitoso();

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + nombre + ", salud=" + salud + ", poder=" + poder + ", oro=" + oro + "]";
    }
}