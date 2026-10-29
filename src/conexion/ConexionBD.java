package conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {
    private static final String URL = "jdbc:postgresql://localhost:5432/bd_sistema_motorcorp";
    private static final String USER = "postgres";
    private static final String PASSWORD = "1234";

    public static Connection conectar() {
        Connection conn = null;
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("¡Conexión exitosa a la Base de Datos de MotorCorp!");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error de conexión: " + e.getMessage());
        }
        return conn;
    }
}