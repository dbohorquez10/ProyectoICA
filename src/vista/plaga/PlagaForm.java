package vista.plaga;

import controlador.PlagaController;
import modelo.RolUsuario;
import modelo.Sesion;
import modelo.entidades.Plaga;
import vista.UIStyle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import modelo.reportes.ReporteUtil;
import java.io.File;

/**
 * Gestión de plagas:
 *  - ADMIN: CRUD + acceso a la relación cultivo-plaga.
 *  - TECNICO / PRODUCTOR: solo visualización en tabla.
 */
public class PlagaForm extends JDialog {

    // Campos de formulario (sólo usará ADMIN)
    private final JTextField txtNombreCientifico = new JTextField();
    private final JTextField txtNombreComun      = new JTextField();
    private final JComboBox<String> cbTipoPlaga  = new JComboBox<>(
            new String[]{"INSECTO", "HONGO", "ÁCARO", "BACTERIA", "VIRUS", "NEMATODO", "OTRO"}
    );

    private final JTable tblPlagas = new JTable();
    private DefaultTableModel tableModel;

    private final PlagaController plagaController = new PlagaController();

    private String idPlagaSeleccionada = null;

    public PlagaForm(Frame owner) {
        super(owner, "Catálogo de plagas", true);
        setSize(900, 540);
        setLocationRelativeTo(owner);
        initUI();
        cargarTabla();
    }

