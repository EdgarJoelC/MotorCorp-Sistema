package paneles;

import conexion.ConexionBD;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerDateModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class PanelDashboard extends JPanel {

    private JLabel lblTotalVentas, lblTotalServicios, lblTotalClientes, lblTotalMecanicos;
    private JLabel lblEfectivo, lblYape, lblTransf, lblTarjeta;
    private JComboBox<String> cmbPeriodo;
    private JSpinner spinnerFecha;
    private JTable tablaVentas;
    private DefaultTableModel modeloVentas;

    private GraficoDonutMetodos panelDonut;
    private GraficoBarrasCostos panelBarras;

    public PanelDashboard() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(crearBarraFiltrosSuperiores(), BorderLayout.NORTH);

        JPanel panelCentro = new JPanel(new BorderLayout(0, 15));
        panelCentro.setOpaque(false);

        panelCentro.add(crearTarjetasMetricasGenerales(), BorderLayout.NORTH);

        JPanel panelMedio = new JPanel(new BorderLayout(0, 15));
        panelMedio.setOpaque(false);

        panelMedio.add(crearFilaMetodosPago(), BorderLayout.NORTH);

        JPanel panelGraficosYTabla = new JPanel(new GridLayout(1, 3, 15, 0));
        panelGraficosYTabla.setOpaque(false);

        panelDonut = new GraficoDonutMetodos();
        panelBarras = new GraficoBarrasCostos();

        panelGraficosYTabla.add(panelDonut);
        panelGraficosYTabla.add(panelBarras);
        panelGraficosYTabla.add(crearPanelTablaVentas());

        panelMedio.add(panelGraficosYTabla, BorderLayout.CENTER);
        panelCentro.add(panelMedio, BorderLayout.CENTER);

        add(panelCentro, BorderLayout.CENTER);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                cargarDatosDashboard();
            }
        });

        cargarDatosDashboard();
    }

    private JPanel crearBarraFiltrosSuperiores() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);

        JLabel lblTitulo = new JLabel("Dashboard Ejecutivo - Métricas y Facturación");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(45, 55, 72));
        bar.add(lblTitulo, BorderLayout.WEST);

        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelFiltros.setOpaque(false);

        JLabel lblFiltro = new JLabel("Filtrar Período:");
        lblFiltro.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblFiltro.setForeground(new Color(71, 85, 105));

        cmbPeriodo = new JComboBox<>(new String[]{"Hoy", "Esta Semana", "Este Mes", "Este Año", "Día Específico", "Todo el Historial"});
        cmbPeriodo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cmbPeriodo.setPreferredSize(new Dimension(145, 30));

        spinnerFecha = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinnerFecha, "dd/MM/yyyy");
        spinnerFecha.setEditor(editor);
        spinnerFecha.setValue(new Date());
        spinnerFecha.setPreferredSize(new Dimension(115, 30));
        spinnerFecha.setVisible(false);

        cmbPeriodo.addActionListener(e -> {
            boolean esDiaEspecifico = "Día Específico".equals(cmbPeriodo.getSelectedItem());
            spinnerFecha.setVisible(esDiaEspecifico);
            cargarDatosDashboard();
        });

        spinnerFecha.addChangeListener(e -> {
            if ("Día Específico".equals(cmbPeriodo.getSelectedItem())) {
                cargarDatosDashboard();
            }
        });

        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnActualizar.setFocusPainted(false);
        btnActualizar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnActualizar.addActionListener(e -> cargarDatosDashboard());

        panelFiltros.add(lblFiltro);
        panelFiltros.add(cmbPeriodo);
        panelFiltros.add(spinnerFecha);
        panelFiltros.add(btnActualizar);

        bar.add(panelFiltros, BorderLayout.EAST);
        return bar;
    }

    private JPanel crearTarjetasMetricasGenerales() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 15, 0));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(0, 85));

        lblTotalVentas = new JLabel("S/. 0.00");
        lblTotalServicios = new JLabel("0");
        lblTotalClientes = new JLabel("0");
        lblTotalMecanicos = new JLabel("0");

        panel.add(crearCardKpi("Recaudación Período", lblTotalVentas, new Color(4, 120, 87), new Color(209, 250, 229)));
        panel.add(crearCardKpi("Servicios Cobrados", lblTotalServicios, new Color(29, 78, 216), new Color(219, 234, 254)));
        panel.add(crearCardKpi("Clientes Registrados", lblTotalClientes, new Color(180, 83, 9), new Color(254, 243, 199)));
        panel.add(crearCardKpi("Especialistas Técnicos", lblTotalMecanicos, new Color(109, 40, 217), new Color(237, 233, 254)));

        return panel;
    }

    private JPanel crearFilaMetodosPago() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 15, 0));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(0, 65));

        lblEfectivo = new JLabel("S/. 0.00");
        lblYape = new JLabel("S/. 0.00");
        lblTransf = new JLabel("S/. 0.00");
        lblTarjeta = new JLabel("S/. 0.00");

        panel.add(crearChipMetodo("Efectivo", lblEfectivo, new Color(16, 185, 129)));
        panel.add(crearChipMetodo("Yape / Plin", lblYape, new Color(147, 51, 234)));
        panel.add(crearChipMetodo("Transferencias", lblTransf, new Color(37, 99, 235)));
        panel.add(crearChipMetodo("Tarjetas Débito/Crédito", lblTarjeta, new Color(217, 119, 6)));

        return panel;
    }

    private JPanel crearChipMetodo(String titulo, JLabel lblMonto, Color colorAcorde) {
        JPanel chip = new JPanel(new BorderLayout(5, 2));
        chip.setBackground(Color.WHITE);
        chip.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(8, 14, 8, 14)
        ));

        JLabel lblTit = new JLabel(titulo);
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTit.setForeground(new Color(100, 116, 139));

        lblMonto.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblMonto.setForeground(colorAcorde);

        chip.add(lblTit, BorderLayout.NORTH);
        chip.add(lblMonto, BorderLayout.CENTER);
        return chip;
    }

    private JPanel crearCardKpi(String titulo, JLabel lblValor, Color colorTexto, Color colorFondo) {
        JPanel card = new JPanel(new BorderLayout(0, 4));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(12, 16, 12, 16)
        ));

        JLabel lblSub = new JLabel(titulo.toUpperCase());
        lblSub.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblSub.setForeground(new Color(100, 116, 139));

        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblValor.setForeground(colorTexto);

        card.add(lblSub, BorderLayout.NORTH);
        card.add(lblValor, BorderLayout.CENTER);

        return card;
    }

    private JPanel crearPanelTablaVentas() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(14, 14, 14, 14)
        ));

        JLabel lbl = new JLabel("Historial de Transacciones");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(new Color(45, 55, 72));
        panel.add(lbl, BorderLayout.NORTH);

        String[] cols = {"N°", "Cliente", "Total (S/.)", "Medio", "Fecha"};
        modeloVentas = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaVentas = new JTable(modeloVentas);
        tablaVentas.setRowHeight(26);
        tablaVentas.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tablaVentas.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tablaVentas.getTableHeader().setBackground(new Color(241, 245, 249));

        tablaVentas.getColumnModel().getColumn(0).setPreferredWidth(25);
        tablaVentas.getColumnModel().getColumn(1).setPreferredWidth(85);
        tablaVentas.getColumnModel().getColumn(2).setPreferredWidth(60);
        tablaVentas.getColumnModel().getColumn(3).setPreferredWidth(70);
        tablaVentas.getColumnModel().getColumn(4).setPreferredWidth(75);

        JScrollPane scroll = new JScrollPane(tablaVentas);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private String construirClausulaWhereFecha() {
        String periodo = cmbPeriodo.getSelectedItem() != null ? cmbPeriodo.getSelectedItem().toString() : "Hoy";
        switch (periodo) {
            case "Hoy":
                return "WHERE CAST(fecha AS DATE) = CURRENT_DATE";
            case "Esta Semana":
                return "WHERE fecha >= DATE_TRUNC('week', CURRENT_DATE) AND fecha < DATE_TRUNC('week', CURRENT_DATE) + INTERVAL '1 week'";
            case "Este Mes":
                return "WHERE EXTRACT(MONTH FROM fecha) = EXTRACT(MONTH FROM CURRENT_DATE) AND EXTRACT(YEAR FROM fecha) = EXTRACT(YEAR FROM CURRENT_DATE)";
            case "Este Año":
                return "WHERE EXTRACT(YEAR FROM fecha) = EXTRACT(YEAR FROM CURRENT_DATE)";
            case "Día Específico":
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String fechaSel = sdf.format((Date) spinnerFecha.getValue());
                return "WHERE CAST(fecha AS DATE) = '" + fechaSel + "'";
            default:
                return "";
        }
    }

    public void cargarDatosDashboard() {
        double totalVentas = 0.0;
        double totalManoObra = 0.0;
        double totalRepuestos = 0.0;
        int cantidadVentas = 0;

        double sumEfectivo = 0.0;
        double sumYape = 0.0;
        double sumTransf = 0.0;
        double sumTarjeta = 0.0;

        Map<String, Double> metodosMap = new LinkedHashMap<>();
        metodosMap.put("Efectivo", 0.0);
        metodosMap.put("Yape / Plin", 0.0);
        metodosMap.put("Transferencia Bancaria", 0.0);
        metodosMap.put("Tarjeta Débito/Crédito", 0.0);

        modeloVentas.setRowCount(0);

        String whereFecha = construirClausulaWhereFecha();

        try (Connection conn = ConexionBD.conectar()) {
            if (conn != null) {
                String sqlVentas = "SELECT id_venta, COALESCE(cliente, '-'), COALESCE(mano_obra, 0), COALESCE(repuestos, 0), " +
                        "COALESCE(total, 0), COALESCE(metodo_pago, 'Efectivo'), TO_CHAR(fecha, 'DD/MM HH24:MI') " +
                        "FROM ventas " + whereFecha + " ORDER BY id_venta DESC";

                try (PreparedStatement pstmt = conn.prepareStatement(sqlVentas);
                     ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        int id = rs.getInt(1);
                        String cli = rs.getString(2);
                        double mo = rs.getDouble(3);
                        double rep = rs.getDouble(4);
                        double tot = rs.getDouble(5);
                        String met = rs.getString(6);
                        String fch = rs.getString(7);

                        totalVentas += tot;
                        totalManoObra += mo;
                        totalRepuestos += rep;
                        cantidadVentas++;

                        if (met.toLowerCase().contains("efectivo")) {
                            sumEfectivo += tot;
                            metodosMap.put("Efectivo", metodosMap.get("Efectivo") + tot);
                        } else if (met.toLowerCase().contains("yape") || met.toLowerCase().contains("plin")) {
                            sumYape += tot;
                            metodosMap.put("Yape / Plin", metodosMap.get("Yape / Plin") + tot);
                        } else if (met.toLowerCase().contains("transf")) {
                            sumTransf += tot;
                            metodosMap.put("Transferencia Bancaria", metodosMap.get("Transferencia Bancaria") + tot);
                        } else {
                            sumTarjeta += tot;
                            metodosMap.put("Tarjeta Débito/Crédito", metodosMap.get("Tarjeta Débito/Crédito") + tot);
                        }

                        modeloVentas.addRow(new Object[]{id, cli, String.format("%.2f", tot), met, fch});
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al cargar ventas en dashboard: " + e.getMessage());
        }

        int cantidadClientes = 0;
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) FROM clientes");
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) cantidadClientes = rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Error al contar clientes: " + e.getMessage());
        }

        int cantidadMecanicos = 0;
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) FROM mecanicos");
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) cantidadMecanicos = rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Error al contar mecánicos: " + e.getMessage());
        }

        lblTotalVentas.setText("S/. " + String.format("%.2f", totalVentas));
        lblTotalServicios.setText(String.valueOf(cantidadVentas));
        lblTotalClientes.setText(String.valueOf(cantidadClientes));
        lblTotalMecanicos.setText(String.valueOf(cantidadMecanicos));

        lblEfectivo.setText("S/. " + String.format("%.2f", sumEfectivo));
        lblYape.setText("S/. " + String.format("%.2f", sumYape));
        lblTransf.setText("S/. " + String.format("%.2f", sumTransf));
        lblTarjeta.setText("S/. " + String.format("%.2f", sumTarjeta));

        panelDonut.actualizarDatos(metodosMap, totalVentas);
        panelBarras.actualizarDatos(totalManoObra, totalRepuestos);
    }

    private static class GraficoDonutMetodos extends JPanel {
        private Map<String, Double> datos = new LinkedHashMap<>();
        private double totalRecaudado = 0.0;

        public GraficoDonutMetodos() {
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                    new EmptyBorder(14, 14, 14, 14)
            ));
        }

        public void actualizarDatos(Map<String, Double> map, double total) {
            this.datos = map;
            this.totalRecaudado = total;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
            g2.setColor(new Color(45, 55, 72));
            g2.drawString("Distribución por Método", 15, 20);

            if (totalRecaudado <= 0) {
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                g2.setColor(new Color(148, 163, 184));
                g2.drawString("Sin transacciones en este período.", 15, 60);
                g2.dispose();
                return;
            }

            int diametro = Math.min(getWidth() - 30, getHeight() - 90);
            int x = (getWidth() - diametro) / 2;
            int y = 35;

            Color[] paleta = {
                    new Color(16, 185, 129),
                    new Color(147, 51, 234),
                    new Color(37, 99, 235),
                    new Color(217, 119, 6)
            };

            int startAngle = 90;
            int i = 0;
            for (Map.Entry<String, Double> e : datos.entrySet()) {
                double val = e.getValue();
                if (val > 0) {
                    int arcAngle = (int) Math.round((val / totalRecaudado) * 360.0);
                    g2.setColor(paleta[i % paleta.length]);
                    g2.fillArc(x, y, diametro, diametro, startAngle, arcAngle);
                    startAngle += arcAngle;
                }
                i++;
            }

            int hueco = (int) (diametro * 0.58);
            int xHueco = x + (diametro - hueco) / 2;
            int yHueco = y + (diametro - hueco) / 2;
            g2.setColor(Color.WHITE);
            g2.fillOval(xHueco, yHueco, hueco, hueco);

            g2.setColor(new Color(30, 41, 59));
            g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
            String txtTotal = "S/." + String.format("%.0f", totalRecaudado);
            int strW = g2.getFontMetrics().stringWidth(txtTotal);
            g2.drawString(txtTotal, xHueco + (hueco - strW) / 2, yHueco + (hueco / 2) + 4);

            int ly = y + diametro + 18;
            int lx = 15;
            i = 0;
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            for (Map.Entry<String, Double> e : datos.entrySet()) {
                if (e.getValue() > 0) {
                    g2.setColor(paleta[i % paleta.length]);
                    g2.fillRect(lx, ly, 8, 8);
                    g2.setColor(new Color(71, 85, 105));
                    int pct = (int) Math.round((e.getValue() / totalRecaudado) * 100);
                    g2.drawString(e.getKey() + " (" + pct + "%)", lx + 12, ly + 8);
                    lx += 85;
                    if (lx > getWidth() - 90) {
                        lx = 15;
                        ly += 14;
                    }
                }
                i++;
            }

            g2.dispose();
        }
    }

    private static class GraficoBarrasCostos extends JPanel {
        private double manoObra = 0.0;
        private double repuestos = 0.0;

        public GraficoBarrasCostos() {
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                    new EmptyBorder(14, 14, 14, 14)
            ));
        }

        public void actualizarDatos(double mo, double rep) {
            this.manoObra = mo;
            this.repuestos = rep;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
            g2.setColor(new Color(45, 55, 72));
            g2.drawString("Origen de Ingresos (S/.)", 15, 20);

            double maxVal = Math.max(manoObra, repuestos);
            if (maxVal <= 0) {
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                g2.setColor(new Color(148, 163, 184));
                g2.drawString("Sin datos registrados en el período.", 15, 60);
                g2.dispose();
                return;
            }

            int yBase = getHeight() - 40;
            int alturaMax = getHeight() - 85;
            int anchoBarra = Math.min(50, (getWidth() - 80) / 4);

            int x1 = getWidth() / 4 - (anchoBarra / 2);
            int altoBarra1 = (int) ((manoObra / maxVal) * alturaMax);
            int yBarra1 = yBase - altoBarra1;

            g2.setColor(new Color(29, 78, 216));
            g2.fillRoundRect(x1, yBarra1, anchoBarra, altoBarra1, 8, 8);

            g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
            g2.setColor(new Color(30, 41, 59));
            String moStr = "S/." + String.format("%.0f", manoObra);
            g2.drawString(moStr, x1 - 2, yBarra1 - 5);

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            g2.setColor(new Color(100, 116, 139));
            g2.drawString("Mano Obra", x1 - 8, yBase + 16);

            int x2 = (getWidth() * 3) / 4 - (anchoBarra / 2);
            int altoBarra2 = (int) ((repuestos / maxVal) * alturaMax);
            int yBarra2 = yBase - altoBarra2;

            g2.setColor(new Color(217, 119, 6));
            g2.fillRoundRect(x2, yBarra2, anchoBarra, altoBarra2, 8, 8);

            g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
            g2.setColor(new Color(30, 41, 59));
            String repStr = "S/." + String.format("%.0f", repuestos);
            g2.drawString(repStr, x2 - 2, yBarra2 - 5);

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            g2.setColor(new Color(100, 116, 139));
            g2.drawString("Repuestos", x2 - 6, yBase + 16);

            g2.setColor(new Color(226, 232, 240));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawLine(15, yBase, getWidth() - 15, yBase);

            g2.dispose();
        }
    }
}