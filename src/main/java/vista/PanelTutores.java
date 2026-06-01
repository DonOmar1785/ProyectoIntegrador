package vista;

import dao.ProgramaDAO;
import dao.TutorDAO;
import modelo.Programa;
import modelo.Tutor;
import util.Validador;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PanelTutores extends JPanel {

    private JTextField txtNombre, txtApellido, txtDocumento, txtCorreo;
    private JPasswordField txtContrasena;
    private JComboBox<Programa> cmbPrograma;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private TutorDAO dao;
    private ProgramaDAO programaDAO;
    private int idSeleccionado = -1;

    public PanelTutores() {
        dao = new TutorDAO();
        programaDAO = new ProgramaDAO();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblTitulo = new JLabel("Gestión de Tutores Académicos");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        add(lblTitulo, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Datos del tutor"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 5, 4, 5);
        gbc.anchor = GridBagConstraints.WEST;

        txtNombre     = new JTextField(20);
        txtApellido   = new JTextField(20);
        txtDocumento  = new JTextField(20);
        txtCorreo     = new JTextField(20);
        txtContrasena = new JPasswordField(20);
        cmbPrograma   = new JComboBox<>();

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
        formPanel.add(new JLabel("Programa:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        cargarProgramas();
        formPanel.add(cmbPrograma, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnGuardar    = new JButton("Guardar");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar   = new JButton("Eliminar");
        JButton btnLimpiar    = new JButton("Limpiar");
        JButton btnRefrescar  = new JButton("⟳ Refrescar");
        btnRefrescar.setBackground(new Color(80, 80, 80)); btnRefrescar.setForeground(Color.BLACK);

        btnGuardar.setBackground(new Color(33, 130, 70));    btnGuardar.setForeground(Color.BLACK);
        btnActualizar.setBackground(new Color(33, 80, 160)); btnActualizar.setForeground(Color.BLACK);
        btnEliminar.setBackground(new Color(180, 40, 40));   btnEliminar.setForeground(Color.BLACK);

        btnPanel.add(btnGuardar); btnPanel.add(btnActualizar);
        btnPanel.add(btnEliminar); btnPanel.add(btnLimpiar); btnPanel.add(btnRefrescar);

        gbc.gridx = 0; gbc.gridy = campos.length + 1; gbc.gridwidth = 2;
        formPanel.add(btnPanel, gbc);
        add(formPanel, BorderLayout.NORTH);

        modeloTabla = new DefaultTableModel(
            new String[]{"ID", "Nombre", "Apellido", "Documento", "Correo", "Programa"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createTitledBorder("Tutores registrados"));
        add(scroll, BorderLayout.CENTER);

        btnGuardar.addActionListener(e -> guardar());
        btnActualizar.addActionListener(e -> actualizar());
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
        if (cmbPrograma.getSelectedItem() == null) return "Debe existir al menos un programa registrado.";
        return null;
    }

    private String validarActualizar() {
        String e;
        e = Validador.validarNombre(txtNombre.getText(), "Nombre");     if (e != null) return e;
        e = Validador.validarNombre(txtApellido.getText(), "Apellido"); if (e != null) return e;
        e = Validador.validarDocumento(txtDocumento.getText());          if (e != null) return e;
        e = Validador.validarCorreo(txtCorreo.getText());               if (e != null) return e;
        if (cmbPrograma.getSelectedItem() == null) return "Seleccioná un programa.";
        return null;
    }

    private void cargarProgramas() {
        cmbPrograma.removeAllItems();
        for (Programa p : programaDAO.listar()) cmbPrograma.addItem(p);
    }

    private void guardar() {
        String err = validarGuardar();
        if (err != null) { JOptionPane.showMessageDialog(this, err, "Validación", JOptionPane.WARNING_MESSAGE); return; }

        Tutor t = new Tutor(0, txtNombre.getText().trim(), txtApellido.getText().trim(),
            txtDocumento.getText().trim(), txtCorreo.getText().trim(),
            new String(txtContrasena.getPassword()).trim(),
            ((Programa) cmbPrograma.getSelectedItem()).getIdPrograma());

        String resI = dao.insertarConMensaje(t);
        if (resI == null) {
            JOptionPane.showMessageDialog(this, "Tutor guardado correctamente.");
            limpiar(); cargarTabla();
        } else {
            JOptionPane.showMessageDialog(this, util.Validador.interpretarErrorOracle(resI), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizar() {
        if (idSeleccionado == -1) { JOptionPane.showMessageDialog(this, "Seleccioná un tutor de la tabla."); return; }
        String err = validarActualizar();
        if (err != null) { JOptionPane.showMessageDialog(this, err, "Validación", JOptionPane.WARNING_MESSAGE); return; }

        Tutor t = new Tutor(idSeleccionado, txtNombre.getText().trim(), txtApellido.getText().trim(),
            txtDocumento.getText().trim(), txtCorreo.getText().trim(), "",
            ((Programa) cmbPrograma.getSelectedItem()).getIdPrograma());

        String resA = dao.actualizarConMensaje(t);
        if (resA == null) {
            JOptionPane.showMessageDialog(this, "Tutor actualizado correctamente.");
            limpiar(); cargarTabla();
        } else {
            JOptionPane.showMessageDialog(this, util.Validador.interpretarErrorOracle(resA), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminar() {
        if (idSeleccionado == -1) { JOptionPane.showMessageDialog(this, "Seleccioná un tutor."); return; }
        int c = JOptionPane.showConfirmDialog(this, "¿Eliminar este tutor?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (c == JOptionPane.YES_OPTION) {
            if (dao.eliminar(idSeleccionado)) {
                JOptionPane.showMessageDialog(this, "Tutor eliminado.");
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
        cargarProgramas();
    }

    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        String sql = "SELECT t.IdTutor, t.Nombre, t.Apellido, t.Documento, t.Correo, pr.Nombre AS Programa " +
                     "FROM Tutor t JOIN Programa pr ON t.IdPrograma = pr.IdPrograma " +
                     "ORDER BY t.IdTutor";
        try (java.sql.Connection con = conexion.Conexion.conectar();
             java.sql.Statement st = con.createStatement();
             java.sql.ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                modeloTabla.addRow(new Object[]{
                    rs.getInt("IdTutor"), rs.getString("Nombre"), rs.getString("Apellido"),
                    rs.getString("Documento"), rs.getString("Correo"), rs.getString("Programa")
                });
            }
        } catch (java.sql.SQLException e) {
            System.out.println("Error cargar tutores: " + e.getMessage());
        }
    }
}