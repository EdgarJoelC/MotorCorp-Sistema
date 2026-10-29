package dao;

import conexion.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MecanicoDAO {

    public MecanicoDAO() {
        asegurarColumnas();
    }

    private void asegurarColumnas() {
        String sql = "ALTER TABLE mecanicos ADD COLUMN IF NOT EXISTS telefono VARCHAR(20); " +
                "ALTER TABLE mecanicos ADD COLUMN IF NOT EXISTS estado VARCHAR(20) DEFAULT 'Activo';";
        try (Connection conn = ConexionBD.conectar();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Aviso al verificar estructura de mecanicos: " + e.getMessage());
        }
    }

    public boolean registrar(String nombre, String especialidad, String telefono) {
        String sql = "INSERT INTO mecanicos (nombre_mecanico, especialidad, estado, telefono) VALUES (?, ?, 'Activo', ?)";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombre.trim());
            pstmt.setString(2, especialidad.trim());
            pstmt.setString(3, (telefono == null || telefono.trim().isEmpty()) ? "-" : telefono.trim());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error SQL al registrar mecánico: " + e.getMessage());
            // Intento alternativo en caso de que la columna sea 'nombre' en vez de 'nombre_mecanico'
            String sqlAlt = "INSERT INTO mecanicos (nombre, especialidad, estado, telefono) VALUES (?, ?, 'Activo', ?)";
            try (Connection conn2 = ConexionBD.conectar();
                 PreparedStatement pstmt2 = conn2.prepareStatement(sqlAlt)) {
                pstmt2.setString(1, nombre.trim());
                pstmt2.setString(2, especialidad.trim());
                pstmt2.setString(3, (telefono == null || telefono.trim().isEmpty()) ? "-" : telefono.trim());
                return pstmt2.executeUpdate() > 0;
            } catch (SQLException ex) {
                System.err.println("Error SQL alternativo: " + ex.getMessage());
                return false;
            }
        }
    }

    public boolean modificar(int id, String nombre, String especialidad, String telefono) {
        String sql = "UPDATE mecanicos SET nombre_mecanico = ?, especialidad = ?, telefono = ? WHERE id_mecanico = ?";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombre.trim());
            pstmt.setString(2, especialidad.trim());
            pstmt.setString(3, (telefono == null || telefono.trim().isEmpty()) ? "-" : telefono.trim());
            pstmt.setInt(4, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error SQL al modificar mecánico: " + e.getMessage());
            String sqlAlt = "UPDATE mecanicos SET nombre = ?, especialidad = ?, telefono = ? WHERE id_mecanico = ?";
            try (Connection conn2 = ConexionBD.conectar();
                 PreparedStatement pstmt2 = conn2.prepareStatement(sqlAlt)) {
                pstmt2.setString(1, nombre.trim());
                pstmt2.setString(2, especialidad.trim());
                pstmt2.setString(3, (telefono == null || telefono.trim().isEmpty()) ? "-" : telefono.trim());
                pstmt2.setInt(4, id);
                return pstmt2.executeUpdate() > 0;
            } catch (SQLException ex) {
                return false;
            }
        }
    }

    public List<Object[]> listar() {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT * FROM mecanicos ORDER BY 1 ASC";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            ResultSetMetaData meta = rs.getMetaData();
            int total = meta.getColumnCount();
            while (rs.next()) {
                Object[] fila = new Object[total];
                for (int i = 1; i <= total; i++) {
                    fila[i - 1] = rs.getObject(i);
                }
                lista.add(fila);
            }
        } catch (SQLException e) {
            System.err.println("Error SQL al listar mecánicos: " + e.getMessage());
        }
        return lista;
    }

    public List<Object[]> listarServiciosPorMecanico(int idMecanico) {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT o.id_orden, " +
                "COALESCE(c.nombre, 'Cliente #' || o.id_cliente) AS cliente, " +
                "COALESCE(o.vehiculo, '-') AS vehiculo, " +
                "COALESCE(o.placa, '-') AS placa, " +
                "o.descripcion_problema, " +
                "o.estado " +
                "FROM ordenes_taller o " +
                "LEFT JOIN clientes c ON o.id_cliente = c.id_cliente " +
                "WHERE o.id_mecanico = ? AND TRIM(LOWER(o.estado)) IN ('recepcionado', 'en proceso') " +
                "ORDER BY o.id_orden ASC";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idMecanico);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Object[]{
                            rs.getInt("id_orden"),
                            rs.getString("cliente"),
                            rs.getString("vehiculo"),
                            rs.getString("placa"),
                            rs.getString("descripcion_problema"),
                            rs.getString("estado")
                    });
                }
            }
        } catch (SQLException e) {
            System.err.println("Error SQL al listar servicios del mecánico: " + e.getMessage());
        }
        return lista;
    }

    public List<Object[]> listarParaCombo() {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT * FROM mecanicos ORDER BY 1 ASC";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            ResultSetMetaData meta = rs.getMetaData();
            int total = meta.getColumnCount();
            while (rs.next()) {
                int id = rs.getInt(1);
                String nombre = total >= 2 && rs.getString(2) != null ? rs.getString(2) : "Mecánico #" + id;
                String esp = total >= 3 && rs.getString(3) != null ? rs.getString(3) : "General";
                lista.add(new Object[]{id, nombre, esp});
            }
        } catch (SQLException e) {
            System.err.println("Error SQL al listar para combo: " + e.getMessage());
        }
        return lista;
    }

    public String[] obtenerColumnasTabla() {
        String sql = "SELECT * FROM mecanicos LIMIT 1";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            ResultSetMetaData meta = rs.getMetaData();
            int total = meta.getColumnCount();
            String[] cols = new String[total];
            for (int i = 1; i <= total; i++) {
                cols[i - 1] = meta.getColumnLabel(i).toUpperCase();
            }
            return cols;
        } catch (SQLException e) {
            return new String[]{"ID", "NOMBRE", "ESPECIALIDAD", "ESTADO"};
        }
    }
}