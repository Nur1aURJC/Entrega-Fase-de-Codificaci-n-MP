package metprog;

import metprog.combate.ResultadoCombate;
import metprog.desafio.Desafio;
import metprog.model.equipo.Arma;
import metprog.model.equipo.Armadura;
import metprog.model.esbirro.*;
import metprog.model.modificador.Modificador;
import metprog.model.personaje.*;
import persistencia.GestorPersistencia;
import metprog.sistema.*;
import metprog.vista.ConsoleView;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase principal del juego. Orquesta toda la lógica de la aplicación.
 * Contiene la lista de usuarios, operadores e historial de combates.
 */
public class AplicacionJuego {

    private List<Usuario>          usuariosRegistrados;
    private List<Operador>         operadorRegistrados;
    private List<ResultadoCombate> historialCombates;

    private List<Arma> catalogoArmas;
    private List<Armadura> catalogoArmaduras;

    private final ActoresManager   actoresManager;
    private final DesafiosManager  desafiosManager;
    private final GestorPersistencia persistencia;
    private final ConsoleView      vista;

    private Actor actorActual; // usuario u operador logueado

    private List<Arma> catalogoArmasBase;
    private List<Armadura> catalogoArmadurasBase;

    public AplicacionJuego() {
        this.persistencia = new GestorPersistencia();
        this.vista        = new ConsoleView();

        // Cargar datos persistidos
        this.usuariosRegistrados = persistencia.cargarUsuarios();
        this.operadorRegistrados = persistencia.cargarOperadores();
        this.historialCombates   = persistencia.cargarCombates();

        this.actoresManager  = new ActoresManager(usuariosRegistrados, operadorRegistrados);
        this.desafiosManager = new DesafiosManager(usuariosRegistrados, historialCombates);
        this.catalogoArmasBase = new ArrayList<>();     // <--- ESTA LÍNEA ES LA QUE EVITA EL NULLPOINTER
        this.catalogoArmadurasBase = new ArrayList<>(); // <--- ESTA TAMBIÉN

        // Llenamos el catálogo con los objetos
        catalogoArmasBase.add(new Arma("Espada de Plata", 2, 1, Arma.TipoManos.UNA_MANO));
        catalogoArmasBase.add(new Arma("Mandoble Pesado", 3, 0, Arma.TipoManos.DOS_MANOS));
        catalogoArmasBase.add(new Arma("Daga de Cazador", 1, 0, Arma.TipoManos.UNA_MANO));

        catalogoArmadurasBase.add(new Armadura("Cota de Malla", 0, 2));
        catalogoArmadurasBase.add(new Armadura("Armadura de Placas", 0, 3));
        catalogoArmadurasBase.add(new Armadura("Túnica Ligera", 1, 1));
    }

    // ─── Entrada principal ────────────────────────────────────────────────────
    public void start() {
        vista.mostrarMensaje("╔════════════════════════════════════════╗");
        vista.mostrarMensaje("║     BIENVENIDO A METPROG URJC S.L.     ║");
        vista.mostrarMensaje("║      Videojuego de Combate v1.0        ║");
        vista.mostrarMensaje("╚════════════════════════════════════════╝");

        boolean ejecutando = true;
        while (ejecutando) {
            actorActual = loginOrReg();
            if (actorActual == null) { ejecutando = false; break; }
            if (actorActual instanceof Operador op) {
                menuOperador(op);
            } else if (actorActual instanceof Usuario u) {
                menuUsuario(u);
            }
            guardarDatos();
        }
        vista.mostrarMensaje("¡Hasta pronto!");
        vista.cerrar();
    }

    // ─── Login / Registro ─────────────────────────────────────────────────────
    private Actor loginOrReg() {
        while (true) {
            vista.mostrarTitulo("Inicio de sesión");
            vista.mostrarMensaje("1. Iniciar sesión");
            vista.mostrarMensaje("2. Registrarse");
            vista.mostrarMensaje("0. Salir");
            String op = vista.leerPantalla("Opción");
            switch (op) {
                case "1" -> { return realizarLogin(); }
                case "2" -> { realizarRegistro(); }
                case "0" -> { return null; }
                default  -> vista.mostrarError("Opción inválida.");
            }
        }
    }

