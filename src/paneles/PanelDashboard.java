package paneles;
import java.awt.*;
import javax.swing.*;

public class PanelDashboard extends JPanel {
    public PanelDashboard() {
        setLayout(new GridBagLayout());
        JLabel lbl = new JLabel("Panel General de Métricas - MotorCorp");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        add(lbl);
    }
}