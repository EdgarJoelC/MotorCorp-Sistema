package paneles;

import dao.InventarioDAO;
import dao.MecanicoDAO;
import dao.OrdenTallerDAO;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class PanelInventario extends JPanel {

    private JTextField txtProducto, txtStock, txtPrecio, txtBuscar;
    private JButton btnRegistrar, btnModificar, btnAumentarStock, btnRestarUso;
    private JTable tablaInventario;
    private DefaultTableModel modeloInventario;

    private InventarioDAO inventarioDAO;
    private MecanicoDAO mecanicoDAO;
    private OrdenTallerDAO ordenTallerDAO;

    public PanelInventario() {
        inventarioDAO = new InventarioDAO();
        mecanicoDAO = new MecanicoDAO();
        ordenTallerDAO = new OrdenTallerDAO();

        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel lblTitulo = new JLabel("Inventario Avanzado - Repuestos y Materiales");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(45, 55, 72));
        add(lblTitulo, BorderLayout.NORTH);

        JPanel panelContenedor = new JPanel(new GridLayout(1, 2, 20, 0));
        panelContenedor.setOpaque(false);

        panelContenedor.add(crearPanelFormulario());
        panelContenedor.add(crearPanelTabla());

        add(panelContenedor, BorderLayout.CENTER);

        cargarInventarioBD();
    }

    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        JLabel lblSub = new JLabel("Gestión de Producto / Repuesto");
        lblSub.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblSub.setForeground(new Color(224, 43, 32));
        gbc.gridy = 0;
        panel.add(lblSub, gbc);

        gbc.gridy = 1;
        panel.add(crearLabel("Nombre del Producto / Repuesto:"), gbc);
        txtProducto = crearTextField("Ej: Pastillas de freno");
        gbc.gridy = 2;
        panel.add(txtProducto, gbc);

        gbc.gridy = 3;
        panel.add(crearLabel("Stock Inicial / Cantidad:"), gbc);
        txtStock = crearTextField("Ej: 20");
        gbc.gridy = 4;
        panel.add(txtStock, gbc);

        gbc.gridy = 5;
        panel.add(crearLabel("Precio Unitario (S/.):"), gbc);
        txtPrecio = crearTextField("Ej: 45.00");
        gbc.gridy = 6;
        panel.add(txtPrecio, gbc);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBotones.setOpaque(false);

        btnModificar = new JButton("Modificar Datos");
        btnModificar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnModificar.setFocusPainted(false);
        btnModificar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnModificar.putClientProperty("JButton.buttonType", "roundRect");
        btnModificar.addActionListener(e -> modificarProducto());

        btnRegistrar = new JButton("Registrar Nuevo");
        btnRegistrar.setBackground(new Color(224, 43, 32));
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnRegistrar.setFocusPainted(false);
        btnRegistrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegistrar.putClientProperty("JButton.buttonType", "roundRect");
        btnRegistrar.addActionListener(e -> registrarProducto());

        panelBotones.add(btnModificar);
        panelBotones.add(btnRegistrar);

        gbc.gridy = 7;
        gbc.insets = new Insets(18, 0, 0, 0);
        panel.add(panelBotones, gbc);

        return panel;
    }

    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JPanel panelTop = new JPanel(new BorderLayout());
        panelTop.setOpaque(false);

        JLabel lblListado = new JLabel("Listado de Inventario");
        lblListado.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblListado.setForeground(new Color(45, 55, 72));
        panelTop.add(lblListado, BorderLayout.WEST);

        txtBuscar = crearTextField("Buscar repuesto...");
        txtBuscar.setPreferredSize(new Dimension(180, 30));
        txtBuscar.addActionListener(e -> buscarInventario());
        panelTop.add(txtBuscar, BorderLayout.EAST);

        panel.add(panelTop, BorderLayout.NORTH);

        String[] columnas = {"ID", "Producto", "Stock", "Precio (S/.)"};
        modeloInventario = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaInventario = new JTable(modeloInventario);
        tablaInventario.setRowHeight(30);
        tablaInventario.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaInventario.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaInventario.getTableHeader().setBackground(new Color(241, 245, 249));

        tablaInventario.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaInventario.getSelectedRow() != -1) {
                int fila = tablaInventario.getSelectedRow();
                txtProducto.setText(modeloInventario.getValueAt(fila, 1).toString());
                txtStock.setText(modeloInventario.getValueAt(fila, 2).toString());
                txtPrecio.setText(modeloInventario.getValueAt(fila, 3).toString());
            }
        });

        JScrollPane scroll = new JScrollPane(tablaInventario);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        panel.add(scroll, BorderLayout.CENTER);

        JPanel panelAccionesStock = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelAccionesStock.setOpaque(false);

        btnAumentarStock = new JButton("Aumentar Stock");
        btnAumentarStock.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAumentarStock.setBackground(new Color(16, 185, 129));
        btnAumentarStock.setForeground(Color.WHITE);
        btnAumentarStock.setFocusPainted(false);
        btnAumentarStock.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAumentarStock.putClientProperty("JButton.buttonType", "roundRect");
        btnAumentarStock.addActionListener(e -> aumentarStock());

        btnRestarUso = new JButton("Restar por Uso");
        btnRestarUso.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnRestarUso.setBackground(new Color(217, 119, 6));
        btnRestarUso.setForeground(Color.WHITE);
        btnRestarUso.setFocusPainted(false);
        btnRestarUso.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRestarUso.putClientProperty("JButton.buttonType", "roundRect");
        btnRestarUso.addActionListener(e -> abrirModalAsignarRepuesto());

        panelAccionesStock.add(btnAumentarStock);
        panelAccionesStock.add(btnRestarUso);

        panel.add(panelAccionesStock, BorderLayout.SOUTH);

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

    private void cargarInventarioBD() {
        modeloInventario.setRowCount(0);
        List<Object[]> lista = inventarioDAO.listar();
        for (Object[] fila : lista) {
            modeloInventario.addRow(fila);
        }
    }

    private void registrarProducto() {
        String prod = txtProducto.getText().trim();
        String stockStr = txtStock.getText().trim();
        String precioStr = txtPrecio.getText().trim();

        if (prod.isEmpty() || stockStr.isEmpty() || precioStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos del producto.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int stock = Integer.parseInt(stockStr);
            double precio = Double.parseDouble(precioStr.replace(",", "."));
            if (inventarioDAO.registrar(prod, stock, precio)) {
                JOptionPane.showMessageDialog(this, "Producto registrado correctamente.");
                limpiarCampos();
                cargarInventarioBD();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Stock y precio deben tener formato numérico válido.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modificarProducto() {
        int fila = tablaInventario.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un repuesto de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(modeloInventario.getValueAt(fila, 0).toString());
        String prod = txtProducto.getText().trim();
        String stockStr = txtStock.getText().trim();
        String precioStr = txtPrecio.getText().trim();

        try {
            int stock = Integer.parseInt(stockStr);
            double precio = Double.parseDouble(precioStr.replace(",", "."));
            if (inventarioDAO.modificar(id, prod, stock, precio)) {
                JOptionPane.showMessageDialog(this, "Repuesto actualizado.");
                limpiarCampos();
                cargarInventarioBD();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Revise los valores numéricos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void aumentarStock() {
        int fila = tablaInventario.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un repuesto.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(modeloInventario.getValueAt(fila, 0).toString());
        String cantStr = JOptionPane.showInputDialog(this, "Ingrese la cantidad a ingresar al stock:", "Aumentar Stock", JOptionPane.QUESTION_MESSAGE);
        if (cantStr != null && !cantStr.trim().isEmpty()) {
            try {
                int cant = Integer.parseInt(cantStr.trim());
                if (cant > 0 && inventarioDAO.aumentarStock(id, cant)) {
                    cargarInventarioBD();
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Número inválido.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void abrirModalAsignarRepuesto() {
        int fila = tablaInventario.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un repuesto de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idProducto = Integer.parseInt(modeloInventario.getValueAt(fila, 0).toString());
        String nomProducto = modeloInventario.getValueAt(fila, 1).toString();
        int stockActual = Integer.parseInt(modeloInventario.getValueAt(fila, 2).toString());
        double precioUnitario = Double.parseDouble(modeloInventario.getValueAt(fila, 3).toString().replace(",", "."));

        if (stockActual <= 0) {
            JOptionPane.showMessageDialog(this, "No hay stock disponible de este repuesto.", "Sin Stock", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        JDialog modal = new JDialog(parentFrame, "Asignar Repuesto a Servicio", true);
        modal.setSize(480, 390);
        modal.setLocationRelativeTo(this);
        modal.setResizable(false);

        JPanel panelModal = new JPanel(new GridBagLayout());
        panelModal.setBackground(Color.WHITE);
        panelModal.setBorder(new EmptyBorder(20, 25, 20, 25));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        JLabel lblHeader = new JLabel("Consumir: " + nomProducto + " (S/. " + String.format("%.2f", precioUnitario) + " c/u)");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblHeader.setForeground(new Color(224, 43, 32));
        gbc.gridy = 0;
        panelModal.add(lblHeader, gbc);

        gbc.gridy = 1;
        panelModal.add(crearLabel("Mecánico que retira:"), gbc);
        JComboBox<ComboItem> cmbMec = new JComboBox<>();
        cmbMec.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        List<Object[]> mecs = mecanicoDAO.listarParaCombo();
        for (Object[] m : mecs) {
            cmbMec.addItem(new ComboItem((int) m[0], m[1].toString() + " (" + m[2].toString() + ")"));
        }
        gbc.gridy = 2;
        panelModal.add(cmbMec, gbc);

        gbc.gridy = 3;
        panelModal.add(crearLabel("Servicio Activo en Proceso:"), gbc);
        JComboBox<ComboItem> cmbOrdenes = new JComboBox<>();
        cmbOrdenes.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridy = 4;
        panelModal.add(cmbOrdenes, gbc);

        Runnable recargarOrdenes = () -> {
            cmbOrdenes.removeAllItems();
            ComboItem mecSel = (ComboItem) cmbMec.getSelectedItem();
            if (mecSel != null) {
                List<Object[]> ordenes = ordenTallerDAO.listarOrdenesActivasPorMecanico(mecSel.getId());
                for (Object[] o : ordenes) {
                    int idOrd = (int) o[0];
                    String veh = o[1].toString();
                    String plc = o[2].toString();
                    String prob = o[3].toString();
                    cmbOrdenes.addItem(new ComboItem(idOrd, "Orden #" + idOrd + " - " + veh + " [" + plc + "]: " + prob));
                }
            }
            if (cmbOrdenes.getItemCount() == 0) {
                cmbOrdenes.addItem(new ComboItem(-1, "Sin órdenes activas"));
            }
        };

        cmbMec.addActionListener(e -> recargarOrdenes.run());
        recargarOrdenes.run();

        gbc.gridy = 5;
        panelModal.add(crearLabel("Cantidad a utilizar (Stock disponible: " + stockActual + "):"), gbc);
        JSpinner spinCantidad = new JSpinner(new SpinnerNumberModel(1, 1, stockActual, 1));
        spinCantidad.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridy = 6;
        panelModal.add(spinCantidad, gbc);

        JPanel panelBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBtns.setOpaque(false);

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnCancelar.setFocusPainted(false);
        btnCancelar.addActionListener(e -> modal.dispose());

        JButton btnConfirmar = new JButton("Descontar y Cargar a Cuenta");
        btnConfirmar.setBackground(new Color(217, 119, 6));
        btnConfirmar.setForeground(Color.WHITE);
        btnConfirmar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnConfirmar.setFocusPainted(false);
        btnConfirmar.addActionListener(e -> {
            ComboItem ordenSel = (ComboItem) cmbOrdenes.getSelectedItem();
            if (ordenSel == null || ordenSel.getId() == -1) {
                JOptionPane.showMessageDialog(modal, "El mecánico seleccionado debe tener una orden activa asignada.", "Sin Orden Activa", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int cant = (int) spinCantidad.getValue();
            double costoTotalRepuestos = cant * precioUnitario;

            if (inventarioDAO.restarStock(idProducto, cant)) {
                ordenTallerDAO.agregarCostoRepuesto(ordenSel.getId(), costoTotalRepuestos);
                JOptionPane.showMessageDialog(modal, "Se descontaron " + cant + " unidades.\nS/. " + String.format("%.2f", costoTotalRepuestos) + " cargados a la Orden #" + ordenSel.getId() + ".");
                modal.dispose();
                cargarInventarioBD();
            } else {
                JOptionPane.showMessageDialog(modal, "Error al descontar el inventario en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panelBtns.add(btnCancelar);
        panelBtns.add(btnConfirmar);

        gbc.gridy = 7;
        gbc.insets = new Insets(18, 0, 0, 0);
        panelModal.add(panelBtns, gbc);

        modal.setContentPane(panelModal);
        modal.setVisible(true);
    }

    private void buscarInventario() {
        String filtro = txtBuscar.getText().trim();
        if (filtro.isEmpty()) {
            cargarInventarioBD();
            return;
        }
        modeloInventario.setRowCount(0);
        List<Object[]> lista = inventarioDAO.buscar(filtro);
        for (Object[] fila : lista) {
            modeloInventario.addRow(fila);
        }
    }

    private void limpiarCampos() {
        txtProducto.setText("");
        txtStock.setText("");
        txtPrecio.setText("");
        tablaInventario.clearSelection();
    }

    private static class ComboItem {
        private final int id;
        private final String texto;

        public ComboItem(int id, String texto) {
            this.id = id;
            this.texto = texto;
        }

        public int getId() { return id; }
        @Override
        public String toString() { return texto; }
    }
}