package paneles;

import java.awt.*;
import javax.swing.*;

public class PanelTaller extends JPanel {
    public PanelTaller() {
        setLayout(new GridBagLayout());
        JLabel lbl = new JLabel("Taller y Cola de Espera - MotorCorp");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        add(lbl);
    }
}