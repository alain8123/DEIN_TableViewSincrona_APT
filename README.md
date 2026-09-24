# 📌 IDENTIFICACIÓN DE LA ACTIVIDAD: UDn.ACTm

---

## 📖 Descripción
Aplicación JavaFX con una tabla para añadir, eliminar y restaurar personas.

---

## 🖼️ Funcionamiento

La ventana permite gestionar una lista de personas mostrada en un `TableView` con las columnas **ID**, **Nombre**, **Apellidos** y **Fecha_Nacimiento**.

| Elemento | Acción |
|----------|--------|
| **Nombre / Apellidos / Fecha de nacimiento** | Campos donde se escriben los datos de la nueva persona. |
| **Botón `Add`** | Añade una fila nueva con los datos escritos y un ID automático. Si falta algún dato, no añade nada. Después limpia los campos. |
| **Botón `Eliminar Seleccion`** | Elimina de la tabla las filas seleccionadas (se puede seleccionar varias con `Ctrl` o `Mayús`). |
| **Botón `Restaurar Seleccion`** | Devuelve a la tabla las filas eliminadas, ordenadas por ID. |

La tabla arranca con 5 personas de ejemplo (IDs del 1 al 5). Las nuevas filas continúan desde el ID 6.

---

## 📂 Estructura

### 1. Código fuente
```plaintext
📁 /src/main/java/com/example/demo
    ✅ HelloController.java → Controlador: inicializa la tabla y programa las acciones de los tres botones

📁 /src/main/resources/com/example/demo
    ✅ hello-view.fxml → Diseño de la ventana (campos de texto, selector de fecha, botones y tabla)
```

### 2. Clases y elementos principales

```plaintext
✅ Persona (clase interna de HelloController) → Representa una fila: id, nombre, apellidos y fecha
✅ personas (ObservableList) → Lista de filas que muestra la tabla; al modificarla, la tabla se actualiza sola
✅ eliminadas (List) → Guarda las filas borradas para poder restaurarlas
✅ initialize() → Se ejecuta al cargar el FXML: configura las columnas y carga los datos iniciales
✅ onAnadir() / onEliminar() / onRestaurar() → Métodos enlazados a los botones con onAction en el FXML
```

### 3. Bibliotecas adicionales

```plaintext
No se han utilizado bibliotecas adicionales (solo JavaFX).
```

---

## ⚠️ Solución de problemas

```plaintext
✅ El controlador no se encontraba al abrir la ventana → Se añadió fx:controller con el paquete correcto en el VBox raíz del FXML.
✅ Los campos y la tabla salían como null → Cada elemento del FXML necesita un fx:id que coincida exactamente con el nombre de la variable anotada con @FXML.
✅ Un fx:id con la letra ñ (bt_Añadir) podía dar problemas → Se renombró a bt_anadir.
✅ Las columnas salían vacías → Se asignó a cada columna un cellValueFactory con una expresión lambda que indica qué dato de Persona mostrar.
✅ Al eliminar varias filas fallaba o borraba mal → Se copia primero la lista de filas seleccionadas en una nueva lista antes de borrarlas, porque la selección cambia mientras se elimina.
✅ Al restaurar, las filas aparecían al final → Después de restaurar, la lista se ordena por ID.
```

---

## ⚙️ Requisitos de ejecución

```plaintext
✅ Lenguaje: Java 17 o superior
✅ Biblioteca: JavaFX (controls y fxml)
✅ IDE utilizado: IntelliJ IDEA (proyecto JavaFX con Maven o Gradle)
✅ Sistema operativo probado: (indicar el que hayas usado)
✅ Si existe module-info.java: debe incluir "opens com.example.demo to javafx.fxml;"
```

---

## 🚀 Instalación y ejecución

```plaintext
✅ Paso 1: Abrir el proyecto en el IDE y comprobar que JavaFX está configurado.
✅ Paso 2: Ejecutar el Luncher, que carga la aplicacion.
✅ Paso 3: Escribir nombre, apellidos y fecha, y pulsar "Add" para añadir una fila.
✅ Paso 4: Seleccionar una o varias filas y pulsar "Eliminar Seleccion" para borrarlas.
✅ Paso 5: Pulsar "Restaurar Seleccion" para recuperar las filas eliminadas.
```

> ✏️ Con Maven también se puede ejecutar desde terminal con `mvn javafx:run`.

---

## ✨ Autor/a

```plaintext
👤 Alain Paunero
```
