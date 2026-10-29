package paneles;

import dao.OrdenTallerDAO;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class PanelPOS extends JPanel {

    private JTable tablaCobro;
    private DefaultTableModel modeloCobro;
    private OrdenTallerDAO ordenTallerDAO;

    private JLabel lblDetalleOrden, lblCliente, lblVehiculoPlaca, lblServicio, lblMecanico;
    private JTextField txtMontoManoObra, txtMontoRepuestos, txtTotalPagar;
    private JComboBox<String> cmbMetodoPago;
    private JButton btnProcesarCobro, btnLimpiar;

    private int idOrdenSeleccionada = -1;
    private String nombreClienteActual = "";
    private String vehiculoActual = "";
    private String placaActual = "";

    public PanelPOS() {
        ordenTallerDAO = new OrdenTallerDAO();

        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel lblTitulo = new JLabel("Punto de Venta - Cobro y Liquidación de Servicios Finalizados");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(45, 55, 72));
        add(lblTitulo, BorderLayout.NORTH);

        JPanel panelContenedor = new JPanel(new GridLayout(1, 2, 20, 0));
        panelContenedor.setOpaque(false);

        panelContenedor.add(crearPanelTablaPendientes());
        panelContenedor.add(crearPanelFacturacion());

        add(panelContenedor, BorderLayout.CENTER);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                cargarOrdenesFinalizadas();
            }
        });

        cargarOrdenesFinalizadas();
    }

    private JPanel crearPanelTablaPendientes() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JPanel panelTop = new JPanel(new BorderLayout());
        panelTop.setOpaque(false);

        JLabel lblListado = new JLabel("Servicios Listos para Cobrar");
        lblListado.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblListado.setForeground(new Color(45, 55, 72));
        panelTop.add(lblListado, BorderLayout.WEST);

        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnActualizar.setFocusPainted(false);
        btnActualizar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnActualizar.addActionListener(e -> cargarOrdenesFinalizadas());
        panelTop.add(btnActualizar, BorderLayout.EAST);

        panel.add(panelTop, BorderLayout.NORTH);

        String[] columnas = {"N°", "Cliente", "DNI", "Vehículo", "Placa", "Mecánico", "Falla / Servicio", "Mano Obra", "Repuestos", "Ingreso"};
        modeloCobro = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaCobro = new JTable(modeloCobro);
        tablaCobro.setRowHeight(32);
        tablaCobro.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaCobro.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaCobro.getTableHeader().setBackground(new Color(241, 245, 249));

        tablaCobro.getColumnModel().getColumn(0).setPreferredWidth(30);
        tablaCobro.getColumnModel().getColumn(1).setPreferredWidth(95);
        tablaCobro.getColumnModel().getColumn(2).setPreferredWidth(65);
        tablaCobro.getColumnModel().getColumn(3).setPreferredWidth(80);
        tablaCobro.getColumnModel().getColumn(4).setPreferredWidth(65);
        tablaCobro.getColumnModel().getColumn(5).setPreferredWidth(90);
        tablaCobro.getColumnModel().getColumn(6).setPreferredWidth(110);
        tablaCobro.getColumnModel().getColumn(7).setPreferredWidth(65);
        tablaCobro.getColumnModel().getColumn(8).setPreferredWidth(65);
        tablaCobro.getColumnModel().getColumn(9).setPreferredWidth(85);

        tablaCobro.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaCobro.getSelectedRow() != -1) {
                seleccionarOrdenParaCobro(tablaCobro.getSelectedRow());
            }
        });

        JScrollPane scroll = new JScrollPane(tablaCobro);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelFacturacion() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 0, 4, 0);
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        lblDetalleOrden = new JLabel("Resumen de Cobro: Ninguna orden seleccionada");
        lblDetalleOrden.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblDetalleOrden.setForeground(new Color(224, 43, 32));
        gbc.gridy = 0;
        panel.add(lblDetalleOrden, gbc);

        gbc.gridy = 1;
        panel.add(new JSeparator(), gbc);

        lblCliente = new JLabel("Cliente: -");
        lblCliente.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridy = 2;
        panel.add(lblCliente, gbc);

        lblVehiculoPlaca = new JLabel("Vehículo / Placa: -");
        lblVehiculoPlaca.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridy = 3;
        panel.add(lblVehiculoPlaca, gbc);

        lblMecanico = new JLabel("Mecánico a cargo: -");
        lblMecanico.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridy = 4;
        panel.add(lblMecanico, gbc);

        lblServicio = new JLabel("Servicio realizado: -");
        lblServicio.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridy = 5;
        panel.add(lblServicio, gbc);

        gbc.gridy = 6;
        panel.add(new JSeparator(), gbc);

        gbc.gridy = 7;
        panel.add(crearLabel("Mano de Obra Base (S/.):"), gbc);
        txtMontoManoObra = crearTextField("0.00");
        txtMontoManoObra.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) { calcularTotal(); }
        });
        gbc.gridy = 8;
        panel.add(txtMontoManoObra, gbc);

        gbc.gridy = 9;
        panel.add(crearLabel("Repuestos y Consumibles Utilizados (S/.):"), gbc);
        txtMontoRepuestos = crearTextField("0.00");
        txtMontoRepuestos.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) { calcularTotal(); }
        });
        gbc.gridy = 10;
        panel.add(txtMontoRepuestos, gbc);

        gbc.gridy = 11;
        panel.add(crearLabel("Medio de Pago:"), gbc);
        cmbMetodoPago = new JComboBox<>(new String[]{"Efectivo", "Yape / Plin", "Transferencia Bancaria", "Tarjeta Débito/Crédito"});
        cmbMetodoPago.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbMetodoPago.setPreferredSize(new Dimension(200, 32));
        gbc.gridy = 12;
        panel.add(cmbMetodoPago, gbc);

        gbc.gridy = 13;
        panel.add(crearLabel("Total a Cobrar (S/.):"), gbc);
        txtTotalPagar = crearTextField("0.00");
        txtTotalPagar.setEditable(false);
        txtTotalPagar.setFont(new Font("Segoe UI", Font.BOLD, 18));
        txtTotalPagar.setForeground(new Color(4, 120, 87));
        txtTotalPagar.setBackground(new Color(241, 245, 249));
        gbc.gridy = 14;
        panel.add(txtTotalPagar, gbc);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBotones.setOpaque(false);

        btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLimpiar.setFocusPainted(false);
        btnLimpiar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLimpiar.putClientProperty("JButton.buttonType", "roundRect");
        btnLimpiar.addActionListener(e -> limpiarFormularioCobro());

        btnProcesarCobro = new JButton("Cobrar y Archivar Servicio");
        btnProcesarCobro.setBackground(new Color(4, 120, 87));
        btnProcesarCobro.setForeground(Color.WHITE);
        btnProcesarCobro.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnProcesarCobro.setEnabled(false);
        btnProcesarCobro.setFocusPainted(false);
        btnProcesarCobro.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnProcesarCobro.putClientProperty("JButton.buttonType", "roundRect");
        btnProcesarCobro.addActionListener(e -> procesarCobro());

        panelBotones.add(btnLimpiar);
        panelBotones.add(btnProcesarCobro);

        gbc.gridy = 15;
        gbc.insets = new Insets(16, 0, 0, 0);
        panel.add(panelBotones, gbc);

        return panel;
    }

    private JLabel crearLabel(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(new Color(71, 85, 105));
        return lbl;
    }

    private JTextField crearTextField(String placeholder) {
        JTextField txt = new JTextField();
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txt.setPreferredSize(new Dimension(200, 32));
        txt.putClientProperty("JTextField.placeholderText", placeholder);
        txt.putClientProperty("JComponent.roundRect", true);
        return txt;
    }

    private void cargarOrdenesFinalizadas() {
        modeloCobro.setRowCount(0);
        List<Object[]> lista = ordenTallerDAO.listarOrdenesParaCobro();
        for (Object[] fila : lista) {
            modeloCobro.addRow(new Object[]{
                    fila[0],
                    fila[1],
                    fila[2],
                    fila[3],
                    fila[4],
                    fila[5],
                    fila[6],
                    String.format("%.2f", (double) fila[7]),
                    String.format("%.2f", (double) fila[8]),
                    fila[9]
            });
        }
        limpiarFormularioCobro();
    }

    private void seleccionarOrdenParaCobro(int fila) {
        idOrdenSeleccionada = Integer.parseInt(modeloCobro.getValueAt(fila, 0).toString());
        nombreClienteActual = modeloCobro.getValueAt(fila, 1).toString();
        String dni = modeloCobro.getValueAt(fila, 2).toString();
        vehiculoActual = modeloCobro.getValueAt(fila, 3).toString();
        placaActual = modeloCobro.getValueAt(fila, 4).toString();
        String mecanico = modeloCobro.getValueAt(fila, 5).toString();
        String servicio = modeloCobro.getValueAt(fila, 6).toString();
        String manoObra = modeloCobro.getValueAt(fila, 7).toString();
        String repuestos = modeloCobro.getValueAt(fila, 8).toString();

        lblDetalleOrden.setText("Liquidación de Orden N° #" + idOrdenSeleccionada);
        lblCliente.setText("Cliente: " + nombreClienteActual + (dni.equals("-") ? "" : " (DNI: " + dni + ")"));
        lblVehiculoPlaca.setText("Vehículo: " + vehiculoActual + " | Placa: " + placaActual);
        lblMecanico.setText("Mecánico: " + mecanico);
        lblServicio.setText("Servicio: " + servicio);

        txtMontoManoObra.setText(manoObra);
        txtMontoRepuestos.setText(repuestos);
        calcularTotal();

        btnProcesarCobro.setEnabled(true);
    }

    private void calcularTotal() {
        try {
            double manoObra = txtMontoManoObra.getText().trim().isEmpty() ? 0.0 : Double.parseDouble(txtMontoManoObra.getText().trim().replace(",", "."));
            double repuestos = txtMontoRepuestos.getText().trim().isEmpty() ? 0.0 : Double.parseDouble(txtMontoRepuestos.getText().trim().replace(",", "."));
            txtTotalPagar.setText(String.format("%.2f", manoObra + repuestos));
        } catch (NumberFormatException e) {
            txtTotalPagar.setText("0.00");
        }
    }

    private void procesarCobro() {
        if (idOrdenSeleccionada == -1) return;

        double total;
        double manoObra;
        double repuestos;

        try {
            manoObra = Double.parseDouble(txtMontoManoObra.getText().trim().replace(",", "."));
            repuestos = Double.parseDouble(txtMontoRepuestos.getText().trim().replace(",", "."));
            total = Double.parseDouble(txtTotalPagar.getText().replace(",", "."));
            if (total <= 0) {
                JOptionPane.showMessageDialog(this, "Debe ingresar un monto válido a cobrar.", "Monto Requerido", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Formato de importe inválido.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String metodo = cmbMetodoPago.getSelectedItem().toString();

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Confirmar cobro de S/. " + String.format("%.2f", total) + " vía " + metodo + "?\nSe guardará en el registro de ventas y el vehículo quedará como ENTREGADO.",
                "Confirmación de Cobro",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            if (ordenTallerDAO.registrarCobroYArchivar(idOrdenSeleccionada, nombreClienteActual, vehiculoActual, placaActual, manoObra, repuestos, total, metodo)) {
                JOptionPane.showMessageDialog(this, "¡Venta guardada y servicio liquidado con éxito!");
                cargarOrdenesFinalizadas();
            } else {
                JOptionPane.showMessageDialog(this, "Error al procesar el cobro en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limpiarFormularioCobro() {
        idOrdenSeleccionada = -1;
        nombreClienteActual = "";
        vehiculoActual = "";
        placaActual = "";
        lblDetalleOrden.setText("Resumen de Cobro: Ninguna orden seleccionada");
        lblCliente.setText("Cliente: -");
        lblVehiculoPlaca.setText("Vehículo / Placa: -");
        lblMecanico.setText("Mecánico a cargo: -");
        lblServicio.setText("Servicio realizado: -");
        txtMontoManoObra.setText("");
        txtMontoRepuestos.setText("");
        txtTotalPagar.setText("0.00");
        btnProcesarCobro.setEnabled(false);
        tablaCobro.clearSelection();
    }
}