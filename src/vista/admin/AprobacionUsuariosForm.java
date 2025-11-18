/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package vista.admin;

import controlador.ProductorController;
import controlador.TecnicoController;
import modelo.entidades.Productor;
import modelo.entidades.Tecnico;
import vista.UIStyle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AprobacionUsuariosForm extends JDialog {

    private final TecnicoController tecnicoController = new TecnicoController();
    private final ProductorController productorController = new ProductorController();

    private JTable tblTecnicos;
    private JTable tblProductores;
    private DefaultTableModel modeloTec;
    private DefaultTableModel modeloProd;

    // Solo para cambiar el registro ICA del técnico seleccionado
    private final JTextField txtRegistroIca = new JTextField();

    public AprobacionUsuariosForm(Frame owner) {
        super(owner, "Aprobación de usuarios", true);
        setSize(900, 600);
        setLocationRelativeTo(owner);
        initUI();
        cargarTecnicos();
        cargarProductores();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel header = UIStyle.sectionHeader(
                "Aprobación de usuarios",
                "Aprobar técnicos ICA y activar/desactivar productores."
        );

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Técnicos ICA", crearPanelTecnicos());
        tabs.addTab("Productores", crearPanelProductores());

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnCerrar = UIStyle.ghostButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());
        footer.add(btnCerrar);

        root.add(header, BorderLayout.NORTH);
        root.add(tabs, BorderLayout.CENTER);
        root.add(footer, BorderLayout.SOUTH);

        setContentPane(root);
    }

    // =================== PANEL TÉCNICOS ===================

    private JPanel crearPanelTecnicos() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));

        String[] colsTec = {
                "ID Técnico", "Nombre", "Identificación",
                "Tarjeta prof.", "Correo", "Registro ICA", "Estado"
        };
        modeloTec = new DefaultTableModel(colsTec, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tblTecnicos = new JTable(modeloTec);
        tblTecnicos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblTecnicos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) onSeleccionTecnico();
        });

        panel.add(new JScrollPane(tblTecnicos), BorderLayout.CENTER);

        // Form para registro ICA
        JPanel formRegistro = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0;

        UIStyle.styleTextField(txtRegistroIca);

        gbc.gridx = 0; gbc.gridy = 0;
        formRegistro.add(new JLabel("Registro ICA:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        formRegistro.add(txtRegistroIca, gbc);

        // Botones técnicos
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnDesactivar = UIStyle.dangerButton("Desactivar técnico");
        JButton btnActivar = UIStyle.primaryButton("Activar técnico");

        btnDesactivar.addActionListener(e -> cambiarEstadoTecnico(false));
        btnActivar.addActionListener(e -> cambiarEstadoTecnico(true));

        botones.add(btnDesactivar);
        botones.add(btnActivar);

        JPanel south = new JPanel(new BorderLayout());
        south.add(formRegistro, BorderLayout.CENTER);
        south.add(botones, BorderLayout.SOUTH);

        panel.add(south, BorderLayout.SOUTH);
        return panel;
    }

    // =================== PANEL PRODUCTORES ===================

    private JPanel crearPanelProductores() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));

        String[] colsProd = {
                "ID Productor", "Nombre", "Identificación",
                "Correo", "Teléfono", "Estado"
        };
        modeloProd = new DefaultTableModel(colsProd, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tblProductores = new JTable(modeloProd);
        tblProductores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        panel.add(new JScrollPane(tblProductores), BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnDesactivar = UIStyle.dangerButton("Desactivar productor");
        JButton btnActivar = UIStyle.primaryButton("Activar productor");

        btnDesactivar.addActionListener(e -> cambiarEstadoProductor(false));
        btnActivar.addActionListener(e -> cambiarEstadoProductor(true));

        botones.add(btnDesactivar);
        botones.add(btnActivar);

        panel.add(botones, BorderLayout.SOUTH);
        return panel;
    }

    // =================== CARGA DE DATOS ===================

    private void cargarTecnicos() {
        try {
            List<Tecnico> lista = tecnicoController.listarTecnicos();
            modeloTec.setRowCount(0);
            for (Tecnico t : lista) {
                String estado = t.isActivo() ? "ACTIVO" : "INACTIVO";
                modeloTec.addRow(new Object[]{
                        t.getIdTecnico(),
                        t.getNombreCompleto(),
                        t.getIdentificacion(),
                        t.getTarjetaProfesional(),
                        t.getCorreoElectronico(),
                        t.getRegistroIcaId(),
                        estado
                });
            }
            txtRegistroIca.setText("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo cargar la lista de técnicos:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarProductores() {
        try {
            List<Productor> lista = productorController.listarProductores();
            modeloProd.setRowCount(0);
            for (Productor p : lista) {
                String estado = p.isActivo() ? "ACTIVO" : "INACTIVO";
                modeloProd.addRow(new Object[]{
                        p.getIdProductor(),
                        p.getNombreCompleto(),
                        p.getIdentificacion(),
                        p.getCorreoElectronico(),
                        p.getTelefono(),
                        estado
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo cargar la lista de productores:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // =================== SINCRONIZAR SELECCIÓN ===================

    private void onSeleccionTecnico() {
        int row = tblTecnicos.getSelectedRow();
        if (row == -1) {
            txtRegistroIca.setText("");
            return;
        }
        String reg = (String) modeloTec.getValueAt(row, 5); // columna Registro ICA
        txtRegistroIca.setText(reg != null ? reg : "");
    }

    // =================== ACCIONES ===================

    private void cambiarEstadoTecnico(boolean activar) {
        int row = tblTecnicos.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un técnico en la tabla.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String idTecnico = (String) modeloTec.getValueAt(row, 0);
        String registro = txtRegistroIca.getText().trim();

        try {
            // 1. Activar / desactivar (USUARIO.ACTIVO)
            tecnicoController.actualizarActivo(idTecnico, activar);

            // 2. Si hay registro ICA, lo actualizamos también
            if (!registro.isEmpty()) {
                tecnicoController.actualizarRegistroIca(idTecnico, registro);
            }

            cargarTecnicos();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo actualizar el técnico:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cambiarEstadoProductor(boolean activar) {
        int row = tblProductores.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un productor en la tabla.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String idProd = (String) modeloProd.getValueAt(row, 0);

        try {
            productorController.actualizarActivo(idProd, activar);
            cargarProductores();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo actualizar el productor:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
