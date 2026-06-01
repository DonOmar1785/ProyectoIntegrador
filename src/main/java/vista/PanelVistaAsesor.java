package vista;

import conexion.Conexion;
import modelo.UsuarioLogin;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class PanelVistaAsesor extends JPanel {

    public PanelVistaAsesor(UsuarioLogin usuario) {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblTitulo = new JLabel("Mi información como Asesor Pedagógico");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        add(lblTitulo, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Mi institución",   buildTabInstitucion(usuario));
        tabs.addTab("Mis estudiantes",  buildTabEstudiantes(usuario));
        add(tabs, BorderLayout.CENTER);
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
                     "FROM AsesorPedagogico a " +
                     "JOIN Institucion i ON a.IdInstitucion = i.IdInstitucion " +
                     "WHERE a.Documento = ?";
        cargarTabla(modelo, sql, usuario.getDocumento());

        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        if (modelo.getRowCount() == 0)
            panel.add(buildMsgVacio("No tenés institución asignada."), BorderLayout.NORTH);
        return panel;
    }

    private JPanel buildTabEstudiantes(UsuarioLogin usuario) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"Estudiante", "Documento", "Correo", "Programa", "Práctica", "Semestre"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabla = new JTable(modelo);
        tabla.setRowHeight(22);

        String sql = "SELECT e.Nombre || ' ' || e.Apellido AS Estudiante, " +
                     "e.Documento, e.Correo, pr.Nombre AS Programa, " +
                     "p.Nombre AS Practica, p.Semestre " +
                     "FROM AsesorPedagogico a " +
                     "JOIN AsesorEstudiante ae ON a.IdAsesor = ae.IdAsesor " +
                     "JOIN Estudiante e ON ae.IdEstudiante = e.IdEstudiante " +
                     "JOIN Programa pr ON e.IdPrograma = pr.IdPrograma " +
                     "LEFT JOIN EstudiantePractica ep ON e.IdEstudiante = ep.IdEstudiante " +
                     "LEFT JOIN Practica p ON ep.IdPractica = p.IdPractica " +
                     "WHERE a.Documento = ? ORDER BY e.Apellido";
        cargarTabla(modelo, sql, usuario.getDocumento());

        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        if (modelo.getRowCount() == 0)
            panel.add(buildMsgVacio("No tenés estudiantes asignados aún."), BorderLayout.NORTH);
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
            System.out.println("Error vista asesor: " + e.getMessage());
        }
    }

    private JLabel buildMsgVacio(String msg) {
        JLabel lbl = new JLabel("  " + msg);
        lbl.setFont(new Font("SansSerif", Font.ITALIC, 12));
        lbl.setForeground(new Color(150, 100, 0));
        return lbl;
    }
}
