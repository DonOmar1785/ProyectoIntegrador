package vista;

import modelo.UsuarioLogin;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private JPanel panelContenido;
    private CardLayout cardLayout;
    private UsuarioLogin usuarioActual;

    public MainFrame(UsuarioLogin usuario) {
        this.usuarioActual = usuario;
        setTitle("Software de Gestión de Prácticas Académicas");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Barra superior
        JPanel barraTop = new JPanel(new BorderLayout());
        barraTop.setBackground(new Color(22, 38, 60));
        barraTop.setPreferredSize(new Dimension(0, 40));
        JLabel lblUsuario = new JLabel("  Usuario: " + usuario.getNombre() + " " + usuario.getApellido()
                + "  |  Rol: " + usuario.getRol());
        lblUsuario.setForeground(new Color(180, 200, 230));
        lblUsuario.setFont(new Font("SansSerif", Font.PLAIN, 12));
        JButton btnSalir = new JButton("Cerrar sesión");
        btnSalir.setForeground(Color.WHITE);
        btnSalir.setBackground(new Color(160, 40, 40));
        btnSalir.setBorderPainted(false);
        btnSalir.setFocusPainted(false);
        btnSalir.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSalir.addActionListener(e -> { dispose(); new LoginFrame().setVisible(true); });
        barraTop.add(lblUsuario, BorderLayout.WEST);
        barraTop.add(btnSalir, BorderLayout.EAST);
        add(barraTop, BorderLayout.NORTH);

        cardLayout = new CardLayout();
        panelContenido = new JPanel(cardLayout);

        JPanel bienvenida = new JPanel(new GridBagLayout());
        JLabel lblBien = new JLabel("Seleccioná un módulo del menú");
        lblBien.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblBien.setForeground(Color.GRAY);
        bienvenida.add(lblBien);
        panelContenido.add(bienvenida, "Inicio");

        // Sidebar y módulos según rol
        JPanel sidebar = buildSidebar(usuario.getRol());
        add(sidebar, BorderLayout.WEST);
        add(panelContenido, BorderLayout.CENTER);

        JLabel statusBar = new JLabel("  Software de Prácticas Académicas - UDI");
        statusBar.setFont(new Font("SansSerif", Font.PLAIN, 11));
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        statusBar.setPreferredSize(new Dimension(0, 22));
        add(statusBar, BorderLayout.SOUTH);
    }

    private JPanel buildSidebar(String rol) {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(33, 53, 85));
        sidebar.setPreferredSize(new Dimension(185, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("GESTIÓN");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        titulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        sidebar.add(titulo);

        switch (rol) {
            case "ADMIN":
                agregarBotones(sidebar, new String[]{
                    "Usuarios", "Programas", "Prácticas",
                    "Estudiantes", "Tutores", "Asesores", "Instituciones", "Asignaciones"
                }, false);
                sidebar.add(Box.createRigidArea(new Dimension(0, 12)));
                sidebar.add(buildSeparador());
                sidebar.add(Box.createRigidArea(new Dimension(0, 12)));
                sidebar.add(crearBoton("Reportes", true));
                break;

            case "TUTOR":
                sidebar.add(crearBotonVista("Mi Panel", true));
                break;

            case "ASESOR":
                sidebar.add(crearBotonVista("Mi Panel", true));
                break;

            case "ESTUDIANTE":
                sidebar.add(crearBotonVista("Mi Panel", true));
                break;
        }

        return sidebar;
    }

    private void agregarBotones(JPanel sidebar, String[] modulos, boolean destacado) {
        for (String m : modulos) {
            sidebar.add(crearBoton(m, destacado));
            sidebar.add(Box.createRigidArea(new Dimension(0, 4)));
        }
    }

    private JSeparator buildSeparador() {
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(165, 1));
        sep.setForeground(new Color(80, 110, 150));
        return sep;
    }

    private JButton crearBotonVista(String texto, boolean destacado) {
        JButton btn = crearBoton(texto, destacado);
        btn.addActionListener(null); // quitar el listener anterior
        // Reasignar correctamente
        for (java.awt.event.ActionListener al : btn.getActionListeners())
            btn.removeActionListener(al);
        btn.addActionListener(e -> mostrarPanelVista());
        return btn;
    }

    private void mostrarPanelVista() {
        String nombre = "MiPanel";
        for (java.awt.Component c : panelContenido.getComponents()) {
            if (nombre.equals(c.getName())) {
                cardLayout.show(panelContenido, nombre);
                return;
            }
        }
        JPanel panel = crearPanelVista(usuarioActual.getRol());
        if (panel != null) {
            panel.setName(nombre);
            panelContenido.add(panel, nombre);
        }
        cardLayout.show(panelContenido, nombre);
    }

    private JPanel crearPanelVista(String rol) {
        switch (rol) {
            case "ESTUDIANTE": return new PanelVistaEstudiante(usuarioActual);
            case "TUTOR":      return new PanelVistaTutor(usuarioActual);
            case "ASESOR":     return new PanelVistaAsesor(usuarioActual);
            default:           return null;
        }
    }

    private JButton crearBoton(String texto, boolean destacado) {
        JButton btn = new JButton(texto);
        btn.setMaximumSize(new Dimension(165, 38));
        btn.setPreferredSize(new Dimension(165, 38));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setBackground(destacado ? new Color(33, 100, 160) : new Color(52, 80, 120));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setFont(new Font("SansSerif", destacado ? Font.BOLD : Font.PLAIN, 13));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> mostrarPanel(texto));
        return btn;
    }

    private void mostrarPanel(String nombre) {
        // Siempre crear un panel fresco para que los datos estén actualizados
        JPanel panel = crearPanel(nombre);
        if (panel != null) {
            panel.setName(nombre);
            // Remover panel anterior si existe
            for (java.awt.Component c : panelContenido.getComponents()) {
                if (nombre.equals(c.getName())) {
                    panelContenido.remove(c);
                    break;
                }
            }
            panelContenido.add(panel, nombre);
        }
        cardLayout.show(panelContenido, nombre);
    }

    private JPanel crearPanel(String nombre) {
        switch (nombre) {
            case "Usuarios":      return new PanelGestionUsuarios();
            case "Programas":     return new PanelProgramas();
            case "Prácticas":     return new PanelPracticas();
            case "Estudiantes":   return new PanelUsuarios();
            case "Tutores":       return new PanelTutores();
            case "Asesores":      return new PanelAsesores();
            case "Instituciones": return new PanelInstituciones();
            case "Asignaciones":  return new PanelAsignaciones();
            case "Reportes":      return new PanelReportes();
            default:              return null;
        }
    }
}