    private Actor realizarLogin() {
        String nick = vista.leerPantalla("Nick");
        String pass = vista.leerPantalla("Contraseña");
        Actor a = actoresManager.login(nick, pass);
        if (a == null) { vista.mostrarError("Credenciales incorrectas."); return null; }
        vista.mostrarMensaje("¡Bienvenido, " + a.getNombre() + "!");
        return a;
    }

    private void realizarRegistro() {
        vista.mostrarMensaje("1. Registrarse como Jugador");
        vista.mostrarMensaje("2. Registrarse como Operador");
        String tipo = vista.leerPantalla("Tipo");
        String nombre = vista.leerPantalla("Nombre");
        String nick   = vista.leerPantalla("Nick");
        String pass;
        while (true) {
            pass = vista.leerPantalla("Contraseña (8-12 caracteres)");
            if (pass.length() >= 8 && pass.length() <= 12) break;
            vista.mostrarError("La contraseña debe tener entre 8 y 12 caracteres.");
        }
        try {
            if (tipo.equals("1")) {
                Usuario u = actoresManager.registrarJugador(nombre, nick, pass);
                vista.mostrarMensaje("Registrado correctamente. Tu número de registro: " + u.getNumRegistro());
            } else {
                actoresManager.registrarOperador(nombre, nick, pass);
                vista.mostrarMensaje("Operador registrado correctamente.");
            }
            guardarDatos();
        } catch (Exception e) {
            vista.mostrarError(e.getMessage());
        }
    }


    // ─── Menú Usuario ─────────────────────────────────────────────────────────
    private void menuUsuario(Usuario u) {
        boolean sesionActiva = true;
        while (sesionActiva) {
            // Si tiene desafío pendiente, forzar respuesta
            if (u.tieneDesafioPendiente()) {
                manejarDesafioPendiente(u);
                continue;
            }
            vista.mostrarTitulo("Menú Jugador - " + u.getNick());
            if (u.isBloqueado()) vista.mostrarMensaje("⚠ Tu cuenta está BLOQUEADA (derrota en últimas 24h).");
            vista.mostrarMensaje("1. Crear personaje");
            vista.mostrarMensaje("2. Dar de baja personaje");
            vista.mostrarMensaje("3. Gestionar equipo activo");
            vista.mostrarMensaje("4. Desafiar a otro usuario");
            vista.mostrarMensaje("5. Consultar historial de oro");
            vista.mostrarMensaje("6. Consultar ranking global");
            vista.mostrarMensaje("0. Cerrar sesión");
            String op = vista.leerPantalla("Opción");
            switch (op) {
                case "1" -> crearPersonaje(u);
                case "2" -> darDeBajaPersonaje(u);
                case "3" -> gestionarEquipo(u);
                case "4" -> { if (!u.isBloqueado()) desafiarUsuario(u); else vista.mostrarError("Cuenta bloqueada."); }
                case "5" -> mostrarHistorialOro(u);
                case "6" -> mostrarRanking();
                case "0" -> sesionActiva = false;
                default  -> vista.mostrarError("Opción inválida.");
            }
        }
    }

    private void crearPersonaje(Usuario u) {
        if (u.tienePersonaje()) { vista.mostrarError("Ya tienes un personaje activo."); return; }
        vista.mostrarMensaje("Tipo de personaje: 1=Vampiro  2=Licántropo  3=Cazador");
        String tipo   = vista.leerPantalla("Tipo");
        String nombre = vista.leerPantalla("Nombre del personaje");
        int poder     = leerEnteroRango("Poder (1-5)", 1, 5);
        Personaje p;
        switch (tipo) {
            case "1" -> {
                int edad = vista.leerEntero("Edad del vampiro");
                p = new Vampiro(nombre, poder, edad);
            }
            case "2" -> {
                double estatura = vista.leerDecimal("Estatura humana (m)");
                double peso     = vista.leerDecimal("Peso humano (kg)");
                p = new Licantropo(nombre, poder, estatura, peso);
            }
            case "3" -> p = new Cazador(nombre, poder);
            default  -> { vista.mostrarError("Tipo inválido."); return; }
        }
        // --- INICIO DEL EQUIPO BASE ---
        // Le damos el equipo al inventario (Armas/Armaduras disponibles)
        p.addEquipo(new Arma("Espada Corta (Base)", 1, 0, Arma.TipoManos.UNA_MANO));
        p.addEquipo(new Armadura("Ropa de Cuero (Base)", 0, 1));

        // (Opcional) Si además quieres que se lo equipe automáticamente al crearlo:
        p.equiparArma(p.getArmas().get(0));
        p.equiparArmadura(p.getArmaduras().get(0));

        u.setPersonaje(p);
        vista.mostrarMensaje("Personaje '" + nombre + "' creado correctamente.");
    }

