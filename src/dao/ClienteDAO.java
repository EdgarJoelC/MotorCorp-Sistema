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

public class ClienteDAO {

    public ClienteDAO() {
        asegurarColumnas();
    }

    private void asegurarColumnas() {
        String[] alteraciones = {
                "ALTER TABLE clientes ADD COLUMN IF NOT EXISTS telefono VARCHAR(20)",
                "ALTER TABLE clientes ADD COLUMN IF NOT EXISTS vehiculo VARCHAR(100)",
                "ALTER TABLE clientes ADD COLUMN IF NOT EXISTS placa VARCHAR(20)"
        };
        try (Connection conn = ConexionBD.conectar();
             Statement stmt = conn.createStatement()) {
            for (String sql : alteraciones) {
                try {
                    stmt.execute(sql);
                } catch (SQLException ignored) {}
            }
        } catch (SQLException e) {
            System.err.println("Aviso columnas clientes: " + e.getMessage());
        }
    }

    public String[] obtenerColumnasTabla() {
        String sql = "SELECT * FROM clientes LIMIT 1";
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
            return new String[]{"ID_CLIENTE", "NOMBRE", "DNI", "TELEFONO", "VEHICULO", "PLACA"};
        }
    }

    // --- Sobrecargas de Registrar ---
    public boolean registrar(String nombre, String dni, String telefono, String vehiculo, String placa) {
        String sql = "INSERT INTO clientes (nombre, dni, telefono, vehiculo, placa) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombre != null ? nombre.trim() : "");
            pstmt.setString(2, dni != null ? dni.trim() : "");
            pstmt.setString(3, telefono != null ? telefono.trim() : "-");
            pstmt.setString(4, vehiculo != null ? vehiculo.trim() : "-");
            pstmt.setString(5, placa != null ? placa.trim().toUpperCase() : "-");
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error SQL al registrar cliente: " + e.getMessage());
            return false;
        }
    }

    public boolean registrar(String nombre, String dni, String telefono, String campo4) {
        return registrar(nombre, dni, telefono, campo4, "-");
    }

    public boolean registrar(String nombre, String dni, String telefono) {
        return registrar(nombre, dni, telefono, "-", "-");
    }

    public boolean registrar(String nombre, String dni, String telefono, String c4, String c5, String c6) {
        return registrar(nombre, dni, telefono, c5, c6);
    }

    // --- Sobrecargas de Modificar ---
    public boolean modificar(int id, String nombre, String dni, String telefono, String vehiculo, String placa) {
        String sql = "UPDATE clientes SET nombre = ?, dni = ?, telefono = ?, vehiculo = ?, placa = ? WHERE id_cliente = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombre != null ? nombre.trim() : "");
            pstmt.setString(2, dni != null ? dni.trim() : "");
            pstmt.setString(3, telefono != null ? telefono.trim() : "-");
            pstmt.setString(4, vehiculo != null ? vehiculo.trim() : "-");
            pstmt.setString(5, placa != null ? placa.trim().toUpperCase() : "-");
            pstmt.setInt(6, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error SQL al modificar cliente: " + e.getMessage());
            return false;
        }
    }

    public boolean modificar(int id, String nombre, String dni, String telefono, String campo4) {
        return modificar(id, nombre, dni, telefono, campo4, "-");
    }

    public boolean modificar(int id, String nombre, String dni, String telefono) {
        return modificar(id, nombre, dni, telefono, "-", "-");
    }

    public boolean modificar(int id, String nombre, String dni, String telefono, String c4, String c5, String c6) {
        return modificar(id, nombre, dni, telefono, c5, c6);
    }

    public boolean modificar(String id, String nombre, String dni, String telefono, String campo4) {
        try {
            return modificar(Integer.parseInt(id), nombre, dni, telefono, campo4, "-");
        } catch (Exception e) {
            return false;
        }
    }

    public boolean modificar(String id, String nombre, String dni, String telefono, String campo4, String campo5) {
        try {
            return modificar(Integer.parseInt(id), nombre, dni, telefono, campo4, campo5);
        } catch (Exception e) {
            return false;
        }
    }

    // --- Consultas ---
    public List<Object[]> listar() {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT * FROM clientes ORDER BY 1 ASC";
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
            System.err.println("Error al listar clientes: " + e.getMessage());
        }
        return lista;
    }

    public List<Object[]> buscar(String filtro) {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT * FROM clientes WHERE CAST(id_cliente AS TEXT) ILIKE ? OR nombre ILIKE ? OR COALESCE(dni,'') ILIKE ? ORDER BY 1 ASC";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String f = "%" + filtro + "%";
            pstmt.setString(1, f);
            pstmt.setString(2, f);
            pstmt.setString(3, f);
            try (ResultSet rs = pstmt.executeQuery()) {
                ResultSetMetaData meta = rs.getMetaData();
                int total = meta.getColumnCount();
                while (rs.next()) {
                    Object[] fila = new Object[total];
                    for (int i = 1; i <= total; i++) {
                        fila[i - 1] = rs.getObject(i);
                    }
                    lista.add(fila);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar cliente: " + e.getMessage());
        }
        return lista;
    }
}