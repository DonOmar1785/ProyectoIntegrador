package vista;

import dao.ProgramaDAO;
import modelo.Programa;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PanelProgramas extends JPanel {

    private JTextField txtNombre, txtFacultad;
    private JComboBox<String> cmbEstado;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private ProgramaDAO dao;
    private int idSeleccionado = -1;

    public PanelProgramas() {
        dao = new ProgramaDAO();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Título
        JLabel lblTitulo = new JLabel("Gestión de Programas Académicos");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        add(lblTitulo, BorderLayout.NORTH);

        // Formulario
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Datos del programa"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        txtNombre = new JTextField(25);
        formPanel.add(txtNombre, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Facultad:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        txtFacultad = new JTextField(25);
        formPanel.add(txtFacultad, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Estado:"), gbc);
        gbc.gridx = 1;
        cmbEstado = new JComboBox<>(new String[]{"Activo", "Inactivo"});
        formPanel.add(cmbEstado, gbc);

        // Botones
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnGuardar   = new JButton("Guardar");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar  = new JButton("Eliminar");
        JButton btnLimpiar   = new JButton("Limpiar");

        btnGuardar.setBackground(new Color(33, 130, 70));
        btnGuardar.setForeground(Color.BLACK);
        btnActualizar.setBackground(new Color(33, 80, 160));
        btnActualizar.setForeground(Color.BLACK);
        btnEliminar.setBackground(new Color(180, 40, 40));
        btnEliminar.setForeground(Color.BLACK);

        btnPanel.add(btnGuardar);
        btnPanel.add(btnActualizar);
        btnPanel.add(btnEliminar);
        btnPanel.add(btnLimpiar);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        formPanel.add(btnPanel, gbc);

        add(formPanel, BorderLayout.NORTH);

        // Tabla
        modeloTabla = new DefaultTableModel(new String[]{"ID", "Nombre", "Facultad", "Estado"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(40);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createTitledBorder("Programas registrados"));
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
                txtFacultad.setText((String) modeloTabla.getValueAt(fila, 2));
                cmbEstado.setSelectedItem(modeloTabla.getValueAt(fila, 3));
            }
        });

        cargarTabla();
    }

    private void guardar() {
        if (txtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre es obligatorio.");
            return;
        }
        Programa p = new Programa(0, txtNombre.getText().trim(),
                txtFacultad.getText().trim(), (String) cmbEstado.getSelectedItem());
        if (dao.insertar(p)) {
            JOptionPane.showMessageDialog(this, "Programa guardado correctamente.");
            limpiar();
            cargarTabla();
        } else {
            JOptionPane.showMessageDialog(this, "Error al guardar.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizar() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Seleccioná un programa de la tabla.");
            return;
        }
        Programa p = new Programa(idSeleccionado, txtNombre.getText().trim(),
                txtFacultad.getText().trim(), (String) cmbEstado.getSelectedItem());
        if (dao.actualizar(p)) {
            JOptionPane.showMessageDialog(this, "Programa actualizado.");
            limpiar();
            cargarTabla();
        }
    }

    private void eliminar() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Seleccioná un programa de la tabla.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "¿Eliminar este programa?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (dao.eliminar(idSeleccionado)) {
                JOptionPane.showMessageDialog(this, "Programa eliminado.");
                limpiar();
                cargarTabla();
            } else {
                JOptionPane.showMessageDialog(this, "No se puede eliminar, tiene registros asociados.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limpiar() {
        txtNombre.setText("");
        txtFacultad.setText("");
        cmbEstado.setSelectedIndex(0);
        idSeleccionado = -1;
        tabla.clearSelection();
    }

    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        List<Programa> lista = dao.listar();
        for (Programa p : lista) {
            modeloTabla.addRow(new Object[]{p.getIdPrograma(), p.getNombre(), p.getFacultad(), p.getEstado()});
        }
    }
}