    private void darDeBajaPersonaje(Usuario u) {
        if (!u.tienePersonaje()) { vista.mostrarError("No tienes personaje."); return; }
        if (vista.leerBooleano("¿Seguro que quieres eliminar tu personaje?")) {
            u.setPersonaje(null);
            vista.mostrarMensaje("Personaje eliminado.");
        }
    }

    private void gestionarEquipo(Usuario u) {
        if (!u.tienePersonaje()) { vista.mostrarError("No tienes personaje."); return; }
        Personaje p = u.getPersonaje();

        boolean gestionando = true;
        while (gestionando) {
            vista.mostrarTitulo("Gestión de equipo de " + p.getNombre());

            // 1. Mostrar inventario actual
            vista.mostrarMensaje("Armas en inventario:");
            for (int i = 0; i < p.getArmas().size(); i++) {
                vista.mostrarMensaje("  " + (i+1) + ". " + p.getArmas().get(i));
            }
            vista.mostrarMensaje("Armaduras en inventario:");
            for (int i = 0; i < p.getArmaduras().size(); i++) {
                vista.mostrarMensaje("  " + (i+1) + ". " + p.getArmaduras().get(i));
            }
            vista.mostrarMensaje("Armas activas: " + p.getArmasActivas());
            vista.mostrarMensaje("Armadura activa: " + p.getArmaduraActiva());
            vista.mostrarMensaje("");

            // 2. Submenú limpio
            vista.mostrarMensaje("1. Coger equipo del catálogo base");
            vista.mostrarMensaje("2. Equipar un arma del inventario");
            vista.mostrarMensaje("3. Equipar una armadura del inventario");
            vista.mostrarMensaje("0. Volver al menú principal");

            String op = vista.leerPantalla("Opción");
            switch (op) {
                case "1" -> {
                    vista.mostrarMensaje("1. Ver Armas  2. Ver Armaduras");
                    String opCat = vista.leerPantalla("Opción");
                    if (opCat.equals("1")) {
                        for (int i = 0; i < catalogoArmasBase.size(); i++) {
                            vista.mostrarMensaje("  " + (i+1) + ". " + catalogoArmasBase.get(i).toString());
                        }
                        int idx = leerEnteroRango("Elige un arma (1-" + catalogoArmasBase.size() + ")", 1, catalogoArmasBase.size()) - 1;
                        p.addEquipo(catalogoArmasBase.get(idx));
                        vista.mostrarMensaje("Arma añadida al inventario.");
                    } else if (opCat.equals("2")) {
                        for (int i = 0; i < catalogoArmadurasBase.size(); i++) {
                            vista.mostrarMensaje("  " + (i+1) + ". " + catalogoArmadurasBase.get(i).toString());
                        }
                        int idx = leerEnteroRango("Elige una armadura (1-" + catalogoArmadurasBase.size() + ")", 1, catalogoArmadurasBase.size()) - 1;
                        p.addEquipo(catalogoArmadurasBase.get(idx));
                        vista.mostrarMensaje("Armadura añadida al inventario.");
                    } else {
                        vista.mostrarError("Opción de catálogo inválida.");
                    }
                }
                case "2" -> {
                    if (p.getArmas().isEmpty()) {
                        vista.mostrarError("No tienes armas en el inventario.");
                    } else {
                        int idx = leerEnteroRango("Índice de arma a equipar (1-" + p.getArmas().size() + ")", 1, p.getArmas().size()) - 1;
                        try {
                            p.equiparArma(p.getArmas().get(idx));
                            vista.mostrarMensaje("Arma equipada con éxito.");
                        } catch (Exception e) {
                            vista.mostrarError(e.getMessage());
                        }
                    }
                }
                case "3" -> {
                    if (p.getArmaduras().isEmpty()) {
                        vista.mostrarError("No tienes armaduras en el inventario.");
                    } else {
                        int idx = leerEnteroRango("Índice de armadura a equipar (1-" + p.getArmaduras().size() + ")", 1, p.getArmaduras().size()) - 1;
                        try {
                            p.equiparArmadura(p.getArmaduras().get(idx));
                            vista.mostrarMensaje("Armadura equipada con éxito.");
                        } catch (Exception e) {
                            vista.mostrarError(e.getMessage());
                        }
                    }
                }
                case "0" -> gestionando = false;
                default -> vista.mostrarError("Opción inválida.");
            }
        }
    }

