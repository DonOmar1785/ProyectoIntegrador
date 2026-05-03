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
        setTitle("Sistema de Gestión de Prácticas Académicas");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Barra superior con info del usuario
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
        btnSalir.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
        barraTop.add(lblUsuario, BorderLayout.WEST);
        barraTop.add(btnSalir, BorderLayout.EAST);
        add(barraTop, BorderLayout.NORTH);

        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(33, 53, 85));
        sidebar.setPreferredSize(new Dimension(185, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("GESTIÓN");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        titulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        sidebar.add(titulo);

        // CardLayout
        cardLayout = new CardLayout();
        panelContenido = new JPanel(cardLayout);
        panelContenido.add(new PanelGestionUsuarios(), "Usuarios");
        panelContenido.add(new PanelProgramas(), "Programas");
        panelContenido.add(new PanelPracticas(), "Prácticas");
        panelContenido.add(new PanelUsuarios(), "Estudiantes");

        String[] modulos = {"Usuarios", "Programas", "Prácticas", "Estudiantes"};
        for (String modulo : modulos) {
            JButton btn = crearBotonSidebar(modulo);
            sidebar.add(btn);
            sidebar.add(Box.createRigidArea(new Dimension(0, 4)));
        }

        add(sidebar, BorderLayout.WEST);
        add(panelContenido, BorderLayout.CENTER);

        // Barra de estado
        JLabel statusBar = new JLabel("  Sistema de Prácticas Académicas - UDI");
        statusBar.setFont(new Font("SansSerif", Font.PLAIN, 11));
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        statusBar.setPreferredSize(new Dimension(0, 22));
        add(statusBar, BorderLayout.SOUTH);
    }

    private JButton crearBotonSidebar(String texto) {
        JButton btn = new JButton(texto);
        btn.setMaximumSize(new Dimension(165, 38));
        btn.setPreferredSize(new Dimension(165, 38));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setBackground(new Color(52, 80, 120));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> cardLayout.show(panelContenido, texto));
        return btn;
    }
}
