package dao;

import conexion.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InventarioDAO {

    public boolean registrar(String nombre, int stock, double precio) {
        String sql = "INSERT INTO inventario (nombre_producto, stock, precio) VALUES (?, ?, ?)";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombre);
            pstmt.setInt(2, stock);
            pstmt.setDouble(3, precio);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean modificar(int id, String nombre, int stock, double precio) {
        String sql = "UPDATE inventario SET nombre_producto = ?, stock = ?, precio = ? WHERE id_inventario = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombre);
            pstmt.setInt(2, stock);
            pstmt.setDouble(3, precio);
            pstmt.setInt(4, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean actualizarStock(int id, int nuevoStock) {
        String sql = "UPDATE inventario SET stock = ? WHERE id_inventario = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, nuevoStock);
            pstmt.setInt(2, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean aumentarStock(int id, int cantidad) {
        String sql = "UPDATE inventario SET stock = stock + ? WHERE id_inventario = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, cantidad);
            pstmt.setInt(2, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean restarStock(int id, int cantidad) {
        String sql = "UPDATE inventario SET stock = stock - ? WHERE id_inventario = ? AND stock >= ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, cantidad);
            pstmt.setInt(2, id);
            pstmt.setInt(3, cantidad);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public List<Object[]> listar() {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT id_inventario, nombre_producto, stock, precio FROM inventario ORDER BY id_inventario ASC";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                lista.add(new Object[]{
                        rs.getInt("id_inventario"),
                        rs.getString("nombre_producto"),
                        rs.getInt("stock"),
                        rs.getDouble("precio")
                });
            }
        } catch (SQLException e) {
        }
        return lista;
    }

    public List<Object[]> buscar(String filtro) {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT id_inventario, nombre_producto, stock, precio FROM inventario WHERE nombre_producto ILIKE ? ORDER BY id_inventario ASC";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + filtro + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Object[]{
                            rs.getInt("id_inventario"),
                            rs.getString("nombre_producto"),
                            rs.getInt("stock"),
                            rs.getDouble("precio")
                    });
                }
            }
        } catch (SQLException e) {
        }
        return lista;
    }
}