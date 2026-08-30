package paneles;

import java.awt.*;
import javax.swing.*;

public class PanelPOS extends JPanel {
    public PanelPOS() {
        setLayout(new GridBagLayout());
        JLabel lbl = new JLabel("Punto de Venta (POS) - MotorCorp");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        add(lbl);
    }
}