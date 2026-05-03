package vista;

import dao.ProgramaDAO;
import dao.UsuarioDAO;
import modelo.Programa;
import modelo.Usuario;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PanelUsuarios extends JPanel {

    private JTextField txtNombre, txtApellido, txtDocumento, txtCorreo, txtContrasena;
    private JComboBox<Programa> cmbPrograma;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private UsuarioDAO dao;
    private ProgramaDAO programaDAO;
    private int idSeleccionado = -1;

    public PanelUsuarios() {
        dao = new UsuarioDAO();
        programaDAO = new ProgramaDAO();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblTitulo = new JLabel("Gestión de Usuarios (Estudiantes)");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        add(lblTitulo, BorderLayout.NORTH);

        // Formulario
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Datos del estudiante"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 5, 4, 5);
        gbc.anchor = GridBagConstraints.WEST;

        String[] labels = {"Nombre:", "Apellido:", "Documento:", "Correo:", "Contraseña:"};
        txtNombre     = new JTextField(20);
        txtApellido   = new JTextField(20);
        txtDocumento  = new JTextField(20);
        txtCorreo     = new JTextField(20);
        txtContrasena = new JPasswordField(20);
        JTextField[] fields = {txtNombre, txtApellido, txtDocumento, txtCorreo, txtContrasena};

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
            formPanel.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
            formPanel.add(fields[i], gbc);
        }

        gbc.gridx = 0; gbc.gridy = 5; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Programa:"), gbc);
        gbc.gridx = 1;
        cmbPrograma = new JComboBox<>();
        cargarProgramas();
        formPanel.add(cmbPrograma, gbc);

        // Botones
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

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        formPanel.add(btnPanel, gbc);

        add(formPanel, BorderLayout.NORTH);

        // Tabla
        modeloTabla = new DefaultTableModel(new String[]{"ID", "Nombre", "Apellido", "Documento", "Correo", "Programa"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createTitledBorder("Estudiantes registrados"));
        add(scroll, BorderLayout.CENTER);

        // Eventos
        btnGuardar.addActionListener(e -> guardar());
        btnActualizar.addActionListener(e -> actualizar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiar());

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() != -1) {
                int fila = tabla.getSelectedRow();
                idSeleccionado = (int) modeloTabla.getValueAt(fila, 0);
                txtNombre.setText((String) modeloTabla.getValueAt(fila, 1));
                txtApellido.setText((String) modeloTabla.getValueAt(fila, 2));
                txtDocumento.setText((String) modeloTabla.getValueAt(fila, 3));
                txtCorreo.setText((String) modeloTabla.getValueAt(fila, 4));
            }
        });

        cargarTabla();
    }

    private void cargarProgramas() {
        cmbPrograma.removeAllItems();
        for (Programa p : programaDAO.listar()) {
            cmbPrograma.addItem(p);
        }
    }

    private void guardar() {
        if (txtNombre.getText().trim().isEmpty() || txtDocumento.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nombre y documento son obligatorios.");
            return;
        }
        if (cmbPrograma.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Debe existir al menos un programa registrado.");
            return;
        }
        Usuario u = new Usuario();
        u.setNombre(txtNombre.getText().trim());
        u.setApellido(txtApellido.getText().trim());
        u.setDocumento(txtDocumento.getText().trim());
        u.setCorreo(txtCorreo.getText().trim());
        u.setContrasena(txtContrasena.getText().trim());
        int idProg = ((Programa) cmbPrograma.getSelectedItem()).getIdPrograma();

        if (dao.insertarEstudiante(u, idProg)) {
            JOptionPane.showMessageDialog(this, "Estudiante guardado correctamente.");
            limpiar();
            cargarTabla();
        } else {
            JOptionPane.showMessageDialog(this, "Error al guardar.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizar() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Seleccioná un estudiante de la tabla.");
            return;
        }
        Usuario u = new Usuario();
        u.setIdUsuario(idSeleccionado);
        u.setNombre(txtNombre.getText().trim());
        u.setApellido(txtApellido.getText().trim());
        u.setDocumento(txtDocumento.getText().trim());
        u.setCorreo(txtCorreo.getText().trim());
        int idProg = ((Programa) cmbPrograma.getSelectedItem()).getIdPrograma();

        if (dao.actualizarEstudiante(u, idProg)) {
            JOptionPane.showMessageDialog(this, "Estudiante actualizado.");
            limpiar();
            cargarTabla();
        }
    }

    private void eliminar() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Seleccioná un estudiante.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "¿Eliminar este estudiante?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (dao.eliminarEstudiante(idSeleccionado)) {
                JOptionPane.showMessageDialog(this, "Estudiante eliminado.");
                limpiar();
                cargarTabla();
            } else {
                JOptionPane.showMessageDialog(this, "No se puede eliminar.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limpiar() {
        txtNombre.setText(""); txtApellido.setText("");
        txtDocumento.setText(""); txtCorreo.setText("");
        txtContrasena.setText("");
        idSeleccionado = -1;
        tabla.clearSelection();
        cargarProgramas();
    }

    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        for (Usuario u : dao.listarEstudiantes()) {
            modeloTabla.addRow(new Object[]{u.getIdUsuario(), u.getNombre(), u.getApellido(),
                    u.getDocumento(), u.getCorreo(), u.getRol()});
        }
    }
}
