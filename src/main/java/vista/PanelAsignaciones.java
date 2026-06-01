package vista;

import dao.AsignacionDAO;
import dao.AsesorPedagogicoDAO;
import dao.EstudianteDAO;
import dao.InstitucionDAO;
import dao.PracticaDAO;
import dao.TutorDAO;
import modelo.AsesorPedagogico;
import modelo.Estudiante;
import modelo.Institucion;
import modelo.Practica;
import modelo.Tutor;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;
import conexion.Conexion;
import java.sql.*;

public class PanelAsignaciones extends JPanel {

    private JTabbedPane tabs;
    private AsignacionDAO dao;

    public PanelAsignaciones() {
        dao = new AsignacionDAO();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblTitulo = new JLabel("Gestión de Asignaciones");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        add(lblTitulo, BorderLayout.NORTH);

        tabs = new JTabbedPane();
        tabs.addTab("Tutor → Práctica",         buildTabTutorPractica());
        tabs.addTab("Estudiante → Práctica",     buildTabEstudiantePractica());
        tabs.addTab("Estudiante → Institución",  buildTabEstudianteInstitucion());
        tabs.addTab("Asesor → Estudiante",       buildTabAsesorEstudiante());
        add(tabs, BorderLayout.CENTER);
    }

    // ── TAB: TUTOR ↔ PRÁCTICA ─────────────────────────────────
    // Validación: al seleccionar tutor, solo se muestran prácticas de su programa
    private JPanel buildTabTutorPractica() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JComboBox<Tutor> cmbTutor = new JComboBox<>();
        JComboBox<Practica> cmbPractica = new JComboBox<>();
        cargarTutores(cmbTutor);

        // Al cambiar tutor, filtrar prácticas de su programa
        cmbTutor.addActionListener(e -> {
            Tutor t = (Tutor) cmbTutor.getSelectedItem();
            if (t != null) cargarPracticasPorPrograma(cmbPractica, t.getIdPrograma());
        });
        if (cmbTutor.getItemCount() > 0)
            cargarPracticasPorPrograma(cmbPractica, ((Tutor) cmbTutor.getItemAt(0)).getIdPrograma());

        JLabel lblAviso = new JLabel("  Solo se muestran prácticas del programa del tutor seleccionado.");
        lblAviso.setFont(new Font("SansSerif", Font.ITALIC, 11));
        lblAviso.setForeground(new Color(80, 100, 160));

        JPanel form = buildFormDosCombo("Tutor:", cmbTutor, "Práctica:", cmbPractica);

        DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"ID Tutor", "Tutor", "ID Práctica", "Práctica", "Semestre", "Estado"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabla = new JTable(modelo);
        JScrollPane scroll = new JScrollPane(tabla);

        JButton btnAsignar    = buildBtn("Asignar",    new Color(33, 130, 70));
        JButton btnDesasignar = buildBtn("Desasignar", new Color(180, 40, 40));
        JButton btnRefrescar  = buildBtn("⟳ Refrescar", new Color(80, 80, 80));

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btns.add(btnAsignar); btns.add(btnDesasignar); btns.add(btnRefrescar);

        btnAsignar.addActionListener(e -> {
            if (cmbTutor.getSelectedItem() == null || cmbPractica.getSelectedItem() == null) return;
            int idT = ((Tutor) cmbTutor.getSelectedItem()).getIdTutor();
            int idP = ((Practica) cmbPractica.getSelectedItem()).getIdPractica();
            if (dao.asignarTutorPractica(idT, idP))
                JOptionPane.showMessageDialog(panel, "Asignado correctamente.");
            else
                JOptionPane.showMessageDialog(panel, "Error al asignar.", "Error", JOptionPane.ERROR_MESSAGE);
            recargarTabla(modelo, dao.listarTutorPractica());
        });

