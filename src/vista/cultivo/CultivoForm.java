/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista.cultivo;

import controlador.CultivoController;
import modelo.RolUsuario;
import modelo.Sesion;
import modelo.entidades.Cultivo;
import vista.UIStyle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import modelo.reportes.ReporteUtil;
import java.io.File;

/**
 * Gestión de cultivos.
 *
 * - ADMIN: puede registrar / editar / eliminar cultivos.
 * - PRODUCTOR y TECNICO: SOLO CONSULTAN (tabla en modo lectura).
 */
public class CultivoForm extends JDialog {

    // ===== Modo solo lectura según el rol =====
    private final boolean soloLectura;

    // ===== Campos de formulario (solo ADMIN) =====
    private final JTextField txtNombreCientifico = new JTextField();
    private final JTextField txtNombreComun      = new JTextField();
    private final JComboBox<String> cbCicloCultivo =
            new JComboBox<>(new String[]{"Corto", "Intermedio", "Largo", "Perenne"});

    // ===== Tabla =====
    private final JTable tblCultivos = new JTable();
    private DefaultTableModel tableModel;

    // ID del cultivo seleccionado (para actualizar / eliminar)
    private String idCultivoSeleccionado = null;

    // ===== Controlador =====
    private final CultivoController controller = new CultivoController();

    public CultivoForm(Frame owner) {
        super(owner, "Gestión de cultivos", true);

        // Determinar rol actual
        RolUsuario rol = Sesion.getRolActual();
        soloLectura = (rol != RolUsuario.ADMIN); // sólo ADMIN edita

        setSize(900, 600);
        setLocationRelativeTo(owner);
        initUI();
        cargarTabla();
    }

    // =========================================================
    //  UI
    // =========================================================
    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel header = UIStyle.sectionHeader(
                "Catálogo de cultivos",
                soloLectura
                        ? "Consulta el catálogo de cultivos disponibles."
                        : "Registra y consulta cultivos disponibles."
        );

        // ---------- Formulario (solo para ADMIN) ----------
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
            cbCicloCultivo.setFocusable(false);

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
            form.add(UIStyle.formLabel("Ciclo cultivo"), gbc);
            gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 0.7;
            form.add(cbCicloCultivo, gbc);
            cbCicloCultivo.setSelectedIndex(-1);

            formCard = UIStyle.wrapInCard(form);
        }

        // ---------- Tabla ----------
        String[] columnas = {"ID", "Nombre científico", "Nombre común", "Ciclo"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tblCultivos.setModel(tableModel);
        tblCultivos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblCultivos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) onTablaSeleccion();
        });

        JScrollPane scrollTabla = new JScrollPane(tblCultivos);
        scrollTabla.setBorder(new EmptyBorder(10, 0, 0, 0));

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);

        // Si es ADMIN, mostramos formulario + tabla; si no, solo la tabla
        if (!soloLectura && formCard != null) {
            center.add(formCard, BorderLayout.NORTH);
        }
        center.add(scrollTabla, BorderLayout.CENTER);

        // ---------- Botones ----------
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
footer.setOpaque(false);

// Botón para ver plagas (todos los roles)
JButton btnPlagas = UIStyle.ghostButton("Ver plagas");
btnPlagas.addActionListener(e -> verPlagasDelCultivo());

// Botón Cerrar siempre
JButton btnCerrar = UIStyle.backButton();
btnCerrar.setText("Cerrar");
btnCerrar.addActionListener(e -> dispose());

// Botón reporte (todos los roles)
JButton btnReporte = UIStyle.primaryButton("Exportar reporte PDF");
btnReporte.addActionListener(e -> exportarReporteCultivos());

