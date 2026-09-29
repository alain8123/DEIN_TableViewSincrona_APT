package alain.holamundo.table.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Centraliza la conexión a MariaDB.
 *
 * NUNCA se hardcodean aquí usuario/contraseña/URL. Los datos se buscan,
 * por orden de prioridad, en:
 *
 *   1) Variables de entorno reales del sistema/IDE (DB_URL, DB_USER, DB_PASSWORD
 *      o bien DB_NAME/DB_USER/DB_PASSWORD/DB_PORT)
 *   2) El fichero .env que ya usas para el docker-compose:
 *        - ./docker/.env                        (raíz del proyecto, donde lo ejecutes)
 *        - ~/.holamundo/.env               (fuera del proyecto, alternativa)
 *
 * El .env es el MISMO fichero que usa docker-compose.yml (DB_NAME, DB_USER,
 * DB_PASSWORD, DB_PORT).
 */
public class DBConnection {

    private static final Path PROJECT_ENV = Paths.get("docker", ".env");
    private static final Path HOME_ENV =
            Paths.get(System.getProperty("user.home"), ".holamundo", ".env");

    public static Connection getConnection() throws SQLException {
        Map<String, String> vars = loadVars();

        String url = vars.get("DB_URL");
        String user = vars.get("DB_USER");
        String password = vars.get("DB_PASSWORD");

        // Si no hay DB_URL explícita, la construimos con DB_NAME / DB_PORT / DB_HOST
        if (url == null) {
            String host = vars.getOrDefault("DB_HOST", "localhost");
            String port = vars.getOrDefault("DB_PORT", "3306");
            String name = vars.get("DB_NAME");
            if (name != null) {
                url = "jdbc:mariadb://" + host + ":" + port + "/" + name;
            }
        }

        if (url == null || user == null || password == null) {
            throw new SQLException(
                    "Faltan datos de conexion a la base de datos. Define las variables de entorno "
                            + "DB_URL/DB_NAME, DB_USER y DB_PASSWORD, o crea el fichero .env en la raiz del "
                            + "proyecto (o " + HOME_ENV + ") con esas claves, igual que el que usa docker-compose.");
        }

        return DriverManager.getConnection(url, user, password);
    }

    private static Map<String, String> loadVars() {
        Map<String, String> vars = new HashMap<>();

        // 1) Variables de entorno reales (prioridad más alta)
        for (String key : new String[]{"DB_URL", "DB_HOST", "DB_PORT", "DB_NAME", "DB_USER", "DB_PASSWORD"}) {
            String value = System.getenv(key);
            if (value != null) {
                vars.put(key, value);
            }
        }

        // Si ya tenemos lo necesario por variables de entorno, no hace falta leer el .env
        if (tieneDatosCompletos(vars)) {
            return vars;
        }

        // 2) Fichero .env en la raíz del proyecto
        Map<String, String> fromFile = readEnvFile(PROJECT_ENV);
        if (fromFile == null) {
            // 3) Fichero .env externo, fuera del proyecto
            fromFile = readEnvFile(HOME_ENV);
        }

        if (fromFile != null) {
            // Las variables de entorno reales ya cargadas tienen prioridad sobre el fichero
            for (Map.Entry<String, String> entry : fromFile.entrySet()) {
                vars.putIfAbsent(entry.getKey(), entry.getValue());
            }
        }

        return vars;
    }

    private static boolean tieneDatosCompletos(Map<String, String> vars) {
        boolean tieneUrl = vars.get("DB_URL") != null || vars.get("DB_NAME") != null;
        return tieneUrl && vars.get("DB_USER") != null && vars.get("DB_PASSWORD") != null;
    }

    /** Lee un fichero .env sencillo (CLAVE=VALOR por linea, admite # como comentario). */
    private static Map<String, String> readEnvFile(Path path) {
        if (!Files.exists(path)) {
            return null;
        }

        Map<String, String> vars = new HashMap<>();
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                int eq = line.indexOf('=');
                if (eq <= 0) {
                    continue;
                }
                String key = line.substring(0, eq).trim();
                String value = line.substring(eq + 1).trim();

                // Quita comillas si las tiene: DB_PASSWORD="algo"
                if (value.length() >= 2
                        && ((value.startsWith("\"") && value.endsWith("\""))
                        || (value.startsWith("'") && value.endsWith("'")))) {
                    value = value.substring(1, value.length() - 1);
                }

                vars.put(key, value);
            }
        } catch (IOException e) {
            System.err.println("No se pudo leer " + path + ": " + e.getMessage());
            return null;
        }
        return vars;
    }
}