package metprog.desafio;

import java.io.Serializable;

public interface EstadoDesafio extends Serializable {
    void procesar(Desafio desafio);
    String getNombre();
}