        btnDesasignar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila == -1) { JOptionPane.showMessageDialog(panel, "Seleccioná una fila de la tabla."); return; }
            int idT = (int) modelo.getValueAt(fila, 0);
            int idP = (int) modelo.getValueAt(fila, 2);
            int confirm = JOptionPane.showConfirmDialog(panel,
                "¿Desasignar a " + modelo.getValueAt(fila, 1) + " de la práctica " + modelo.getValueAt(fila, 3) + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;
            if (dao.desasignarTutorPractica(idT, idP))
                JOptionPane.showMessageDialog(panel, "Desasignado correctamente.");
            else
                JOptionPane.showMessageDialog(panel, "Error al desasignar.", "Error", JOptionPane.ERROR_MESSAGE);
            recargarTabla(modelo, dao.listarTutorPractica());
        });

        btnRefrescar.addActionListener(e -> {
            cargarTutores(cmbTutor);
            Tutor t = (Tutor) cmbTutor.getSelectedItem();
            if (t != null) cargarPracticasPorPrograma(cmbPractica, t.getIdPrograma());
            recargarTabla(modelo, dao.listarTutorPractica());
        });

        recargarTabla(modelo, dao.listarTutorPractica());

        JPanel top = new JPanel(new BorderLayout());
        top.add(form, BorderLayout.CENTER);
        top.add(lblAviso, BorderLayout.SOUTH);

        panel.add(top, BorderLayout.NORTH);
        panel.add(btns, BorderLayout.CENTER);
        scroll.setPreferredSize(new Dimension(0, 280));
        panel.add(scroll, BorderLayout.SOUTH);
        return panel;
    }

    // ── TAB: ESTUDIANTE ↔ PRÁCTICA ────────────────────────────
    // Validación: al seleccionar estudiante, solo se muestran prácticas de su programa
    private JPanel buildTabEstudiantePractica() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JComboBox<Estudiante> cmbEst  = new JComboBox<>();
        JComboBox<Practica>   cmbPrac = new JComboBox<>();
        cargarEstudiantes(cmbEst);

        // Al cambiar estudiante, filtrar prácticas de su programa
        cmbEst.addActionListener(e -> {
            Estudiante est = (Estudiante) cmbEst.getSelectedItem();
            if (est != null) cargarPracticasPorPrograma(cmbPrac, est.getIdPrograma());
        });
        if (cmbEst.getItemCount() > 0)
            cargarPracticasPorPrograma(cmbPrac, ((Estudiante) cmbEst.getItemAt(0)).getIdPrograma());

        JLabel lblAviso = new JLabel("  Solo se muestran prácticas del programa del estudiante seleccionado.");
        lblAviso.setFont(new Font("SansSerif", Font.ITALIC, 11));
        lblAviso.setForeground(new Color(80, 100, 160));

        JPanel form = buildFormDosCombo("Estudiante:", cmbEst, "Práctica:", cmbPrac);

        DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"ID Estudiante", "Estudiante", "ID Práctica", "Práctica", "Semestre", "Estado"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabla = new JTable(modelo);
        JScrollPane scroll = new JScrollPane(tabla);

        JButton btnAsignar    = buildBtn("Asignar",    new Color(33, 130, 70));
        JButton btnDesasignar = buildBtn("Desasignar", new Color(180, 40, 40));
        JButton btnRefrescar  = buildBtn("⟳ Refrescar", new Color(80, 80, 80));

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btns.add(btnAsignar); btns.add(btnDesasignar); btns.add(btnRefrescar);

        btnAsignar.addActionListener(e -> {
            Estudiante est = (Estudiante) cmbEst.getSelectedItem();
            Practica prac  = (Practica)   cmbPrac.getSelectedItem();
            if (est == null || prac == null) return;
            // Validación: confirmar que la práctica pertenece al programa del estudiante
            if (prac.getIdPrograma() != est.getIdPrograma()) {
                JOptionPane.showMessageDialog(panel,
                    "La práctica seleccionada no pertenece al programa del estudiante.",
                    "Asignación inválida", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (dao.asignarEstudiantePractica(est.getIdEstudiante(), prac.getIdPractica()))
                JOptionPane.showMessageDialog(panel, "Asignado correctamente.");
            else
                JOptionPane.showMessageDialog(panel, "Error al asignar.", "Error", JOptionPane.ERROR_MESSAGE);
            recargarTabla(modelo, dao.listarEstudiantePractica());
        });

        btnDesasignar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila == -1) { JOptionPane.showMessageDialog(panel, "Seleccioná una fila de la tabla."); return; }
            int idEst  = (int) modelo.getValueAt(fila, 0);
            int idPrac = (int) modelo.getValueAt(fila, 2);
            int confirm = JOptionPane.showConfirmDialog(panel,
                "¿Desasignar a " + modelo.getValueAt(fila, 1) + " de la práctica " + modelo.getValueAt(fila, 3) + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;
            if (dao.desasignarEstudiantePractica(idEst, idPrac))
                JOptionPane.showMessageDialog(panel, "Desasignado correctamente.");
            else
                JOptionPane.showMessageDialog(panel, "Error al desasignar.", "Error", JOptionPane.ERROR_MESSAGE);
            recargarTabla(modelo, dao.listarEstudiantePractica());
        });

        btnRefrescar.addActionListener(e -> {
            cargarEstudiantes(cmbEst);
            Estudiante est = (Estudiante) cmbEst.getSelectedItem();
            if (est != null) cargarPracticasPorPrograma(cmbPrac, est.getIdPrograma());
            recargarTabla(modelo, dao.listarEstudiantePractica());
        });

        recargarTabla(modelo, dao.listarEstudiantePractica());

        JPanel top = new JPanel(new BorderLayout());
        top.add(form, BorderLayout.CENTER);
        top.add(lblAviso, BorderLayout.SOUTH);

        panel.add(top, BorderLayout.NORTH);
        panel.add(btns, BorderLayout.CENTER);
        scroll.setPreferredSize(new Dimension(0, 280));
        panel.add(scroll, BorderLayout.SOUTH);
        return panel;
    }

    // ── TAB: ESTUDIANTE ↔ INSTITUCIÓN ─────────────────────────
    // Sin restricción especial: cualquier estudiante puede ir a cualquier institución
    private JPanel buildTabEstudianteInstitucion() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JComboBox<Estudiante> cmbEst   = new JComboBox<>();
        JComboBox<Institucion> cmbInst = new JComboBox<>();
        cargarEstudiantes(cmbEst);
        cargarInstituciones(cmbInst);

        JPanel form = buildFormDosCombo("Estudiante:", cmbEst, "Institución:", cmbInst);

        DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"ID Estudiante", "Estudiante", "ID Institución", "Institución", "Tipo"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabla = new JTable(modelo);
        JScrollPane scroll = new JScrollPane(tabla);

        JButton btnAsignar    = buildBtn("Asignar",    new Color(33, 130, 70));
        JButton btnDesasignar = buildBtn("Desasignar", new Color(180, 40, 40));
        JButton btnRefrescar  = buildBtn("⟳ Refrescar", new Color(80, 80, 80));

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btns.add(btnAsignar); btns.add(btnDesasignar); btns.add(btnRefrescar);

        btnAsignar.addActionListener(e -> {
            if (cmbEst.getSelectedItem() == null || cmbInst.getSelectedItem() == null) return;
            int idE = ((Estudiante) cmbEst.getSelectedItem()).getIdEstudiante();
            int idI = ((Institucion) cmbInst.getSelectedItem()).getIdInstitucion();
            if (dao.asignarEstudianteInstitucion(idE, idI))
                JOptionPane.showMessageDialog(panel, "Asignado correctamente.");
            else
                JOptionPane.showMessageDialog(panel, "Error al asignar.", "Error", JOptionPane.ERROR_MESSAGE);
            recargarTabla(modelo, dao.listarEstudianteInstitucion());
        });

        btnDesasignar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila == -1) { JOptionPane.showMessageDialog(panel, "Seleccioná una fila de la tabla."); return; }
            int idE = (int) modelo.getValueAt(fila, 0);
            int idI = (int) modelo.getValueAt(fila, 2);
            int confirm = JOptionPane.showConfirmDialog(panel,
                "¿Desasignar a " + modelo.getValueAt(fila, 1) + " de " + modelo.getValueAt(fila, 3) + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;
            if (dao.desasignarEstudianteInstitucion(idE, idI))
                JOptionPane.showMessageDialog(panel, "Desasignado correctamente.");
            else
                JOptionPane.showMessageDialog(panel, "Error al desasignar.", "Error", JOptionPane.ERROR_MESSAGE);
            recargarTabla(modelo, dao.listarEstudianteInstitucion());
        });

        btnRefrescar.addActionListener(e -> {
            cargarEstudiantes(cmbEst); cargarInstituciones(cmbInst);
            recargarTabla(modelo, dao.listarEstudianteInstitucion());
        });

        recargarTabla(modelo, dao.listarEstudianteInstitucion());

        panel.add(form, BorderLayout.NORTH);
        panel.add(btns, BorderLayout.CENTER);
        scroll.setPreferredSize(new Dimension(0, 280));
        panel.add(scroll, BorderLayout.SOUTH);
        return panel;
    }

    // ── TAB: ASESOR ↔ ESTUDIANTE ──────────────────────────────
    // Validación: al seleccionar asesor, solo se muestran estudiantes asignados
    // a la institución de ese asesor
    private JPanel buildTabAsesorEstudiante() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JComboBox<AsesorPedagogico> cmbAsesor = new JComboBox<>();
        JComboBox<Estudiante>       cmbEst    = new JComboBox<>();
        cargarAsesores(cmbAsesor);

        // Al cambiar asesor, filtrar estudiantes de su institución
        cmbAsesor.addActionListener(e -> {
            AsesorPedagogico a = (AsesorPedagogico) cmbAsesor.getSelectedItem();
            if (a != null) cargarEstudiantesPorInstitucion(cmbEst, a.getIdInstitucion());
        });
        if (cmbAsesor.getItemCount() > 0)
            cargarEstudiantesPorInstitucion(cmbEst,
                ((AsesorPedagogico) cmbAsesor.getItemAt(0)).getIdInstitucion());

        JLabel lblAviso = new JLabel("  Solo se muestran estudiantes asignados a la institución del asesor.");
        lblAviso.setFont(new Font("SansSerif", Font.ITALIC, 11));
        lblAviso.setForeground(new Color(80, 100, 160));

        JPanel form = buildFormDosCombo("Asesor:", cmbAsesor, "Estudiante:", cmbEst);

        DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"ID Asesor", "Asesor", "Institución", "ID Estudiante", "Estudiante"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabla = new JTable(modelo);
        JScrollPane scroll = new JScrollPane(tabla);

        JButton btnAsignar    = buildBtn("Asignar",    new Color(33, 130, 70));
        JButton btnDesasignar = buildBtn("Desasignar", new Color(180, 40, 40));
        JButton btnRefrescar  = buildBtn("⟳ Refrescar", new Color(80, 80, 80));

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btns.add(btnAsignar); btns.add(btnDesasignar); btns.add(btnRefrescar);

        btnAsignar.addActionListener(e -> {
            if (cmbAsesor.getSelectedItem() == null || cmbEst.getSelectedItem() == null) return;
            int idA = ((AsesorPedagogico) cmbAsesor.getSelectedItem()).getIdAsesor();
            int idE = ((Estudiante) cmbEst.getSelectedItem()).getIdEstudiante();
            if (dao.asignarAsesorEstudiante(idA, idE))
                JOptionPane.showMessageDialog(panel, "Asignado correctamente.");
            else
                JOptionPane.showMessageDialog(panel, "Error al asignar.", "Error", JOptionPane.ERROR_MESSAGE);
            recargarTabla(modelo, dao.listarAsesorEstudiante());
        });

        btnDesasignar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila == -1) { JOptionPane.showMessageDialog(panel, "Seleccioná una fila de la tabla."); return; }
            int idA = (int) modelo.getValueAt(fila, 0);
            int idE = (int) modelo.getValueAt(fila, 3);
            int confirm = JOptionPane.showConfirmDialog(panel,
                "¿Desasignar a " + modelo.getValueAt(fila, 4) + " del asesor " + modelo.getValueAt(fila, 1) + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;
            if (dao.desasignarAsesorEstudiante(idA, idE))
                JOptionPane.showMessageDialog(panel, "Desasignado correctamente.");
            else
                JOptionPane.showMessageDialog(panel, "Error al desasignar.", "Error", JOptionPane.ERROR_MESSAGE);
            recargarTabla(modelo, dao.listarAsesorEstudiante());
        });

        btnRefrescar.addActionListener(e -> {
            cargarAsesores(cmbAsesor);
            AsesorPedagogico a = (AsesorPedagogico) cmbAsesor.getSelectedItem();
            if (a != null) cargarEstudiantesPorInstitucion(cmbEst, a.getIdInstitucion());
            recargarTabla(modelo, dao.listarAsesorEstudiante());
        });

        recargarTabla(modelo, dao.listarAsesorEstudiante());

        JPanel top = new JPanel(new BorderLayout());
        top.add(form, BorderLayout.CENTER);
        top.add(lblAviso, BorderLayout.SOUTH);

        panel.add(top, BorderLayout.NORTH);
        panel.add(btns, BorderLayout.CENTER);
        scroll.setPreferredSize(new Dimension(0, 280));
        panel.add(scroll, BorderLayout.SOUTH);
        return panel;
    }

    // ── HELPERS ───────────────────────────────────────────────

    /** Carga prácticas filtradas por programa */
    private void cargarPracticasPorPrograma(JComboBox<Practica> cmb, int idPrograma) {
        cmb.removeAllItems();
        String sql = "SELECT IdPractica, Nombre, Semestre, FechaInicio, FechaFin, Estado, IdPrograma, IdTipoPrac " +
                     "FROM Practica WHERE IdPrograma = ? ORDER BY Semestre";
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPrograma);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                cmb.addItem(new Practica(
                    rs.getInt("IdPractica"), rs.getString("Nombre"), rs.getInt("Semestre"),
                    rs.getDate("FechaInicio"), rs.getDate("FechaFin"),
                    rs.getString("Estado"), rs.getInt("IdPrograma"), rs.getInt("IdTipoPrac")));
            }
        } catch (SQLException e) {
            System.out.println("Error cargar prácticas por programa: " + e.getMessage());
        }
        if (cmb.getItemCount() == 0)
            cmb.addItem(new Practica(0, "— Sin prácticas en este programa —", 0, null, null, "", idPrograma, 0));
    }

    /** Carga estudiantes que ya están asignados a una institución específica */
    private void cargarEstudiantesPorInstitucion(JComboBox<Estudiante> cmb, int idInstitucion) {
        cmb.removeAllItems();
        String sql = "SELECT e.IdEstudiante, e.Nombre, e.Apellido, e.Documento, e.Correo, e.IdPrograma " +
                     "FROM Estudiante e " +
                     "JOIN EstudianteInstitucion ei ON e.IdEstudiante = ei.IdEstudiante " +
                     "WHERE ei.IdInstitucion = ? ORDER BY e.Apellido";
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idInstitucion);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                cmb.addItem(new Estudiante(
                    rs.getInt("IdEstudiante"), rs.getString("Nombre"), rs.getString("Apellido"),
                    rs.getString("Documento"), rs.getString("Correo"), rs.getInt("IdPrograma")));
            }
        } catch (SQLException e) {
            System.out.println("Error cargar estudiantes por institución: " + e.getMessage());
        }
        if (cmb.getItemCount() == 0) {
            // Fallback: si no hay estudiantes asignados aún, mostrar todos con aviso
            for (Estudiante est : new EstudianteDAO().listar()) cmb.addItem(est);
        }
    }

    private JPanel buildFormDosCombo(String lbl1, JComboBox<?> cmb1, String lbl2, JComboBox<?> cmb2) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createTitledBorder("Seleccionar"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 5, 4, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        p.add(new JLabel(lbl1), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        p.add(cmb1, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        p.add(new JLabel(lbl2), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        p.add(cmb2, gbc);
        return p;
    }

    private JButton buildBtn(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setBackground(color);
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        return btn;
    }

    private void recargarTabla(DefaultTableModel modelo, List<Object[]> datos) {
        modelo.setRowCount(0);
        for (Object[] fila : datos) modelo.addRow(fila);
    }

    private void cargarTutores(JComboBox<Tutor> cmb) {
        cmb.removeAllItems();
        for (Tutor t : new TutorDAO().listar()) cmb.addItem(t);
    }

    private void cargarEstudiantes(JComboBox<Estudiante> cmb) {
        cmb.removeAllItems();
        for (Estudiante e : new EstudianteDAO().listar()) cmb.addItem(e);
    }

    private void cargarInstituciones(JComboBox<Institucion> cmb) {
        cmb.removeAllItems();
        for (Institucion i : new InstitucionDAO().listar()) cmb.addItem(i);
    }

    private void cargarAsesores(JComboBox<AsesorPedagogico> cmb) {
        cmb.removeAllItems();
        for (AsesorPedagogico a : new AsesorPedagogicoDAO().listar()) cmb.addItem(a);
    }
}