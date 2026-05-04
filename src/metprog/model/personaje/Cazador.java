package metprog.model.personaje;

import metprog.combate.CombateCazador;

/**
 * Personaje de tipo Cazador.
 * - voluntad: 0-3 (empieza en 3, -1 cada vez que pierde salud)
 * - Habilidad especial: Talento
 */
public class Cazador extends Personaje {

    private static final long serialVersionUID = 1L;

    private int voluntad; // 0-3

    private int bonusFortalezas = 0;
    private int malusDebilidades = 0;

    public Cazador(String nombre, int poder) {
        super(nombre, poder);
        this.voluntad = 3; // Empieza en 3
        this.estrategiaCombate = new CombateCazador();
    }

    // ─── Voluntad ──────────────────────────────────────────────────────────────
    public int getVoluntad() { return voluntad; }
    public void setVoluntad(int voluntad) {
        this.voluntad = Math.max(0, Math.min(3, voluntad));
    }
    public void reducirVoluntad() { setVoluntad(voluntad - 1); }

    // ─── Combate ───────────────────────────────────────────────────────────────
    @Override
    public void iniciarCombate() {
        this.voluntad = 3; // Empieza en 3 cada combate
        this.estrategiaCombate = new CombateCazador();
        bonusFortalezas = 0;
        malusDebilidades = 0;
    }

    @Override
    public void onPerderSalud() {
        reducirVoluntad(); // -1 voluntad al perder salud
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
        return "Cazador[" + getNombre() + ", salud=" + getSalud() + ", poder=" + getPoder()
                + ", voluntad=" + voluntad + ", oro=" + getOro() + "]";
    }
}