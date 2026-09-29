package alain.holamundo.table.dao;

import alain.holamundo.table.controller.PersonaController;
import alain.holamundo.table.db.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a datos de la tabla "personas" en MariaDB.
 * Ver schema.sql para la definicion de la tabla.
 */
public class PersonaDAO {

    public List<PersonaController.Persona> findAll() throws SQLException {
        String sql = "SELECT id, nombre, apellidos, fecha_nacimiento FROM personas ORDER BY id";
        List<PersonaController.Persona> lista = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new PersonaController.Persona(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("apellidos"),
                        rs.getDate("fecha_nacimiento").toLocalDate()
                ));
            }
        }
        return lista;
    }

    /** Inserta una persona y devuelve el id autogenerado. */
    public int insertar(String nombre, String apellidos, LocalDate fecha) throws SQLException {
        String sql = "INSERT INTO personas (nombre, apellidos, fecha_nacimiento) VALUES (?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, nombre);
            ps.setString(2, apellidos);
            ps.setDate(3, Date.valueOf(fecha));
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        return -1;
    }

    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM personas WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    /** true si la tabla ya tiene filas (para no duplicar los datos iniciales). */
    public boolean tieneDatos() throws SQLException {
        String sql = "SELECT COUNT(*) FROM personas";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1) > 0;
        }
    }
}
