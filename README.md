# 📌 TableView Sincrona

---

## 📖 Descripción
Aplicación JavaFX con una tabla para añadir, eliminar y restaurar personas. Los datos se guardan en una base de datos **MariaDB** ejecutada con **Docker**, y la interfaz está disponible en **castellano, inglés y euskera**.

---

## 🖼️ Funcionamiento

La ventana permite gestionar una lista de personas mostrada en un `TableView` con las columnas **ID**, **Nombre**, **Apellidos** y **Fecha_Nacimiento**, cargadas desde la tabla `personas` de MariaDB.

| Elemento | Acción |
|----------|--------|
| **Nombre / Apellidos / Fecha de nacimiento** | Campos donde se escriben los datos de la nueva persona. |
| **Botón `Add`** | Inserta la persona en la base de datos y la añade a la tabla con el ID autogenerado. Si falta algún dato, no añade nada. Después limpia los campos. |
| **Botón `Eliminar Seleccion`** | Borra de la base de datos y de la tabla las filas seleccionadas (se pueden seleccionar varias con `Ctrl` o `Mayús`). |
| **Botón `Restaurar Seleccion`** | Vuelve a insertar en la base de datos las filas eliminadas (con un ID nuevo) y las muestra ordenadas por ID. |
| **Desplegable de idioma** | Cambia entre Castellano, English y Euskara; recarga la vista manteniendo el tamaño de la ventana. |

La primera vez que se ejecuta con la tabla vacía, se insertan 5 personas de ejemplo. Las filas eliminadas que se pueden restaurar solo se guardan en memoria durante la sesión.

---

## 📂 Estructura

### 1. Código fuente
```plaintext
📁 /src/main/java/alain/holamundo/table
    ✅ Launcher.java → Punto de entrada: lanza la aplicación JavaFX
    ✅ TableApp.java → Aplicación JavaFX: configura la ventana, los iconos y carga (o recarga) la vista

📁 /src/main/java/alain/holamundo/table/controller
    ✅ PersonaController.java → Controlador: inicializa la tabla, el selector de idioma y programa las acciones de los tres botones

📁 /src/main/java/alain/holamundo/table/dao
    ✅ PersonaDAO.java → Acceso a datos de la tabla "personas" (listar, insertar, eliminar, comprobar si hay datos)

📁 /src/main/java/alain/holamundo/table/db
    ✅ DBConnection.java → Crea la conexión a MariaDB leyendo las credenciales de variables de entorno o de un fichero .env

📁 /src/main/java/alain/holamundo/table/util
    ✅ I18n.java → Gestiona el idioma actual y devuelve el ResourceBundle con los textos
```

### 2. Recursos
```plaintext
📁 /src/main/resources/alain/holamundo/table
    ✅ view/hello-view.fxml → Diseño de la ventana (campos de texto, selector de fecha, selector de idioma, botones y tabla)
    ✅ i18n/messages*.properties → Textos de la interfaz en cada idioma (es, en, eu)
    ✅ images/icon-*.png → Iconos de la aplicación (16 a 256 px)
```

### 3. Base de datos y Docker
```plaintext
📁 /docker
    ✅ docker-compose.yml → Define el contenedor de MariaDB (mariadb-dein) y su volumen de datos
    ✅ 01_schema.sql → Crea la tabla "personas" la primera vez que se inicia el contenedor
    ✅ env.example → Plantilla de variables de entorno (se copia como .env)
    ✅ .env → Credenciales reales de la base de datos (no se sube a Git)
    ✅ README_docker.md → Notas adicionales sobre Docker
```

### 4. Clases y elementos principales

```plaintext
✅ Persona (clase interna de PersonaController) → Representa una fila: id, nombre, apellidos y fecha
✅ personas (ObservableList) → Lista de filas que muestra la tabla; al modificarla, la tabla se actualiza sola
✅ eliminadas (List) → Guarda las filas borradas para poder restaurarlas
✅ initialize() → Se ejecuta al cargar el FXML: configura las columnas, el selector de idioma y carga los datos desde la base de datos
✅ onAnadir() / onEliminar() / onRestaurar() → Métodos enlazados a los botones con onAction en el FXML
✅ TableApp.loadView() → Carga el FXML con el ResourceBundle del idioma actual; se vuelve a llamar al cambiar de idioma
```

### 5. Bibliotecas adicionales

