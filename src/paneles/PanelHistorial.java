package paneles;

import java.awt.*;
import javax.swing.*;

public class PanelHistorial extends JPanel {
    public PanelHistorial() {
        setLayout(new GridBagLayout());
        JLabel lbl = new JLabel("Historial de Taller - MotorCorp");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        add(lbl);
    }
}