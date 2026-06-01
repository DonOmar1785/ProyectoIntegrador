package vista;

import conexion.Conexion;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class PanelReportes extends JPanel {

    // Categorías del panel izquierdo
    private static final String[] CATEGORIAS = {
        "Asesores",
        "Tutores",
        "Estudiantes",
        "Prácticas",
        "Instituciones",
        "Resumen por Programa"
    };

    // Para cada categoría: título, descripción, SQL, columnas
    private static final String[][][] REPORTES_POR_CATEGORIA = {
        // ── ASESORES ──
        {
            {
                "Asesor → Institución y Estudiantes",
                "Todo lo vinculado a cada asesor: institución donde trabaja y estudiantes que observa.",
                "SELECT a.IdAsesor, a.Nombre || ' ' || a.Apellido AS Asesor, " +
                "a.Correo, i.Nombre AS Institucion, i.Tipo AS TipoInstitucion, " +
                "e.Nombre || ' ' || e.Apellido AS Estudiante, " +
                "pr.Nombre AS ProgramaEstudiante " +
                "FROM AsesorPedagogico a " +
                "JOIN Institucion i ON a.IdInstitucion = i.IdInstitucion " +
                "LEFT JOIN AsesorEstudiante ae ON a.IdAsesor = ae.IdAsesor " +
                "LEFT JOIN Estudiante e ON ae.IdEstudiante = e.IdEstudiante " +
                "LEFT JOIN Programa pr ON e.IdPrograma = pr.IdPrograma " +
                "ORDER BY a.Apellido, e.Apellido",
                "ID Asesor,Asesor,Correo,Institución,Tipo Institución,Estudiante Observado,Programa"
            }
        },
        // ── TUTORES ──
        {
            {
                "Tutor → Programa y Prácticas asignadas",
                "Todo lo vinculado a cada tutor: programa al que pertenece y prácticas que tiene asignadas.",
                "SELECT t.IdTutor, t.Nombre || ' ' || t.Apellido AS Tutor, " +
                "t.Correo, pr.Nombre AS Programa, pr.Facultad, " +
                "p.Nombre AS Practica, p.Semestre, p.Estado AS EstadoPractica " +
                "FROM Tutor t " +
                "JOIN Programa pr ON t.IdPrograma = pr.IdPrograma " +
                "LEFT JOIN TutorPractica tp ON t.IdTutor = tp.IdTutor " +
                "LEFT JOIN Practica p ON tp.IdPractica = p.IdPractica " +
                "ORDER BY t.Apellido, p.Semestre",
                "ID Tutor,Tutor,Correo,Programa,Facultad,Práctica Asignada,Semestre,Estado Práctica"
            }
        },
        // ── ESTUDIANTES ──
        {
            {
                "Estudiante → Práctica, Institución y Asesor",
                "Vista completa por estudiante: programa, práctica asignada, institución y asesor que lo observa.",
                "SELECT e.IdEstudiante, e.Nombre || ' ' || e.Apellido AS Estudiante, " +
                "e.Correo, pr.Nombre AS Programa, " +
                "p.Nombre AS Practica, p.Semestre, " +
                "inst.Nombre AS Institucion, " +
                "a.Nombre || ' ' || a.Apellido AS Asesor " +
                "FROM Estudiante e " +
                "JOIN Programa pr ON e.IdPrograma = pr.IdPrograma " +
                "LEFT JOIN EstudiantePractica ep ON e.IdEstudiante = ep.IdEstudiante " +
                "LEFT JOIN Practica p ON ep.IdPractica = p.IdPractica " +
                "LEFT JOIN EstudianteInstitucion ei ON e.IdEstudiante = ei.IdEstudiante " +
                "LEFT JOIN Institucion inst ON ei.IdInstitucion = inst.IdInstitucion " +
                "LEFT JOIN AsesorEstudiante ase ON e.IdEstudiante = ase.IdEstudiante " +
                "LEFT JOIN AsesorPedagogico a ON ase.IdAsesor = a.IdAsesor " +
                "ORDER BY e.Apellido",
                "ID Est.,Estudiante,Correo,Programa,Práctica,Semestre,Institución,Asesor"
            }
        },
        // ── PRÁCTICAS ──
        {
            {
                "Práctica → Tutores, Estudiantes y Tipo",
                "Vista completa por práctica: tipo, programa, tutores asignados y cantidad de estudiantes.",
                "SELECT p.IdPractica, p.Nombre AS Practica, p.Semestre, p.Estado, " +
                "pr.Nombre AS Programa, tp.Nombre AS TipoPractica, " +
                "t.Nombre || ' ' || t.Apellido AS Tutor, " +
                "COUNT(DISTINCT ep.IdEstudiante) AS CantEstudiantes " +
                "FROM Practica p " +
                "JOIN Programa pr ON p.IdPrograma = pr.IdPrograma " +
                "JOIN TipoPractica tp ON p.IdTipoPrac = tp.IdTipoPrac " +
                "LEFT JOIN TutorPractica tpr ON p.IdPractica = tpr.IdPractica " +
                "LEFT JOIN Tutor t ON tpr.IdTutor = t.IdTutor " +
                "LEFT JOIN EstudiantePractica ep ON p.IdPractica = ep.IdPractica " +
                "GROUP BY p.IdPractica, p.Nombre, p.Semestre, p.Estado, pr.Nombre, tp.Nombre, t.Nombre, t.Apellido " +
                "ORDER BY p.Semestre, p.IdPractica",
                "ID Práctica,Práctica,Semestre,Estado,Programa,Tipo,Tutor Asignado,Cant. Estudiantes"
            }
        },
        // ── INSTITUCIONES ──
        {
            {
                "Institución → Asesores y Estudiantes",
                "Vista completa por institución: asesores vinculados y estudiantes que realizan prácticas allí.",
                "SELECT i.IdInstitucion, i.Nombre AS Institucion, i.Tipo, i.Direccion, " +
                "a.Nombre || ' ' || a.Apellido AS Asesor, " +
                "e.Nombre || ' ' || e.Apellido AS Estudiante " +
                "FROM Institucion i " +
                "LEFT JOIN AsesorPedagogico a ON i.IdInstitucion = a.IdInstitucion " +
                "LEFT JOIN EstudianteInstitucion ei ON i.IdInstitucion = ei.IdInstitucion " +
                "LEFT JOIN Estudiante e ON ei.IdEstudiante = e.IdEstudiante " +
                "ORDER BY i.Nombre, a.Apellido, e.Apellido",
                "ID Inst.,Institución,Tipo,Dirección,Asesor,Estudiante"
            }
        },
        // ── RESUMEN POR PROGRAMA ──
        {
            {
                "Resumen general por programa",
                "Totales consolidados: prácticas, estudiantes y tutores por programa académico.",
                "SELECT pr.IdPrograma, pr.Nombre AS Programa, pr.Facultad, pr.Estado, " +
                "COUNT(DISTINCT p.IdPractica) AS TotalPracticas, " +
                "COUNT(DISTINCT e.IdEstudiante) AS TotalEstudiantes, " +
                "COUNT(DISTINCT t.IdTutor) AS TotalTutores " +
                "FROM Programa pr " +
                "LEFT JOIN Practica p ON pr.IdPrograma = p.IdPrograma " +
                "LEFT JOIN Estudiante e ON pr.IdPrograma = e.IdPrograma " +
                "LEFT JOIN Tutor t ON pr.IdPrograma = t.IdPrograma " +
                "GROUP BY pr.IdPrograma, pr.Nombre, pr.Facultad, pr.Estado " +
                "ORDER BY pr.Nombre",
                "ID Programa,Programa,Facultad,Estado,Total Prácticas,Total Estudiantes,Total Tutores"
            }
        }
    };

    private DefaultTableModel modeloTabla;
    private JTable tabla;
    private JLabel lblTituloReporte;
    private JLabel lblDescripcion;
    private JLabel lblContador;
    private JList<String> listaCategorias;

    public PanelReportes() {
        setLayout(new BorderLayout(0, 0));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // ── Título principal ──
        JLabel lblTitulo = new JLabel("Reportes del Sistema");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(lblTitulo, BorderLayout.NORTH);

        // ── Panel izquierdo: lista de categorías ──
        listaCategorias = new JList<>(CATEGORIAS);
        listaCategorias.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaCategorias.setFont(new Font("SansSerif", Font.PLAIN, 13));
        listaCategorias.setFixedCellHeight(38);
        listaCategorias.setBackground(new Color(240, 244, 250));
        listaCategorias.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        // Renderer con color de selección personalizado
        listaCategorias.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);
                lbl.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
                if (isSelected) {
                    lbl.setBackground(new Color(33, 80, 160));
                    lbl.setForeground(Color.BLACK);
                    lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));
                } else {
                    lbl.setBackground(new Color(240, 244, 250));
                    lbl.setForeground(new Color(40, 40, 40));
                    lbl.setFont(lbl.getFont().deriveFont(Font.PLAIN));
                }
                return lbl;
            }
        });

        JScrollPane scrollLista = new JScrollPane(listaCategorias);
        scrollLista.setPreferredSize(new Dimension(195, 0));
        scrollLista.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180, 200, 230)),
                "Categoría", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 11), new Color(33, 80, 160)));

        // ── Panel derecho: descripción + tabla ──
        JPanel panelDerecho = new JPanel(new BorderLayout(0, 6));

        // Info del reporte activo
        lblTituloReporte = new JLabel(" ");
        lblTituloReporte.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblTituloReporte.setForeground(new Color(33, 80, 160));

        lblDescripcion = new JLabel(" ");
        lblDescripcion.setFont(new Font("SansSerif", Font.ITALIC, 11));
        lblDescripcion.setForeground(new Color(90, 90, 90));

        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 210, 230)),
                BorderFactory.createEmptyBorder(4, 6, 6, 6)));
        infoPanel.setBackground(new Color(248, 250, 255));
        infoPanel.add(lblTituloReporte);
        infoPanel.add(lblDescripcion);

        // Tabla
        modeloTabla = new DefaultTableModel() {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(modeloTabla);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 11));
        tabla.getTableHeader().setBackground(new Color(33, 80, 160));
        tabla.getTableHeader().setForeground(Color.BLACK);
        tabla.setRowHeight(22);
        tabla.setGridColor(new Color(220, 225, 235));
        tabla.setIntercellSpacing(new Dimension(6, 2));

        JScrollPane scrollTabla = new JScrollPane(tabla);
        scrollTabla.setBorder(BorderFactory.createLineBorder(new Color(200, 210, 230)));

        lblContador = new JLabel("  Seleccioná una categoría para ver el reporte.");
        lblContador.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblContador.setForeground(new Color(100, 100, 100));
        lblContador.setBorder(BorderFactory.createEmptyBorder(3, 4, 0, 0));

        panelDerecho.add(infoPanel, BorderLayout.NORTH);
        panelDerecho.add(scrollTabla, BorderLayout.CENTER);
        panelDerecho.add(lblContador, BorderLayout.SOUTH);

        // ── Split principal ──
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollLista, panelDerecho);
        split.setDividerLocation(200);
        split.setDividerSize(5);
        split.setBorder(null);
        add(split, BorderLayout.CENTER);

        // ── Listener: click en categoría ejecuta reporte de inmediato ──
        listaCategorias.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int idx = listaCategorias.getSelectedIndex();
                if (idx >= 0) ejecutarCategoria(idx);
            }
        });
    }

    private void ejecutarCategoria(int categoriaIdx) {
        // REPORTES_POR_CATEGORIA[categoria][reporte][campo]
        // campo: 0=título, 1=descripción, 2=SQL, 3=columnas CSV
        String[] reporte = REPORTES_POR_CATEGORIA[categoriaIdx][0];
        lblTituloReporte.setText(reporte[0]);
        lblDescripcion.setText("  " + reporte[1]);

        String[] columnas = reporte[3].split(",");
        modeloTabla.setRowCount(0);
        modeloTabla.setColumnCount(0);
        for (String col : columnas) modeloTabla.addColumn(col);

        try (Connection con = Conexion.conectar();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(reporte[2])) {
            int count = 0;
            int numCols = rs.getMetaData().getColumnCount();
            while (rs.next()) {
                Object[] fila = new Object[numCols];
                for (int i = 0; i < numCols; i++) fila[i] = rs.getObject(i + 1);
                modeloTabla.addRow(fila);
                count++;
            }
            lblContador.setText("  " + count + " registro(s) encontrado(s).");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error al generar reporte:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            lblContador.setText("  Error al ejecutar la consulta.");
        }
    }
}