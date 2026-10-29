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
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class PanelMecanicos extends JPanel {

    private JTextField txtNombre, txtEspecialidad, txtTelefono;
    private JButton btnRegistrar, btnModificar, btnLimpiar;
    private JTable tablaMecanicos, tablaServicios;
    private DefaultTableModel modeloMecanicos, modeloServicios;
    private JLabel lblServiciosTitulo;
    private JButton btnAvanzarEtapa;

    private MecanicoDAO mecanicoDAO;
    private OrdenTallerDAO ordenTallerDAO;
    private int idMecanicoSeleccionado = -1;

    public PanelMecanicos() {
        mecanicoDAO = new MecanicoDAO();
        ordenTallerDAO = new OrdenTallerDAO();

        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel lblTitulo = new JLabel("Personal Técnico - Asignación y Control de Procesos");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(45, 55, 72));
        add(lblTitulo, BorderLayout.NORTH);

        JPanel panelContenedor = new JPanel(new GridLayout(1, 2, 20, 0));
        panelContenedor.setOpaque(false);

        panelContenedor.add(crearPanelIzquierdoMecanicos());
        panelContenedor.add(crearPanelDerechoProcesos());

        add(panelContenedor, BorderLayout.CENTER);

        cargarMecanicosBD();
    }

    private JPanel crearPanelIzquierdoMecanicos() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setOpaque(false);

        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBackground(Color.WHITE);
        panelForm.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(18, 20, 18, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 0, 4, 0);
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        JLabel lblSub = new JLabel("Registrar Nuevo Mecánico");
        lblSub.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblSub.setForeground(new Color(224, 43, 32));
        gbc.gridy = 0;
        panelForm.add(lblSub, gbc);

        gbc.gridy = 1;
        panelForm.add(crearLabel("Nombre Completo:"), gbc);
        txtNombre = crearTextField("Ej: Roberto Gómez");
        gbc.gridy = 2;
        panelForm.add(txtNombre, gbc);

        gbc.gridy = 3;
        panelForm.add(crearLabel("Especialidad Técnica:"), gbc);
        txtEspecialidad = crearTextField("Ej: Frenos, Motor o Suspensión");
        gbc.gridy = 4;
        panelForm.add(txtEspecialidad, gbc);

        gbc.gridy = 5;
        panelForm.add(crearLabel("Teléfono (9 dígitos):"), gbc);
        txtTelefono = crearTextFieldNumerico("Ej: 991234567", 9);
        gbc.gridy = 6;
        panelForm.add(txtTelefono, gbc);

        JPanel panelBotonesForm = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panelBotonesForm.setOpaque(false);

        btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLimpiar.setFocusPainted(false);
        btnLimpiar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLimpiar.putClientProperty("JButton.buttonType", "roundRect");
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        btnRegistrar = new JButton("Registrar");
        btnRegistrar.setBackground(new Color(224, 43, 32));
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnRegistrar.setFocusPainted(false);
        btnRegistrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegistrar.putClientProperty("JButton.buttonType", "roundRect");
        btnRegistrar.addActionListener(e -> registrarMecanico());

        panelBotonesForm.add(btnLimpiar);
        panelBotonesForm.add(btnRegistrar);

        gbc.gridy = 7;
        gbc.insets = new Insets(12, 0, 0, 0);
        panelForm.add(panelBotonesForm, gbc);

        JPanel panelTablaMec = new JPanel(new BorderLayout(0, 8));
        panelTablaMec.setBackground(Color.WHITE);
        panelTablaMec.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblLista = new JLabel("Lista de Personal Técnico");
        lblLista.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblLista.setForeground(new Color(45, 55, 72));
        panelTablaMec.add(lblLista, BorderLayout.NORTH);

        String[] colsMec = mecanicoDAO.obtenerColumnasTabla();
        modeloMecanicos = new DefaultTableModel(colsMec, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaMecanicos = new JTable(modeloMecanicos);
        tablaMecanicos.setRowHeight(28);
        tablaMecanicos.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaMecanicos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaMecanicos.getTableHeader().setBackground(new Color(241, 245, 249));

        tablaMecanicos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaMecanicos.getSelectedRow() != -1) {
                int fila = tablaMecanicos.getSelectedRow();
                idMecanicoSeleccionado = Integer.parseInt(modeloMecanicos.getValueAt(fila, 0).toString());
                String nomMec = modeloMecanicos.getValueAt(fila, 1).toString();
                lblServiciosTitulo.setText("Servicios asignados a: " + nomMec);
                cargarServiciosDeMecanico(idMecanicoSeleccionado);
            }
        });

        JScrollPane scrollMec = new JScrollPane(tablaMecanicos);
        scrollMec.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        panelTablaMec.add(scrollMec, BorderLayout.CENTER);

        JPanel panelAccionesMec = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        panelAccionesMec.setOpaque(false);

        btnModificar = new JButton("Modificar Seleccionado");
        btnModificar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnModificar.setBackground(new Color(49, 130, 206));
        btnModificar.setForeground(Color.WHITE);
        btnModificar.setFocusPainted(false);
        btnModificar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnModificar.putClientProperty("JButton.buttonType", "roundRect");
        btnModificar.addActionListener(e -> abrirModalModificarMecanico());

        panelAccionesMec.add(btnModificar);
        panelTablaMec.add(panelAccionesMec, BorderLayout.SOUTH);

        panel.add(panelForm, BorderLayout.NORTH);
        panel.add(panelTablaMec, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelDerechoProcesos() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(18, 20, 18, 20)
        ));

        lblServiciosTitulo = new JLabel("Seleccione un mecánico para gestionar sus procesos");
        lblServiciosTitulo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblServiciosTitulo.setForeground(new Color(45, 55, 72));
        panel.add(lblServiciosTitulo, BorderLayout.NORTH);

        String[] colsServicios = {"N°", "Cliente", "Vehículo", "Placa", "Falla / Servicio", "Estado"};
        modeloServicios = new DefaultTableModel(colsServicios, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaServicios = new JTable(modeloServicios);
        tablaServicios.setRowHeight(32);
        tablaServicios.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaServicios.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaServicios.getTableHeader().setBackground(new Color(241, 245, 249));

        tablaServicios.getColumnModel().getColumn(0).setPreferredWidth(35);
        tablaServicios.getColumnModel().getColumn(1).setPreferredWidth(100);
        tablaServicios.getColumnModel().getColumn(2).setPreferredWidth(95);
        tablaServicios.getColumnModel().getColumn(3).setPreferredWidth(75);
        tablaServicios.getColumnModel().getColumn(4).setPreferredWidth(130);
        tablaServicios.getColumnModel().getColumn(5).setPreferredWidth(105);

        tablaServicios.getColumnModel().getColumn(5).setCellRenderer(new EstadoBadgeRenderer());

        tablaServicios.getSelectionModel().addListSelectionListener(e -> actualizarBotonSegunSeleccion());
        tablaServicios.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                actualizarBotonSegunSeleccion();
            }
        });

        JScrollPane scrollServicios = new JScrollPane(tablaServicios);
        scrollServicios.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        panel.add(scrollServicios, BorderLayout.CENTER);

        JPanel panelAccionesEstado = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelAccionesEstado.setOpaque(false);

        btnAvanzarEtapa = new JButton("Avanzar a Siguiente Etapa");
        btnAvanzarEtapa.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAvanzarEtapa.setFocusPainted(false);
        btnAvanzarEtapa.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAvanzarEtapa.putClientProperty("JButton.buttonType", "roundRect");
        btnAvanzarEtapa.addActionListener(e -> avanzarSiguienteEtapa());

        panelAccionesEstado.add(btnAvanzarEtapa);
        panel.add(panelAccionesEstado, BorderLayout.SOUTH);

        actualizarBotonSegunSeleccion();

        return panel;
    }

    private void actualizarBotonSegunSeleccion() {
        int fila = tablaServicios.getSelectedRow();
        if (fila == -1) {
            btnAvanzarEtapa.setText("Seleccione un servicio");
            btnAvanzarEtapa.setEnabled(false);
            btnAvanzarEtapa.setBackground(new Color(241, 245, 249));
            btnAvanzarEtapa.setForeground(new Color(148, 163, 184));
            return;
        }

        btnAvanzarEtapa.setEnabled(true);
        String estado = modeloServicios.getValueAt(fila, 5).toString();

        if (estado.equalsIgnoreCase("Recepcionado")) {
            btnAvanzarEtapa.setText("▶ Iniciar Reparación (A 'En Proceso')");
            btnAvanzarEtapa.setBackground(new Color(219, 234, 254));
            btnAvanzarEtapa.setForeground(new Color(29, 78, 216));
        } else if (estado.equalsIgnoreCase("En Proceso")) {
            btnAvanzarEtapa.setText("✔ Finalizar Servicio (Terminar)");
            btnAvanzarEtapa.setBackground(new Color(209, 250, 229));
            btnAvanzarEtapa.setForeground(new Color(4, 120, 87));
        } else {
            btnAvanzarEtapa.setText("Servicio Concluido");
            btnAvanzarEtapa.setEnabled(false);
            btnAvanzarEtapa.setBackground(new Color(241, 245, 249));
            btnAvanzarEtapa.setForeground(new Color(100, 116, 139));
        }
    }

    private void avanzarSiguienteEtapa() {
        int fila = tablaServicios.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un servicio de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idOrden = Integer.parseInt(modeloServicios.getValueAt(fila, 0).toString());
        String estadoActual = modeloServicios.getValueAt(fila, 5).toString();

        if (estadoActual.equalsIgnoreCase("Recepcionado")) {
            if (ordenTallerDAO.actualizarEstado(idOrden, "En Proceso")) {
                cargarServiciosDeMecanico(idMecanicoSeleccionado);
            }
        } else if (estadoActual.equalsIgnoreCase("En Proceso")) {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "¿Desea marcar como FINALIZADO este servicio?\nSe retirará de la cola activa y quedará registrado en el historial.",
                    "Confirmar Finalización",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );
            if (confirm == JOptionPane.YES_OPTION) {
                if (ordenTallerDAO.actualizarEstado(idOrden, "Finalizado")) {
                    JOptionPane.showMessageDialog(this, "Servicio finalizado exitosamente.");
                    cargarServiciosDeMecanico(idMecanicoSeleccionado);
                }
            }
        }
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

    private JTextField crearTextFieldNumerico(String placeholder, int maxDigitos) {
        JTextField txt = crearTextField(placeholder);
        ((AbstractDocument) txt.getDocument()).setDocumentFilter(new LimiteNumericoFilter(maxDigitos));
        return txt;
    }

    private void cargarMecanicosBD() {
        modeloMecanicos.setRowCount(0);
        List<Object[]> lista = mecanicoDAO.listar();
        for (Object[] fila : lista) {
            modeloMecanicos.addRow(fila);
        }
    }

    private void cargarServiciosDeMecanico(int idMec) {
        modeloServicios.setRowCount(0);
        List<Object[]> servicios = mecanicoDAO.listarServiciosPorMecanico(idMec);
        for (Object[] fila : servicios) {
            modeloServicios.addRow(fila);
        }
        actualizarBotonSegunSeleccion();
    }

    private void registrarMecanico() {
        String nombre = txtNombre.getText().trim();
        String especialidad = txtEspecialidad.getText().trim();
        String telefono = txtTelefono.getText().trim();

        if (nombre.isEmpty() || especialidad.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar el nombre y la especialidad.", "Campos Requeridos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!telefono.isEmpty() && telefono.length() != 9) {
            JOptionPane.showMessageDialog(this, "El teléfono debe tener exactamente 9 dígitos.", "Teléfono Inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (mecanicoDAO.registrar(nombre, especialidad, telefono)) {
            JOptionPane.showMessageDialog(this, "Mecánico registrado exitosamente.");
            limpiarFormulario();
            cargarMecanicosBD();
        } else {
            JOptionPane.showMessageDialog(this, "Error al registrar el mecánico en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirModalModificarMecanico() {
        int fila = tablaMecanicos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un mecánico de la tabla para modificar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(modeloMecanicos.getValueAt(fila, 0).toString());
        String nombreActual = modeloMecanicos.getValueAt(fila, 1) != null ? modeloMecanicos.getValueAt(fila, 1).toString() : "";
        String especialidadActual = modeloMecanicos.getValueAt(fila, 2) != null ? modeloMecanicos.getValueAt(fila, 2).toString() : "";
        String telefonoActual = modeloMecanicos.getColumnCount() > 3 && modeloMecanicos.getValueAt(fila, 3) != null ? modeloMecanicos.getValueAt(fila, 3).toString() : "";

        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        JDialog modal = new JDialog(parentFrame, "Modificar Mecánico - ID #" + id, true);
        modal.setSize(400, 380);
        modal.setLocationRelativeTo(this);
        modal.setResizable(false);

        JPanel panelModal = new JPanel(new GridBagLayout());
        panelModal.setBackground(Color.WHITE);
        panelModal.setBorder(new EmptyBorder(20, 25, 20, 25));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        JLabel lblModal = new JLabel("Editar Ficha del Especialista");
        lblModal.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblModal.setForeground(new Color(45, 55, 72));
        gbc.gridy = 0;
        panelModal.add(lblModal, gbc);

        gbc.gridy = 1;
        panelModal.add(crearLabel("Nombre Completo:"), gbc);
        JTextField txtEditNom = crearTextField("Nombre");
        txtEditNom.setText(nombreActual);
        gbc.gridy = 2;
        panelModal.add(txtEditNom, gbc);

        gbc.gridy = 3;
        panelModal.add(crearLabel("Especialidad:"), gbc);
        JTextField txtEditEsp = crearTextField("Especialidad");
        txtEditEsp.setText(especialidadActual);
        gbc.gridy = 4;
        panelModal.add(txtEditEsp, gbc);

        gbc.gridy = 5;
        panelModal.add(crearLabel("Teléfono (9 dígitos):"), gbc);
        JTextField txtEditTel = crearTextFieldNumerico("Teléfono", 9);
        txtEditTel.setText(telefonoActual);
        gbc.gridy = 6;
        panelModal.add(txtEditTel, gbc);

        JPanel panelBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBtns.setOpaque(false);

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnCancelar.setFocusPainted(false);
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancelar.putClientProperty("JButton.buttonType", "roundRect");
        btnCancelar.addActionListener(e -> modal.dispose());

        JButton btnGuardar = new JButton("Guardar Cambios");
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnGuardar.setBackground(new Color(49, 130, 206));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGuardar.putClientProperty("JButton.buttonType", "roundRect");
        btnGuardar.addActionListener(e -> {
            String nuevoNom = txtEditNom.getText().trim();
            String nuevaEsp = txtEditEsp.getText().trim();
            String nuevoTel = txtEditTel.getText().trim();

            if (nuevoNom.isEmpty() || nuevaEsp.isEmpty()) {
                JOptionPane.showMessageDialog(modal, "Nombre y especialidad son obligatorios.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!nuevoTel.isEmpty() && nuevoTel.length() != 9) {
                JOptionPane.showMessageDialog(modal, "El teléfono debe tener 9 dígitos.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (mecanicoDAO.modificar(id, nuevoNom, nuevaEsp, nuevoTel)) {
                JOptionPane.showMessageDialog(modal, "Mecánico actualizado correctamente.");
                modal.dispose();
                cargarMecanicosBD();
            } else {
                JOptionPane.showMessageDialog(modal, "Error al actualizar en base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panelBtns.add(btnCancelar);
        panelBtns.add(btnGuardar);

        gbc.gridy = 7;
        gbc.insets = new Insets(16, 0, 0, 0);
        panelModal.add(panelBtns, gbc);

        modal.setContentPane(panelModal);
        modal.setVisible(true);
    }

    private void limpiarFormulario() {
        txtNombre.setText("");
        txtEspecialidad.setText("");
        txtTelefono.setText("");
        tablaMecanicos.clearSelection();
    }

    private static class LimiteNumericoFilter extends DocumentFilter {
        private final int limite;

        public LimiteNumericoFilter(int limite) {
            this.limite = limite;
        }

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (string == null) return;
            if (string.matches("^[0-9]*$") && (fb.getDocument().getLength() + string.length()) <= limite) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (text == null) return;
            if (text.matches("^[0-9]*$") && (fb.getDocument().getLength() - length + text.length()) <= limite) {
                super.replace(fb, offset, length, text, attrs);
            }
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