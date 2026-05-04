package metprog.sistema;

import metprog.desafio.Desafio;
import metprog.model.personaje.Personaje;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Actor de tipo Usuario (jugador).
 * - Tiene número de registro único con formato LNNLL.
 * - Gestiona un único personaje activo.
 * - Puede tener un desafío pendiente de aceptar/rechazar.
 * - Puede ser bloqueado por el operador.
 */
public class Usuario extends Actor {

    private static final long serialVersionUID = 1L;

    // Formato LNNLL: Letra, Número, Número, Letra, Letra
    private static final Pattern FORMATO_REGISTRO = Pattern.compile("[A-Z][0-9][0-9][A-Z][A-Z]");

    private String numRegistro;
    private Personaje personaje;
    private boolean bloqueado;

    // Desafío pendiente de respuesta
    private Desafio desafioPendiente;

    // Historial de oro ganado/perdido
    private List<String> historialOro;

    public Usuario(String nombre, String nick, String password, String numRegistro) {
        super(nombre, nick, password);
        validarNumRegistro(numRegistro);
        this.numRegistro = numRegistro;
        this.bloqueado = false;
        this.historialOro = new ArrayList<>();
    }

    private void validarNumRegistro(String num) {
        if (num == null || !FORMATO_REGISTRO.matcher(num).matches()) {
            throw new IllegalArgumentException("Número de registro inválido. Formato: LNNLL (ej: A12BC).");
        }
    }

    // ─── Personaje ─────────────────────────────────────────────────────────────
    public Personaje getPersonaje() { return personaje; }
    public void setPersonaje(Personaje personaje) { this.personaje = personaje; }
    public boolean tienePersonaje() { return personaje != null; }

    // ─── Bloqueo ───────────────────────────────────────────────────────────────
    public boolean isBloqueado() { return bloqueado; }
    public void bloquear()   { this.bloqueado = true; }
    public void desbloquear(){ this.bloqueado = false; }

    // ─── Número de registro ────────────────────────────────────────────────────
    public String getNumRegistro() { return numRegistro; }

    // ─── Desafío pendiente ─────────────────────────────────────────────────────
    public Desafio getDesafioPendiente() { return desafioPendiente; }
    public void setDesafioPendiente(Desafio d) { this.desafioPendiente = d; }
    public boolean tieneDesafioPendiente() { return desafioPendiente != null; }

    // ─── Historial oro ─────────────────────────────────────────────────────────
    public List<String> getHistorialOro() { return historialOro; }
    public void registrarMovimientoOro(String descripcion) { historialOro.add(descripcion); }

    @Override
    public String toString() {
        return "Usuario[nick=" + getNick() + ", registro=" + numRegistro
                + ", bloqueado=" + bloqueado
                + ", personaje=" + (personaje != null ? personaje.getNombre() : "ninguno") + "]";
    }
}