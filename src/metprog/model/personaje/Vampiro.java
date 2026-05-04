package metprog.model.personaje;

import metprog.combate.CombateVampiro;
import metprog.model.esbirro.Esbirro;
import metprog.model.esbirro.Humano;

/**
 * Personaje de tipo Vampiro.
 * - puntosSangre: 0-10
 * - edad: registrada
 * - Habilidad especial: Disciplina
 * - NO puede tener esbirros humanos
 */
public class Vampiro extends Personaje {

    private static final long serialVersionUID = 1L;

    private static final int MAX_SANGRE = 10;

    private int puntosSangre;
    private int edad;

    // Modificadores activos en combate (asignados por operador)
    private int bonusFortalezas = 0;
    private int malusDebilidades = 0;

    public Vampiro(String nombre, int poder, int edad) {
        super(nombre, poder);
        this.edad = edad;
        this.puntosSangre = 0;
        this.estrategiaCombate = new CombateVampiro();
    }

    // ─── Sangre ────────────────────────────────────────────────────────────────
    public int getPuntosSangre() { return puntosSangre; }
    public void setPuntosSangre(int puntosSangre) {
        this.puntosSangre = Math.max(0, Math.min(MAX_SANGRE, puntosSangre));
    }
    public void gastarSangre(int cantidad) {
        puntosSangre = Math.max(0, puntosSangre - cantidad);
    }
    public void recuperarSangre(int cantidad) {
        puntosSangre = Math.min(MAX_SANGRE, puntosSangre + cantidad);
    }

    // ─── Edad ──────────────────────────────────────────────────────────────────
    public int getEdad() { return edad; }
    public void setEdad(int edad) { this.edad = edad; }

    // ─── Restricción: no esbirros humanos ──────────────────────────────────────
    @Override
    public void addEsbirro(Esbirro e) {
        if (e instanceof Humano) throw new IllegalArgumentException("Los vampiros NO pueden tener esbirros humanos.");
        super.addEsbirro(e);
    }

    // ─── Combate ───────────────────────────────────────────────────────────────
    @Override
    public void iniciarCombate() {
        // La sangre NO se reinicia al inicio; se mantiene la que tenía
        this.estrategiaCombate = new CombateVampiro();
        bonusFortalezas = 0;
        malusDebilidades = 0;
    }

    @Override
    public void onPerderSalud() {
        // El vampiro no tiene mecánica especial al perder salud
    }

    @Override
    public void onAtaqueExitoso() {
        recuperarSangre(4);
    }

    // ─── Modificadores de combate ──────────────────────────────────────────────
    public int getBonusFortalezas()   { return bonusFortalezas; }
    public int getMalusDebilidades()  { return malusDebilidades; }
    public void setBonusFortalezas(int v)  { this.bonusFortalezas = v; }
    public void setMalusDebilidades(int v) { this.malusDebilidades = v; }

    @Override
    public String toString() {
        return "Vampiro[" + getNombre() + ", salud=" + getSalud() + ", poder=" + getPoder()
                + ", sangre=" + puntosSangre + ", edad=" + edad + ", oro=" + getOro() + "]";
    }
}