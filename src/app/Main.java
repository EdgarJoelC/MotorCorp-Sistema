package app;

import com.formdev.flatlaf.FlatLightLaf;
import conexion.ConexionBD;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import vistas.LoginView;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("Error al cargar FlatLaf: " + ex.getMessage());
        }


        if (ConexionBD.conectar() != null) {
            System.out.println("¡Base de datos vinculada y lista para operar!");
        } else {
            System.err.println("Revisa tus credenciales o el servicio de PostgreSQL.");
        }

        SwingUtilities.invokeLater(() -> {
            new LoginView().setVisible(true);
        });
    }
}