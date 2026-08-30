package paneles;

import java.awt.*;
import javax.swing.*;

public class PanelClientes extends JPanel {
    public PanelClientes() {
        setLayout(new GridBagLayout());
        JLabel lbl = new JLabel("Gestión de Clientes - MotorCorp");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        add(lbl);
    }
}