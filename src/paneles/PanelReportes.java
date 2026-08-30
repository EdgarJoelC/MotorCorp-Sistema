package paneles;

import java.awt.*;
import javax.swing.*;

public class PanelReportes extends JPanel {
    public PanelReportes() {
        setLayout(new GridBagLayout());
        JLabel lbl = new JLabel("Historial y Reportes - MotorCorp");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        add(lbl);
    }
}