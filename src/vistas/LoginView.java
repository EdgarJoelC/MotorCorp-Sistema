package vistas;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

public class LoginView extends JFrame {

    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JButton btnIngresar;

    public LoginView() {
        setTitle("MotorCorp - Acceso al Sistema");
        setSize(420, 560);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        initComponentes();
    }

    private void initComponentes() {
        JPanel panelPrincipal = new JPanel(new GridBagLayout());
        panelPrincipal.setBackground(Color.WHITE);
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.gridx = 0;

        try {
            ImageIcon iconoOriginal = new ImageIcon(getClass().getResource("/resources/logo.png"));
            Image imagenEscalada = iconoOriginal.getImage().getScaledInstance(140, 140, Image.SCALE_SMOOTH);
            JLabel lblLogo = new JLabel(new ImageIcon(imagenEscalada), SwingConstants.CENTER);
            gbc.gridy = 0;
            panelPrincipal.add(lblLogo, gbc);
        } catch (Exception e) {
            JLabel lblAlt = new JLabel("MOTORCORP", SwingConstants.CENTER);
            lblAlt.setFont(new Font("Segoe UI", Font.BOLD, 26));
            lblAlt.setForeground(new Color(224, 43, 32));
            gbc.gridy = 0;
            panelPrincipal.add(lblAlt, gbc);
        }

        JLabel lblSub = new JLabel("Sistema de Gestión de Taller", SwingConstants.CENTER);
        lblSub.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
        lblSub.setForeground(new Color(110, 115, 125));
        gbc.gridy = 1;
        gbc.insets = new Insets(4, 0, 20, 0);
        panelPrincipal.add(lblSub, gbc);

        JLabel lblUser = new JLabel("Usuario");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUser.setForeground(new Color(45, 55, 72));
        gbc.insets = new Insets(4, 0, 4, 0);
        gbc.gridy = 2;
        panelPrincipal.add(lblUser, gbc);

        txtUsuario = new JTextField(15);
        txtUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsuario.putClientProperty("JTextField.placeholderText", "Ingrese su usuario");
        txtUsuario.putClientProperty("JComponent.roundRect", true);
        gbc.gridy = 3;
        panelPrincipal.add(txtUsuario, gbc);

        JLabel lblPass = new JLabel("Contraseña");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPass.setForeground(new Color(45, 55, 72));
        gbc.gridy = 4;
        gbc.insets = new Insets(10, 0, 4, 0);
        panelPrincipal.add(lblPass, gbc);

        txtPassword = new JPasswordField(15);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.putClientProperty("JTextField.placeholderText", "Ingrese su contraseña");
        txtPassword.putClientProperty("JComponent.roundRect", true);
        gbc.gridy = 5;
        gbc.insets = new Insets(4, 0, 4, 0);
        panelPrincipal.add(txtPassword, gbc);

        Color colorNormal = new Color(224, 43, 32);
        Color colorHover = new Color(195, 30, 20);

        btnIngresar = new JButton("Iniciar Sesión");
        btnIngresar.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnIngresar.setBackground(colorNormal);
        btnIngresar.setForeground(Color.WHITE);
        btnIngresar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnIngresar.putClientProperty("JButton.buttonType", "roundRect");
        btnIngresar.setFocusPainted(false);
        gbc.gridy = 6;
        gbc.insets = new Insets(24, 0, 0, 0);
        panelPrincipal.add(btnIngresar, gbc);

        btnIngresar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnIngresar.setBackground(colorHover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnIngresar.setBackground(colorNormal);
            }
        });

        btnIngresar.addActionListener(e -> {
            new DashboardView().setVisible(true);
            this.dispose();
        });

        add(panelPrincipal);
    }
}