```plaintext
✅ mariadb-java-client 3.5.10 → Driver JDBC para conectar con MariaDB
✅ JavaFX 21.0.6 (controls y fxml) → Interfaz gráfica
✅ JUnit Jupiter 5.12.1 → Pruebas (ámbito test)
```

---

## ⚠️ Solución de problemas

```plaintext
✅ El controlador no se encontraba al abrir la ventana → Se añadió fx:controller con el paquete correcto (alain.holamundo.table.controller.PersonaController) en el VBox raíz del FXML.
✅ Los campos y la tabla salían como null → Cada elemento del FXML necesita un fx:id que coincida exactamente con el nombre de la variable anotada con @FXML.
✅ Un fx:id con la letra ñ (bt_Añadir) podía dar problemas → Se renombró a bt_anadir.
✅ Las columnas salían vacías → Se asignó a cada columna un cellValueFactory con una expresión lambda que indica qué dato de Persona mostrar.
✅ Al eliminar varias filas fallaba o borraba mal → Se copia primero la lista de filas seleccionadas en una nueva lista antes de borrarlas, porque la selección cambia mientras se elimina.
✅ Al restaurar, las filas aparecían al final → Después de restaurar, la lista se ordena por ID.
✅ "Location is not set" al arrancar → Tras reorganizar en paquetes, la ruta del FXML en TableApp.loadView() debía coincidir con su ubicación real (/alain/holamundo/table/view/hello-view.fxml).
✅ La tabla "personas" no se creaba con docker compose → El bloque volumes estaba mal indentado (dentro de environment) y faltaban espacios tras los guiones. Además, el script solo se ejecuta si el volumen está vacío: docker compose down -v y volver a levantar.
✅ "Faltan datos de conexion a la base de datos" → No se encontró el .env. Comprobar que existe docker/.env y ejecutar la aplicación desde la raíz del proyecto.
```

---

## ⚙️ Requisitos de ejecución

```plaintext
✅ Lenguaje: Java 21 (maven.compiler.release = 21)
✅ Bibliotecas: JavaFX 21.0.6 (controls y fxml) y driver MariaDB (se descargan solas con Maven)
✅ Base de datos: MariaDB en Docker (Docker Desktop o Docker Engine con Docker Compose)
✅ IDE utilizado: IntelliJ IDEA (proyecto JavaFX con Maven)
✅ Maven: no hace falta instalarlo, el proyecto incluye Maven Wrapper (mvnw / mvnw.cmd)
✅ Sistema operativo probado: Windows
✅ El proyecto no usa module-info.java
```

---

## 🚀 Instalación y ejecución

```plaintext
✅ Paso 1: Abrir el proyecto en el IDE y comprobar que Maven ha descargado las dependencias.
✅ Paso 2: Crear el fichero de configuración: copiar docker/env.example como docker/.env y rellenar
           DB_PORT, DB_ROOT_PASSWORD, DB_NAME, DB_USER y DB_PASSWORD.
✅ Paso 3: Levantar la base de datos desde la carpeta docker con: docker compose up -d
           (crea el contenedor mariadb-dein y la tabla "personas").
✅ Paso 4: Ejecutar Launcher desde la raíz del proyecto, que carga la aplicación.
✅ Paso 5: Escribir nombre, apellidos y fecha, y pulsar "Add" para añadir una fila.
✅ Paso 6: Seleccionar una o varias filas y pulsar "Eliminar Seleccion" para borrarlas.
✅ Paso 7: Pulsar "Restaurar Seleccion" para recuperar las filas eliminadas.
✅ Paso 8: Usar el desplegable de idioma para cambiar entre Castellano, English y Euskara.
```

> ✏️ Con Maven también se puede ejecutar desde terminal, en la raíz del proyecto, con `./mvnw javafx:run` pero primero se debera hacer un compile (`mvnw.cmd javafx:run` en Windows).

> ✏️ Para reiniciar la base de datos desde cero (se pierden los datos): `docker compose down -v` y después `docker compose up -d`.

> ✏️ La conexión busca las credenciales, por orden de prioridad, en: variables de entorno del sistema (`DB_URL` o `DB_NAME`, `DB_USER`, `DB_PASSWORD`, y opcionalmente `DB_HOST` y `DB_PORT`), en `docker/.env` y en `~/.holamundo/.env`.

---

## ✨ Autor/a

```plaintext
👤 Alain Paunero
```
