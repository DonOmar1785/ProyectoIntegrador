package vista;

import dao.PracticaDAO;
import dao.ProgramaDAO;
import dao.TipoPracticaDAO;
import modelo.Practica;
import modelo.Programa;
import modelo.TipoPractica;
import util.SelectorFecha;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PanelPracticas extends JPanel {

    private JTextField txtNombre;
    private SelectorFecha selectorInicio, selectorFin;
    private JSpinner spnSemestre;
    private JComboBox<String> cmbEstado;
    private JComboBox<Programa> cmbPrograma;
    private JComboBox<TipoPractica> cmbTipo;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private PracticaDAO dao;
    private ProgramaDAO programaDAO;
    private TipoPracticaDAO tipoPracticaDAO;
    private int idSeleccionado = -1;
    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy");

    public PanelPracticas() {
        dao = new PracticaDAO();
        programaDAO = new ProgramaDAO();
        tipoPracticaDAO = new TipoPracticaDAO();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblTitulo = new JLabel("Gestión de Prácticas Académicas");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        add(lblTitulo, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Datos de la práctica"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 5, 4, 5);
        gbc.anchor = GridBagConstraints.WEST;

        txtNombre      = new JTextField(22);
        selectorInicio = new SelectorFecha();
        selectorFin    = new SelectorFecha();
        // Estado inicial SIEMPRE Cerrada (la directora abre explícitamente, nunca queda abierta al crear)
        cmbEstado      = new JComboBox<>(new String[]{"Cerrada", "Abierta"});
        cmbPrograma    = new JComboBox<>();
        cmbTipo        = new JComboBox<>();

        // Semestre como spinner: 1 a 8 (máximo permitido por el programa de licenciatura)
        SpinnerNumberModel spinModel = new SpinnerNumberModel(1, 1, 8, 1);
        spnSemestre = new JSpinner(spinModel);
        spnSemestre.setPreferredSize(new Dimension(60, 25));

        // Listener: al cambiar semestre, sugerir tipo automáticamente (debe ir DESPUÉS de crear el spinner)
        spnSemestre.addChangeListener(e -> sugerirTipoPorSemestre());

        Object[][] campos = {
            {"Nombre:",            txtNombre},
            {"Semestre (1–8):",   spnSemestre},
            {"Fecha inicio:",      selectorInicio},
            {"Fecha fin:",         selectorFin},
            {"Estado:",            cmbEstado}
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

        gbc.gridx = 0; gbc.gridy = campos.length + 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Tipo de práctica:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        cargarTipos();
        formPanel.add(cmbTipo, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnGuardar    = new JButton("Guardar");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar   = new JButton("Eliminar");
        JButton btnAbrir      = new JButton("Abrir");
        JButton btnCerrar     = new JButton("Cerrar");
        JButton btnLimpiar    = new JButton("Limpiar");
        JButton btnRefrescar  = new JButton("⟳ Refrescar");

        btnGuardar.setBackground(new Color(33, 130, 70));     btnGuardar.setForeground(Color.BLACK);
        btnActualizar.setBackground(new Color(33, 80, 160));  btnActualizar.setForeground(Color.BLACK);
        btnEliminar.setBackground(new Color(180, 40, 40));    btnEliminar.setForeground(Color.BLACK);
        btnAbrir.setBackground(new Color(0, 140, 100));       btnAbrir.setForeground(Color.BLACK);
        btnCerrar.setBackground(new Color(140, 80, 0));       btnCerrar.setForeground(Color.BLACK);
        btnRefrescar.setBackground(new Color(80, 80, 80));    btnRefrescar.setForeground(Color.BLACK);

        for (JButton b : new JButton[]{btnGuardar, btnActualizar, btnEliminar,
                                        btnAbrir, btnCerrar, btnLimpiar, btnRefrescar}) {
            b.setFocusPainted(false);
            btnPanel.add(b);
        }

        gbc.gridx = 0; gbc.gridy = campos.length + 2; gbc.gridwidth = 2;
        formPanel.add(btnPanel, gbc);
        add(formPanel, BorderLayout.NORTH);

        modeloTabla = new DefaultTableModel(
            new String[]{"ID", "Nombre", "Semestre", "Inicio", "Fin", "Estado", "Programa", "Tipo"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createTitledBorder("Prácticas registradas"));
        add(scroll, BorderLayout.CENTER);

        btnGuardar.addActionListener(e -> guardar());
        btnActualizar.addActionListener(e -> actualizar());
        btnEliminar.addActionListener(e -> eliminar());
        btnAbrir.addActionListener(e -> cambiarEstado("Abierta"));
        btnCerrar.addActionListener(e -> cambiarEstado("Cerrada"));
        btnLimpiar.addActionListener(e -> limpiar());
        btnRefrescar.addActionListener(e -> { cargarProgramas(); cargarTipos(); cargarTabla();
            JOptionPane.showMessageDialog(this, "Datos actualizados."); });

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() != -1) {
                int fila = tabla.getSelectedRow();
                idSeleccionado = (int) modeloTabla.getValueAt(fila, 0);
                txtNombre.setText((String) modeloTabla.getValueAt(fila, 1));
                spnSemestre.setValue(modeloTabla.getValueAt(fila, 2));
                // Precargar fechas desde la tabla (formato dd/MM/yyyy)
                try {
                    selectorInicio.setFecha(SDF.parse((String) modeloTabla.getValueAt(fila, 3)));
                    selectorFin.setFecha(SDF.parse((String) modeloTabla.getValueAt(fila, 4)));
                } catch (Exception ex) {
                    selectorInicio.limpiar(); selectorFin.limpiar();
                }
                cmbEstado.setSelectedItem(modeloTabla.getValueAt(fila, 5));
            }
        });

        cargarTabla();
        // Sugerir tipo por defecto según semestre inicial (1 = Práctica Pedagógica)
        sugerirTipoPorSemestre();
    }

    /**
     * Al cambiar el semestre, selecciona automáticamente el tipo de práctica correspondiente:
     * Semestres 1-3 → Práctica Pedagógica
     * Semestres 4-8 → Práctica Pedagógica Investigativa
     */
    private void sugerirTipoPorSemestre() {
        if (cmbTipo.getItemCount() == 0) return;
        int sem = (int) spnSemestre.getValue();
        String sugerido = util.Validador.sugerirTipoPractica(sem);
        if (sugerido == null) return;
        for (int i = 0; i < cmbTipo.getItemCount(); i++) {
            TipoPractica tipo = cmbTipo.getItemAt(i);
            if (tipo.getNombre().toLowerCase().contains(sugerido.toLowerCase().substring(0, 10))) {
                cmbTipo.setSelectedIndex(i);
                return;
            }
        }
    }

    private void cargarProgramas() {
        cmbPrograma.removeAllItems();
        for (Programa p : programaDAO.listar()) cmbPrograma.addItem(p);
    }

    private void cargarTipos() {
        cmbTipo.removeAllItems();
        for (TipoPractica t : tipoPracticaDAO.listar()) cmbTipo.addItem(t);
    }

    private void guardar() {
        // Validar nombre
        String errNombre = util.Validador.validarTexto(txtNombre.getText().trim(), "El nombre");
        if (errNombre != null) { JOptionPane.showMessageDialog(this, errNombre); return; }

        // Validar semestre (1-8)
        int sem = (int) spnSemestre.getValue();
        String errSem = util.Validador.validarSemestre(sem);
        if (errSem != null) { JOptionPane.showMessageDialog(this, errSem); return; }

        // Validar fechas
        Date fi = selectorInicio.getFecha();
        Date ff = selectorFin.getFecha();
        String errFechas = util.Validador.validarFechasPractica(fi, ff);
        if (errFechas != null) { JOptionPane.showMessageDialog(this, errFechas); return; }

        // Validar programa seleccionado
        if (cmbPrograma.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Debe registrar al menos un programa primero."); return;
        }
        Programa progSeleccionado = (Programa) cmbPrograma.getSelectedItem();

        // Validar que el programa esté activo
        if (!"Activo".equalsIgnoreCase(progSeleccionado.getEstado())) {
            JOptionPane.showMessageDialog(this,
                "El programa seleccionado está inactivo.\nNo se pueden crear prácticas para programas inactivos.",
                "Programa inactivo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validar tipo de práctica
        if (cmbTipo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un tipo de práctica."); return;
        }

        Practica p = new Practica(0, txtNombre.getText().trim(), sem, fi, ff,
            (String) cmbEstado.getSelectedItem(),
            progSeleccionado.getIdPrograma(),
            ((TipoPractica) cmbTipo.getSelectedItem()).getIdTipoPrac());

        if (dao.insertar(p)) {
            JOptionPane.showMessageDialog(this, "Práctica guardada correctamente.");
            limpiar(); cargarTabla();
        } else {
            JOptionPane.showMessageDialog(this,
                "Error al guardar la práctica.\nVerifique que no exista ya una práctica en el semestre "
                + sem + " para este programa.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizar() {
        if (idSeleccionado == -1) { JOptionPane.showMessageDialog(this, "Seleccione una práctica de la tabla."); return; }

        String errNombre = util.Validador.validarTexto(txtNombre.getText().trim(), "El nombre");
        if (errNombre != null) { JOptionPane.showMessageDialog(this, errNombre); return; }

        int sem = (int) spnSemestre.getValue();
        String errSem = util.Validador.validarSemestre(sem);
        if (errSem != null) { JOptionPane.showMessageDialog(this, errSem); return; }

        Date fi = selectorInicio.getFecha();
        Date ff = selectorFin.getFecha();
        String errFechas = util.Validador.validarFechasPractica(fi, ff);
        if (errFechas != null) { JOptionPane.showMessageDialog(this, errFechas); return; }

        if (cmbPrograma.getSelectedItem() == null || cmbTipo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar programa y tipo de práctica."); return;
        }

        Practica p = new Practica(idSeleccionado, txtNombre.getText().trim(), sem, fi, ff,
            (String) cmbEstado.getSelectedItem(),
            ((Programa) cmbPrograma.getSelectedItem()).getIdPrograma(),
            ((TipoPractica) cmbTipo.getSelectedItem()).getIdTipoPrac());

        if (dao.actualizar(p)) {
            JOptionPane.showMessageDialog(this, "Práctica actualizada correctamente.");
            limpiar(); cargarTabla();
        } else {
            JOptionPane.showMessageDialog(this, "Error al actualizar la práctica.", "Error", JOptionPane.ERROR_MESSAGE);
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
        int c = JOptionPane.showConfirmDialog(this, "¿Eliminar esta práctica?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (c == JOptionPane.YES_OPTION) {
            if (dao.eliminar(idSeleccionado)) {
                JOptionPane.showMessageDialog(this, "Práctica eliminada.");
                limpiar(); cargarTabla();
            } else {
                JOptionPane.showMessageDialog(this, "No se puede eliminar, tiene registros asociados.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limpiar() {
        txtNombre.setText("");
        spnSemestre.setValue(1);
        selectorInicio.limpiar();
        selectorFin.limpiar();
        cmbEstado.setSelectedIndex(0);
        idSeleccionado = -1;
        tabla.clearSelection();
        cargarProgramas();
        cargarTipos();
    }

    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        String sql = "SELECT p.IdPractica, p.Nombre, p.Semestre, p.FechaInicio, p.FechaFin, p.Estado, " +
                     "pr.Nombre AS Programa, tp.Nombre AS Tipo " +
                     "FROM Practica p " +
                     "JOIN Programa pr ON p.IdPrograma = pr.IdPrograma " +
                     "JOIN TipoPractica tp ON p.IdTipoPrac = tp.IdTipoPrac " +
                     "ORDER BY p.IdPractica";
        try (java.sql.Connection con = conexion.Conexion.conectar();
             java.sql.Statement st = con.createStatement();
             java.sql.ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                modeloTabla.addRow(new Object[]{
                    rs.getInt("IdPractica"), rs.getString("Nombre"), rs.getInt("Semestre"),
                    SDF.format(rs.getDate("FechaInicio")), SDF.format(rs.getDate("FechaFin")),
                    rs.getString("Estado"), rs.getString("Programa"), rs.getString("Tipo")
                });
            }
        } catch (java.sql.SQLException e) {
            System.out.println("Error cargar prácticas: " + e.getMessage());
        }
    }
}