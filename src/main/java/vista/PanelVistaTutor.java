package vista;

import conexion.Conexion;
import modelo.UsuarioLogin;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class PanelVistaTutor extends JPanel {

    public PanelVistaTutor(UsuarioLogin usuario) {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblTitulo = new JLabel("Mi información como Tutor");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        add(lblTitulo, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Mis prácticas",    buildTabPracticas(usuario));
        tabs.addTab("Mis estudiantes",  buildTabEstudiantes(usuario));
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel buildTabPracticas(UsuarioLogin usuario) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"Práctica", "Semestre", "Tipo", "Fecha Inicio", "Fecha Fin", "Estado", "Programa"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabla = new JTable(modelo);
        tabla.setRowHeight(22);

        String sql = "SELECT p.Nombre AS Practica, p.Semestre, tp.Nombre AS Tipo, " +
                     "p.FechaInicio, p.FechaFin, p.Estado, pr.Nombre AS Programa " +
                     "FROM Tutor t " +
                     "JOIN TutorPractica tp2 ON t.IdTutor = tp2.IdTutor " +
                     "JOIN Practica p ON tp2.IdPractica = p.IdPractica " +
                     "JOIN TipoPractica tp ON p.IdTipoPrac = tp.IdTipoPrac " +
                     "JOIN Programa pr ON p.IdPrograma = pr.IdPrograma " +
                     "WHERE t.Documento = ? ORDER BY p.Semestre";
        cargarTabla(modelo, sql, usuario.getDocumento());

        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        if (modelo.getRowCount() == 0)
            panel.add(buildMsgVacio("No tenés prácticas asignadas aún."), BorderLayout.NORTH);
        return panel;
    }

    private JPanel buildTabEstudiantes(UsuarioLogin usuario) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"Estudiante", "Documento", "Correo", "Práctica", "Semestre", "Institución"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabla = new JTable(modelo);
        tabla.setRowHeight(22);

        // Estudiantes de las prácticas asignadas a este tutor
        String sql = "SELECT DISTINCT e.Nombre || ' ' || e.Apellido AS Estudiante, " +
                     "e.Documento, e.Correo, p.Nombre AS Practica, p.Semestre, " +
                     "i.Nombre AS Institucion " +
                     "FROM Tutor t " +
                     "JOIN TutorPractica tp ON t.IdTutor = tp.IdTutor " +
                     "JOIN Practica p ON tp.IdPractica = p.IdPractica " +
                     "JOIN EstudiantePractica ep ON p.IdPractica = ep.IdPractica " +
                     "JOIN Estudiante e ON ep.IdEstudiante = e.IdEstudiante " +
                     "LEFT JOIN EstudianteInstitucion ei ON e.IdEstudiante = ei.IdEstudiante " +
                     "LEFT JOIN Institucion i ON ei.IdInstitucion = i.IdInstitucion " +
                     "WHERE t.Documento = ? ORDER BY e.Apellido";
        cargarTabla(modelo, sql, usuario.getDocumento());

        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        if (modelo.getRowCount() == 0)
            panel.add(buildMsgVacio("No hay estudiantes en tus prácticas aún."), BorderLayout.NORTH);
        return panel;
    }

    private void cargarTabla(DefaultTableModel modelo, String sql, String documento) {
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, documento);
            ResultSet rs = ps.executeQuery();
            int cols = rs.getMetaData().getColumnCount();
            while (rs.next()) {
                Object[] fila = new Object[cols];
                for (int i = 0; i < cols; i++) fila[i] = rs.getObject(i + 1);
                modelo.addRow(fila);
            }
        } catch (SQLException e) {
            System.out.println("Error vista tutor: " + e.getMessage());
        }
    }

    private JLabel buildMsgVacio(String msg) {
        JLabel lbl = new JLabel("  " + msg);
        lbl.setFont(new Font("SansSerif", Font.ITALIC, 12));
        lbl.setForeground(new Color(150, 100, 0));
        return lbl;
    }
}
