#  Videojuego de Combate

Proyecto grupal para la asignatura **Metodología de la Programación** (URJC).  
Grupo 4: Nuria Amrani Villuendas, Ignacio Roncero Medina, Rim Afoud.


---

## 1. Descripción del proyecto

Mal Presagio es un videojuego de combate por consola en el que los jugadores crean personajes de distintas razas (Vampiro, Licántropo o Cazador) y se desafían entre sí apostando oro. Un operador actúa como administrador del sistema, validando los desafíos antes de que lleguen al rival.

El flujo principal es el siguiente:
- Los usuarios se registran e inician sesión.
- Cada usuario crea un personaje y le asigna equipo (armas y armadura).
- Un usuario puede desafiar a otro apostando oro; el desafío pasa primero por el operador para ser validado.
- El usuario desafiado acepta o rechaza. Si acepta, se ejecuta el combate automáticamente por rondas.
- El ganador recibe el oro apostado; el perdedor queda bloqueado 24 horas.
- Los datos se guardan automáticamente entre sesiones mediante serialización Java.

---

## 2. Requisitos previos

- **Java JDK 21 o superior** instalado en el sistema.
- Comprobar que Java está instalado ejecutando en la terminal:

```bash
java -version
javac -version
```


No se necesita ninguna librería externa ni framework. El proyecto usa únicamente la biblioteca estándar de Java.

---

## 3. Estructura del proyecto

```
Implementacion-MP-PRUEBA/
├── src/
│   ├── metprog/
│   │   ├── Main.java
│   │   ├── AplicacionJuego.java
│   │   ├── combate/
│   │   ├── desafio/
│   │   ├── model/
│   │   ├── sistema/
│   │   └── vista/
│   └── persistencia/
│       └── GestorPersistencia.java
├── data/              ← se crea automáticamente al ejecutar
└── README.md
```

---


## 4. Cómo compilar y ejecutar


### Opción A — Con IntelliJ IDEA (recomendado)

1. Abrir IntelliJ IDEA → **File → Open** → seleccionar la carpeta raíz del proyecto.
2. Clic derecho sobre la carpeta `src` → **Mark Directory as → Sources Root**.
3. Clic derecho sobre `src/metprog/Main.java` → **Run 'Main'**.

El programa arrancará directamente en la consola de IntelliJ.

### Opción B — Con línea de comandos

Desde la **raíz del proyecto** (carpeta `Implementacion-MP-PRUEBA/`):

**Paso 1 — Crear carpeta de salida y compilar:**

```bash
mkdir -p out
javac -d out src/metprog/Main.java src/metprog/**/*.java src/persistencia/GestorPersistencia.java
```

**Paso 2 — Ejecutar:**

```bash
java -cp out metprog.Main
```

---


## 5. Qué pedirá el programa al usuario

El programa funciona completamente por consola. Al ejecutarse pedirá:

### Pantalla de inicio
```
1. Iniciar sesión
2. Registrarse
0. Salir
```

### Si te registras
- Tipo de cuenta: **1** para Jugador, **2** para Operador
- Nombre completo
- Nick (único en el sistema)
- Contraseña (entre **8 y 12 caracteres**)

### Si eres Jugador, el menú principal ofrece
1. Crear personaje (Vampiro / Licántropo / Cazador)
2. Dar de baja personaje
3. Gestionar equipo activo (armas y armadura)
4. Desafiar a otro usuario (introduce su nick y el oro a apostar)
5. Consultar historial de oro
6. Consultar ranking global

> ⚠️ Si tienes un desafío pendiente al iniciar sesión, el programa te lo mostrará y deberás aceptarlo o rechazarlo antes de hacer cualquier otra cosa.

### Si eres Operador, el menú ofrece
1. Validar desafíos pendientes
2. Editar personaje de un usuario
3. Añadir equipo a un personaje
4. Añadir fortaleza/debilidad a un personaje
5. Añadir esbirro a un personaje
6. Bloquear usuario
7. Desbloquear usuario

### Datos persistentes
La carpeta `data/` se crea automáticamente en la raíz del proyecto y almacena los datos entre sesiones. No es necesario hacer nada con ella manualmente.

---

## 6. Notas adicionales

- El combate se ejecuta de forma automática al aceptar un desafío; no requiere ninguna entrada del usuario durante las rondas.
- Un usuario bloqueado no puede desafiar ni ser desafiado. El operador puede desbloquearlo manualmente desde su menú.
- Los Vampiros no pueden tener esbirros de tipo Humano.
- Para probar el flujo completo se necesitan al menos **dos cuentas de jugador** y **una de operador**.









---

## 6. Notas adicionales

- El combate se ejecuta de forma automática al aceptar un desafío; no requiere ninguna entrada del usuario durante las rondas.
- Un usuario bloqueado no puede desafiar ni ser desafiado. El operador puede desbloquearlo manualmente desde su menú.
- Los Vampiros no pueden tener esbirros de tipo Humano.
- Para probar el flujo completo se necesitan al menos **dos cuentas de jugador** y **una de operador**.
