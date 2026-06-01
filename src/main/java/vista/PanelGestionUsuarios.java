package vista;

import dao.InstitucionDAO;
import dao.ProgramaDAO;
import dao.UsuarioLoginDAO;
import modelo.Institucion;
import modelo.Programa;
import modelo.UsuarioLogin;
import util.Validador;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PanelGestionUsuarios extends JPanel {

    private JTextField txtNombre, txtApellido, txtDocumento, txtCorreo, txtContrasena;
    private JComboBox<String> cmbRol, cmbEstado;
    private JComboBox<Object> cmbExtra;
    private JLabel lblExtra;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private UsuarioLoginDAO dao;
    private ProgramaDAO programaDAO;
    private InstitucionDAO institucionDAO;
    private int idSeleccionado = -1;

    public PanelGestionUsuarios() {
        dao = new UsuarioLoginDAO();
        programaDAO = new ProgramaDAO();
        institucionDAO = new InstitucionDAO();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblTitulo = new JLabel("Gestión de Usuarios del Sistema");
        JLabel lblAviso = new JLabel("  Nota: Tutores, Estudiantes y Asesores se gestionan desde sus propios paneles.");
        lblAviso.setFont(new Font("SansSerif", Font.ITALIC, 11));
        lblAviso.setForeground(new Color(140, 80, 0));
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        JPanel panelNorte = new JPanel(new BorderLayout());
        panelNorte.add(lblTitulo, BorderLayout.NORTH);
        panelNorte.add(lblAviso, BorderLayout.SOUTH);
        add(panelNorte, BorderLayout.NORTH);

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
        cmbRol.setSelectedItem("ADMIN");
        cmbRol.setEnabled(false);
        cmbEstado     = new JComboBox<>(new String[]{"Activo", "Inactivo"});
        cmbExtra      = new JComboBox<>();
        lblExtra      = new JLabel("Programa:");

        Object[][] campos = {
            {"Nombre:",     txtNombre},
            {"Apellido:",   txtApellido},
            {"Documento:",  txtDocumento},
            {"Correo:",     txtCorreo},
            {"Contraseña:", txtContrasena},
            {"Rol:",        cmbRol},
            {"Estado:",     cmbEstado}
        };

        for (int i = 0; i < campos.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
            formPanel.add(new JLabel((String) campos[i][0]), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
            formPanel.add((Component) campos[i][1], gbc);
        }

        gbc.gridx = 0; gbc.gridy = campos.length; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(lblExtra, gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        formPanel.add(cmbExtra, gbc);

        cmbRol.addActionListener(e -> actualizarCampoExtra());
        actualizarCampoExtra();

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnGuardar    = new JButton("Guardar");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnActivar    = new JButton("Activar");
        JButton btnDesactivar = new JButton("Desactivar");
        JButton btnEliminar   = new JButton("Eliminar");
        JButton btnLimpiar    = new JButton("Limpiar");
        JButton btnRefrescar  = new JButton("⟳ Refrescar");
        btnRefrescar.setBackground(new Color(80, 80, 80)); btnRefrescar.setForeground(Color.BLACK);

        btnPanel.add(btnGuardar); btnPanel.add(btnActualizar); btnPanel.add(btnActivar);
        btnPanel.add(btnDesactivar); btnPanel.add(btnEliminar); btnPanel.add(btnLimpiar); btnPanel.add(btnRefrescar);

        gbc.gridx = 0; gbc.gridy = campos.length + 1; gbc.gridwidth = 2;
        formPanel.add(btnPanel, gbc);
        add(formPanel, BorderLayout.NORTH);

        modeloTabla = new DefaultTableModel(
            new String[]{"ID", "Nombre", "Apellido", "Documento", "Correo", "Rol", "Estado"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createTitledBorder("Usuarios registrados"));
        add(scroll, BorderLayout.CENTER);

        btnGuardar.addActionListener(e -> guardar());
        btnActualizar.addActionListener(e -> actualizar());
        btnActivar.addActionListener(e -> cambiarEstado("Activo"));
        btnDesactivar.addActionListener(e -> cambiarEstado("Inactivo"));
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiar());
        btnRefrescar.addActionListener(e -> cargarTabla());

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() != -1) {
                int fila = tabla.getSelectedRow();
                idSeleccionado = (int) modeloTabla.getValueAt(fila, 0);
                txtNombre.setText((String) modeloTabla.getValueAt(fila, 1));
                txtApellido.setText((String) modeloTabla.getValueAt(fila, 2));
                txtDocumento.setText((String) modeloTabla.getValueAt(fila, 3));
                txtCorreo.setText((String) modeloTabla.getValueAt(fila, 4));
                cmbRol.setEnabled(true);
                cmbRol.setSelectedItem(modeloTabla.getValueAt(fila, 5));
                cmbRol.setEnabled(false);
                cmbEstado.setSelectedItem(modeloTabla.getValueAt(fila, 6));
            }
        });

        cargarTabla();
    }

    private void actualizarCampoExtra() {
        String rol = (String) cmbRol.getSelectedItem();
        cmbExtra.removeAllItems();
        if ("ADMIN".equals(rol)) {
            lblExtra.setText("—");
            cmbExtra.setEnabled(false);
            return;
        }
        cmbExtra.setEnabled(true);
        if ("ESTUDIANTE".equals(rol) || "TUTOR".equals(rol)) {
            lblExtra.setText("Programa:");
            for (Programa p : programaDAO.listar()) cmbExtra.addItem(p);
        } else if ("ASESOR".equals(rol)) {
            lblExtra.setText("Institución:");
            for (Institucion i : institucionDAO.listar()) cmbExtra.addItem(i);
        }
    }

    private String validar() {
        String err;
        err = Validador.validarNombre(txtNombre.getText(), "Nombre");        if (err != null) return err;
        err = Validador.validarNombre(txtApellido.getText(), "Apellido");    if (err != null) return err;
        err = Validador.validarDocumento(txtDocumento.getText());             if (err != null) return err;
        err = Validador.validarCorreo(txtCorreo.getText());                  if (err != null) return err;
        err = Validador.validarContrasena(txtContrasena.getText());          if (err != null) return err;
        String rol = (String) cmbRol.getSelectedItem();
        if (!"ADMIN".equals(rol) && cmbExtra.getSelectedItem() == null)
            return "Debés seleccionar un programa o institución según el rol.";
        return null;
    }

    private int getIdExtra() {
        Object sel = cmbExtra.getSelectedItem();
        if (sel instanceof Programa) return ((Programa) sel).getIdPrograma();
        if (sel instanceof Institucion) return ((Institucion) sel).getIdInstitucion();
        return -1;
    }

    private void guardar() {
        String rolSeleccionado = (String) cmbRol.getSelectedItem();
        if (!"ADMIN".equals(rolSeleccionado)) {
            JOptionPane.showMessageDialog(this,
                "Los roles Tutor, Estudiante y Asesor se crean desde sus paneles específicos.\n" +
                "Usá el panel 'Tutores', 'Estudiantes' o 'Asesores' del menú lateral.",
                "Operación no permitida", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String err = validar();
        if (err != null) { JOptionPane.showMessageDialog(this, err, "Validación", JOptionPane.WARNING_MESSAGE); return; }

        UsuarioLogin u = new UsuarioLogin(0,
            txtNombre.getText().trim(), txtApellido.getText().trim(),
            txtDocumento.getText().trim(), txtCorreo.getText().trim(),
            txtContrasena.getText().trim(),
            (String) cmbEstado.getSelectedItem(), (String) cmbRol.getSelectedItem());

        String resultado = dao.insertarConMensaje(u, getIdExtra());
        if (resultado == null) {
            JOptionPane.showMessageDialog(this, "Usuario guardado correctamente.");
            limpiar(); cargarTabla();
        } else {
            JOptionPane.showMessageDialog(this, Validador.interpretarErrorOracle(resultado), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizar() {
        if (idSeleccionado == -1) { JOptionPane.showMessageDialog(this, "Seleccioná un usuario."); return; }
        String err = Validador.validarNombre(txtNombre.getText(), "Nombre");
        if (err == null) err = Validador.validarNombre(txtApellido.getText(), "Apellido");
        if (err == null) err = Validador.validarDocumento(txtDocumento.getText());
        if (err == null) err = Validador.validarCorreo(txtCorreo.getText());
        if (err != null) { JOptionPane.showMessageDialog(this, err, "Validación", JOptionPane.WARNING_MESSAGE); return; }

        UsuarioLogin u = new UsuarioLogin(idSeleccionado,
            txtNombre.getText().trim(), txtApellido.getText().trim(),
            txtDocumento.getText().trim(), txtCorreo.getText().trim(), "",
            (String) cmbEstado.getSelectedItem(), (String) cmbRol.getSelectedItem());

        if (dao.actualizar(u)) {
            JOptionPane.showMessageDialog(this, "Usuario actualizado correctamente.");
            limpiar(); cargarTabla();
        } else {
            JOptionPane.showMessageDialog(this,
                "Error al actualizar. Verificá que el documento o correo no estén duplicados.",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cambiarEstado(String estado) {
        if (idSeleccionado == -1) { JOptionPane.showMessageDialog(this, "Seleccioná un usuario."); return; }
        if (dao.cambiarEstado(idSeleccionado, estado))
            JOptionPane.showMessageDialog(this, "Usuario " + estado.toLowerCase() + ".");
        cargarTabla();
    }

    private void eliminar() {
        if (idSeleccionado == -1) { JOptionPane.showMessageDialog(this, "Seleccioná un usuario."); return; }
        int c = JOptionPane.showConfirmDialog(this, "¿Eliminar este usuario?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (c == JOptionPane.YES_OPTION) {
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
        txtDocumento.setText(""); txtCorreo.setText(""); txtContrasena.setText("");
        cmbRol.setEnabled(true);
        cmbRol.setSelectedItem("ADMIN");
        cmbRol.setEnabled(false);
        cmbEstado.setSelectedIndex(0);
        idSeleccionado = -1; tabla.clearSelection(); actualizarCampoExtra();
    }

    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        for (UsuarioLogin u : dao.listar())
            modeloTabla.addRow(new Object[]{u.getIdUsuario(), u.getNombre(), u.getApellido(),
                u.getDocumento(), u.getCorreo(), u.getRol(), u.getEstado()});
    }
}