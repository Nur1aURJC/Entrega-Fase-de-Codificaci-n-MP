package metprog.combate;

import metprog.model.habilidad.Don;
import metprog.model.personaje.Licantropo;
import metprog.model.personaje.Personaje;

/**
 * Estrategia de combate para Licántropos.
 * Potencial Ataque = poder + ataque(Don, si rabia suficiente) + ataque(equipo) + rabia actual.
 */
public class CombateLicantropo implements EstrategiaCombate {

    @Override
    public int calcularAtaque(Personaje p) {
        Licantropo l = (Licantropo) p;
        int total = l.getPoder();

        if (l.getHabilidadEspecial() instanceof Don don) {
            if (l.getRabia() >= don.getRabiaMinima()) {
                total += don.getValorAtaque();
            }
        }

        total += l.getModAtaqueEquipo();
        total += l.getRabia();
        total += l.getBonusFortalezas() - l.getMalusDebilidades();

        return Math.max(1, total);
    }

    @Override
    public int calcularDefensa(Personaje p) {
        Licantropo l = (Licantropo) p;
        int total = l.getPoder();

        if (l.getHabilidadEspecial() instanceof Don don) {
            if (l.getRabia() >= don.getRabiaMinima()) {
                total += don.getValorDefensa();
            }
        }

        total += l.getModDefensaEquipo();
        total += l.getRabia();
        total += l.getBonusFortalezas() - l.getMalusDebilidades();

        return Math.max(1, total);
    }
}