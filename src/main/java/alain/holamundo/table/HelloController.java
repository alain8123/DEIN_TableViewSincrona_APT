package alain.holamundo.table;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HelloController {

    /* ---------- Elementos del FXML ---------- */
    @FXML private TextField tf_nombre;
    @FXML private TextField tf_Apellidos;
    @FXML private DatePicker dp_Fecha;
    @FXML private TableView<Persona> tv_personas;
    @FXML private TableColumn<Persona, Integer> col_id;
    @FXML private TableColumn<Persona, String> col_nombre;
    @FXML private TableColumn<Persona, String> col_apellidos;
    @FXML private TableColumn<Persona, LocalDate> col_fecha;

    /* ---------- Datos ---------- */
    private final ObservableList<Persona> personas = FXCollections.observableArrayList();
    private final List<Persona> eliminadas = new ArrayList<>();   // filas borradas, para poder restaurarlas
    private int siguienteId = 6;                                  // los ids 1..5 ya están en la tabla

    /* Clase sencilla que representa cada fila de la tabla */
    public static class Persona {
        private final int id;
        private final String nombre;
        private final String apellidos;
        private final LocalDate fecha;

        public Persona(int id, String nombre, String apellidos, LocalDate fecha) {
            this.id = id;
            this.nombre = nombre;
            this.apellidos = apellidos;
            this.fecha = fecha;
        }

        public int getId() { return id; }
        public String getNombre() { return nombre; }
        public String getApellidos() { return apellidos; }
        public LocalDate getFecha() { return fecha; }
    }

    /* Se ejecuta automáticamente al cargar el FXML */
    @FXML
    public void initialize() {
        // Cada columna sabe qué dato de Persona debe mostrar
        col_id.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getId()));
        col_nombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        col_apellidos.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getApellidos()));
        col_fecha.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getFecha()));

        // Permite seleccionar varias filas (Ctrl / Mayús + clic)
        tv_personas.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Datos iniciales
        personas.add(new Persona(1, "Ashwin", "Sharan", LocalDate.of(2012, 10, 11)));
        personas.add(new Persona(2, "Advik", "Sharan", LocalDate.of(2012, 10, 11)));
        personas.add(new Persona(3, "Layne", "Estes", LocalDate.of(2011, 12, 16)));
        personas.add(new Persona(4, "Mason", "Boyd", LocalDate.of(2003, 4, 20)));
        personas.add(new Persona(5, "Babalu", "Sharan", LocalDate.of(1980, 1, 10)));

        tv_personas.setItems(personas);
    }

    /* Botón Add: añade una fila con los datos escritos */
    @FXML
    protected void onAnadir() {
        String nombre = tf_nombre.getText().trim();
        String apellidos = tf_Apellidos.getText().trim();
        LocalDate fecha = dp_Fecha.getValue();

        // Si falta algún dato no se añade nada
        if (nombre.isEmpty() || apellidos.isEmpty() || fecha == null) {
            return;
        }

        personas.add(new Persona(siguienteId, nombre, apellidos, fecha));
        siguienteId++;

        // Limpiar los campos y volver al primero
        tf_nombre.clear();
        tf_Apellidos.clear();
        dp_Fecha.setValue(null);
        tf_nombre.requestFocus();
    }

    /* Botón Eliminar: borra las filas seleccionadas (las guarda por si se quieren restaurar) */
    @FXML
    protected void onEliminar() {
        // Se hace una copia porque la lista de seleccionadas cambia al borrar
        List<Persona> seleccionadas = new ArrayList<>(tv_personas.getSelectionModel().getSelectedItems());

        eliminadas.addAll(seleccionadas);
        personas.removeAll(seleccionadas);
    }

    /* Botón Restaurar: devuelve a la tabla las filas eliminadas */
    @FXML
    protected void onRestaurar() {
        personas.addAll(eliminadas);
        eliminadas.clear();

        // Ordenar por id para que vuelvan a su sitio
        personas.sort((a, b) -> Integer.compare(a.getId(), b.getId()));
    }
}