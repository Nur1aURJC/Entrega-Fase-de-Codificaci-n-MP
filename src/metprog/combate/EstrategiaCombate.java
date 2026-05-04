package metprog.combate;

import metprog.model.personaje.Personaje;

/**
 * Interfaz Strategy para el cálculo de potencial de ataque y defensa.
 * Cada raza implementa su propia lógica de combate.
 */
public interface EstrategiaCombate {

    /**
     * Calcula el potencial de ataque del personaje en una ronda.
     * @param p El personaje atacante.
     * @return potencial de ataque (número de dados a lanzar).
     */
    int calcularAtaque(Personaje p);

    /**
     * Calcula el potencial de defensa del personaje en una ronda.
     * @param p El personaje defensor.
     * @return potencial de defensa (número de dados a lanzar).
     */
    int calcularDefensa(Personaje p);
}