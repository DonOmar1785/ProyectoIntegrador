package vista;

import conexion.Conexion;
import modelo.UsuarioLogin;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class PanelVistaEstudiante extends JPanel {

    public PanelVistaEstudiante(UsuarioLogin usuario) {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblTitulo = new JLabel("Mi información de práctica");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        add(lblTitulo, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Mi práctica",     buildTabPractica(usuario));
        tabs.addTab("Mi institución",  buildTabInstitucion(usuario));
        tabs.addTab("Mi asesor",       buildTabAsesor(usuario));
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel buildTabPractica(UsuarioLogin usuario) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"Práctica", "Semestre", "Tipo", "Fecha Inicio", "Fecha Fin", "Estado", "Programa"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabla = new JTable(modelo);
        tabla.setRowHeight(22);

        // Buscar IdEstudiante por documento del usuario logueado
        String sql = "SELECT p.Nombre AS Practica, p.Semestre, tp.Nombre AS Tipo, " +
                     "p.FechaInicio, p.FechaFin, p.Estado, pr.Nombre AS Programa " +
                     "FROM Estudiante e " +
                     "JOIN EstudiantePractica ep ON e.IdEstudiante = ep.IdEstudiante " +
                     "JOIN Practica p ON ep.IdPractica = p.IdPractica " +
                     "JOIN TipoPractica tp ON p.IdTipoPrac = tp.IdTipoPrac " +
                     "JOIN Programa pr ON p.IdPrograma = pr.IdPrograma " +
                     "WHERE e.Documento = ?";
        cargarTabla(modelo, sql, usuario.getDocumento());

        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        if (modelo.getRowCount() == 0)
            panel.add(buildMsgVacio("No tenés prácticas asignadas aún."), BorderLayout.NORTH);
        return panel;
    }

    private JPanel buildTabInstitucion(UsuarioLogin usuario) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"Institución", "Tipo", "Dirección", "Contacto"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabla = new JTable(modelo);
        tabla.setRowHeight(22);

        String sql = "SELECT i.Nombre, i.Tipo, i.Direccion, i.Contacto " +
                     "FROM Estudiante e " +
                     "JOIN EstudianteInstitucion ei ON e.IdEstudiante = ei.IdEstudiante " +
                     "JOIN Institucion i ON ei.IdInstitucion = i.IdInstitucion " +
                     "WHERE e.Documento = ?";
        cargarTabla(modelo, sql, usuario.getDocumento());

        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        if (modelo.getRowCount() == 0)
            panel.add(buildMsgVacio("No tenés institución asignada aún."), BorderLayout.NORTH);
        return panel;
    }

    private JPanel buildTabAsesor(UsuarioLogin usuario) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"Asesor", "Correo", "Institución"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabla = new JTable(modelo);
        tabla.setRowHeight(22);

        String sql = "SELECT a.Nombre || ' ' || a.Apellido AS Asesor, a.Correo, i.Nombre AS Institucion " +
                     "FROM Estudiante e " +
                     "JOIN AsesorEstudiante ae ON e.IdEstudiante = ae.IdEstudiante " +
                     "JOIN AsesorPedagogico a ON ae.IdAsesor = a.IdAsesor " +
                     "JOIN Institucion i ON a.IdInstitucion = i.IdInstitucion " +
                     "WHERE e.Documento = ?";
        cargarTabla(modelo, sql, usuario.getDocumento());

        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        if (modelo.getRowCount() == 0)
            panel.add(buildMsgVacio("No tenés asesor asignado aún."), BorderLayout.NORTH);
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
            System.out.println("Error vista estudiante: " + e.getMessage());
        }
    }

    private JLabel buildMsgVacio(String msg) {
        JLabel lbl = new JLabel("  " + msg);
        lbl.setFont(new Font("SansSerif", Font.ITALIC, 12));
        lbl.setForeground(new Color(150, 100, 0));
        return lbl;
    }
}
