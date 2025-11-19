package vista.inspeccion;

import controlador.InspeccionController;
import modelo.RolUsuario;
import modelo.Sesion;
import modelo.dao.LoteDAO;
import modelo.dao.ProductorDAO;
import modelo.dao.TecnicoDAO;
import modelo.entidades.Inspeccion;
import modelo.entidades.Lote;
import modelo.entidades.Productor;
import modelo.entidades.Tecnico;
import vista.UIStyle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import modelo.reportes.ReporteUtil;
import java.io.File;

/**
 * Gestión de inspecciones fitosanitarias.
 *
 * - TÉCNICO:
 *      * Solo ve lotes que tiene ASIGNADOS en VISITA.
 *      * Puede crear / actualizar inspecciones.
 *      * NO puede eliminar.
 * - ADMIN:
 *      * Solo consulta.
 *      * Puede elegir técnico y ver los lotes que tiene asignados.
 * - PRODUCTOR:
 *      * Solo consulta inspecciones sobre sus lotes.
 */
public class InspeccionForm extends JDialog {

    // Combos
    private final JComboBox<ComboItem> cbLote    = new JComboBox<>();
    private final JComboBox<ComboItem> cbTecnico = new JComboBox<>();

    // Campo solo lectura para el cultivo del lote
    private final JTextField txtCultivo         = new JTextField();

    // Campos numéricos/texto
    private final JTextField txtPorcentaje       = new JTextField(); // solo lectura
    private final JTextField txtTotalPlantas     = new JTextField();
    private final JTextField txtPlantasAfectadas = new JTextField();
    private final JTextField txtEstadoFeno       = new JTextField();
    private final JTextArea  txtObs              = new JTextArea(3, 20);

    // Tabla
    private final JTable tblInspecciones = new JTable();
    private DefaultTableModel tableModel;

    private final InspeccionController controller = new InspeccionController();

    private RolUsuario rolActual;
    private String idInspeccionSeleccionada = null;
    private String idTecnicoActual = null;
    private String idProductorActual = null;

    // Mapa auxiliar: ID_LOTE -> ID_CULTIVO (para rellenar txtCultivo)
    private final Map<String,String> cultivoPorLote = new HashMap<>();

    // Botones (referencia para habilitar/ocultar)
    private JButton btnNuevo;
    private JButton btnEliminar;
    private JButton btnGuardar;
    private JButton btnExportarPdf; // botón para informe

    public InspeccionForm(Frame owner) {
        super(owner, "Gestión de inspecciones", true);
        setSize(900, 560);
        setLocationRelativeTo(owner);
        detectarContexto();
        initUI();
        cargarCombos();
        cargarTabla();
        aplicarPermisosPorRol();
    }

    private void detectarContexto() {
        rolActual = Sesion.getRolActual();

        try {
            if (rolActual == RolUsuario.TECNICO) {
                TecnicoDAO tecDAO = new TecnicoDAO();
                Tecnico t = tecDAO.buscarPorUsuarioId(Sesion.getUsuarioActual().getId());
                if (t != null) idTecnicoActual = t.getIdTecnico();
            } else if (rolActual == RolUsuario.PRODUCTOR) {
                ProductorDAO prodDAO = new ProductorDAO();
                Productor p = prodDAO.buscarPorUsuarioId(Sesion.getUsuarioActual().getId());
                if (p != null) idProductorActual = p.getIdProductor();
            }
        } catch (Exception ignored) {}
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel header = UIStyle.sectionHeader(
                "Inspecciones fitosanitarias",
                "Registra y consulta inspecciones realizadas en los lotes."
        );

        // ===== FORMULARIO SUPERIOR =====
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(12, 12, 12, 12));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.3;

        int row = 0;

        JLabel lblLote    = new JLabel("Lote");
        JLabel lblCultivo = new JLabel("Cultivo (ID cultivo)");
        JLabel lblTec     = new JLabel("Técnico (TEC-#)");
        JLabel lblTotal   = new JLabel("Total plantas");
        JLabel lblAfect   = new JLabel("Plantas afectadas");
        JLabel lblFeno    = new JLabel("Estado fenológico");
        JLabel lblObs     = new JLabel("Observaciones");
        JLabel lblPorc    = new JLabel("% infestación");

