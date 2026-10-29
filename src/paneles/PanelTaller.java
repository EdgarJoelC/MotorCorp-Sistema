package paneles;

import dao.MecanicoDAO;
import dao.OrdenTallerDAO;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
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
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class PanelTaller extends JPanel {

    private JTextField txtBuscarCliente, txtVehiculo, txtPlaca, txtFalla, txtManoObra;
    private JComboBox<ClienteItem> cmbClientes;
    private JComboBox<MecanicoItem> cmbMecanicos;
    private JButton btnRegistrarOrden, btnLimpiar;
    private JTable tablaCola;
    private DefaultTableModel modeloTabla;
    private OrdenTallerDAO ordenTallerDAO;
    private MecanicoDAO mecanicoDAO;
    private List<ClienteItem> listaClientesMemoria;

    public PanelTaller() {
        ordenTallerDAO = new OrdenTallerDAO();
        mecanicoDAO = new MecanicoDAO();
        listaClientesMemoria = new ArrayList<>();

        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel lblTitulo = new JLabel("Cola de Espera - Recepción y Orden de Llegada");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(45, 55, 72));
        add(lblTitulo, BorderLayout.NORTH);

        JPanel panelPrincipal = new JPanel(new GridLayout(1, 2, 20, 0));
        panelPrincipal.setOpaque(false);

        panelPrincipal.add(crearPanelRecepcion());
        panelPrincipal.add(crearPanelColaLlegada());

        add(panelPrincipal, BorderLayout.CENTER);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                cargarColaBD();
                cargarDatosIniciales();
            }
        });

        cargarDatosIniciales();
        cargarColaBD();
    }

    private JPanel crearPanelRecepcion() {
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

        JLabel lblSub = new JLabel("Recepción de Vehículo");
        lblSub.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblSub.setForeground(new Color(224, 43, 32));
        gbc.gridy = 0;
        panel.add(lblSub, gbc);

        gbc.gridy = 1;
        panel.add(crearLabel("Filtrar Cliente:"), gbc);
        txtBuscarCliente = crearTextField("Escriba para filtrar...");
        gbc.gridy = 2;
        panel.add(txtBuscarCliente, gbc);

        gbc.gridy = 3;
        panel.add(crearLabel("Cliente Seleccionado:"), gbc);
        cmbClientes = new JComboBox<>();
        cmbClientes.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbClientes.setPreferredSize(new Dimension(200, 32));
        cmbClientes.addActionListener(e -> autoCompletarDatosCliente());
        gbc.gridy = 4;
        panel.add(cmbClientes, gbc);

        gbc.gridy = 5;
        panel.add(crearLabel("Modelo de Vehículo:"), gbc);
        txtVehiculo = crearTextField("Ej: Toyota Yaris");
        gbc.gridy = 6;
        panel.add(txtVehiculo, gbc);

        gbc.gridy = 7;
        panel.add(crearLabel("Placa del Vehículo:"), gbc);
        txtPlaca = crearTextField("Ej: AEA-312");
        gbc.gridy = 8;
        panel.add(txtPlaca, gbc);

        gbc.gridy = 9;
        panel.add(crearLabel("Mecánico Asignado:"), gbc);
        cmbMecanicos = new JComboBox<>();
        cmbMecanicos.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbMecanicos.setPreferredSize(new Dimension(200, 32));
        gbc.gridy = 10;
        panel.add(cmbMecanicos, gbc);

        gbc.gridy = 11;
        panel.add(crearLabel("Descripción de la Falla / Servicio:"), gbc);
        txtFalla = crearTextField("Ej: Mantenimiento y frenos");
        gbc.gridy = 12;
        panel.add(txtFalla, gbc);

        gbc.gridy = 13;
        panel.add(crearLabel("Precio Base Mano de Obra (S/.):"), gbc);
        txtManoObra = crearTextField("Ej: 80.00");
        gbc.gridy = 14;
        panel.add(txtManoObra, gbc);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBotones.setOpaque(false);

        btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLimpiar.setFocusPainted(false);
        btnLimpiar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLimpiar.putClientProperty("JButton.buttonType", "roundRect");
        btnLimpiar.addActionListener(e -> limpiarCampos());

        btnRegistrarOrden = new JButton("Ingresar a Cola");
        btnRegistrarOrden.setBackground(new Color(224, 43, 32));
        btnRegistrarOrden.setForeground(Color.WHITE);
        btnRegistrarOrden.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnRegistrarOrden.setFocusPainted(false);
        btnRegistrarOrden.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegistrarOrden.putClientProperty("JButton.buttonType", "roundRect");
        btnRegistrarOrden.addActionListener(e -> registrarEnCola());

        panelBotones.add(btnLimpiar);
        panelBotones.add(btnRegistrarOrden);

        gbc.gridy = 15;
        gbc.insets = new Insets(14, 0, 0, 0);
        panel.add(panelBotones, gbc);

        txtBuscarCliente.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { filtrarClientes(); }
            @Override
            public void removeUpdate(DocumentEvent e) { filtrarClientes(); }
            @Override
            public void changedUpdate(DocumentEvent e) { filtrarClientes(); }
        });

        return panel;
    }

    private JPanel crearPanelColaLlegada() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JPanel panelTop = new JPanel(new BorderLayout());
        panelTop.setOpaque(false);

        JLabel lblListado = new JLabel("Servicios en Cola (Orden de Llegada)");
        lblListado.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblListado.setForeground(new Color(45, 55, 72));
        panelTop.add(lblListado, BorderLayout.WEST);

        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnActualizar.setFocusPainted(false);
        btnActualizar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnActualizar.addActionListener(e -> cargarColaBD());
        panelTop.add(btnActualizar, BorderLayout.EAST);

        panel.add(panelTop, BorderLayout.NORTH);

        String[] columnas = {"N°", "Cliente", "Vehículo", "Placa", "Mecánico", "Falla / Servicio", "Mano Obra", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaCola = new JTable(modeloTabla);
        tablaCola.setRowHeight(32);
        tablaCola.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaCola.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaCola.getTableHeader().setBackground(new Color(241, 245, 249));

        tablaCola.getColumnModel().getColumn(0).setPreferredWidth(30);
        tablaCola.getColumnModel().getColumn(1).setPreferredWidth(100);
        tablaCola.getColumnModel().getColumn(2).setPreferredWidth(85);
        tablaCola.getColumnModel().getColumn(3).setPreferredWidth(65);
        tablaCola.getColumnModel().getColumn(4).setPreferredWidth(95);
        tablaCola.getColumnModel().getColumn(5).setPreferredWidth(125);
        tablaCola.getColumnModel().getColumn(6).setPreferredWidth(70);
        tablaCola.getColumnModel().getColumn(7).setPreferredWidth(100);

        tablaCola.getColumnModel().getColumn(7).setCellRenderer(new EstadoBadgeRenderer());

        JScrollPane scroll = new JScrollPane(tablaCola);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        panel.add(scroll, BorderLayout.CENTER);

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

    public void cargarDatosIniciales() {
        listaClientesMemoria.clear();
        cmbClientes.removeAllItems();
        List<Object[]> clientes = ordenTallerDAO.listarClientesCompletos();
        for (Object[] c : clientes) {
            int id = (int) c[0];
            String nombre = c[1] != null ? c[1].toString() : "";
            String dni = c[2] != null ? c[2].toString() : "";
            String vehiculo = c[3] != null ? c[3].toString() : "";
            String placa = c[4] != null ? c[4].toString() : "";

            ClienteItem item = new ClienteItem(id, nombre, dni, vehiculo, placa);
            listaClientesMemoria.add(item);
            cmbClientes.addItem(item);
        }

        cmbMecanicos.removeAllItems();
        List<Object[]> mecanicos = mecanicoDAO.listarParaCombo();
        for (Object[] m : mecanicos) {
            int id = (int) m[0];
            String nombre = m[1].toString();
            String especialidad = m[2].toString();
            cmbMecanicos.addItem(new MecanicoItem(id, nombre, especialidad));
        }
    }

    private void filtrarClientes() {
        String query = txtBuscarCliente.getText().trim().toLowerCase();
        cmbClientes.removeAllItems();
        for (ClienteItem item : listaClientesMemoria) {
            if (query.isEmpty() ||
                    item.getNombre().toLowerCase().contains(query) ||
                    item.getDni().toLowerCase().contains(query) ||
                    item.getPlaca().toLowerCase().contains(query)) {
                cmbClientes.addItem(item);
            }
        }
    }

    private void autoCompletarDatosCliente() {
        ClienteItem item = (ClienteItem) cmbClientes.getSelectedItem();
        if (item != null) {
            txtVehiculo.setText(item.getVehiculo());
            txtPlaca.setText(item.getPlaca());
        }
    }

    public void cargarColaBD() {
        modeloTabla.setRowCount(0);
        List<Object[]> lista = ordenTallerDAO.listarOrdenesEnCola();
        for (Object[] fila : lista) {
            modeloTabla.addRow(fila);
        }
    }

    private void registrarEnCola() {
        ClienteItem cliente = (ClienteItem) cmbClientes.getSelectedItem();
        MecanicoItem mecanico = (MecanicoItem) cmbMecanicos.getSelectedItem();
        String vehiculo = txtVehiculo.getText().trim();
        String placa = txtPlaca.getText().trim();
        String falla = txtFalla.getText().trim();
        String manoObraStr = txtManoObra.getText().trim();

        if (cliente == null || mecanico == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un cliente y un mecánico.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (falla.isEmpty() || vehiculo.isEmpty() || placa.isEmpty() || manoObraStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar vehículo, placa, falla y precio de mano de obra.", "Campos Requeridos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double manoObra = Double.parseDouble(manoObraStr.replace(",", "."));
            if (ordenTallerDAO.registrar(cliente.getId(), mecanico.getId(), vehiculo, placa, falla, manoObra)) {
                JOptionPane.showMessageDialog(this, "Vehículo ingresado a la cola con mano de obra registrada.");
                limpiarCampos();
                cargarColaBD();
            } else {
                JOptionPane.showMessageDialog(this, "Error al registrar servicio en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El costo de mano de obra debe ser un número válido (ej. 80.00).", "Formato Inválido", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarCampos() {
        txtBuscarCliente.setText("");
        txtVehiculo.setText("");
        txtPlaca.setText("");
        txtFalla.setText("");
        txtManoObra.setText("");
        if (cmbClientes.getItemCount() > 0) cmbClientes.setSelectedIndex(0);
        if (cmbMecanicos.getItemCount() > 0) cmbMecanicos.setSelectedIndex(0);
    }

    private static class ClienteItem {
        private final int id;
        private final String nombre;
        private final String dni;
        private final String vehiculo;
        private final String placa;

        public ClienteItem(int id, String nombre, String dni, String vehiculo, String placa) {
            this.id = id;
            this.nombre = nombre;
            this.dni = dni;
            this.vehiculo = vehiculo;
            this.placa = placa;
        }

        public int getId() { return id; }
        public String getNombre() { return nombre; }
        public String getDni() { return dni; }
        public String getVehiculo() { return vehiculo; }
        public String getPlaca() { return placa; }

        @Override
        public String toString() {
            String str = nombre;
            if (!dni.isEmpty()) str += " | DNI: " + dni;
            if (!placa.isEmpty()) str += " [" + placa + "]";
            return str;
        }
    }

    private static class MecanicoItem {
        private final int id;
        private final String nombre;
        private final String especialidad;

        public MecanicoItem(int id, String nombre, String especialidad) {
            this.id = id;
            this.nombre = nombre;
            this.especialidad = especialidad;
        }

        public int getId() { return id; }

        @Override
        public String toString() {
            return nombre + " (" + especialidad + ")";
        }
    }

    private static class EstadoBadgeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lbl.setOpaque(true);

            String estado = value != null ? value.toString() : "";
            lbl.setText(estado);

            if (isSelected) {
                lbl.setBackground(table.getSelectionBackground());
                lbl.setForeground(table.getSelectionForeground());
                return lbl;
            }

            switch (estado.toLowerCase()) {
                case "recepcionado":
                    lbl.setBackground(new Color(254, 243, 199));
                    lbl.setForeground(new Color(180, 83, 9));
                    break;
                case "en proceso":
                    lbl.setBackground(new Color(219, 234, 254));
                    lbl.setForeground(new Color(29, 78, 216));
                    break;
                case "finalizado":
                case "entregado":
                    lbl.setBackground(new Color(209, 250, 229));
                    lbl.setForeground(new Color(4, 120, 87));
                    break;
                default:
                    lbl.setBackground(new Color(241, 245, 249));
                    lbl.setForeground(new Color(71, 85, 105));
                    break;
            }

            lbl.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(2, 6, 2, 6),
                    BorderFactory.createLineBorder(lbl.getForeground(), 1, true)
            ));

            return lbl;
        }
    }
}