    private void desafiarUsuario(Usuario u) {
        if (!u.tienePersonaje()) { vista.mostrarError("Necesitas un personaje."); return; }
        if (!u.getPersonaje().tieneEquipoActivo()) { vista.mostrarError("Tu personaje necesita equipo activo."); return; }
        String nickRetado = vista.leerPantalla("Nick del usuario a desafiar");
        int oro = leerEnteroRango("Oro a apostar (0-" + u.getPersonaje().getOro() + ")", 0, u.getPersonaje().getOro());
        try {
            Desafio d = desafiosManager.mandarDesafio(u, nickRetado, oro);
            vista.mostrarMensaje("Desafío enviado al operador para validación. Estado: " + d.getEstado().getNombre());
        } catch (Exception e) {
            vista.mostrarError(e.getMessage());
        }
    }

    private void manejarDesafioPendiente(Usuario u) {
        Desafio d = u.getDesafioPendiente();
        vista.mostrarTitulo("¡Tienes un desafío pendiente!");
        vista.mostrarMensaje("De: " + d.getDesafiante().getNick() + " | Oro apostado: " + d.getOroApostado());
        vista.mostrarMensaje("1. Aceptar  2. Rechazar (pagarás el 10%: " + (int)Math.ceil(d.getOroApostado()*0.1) + " oro)");
        String op = vista.leerPantalla("Opción");
        if (op.equals("1")) {
            // Opción de cambiar equipo antes de combatir
            gestionarEquipo(u);
            try {
                ResultadoCombate resultado = desafiosManager.aceptarDesafio(d);
                mostrarResultadoCombate(resultado);
            } catch (Exception e) { vista.mostrarError(e.getMessage()); }
        } else {
            desafiosManager.rechazarDesafio(d);
            vista.mostrarMensaje("Desafío rechazado. Se te ha cobrado el 10% del oro apostado.");
        }
    }

    private void mostrarResultadoCombate(ResultadoCombate r) {
        vista.mostrarTitulo("Resultado del combate");
        r.getLogRondas().forEach(vista::mostrarMensaje);
        vista.mostrarMensaje(r.toString());
    }

    private void mostrarHistorialOro(Usuario u) {
        vista.mostrarTitulo("Historial de oro de " + u.getNick());
        if (u.getHistorialOro().isEmpty()) { vista.mostrarMensaje("Sin movimientos."); return; }
        u.getHistorialOro().forEach(vista::mostrarMensaje);
    }

    private void mostrarRanking() {
        vista.mostrarTitulo("Ranking Global");
        List<Usuario> ranking = desafiosManager.getRankingGlobal();
        for (int i = 0; i < ranking.size(); i++) {
            Usuario u = ranking.get(i);
            int oro = u.tienePersonaje() ? u.getPersonaje().getOro() : 0;
            vista.mostrarMensaje((i+1) + ". " + u.getNick() + " - " + oro + " oro");
        }
    }