        // Lote
        gbc.gridx = 0; gbc.gridy = row;
        form.add(lblLote, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        form.add(cbLote, gbc);

        // Cultivo (solo lectura)
        row++;
        txtCultivo.setEditable(false);
        gbc.gridx = 0; gbc.gridy = row;
        gbc.weightx = 0.3;
        form.add(lblCultivo, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        form.add(txtCultivo, gbc);

        // Técnico
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        gbc.weightx = 0.3;
        form.add(lblTec, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        form.add(cbTecnico, gbc);

        // Total plantas
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        gbc.weightx = 0.3;
        form.add(lblTotal, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        form.add(txtTotalPlantas, gbc);

        // Plantas afectadas
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        gbc.weightx = 0.3;
        form.add(lblAfect, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        form.add(txtPlantasAfectadas, gbc);

        // Estado fenológico
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        gbc.weightx = 0.3;
        form.add(lblFeno, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        form.add(txtEstadoFeno, gbc);

        // Observaciones
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        gbc.weightx = 0.3;
        form.add(lblObs, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        gbc.fill = GridBagConstraints.BOTH;
        form.add(new JScrollPane(txtObs), gbc);

        // % Infestación (solo lectura)
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        gbc.weightx = 0.3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        form.add(lblPorc, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        form.add(txtPorcentaje, gbc);
        txtPorcentaje.setEditable(false);

        // Listeners combos
        cbTecnico.addActionListener(e -> onCambioTecnico());
        cbLote.addActionListener(e -> actualizarCultivoDesdeLoteSeleccionado());

        // ===== TABLA =====
        String[] columnas = {"ID", "Lote", "Técnico", "Fecha", "Total",
                "Afectadas", "% Inf.", "Obs"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tblInspecciones.setModel(tableModel);
        tblInspecciones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblInspecciones.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) onTablaSeleccion();
        });

        JScrollPane scrollTabla = new JScrollPane(tblInspecciones);

        JPanel center = new JPanel(new BorderLayout());
        center.add(form, BorderLayout.NORTH);
        center.add(scrollTabla, BorderLayout.CENTER);

        // ===== BOTONES =====
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        btnNuevo = UIStyle.backButton();
        btnNuevo.setText("Nuevo");
        btnNuevo.addActionListener(e -> limpiarFormulario());

        btnEliminar = UIStyle.dangerButton("Eliminar");
        btnEliminar.addActionListener(e -> eliminarSeleccionada());

        JButton btnCerrar = UIStyle.backButton();
        btnCerrar.setText("Cerrar");
        btnCerrar.addActionListener(e -> dispose());

        btnGuardar = UIStyle.primaryButton("Guardar inspección");
        btnGuardar.addActionListener(e -> guardar());

        btnExportarPdf = UIStyle.primaryButton("Exportar informe PDF");
        btnExportarPdf.addActionListener(e -> exportarInformeSeleccionado());

        footer.add(btnNuevo);
        footer.add(btnEliminar);
        footer.add(btnCerrar);
        footer.add(btnGuardar);
        footer.add(btnExportarPdf);

        root.add(header, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);
        root.add(footer, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private void aplicarPermisosPorRol() {
        if (rolActual != RolUsuario.TECNICO) {
            // Admin y productor: solo consulta
            cbLote.setEnabled(false);
            cbTecnico.setEnabled(rolActual == RolUsuario.ADMIN);

            txtTotalPlantas.setEditable(false);
            txtPlantasAfectadas.setEditable(false);
            txtEstadoFeno.setEditable(false);
            txtObs.setEditable(false);

            btnNuevo.setEnabled(false);
            btnGuardar.setEnabled(false);
            btnEliminar.setEnabled(false);

            // OJO: btnExportarPdf se deja habilitado para que puedan descargar
        } else {
            // Técnico
            cbTecnico.setEnabled(false);
            btnEliminar.setEnabled(false);
            // btnExportarPdf también habilitado
        }

        // ========================== ALERTA PARA PRODUCTOR ==========================
        if (rolActual == RolUsuario.PRODUCTOR) {
            tblInspecciones.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(
                        JTable table, Object value, boolean isSelected,
                        boolean hasFocus, int row, int column) {

                    Component c = super.getTableCellRendererComponent(
                            table, value, isSelected, hasFocus, row, column);

                    Object val = table.getValueAt(row, 6); // columna % Inf.
                    double p = 0;
                    if (val != null && !val.toString().isBlank()) {
                        try {
                            p = Double.parseDouble(val.toString());
                        } catch (NumberFormatException ignored) {}
                    }

                    if (!isSelected) {
                        if (p >= 30) {
                            c.setBackground(new Color(255, 220, 220)); // rojito alerta
                        } else {
                            c.setBackground(Color.WHITE);
                        }
                    }

                    return c;
                }
            });
        }
    }

    private void cargarCombos() {
        try {
            LoteDAO loteDAO   = new LoteDAO();
            TecnicoDAO tecDAO = new TecnicoDAO();

            cbTecnico.removeAllItems();
            cbLote.removeAllItems();
            cultivoPorLote.clear();

            if (rolActual == RolUsuario.TECNICO) {
                // Técnico actual
                Tecnico t = tecDAO.buscarPorUsuarioId(Sesion.getUsuarioActual().getId());
                if (t != null) {
                    idTecnicoActual = t.getIdTecnico();
                    String label = "(" + t.getIdentificacion() + ") " + t.getNombreCompleto();
                    cbTecnico.addItem(new ComboItem(t.getIdTecnico(), label));
                    cbTecnico.setSelectedIndex(0);
                }
                // Lotes asignados a ese técnico
                if (idTecnicoActual != null) {
                    List<Lote> lotes = loteDAO.listarLotesAsignadosATecnico(idTecnicoActual);
                    for (Lote l : lotes) {
                        String lab = "Lote " + l.getNumeroLote()
                                + " (" + l.getAreaHectareas() + " ha)";
                        cbLote.addItem(new ComboItem(l.getIdLote(), lab));
                        cultivoPorLote.put(l.getIdLote(), l.getIdCultivo());
                    }
                }

            } else if (rolActual == RolUsuario.ADMIN) {
                // Admin: puede elegir técnico y ver lotes por técnico
                List<Tecnico> tecnicos = tecDAO.listarTecnicos();
                for (Tecnico t : tecnicos) {
                    String label = "(" + t.getIdentificacion() + ") " + t.getNombreCompleto();
                    cbTecnico.addItem(new ComboItem(t.getIdTecnico(), label));
                }
                onCambioTecnico(); // y según el técnico cargamos lotes

            } else if (rolActual == RolUsuario.PRODUCTOR) {
                // Productor: solo sus lotes, sin elegir técnico
                cbTecnico.setEnabled(false);
                if (idProductorActual != null) {
                    List<Lote> lotes = loteDAO.listarPorProductor(idProductorActual);
                    for (Lote l : lotes) {
                        String lab = "Lote " + l.getNumeroLote()
                                + " (" + l.getAreaHectareas() + " ha)";
                        cbLote.addItem(new ComboItem(l.getIdLote(), lab));
                        cultivoPorLote.put(l.getIdLote(), l.getIdCultivo());
                    }
                }
            }

            // Inicializar campo cultivo según lote seleccionado
            actualizarCultivoDesdeLoteSeleccionado();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudieron cargar los combos de inspección:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onCambioTecnico() {
        if (rolActual != RolUsuario.ADMIN) return;
        ComboItem tecItem = (ComboItem) cbTecnico.getSelectedItem();
        cbLote.removeAllItems();
        cultivoPorLote.clear();
        if (tecItem == null) {
            txtCultivo.setText("");
            return;
        }
        try {
            LoteDAO loteDAO = new LoteDAO();
            List<Lote> lotes = loteDAO.listarLotesAsignadosATecnico(tecItem.getId());
            for (Lote l : lotes) {
                String lab = "Lote " + l.getNumeroLote()
                        + " (" + l.getAreaHectareas() + " ha)";
                cbLote.addItem(new ComboItem(l.getIdLote(), lab));
                cultivoPorLote.put(l.getIdLote(), l.getIdCultivo());
            }
            actualizarCultivoDesdeLoteSeleccionado();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudieron cargar los lotes del técnico:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarCultivoDesdeLoteSeleccionado() {
        ComboItem loteItem = (ComboItem) cbLote.getSelectedItem();
        if (loteItem == null) {
            txtCultivo.setText("");
            return;
        }
        String idLote = loteItem.getId();
        String idCultivo = cultivoPorLote.get(idLote);
        txtCultivo.setText(idCultivo != null ? idCultivo : "");
    }

    private void cargarTabla() {
        try {
            List<Inspeccion> lista = controller.listarInspeccionesParaUsuarioActual();
            tableModel.setRowCount(0);
            for (Inspeccion i : lista) {
                tableModel.addRow(new Object[]{
                        i.getIdInspeccion(),
                        i.getIdLote(),
                        i.getIdTecnico(),
                        i.getFechaInspeccion() != null
                                ? i.getFechaInspeccion().toString() : "",
                        i.getTotalPlantas(),
                        i.getPlantasAfectadas(),
                        i.getPorcentajeInfestacion(),
                        i.getObservaciones()
                });
            }
            limpiarFormulario();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo cargar la lista de inspecciones:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onTablaSeleccion() {
        int row = tblInspecciones.getSelectedRow();
        if (row == -1) return;

        idInspeccionSeleccionada = (String) tableModel.getValueAt(row, 0);

        String idLote = (String) tableModel.getValueAt(row, 1);
        String idTec  = (String) tableModel.getValueAt(row, 2);

        seleccionarComboPorId(cbLote, idLote);
        seleccionarComboPorId(cbTecnico, idTec);
        actualizarCultivoDesdeLoteSeleccionado();

        txtTotalPlantas.setText(String.valueOf(tableModel.getValueAt(row, 4)));
        txtPlantasAfectadas.setText(String.valueOf(tableModel.getValueAt(row, 5)));
        txtPorcentaje.setText(String.valueOf(tableModel.getValueAt(row, 6)));
        txtEstadoFeno.setText(""); // si luego lo agregas a la tabla, actualizas esto
        txtObs.setText(String.valueOf(tableModel.getValueAt(row, 7)));
    }

    private void seleccionarComboPorId(JComboBox<ComboItem> combo, String id) {
        if (id == null) {
            combo.setSelectedIndex(-1);
            return;
        }
        for (int i = 0; i < combo.getItemCount(); i++) {
            ComboItem it = combo.getItemAt(i);
            if (id.equals(it.getId())) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private void limpiarFormulario() {
        idInspeccionSeleccionada = null;
        txtTotalPlantas.setText("");
        txtPlantasAfectadas.setText("");
        txtEstadoFeno.setText("");
        txtObs.setText("");
        txtPorcentaje.setText("");
        tblInspecciones.clearSelection();
        actualizarCultivoDesdeLoteSeleccionado();
    }

    private void guardar() {
        // Solo el técnico puede guardar
        if (rolActual != RolUsuario.TECNICO) return;

        try {
            ComboItem loteItem = (ComboItem) cbLote.getSelectedItem();
            ComboItem tecItem  = (ComboItem) cbTecnico.getSelectedItem();

            if (loteItem == null) throw new Exception("Seleccione un lote.");
            if (tecItem == null) throw new Exception("No se encontró el técnico.");

            // ===== Leer y validar números =====
            String totalStr  = txtTotalPlantas.getText().trim();
            String afectStr  = txtPlantasAfectadas.getText().trim();

            if (totalStr.isEmpty() || afectStr.isEmpty()) {
                throw new Exception("Debe registrar el total de plantas y las plantas afectadas.");
            }

            int total   = Integer.parseInt(totalStr);
            int afect   = Integer.parseInt(afectStr);

            if (total <= 0) {
                throw new Exception("El total de plantas debe ser mayor que cero.");
            }
            if (afect < 0 || afect > total) {
                throw new Exception("Las plantas afectadas deben estar entre 0 y el total de plantas.");
            }

            // ===== Calcular porcentaje de infestación =====
            double porcentaje = (afect * 100.0) / total;
            txtPorcentaje.setText(String.format("%.2f", porcentaje));

            // ===== Armar entidad =====
            Inspeccion i = new Inspeccion();
            i.setIdInspeccion(idInspeccionSeleccionada);
            i.setIdLote(loteItem.getId());
            i.setIdTecnico(tecItem.getId());
            i.setFechaInspeccion(LocalDate.now());
            i.setTotalPlantas(total);
            i.setPlantasAfectadas(afect);
            i.setEstadoFenologico(txtEstadoFeno.getText().trim());
            i.setPorcentajeInfestacion(porcentaje);
            i.setObservaciones(txtObs.getText().trim());

            // ===== Insert / update =====
            if (idInspeccionSeleccionada == null) {
                controller.registrarInspeccion(i);
                JOptionPane.showMessageDialog(this, "Inspección registrada.");
            } else {
                controller.actualizarInspeccion(i);
                JOptionPane.showMessageDialog(this, "Inspección actualizada.");
            }

            cargarTabla();

        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this,
                    "Total de plantas y plantas afectadas deben ser números enteros.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarSeleccionada() {
        // No permitido; simplemente avisamos
        JOptionPane.showMessageDialog(this,
                "No está permitido eliminar inspecciones.",
                "Aviso",
                JOptionPane.WARNING_MESSAGE);
    }

    private void exportarInformeSeleccionado() {
        int row = tblInspecciones.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione una inspección en la tabla.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String idInspeccion = (String) tableModel.getValueAt(row, 0); // columna ID

        // Diálogo para elegir dónde guardar
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar informe de inspección");
        chooser.setSelectedFile(new File("informe_" + idInspeccion + ".pdf"));

        int result = chooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return; // canceló
        }

        File destino = chooser.getSelectedFile();

        try {
            ReporteUtil.exportarInspeccionPdf(idInspeccion, destino.getAbsolutePath());
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

    // ----- Clase auxiliar ComboItem -----
    private static class ComboItem {
        private final String id;
        private final String label;

        public ComboItem(String id, String label) {
            this.id = id;
            this.label = label;
        }

        public String getId() { return id; }

        @Override
        public String toString() { return label; }
    }
}
