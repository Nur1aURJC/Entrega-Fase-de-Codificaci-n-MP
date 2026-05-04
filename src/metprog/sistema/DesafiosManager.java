package metprog.sistema;

import metprog.combate.MotorCombate;
import metprog.combate.ResultadoCombate;
import metprog.desafio.Desafio;
import metprog.model.modificador.Modificador;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestiona los desafíos y la ejecución de combates.
 */
public class DesafiosManager {

    private final List<Usuario>         usuarios;
    private final List<Desafio>         desafiosPendientes; // pendientes de validación o respuesta
    private final List<ResultadoCombate> historialCombates;
    private final MotorCombate           motorCombate;

    public DesafiosManager(List<Usuario> usuarios, List<ResultadoCombate> historialCombates) {
        this.usuarios           = usuarios;
        this.desafiosPendientes = new ArrayList<>();
        this.historialCombates  = historialCombates;
        this.motorCombate       = new MotorCombate();
    }

    /**
     * Un usuario desafía a otro por su nick.
     * RF-57, 58, 59, 66, 68.
     */
    public Desafio mandarDesafio(Usuario retador, String nickRetado, int oroApostado) {
        if (retador.isBloqueado()) throw new IllegalStateException("Tu cuenta está bloqueada.");
        if (!retador.tienePersonaje()) throw new IllegalStateException("Debes tener un personaje para desafiar.");
        if (!retador.getPersonaje().tieneEquipoActivo()) throw new IllegalStateException("Tu personaje necesita equipo activo.");

        Usuario retado = buscarUsuario(nickRetado);
        if (retado == null) throw new IllegalArgumentException("No existe usuario con nick: " + nickRetado);
        if (retado.isBloqueado()) throw new IllegalStateException("El usuario retado está bloqueado.");
        if (!retado.tienePersonaje()) throw new IllegalStateException("El usuario retado no tiene personaje.");
        if (!retado.getPersonaje().tieneEquipoActivo()) throw new IllegalStateException("El personaje retado no tiene equipo activo.");
        if (haPerididoCombateEnUltimas24h(retado)) throw new IllegalStateException("No puedes desafiar a alguien que perdió un combate en las últimas 24h.");

        Desafio d = new Desafio(retador, nickRetado, oroApostado);
        desafiosPendientes.add(d);
        return d;
    }

    /**
     * El operador valida un desafío y decide las fortalezas/debilidades activas.
     * RF-60, 61.
     */
    public void validarDesafio(Desafio d, List<Modificador> fortalezas, List<Modificador> debilidades) {
        fortalezas.forEach(d::addFortalezaPresente);
        debilidades.forEach(d::addDebilidadPresente);

        // Asignar el usuario desafiado
        Usuario desafiado = buscarUsuario(d.getNickRetado());
        if (desafiado == null) throw new IllegalStateException("Usuario desafiado no encontrado.");
        d.setDesafiado(desafiado);

        d.validar(); // Cambia estado a Validado

        // Notificar al desafiado
        desafiado.setDesafioPendiente(d);
    }

    /**
     * El desafiado acepta el desafío e inicia el combate.
     */
    public ResultadoCombate aceptarDesafio(Desafio d) {
        d.aceptar();
        ResultadoCombate resultado = motorCombate.ejecutar(d);
        historialCombates.add(resultado);
        desafiosPendientes.remove(d);
        d.getDesafiado().setDesafioPendiente(null);

        // Bloquear al perdedor 24h (se gestiona mediante timestamp en Usuario)
        if (resultado.getNickGanador() != null) {
            String nickPerdedor = resultado.getNickGanador().equals(d.getDesafiante().getNick())
                    ? d.getDesafiado().getNick() : d.getDesafiante().getNick();
            Usuario perdedor = buscarUsuario(nickPerdedor);
            if (perdedor != null) perdedor.bloquear();
            // En una implementación real se usaría un timestamp para auto-desbloquear a las 24h
        }

        return resultado;
    }

    /**
     * El desafiado rechaza el desafío.
     * RF-64: se cobra el 10% automáticamente.
     */
    public void rechazarDesafio(Desafio d) {
        d.rechazar();
        desafiosPendientes.remove(d);
        d.getDesafiado().setDesafioPendiente(null);
    }

    /** RF-89: Ranking global (por oro). */
    public List<Usuario> getRankingGlobal() {
        List<Usuario> ranking = new ArrayList<>(usuarios);
        ranking.sort((a, b) -> {
            int oroA = a.tienePersonaje() ? a.getPersonaje().getOro() : 0;
            int oroB = b.tienePersonaje() ? b.getPersonaje().getOro() : 0;
            return Integer.compare(oroB, oroA);
        });
        return ranking;
    }

    public List<Desafio>          getDesafiosPendientes() { return desafiosPendientes; }
    public List<ResultadoCombate> getHistorialCombates()  { return historialCombates; }

    // ─── Helpers ──────────────────────────────────────────────────────────────
    private Usuario buscarUsuario(String nick) {
        return usuarios.stream().filter(u -> u.getNick().equals(nick)).findFirst().orElse(null);
    }

    private boolean haPerididoCombateEnUltimas24h(Usuario u) {
        LocalDateTime hace24h = LocalDateTime.now().minusHours(24);
        return historialCombates.stream().anyMatch(c ->
                c.getNickDesafiado().equals(u.getNick()) || c.getNickDesafiante().equals(u.getNick())
                        && c.getNickGanador() != null
                        && !c.getNickGanador().equals(u.getNick())
                        && c.getFecha().isAfter(hace24h)
        );
    }
}