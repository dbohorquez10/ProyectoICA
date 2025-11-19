package vista.lote;

import controlador.LoteController;
import modelo.RolUsuario;
import modelo.Sesion;
import modelo.dao.CultivoDAO;
import modelo.dao.ProductorDAO;
import modelo.entidades.Cultivo;
import modelo.entidades.Lote;
import modelo.entidades.Productor;
import vista.UIStyle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.io.File;

import modelo.reportes.ReporteUtil;

/**
 * Gestión de lotes:
 *  - ADMIN: puede ver y gestionar todos los lotes.
 *  - PRODUCTOR: ve y gestiona solo sus lotes (su productor se fija automáticamente).
 *
 * Campos:
 *  - Cultivo: combo con nombre del cultivo.
 *  - Productor: combo con cédula (y nombre) del productor.
 */
public class LoteForm extends JDialog {

    // Combos en vez de texto
    private final JComboBox<CultivoItem>     cbCultivo    = new JComboBox<>();
    private final JComboBox<ProductorItem>   cbProductor  = new JComboBox<>();

    // Botón reporte
    private JButton btnExportarPdf;

    // Campos del formulario
    private final JTextField txtArea         = new JTextField();
    private final JTextField txtNumeroLote   = new JTextField();
    private final JTextField txtFechaSiembra = new JTextField(); // YYYY-MM-DD

    // Tabla
    private final JTable tblLotes = new JTable();
    private DefaultTableModel tableModel;

    private final LoteController controller = new LoteController();

    // Contexto de sesión
    private boolean   modoProductor      = false;
    private Productor productorActual    = null;   // productor logueado (si aplica)
    private String    idLoteSeleccionado = null;

    public LoteForm(Frame owner) {
        super(owner, "Gestión de lotes", true);
        setSize(900, 540);
        setLocationRelativeTo(owner);

        detectarContexto();
        initUI();
        cargarCombos();
        cargarTabla();
    }

