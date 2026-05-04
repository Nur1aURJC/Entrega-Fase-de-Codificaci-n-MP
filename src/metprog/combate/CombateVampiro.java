package metprog.combate;

import metprog.model.habilidad.Disciplina;
import metprog.model.personaje.Personaje;
import metprog.model.personaje.Vampiro;

/**
 * Estrategia de combate para Vampiros.
 * Potencial Ataque = poder + ataque(Disciplina) + ataque(equipo) + 2 si sangre >= 5.
 * El vampiro paga el coste de sangre de la disciplina al atacar.
 * Si no puede pagar, no usa el valor de la disciplina.
 */
public class CombateVampiro implements EstrategiaCombate {

    @Override
    public int calcularAtaque(Personaje p) {
        Vampiro v = (Vampiro) p;
        int total = v.getPoder();

        if (v.getHabilidadEspecial() instanceof Disciplina disciplina) {
            if (v.getPuntosSangre() >= disciplina.getCosteSangre()) {
                total += disciplina.getValorAtaque();
                v.gastarSangre(disciplina.getCosteSangre());
            }
            // si no puede pagar, no suma el valor de la disciplina
        }

        total += v.getModAtaqueEquipo();

        if (v.getPuntosSangre() >= 5) {
            total += 2;
        }

        // Aplicar fortalezas/debilidades presentes
        total += v.getBonusFortalezas() - v.getMalusDebilidades();

        return Math.max(1, total);
    }

    @Override
    public int calcularDefensa(Personaje p) {
        Vampiro v = (Vampiro) p;
        int total = v.getPoder();

        if (v.getHabilidadEspecial() instanceof Disciplina disciplina) {
            total += disciplina.getValorDefensa();
        }

        total += v.getModDefensaEquipo();
        total += v.getBonusFortalezas() - v.getMalusDebilidades();

        return Math.max(1, total);
    }
}