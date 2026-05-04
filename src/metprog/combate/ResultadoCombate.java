package metprog.combate;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Almacena el resultado completo de un combate para persistencia.
 * RF-91: desafiante, desafiado, rondas, fecha, vencedor, esbirros supervivientes, oro ganado.
 */
public class ResultadoCombate implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nickDesafiante;
    private String nickDesafiado;
    private int rondas;
    private LocalDateTime fecha;
    private String nickGanador; // null si empate
    private int oroGanado;

    private List<String> esbirrosSobrevivientes; // nombres de esbirros que sobrevivieron
    private List<String> logRondas;

    public ResultadoCombate(String nickDesafiante, String nickDesafiado,
                            int rondas, LocalDateTime fecha,
                            String nickGanador, int oroGanado) {
        this.nickDesafiante = nickDesafiante;
        this.nickDesafiado = nickDesafiado;
        this.rondas = rondas;
        this.fecha = fecha;
        this.nickGanador = nickGanador;
        this.oroGanado = oroGanado;
        this.esbirrosSobrevivientes = new ArrayList<>();
        this.logRondas = new ArrayList<>();
    }

    public void addEsbirroSobreviviente(String nombre) { esbirrosSobrevivientes.add(nombre); }
    public void addLogRonda(String log) { logRondas.add(log); }

    public String getNickDesafiante()           { return nickDesafiante; }
    public String getNickDesafiado()            { return nickDesafiado; }
    public int getRondas()                      { return rondas; }
    public LocalDateTime getFecha()             { return fecha; }
    public String getNickGanador()              { return nickGanador; }
    public int getOroGanado()                   { return oroGanado; }
    public List<String> getEsbirrosSobrevivientes() { return esbirrosSobrevivientes; }
    public List<String> getLogRondas()          { return logRondas; }

    @Override
    public String toString() {
        String resultado = nickGanador != null ? "Ganador: " + nickGanador : "EMPATE";
        return "Combate[" + nickDesafiante + " vs " + nickDesafiado
                + " | Rondas: " + rondas + " | " + resultado
                + " | Oro: " + oroGanado + " | " + fecha + "]";
    }
}