    // ─── Menú Operador ────────────────────────────────────────────────────────
    private void menuOperador(Operador op) {
        boolean sesionActiva = true;
        while (sesionActiva) {
            vista.mostrarTitulo("Menú Operador - " + op.getNick());
            vista.mostrarMensaje("1. Validar desafíos pendientes");
            vista.mostrarMensaje("2. Editar personaje");
            vista.mostrarMensaje("3. Añadir equipo a personaje");
            vista.mostrarMensaje("4. Añadir fortaleza/debilidad");
            vista.mostrarMensaje("5. Añadir esbirro");
            vista.mostrarMensaje("6. Bloquear usuario");
            vista.mostrarMensaje("7. Desbloquear usuario");
            vista.mostrarMensaje("0. Cerrar sesión");
            String opc = vista.leerPantalla("Opción");
            switch (opc) {
                case "1" -> validarDesafios();
                case "2" -> editarPersonaje();
                case "3" -> añadirEquipo();
                case "4" -> añadirModificador();
                case "5" -> añadirEsbirro();
                case "6" -> bloquearUsuario();
                case "7" -> desbloquearUsuario();
                case "0" -> sesionActiva = false;
                default  -> vista.mostrarError("Opción inválida.");
            }
        }
    }

    private void validarDesafios() {
        List<Desafio> pendientes = desafiosManager.getDesafiosPendientes().stream()
                .filter(Desafio::isPendienteValidacion).toList();
        if (pendientes.isEmpty()) { vista.mostrarMensaje("No hay desafíos pendientes de validación."); return; }
        for (Desafio d : pendientes) {
            vista.mostrarMensaje(d.toString());
            if (vista.leerBooleano("¿Validar este desafío?")) {
                desafiosManager.validarDesafio(d, new ArrayList<>(), new ArrayList<>());
                vista.mostrarMensaje("Desafío validado y publicado al usuario desafiado.");
            }
        }
    }

    private void editarPersonaje() {
        String nick = vista.leerPantalla("Nick del usuario");
        Usuario u = actoresManager.buscarUsuarioPorNick(nick);
        if (u == null || !u.tienePersonaje()) { vista.mostrarError("Usuario o personaje no encontrado."); return; }
        Personaje p = u.getPersonaje();
        vista.mostrarMensaje("Personaje actual: " + p);
        String nuevoNombre = vista.leerPantalla("Nuevo nombre (enter para conservar)");
        if (!nuevoNombre.isEmpty()) p.setNombre(nuevoNombre);
        String poderStr = vista.leerPantalla("Nuevo poder 1-5 (enter para conservar)");
        if (!poderStr.isEmpty()) { try { p.setPoder(Integer.parseInt(poderStr)); } catch (Exception e) { vista.mostrarError(e.getMessage()); } }
        vista.mostrarMensaje("Personaje actualizado: " + p);
    }

    private void añadirEquipo() {
        String nick = vista.leerPantalla("Nick del usuario");
        Usuario u = actoresManager.buscarUsuarioPorNick(nick);

        if (u == null || !u.tienePersonaje()) {
            vista.mostrarError("Usuario o personaje no encontrado.");
            return;
        }

        Personaje p = u.getPersonaje();

        vista.mostrarMensaje("1. Añadir arma  2. Añadir armadura");
        String tipo   = vista.leerPantalla("Tipo");
        String nombre = vista.leerPantalla("Nombre");

        // Pide los modificadores de ataque y defensa usando tu validador de rangos
        int modAtk    = leerEnteroRango("Modificador ataque (1-3)", 1, 3);
        int modDef    = leerEnteroRango("Modificador defensa (0-3)", 0, 3);

        if (tipo.equals("1")) {
            vista.mostrarMensaje("Manos: 1=Una mano  2=Dos manos");
            String manos = vista.leerPantalla("Tipo");
            Arma.TipoManos t = manos.equals("2") ? Arma.TipoManos.DOS_MANOS : Arma.TipoManos.UNA_MANO;

            p.addEquipo(new Arma(nombre, modAtk, modDef, t));
            vista.mostrarMensaje("Arma añadida.");
        } else {
            p.addEquipo(new Armadura(nombre, modAtk, modDef));
            vista.mostrarMensaje("Armadura añadida.");
        }
    }

    private void añadirModificador() {
        String nick = vista.leerPantalla("Nick del usuario");
        Usuario u = actoresManager.buscarUsuarioPorNick(nick);
        if (u == null || !u.tienePersonaje()) { vista.mostrarError("No encontrado."); return; }
        Personaje p = u.getPersonaje();
        vista.mostrarMensaje("1. Fortaleza  2. Debilidad");
        String tipo  = vista.leerPantalla("Tipo");
        String nombre = vista.leerPantalla("Nombre");
        int valor = leerEnteroRango("Valor (1-5)", 1, 5);
        Modificador m = new Modificador(nombre, valor);
        if (tipo.equals("1")) p.addFortaleza(m); else p.addDebilidad(m);
        vista.mostrarMensaje("Modificador añadido.");
    }

