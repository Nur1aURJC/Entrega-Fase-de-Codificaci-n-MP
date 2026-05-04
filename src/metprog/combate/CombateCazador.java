package metprog.combate;

import metprog.model.habilidad.Talento;
import metprog.model.personaje.Cazador;
import metprog.model.personaje.Personaje;

/**
 * Estrategia de combate para Cazadores.
 * Potencial Ataque = poder + ataque(Talento) + ataque(equipo) + voluntad actual.
 */
public class CombateCazador implements EstrategiaCombate {

    @Override
    public int calcularAtaque(Personaje p) {
        Cazador c = (Cazador) p;
        int total = c.getPoder();

        if (c.getHabilidadEspecial() instanceof Talento talento) {
            total += talento.getValorAtaque();
        }

        total += c.getModAtaqueEquipo();
        total += c.getVoluntad();
        total += c.getBonusFortalezas() - c.getMalusDebilidades();

        return Math.max(1, total);
    }

    @Override
    public int calcularDefensa(Personaje p) {
        Cazador c = (Cazador) p;
        int total = c.getPoder();

        if (c.getHabilidadEspecial() instanceof Talento talento) {
            total += talento.getValorDefensa();
        }

        total += c.getModDefensaEquipo();
        total += c.getVoluntad();
        total += c.getBonusFortalezas() - c.getMalusDebilidades();

        return Math.max(1, total);
    }
}