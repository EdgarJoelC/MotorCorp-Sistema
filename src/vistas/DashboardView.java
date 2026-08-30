package vistas;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import paneles.*;

public class DashboardView extends JFrame {

    private JPanel panelContenedor;
    private JPanel sidebar;
    private JLabel lblLogoHeader;
    private CardLayout cardLayout;
    private boolean sidebarVisible = true;
    private JButton botonSeleccionado;
    private Timer animadorSidebar;
    private final int ANCHO_MAX = 260;
    private int anchoActual = 260;

    public DashboardView() {
        setTitle("MotorCorp - Panel de Control Principal");
        setSize(1180, 740);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initLayout();
    }

    private void initLayout() {
        setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 65));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));

        JPanel headerLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 12));
        headerLeft.setBackground(Color.WHITE);

        JButton btnMenuToggle = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                g2.setColor(new Color(226, 232, 240));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);

                g2.setColor(new Color(51, 65, 85));
                g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                int w = getWidth();
                int h = getHeight();
                int x = 11;
                int anchoLinea = w - 22;

                g2.drawLine(x, h / 2 - 5, x + anchoLinea, h / 2 - 5);
                g2.drawLine(x, h / 2, x + anchoLinea, h / 2);
                g2.drawLine(x, h / 2 + 5, x + anchoLinea, h / 2 + 5);
                g2.dispose();
            }
        };
        btnMenuToggle.setPreferredSize(new Dimension(42, 42));
        btnMenuToggle.setBackground(new Color(248, 250, 252));
        btnMenuToggle.setContentAreaFilled(false);
        btnMenuToggle.setBorderPainted(false);
        btnMenuToggle.setFocusPainted(false);
        btnMenuToggle.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnMenuToggle.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnMenuToggle.setBackground(new Color(241, 245, 249));
                btnMenuToggle.repaint();
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btnMenuToggle.setBackground(new Color(248, 250, 252));
                btnMenuToggle.repaint();
            }
        });

        try {
            ImageIcon iconLogo3 = new ImageIcon(getClass().getResource("/resources/logo3.png"));
            Image original3 = iconLogo3.getImage();
            int origW3 = original3.getWidth(null);
            int origH3 = original3.getHeight(null);

            int targetH3 = 38;
            int targetW3 = (origH3 > 0) ? (origW3 * targetH3) / origH3 : 65;

            Image imgEscalada3 = original3.getScaledInstance(targetW3, targetH3, Image.SCALE_SMOOTH);
            lblLogoHeader = new JLabel(new ImageIcon(imgEscalada3));
        } catch (Exception e) {
            lblLogoHeader = new JLabel("MC");
            lblLogoHeader.setFont(new Font("Segoe UI", Font.BOLD, 20));
            lblLogoHeader.setForeground(new Color(224, 43, 32));
        }

        lblLogoHeader.setVisible(false);

        headerLeft.add(btnMenuToggle);
        headerLeft.add(lblLogoHeader);
        header.add(headerLeft, BorderLayout.WEST);

        sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(ANCHO_MAX, 0));
        sidebar.setBackground(Color.WHITE);
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(226, 232, 240)));

        JPanel panelLogoSidebar = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20));
        panelLogoSidebar.setBackground(Color.WHITE);
        panelLogoSidebar.setMaximumSize(new Dimension(ANCHO_MAX, 90));

        try {
            ImageIcon iconLogo2 = new ImageIcon(getClass().getResource("/resources/logo2.png"));
            Image original2 = iconLogo2.getImage();
            int origW2 = original2.getWidth(null);
            int origH2 = original2.getHeight(null);

            int targetW2 = 200;
            int targetH2 = (origW2 > 0) ? (origH2 * targetW2) / origW2 : 55;

            Image imgEscalada2 = original2.getScaledInstance(targetW2, targetH2, Image.SCALE_SMOOTH);
            JLabel lblLogoSidebar = new JLabel(new ImageIcon(imgEscalada2));
            panelLogoSidebar.add(lblLogoSidebar);
        } catch (Exception e) {
            JLabel lblAltSidebar = new JLabel("MOTORCORP");
            lblAltSidebar.setFont(new Font("Segoe UI", Font.BOLD, 22));
            lblAltSidebar.setForeground(new Color(224, 43, 32));
            panelLogoSidebar.add(lblAltSidebar);
        }

        JPanel menuItems = new JPanel();
        menuItems.setLayout(new GridLayout(7, 1, 0, 6));
        menuItems.setBackground(Color.WHITE);
        menuItems.setBorder(BorderFactory.createEmptyBorder(5, 14, 10, 14));
        menuItems.setMaximumSize(new Dimension(ANCHO_MAX, 360));

        JButton btnInicio = crearBotonMenu("Inicio");
        JButton btnClientes = crearBotonMenu("Registrar Cliente");
        JButton btnCola = crearBotonMenu("Cola de Espera");
        JButton btnInventario = crearBotonMenu("Inventario Avanzado");
        JButton btnReportes = crearBotonMenu("Módulo de Reportes");
        JButton btnPOS = crearBotonMenu("Punto de Venta");
        JButton btnHistorial = crearBotonMenu("Historial Taller");

        menuItems.add(btnInicio);
        menuItems.add(btnClientes);
        menuItems.add(btnCola);
        menuItems.add(btnInventario);
        menuItems.add(btnReportes);
        menuItems.add(btnPOS);
        menuItems.add(btnHistorial);

        JButton btnCerrar = new JButton("Cerrar Sesión");
        btnCerrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCerrar.setBackground(new Color(224, 43, 32));
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setFocusPainted(false);
        btnCerrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCerrar.putClientProperty("JButton.buttonType", "roundRect");
        btnCerrar.setBorder(BorderFactory.createEmptyBorder(11, 15, 11, 15));

        btnCerrar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnCerrar.setBackground(new Color(195, 30, 20));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btnCerrar.setBackground(new Color(224, 43, 32));
            }
        });

        JPanel panelBottom = new JPanel(new BorderLayout());
        panelBottom.setBackground(Color.WHITE);
        panelBottom.setBorder(BorderFactory.createEmptyBorder(10, 14, 20, 14));
        panelBottom.setMaximumSize(new Dimension(ANCHO_MAX, 70));
        panelBottom.add(btnCerrar, BorderLayout.SOUTH);

        sidebar.add(panelLogoSidebar);
        sidebar.add(menuItems);
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(panelBottom);

        marcarActivo(btnInicio);

        cardLayout = new CardLayout();
        panelContenedor = new JPanel(cardLayout);
        panelContenedor.setBackground(new Color(248, 250, 252));

        panelContenedor.add(new PanelDashboard(), "INICIO");
        panelContenedor.add(new PanelClientes(), "CLIENTES");
        panelContenedor.add(new PanelTaller(), "COLA");
        panelContenedor.add(new PanelInventario(), "INVENTARIO");
        panelContenedor.add(new PanelReportes(), "REPORTES");
        panelContenedor.add(new PanelPOS(), "POS");
        panelContenedor.add(new PanelHistorial(), "HISTORIAL");

        btnInicio.addActionListener(e -> {
            cardLayout.show(panelContenedor, "INICIO");
            marcarActivo(btnInicio);
        });
        btnClientes.addActionListener(e -> {
            cardLayout.show(panelContenedor, "CLIENTES");
            marcarActivo(btnClientes);
        });
        btnCola.addActionListener(e -> {
            cardLayout.show(panelContenedor, "COLA");
            marcarActivo(btnCola);
        });
        btnInventario.addActionListener(e -> {
            cardLayout.show(panelContenedor, "INVENTARIO");
            marcarActivo(btnInventario);
        });
        btnReportes.addActionListener(e -> {
            cardLayout.show(panelContenedor, "REPORTES");
            marcarActivo(btnReportes);
        });
        btnPOS.addActionListener(e -> {
            cardLayout.show(panelContenedor, "POS");
            marcarActivo(btnPOS);
        });
        btnHistorial.addActionListener(e -> {
            cardLayout.show(panelContenedor, "HISTORIAL");
            marcarActivo(btnHistorial);
        });

        btnCerrar.addActionListener(e -> {
            new LoginView().setVisible(true);
            this.dispose();
        });

        btnMenuToggle.addActionListener(e -> toggleSidebarAnimado());

        add(header, BorderLayout.NORTH);
        add(sidebar, BorderLayout.WEST);
        add(panelContenedor, BorderLayout.CENTER);
    }

    private void toggleSidebarAnimado() {
        if (animadorSidebar != null && animadorSidebar.isRunning()) {
            return;
        }

        final int targetWidth = sidebarVisible ? 0 : ANCHO_MAX;
        final int step = sidebarVisible ? -20 : 20;

        if (!sidebarVisible) {
            lblLogoHeader.setVisible(false);
            sidebar.setVisible(true);
        }

        animadorSidebar = new Timer(10, e -> {
            anchoActual += step;
            if ((step < 0 && anchoActual <= targetWidth) || (step > 0 && anchoActual >= targetWidth)) {
                anchoActual = targetWidth;
                sidebar.setPreferredSize(new Dimension(anchoActual, 0));
                sidebar.setVisible(anchoActual > 0);
                sidebarVisible = !sidebarVisible;

                if (!sidebarVisible) {
                    lblLogoHeader.setVisible(true);
                }

                animadorSidebar.stop();
            } else {
                sidebar.setPreferredSize(new Dimension(anchoActual, 0));
            }
            sidebar.revalidate();
            sidebar.repaint();
            panelContenedor.revalidate();
            panelContenedor.repaint();
        });

        animadorSidebar.start();
    }

    private JButton crearBotonMenu(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setForeground(new Color(71, 85, 105));
        btn.setBackground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.putClientProperty("JButton.buttonType", "roundRect");

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn != botonSeleccionado) {
                    btn.setBackground(new Color(241, 245, 249));
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (btn != botonSeleccionado) {
                    btn.setBackground(Color.WHITE);
                }
            }
        });

        return btn;
    }

    private void marcarActivo(JButton btn) {
        if (botonSeleccionado != null) {
            botonSeleccionado.setBackground(Color.WHITE);
            botonSeleccionado.setForeground(new Color(71, 85, 105));
            botonSeleccionado.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
        }
        botonSeleccionado = btn;
        botonSeleccionado.setBackground(new Color(254, 242, 242));
        botonSeleccionado.setForeground(new Color(224, 43, 32));
        botonSeleccionado.setFont(new Font("Segoe UI", Font.BOLD, 14));
    }
}