    private void añadirEsbirro() {
        String nick = vista.leerPantalla("Nick del usuario");
        Usuario u = actoresManager.buscarUsuarioPorNick(nick);
        if (u == null || !u.tienePersonaje()) { vista.mostrarError("No encontrado."); return; }
        Personaje p = u.getPersonaje();
        vista.mostrarMensaje("Tipo: 1=Humano  2=Ghoul  3=Demonio");
        String tipo  = vista.leerPantalla("Tipo");
        String nombre = vista.leerPantalla("Nombre del esbirro");
        int salud = leerEnteroRango("Salud (1-3)", 1, 3);
        try {
            Esbirro e;
            switch (tipo) {
                case "1" -> {
                    if (p instanceof Vampiro) { vista.mostrarError("Los vampiros no pueden tener esbirros humanos."); return; }
                    vista.mostrarMensaje("Lealtad: 1=Alta  2=Normal  3=Baja");
                    String l = vista.leerPantalla("Lealtad");
                    Humano.Lealtad lealtad = switch (l) { case "1" -> Humano.Lealtad.ALTA; case "3" -> Humano.Lealtad.BAJA; default -> Humano.Lealtad.NORMAL; };
                    e = new Humano(nombre, salud, lealtad);
                }
                case "2" -> {
                    int dep = leerEnteroRango("Dependencia (1-5)", 1, 5);
                    e = new Ghoul(nombre, salud, dep);
                }
                case "3" -> {
                    String pacto = vista.leerPantalla("Descripción del pacto");
                    e = new Demonio(nombre, salud, pacto);
                }
                default -> { vista.mostrarError("Tipo inválido."); return; }
            }
            p.addEsbirro(e);
            vista.mostrarMensaje("Esbirro añadido.");
        } catch (Exception ex) {
            vista.mostrarError(ex.getMessage());
        }
    }

    private void bloquearUsuario() {
        String nick = vista.leerPantalla("Nick del usuario a bloquear");
        Usuario u = actoresManager.buscarUsuarioPorNick(nick);
        if (u == null) { vista.mostrarError("Usuario no encontrado."); return; }
        u.bloquear();
        vista.mostrarMensaje("Usuario " + nick + " bloqueado.");
    }

    private void desbloquearUsuario() {
        String nick = vista.leerPantalla("Nick del usuario a desbloquear");
        Usuario u = actoresManager.buscarUsuarioPorNick(nick);
        if (u == null) { vista.mostrarError("Usuario no encontrado."); return; }
        u.desbloquear();
        vista.mostrarMensaje("Usuario " + nick + " desbloqueado.");
    }

    // ─── Persistencia ─────────────────────────────────────────────────────────
    private void guardarDatos() {
        persistencia.guardarUsuarios(usuariosRegistrados);
        persistencia.guardarOperadores(operadorRegistrados);
        persistencia.guardarCombates(historialCombates);
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────
    private int leerEnteroRango(String prompt, int min, int max) {
        while (true) {
            int v = vista.leerEntero(prompt);
            if (v >= min && v <= max) return v;
            vista.mostrarError("El valor debe estar entre " + min + " y " + max + ".");
        }
    }
    private void cargarCatalogoEquipo() {
        // Recuerda: Nombre, ModAtaque, ModDefensa, TipoManos
        catalogoArmas.add(new Arma("Espada de Plata", 2, 1, Arma.TipoManos.UNA_MANO));
        catalogoArmas.add(new Arma("Mandoble Pesado", 3, 0, Arma.TipoManos.DOS_MANOS));
        catalogoArmas.add(new Arma("Daga de Cazador", 1, 0, Arma.TipoManos.UNA_MANO));

        // Recuerda: Nombre, ModAtaque, ModDefensa
        catalogoArmaduras.add(new Armadura("Cota de Malla", 0, 2));
        catalogoArmaduras.add(new Armadura("Armadura de Placas", 0, 3));
        catalogoArmaduras.add(new Armadura("Túnica Ligera", 1, 1));
    }
}
