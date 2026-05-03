package vista;

import dao.UsuarioLoginDAO;
import modelo.UsuarioLogin;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PanelGestionUsuarios extends JPanel {

    private JTextField txtNombre, txtApellido, txtDocumento, txtCorreo, txtContrasena;
    private JComboBox<String> cmbRol, cmbEstado;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private UsuarioLoginDAO dao;
    private int idSeleccionado = -1;

    public PanelGestionUsuarios() {
        dao = new UsuarioLoginDAO();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblTitulo = new JLabel("Gestión de Usuarios del Sistema");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        add(lblTitulo, BorderLayout.NORTH);

        // Formulario
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Datos del usuario"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 5, 4, 5);
        gbc.anchor = GridBagConstraints.WEST;

        txtNombre     = new JTextField(20);
        txtApellido   = new JTextField(20);
        txtDocumento  = new JTextField(20);
        txtCorreo     = new JTextField(20);
        txtContrasena = new JPasswordField(20);
        cmbRol        = new JComboBox<>(new String[]{"ADMIN", "TUTOR", "ESTUDIANTE", "ASESOR"});
        cmbEstado     = new JComboBox<>(new String[]{"Activo", "Inactivo"});

        Object[][] campos = {
            {"Nombre:", txtNombre},
            {"Apellido:", txtApellido},
            {"Documento:", txtDocumento},
            {"Correo:", txtCorreo},
            {"Contraseña:", txtContrasena},
            {"Rol:", cmbRol},
            {"Estado:", cmbEstado}
        };

        for (int i = 0; i < campos.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
            formPanel.add(new JLabel((String) campos[i][0]), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
            formPanel.add((Component) campos[i][1], gbc);
        }

        // Botones
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnGuardar    = new JButton("Guardar");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnActivar    = new JButton("Activar");
        JButton btnDesactivar = new JButton("Desactivar");
        JButton btnEliminar   = new JButton("Eliminar");
        JButton btnLimpiar    = new JButton("Limpiar");

        btnPanel.add(btnGuardar);
        btnPanel.add(btnActualizar);
        btnPanel.add(btnActivar);
        btnPanel.add(btnDesactivar);
        btnPanel.add(btnEliminar);
        btnPanel.add(btnLimpiar);

        gbc.gridx = 0; gbc.gridy = campos.length; gbc.gridwidth = 2;
        formPanel.add(btnPanel, gbc);

        add(formPanel, BorderLayout.NORTH);

        // Tabla
        modeloTabla = new DefaultTableModel(
            new String[]{"ID", "Nombre", "Apellido", "Documento", "Correo", "Rol", "Estado"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(40);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createTitledBorder("Usuarios registrados"));
        add(scroll, BorderLayout.CENTER);

        // Eventos botones
        btnGuardar.addActionListener(e -> guardar());
        btnActualizar.addActionListener(e -> actualizar());
        btnActivar.addActionListener(e -> cambiarEstado("Activo"));
        btnDesactivar.addActionListener(e -> cambiarEstado("Inactivo"));
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiar());

        // Selección en tabla
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() != -1) {
                int fila = tabla.getSelectedRow();
                idSeleccionado = (int) modeloTabla.getValueAt(fila, 0);
                txtNombre.setText((String) modeloTabla.getValueAt(fila, 1));
                txtApellido.setText((String) modeloTabla.getValueAt(fila, 2));
                txtDocumento.setText((String) modeloTabla.getValueAt(fila, 3));
                txtCorreo.setText((String) modeloTabla.getValueAt(fila, 4));
                cmbRol.setSelectedItem(modeloTabla.getValueAt(fila, 5));
                cmbEstado.setSelectedItem(modeloTabla.getValueAt(fila, 6));
            }
        });

        cargarTabla();
    }

    private void guardar() {
        if (txtNombre.getText().trim().isEmpty() || txtCorreo.getText().trim().isEmpty()
                || txtContrasena.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nombre, correo y contraseña son obligatorios.");
            return;
        }
        UsuarioLogin u = new UsuarioLogin(0,
            txtNombre.getText().trim(), txtApellido.getText().trim(),
            txtDocumento.getText().trim(), txtCorreo.getText().trim(),
            txtContrasena.getText().trim(),
            (String) cmbEstado.getSelectedItem(), (String) cmbRol.getSelectedItem());

        if (dao.insertar(u)) {
            JOptionPane.showMessageDialog(this, "Usuario guardado correctamente.");
            limpiar(); cargarTabla();
        } else {
            JOptionPane.showMessageDialog(this, "Error al guardar.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizar() {
        if (idSeleccionado == -1) { JOptionPane.showMessageDialog(this, "Seleccioná un usuario."); return; }
        UsuarioLogin u = new UsuarioLogin(idSeleccionado,
            txtNombre.getText().trim(), txtApellido.getText().trim(),
            txtDocumento.getText().trim(), txtCorreo.getText().trim(),
            txtContrasena.getText().trim(),
            (String) cmbEstado.getSelectedItem(), (String) cmbRol.getSelectedItem());

        if (dao.actualizar(u)) {
            JOptionPane.showMessageDialog(this, "Usuario actualizado.");
            limpiar(); cargarTabla();
        }
    }

    private void cambiarEstado(String estado) {
        if (idSeleccionado == -1) { JOptionPane.showMessageDialog(this, "Seleccioná un usuario."); return; }
        if (dao.cambiarEstado(idSeleccionado, estado)) {
            JOptionPane.showMessageDialog(this, "Usuario " + estado.toLowerCase() + ".");
            cargarTabla();
        }
    }

    private void eliminar() {
        if (idSeleccionado == -1) { JOptionPane.showMessageDialog(this, "Seleccioná un usuario."); return; }
        int confirm = JOptionPane.showConfirmDialog(this, "¿Eliminar este usuario?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (dao.eliminar(idSeleccionado)) {
                JOptionPane.showMessageDialog(this, "Usuario eliminado.");
                limpiar(); cargarTabla();
            } else {
                JOptionPane.showMessageDialog(this, "No se puede eliminar.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limpiar() {
        txtNombre.setText(""); txtApellido.setText("");
        txtDocumento.setText(""); txtCorreo.setText("");
        txtContrasena.setText("");
        cmbRol.setSelectedIndex(0); cmbEstado.setSelectedIndex(0);
        idSeleccionado = -1;
        tabla.clearSelection();
    }

    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        for (UsuarioLogin u : dao.listar()) {
            modeloTabla.addRow(new Object[]{
                u.getIdUsuario(), u.getNombre(), u.getApellido(),
                u.getDocumento(), u.getCorreo(), u.getRol(), u.getEstado()
            });
        }
    }
}
