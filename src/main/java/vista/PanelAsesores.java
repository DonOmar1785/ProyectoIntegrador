package vista;

import dao.AsesorPedagogicoDAO;
import dao.InstitucionDAO;
import modelo.AsesorPedagogico;
import modelo.Institucion;
import util.Validador;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PanelAsesores extends JPanel {

    private JTextField txtNombre, txtApellido, txtDocumento, txtCorreo;
    private JPasswordField txtContrasena;
    private JComboBox<Institucion> cmbInstitucion;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private AsesorPedagogicoDAO dao;
    private InstitucionDAO institucionDAO;
    private int idSeleccionado = -1;

    public PanelAsesores() {
        dao = new AsesorPedagogicoDAO();
        institucionDAO = new InstitucionDAO();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblTitulo = new JLabel("Gestión de Asesores Pedagógicos");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        add(lblTitulo, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Datos del asesor"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 5, 4, 5);
        gbc.anchor = GridBagConstraints.WEST;

        txtNombre     = new JTextField(20);
        txtApellido   = new JTextField(20);
        txtDocumento  = new JTextField(20);
        txtCorreo     = new JTextField(20);
        txtContrasena = new JPasswordField(20);
        cmbInstitucion = new JComboBox<>();

        Object[][] campos = {
            {"Nombre:",     txtNombre},
            {"Apellido:",   txtApellido},
            {"Documento:",  txtDocumento},
            {"Correo:",     txtCorreo},
            {"Contraseña:", txtContrasena}
        };

        for (int i = 0; i < campos.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
            formPanel.add(new JLabel((String) campos[i][0]), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
            formPanel.add((Component) campos[i][1], gbc);
        }

        gbc.gridx = 0; gbc.gridy = campos.length; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Institución:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        cargarInstituciones();
        formPanel.add(cmbInstitucion, gbc);

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

        gbc.gridx = 0; gbc.gridy = campos.length + 1; gbc.gridwidth = 2;
        formPanel.add(btnPanel, gbc);
        add(formPanel, BorderLayout.NORTH);

        modeloTabla = new DefaultTableModel(
            new String[]{"ID", "Nombre", "Apellido", "Documento", "Correo", "Institución"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createTitledBorder("Asesores registrados"));
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
                txtApellido.setText((String) modeloTabla.getValueAt(fila, 2));
                txtDocumento.setText((String) modeloTabla.getValueAt(fila, 3));
                txtCorreo.setText((String) modeloTabla.getValueAt(fila, 4));
                txtContrasena.setText("");
            }
        });

        cargarTabla();
    }

    private String validarGuardar() {
        String e;
        e = Validador.validarNombre(txtNombre.getText(), "Nombre");     if (e != null) return e;
        e = Validador.validarNombre(txtApellido.getText(), "Apellido"); if (e != null) return e;
        e = Validador.validarDocumento(txtDocumento.getText());          if (e != null) return e;
        e = Validador.validarCorreo(txtCorreo.getText());               if (e != null) return e;
        e = Validador.validarContrasena(new String(txtContrasena.getPassword())); if (e != null) return e;
        if (cmbInstitucion.getSelectedItem() == null) return "Debe existir al menos una institución registrada.";
        return null;
    }

    private String validarActualizar() {
        String e;
        e = Validador.validarNombre(txtNombre.getText(), "Nombre");     if (e != null) return e;
        e = Validador.validarNombre(txtApellido.getText(), "Apellido"); if (e != null) return e;
        e = Validador.validarDocumento(txtDocumento.getText());          if (e != null) return e;
        e = Validador.validarCorreo(txtCorreo.getText());               if (e != null) return e;
        if (cmbInstitucion.getSelectedItem() == null) return "Seleccioná una institución.";
        return null;
    }

    private void cargarInstituciones() {
        cmbInstitucion.removeAllItems();
        for (Institucion i : institucionDAO.listar()) cmbInstitucion.addItem(i);
    }

    private void guardar() {
        String err = validarGuardar();
        if (err != null) { JOptionPane.showMessageDialog(this, err, "Validación", JOptionPane.WARNING_MESSAGE); return; }

        AsesorPedagogico a = new AsesorPedagogico(0, txtNombre.getText().trim(), txtApellido.getText().trim(),
            txtDocumento.getText().trim(), txtCorreo.getText().trim(),
            new String(txtContrasena.getPassword()).trim(),
            ((Institucion) cmbInstitucion.getSelectedItem()).getIdInstitucion());

        String resI = dao.insertarConMensaje(a);
        if (resI == null) {
            JOptionPane.showMessageDialog(this, "Asesor guardado correctamente.");
            limpiar(); cargarTabla();
        } else {
            JOptionPane.showMessageDialog(this, util.Validador.interpretarErrorOracle(resI), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizar() {
        if (idSeleccionado == -1) { JOptionPane.showMessageDialog(this, "Seleccioná un asesor de la tabla."); return; }
        String err = validarActualizar();
        if (err != null) { JOptionPane.showMessageDialog(this, err, "Validación", JOptionPane.WARNING_MESSAGE); return; }

        AsesorPedagogico a = new AsesorPedagogico(idSeleccionado, txtNombre.getText().trim(),
            txtApellido.getText().trim(), txtDocumento.getText().trim(), txtCorreo.getText().trim(), "",
            ((Institucion) cmbInstitucion.getSelectedItem()).getIdInstitucion());

        String resA = dao.actualizarConMensaje(a);
        if (resA == null) {
            JOptionPane.showMessageDialog(this, "Asesor actualizado correctamente.");
            limpiar(); cargarTabla();
        } else {
            JOptionPane.showMessageDialog(this, util.Validador.interpretarErrorOracle(resA), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminar() {
        if (idSeleccionado == -1) { JOptionPane.showMessageDialog(this, "Seleccioná un asesor."); return; }
        int c = JOptionPane.showConfirmDialog(this, "¿Eliminar este asesor?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (c == JOptionPane.YES_OPTION) {
            if (dao.eliminar(idSeleccionado)) {
                JOptionPane.showMessageDialog(this, "Asesor eliminado.");
                limpiar(); cargarTabla();
            } else {
                JOptionPane.showMessageDialog(this, "No se puede eliminar: tiene asignaciones activas.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limpiar() {
        txtNombre.setText(""); txtApellido.setText("");
        txtDocumento.setText(""); txtCorreo.setText("");
        txtContrasena.setText("");
        idSeleccionado = -1;
        tabla.clearSelection();
        cargarInstituciones();
    }

    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        String sql = "SELECT a.IdAsesor, a.Nombre, a.Apellido, a.Documento, a.Correo, i.Nombre AS Institucion " +
                     "FROM AsesorPedagogico a JOIN Institucion i ON a.IdInstitucion = i.IdInstitucion ORDER BY a.IdAsesor";
        try (java.sql.Connection con = conexion.Conexion.conectar();
             java.sql.Statement st = con.createStatement();
             java.sql.ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                modeloTabla.addRow(new Object[]{
                    rs.getInt("IdAsesor"), rs.getString("Nombre"), rs.getString("Apellido"),
                    rs.getString("Documento"), rs.getString("Correo"), rs.getString("Institucion")
                });
            }
        } catch (java.sql.SQLException e) {
            System.out.println("Error cargar asesores: " + e.getMessage());
        }
    }
}