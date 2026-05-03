package vista;

import dao.PracticaDAO;
import dao.ProgramaDAO;
import modelo.Practica;
import modelo.Programa;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PanelPracticas extends JPanel {

    private JTextField txtNombre, txtSemestre, txtFechaInicio, txtFechaFin;
    private JComboBox<String> cmbEstado;
    private JComboBox<Programa> cmbPrograma;
    private JComboBox<String> cmbTipo;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private PracticaDAO dao;
    private ProgramaDAO programaDAO;
    private int idSeleccionado = -1;
    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy");

    public PanelPracticas() {
        dao = new PracticaDAO();
        programaDAO = new ProgramaDAO();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblTitulo = new JLabel("Gestión de Prácticas Académicas");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        add(lblTitulo, BorderLayout.NORTH);

        // Formulario
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Datos de la práctica"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 5, 4, 5);
        gbc.anchor = GridBagConstraints.WEST;

        txtNombre      = new JTextField(22);
        txtSemestre    = new JTextField(5);
        txtFechaInicio = new JTextField(10);
        txtFechaFin    = new JTextField(10);
        cmbEstado      = new JComboBox<>(new String[]{"Abierta", "Cerrada"});
        cmbTipo        = new JComboBox<>(new String[]{"1", "2"}); // IDs de TipoPractica

        Object[][] campos = {
            {"Nombre:", txtNombre},
            {"Semestre:", txtSemestre},
            {"Fecha inicio (dd/mm/aaaa):", txtFechaInicio},
            {"Fecha fin (dd/mm/aaaa):", txtFechaFin},
            {"Estado:", cmbEstado}
        };

        for (int i = 0; i < campos.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
            formPanel.add(new JLabel((String) campos[i][0]), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
            formPanel.add((Component) campos[i][1], gbc);
        }

        gbc.gridx = 0; gbc.gridy = campos.length; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Programa:"), gbc);
        gbc.gridx = 1;
        cmbPrograma = new JComboBox<>();
        cargarProgramas();
        formPanel.add(cmbPrograma, gbc);

        gbc.gridx = 0; gbc.gridy = campos.length + 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Tipo práctica (ID):"), gbc);
        gbc.gridx = 1;
        formPanel.add(cmbTipo, gbc);

        // Botones
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnGuardar    = new JButton("Guardar");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar   = new JButton("Eliminar");
        JButton btnAbrir      = new JButton("Abrir");
        JButton btnCerrar     = new JButton("Cerrar");
        JButton btnLimpiar    = new JButton("Limpiar");

        btnGuardar.setBackground(new Color(33, 130, 70));    btnGuardar.setForeground(Color.BLACK);
        btnActualizar.setBackground(new Color(33, 80, 160)); btnActualizar.setForeground(Color.BLACK);
        btnEliminar.setBackground(new Color(180, 40, 40));   btnEliminar.setForeground(Color.BLACK);
        btnAbrir.setBackground(new Color(0, 140, 100));      btnAbrir.setForeground(Color.BLACK);
        btnCerrar.setBackground(new Color(140, 80, 0));      btnCerrar.setForeground(Color.BLACK);

        btnPanel.add(btnGuardar); btnPanel.add(btnActualizar); btnPanel.add(btnEliminar);
        btnPanel.add(btnAbrir);   btnPanel.add(btnCerrar);     btnPanel.add(btnLimpiar);

        gbc.gridx = 0; gbc.gridy = campos.length + 2; gbc.gridwidth = 2;
        formPanel.add(btnPanel, gbc);

        add(formPanel, BorderLayout.NORTH);

        // Tabla
        modeloTabla = new DefaultTableModel(
            new String[]{"ID", "Nombre", "Semestre", "Inicio", "Fin", "Estado", "Programa"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createTitledBorder("Prácticas registradas"));
        add(scroll, BorderLayout.CENTER);

        // Eventos
        btnGuardar.addActionListener(e -> guardar());
        btnActualizar.addActionListener(e -> actualizar());
        btnEliminar.addActionListener(e -> eliminar());
        btnAbrir.addActionListener(e -> cambiarEstado("Abierta"));
        btnCerrar.addActionListener(e -> cambiarEstado("Cerrada"));
        btnLimpiar.addActionListener(e -> limpiar());

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() != -1) {
                int fila = tabla.getSelectedRow();
                idSeleccionado = (int) modeloTabla.getValueAt(fila, 0);
                txtNombre.setText((String) modeloTabla.getValueAt(fila, 1));
                txtSemestre.setText(String.valueOf(modeloTabla.getValueAt(fila, 2)));
                txtFechaInicio.setText((String) modeloTabla.getValueAt(fila, 3));
                txtFechaFin.setText((String) modeloTabla.getValueAt(fila, 4));
                cmbEstado.setSelectedItem(modeloTabla.getValueAt(fila, 5));
            }
        });

        cargarTabla();
    }

    private void cargarProgramas() {
        cmbPrograma.removeAllItems();
        for (Programa p : programaDAO.listar()) cmbPrograma.addItem(p);
    }

    private Date parseFecha(String texto) {
        try { return SDF.parse(texto); } catch (ParseException e) { return null; }
    }

    private void guardar() {
        if (txtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre es obligatorio."); return;
        }
        Date fi = parseFecha(txtFechaInicio.getText());
        Date ff = parseFecha(txtFechaFin.getText());
        if (fi == null || ff == null) {
            JOptionPane.showMessageDialog(this, "Formato de fecha inválido. Use dd/mm/aaaa."); return;
        }
        if (cmbPrograma.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Registrá al menos un programa primero."); return;
        }
        int sem;
        try { sem = Integer.parseInt(txtSemestre.getText().trim()); }
        catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this, "Semestre debe ser un número."); return; }

        Practica p = new Practica(0, txtNombre.getText().trim(), sem, fi, ff,
                (String) cmbEstado.getSelectedItem(),
                ((Programa) cmbPrograma.getSelectedItem()).getIdPrograma(),
                Integer.parseInt((String) cmbTipo.getSelectedItem()));

        if (dao.insertar(p)) {
            JOptionPane.showMessageDialog(this, "Práctica guardada correctamente.");
            limpiar(); cargarTabla();
        } else {
            JOptionPane.showMessageDialog(this, "Error al guardar.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizar() {
        if (idSeleccionado == -1) { JOptionPane.showMessageDialog(this, "Seleccioná una práctica."); return; }
        Date fi = parseFecha(txtFechaInicio.getText());
        Date ff = parseFecha(txtFechaFin.getText());
        if (fi == null || ff == null) { JOptionPane.showMessageDialog(this, "Formato de fecha inválido."); return; }
        int sem;
        try { sem = Integer.parseInt(txtSemestre.getText().trim()); }
        catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this, "Semestre debe ser un número."); return; }

        Practica p = new Practica(idSeleccionado, txtNombre.getText().trim(), sem, fi, ff,
                (String) cmbEstado.getSelectedItem(),
                ((Programa) cmbPrograma.getSelectedItem()).getIdPrograma(),
                Integer.parseInt((String) cmbTipo.getSelectedItem()));

        if (dao.actualizar(p)) {
            JOptionPane.showMessageDialog(this, "Práctica actualizada.");
            limpiar(); cargarTabla();
        }
    }

    private void cambiarEstado(String estado) {
        if (idSeleccionado == -1) { JOptionPane.showMessageDialog(this, "Seleccioná una práctica."); return; }
        if (dao.cambiarEstado(idSeleccionado, estado)) {
            JOptionPane.showMessageDialog(this, "Práctica " + estado.toLowerCase() + ".");
            cargarTabla();
        }
    }

    private void eliminar() {
        if (idSeleccionado == -1) { JOptionPane.showMessageDialog(this, "Seleccioná una práctica."); return; }
        int confirm = JOptionPane.showConfirmDialog(this, "¿Eliminar esta práctica?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (dao.eliminar(idSeleccionado)) {
                JOptionPane.showMessageDialog(this, "Práctica eliminada.");
                limpiar(); cargarTabla();
            } else {
                JOptionPane.showMessageDialog(this, "No se puede eliminar, tiene registros asociados.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limpiar() {
        txtNombre.setText(""); txtSemestre.setText("");
        txtFechaInicio.setText(""); txtFechaFin.setText("");
        cmbEstado.setSelectedIndex(0);
        idSeleccionado = -1;
        tabla.clearSelection();
        cargarProgramas();
    }

    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        List<Practica> lista = dao.listar();
        for (Practica p : lista) {
            modeloTabla.addRow(new Object[]{
                p.getIdPractica(), p.getNombre(), p.getSemestre(),
                SDF.format(p.getFechaInicio()), SDF.format(p.getFechaFin()),
                p.getEstado(), p.getIdPrograma()
            });
        }
    }
}
