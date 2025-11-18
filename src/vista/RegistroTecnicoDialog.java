/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista;

import controlador.TecnicoController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Diálogo para el registro de un nuevo técnico ICA.
 */
public class RegistroTecnicoDialog extends JDialog {

    // Campos de cuenta
    private final JTextField txtUsername     = new JTextField();
    private final JPasswordField txtPassword = new JPasswordField();

    // Datos del técnico
    private final JTextField txtNombre         = new JTextField();
    private final JTextField txtIdentificacion = new JTextField();
    private final JTextField txtTarjetaProf    = new JTextField();
    private final JTextField txtDepartamento   = new JTextField();
    private final JTextField txtDireccion      = new JTextField();
    private final JTextField txtTelefono       = new JTextField();
    private final JTextField txtCorreo         = new JTextField();

    private final TecnicoController controller = new TecnicoController();

    public RegistroTecnicoDialog(Frame owner) {
        super(owner, "Registrar técnico ICA", true);
        setSize(520, 520);
        setLocationRelativeTo(owner);
        setResizable(false);
        initUI();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel header = UIStyle.sectionHeader(
                "Registro de técnico ICA",
                "Ingresa los datos de acceso y del técnico."
        );

        // Estilo de campos
        UIStyle.styleTextField(txtUsername);
        UIStyle.stylePasswordField(txtPassword);
        UIStyle.styleTextField(txtNombre);
        UIStyle.styleTextField(txtIdentificacion);
        UIStyle.styleTextField(txtTarjetaProf);
        UIStyle.styleTextField(txtDepartamento);
        UIStyle.styleTextField(txtDireccion);
        UIStyle.styleTextField(txtTelefono);
        UIStyle.styleTextField(txtCorreo);

        // ===== FORMULARIO =====
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(new EmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // Título bloque cuenta
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        form.add(UIStyle.formLabel("Datos de cuenta"), gbc);

        // Usuario
        row++;
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        form.add(UIStyle.formLabel("Usuario"), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1;
        form.add(txtUsername, gbc);

        // Contraseña
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        form.add(UIStyle.formLabel("Contraseña"), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1;
        form.add(txtPassword, gbc);

        // Separador
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        form.add(new JSeparator(), gbc);

        // Título bloque datos del técnico
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        form.add(UIStyle.formLabel("Datos del técnico"), gbc);

        // Nombre completo
        row++;
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        form.add(UIStyle.formLabel("Nombre completo"), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1;
        form.add(txtNombre, gbc);

        // Identificación
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        form.add(UIStyle.formLabel("Identificación"), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1;
        form.add(txtIdentificacion, gbc);

        // Tarjeta profesional
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        form.add(UIStyle.formLabel("Tarjeta profesional"), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1;
        form.add(txtTarjetaProf, gbc);

        // Departamento
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        form.add(UIStyle.formLabel("Departamento"), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1;
        form.add(txtDepartamento, gbc);

        // Dirección
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        form.add(UIStyle.formLabel("Dirección"), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1;
        form.add(txtDireccion, gbc);

        // Teléfono
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        form.add(UIStyle.formLabel("Teléfono"), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1;
        form.add(txtTelefono, gbc);

        // Correo
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        form.add(UIStyle.formLabel("Correo electrónico"), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1;
        form.add(txtCorreo, gbc);

        // Relleno vertical
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.VERTICAL;
        form.add(Box.createVerticalGlue(), gbc);

        JPanel card = UIStyle.wrapInCard(form);

        JScrollPane scroll = new JScrollPane(card);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        // ===== FOOTER =====
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setOpaque(false);

        JButton btnCancelar = UIStyle.backButton();
        btnCancelar.setText("Cancelar");
        btnCancelar.addActionListener(e -> dispose());

        JButton btnGuardar = UIStyle.primaryButton("Registrar técnico");
        btnGuardar.addActionListener(e -> guardar());

        footer.add(btnCancelar);
        footer.add(btnGuardar);

        root.add(header, BorderLayout.NORTH);
        root.add(scroll, BorderLayout.CENTER);
        root.add(footer, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private void guardar() {
        try {
            controller.registrarTecnico(
                    txtUsername.getText(),
                    new String(txtPassword.getPassword()),
                    txtNombre.getText(),
                    txtIdentificacion.getText(),
                    txtTarjetaProf.getText(),
                    txtDepartamento.getText(),
                    txtDireccion.getText(),
                    txtTelefono.getText(),
                    txtCorreo.getText()
            );

            JOptionPane.showMessageDialog(this,
                    "Técnico registrado correctamente.",
                    "Registro exitoso",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Error en registro de técnico",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