    /**
     * Si el usuario logueado es PRODUCTOR, buscamos su fila en ADMINICA.PRODUCTOR
     * y la guardamos en productorActual para fijarla luego en el combo.
     */
    private void detectarContexto() {
        if (Sesion.getRolActual() == RolUsuario.PRODUCTOR) {
            modoProductor = true;
            try {
                ProductorDAO prodDAO = new ProductorDAO();
                productorActual = prodDAO.buscarPorUsuarioId(Sesion.getUsuarioActual().getId());
            } catch (Exception ignored) {
                productorActual = null;
            }
        }
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(new EmptyBorder(12, 12, 12, 12));

        String sub;
        if (Sesion.getRolActual() == RolUsuario.PRODUCTOR) {
            sub = "Registra y administra los lotes asociados a tus cultivos.";
        } else {
            sub = "Gestiona y consulta lotes de los productores.";
        }
        JPanel header = UIStyle.sectionHeader("Registro de lotes", sub);

        // ================= FORMULARIO =================
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(new EmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.3;

        UIStyle.styleTextField(txtArea);
        UIStyle.styleTextField(txtNumeroLote);
        UIStyle.styleTextField(txtFechaSiembra);

        int row = 0;

        // Cultivo (lista)
        gbc.gridx = 0; gbc.gridy = row;
        form.add(UIStyle.formLabel("Cultivo"), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 0.7;
        form.add(cbCultivo, gbc);

        // Productor (lista por cédula)
        row++;
        gbc.weightx = 0.3;
        gbc.gridx = 0; gbc.gridy = row;
        form.add(UIStyle.formLabel("Productor (cédula)"), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 0.7;
        form.add(cbProductor, gbc);

        // Área
        row++;
        gbc.weightx = 0.3;
        gbc.gridx = 0; gbc.gridy = row;
        form.add(UIStyle.formLabel("Área (ha)"), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 0.7;
        form.add(txtArea, gbc);

        // Número lote
        row++;
        gbc.weightx = 0.3;
        gbc.gridx = 0; gbc.gridy = row;
        form.add(UIStyle.formLabel("Número lote"), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 0.7;
        form.add(txtNumeroLote, gbc);

        // Fecha siembra
        row++;
        gbc.weightx = 0.3;
        gbc.gridx = 0; gbc.gridy = row;
        form.add(UIStyle.formLabel("Fecha siembra (YYYY-MM-DD)"), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 0.7;
        form.add(txtFechaSiembra, gbc);

        JPanel formCard = UIStyle.wrapInCard(form);

        // ================= TABLA =================
        String[] columnas = {
                "ID", "Cultivo", "Productor", "Área (ha)",
                "Número lote", "F. siembra", "F. eliminación"
        };
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tblLotes.setModel(tableModel);
        tblLotes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblLotes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) onTablaSeleccion();
        });

        JScrollPane scrollTabla = new JScrollPane(tblLotes);
        scrollTabla.setBorder(new EmptyBorder(10, 0, 0, 0));

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.add(formCard, BorderLayout.NORTH);
        center.add(scrollTabla, BorderLayout.CENTER);

        // ================= BOTONES =================
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setOpaque(false);

        // Reporte
        btnExportarPdf = UIStyle.primaryButton("Exportar informe PDF");
        btnExportarPdf.addActionListener(e -> exportarReporteProductor());

        JButton btnNuevo = UIStyle.ghostButton("Nuevo");
        btnNuevo.addActionListener(e -> limpiarFormulario());

        JButton btnEliminar = UIStyle.dangerButton("Eliminar");
        btnEliminar.addActionListener(e -> eliminarSeleccionado());

        JButton btnCerrar = UIStyle.backButton();
        btnCerrar.setText("Cerrar");
        btnCerrar.addActionListener(e -> dispose());

        JButton btnGuardar = UIStyle.primaryButton("Guardar lote");
        btnGuardar.addActionListener(e -> guardar());

        footer.add(btnExportarPdf);
        footer.add(btnNuevo);
        footer.add(btnEliminar);
        footer.add(btnCerrar);
        footer.add(btnGuardar);

        root.add(header, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);
        root.add(footer, BorderLayout.SOUTH);

        setContentPane(root);
    }

    // ================= CARGA DE COMBOS =================

    private void cargarCombos() {
        try {
            // ----- Cultivos (todos los roles los pueden ver) -----
            cbCultivo.removeAllItems();
            CultivoDAO cultivoDAO = new CultivoDAO();
            List<Cultivo> cultivos = cultivoDAO.listarCultivos();
            for (Cultivo c : cultivos) {
                cbCultivo.addItem(new CultivoItem(
                        c.getIdCultivo(),
                        c.getNombreComun()
                ));
            }

            // ----- Productores -----
            cbProductor.removeAllItems();
            ProductorDAO prodDAO = new ProductorDAO();

            if (modoProductor) {
                // Solo el productor logueado
                if (productorActual != null) {
                    cbProductor.addItem(new ProductorItem(
                            productorActual.getIdProductor(),
                            productorActual.getIdentificacion(),
                            productorActual.getNombreCompleto()
                    ));
                    cbProductor.setSelectedIndex(0);
                    cbProductor.setEnabled(false); // bloqueado, no puede cambiarse
                } else {
                    cbProductor.setEnabled(false);
                }
            } else {
                // ADMIN (y futuro TECNICO): todos los productores
                List<Productor> productores = prodDAO.listarProductores();
                for (Productor p : productores) {
                    cbProductor.addItem(new ProductorItem(
                            p.getIdProductor(),          // PROD-#
                            p.getIdentificacion(),       // cédula
                            p.getNombreCompleto()
                    ));
                }
                cbProductor.setEnabled(true);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudieron cargar cultivos/productores:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ================= CARGA DE TABLA =================

    private void cargarTabla() {
        try {
            List<Lote> lista = controller.listarLotesParaUsuarioActual();
            tableModel.setRowCount(0);
            for (Lote l : lista) {
                tableModel.addRow(new Object[] {
                        l.getIdLote(),
                        l.getIdCultivo(),
                        l.getIdProductor(),
                        l.getAreaHectareas(),
                        l.getNumeroLote(),
                        l.getFechaSiembra()     != null ? l.getFechaSiembra().toString()     : "",
                        l.getFechaEliminacion() != null ? l.getFechaEliminacion().toString() : ""
                });
            }
            limpiarFormulario();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo cargar la lista de lotes:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ================= SINCRONIZAR FORM ↔ TABLA =================

    private void onTablaSeleccion() {
        int row = tblLotes.getSelectedRow();
        if (row == -1) return;

        idLoteSeleccionado = (String) tableModel.getValueAt(row, 0);
        String idCultivo   = (String) tableModel.getValueAt(row, 1);
        String idProductor = (String) tableModel.getValueAt(row, 2);

        seleccionarCultivoPorId(idCultivo);
        seleccionarProductorPorId(idProductor);

        txtArea.setText(String.valueOf(tableModel.getValueAt(row, 3)));
        txtNumeroLote.setText(String.valueOf(tableModel.getValueAt(row, 4)));
        txtFechaSiembra.setText(String.valueOf(tableModel.getValueAt(row, 5)));
    }

    private void limpiarFormulario() {
        idLoteSeleccionado = null;

        if (cbCultivo.getItemCount() > 0) {
            cbCultivo.setSelectedIndex(0);
        }

        if (modoProductor) {
            cbProductor.setEnabled(false);
        } else {
            if (cbProductor.getItemCount() > 0) {
                cbProductor.setSelectedIndex(0);
            }
            cbProductor.setEnabled(true);
        }

        txtArea.setText("");
        txtNumeroLote.setText("");
        txtFechaSiembra.setText("");
        tblLotes.clearSelection();
    }

    private void seleccionarCultivoPorId(String idCultivo) {
        if (idCultivo == null) return;
        for (int i = 0; i < cbCultivo.getItemCount(); i++) {
            CultivoItem it = cbCultivo.getItemAt(i);
            if (idCultivo.equals(it.idCultivo)) {
                cbCultivo.setSelectedIndex(i);
                break;
            }
        }
    }

    private void seleccionarProductorPorId(String idProductor) {
        if (idProductor == null) return;
        for (int i = 0; i < cbProductor.getItemCount(); i++) {
            ProductorItem it = cbProductor.getItemAt(i);
            if (idProductor.equals(it.idProductor)) {
                cbProductor.setSelectedIndex(i);
                break;
            }
        }
    }

    // ================= ACCIONES: GUARDAR / ELIMINAR =================

    private void guardar() {
        try {
            CultivoItem cultivoSel     = (CultivoItem) cbCultivo.getSelectedItem();
            ProductorItem productorSel = (ProductorItem) cbProductor.getSelectedItem();

            if (cultivoSel == null || productorSel == null) {
                throw new Exception("Debe seleccionar un cultivo y un productor.");
            }

            if (txtArea.getText().trim().isEmpty()
                    || txtNumeroLote.getText().trim().isEmpty()
                    || txtFechaSiembra.getText().trim().isEmpty()) {
                throw new Exception("Todos los campos son obligatorios.");
            }

            double area = Double.parseDouble(txtArea.getText().trim());
            LocalDate fechaSiembra = LocalDate.parse(txtFechaSiembra.getText().trim());

            String idCultivo   = cultivoSel.idCultivo;
            String idProductor = productorSel.idProductor; // sigue siendo PROD-#

            if (idLoteSeleccionado == null) {
                controller.registrarLote(
                        idCultivo,
                        idProductor,
                        area,
                        txtNumeroLote.getText().trim(),
                        fechaSiembra
                );
                JOptionPane.showMessageDialog(this, "Lote registrado correctamente.");
            } else {
                Lote l = new Lote();
                l.setIdLote(idLoteSeleccionado);
                l.setIdCultivo(idCultivo);
                l.setIdProductor(idProductor);
                l.setAreaHectareas(area);
                l.setNumeroLote(txtNumeroLote.getText().trim());
                l.setFechaSiembra(fechaSiembra);

                controller.actualizarLote(l);
                JOptionPane.showMessageDialog(this, "Lote actualizado correctamente.");
            }
            cargarTabla();

        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this,
                    "El valor del área debe ser numérico.",
                    "Dato inválido",
                    JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarSeleccionado() {
        int row = tblLotes.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un lote en la tabla.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = (String) tableModel.getValueAt(row, 0);

        int opt = JOptionPane.showConfirmDialog(
                this,
                "¿Eliminar el lote " + id + "?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );
        if (opt != JOptionPane.YES_OPTION) return;

        try {
            controller.eliminarLote(id);
            cargarTabla();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Error al eliminar",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ================= REPORTE PRODUCTOR + LOTES =================

    /**
     * Usa la fila seleccionada de la tabla de lotes para tomar el ID_PRODUCTOR
     * y genera el PDF "Productor + sus lotes".
     */
    private void exportarReporteProductor() {
        int row = tblLotes.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un lote en la tabla (se usará su productor).",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Columna 2 de la tabla = ID_PRODUCTOR (PROD-#)
        String idProductor = (String) tableModel.getValueAt(row, 2);

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar informe de productor y lotes");
        chooser.setSelectedFile(new File("productor_" + idProductor + ".pdf"));

        int result = chooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File destino = chooser.getSelectedFile();
        try {
            ReporteUtil.exportarProductorLotesPdf(idProductor, destino.getAbsolutePath());
            JOptionPane.showMessageDialog(this,
                    "Informe generado correctamente:\n" + destino.getAbsolutePath(),
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al generar el informe:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    // ================= CLASES AUXILIARES PARA LOS COMBOS =================

    private static class CultivoItem {
        final String idCultivo;
        final String nombreComun;

        CultivoItem(String idCultivo, String nombreComun) {
            this.idCultivo = idCultivo;
            this.nombreComun = nombreComun;
        }

        @Override
        public String toString() {
            return nombreComun;
        }
    }

    private static class ProductorItem {
        final String idProductor;  // PROD-#
        final String cedula;       // IDENTIFICACION
        final String nombre;

        ProductorItem(String idProductor, String cedula, String nombre) {
            this.idProductor = idProductor;
            this.cedula = cedula;
            this.nombre = nombre;
        }

        @Override
        public String toString() {
            return cedula + " - " + nombre;
        }
    }
}
