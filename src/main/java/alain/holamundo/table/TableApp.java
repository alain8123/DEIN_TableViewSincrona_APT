package alain.holamundo.table;

import alain.holamundo.table.util.I18n;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.util.ResourceBundle;

public class TableApp extends Application {

    private static final int[] ICON_SIZES = {16, 24, 32, 48, 64, 128, 256};
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        stage.setMinHeight(400);
        stage.setMinWidth(500);

        for (int size : ICON_SIZES) {
            InputStream stream = TableApp.class.getResourceAsStream("images/icon-" + size + ".png");
            if (stream != null) {
                stage.getIcons().add(new Image(stream));
            }
        }

        I18n.setLocale("es");   // idioma inicial
        loadView();
        stage.show();
    }

    /** Carga (o recarga) la vista con el idioma actual de I18n. */
    public static void loadView() throws IOException {
        ResourceBundle bundle = I18n.bundle();
        FXMLLoader fxmlLoader = new FXMLLoader(TableApp.class.getResource("/alain/holamundo/table/view/hello-view.fxml"), bundle);
        Parent root = fxmlLoader.load();

        // Mantiene el tamaño de la ventana al cambiar de idioma
        double w = primaryStage.getScene() != null ? primaryStage.getScene().getWidth() : 320;
        double h = primaryStage.getScene() != null ? primaryStage.getScene().getHeight() : 240;

        primaryStage.setTitle(bundle.getString("app.title"));
        primaryStage.setScene(new Scene(root, w, h));
    }
}