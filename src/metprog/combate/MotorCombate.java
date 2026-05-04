package metprog.combate;

import metprog.desafio.Desafio;
import metprog.model.esbirro.Esbirro;
import metprog.model.personaje.Cazador;
import metprog.model.personaje.Licantropo;
import metprog.model.personaje.Personaje;
import metprog.model.personaje.Vampiro;
import metprog.sistema.Usuario;

import java.time.LocalDateTime;
import java.util.Random;

/**
 * Motor de combate. Ejecuta un Desafío aceptado y devuelve el ResultadoCombate.
 * Lógica:
 *  - Rondas hasta que uno llega a 0 de salud.
 *  - Potencial ataque/defensa -> lanzar N dados (1-6), éxito si >= 5.
 *  - Si éxitos ataque >= éxitos defensa -> -1 salud al defensor (esbirros primero).
 *  - Ambos 0 en misma ronda -> empate.
 */
public class MotorCombate {

    private static final Random RANDOM = new Random();

    public ResultadoCombate ejecutar(Desafio desafio) {
        Usuario uA = desafio.getDesafiante();
        Usuario uB = desafio.getDesafiado();
        Personaje pA = uA.getPersonaje();
        Personaje pB = uB.getPersonaje();

        // Aplicar modificadores activos del desafío
        aplicarModificadores(pA, pB, desafio);

        // Iniciar combate (reiniciar recursos: rabia=0, voluntad=3, etc.)
        pA.iniciarCombate();
        pB.iniciarCombate();

        //Los curamos por si han cursado algún combate anteriormente
        pA.setSalud(5);
        pB.setSalud(5);

        // Salud efectiva total (esbirros + personaje)
        int saludEfectivaA = pA.getSaludTotalEsbirros() + pA.getSalud();
        int saludEfectivaB = pB.getSaludTotalEsbirros() + pB.getSalud();

        int dañoAcumuladoA = 0; // daño recibido por A
        int dañoAcumuladoB = 0; // daño recibido por B
        int rondas = 0;

        StringBuilder log = new StringBuilder();
        ResultadoCombate resultado = null;

        while (true) {
            rondas++;

            // Calcular potenciales
            int potAtaqueA = pA.calcularPotenciaAtaque();
            int potDefensaA = pA.calcularPotenciaDefensa();
            int potAtaqueB = pB.calcularPotenciaAtaque();
            int potDefensaB = pB.calcularPotenciaDefensa();

            // Lanzar dados
            int exitosAtaqueA  = lanzarDados(potAtaqueA);
            int exitosDefensaA = lanzarDados(potDefensaA);
            int exitosAtaqueB  = lanzarDados(potAtaqueB);
            int exitosDefensaB = lanzarDados(potDefensaB);

            String logRonda = String.format(
                    "Ronda %d | %s: ATK=%d(éxitos=%d) DEF=%d(éxitos=%d) | %s: ATK=%d(éxitos=%d) DEF=%d(éxitos=%d)",
                    rondas,
                    pA.getNombre(), potAtaqueA, exitosAtaqueA, potDefensaA, exitosDefensaA,
                    pB.getNombre(), potAtaqueB, exitosAtaqueB, potDefensaB, exitosDefensaB
            );

            boolean dañoAB = false; // A daña a B
            boolean dañoBA = false; // B daña a A

            // A ataca a B
            if (exitosAtaqueA >= exitosDefensaB) {
                dañoAcumuladoB++;
                dañoAB = true;
                pA.onAtaqueExitoso();
                logRonda += " | " + pA.getNombre() + " impacta a " + pB.getNombre();
            }
            // B ataca a A
            if (exitosAtaqueB >= exitosDefensaA) {
                dañoAcumuladoA++;
                dañoBA = true;
                pB.onAtaqueExitoso();
                logRonda += " | " + pB.getNombre() + " impacta a " + pA.getNombre();
            }

            // Aplicar daño a esbirros/personaje
            if (dañoAB) aplicarDaño(pB, dañoAcumuladoB);
            if (dañoBA) aplicarDaño(pA, dañoAcumuladoA);

            log.append(logRonda).append("\n");

            boolean AmuertO = dañoAcumuladoB >= saludEfectivaB || !pB.estaVivo();
            boolean BmuertO = dañoAcumuladoA >= saludEfectivaA || !pA.estaVivo();

            if (AmuertO || BmuertO) {
                String nickGanador;
                int oroGanado = desafio.getOroApostado();

                if (AmuertO && BmuertO) {
                    // Empate
                    nickGanador = null;
                    oroGanado = 0;
                    log.append("EMPATE en ronda ").append(rondas).append("\n");
                } else if (AmuertO) {
                    // A (desafiante) gana
                    nickGanador = uA.getNick();
                    uA.getPersonaje().añadirOro(oroGanado);
                    uB.getPersonaje().restarOro(Math.min(oroGanado, uB.getPersonaje().getOro()));
                    uA.registrarMovimientoOro("Ganó combate vs " + uB.getNick() + ": +" + oroGanado);
                    uB.registrarMovimientoOro("Perdió combate vs " + uA.getNick() + ": -" + oroGanado);
                    log.append(uA.getNick()).append(" GANA!\n");
                } else {
                    // B (desafiado) gana
                    nickGanador = uB.getNick();
                    uB.getPersonaje().añadirOro(oroGanado);
                    uA.getPersonaje().restarOro(Math.min(oroGanado, uA.getPersonaje().getOro()));
                    uB.registrarMovimientoOro("Ganó combate vs " + uA.getNick() + ": +" + oroGanado);
                    uA.registrarMovimientoOro("Perdió combate vs " + uB.getNick() + ": -" + oroGanado);
                    log.append(uB.getNick()).append(" GANA!\n");
                }

                resultado = new ResultadoCombate(
                        uA.getNick(), uB.getNick(), rondas, LocalDateTime.now(), nickGanador, oroGanado
                );

                // Registrar esbirros supervivientes
                registrarEsbirrosSobrevivientes(resultado, pA, dañoAcumuladoA);
                registrarEsbirrosSobrevivientes(resultado, pB, dañoAcumuladoB);

                // Añadir log
                for (String linea : log.toString().split("\n")) {
                    resultado.addLogRonda(linea);
                }

                break;
            }
        }

        desafio.setRondas(rondas);
        return resultado;
    }

