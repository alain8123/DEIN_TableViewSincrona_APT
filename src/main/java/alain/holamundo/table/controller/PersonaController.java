package alain.holamundo.table.controller;

import alain.holamundo.table.util.I18n;
import alain.holamundo.table.TableApp;
import alain.holamundo.table.dao.PersonaDAO;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class PersonaController {

    /* ---------- Elementos del FXML ---------- */
    @FXML private TextField tf_nombre;
    @FXML private TextField tf_Apellidos;
    @FXML private DatePicker dp_Fecha;
    @FXML private TableView<Persona> tv_personas;
    @FXML private TableColumn<Persona, Integer> col_id;
    @FXML private TableColumn<Persona, String> col_nombre;
    @FXML private TableColumn<Persona, String> col_apellidos;
    @FXML private TableColumn<Persona, LocalDate> col_fecha;
    @FXML private Button bt_anadir;
    @FXML private Button bt_eliminar;
    @FXML private Button bt_Restaurar;
    @FXML private ComboBox<String> cb_idioma;

    /* JavaFX inyecta aquí el ResourceBundle con los textos del idioma actual */
    @FXML private ResourceBundle resources;

    /* Idiomas disponibles: nombre que se muestra (en su propio idioma) y código */
    private static final String[] IDIOMAS_NOMBRE = {"Castellano", "English", "Euskara"};
    private static final String[] IDIOMAS_CODIGO = {"es", "en", "eu"};

    /* ---------- Datos ---------- */
    private final ObservableList<Persona> personas = FXCollections.observableArrayList();
    private final List<Persona> eliminadas = new ArrayList<>();   // filas borradas, para poder restaurarlas
    private final PersonaDAO personaDAO = new PersonaDAO();

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
        // Textos explicativos (tooltips) de los botones
        bt_anadir.setTooltip(new Tooltip(resources.getString("tooltip.add")));
        bt_eliminar.setTooltip(new Tooltip(resources.getString("tooltip.eliminar")));
        bt_Restaurar.setTooltip(new Tooltip(resources.getString("tooltip.restaurar")));

        configurarSelectorIdioma();

        // Cada columna sabe qué dato de Persona debe mostrar
        col_id.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getId()));
        col_nombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        col_apellidos.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getApellidos()));
        col_fecha.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getFecha()));

        // Permite seleccionar varias filas (Ctrl / Mayús + clic)
        tv_personas.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        tv_personas.setItems(personas);

        cargarDesdeBaseDeDatos();
    }

    /** Rellena el desplegable de idiomas y cambia la vista al elegir otro. */
    private void configurarSelectorIdioma() {
        cb_idioma.getItems().setAll(IDIOMAS_NOMBRE);
        cb_idioma.setTooltip(new Tooltip(resources.getString("tooltip.idioma")));

        // Selecciona el idioma actual
        String actual = I18n.getLocale().getLanguage();
        for (int i = 0; i < IDIOMAS_CODIGO.length; i++) {
            if (IDIOMAS_CODIGO[i].equals(actual)) {
                cb_idioma.getSelectionModel().select(i);
            }
        }

        // El listener se añade DESPUÉS de seleccionar el actual, para que no se dispare solo
        cb_idioma.getSelectionModel().selectedIndexProperty().addListener((obs, viejo, nuevo) -> {
            int i = nuevo.intValue();
            if (i < 0 || IDIOMAS_CODIGO[i].equals(I18n.getLocale().getLanguage())) {
                return;
            }
            // runLater: la vista se sustituye cuando el desplegable ya ha terminado su evento
            Platform.runLater(() -> {
                try {
                    I18n.setLocale(IDIOMAS_CODIGO[i]);
                    TableApp.loadView();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        });
    }

    /** Carga las filas desde MariaDB. Si la tabla está vacía, inserta los 5 datos de ejemplo una vez. */
    private void cargarDesdeBaseDeDatos() {
        try {
            if (!personaDAO.tieneDatos()) {
                personaDAO.insertar("Ashwin", "Sharan", LocalDate.of(2012, 10, 11));
                personaDAO.insertar("Advik", "Sharan", LocalDate.of(2012, 10, 11));
                personaDAO.insertar("Layne", "Estes", LocalDate.of(2011, 12, 16));
                personaDAO.insertar("Mason", "Boyd", LocalDate.of(2003, 4, 20));
                personaDAO.insertar("Babalu", "Sharan", LocalDate.of(1980, 1, 10));
            }

            personas.setAll(personaDAO.findAll());
        } catch (SQLException e) {
            mostrarError("error.conexion", e);
        }
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

        try {
            int id = personaDAO.insertar(nombre, apellidos, fecha);
            personas.add(new Persona(id, nombre, apellidos, fecha));
        } catch (SQLException e) {
            mostrarError("error.guardar", e);
            return;
        }

        // Limpiar los campos y volver al primero
        tf_nombre.clear();
        tf_Apellidos.clear();
        dp_Fecha.setValue(null);
        tf_nombre.requestFocus();
    }

    /* Botón Eliminar: borra las filas seleccionadas de la BD (las guarda por si se quieren restaurar) */
    @FXML
    protected void onEliminar() {
        // Se hace una copia porque la lista de seleccionadas cambia al borrar
        List<Persona> seleccionadas = new ArrayList<>(tv_personas.getSelectionModel().getSelectedItems());

        try {
            for (Persona p : seleccionadas) {
                personaDAO.eliminar(p.getId());
            }
        } catch (SQLException e) {
            mostrarError("error.eliminar", e);
            return;
        }

        eliminadas.addAll(seleccionadas);
        personas.removeAll(seleccionadas);
    }

    /* Botón Restaurar: vuelve a insertar en la BD las filas eliminadas (con id nuevo) */
    @FXML
    protected void onRestaurar() {
        List<Persona> restauradas = new ArrayList<>();

        try {
            for (Persona p : eliminadas) {
                int nuevoId = personaDAO.insertar(p.getNombre(), p.getApellidos(), p.getFecha());
                restauradas.add(new Persona(nuevoId, p.getNombre(), p.getApellidos(), p.getFecha()));
            }
        } catch (SQLException e) {
            mostrarError("error.restaurar", e);
            return;
        }

        personas.addAll(restauradas);
        eliminadas.clear();

        // Ordenar por id para que vuelvan a su sitio
        personas.sort((a, b) -> Integer.compare(a.getId(), b.getId()));
    }

    private void mostrarError(String claveCabecera, SQLException e) {
        e.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(resources.getString("error.title"));
        alert.setHeaderText(resources.getString(claveCabecera));
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }
}