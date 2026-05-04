package metprog.desafio;

import metprog.model.modificador.Modificador;
import metprog.sistema.Usuario;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa un Desafío entre dos usuarios.
 * Implementa el patrón State para gestionar su ciclo de vida.
 * También actúa como Observable (patrón Observer) notificando a los usuarios implicados.
 */
public class Desafio implements Serializable {

    private static final long serialVersionUID = 1L;

    private Usuario desafiante;
    private Usuario desafiado;
    private int oroApostado;
    private LocalDateTime fecha;
    private int rondas;
    private String nickRetado;

    // Fortalezas y debilidades activas en este combate (asignadas por operador)
    private List<Modificador> fortalezasPresentes;
    private List<Modificador> debilidadesPresentes;

    // Estado (patrón State)
    private EstadoDesafio estado;

    // Resultado
    private Usuario ganador; // null en empate

    public Desafio(Usuario desafiante, String nickRetado, int oroApostado) {
        if (oroApostado < 0) throw new IllegalArgumentException("El oro apostado no puede ser negativo.");
        if (desafiante.getPersonaje() == null || desafiante.getPersonaje().getOro() < oroApostado) {
            throw new IllegalArgumentException("Oro apostado inválido o insuficiente.");
        }
        this.desafiante = desafiante;
        this.nickRetado = nickRetado;
        this.oroApostado = oroApostado;
        this.fecha = LocalDateTime.now();
        this.fortalezasPresentes = new ArrayList<>();
        this.debilidadesPresentes = new ArrayList<>();
        this.estado = new PendienteValidacion();
    }

    // ─── Estado (State) ────────────────────────────────────────────────────────
    public EstadoDesafio getEstado() { return estado; }
    public void setEstado(EstadoDesafio estado) { this.estado = estado; }

    public void validar() {
        if (!(estado instanceof PendienteValidacion)) throw new IllegalStateException("El desafío no está pendiente de validación.");
        estado.procesar(this);
    }

    public void aceptar() {
        if (!(estado instanceof Validado)) throw new IllegalStateException("El desafío no está en estado Validado.");
        setEstado(new Aceptado());
    }

    public void rechazar() {
        if (!(estado instanceof Validado)) throw new IllegalStateException("El desafío no está en estado Validado.");
        setEstado(new Rechazado());
        // Cobrar el 10% al desafiado
        if (desafiado != null && desafiado.getPersonaje() != null) {
            int penalizacion = (int) Math.ceil(oroApostado * 0.10);
            int oroDesafiado = desafiado.getPersonaje().getOro();
            desafiado.getPersonaje().setOro(Math.max(0, oroDesafiado - penalizacion));
            desafiado.registrarMovimientoOro("Penalización por rechazar desafío: -" + penalizacion);
        }
    }

    public boolean isPendienteValidacion() { return estado instanceof PendienteValidacion; }
    public boolean isValidado() { return estado instanceof Validado; }
    public boolean isAceptado() { return estado instanceof Aceptado; }
    public boolean isRechazado() { return estado instanceof Rechazado; }

    // ─── Notificación (Observer) ───────────────────────────────────────────────
    public void mostrarResultado() {
        // La vista mostrará el resultado; aquí se delega a ConsoleView
    }

    // ─── Fortalezas/Debilidades activas ───────────────────────────────────────
    public List<Modificador> getFortalezasPresentes()   { return fortalezasPresentes; }
    public List<Modificador> getDebilidadesPresentes()  { return debilidadesPresentes; }
    public void addFortalezaPresente(Modificador m)     { fortalezasPresentes.add(m); }
    public void addDebilidadPresente(Modificador m)     { debilidadesPresentes.add(m); }

    // ─── Getters/Setters ───────────────────────────────────────────────────────
    public Usuario getDesafiante()  { return desafiante; }
    public Usuario getDesafiado()   { return desafiado; }
    public void setDesafiado(Usuario desafiado) { this.desafiado = desafiado; }
    public int getOroApostado()     { return oroApostado; }
    public LocalDateTime getFecha() { return fecha; }
    public int getRondas()          { return rondas; }
    public void setRondas(int r)    { this.rondas = r; }
    public String getNickRetado()   { return nickRetado; }
    public Usuario getGanador()     { return ganador; }
    public void setGanador(Usuario ganador) { this.ganador = ganador; }

    @Override
    public String toString() {
        return "Desafio[" + desafiante.getNick() + " vs " + nickRetado
                + ", oro=" + oroApostado + ", estado=" + estado.getNombre() + "]";
    }
}