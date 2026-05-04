package metprog.model.personaje;

import metprog.combate.CombateLicantropo;

/**
 * Personaje de tipo Licántropo.
 * - rabia: 0-3 (empieza en 0, +1 cada vez que pierde salud)
 * - Puede transformarse (forma bestia: aumenta estatura 0.5-1m y peso 90-110 kg)
 * - Habilidad especial: Don
 */
public class Licantropo extends Personaje {

    private static final long serialVersionUID = 1L;

    private int rabia; // 0-3
    private double estaturaHumana; // metros
    private double pesoHumano;     // kilos

    private int bonusFortalezas = 0;
    private int malusDebilidades = 0;

    public Licantropo(String nombre, int poder, double estaturaHumana, double pesoHumano) {
        super(nombre, poder);
        this.estaturaHumana = estaturaHumana;
        this.pesoHumano = pesoHumano;
        this.rabia = 0;
        this.estrategiaCombate = new CombateLicantropo();
    }

    // ─── Rabia ─────────────────────────────────────────────────────────────────
    public int getRabia() { return rabia; }
    public void setRabia(int rabia) {
        this.rabia = Math.max(0, Math.min(3, rabia));
    }
    public void incrementarRabia() { setRabia(rabia + 1); }

    // ─── Forma bestia ──────────────────────────────────────────────────────────
    public double getEstaturaHumana() { return estaturaHumana; }
    public double getPesoHumano()     { return pesoHumano; }

    /** Estatura en forma de bestia: +0.5 a +1 metro. */
    public double getEstaturabestia(double incremento) {
        if (incremento < 0.5 || incremento > 1.0) throw new IllegalArgumentException("Incremento entre 0.5 y 1.0m.");
        return estaturaHumana + incremento;
    }

    /** Peso en forma de bestia: +90 a +110 kg. */
    public double getPesoBestia(double incremento) {
        if (incremento < 90 || incremento > 110) throw new IllegalArgumentException("Incremento entre 90 y 110 kg.");
        return pesoHumano + incremento;
    }

    public void setEstaturaHumana(double v) { this.estaturaHumana = v; }
    public void setPesoHumano(double v)     { this.pesoHumano = v; }

    // ─── Combate ───────────────────────────────────────────────────────────────
    @Override
    public void iniciarCombate() {
        this.rabia = 0; // Empieza en 0 cada combate
        this.estrategiaCombate = new CombateLicantropo();
        bonusFortalezas = 0;
        malusDebilidades = 0;
    }

    @Override
    public void onPerderSalud() {
        incrementarRabia(); // +1 rabia al perder salud
    }

    @Override
    public void onAtaqueExitoso() {
        // Sin efecto especial post-ataque
    }

    // ─── Modificadores ─────────────────────────────────────────────────────────
    public int getBonusFortalezas()  { return bonusFortalezas; }
    public int getMalusDebilidades() { return malusDebilidades; }
    public void setBonusFortalezas(int v)  { this.bonusFortalezas = v; }
    public void setMalusDebilidades(int v) { this.malusDebilidades = v; }

    @Override
    public String toString() {
        return "Licantropo[" + getNombre() + ", salud=" + getSalud() + ", poder=" + getPoder()
                + ", rabia=" + rabia + ", oro=" + getOro() + "]";
    }
}