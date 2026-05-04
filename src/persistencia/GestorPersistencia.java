package persistencia;

import metprog.combate.ResultadoCombate;
import metprog.sistema.Operador;
import metprog.sistema.Usuario;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestiona la persistencia del sistema mediante serialización Java.
 * Guarda y carga: usuarios, operadores e historial de combates.
 */
public class GestorPersistencia {

    private static final String FICHERO_USUARIOS    = "data/usuarios.dat";
    private static final String FICHERO_OPERADORES  = "data/operadores.dat";
    private static final String FICHERO_COMBATES    = "data/combates.dat";

    public GestorPersistencia() {
        new File("data").mkdirs();
    }

    // ─── Usuarios ─────────────────────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    public List<Usuario> cargarUsuarios() {
        return (List<Usuario>) cargar(FICHERO_USUARIOS, new ArrayList<>());
    }

    public void guardarUsuarios(List<Usuario> usuarios) {
        guardar(FICHERO_USUARIOS, usuarios);
    }

    // ─── Operadores ───────────────────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    public List<Operador> cargarOperadores() {
        return (List<Operador>) cargar(FICHERO_OPERADORES, new ArrayList<>());
    }

    public void guardarOperadores(List<Operador> operadores) {
        guardar(FICHERO_OPERADORES, operadores);
    }

    // ─── Historial combates ───────────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    public List<ResultadoCombate> cargarCombates() {
        return (List<ResultadoCombate>) cargar(FICHERO_COMBATES, new ArrayList<>());
    }

    public void guardarCombates(List<ResultadoCombate> combates) {
        guardar(FICHERO_COMBATES, combates);
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────
    private void guardar(String ruta, Object objeto) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ruta))) {
            oos.writeObject(objeto);
        } catch (IOException e) {
            System.err.println("[PERSISTENCIA] Error al guardar " + ruta + ": " + e.getMessage());
        }
    }

    private Object cargar(String ruta, Object defecto) {
        File f = new File(ruta);
        if (!f.exists()) return defecto;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[PERSISTENCIA] Error al cargar " + ruta + ": " + e.getMessage());
            return defecto;
        }
    }
}