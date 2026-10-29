package paneles;

import dao.ClienteDAO;
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
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class PanelClientes extends JPanel {

    private JTextField txtDni, txtNombres, txtApellidos, txtCelular, txtBuscar;
    private JButton btnRegistrar, btnModificar, btnLimpiar;
    private JTable tablaClientes;
    private DefaultTableModel modeloTabla;
    private ClienteDAO clienteDAO;

    public PanelClientes() {
        clienteDAO = new ClienteDAO();
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel lblTitulo = new JLabel("Gestión y Directorio de Clientes");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(45, 55, 72));
        add(lblTitulo, BorderLayout.NORTH);

        JPanel panelContenido = new JPanel(new GridLayout(1, 2, 20, 0));
        panelContenido.setOpaque(false);

        panelContenido.add(crearPanelFormulario());
        panelContenido.add(crearPanelTabla());

        add(panelContenido, BorderLayout.CENTER);

        cargarClientesBD();
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

        JLabel lblSub = new JLabel("Registrar Nuevo Cliente");
        lblSub.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblSub.setForeground(new Color(224, 43, 32));
        gbc.gridy = 0;
        panel.add(lblSub, gbc);

        gbc.gridy = 1;
        panel.add(crearLabel("DNI (8 dígitos):"), gbc);
        txtDni = crearTextFieldNumerico("Ej: 71234567", 8);
        gbc.gridy = 2;
        panel.add(txtDni, gbc);

        gbc.gridy = 3;
        panel.add(crearLabel("Nombres:"), gbc);
        txtNombres = crearTextField("Ej: Carlos Alberto");
        gbc.gridy = 4;
        panel.add(txtNombres, gbc);

        gbc.gridy = 5;
        panel.add(crearLabel("Apellidos:"), gbc);
        txtApellidos = crearTextField("Ej: Mendoza Pérez");
        gbc.gridy = 6;
        panel.add(txtApellidos, gbc);

        gbc.gridy = 7;
        panel.add(crearLabel("Celular (9 dígitos):"), gbc);
        txtCelular = crearTextFieldNumerico("Ej: 987654321", 9);
        gbc.gridy = 8;
        panel.add(txtCelular, gbc);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBotones.setOpaque(false);

        btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLimpiar.setFocusPainted(false);
        btnLimpiar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLimpiar.putClientProperty("JButton.buttonType", "roundRect");
        btnLimpiar.addActionListener(e -> limpiarCampos());

        btnRegistrar = new JButton("Registrar Cliente");
        btnRegistrar.setBackground(new Color(224, 43, 32));
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnRegistrar.setFocusPainted(false);
        btnRegistrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegistrar.putClientProperty("JButton.buttonType", "roundRect");
        btnRegistrar.addActionListener(e -> registrarCliente());

        panelBotones.add(btnLimpiar);
        panelBotones.add(btnRegistrar);

        gbc.gridy = 9;
        gbc.insets = new Insets(20, 0, 0, 0);
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

        JPanel panelSuperiorTabla = new JPanel(new BorderLayout());
        panelSuperiorTabla.setOpaque(false);

        JLabel lblListado = new JLabel("Directorio Registrado");
        lblListado.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblListado.setForeground(new Color(45, 55, 72));
        panelSuperiorTabla.add(lblListado, BorderLayout.WEST);

        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        panelBusqueda.setOpaque(false);
        txtBuscar = crearTextField("Buscar cliente...");
        txtBuscar.setPreferredSize(new Dimension(190, 30));
        txtBuscar.addActionListener(e -> buscarCliente());
        panelBusqueda.add(txtBuscar);
        panelSuperiorTabla.add(panelBusqueda, BorderLayout.EAST);

        panel.add(panelSuperiorTabla, BorderLayout.NORTH);

        String[] columnas = clienteDAO.obtenerColumnasTabla();
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaClientes = new JTable(modeloTabla);
        tablaClientes.setRowHeight(30);
        tablaClientes.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaClientes.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaClientes.getTableHeader().setBackground(new Color(241, 245, 249));

        JScrollPane scroll = new JScrollPane(tablaClientes);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        panel.add(scroll, BorderLayout.CENTER);

        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelInferior.setOpaque(false);

        btnModificar = new JButton("Modificar Seleccionado");
        btnModificar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnModificar.setBackground(new Color(49, 130, 206));
        btnModificar.setForeground(Color.WHITE);
        btnModificar.setFocusPainted(false);
        btnModificar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnModificar.putClientProperty("JButton.buttonType", "roundRect");
        btnModificar.addActionListener(e -> abrirModalModificar());

        panelInferior.add(btnModificar);
        panel.add(panelInferior, BorderLayout.SOUTH);

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
        txt.setPreferredSize(new Dimension(200, 34));
        txt.putClientProperty("JTextField.placeholderText", placeholder);
        txt.putClientProperty("JComponent.roundRect", true);
        return txt;
    }

    private JTextField crearTextFieldNumerico(String placeholder, int maxDigitos) {
        JTextField txt = crearTextField(placeholder);
        ((AbstractDocument) txt.getDocument()).setDocumentFilter(new LimiteNumericoFilter(maxDigitos));
        return txt;
    }

    private void cargarClientesBD() {
        modeloTabla.setRowCount(0);
        List<Object[]> datos = clienteDAO.listar();
        for (Object[] fila : datos) {
            modeloTabla.addRow(fila);
        }
    }

    private void registrarCliente() {
        String dni = txtDni.getText().trim();
        String nombres = txtNombres.getText().trim();
        String apellidos = txtApellidos.getText().trim();
        String celular = txtCelular.getText().trim();

        if (nombres.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar al menos los nombres.", "Campo Requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (celular.length() != 9) {
            JOptionPane.showMessageDialog(this, "El celular debe contener exactamente 9 dígitos.", "Celular Inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!dni.isEmpty() && dni.length() != 8) {
            JOptionPane.showMessageDialog(this, "El DNI debe contener exactamente 8 dígitos.", "DNI Inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (clienteDAO.registrar(dni, nombres, apellidos, celular)) {
            JOptionPane.showMessageDialog(this, "Cliente registrado exitosamente.");
            limpiarCampos();
            cargarClientesBD();
        } else {
            JOptionPane.showMessageDialog(this, "Error al registrar cliente en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirModalModificar() {
        int filaSel = tablaClientes.getSelectedRow();
        if (filaSel == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente de la lista para modificar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(modeloTabla.getValueAt(filaSel, 0).toString());
        String dniActual = "";
        String nombreActual = "";
        String celularActual = "";

        int totalCols = modeloTabla.getColumnCount();
        if (totalCols >= 2 && modeloTabla.getValueAt(filaSel, 1) != null) {
            nombreActual = modeloTabla.getValueAt(filaSel, 1).toString();
        }
        if (totalCols >= 3 && modeloTabla.getValueAt(filaSel, 2) != null) {
            celularActual = modeloTabla.getValueAt(filaSel, 2).toString();
        }
        for (int c = 0; c < totalCols; c++) {
            String colNom = modeloTabla.getColumnName(c).toLowerCase();
            if (colNom.contains("dni") && modeloTabla.getValueAt(filaSel, c) != null) {
                dniActual = modeloTabla.getValueAt(filaSel, c).toString();
            }
        }

        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        JDialog modal = new JDialog(parentFrame, "Modificar Cliente - ID #" + id, true);
        modal.setSize(420, 480);
        modal.setLocationRelativeTo(this);
        modal.setResizable(false);

        JPanel panelModal = new JPanel(new GridBagLayout());
        panelModal.setBackground(Color.WHITE);
        panelModal.setBorder(new EmptyBorder(25, 30, 25, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        JLabel lblHeader = new JLabel("Editar Datos del Cliente");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblHeader.setForeground(new Color(45, 55, 72));
        gbc.gridy = 0;
        panelModal.add(lblHeader, gbc);

        gbc.gridy = 1;
        panelModal.add(crearLabel("DNI (8 dígitos):"), gbc);
        JTextField txtEditDni = crearTextFieldNumerico("DNI", 8);
        txtEditDni.setText(dniActual);
        gbc.gridy = 2;
        panelModal.add(txtEditDni, gbc);

        gbc.gridy = 3;
        panelModal.add(crearLabel("Nombres / Razón:"), gbc);
        JTextField txtEditNombres = crearTextField("Nombres");
        txtEditNombres.setText(nombreActual);
        gbc.gridy = 4;
        panelModal.add(txtEditNombres, gbc);

        gbc.gridy = 5;
        panelModal.add(crearLabel("Apellidos:"), gbc);
        JTextField txtEditApellidos = crearTextField("Apellidos");
        gbc.gridy = 6;
        panelModal.add(txtEditApellidos, gbc);

        gbc.gridy = 7;
        panelModal.add(crearLabel("Celular (9 dígitos):"), gbc);
        JTextField txtEditCelular = crearTextFieldNumerico("Celular", 9);
        txtEditCelular.setText(celularActual);
        gbc.gridy = 8;
        panelModal.add(txtEditCelular, gbc);

        JPanel panelBotonesModal = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBotonesModal.setOpaque(false);

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnCancelar.setFocusPainted(false);
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancelar.putClientProperty("JButton.buttonType", "roundRect");
        btnCancelar.addActionListener(e -> modal.dispose());

        JButton btnGuardar = new JButton("Guardar Cambios");
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnGuardar.setBackground(new Color(49, 130, 206));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGuardar.putClientProperty("JButton.buttonType", "roundRect");

        btnGuardar.addActionListener(e -> {
            String nuevoDni = txtEditDni.getText().trim();
            String nuevosNombres = txtEditNombres.getText().trim();
            String nuevosApellidos = txtEditApellidos.getText().trim();
            String nuevoCelular = txtEditCelular.getText().trim();

            if (nuevosNombres.isEmpty()) {
                JOptionPane.showMessageDialog(modal, "El nombre es obligatorio.", "Campo Requerido", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (nuevoCelular.length() != 9) {
                JOptionPane.showMessageDialog(modal, "El celular debe contener exactamente 9 dígitos.", "Celular Inválido", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!nuevoDni.isEmpty() && nuevoDni.length() != 8) {
                JOptionPane.showMessageDialog(modal, "El DNI debe contener exactamente 8 dígitos.", "DNI Inválido", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (clienteDAO.modificar(id, nuevoDni, nuevosNombres, nuevosApellidos, nuevoCelular)) {
                JOptionPane.showMessageDialog(modal, "Cliente modificado correctamente.");
                modal.dispose();
                cargarClientesBD();
            } else {
                JOptionPane.showMessageDialog(modal, "Error al actualizar en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panelBotonesModal.add(btnCancelar);
        panelBotonesModal.add(btnGuardar);

        gbc.gridy = 9;
        gbc.insets = new Insets(18, 0, 0, 0);
        panelModal.add(panelBotonesModal, gbc);

        modal.setContentPane(panelModal);
        modal.setVisible(true);
    }

    private void buscarCliente() {
        String filtro = txtBuscar.getText().trim();
        if (filtro.isEmpty()) {
            cargarClientesBD();
            return;
        }

        modeloTabla.setRowCount(0);
        List<Object[]> datos = clienteDAO.buscar(filtro);
        for (Object[] fila : datos) {
            modeloTabla.addRow(fila);
        }
    }

    private void limpiarCampos() {
        txtDni.setText("");
        txtNombres.setText("");
        txtApellidos.setText("");
        txtCelular.setText("");
        tablaClientes.clearSelection();
    }

    private static class LimiteNumericoFilter extends DocumentFilter {
        private final int limite;

        public LimiteNumericoFilter(int limite) {
            this.limite = limite;
        }

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (string == null) return;
            if (esNumerico(string) && (fb.getDocument().getLength() + string.length()) <= limite) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (text == null) return;
            if (esNumerico(text) && (fb.getDocument().getLength() - length + text.length()) <= limite) {
                super.replace(fb, offset, length, text, attrs);
            }
        }

        private boolean esNumerico(String texto) {
            return texto.matches("^[0-9]*$");
        }
    }
}