if (!soloLectura) {
    JButton btnNuevo = UIStyle.ghostButton("Nuevo");
    btnNuevo.addActionListener(e -> limpiarFormulario());

    JButton btnEliminar = UIStyle.dangerButton("Eliminar");
    btnEliminar.addActionListener(e -> eliminarSeleccionado());

    JButton btnGuardar = UIStyle.primaryButton("Guardar cultivo");
    btnGuardar.addActionListener(e -> guardar());

    footer.add(btnPlagas);
    footer.add(btnNuevo);
    footer.add(btnEliminar);
    footer.add(btnCerrar);
    footer.add(btnGuardar);
    footer.add(btnReporte);
} else {
    footer.add(btnPlagas);
    footer.add(btnCerrar);
    footer.add(btnReporte);
}


        root.add(header, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);
        root.add(footer, BorderLayout.SOUTH);

        setContentPane(root);
    }

    // =========================================================
    //  Carga de datos
    // =========================================================
    private void cargarTabla() {
        try {
            List<Cultivo> lista = controller.listarCultivos();
            tableModel.setRowCount(0);

            for (Cultivo c : lista) {
                tableModel.addRow(new Object[]{
                        c.getIdCultivo(),
                        c.getNombreCientifico(),
                        c.getNombreComun(),
                        c.getCicloCultivo()
                });
            }

            limpiarFormulario();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo cargar el catálogo de cultivos:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // =========================================================
    //  Sincronizar FORM ↔ TABLA
    // =========================================================
    private void onTablaSeleccion() {
        int row = tblCultivos.getSelectedRow();
        if (row == -1) return;

        idCultivoSeleccionado = (String) tableModel.getValueAt(row, 0);

        if (!soloLectura) {
            txtNombreCientifico.setText((String) tableModel.getValueAt(row, 1));
            txtNombreComun.setText((String) tableModel.getValueAt(row, 2));

            String ciclo = (String) tableModel.getValueAt(row, 3);
            if (ciclo != null) {
                cbCicloCultivo.setSelectedItem(ciclo);
            } else {
                cbCicloCultivo.setSelectedIndex(-1);
            }
        }
    }

    private void limpiarFormulario() {
        idCultivoSeleccionado = null;
        if (!soloLectura) {
            txtNombreCientifico.setText("");
            txtNombreComun.setText("");
            cbCicloCultivo.setSelectedIndex(-1);
        }
        tblCultivos.clearSelection();
    }

    // =========================================================
    //  Acciones: Guardar / Eliminar (solo ADMIN)
    // =========================================================
    private void guardar() {
        if (soloLectura) return; // seguridad extra

        try {
            String nombreCientifico = txtNombreCientifico.getText().trim();
            String nombreComun      = txtNombreComun.getText().trim();

            String ciclo = null;
            if (cbCicloCultivo.getSelectedItem() != null) {
                ciclo = cbCicloCultivo.getSelectedItem().toString();
            }

            if (idCultivoSeleccionado == null) {
                controller.registrarCultivo(
                        nombreCientifico,
                        nombreComun,
                        ciclo
                );
                JOptionPane.showMessageDialog(this,
                        "Cultivo registrado correctamente.",
                        "Registro exitoso",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                Cultivo c = new Cultivo();
                c.setIdCultivo(idCultivoSeleccionado);
                c.setNombreCientifico(nombreCientifico);
                c.setNombreComun(nombreComun);
                c.setCicloCultivo(ciclo);

                controller.actualizarCultivo(c);

                JOptionPane.showMessageDialog(this,
                        "Cultivo actualizado correctamente.",
                        "Actualización exitosa",
                        JOptionPane.INFORMATION_MESSAGE);
            }

            cargarTabla();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Error al guardar cultivo",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarSeleccionado() {
        if (soloLectura) return; // seguridad extra

        int row = tblCultivos.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un cultivo en la tabla.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = (String) tableModel.getValueAt(row, 0);

        int opt = JOptionPane.showConfirmDialog(
                this,
                "¿Eliminar el cultivo " + id + "?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );
        if (opt != JOptionPane.YES_OPTION) return;

        try {
            controller.eliminarCultivo(id);
            cargarTabla();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Error al eliminar cultivo",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // =========================================================
    //  Ver plagas asociadas (todos los roles)
    // =========================================================
    private void verPlagasDelCultivo() {
        int row = tblCultivos.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Seleccione un cultivo en la tabla.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String idCultivo = (String) tableModel.getValueAt(row, 0);
        String nombre = (String) tableModel.getValueAt(row, 2); // Nombre común

        PlagasPorCultivoDialog dlg =
                new PlagasPorCultivoDialog(this, idCultivo, nombre);
        dlg.setVisible(true);
    }
private void exportarReporteCultivos() {
    JFileChooser chooser = new JFileChooser();
    chooser.setDialogTitle("Guardar reporte de cultivos");
    chooser.setSelectedFile(new File("reporte_cultivos_plagas.pdf"));

    int result = chooser.showSaveDialog(this);
    if (result != JFileChooser.APPROVE_OPTION) {
        return;
    }

    File destino = chooser.getSelectedFile();

    try {
        ReporteUtil.exportarCultivosPdf(destino.getAbsolutePath());
        JOptionPane.showMessageDialog(this,
                "Reporte generado correctamente:\n" + destino.getAbsolutePath(),
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE);
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this,
                "Error al generar el reporte:\n" + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    }
}
}
