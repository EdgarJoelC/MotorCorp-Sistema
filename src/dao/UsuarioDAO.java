package dao;

import conexion.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {

    public boolean autenticar(String username, String password) {
        String sql = "SELECT id_usuario FROM usuarios WHERE username = ? AND password = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            return false;
        }
    }
}