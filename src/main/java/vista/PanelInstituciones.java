package vista;

import dao.InstitucionDAO;
import modelo.Institucion;
import javax.swing.*;
import util.Validador;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PanelInstituciones extends JPanel {

    private JTextField txtNombre, txtTipo, txtDireccion, txtContacto;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private InstitucionDAO dao;
    private int idSeleccionado = -1;

    public PanelInstituciones() {
        dao = new InstitucionDAO();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblTitulo = new JLabel("Gestión de Instituciones");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        add(lblTitulo, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Datos de la institución"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 5, 4, 5);
        gbc.anchor = GridBagConstraints.WEST;

        txtNombre    = new JTextField(25);
        txtTipo      = new JTextField(25);
        txtDireccion = new JTextField(25);
        txtContacto  = new JTextField(25);

        Object[][] campos = {
            {"Nombre:",    txtNombre},
            {"Tipo:",      txtTipo},
            {"Dirección:", txtDireccion},
            {"Contacto:",  txtContacto}
        };

        for (int i = 0; i < campos.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
            formPanel.add(new JLabel((String) campos[i][0]), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
            formPanel.add((Component) campos[i][1], gbc);
        }

        // Nota de tipos válidos
        gbc.gridx = 0; gbc.gridy = campos.length; gbc.gridwidth = 2;
        JLabel lblNota = new JLabel("  Tipos: pública, CDI/ICBF, jardín privado, espacio no convencional");
        lblNota.setFont(new Font("SansSerif", Font.ITALIC, 11));
        lblNota.setForeground(Color.GRAY);
        formPanel.add(lblNota, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnGuardar    = new JButton("Guardar");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar   = new JButton("Eliminar");
        JButton btnLimpiar    = new JButton("Limpiar");

        btnGuardar.setBackground(new Color(33, 130, 70));    btnGuardar.setForeground(Color.BLACK);
        btnActualizar.setBackground(new Color(33, 80, 160)); btnActualizar.setForeground(Color.BLACK);
        btnEliminar.setBackground(new Color(180, 40, 40));   btnEliminar.setForeground(Color.BLACK);

        btnPanel.add(btnGuardar); btnPanel.add(btnActualizar);
        btnPanel.add(btnEliminar); btnPanel.add(btnLimpiar);

        gbc.gridy = campos.length + 1; gbc.gridwidth = 2;
        formPanel.add(btnPanel, gbc);
        add(formPanel, BorderLayout.NORTH);

        modeloTabla = new DefaultTableModel(
            new String[]{"ID", "Nombre", "Tipo", "Dirección", "Contacto"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createTitledBorder("Instituciones registradas"));
        add(scroll, BorderLayout.CENTER);

        btnGuardar.addActionListener(e -> guardar());
        btnActualizar.addActionListener(e -> actualizar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiar());

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() != -1) {
                int fila = tabla.getSelectedRow();
                idSeleccionado = (int) modeloTabla.getValueAt(fila, 0);
                txtNombre.setText((String) modeloTabla.getValueAt(fila, 1));
                txtTipo.setText((String) modeloTabla.getValueAt(fila, 2));
                txtDireccion.setText((String) modeloTabla.getValueAt(fila, 3));
                txtContacto.setText((String) modeloTabla.getValueAt(fila, 4));
            }
        });

        cargarTabla();
    }

    private void guardar() {
        String errI = Validador.validarNombre(txtNombre.getText(), "Nombre");
        if (errI == null) errI = Validador.validarTexto(txtTipo.getText(), "Tipo");
        if (errI == null) errI = Validador.validarTexto(txtDireccion.getText(), "Direccion");
        if (errI != null) { JOptionPane.showMessageDialog(this, errI, "Validacion", JOptionPane.WARNING_MESSAGE); return; }
        Institucion i = new Institucion(0, txtNombre.getText().trim(), txtTipo.getText().trim(),
            txtDireccion.getText().trim(), txtContacto.getText().trim());
        if (dao.insertar(i)) {
            JOptionPane.showMessageDialog(this, "Institución guardada correctamente.");
            limpiar(); cargarTabla();
        } else {
            JOptionPane.showMessageDialog(this, "Error al guardar.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizar() {
        if (idSeleccionado == -1) { JOptionPane.showMessageDialog(this, "Seleccioná una institución."); return; }
        Institucion i = new Institucion(idSeleccionado, txtNombre.getText().trim(), txtTipo.getText().trim(),
            txtDireccion.getText().trim(), txtContacto.getText().trim());
        if (dao.actualizar(i)) {
            JOptionPane.showMessageDialog(this, "Institución actualizada.");
            limpiar(); cargarTabla();
        }
    }

    private void eliminar() {
        if (idSeleccionado == -1) { JOptionPane.showMessageDialog(this, "Seleccioná una institución."); return; }
        int c = JOptionPane.showConfirmDialog(this, "¿Eliminar esta institución?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (c == JOptionPane.YES_OPTION) {
            if (dao.eliminar(idSeleccionado)) {
                JOptionPane.showMessageDialog(this, "Institución eliminada.");
                limpiar(); cargarTabla();
            } else {
                JOptionPane.showMessageDialog(this, "No se puede eliminar, tiene registros asociados.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limpiar() {
        txtNombre.setText(""); txtTipo.setText("");
        txtDireccion.setText(""); txtContacto.setText("");
        idSeleccionado = -1;
        tabla.clearSelection();
    }

    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        for (Institucion i : dao.listar()) {
            modeloTabla.addRow(new Object[]{
                i.getIdInstitucion(), i.getNombre(), i.getTipo(),
                i.getDireccion(), i.getContacto()
            });
        }
    }
}