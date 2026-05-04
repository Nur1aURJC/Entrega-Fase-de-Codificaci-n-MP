package metprog.sistema;

import java.io.Serializable;

/**
 * Clase base para los actores del sistema (Usuario y Operador).
 */
public abstract class Actor implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nombre;
    private String nick;
    private String password; // longitud 8-12

    public Actor(String nombre, String nick, String password) {
        validarPassword(password);
        this.nombre = nombre;
        this.nick = nick;
        this.password = password;
    }

    protected void validarPassword(String password) {
        if (password == null || password.length() < 8 || password.length() > 12) {
            throw new IllegalArgumentException("La contraseña debe tener entre 8 y 12 caracteres.");
        }
    }

    public String getNombre()   { return nombre; }
    public String getNick()     { return nick; }
    public String getPassword() { return password; }

    public void setNombre(String nombre)     { this.nombre = nombre; }
    public void setNick(String nick)         { this.nick = nick; }
    public void setPassword(String password) { validarPassword(password); this.password = password; }

    public boolean autenticar(String nick, String password) {
        return this.nick.equals(nick) && this.password.equals(password);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[nick=" + nick + "]";
    }
}