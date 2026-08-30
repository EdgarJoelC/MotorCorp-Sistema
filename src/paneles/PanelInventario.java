package paneles;

import java.awt.*;
import javax.swing.*;

public class PanelInventario extends JPanel {
    public PanelInventario() {
        setLayout(new GridBagLayout());
        JLabel lbl = new JLabel("Gestión de Inventario - MotorCorp");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        add(lbl);
    }
}