    private void initUI() {
        RolUsuario rol = Sesion.getRolActual();
        boolean soloLectura = (rol == RolUsuario.TECNICO || rol == RolUsuario.PRODUCTOR);

        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(new EmptyBorder(12, 12, 12, 12));

        String sub;
        if (rol == RolUsuario.ADMIN) {
            sub = "Administra el catálogo de plagas y su relación con los cultivos.";
        } else {
            sub = "Consulta el catálogo de plagas registradas en el sistema.";
        }

        JPanel header = UIStyle.sectionHeader("Plagas", sub);

        // ===== FORMULARIO (solo admin) =====
        JPanel formCard = null;
        if (!soloLectura) {
            JPanel form = new JPanel(new GridBagLayout());
            form.setOpaque(false);
            form.setBorder(new EmptyBorder(10, 10, 10, 10));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(4, 6, 4, 6);
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 0.3;

            UIStyle.styleTextField(txtNombreCientifico);
            UIStyle.styleTextField(txtNombreComun);

            int row = 0;

            gbc.gridx = 0; gbc.gridy = row;
            form.add(UIStyle.formLabel("Nombre científico"), gbc);
            gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 0.7;
            form.add(txtNombreCientifico, gbc);

            row++;
            gbc.weightx = 0.3;
            gbc.gridx = 0; gbc.gridy = row;
            form.add(UIStyle.formLabel("Nombre común"), gbc);
            gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 0.7;
            form.add(txtNombreComun, gbc);

            row++;
            gbc.weightx = 0.3;
            gbc.gridx = 0; gbc.gridy = row;
            form.add(UIStyle.formLabel("Tipo de plaga"), gbc);
            gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 0.7;
            form.add(cbTipoPlaga, gbc);

            formCard = UIStyle.wrapInCard(form);
        }

        // ===== TABLA =====
        String[] columnas = {"ID", "Nombre común", "Nombre científico", "Tipo"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tblPlagas.setModel(tableModel);
        tblPlagas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblPlagas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) onTablaSeleccion();
        });

        JScrollPane scrollTabla = new JScrollPane(tblPlagas);
        scrollTabla.setBorder(new EmptyBorder(10, 0, 0, 0));

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        if (!soloLectura && formCard != null) {
            center.add(formCard, BorderLayout.NORTH);
            center.add(scrollTabla, BorderLayout.CENTER);
        } else {
            // Solo tabla para técnico/productor
            center.add(scrollTabla, BorderLayout.CENTER);
        }

        // ===== BOTONES =====
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setOpaque(false);

        // NUEVO: botón exportar plagas
        JButton btnExportar = UIStyle.primaryButton("Exportar plagas PDF");
        btnExportar.addActionListener(e -> exportarPlagasPdf());

        JButton btnCerrar = UIStyle.backButton();
        btnCerrar.setText("Cerrar");
        btnCerrar.addActionListener(e -> dispose());

        if (!soloLectura) {
            JButton btnRelacion = UIStyle.primaryButton("Cultivos afectados...");
            btnRelacion.addActionListener(e -> abrirCultivoPlagaDialog());

            JButton btnNuevo = UIStyle.ghostButton("Nuevo");
            btnNuevo.addActionListener(e -> limpiarFormulario());

            JButton btnEliminar = UIStyle.dangerButton("Eliminar");
            btnEliminar.addActionListener(e -> eliminarSeleccionada());

            JButton btnGuardar = UIStyle.primaryButton("Guardar plaga");
            btnGuardar.addActionListener(e -> guardar());

            footer.add(btnRelacion);
            footer.add(btnExportar);
            footer.add(btnNuevo);
            footer.add(btnEliminar);
            footer.add(btnCerrar);
            footer.add(btnGuardar);
        } else {
            // Técnico / productor: solo ver + exportar + cerrar
            footer.add(btnExportar);
            footer.add(btnCerrar);
        }

        root.add(header, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);
        root.add(footer, BorderLayout.SOUTH);

        setContentPane(root);
    }

    // ================== CARGA DE TABLA ==================

    private void cargarTabla() {
        try {
            List<Plaga> lista = plagaController.listarPlagas();
            tableModel.setRowCount(0);
            for (Plaga p : lista) {
                tableModel.addRow(new Object[]{
                        p.getIdPlaga(),
                        p.getNombreComun(),
                        p.getNombreCientifico(),
                        p.getTipoPlaga()
                });
            }
            limpiarFormulario();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo cargar la lista de plagas:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onTablaSeleccion() {
        int row = tblPlagas.getSelectedRow();
        if (row == -1) return;

        RolUsuario rol = Sesion.getRolActual();
        if (rol != RolUsuario.ADMIN) return;

        idPlagaSeleccionada = (String) tableModel.getValueAt(row, 0);
        txtNombreComun.setText((String) tableModel.getValueAt(row, 1));
        txtNombreCientifico.setText((String) tableModel.getValueAt(row, 2));
        cbTipoPlaga.setSelectedItem(String.valueOf(tableModel.getValueAt(row, 3)));
    }

    private void limpiarFormulario() {
        idPlagaSeleccionada = null;
        txtNombreCientifico.setText("");
        txtNombreComun.setText("");
        cbTipoPlaga.setSelectedIndex(0);
        tblPlagas.clearSelection();
    }

    private void guardar() {
        try {
            String nombreCientifico = txtNombreCientifico.getText().trim();
            String nombreComun = txtNombreComun.getText().trim();
            String tipo = (String) cbTipoPlaga.getSelectedItem();

            if (idPlagaSeleccionada == null) {
                plagaController.registrarPlaga(nombreCientifico, nombreComun, tipo);
                JOptionPane.showMessageDialog(this, "Plaga registrada correctamente.");
            } else {
                Plaga p = new Plaga();
                p.setIdPlaga(idPlagaSeleccionada);
                p.setNombreCientifico(nombreCientifico);
                p.setNombreComun(nombreComun);
                p.setTipoPlaga(tipo);
                plagaController.actualizarPlaga(p);
                JOptionPane.showMessageDialog(this, "Plaga actualizada correctamente.");
            }

            cargarTabla();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarSeleccionada() {
        int row = tblPlagas.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione una plaga en la tabla.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = (String) tableModel.getValueAt(row, 0);

        int opt = JOptionPane.showConfirmDialog(
                this,
                "¿Eliminar la plaga " + id + "?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );
        if (opt != JOptionPane.YES_OPTION) return;

        try {
            plagaController.eliminarPlaga(id);
            cargarTabla();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Error al eliminar",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Abre el diálogo de cultivos afectados para la plaga seleccionada.
     */
    private void abrirCultivoPlagaDialog() {
        if (Sesion.getRolActual() != RolUsuario.ADMIN) return;

        int row = tblPlagas.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Seleccione una plaga en la tabla.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String idPlaga = (String) tableModel.getValueAt(row, 0);
        CultivosPlagaDialog dlg = new CultivosPlagaDialog(this, idPlaga);
        dlg.setVisible(true);
    }

    // ================== REPORTE PLAGAS ==================

    private void exportarPlagasPdf() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar informe de plagas");
        chooser.setSelectedFile(new File("plagas_ica.pdf"));

        int result = chooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) return;

        File destino = chooser.getSelectedFile();
        try {
            ReporteUtil.exportarPlagasPdf(destino.getAbsolutePath());
            JOptionPane.showMessageDialog(this,
                    "Informe de plagas generado correctamente:\n" + destino.getAbsolutePath(),
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al generar el informe de plagas:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