    /** Lanza N dados de 6 caras, cuenta los que sacan 5 o 6 (éxitos). */
    private int lanzarDados(int n) {
        int exitos = 0;
        for (int i = 0; i < n; i++) {
            int dado = RANDOM.nextInt(6) + 1;
            if (dado >= 5) exitos++;
        }
        return exitos;
    }

    /**
     * Aplica el daño acumulado al personaje.
     * Primero absorben los esbirros; cuando se supera su salud total, el resto va al personaje.
     */
    private void aplicarDaño(Personaje p, int dañoAcumulado) {
        int saludEsbirros = p.getSaludTotalEsbirros();
        if (dañoAcumulado > saludEsbirros) {
            int dañoPersonaje = dañoAcumulado - saludEsbirros;
            int saludAnterior = p.getSalud();
            p.setSalud(5 - dañoPersonaje); // salud inicia en 5
            if (p.getSalud() < saludAnterior) {
                p.onPerderSalud();
            }
        }
        // Si daño <= salud esbirros, el personaje no recibe daño aún
    }

    private void registrarEsbirrosSobrevivientes(ResultadoCombate r, Personaje p, int dañoRecibido) {
        int saludEsbirros = p.getSaludTotalEsbirros();
        if (dañoRecibido < saludEsbirros) {
            // Tiene esbirros sobrevivientes
            for (Esbirro e : p.getEsbirros()) {
                r.addEsbirroSobreviviente(p.getNombre() + " -> " + e.getNombre());
            }
        }
    }

    private void aplicarModificadores(Personaje pA, Personaje pB, Desafio desafio) {
        // Bonus/malus según las fortalezas y debilidades presentes
        // Se aplica a ambos si corresponde; la lógica detallada se delega al operador
        int bonusA = desafio.getFortalezasPresentes().stream()
                .filter(m -> pA.getFortalezas().contains(m))
                .mapToInt(m -> m.getValor()).sum();
        int malusA = desafio.getDebilidadesPresentes().stream()
                .filter(m -> pA.getDebilidades().contains(m))
                .mapToInt(m -> m.getValor()).sum();

        int bonusB = desafio.getFortalezasPresentes().stream()
                .filter(m -> pB.getFortalezas().contains(m))
                .mapToInt(m -> m.getValor()).sum();
        int malusB = desafio.getDebilidadesPresentes().stream()
                .filter(m -> pB.getDebilidades().contains(m))
                .mapToInt(m -> m.getValor()).sum();

        // Inyectar en personajes (necesitan método genérico)
        setBonusMalus(pA, bonusA, malusA);
        setBonusMalus(pB, bonusB, malusB);
    }

    private void setBonusMalus(Personaje p, int bonus, int malus) {
        if (p instanceof Vampiro v)   { v.setBonusFortalezas(bonus); v.setMalusDebilidades(malus); }
        if (p instanceof Licantropo l){ l.setBonusFortalezas(bonus); l.setMalusDebilidades(malus); }
        if (p instanceof Cazador c)   { c.setBonusFortalezas(bonus); c.setMalusDebilidades(malus); }
    }
}