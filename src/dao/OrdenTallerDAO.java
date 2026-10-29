package dao;

import conexion.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class OrdenTallerDAO {

    public OrdenTallerDAO() {
        asegurarEstructuraTablas();
    }

    private void asegurarEstructuraTablas() {
        String[] alteraciones = {
                "CREATE TABLE IF NOT EXISTS ventas (id_venta SERIAL PRIMARY KEY)",
                "ALTER TABLE ventas ADD COLUMN IF NOT EXISTS id_orden INT",
                "ALTER TABLE ventas ADD COLUMN IF NOT EXISTS cliente VARCHAR(150)",
                "ALTER TABLE ventas ADD COLUMN IF NOT EXISTS vehiculo VARCHAR(100)",
                "ALTER TABLE ventas ADD COLUMN IF NOT EXISTS placa VARCHAR(20)",
                "ALTER TABLE ventas ADD COLUMN IF NOT EXISTS mano_obra NUMERIC(10,2) DEFAULT 0.00",
                "ALTER TABLE ventas ADD COLUMN IF NOT EXISTS repuestos NUMERIC(10,2) DEFAULT 0.00",
                "ALTER TABLE ventas ADD COLUMN IF NOT EXISTS total NUMERIC(10,2) DEFAULT 0.00",
                "ALTER TABLE ventas ADD COLUMN IF NOT EXISTS metodo_pago VARCHAR(50)",
                "ALTER TABLE ventas ADD COLUMN IF NOT EXISTS fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP",
                "ALTER TABLE ordenes_taller ADD COLUMN IF NOT EXISTS costo_mano_obra NUMERIC(10,2) DEFAULT 0.00",
                "ALTER TABLE ordenes_taller ADD COLUMN IF NOT EXISTS costo_repuestos NUMERIC(10,2) DEFAULT 0.00"
        };

        try (Connection conn = ConexionBD.conectar();
             Statement stmt = conn.createStatement()) {
            for (String sql : alteraciones) {
                try {
                    stmt.execute(sql);
                } catch (SQLException ex) {
                    System.err.println("Aviso migración: " + ex.getMessage());
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al conectar para migraciones: " + e.getMessage());
        }
    }

    public boolean registrar(int idCliente, int idMecanico, String vehiculo, String placa, String descripcionProblema, double costoManoObra) {
        String sql = "INSERT INTO ordenes_taller (id_cliente, id_mecanico, vehiculo, placa, descripcion_problema, estado, fecha_ingreso, costo_mano_obra, costo_repuestos) " +
                "VALUES (?, ?, ?, ?, ?, 'Recepcionado', CURRENT_TIMESTAMP, ?, 0.00)";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idCliente);
            pstmt.setInt(2, idMecanico);
            pstmt.setString(3, vehiculo.trim().isEmpty() ? "No especificado" : vehiculo.trim());
            pstmt.setString(4, (placa == null || placa.trim().isEmpty()) ? "S/P" : placa.trim().toUpperCase());
            pstmt.setString(5, descripcionProblema.trim());
            pstmt.setDouble(6, costoManoObra);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error SQL al registrar orden: " + e.getMessage());
            return false;
        }
    }

    public boolean agregarCostoRepuesto(int idOrden, double montoRepuesto) {
        String sql = "UPDATE ordenes_taller SET costo_repuestos = COALESCE(costo_repuestos, 0) + ? WHERE id_orden = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, montoRepuesto);
            pstmt.setInt(2, idOrden);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al sumar repuesto a la orden: " + e.getMessage());
            return false;
        }
    }

    public List<Object[]> listarOrdenesActivasPorMecanico(int idMecanico) {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT id_orden, vehiculo, placa, descripcion_problema " +
                "FROM ordenes_taller " +
                "WHERE id_mecanico = ? AND TRIM(LOWER(estado)) IN ('recepcionado', 'en proceso') " +
                "ORDER BY id_orden ASC";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idMecanico);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Object[]{
                            rs.getInt("id_orden"),
                            rs.getString("vehiculo"),
                            rs.getString("placa"),
                            rs.getString("descripcion_problema")
                    });
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al listar órdenes activas: " + e.getMessage());
        }
        return lista;
    }

    public boolean actualizarEstado(int idOrden, String nuevoEstado) {
        String sql;
        boolean esFinalizado = nuevoEstado.equalsIgnoreCase("Finalizado") || nuevoEstado.equalsIgnoreCase("Entregado");

        if (esFinalizado) {
            sql = "UPDATE ordenes_taller SET estado = ?, fecha_salida = CURRENT_TIMESTAMP WHERE id_orden = ?";
        } else {
            sql = "UPDATE ordenes_taller SET estado = ? WHERE id_orden = ?";
        }

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nuevoEstado);
            pstmt.setInt(2, idOrden);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error SQL al actualizar estado: " + e.getMessage());
            return false;
        }
    }

    public boolean registrarCobroYArchivar(int idOrden, String cliente, String vehiculo, String placa, double manoObra, double repuestos, double total, String metodoPago) {
        String sqlVenta = "INSERT INTO ventas (id_orden, cliente, vehiculo, placa, mano_obra, repuestos, total, metodo_pago, fecha) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
        String sqlOrden = "UPDATE ordenes_taller SET estado = 'Entregado', fecha_salida = CURRENT_TIMESTAMP WHERE id_orden = ?";

        try (Connection conn = ConexionBD.conectar()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt1 = conn.prepareStatement(sqlVenta);
                 PreparedStatement pstmt2 = conn.prepareStatement(sqlOrden)) {

                pstmt1.setInt(1, idOrden);
                pstmt1.setString(2, cliente);
                pstmt1.setString(3, vehiculo);
                pstmt1.setString(4, placa);
                pstmt1.setDouble(5, manoObra);
                pstmt1.setDouble(6, repuestos);
                pstmt1.setDouble(7, total);
                pstmt1.setString(8, metodoPago);
                pstmt1.executeUpdate();

                pstmt2.setInt(1, idOrden);
                pstmt2.executeUpdate();

                conn.commit();
                return true;
            } catch (SQLException ex) {
                conn.rollback();
                System.err.println("Error en transacción de cobro: " + ex.getMessage());
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error de conexión al registrar cobro: " + e.getMessage());
            return false;
        }
    }

    public List<Object[]> listarOrdenesEnCola() {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT o.id_orden, " +
                "COALESCE(c.nombre, 'Cliente #' || o.id_cliente) AS cliente, " +
                "COALESCE(o.vehiculo, '-') AS vehiculo, " +
                "COALESCE(o.placa, '-') AS placa, " +
                "COALESCE(m.nombre_mecanico, 'Mecánico #' || o.id_mecanico) AS mecanico, " +
                "o.descripcion_problema, " +
                "COALESCE(o.costo_mano_obra, 0) AS mano_obra, " +
                "o.estado " +
                "FROM ordenes_taller o " +
                "LEFT JOIN clientes c ON o.id_cliente = c.id_cliente " +
                "LEFT JOIN mecanicos m ON o.id_mecanico = m.id_mecanico " +
                "WHERE TRIM(LOWER(o.estado)) NOT IN ('finalizado', 'entregado') " +
                "ORDER BY o.id_orden ASC";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                lista.add(new Object[]{
                        rs.getInt("id_orden"),
                        rs.getString("cliente"),
                        rs.getString("vehiculo"),
                        rs.getString("placa"),
                        rs.getString("mecanico"),
                        rs.getString("descripcion_problema"),
                        String.format("%.2f", rs.getDouble("mano_obra")),
                        rs.getString("estado")
                });
            }
        } catch (SQLException e) {
            System.err.println("Error SQL al listar cola: " + e.getMessage());
        }
        return lista;
    }

    public List<Object[]> listarOrdenesParaCobro() {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT o.id_orden, " +
                "COALESCE(c.nombre, 'Cliente #' || o.id_cliente) AS cliente, " +
                "COALESCE(c.dni, '-') AS dni, " +
                "COALESCE(o.vehiculo, '-') AS vehiculo, " +
                "COALESCE(o.placa, '-') AS placa, " +
                "COALESCE(m.nombre_mecanico, 'Mecánico #' || o.id_mecanico) AS mecanico, " +
                "o.descripcion_problema, " +
                "COALESCE(o.costo_mano_obra, 0) AS mano_obra, " +
                "COALESCE(o.costo_repuestos, 0) AS repuestos, " +
                "TO_CHAR(o.fecha_ingreso, 'DD/MM/YYYY HH24:MI') AS ingreso " +
                "FROM ordenes_taller o " +
                "LEFT JOIN clientes c ON o.id_cliente = c.id_cliente " +
                "LEFT JOIN mecanicos m ON o.id_mecanico = m.id_mecanico " +
                "WHERE TRIM(LOWER(o.estado)) = 'finalizado' " +
                "ORDER BY o.id_orden ASC";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                lista.add(new Object[]{
                        rs.getInt("id_orden"),
                        rs.getString("cliente"),
                        rs.getString("dni"),
                        rs.getString("vehiculo"),
                        rs.getString("placa"),
                        rs.getString("mecanico"),
                        rs.getString("descripcion_problema"),
                        rs.getDouble("mano_obra"),
                        rs.getDouble("repuestos"),
                        rs.getString("ingreso")
                });
            }
        } catch (SQLException e) {
            System.err.println("Error al listar órdenes para cobro: " + e.getMessage());
        }
        return lista;
    }

    public List<Object[]> listarClientesCompletos() {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT id_cliente, nombre, COALESCE(dni, ''), COALESCE(vehiculo, ''), COALESCE(placa, '') FROM clientes ORDER BY nombre ASC";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                lista.add(new Object[]{
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5)
                });
            }
        } catch (SQLException e) {
        }
        return lista;
    }
}