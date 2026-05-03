package vista;

import dao.UsuarioLoginDAO;
import modelo.UsuarioLogin;
import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private JTextField txtCorreo;
    private JPasswordField txtContrasena;
    private UsuarioLoginDAO dao;

    public LoginFrame() {
        dao = new UsuarioLoginDAO();
        setTitle("Gestión de Prácticas Académicas - Iniciar sesión");
        setSize(400, 280);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        // Panel superior con título
        JPanel panelTitulo = new JPanel();
        panelTitulo.setBackground(new Color(33, 53, 85));
        panelTitulo.setPreferredSize(new Dimension(0, 70));
        panelTitulo.setLayout(new GridBagLayout());
        JLabel lblTitulo = new JLabel("Sistema de Gestión de Prácticas");
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 15));
        JLabel lblSub = new JLabel("Universidad de Investigación y Desarrollo");
        lblSub.setForeground(new Color(180, 200, 230));
        lblSub.setFont(new Font("SansSerif", Font.PLAIN, 11));
        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.add(lblTitulo);
        textos.add(lblSub);
        panelTitulo.add(textos);
        add(panelTitulo, BorderLayout.NORTH);

        // Formulario
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 5, 6, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Correo:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        txtCorreo = new JTextField(20);
        formPanel.add(txtCorreo, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Contraseña:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        txtContrasena = new JPasswordField(20);
        formPanel.add(txtContrasena, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Botón ingresar
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        JButton btnIngresar = new JButton("Ingresar");
        btnIngresar.setPreferredSize(new Dimension(120, 32));
        btnIngresar.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnPanel.add(btnIngresar);
        add(btnPanel, BorderLayout.SOUTH);

        // Enter en contraseña también loguea
        txtContrasena.addActionListener(e -> login());
        btnIngresar.addActionListener(e -> login());
    }

    private void login() {
        String correo = txtCorreo.getText().trim();
        String contrasena = new String(txtContrasena.getPassword()).trim();

        if (correo.isEmpty() || contrasena.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingresá correo y contraseña.");
            return;
        }

        UsuarioLogin usuario = dao.login(correo, contrasena);
        if (usuario != null) {
            dispose();
            new MainFrame(usuario).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Correo o contraseña incorrectos.", "Error", JOptionPane.ERROR_MESSAGE);
            txtContrasena.setText("");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new LoginFrame().setVisible(true);
        });
    }
}
