package metprog.sistema;

/**
 * Actor de tipo Operador (administrador).
 * No tiene número de registro.
 * Puede editar personajes, validar desafíos, bloquear/desbloquear usuarios.
 */
public class Operador extends Actor {

    private static final long serialVersionUID = 1L;

    public Operador(String nombre, String nick, String password) {
        super(nombre, nick, password);
    }

    @Override
    public String toString() {
        return "Operador[nick=" + getNick() + "]";
    }
}