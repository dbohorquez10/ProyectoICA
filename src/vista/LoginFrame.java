package vista;

import controlador.AuthController;
import modelo.entidades.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Ventana de inicio de sesión.
 */
public class LoginFrame extends JFrame {

    private final JTextField txtUser = new JTextField();
    private final JPasswordField txtPass = new JPasswordField();
    private final AuthController authController = new AuthController();

    public LoginFrame() {
        UIStyle.applyGlobalTheme();
        setTitle("ICA-FITO · Iniciar sesión");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(550, 420);                     // <<< MÁS GRANDE
        setLocationRelativeTo(null);

        // Añadir logo global arriba a la izquierda
        UIStyle.applyLogo(this);

        initUI();
    }

    private void initUI() {

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(16, 16, 16, 16));

        // ====================== LOGO CENTRADO ======================
        JLabel lblLogo = new JLabel();
        lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
        lblLogo.setPreferredSize(new Dimension(150, 150));
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/modelo/logo_ica.png"));
            Image scaled = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            lblLogo.setIcon(new ImageIcon(scaled));
        } catch (Exception e) {
            lblLogo.setText("ICA-FITO");
        }

        // ====================== HEADER ======================
        JPanel header = UIStyle.sectionHeader(
                "Bienvenido a ICA-FITO",
                "Inicia sesión para acceder al sistema"
        );

        // ====================== FORM ======================
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(20, 40, 20, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblUser = new JLabel("Usuario");
        JLabel lblPass = new JLabel("Contraseña");

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        form.add(lblUser, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        form.add(txtUser, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        form.add(lblPass, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        form.add(txtPass, gbc);

        // ====================== BOTÓN LOGIN ======================
        JButton btnLogin = UIStyle.primaryButton("Ingresar");
        btnLogin.setPreferredSize(new Dimension(140, 40));
        btnLogin.addActionListener(e -> doLogin());

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        form.add(btnLogin, gbc);

        // ====================== FOOTER (Registro) ======================
        JPanel footer = new JPanel(new GridBagLayout());
        GridBagConstraints g2 = new GridBagConstraints();
        g2.insets = new Insets(4, 4, 4, 4);

        JLabel lblNuevo = new JLabel("¿No tienes cuenta?");
        JButton btnRegistroProd = new JButton("Registrar productor");
        JButton btnRegistroTec = new JButton("Registrar técnico");

        // estilo tipo "link"
        for (JButton b : new JButton[]{btnRegistroProd, btnRegistroTec}) {
            b.setFocusPainted(false);
            b.setContentAreaFilled(false);
            b.setBorderPainted(false);
            b.setForeground(UIStyle.GREEN.darker());
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        btnRegistroProd.addActionListener(e -> abrirRegistroProductor());
        btnRegistroTec.addActionListener(e -> abrirRegistroTecnico());

        g2.gridx = 0; g2.gridy = 0;
        footer.add(lblNuevo, g2);

        g2.gridy = 1;
        footer.add(btnRegistroProd, g2);

        g2.gridy = 2;
        footer.add(btnRegistroTec, g2);

        // ====================== ARMAR TODO ======================
        root.add(lblLogo, BorderLayout.WEST);
        root.add(header, BorderLayout.NORTH);
        root.add(form, BorderLayout.CENTER);
        root.add(footer, BorderLayout.SOUTH);

        setContentPane(root);
    }

    // ====================== LOGIN ======================
    private void doLogin() {
        String username = txtUser.getText().trim();
        String password = new String(txtPass.getPassword()).trim();

        try {
            Usuario u = authController.login(username, password);

            JOptionPane.showMessageDialog(this,
                    "Bienvenido " + u.getNombre(),
                    "Acceso permitido",
                    JOptionPane.INFORMATION_MESSAGE);

            MainFrame mf = new MainFrame(u);
            mf.setVisible(true);
            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Error de autenticación",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ====================== REGISTROS ======================
    private void abrirRegistroProductor() {
        new RegistroProductorDialog(this).setVisible(true);
    }

    private void abrirRegistroTecnico() {
        new RegistroTecnicoDialog(this).setVisible(true);
    }

    // ====================== MAIN ======================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
