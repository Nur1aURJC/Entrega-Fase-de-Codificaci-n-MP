package metprog.desafio;

/** Estado: esperando validación del operador. */
class PendienteValidacion implements EstadoDesafio {
    @Override
    public void procesar(Desafio d) {
        // El operador valida; transición a Validado
        d.setEstado(new Validado());
    }
    @Override public String getNombre() { return "PENDIENTE_VALIDACION"; }
}

/** Estado: validado por operador, esperando respuesta del desafiado. */
class Validado implements EstadoDesafio {
    @Override
    public void procesar(Desafio d) {
        // El desafiado acepta o rechaza
    }
    @Override public String getNombre() { return "VALIDADO"; }
}

/** Estado: aceptado por el desafiado, combate en curso / finalizado. */
class Aceptado implements EstadoDesafio {
    @Override
    public void procesar(Desafio d) {
        // Combate finalizado
    }
    @Override public String getNombre() { return "ACEPTADO"; }
}

/** Estado: rechazado por el desafiado. */
class Rechazado implements EstadoDesafio {
    @Override
    public void procesar(Desafio d) {
        // Se cobró el 10%; estado final
    }
    @Override public String getNombre() { return "RECHAZADO"; }
}