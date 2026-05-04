package metprog.sistema;

import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * Gestiona el registro e inicio de sesión de Usuarios y Operadores.
 */
public class ActoresManager {

    private static final Pattern FORMATO_REGISTRO = Pattern.compile("[A-Z][0-9][0-9][A-Z][A-Z]");
    private static final Random RANDOM = new Random();

    private final List<Usuario>  usuarios;
    private final List<Operador> operadores;

    public ActoresManager(List<Usuario> usuarios, List<Operador> operadores) {
        this.usuarios   = usuarios;
        this.operadores = operadores;
    }

    // ─── Registro ─────────────────────────────────────────────────────────────
    public Usuario registrarJugador(String nombre, String nick, String password) {
        if (buscarUsuarioPorNick(nick) != null) throw new IllegalArgumentException("El nick ya está en uso.");
        String numReg = generarNumeroRegistro();
        Usuario u = new Usuario(nombre, nick, password, numReg);
        usuarios.add(u);
        return u;
    }

    public Operador registrarOperador(String nombre, String nick, String password) {
        if (buscarOperadorPorNick(nick) != null) throw new IllegalArgumentException("El nick ya está en uso.");
        Operador o = new Operador(nombre, nick, password);
        operadores.add(o);
        return o;
    }

    // ─── Baja ─────────────────────────────────────────────────────────────────
    public boolean darDeBajaUsuario(String nick, String password) {
        Usuario u = autenticarUsuario(nick, password);
        if (u == null) return false;
        usuarios.remove(u);
        return true;
    }

    public boolean darDeBajaOperador(String nick, String password) {
        Operador o = autenticarOperador(nick, password);
        if (o == null) return false;
        operadores.remove(o);
        return true;
    }

    // ─── Login ────────────────────────────────────────────────────────────────
    public Actor login(String nick, String password) {
        Usuario u = buscarUsuarioPorNick(nick);
        if (u != null && u.autenticar(nick, password)) return u;
        Operador o = buscarOperadorPorNick(nick);
        if (o != null && o.autenticar(nick, password)) return o;
        return null;
    }

    public Usuario autenticarUsuario(String nick, String password) {
        Usuario u = buscarUsuarioPorNick(nick);
        return (u != null && u.autenticar(nick, password)) ? u : null;
    }

    public Operador autenticarOperador(String nick, String password) {
        Operador o = buscarOperadorPorNick(nick);
        return (o != null && o.autenticar(nick, password)) ? o : null;
    }

    // ─── Búsqueda ─────────────────────────────────────────────────────────────
    public Usuario buscarUsuarioPorNick(String nick) {
        return usuarios.stream().filter(u -> u.getNick().equals(nick)).findFirst().orElse(null);
    }

    public Operador buscarOperadorPorNick(String nick) {
        return operadores.stream().filter(o -> o.getNick().equals(nick)).findFirst().orElse(null);
    }

    public List<Usuario>  getUsuarios()   { return usuarios; }
    public List<Operador> getOperadores() { return operadores; }

    // ─── Número de registro único ──────────────────────────────────────────────
    private String generarNumeroRegistro() {
        String num;
        do {
            char l1 = (char) ('A' + RANDOM.nextInt(26));
            int  n1 = RANDOM.nextInt(10);
            int  n2 = RANDOM.nextInt(10);
            char l2 = (char) ('A' + RANDOM.nextInt(26));
            char l3 = (char) ('A' + RANDOM.nextInt(26));
            num = "" + l1 + n1 + n2 + l2 + l3;
        } while (existeNumRegistro(num));
        return num;
    }

    private boolean existeNumRegistro(String num) {
        return usuarios.stream().anyMatch(u -> u.getNumRegistro().equals(num));
    }
}