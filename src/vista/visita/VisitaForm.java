/*
 * Gestión de visitas técnicas.
 */
package vista.visita;

import controlador.VisitaController;
import modelo.RolUsuario;
import modelo.Sesion;
import modelo.dao.LoteDAO;
import modelo.dao.ProductorDAO;
import modelo.dao.TecnicoDAO;
import modelo.entidades.Lote;
import modelo.entidades.Productor;
import modelo.entidades.Tecnico;
import modelo.entidades.Visita;
import vista.UIStyle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Gestión de visitas técnicas.
 *
 * Flujo:
 *  - PRODUCTOR:
 *      * Productor se toma de la sesión.
 *      * Solo escoge LOTE, escribe motivo y solicita → estado SOLICITADA.
 *      * No ve técnico, ni fecha visita, ni botón Eliminar.
 *  - ADMIN:
 *      * Ve todas las visitas.
 *      * Puede escoger productor, lote, técnico, fecha y estado
 *        (ASIGNADA / CANCELADA).
 *  - TÉCNICO:
 *      * Ya no debería usar esta pantalla. El estado REALIZADA lo
 *        genera el módulo de inspecciones fitosanitarias.
 */
public class VisitaForm extends JDialog {

    private final JComboBox<ComboItem> cbProductor = new JComboBox<>();
    private final JComboBox<ComboItem> cbLote      = new JComboBox<>();
    private final JComboBox<ComboItem> cbTecnico   = new JComboBox<>();

    private final JTextField txtFechaVisita = new JTextField(); // opcional (solo admin)
    private final JTextArea  txtMotivo      = new JTextArea(3, 20);
    private final JTextArea  txtObs         = new JTextArea(3, 20);

    // El contenido se llena según el rol, ya NO incluye REALIZADA.
    private final JComboBox<String> cbEstado = new JComboBox<>();

    private final JTable tblVisitas = new JTable();
    private DefaultTableModel tableModel;

    private final VisitaController controller = new VisitaController();

    private boolean     modoProductor      = false;
    private RolUsuario  rolActual;
    private String      idProductorActual  = null;
    private String      idVisitaSeleccionada = null;

    public VisitaForm(Frame owner, boolean modoTecnicoNoUsado) {
        super(owner, "Gestión de visitas", true);
        setSize(900, 560);
        setLocationRelativeTo(owner);
        detectarContexto();
        initUI();
        cargarCombosIniciales();
        cargarTabla();
    }

    /**
     * Determina el rol actual y, si es productor,
     * obtiene su ID_PRODUCTOR.
     */
    private void detectarContexto() {
        rolActual = Sesion.getRolActual();
        if (rolActual == RolUsuario.PRODUCTOR) {
            modoProductor = true;
            try {
                ProductorDAO prodDAO = new ProductorDAO();
                Productor p = prodDAO.buscarPorUsuarioId(
                        Sesion.getUsuarioActual().getId()
                );
                if (p != null) {
                    idProductorActual = p.getIdProductor();
                }
            } catch (Exception ignored) {}
        }
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(new EmptyBorder(12, 12, 12, 12));

        String sub;
        if (rolActual == RolUsuario.PRODUCTOR) {
            sub = "Solicita y revisa visitas técnicas para tus lotes.";
        } else if (rolActual == RolUsuario.TECNICO) {
            // En teoría el técnico ya no debería entrar aquí,
            // pero dejamos el texto por si acaso.
            sub = "Consulta información de visitas técnicas.";
        } else {
            sub = "Administra todas las visitas técnicas del sistema.";
        }

        JPanel header = UIStyle.sectionHeader("Visitas técnicas", sub);

        // ===== FORM =====
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(12, 12, 12, 12));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.3;

        int row = 0;

        JLabel lblProd  = new JLabel("Productor (Cédula - Nombre)");
        JLabel lblLote  = new JLabel("Lote");
        JLabel lblTec   = new JLabel("Técnico (Cédula - Nombre)");
        JLabel lblFecha = new JLabel("Fecha visita (YYYY-MM-DD opcional)");
        JLabel lblMotivo = new JLabel("Motivo");
        JLabel lblObs    = new JLabel("Observaciones");
        JLabel lblEstado = new JLabel("Estado");

        // PRODUCTOR
        gbc.gridx = 0; gbc.gridy = row;
        form.add(lblProd, gbc);
        gbc.gridx = 1; gbc.gridy = row;
        gbc.weightx = 0.7;
        form.add(cbProductor, gbc);

        row++;
        // LOTE
        gbc.weightx = 0.3;
        gbc.gridx = 0; gbc.gridy = row;
        form.add(lblLote, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        form.add(cbLote, gbc);

        row++;
        // TÉCNICO
        gbc.gridx = 0; gbc.gridy = row;
        gbc.weightx = 0.3;
        form.add(lblTec, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        form.add(cbTecnico, gbc);

        row++;
        // FECHA VISITA
        gbc.gridx = 0; gbc.gridy = row;
        gbc.weightx = 0.3;
        form.add(lblFecha, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        form.add(txtFechaVisita, gbc);

        row++;
        // MOTIVO
        gbc.gridx = 0; gbc.gridy = row;
        gbc.weightx = 0.3;
        form.add(lblMotivo, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        gbc.fill = GridBagConstraints.BOTH;
        form.add(new JScrollPane(txtMotivo), gbc);

        row++;
        // OBS
        gbc.gridx = 0; gbc.gridy = row;
        gbc.weightx = 0.3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        form.add(lblObs, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        gbc.fill = GridBagConstraints.BOTH;
        form.add(new JScrollPane(txtObs), gbc);

        row++;
        // ESTADO
        gbc.gridx = 0; gbc.gridy = row;
        gbc.weightx = 0.3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        form.add(lblEstado, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        form.add(cbEstado, gbc);

        // ===== CONFIG SEGÚN ROL =====

        // Estados: aquí ya NO incluimos REALIZADA.
        if (rolActual == RolUsuario.ADMIN) {
            cbEstado.addItem("ASIGNADA");
            cbEstado.addItem("CANCELADA");
        } else {
            // Productor y cualquier otro rol solo ven SOLICITADA aquí.
            cbEstado.addItem("SOLICITADA");
        }

        if (modoProductor && idProductorActual != null) {
            // Productor de sesión fijo
            cbProductor.setEnabled(false);

            // No ve técnico
            lblTec.setVisible(false);
            cbTecnico.setVisible(false);

            // No ve fecha visita (la pone admin)
            lblFecha.setVisible(false);
            txtFechaVisita.setVisible(false);

            // Estado fijo SOLICITADA y deshabilitado
            cbEstado.setSelectedItem("SOLICITADA");
            cbEstado.setEnabled(false);
        }

        // Si algún día se usara para TECNICO, aquí podríamos
        // bloquear combos, pero por ahora el técnico no entra.

        // Cambio de productor → recargar lotes (solo admin)
        cbProductor.addActionListener(e -> onCambioProductor());

        // ===== TABLA =====
        String[] columnas = {"ID", "Prod", "Lote", "Téc", "F. Solicitud",
                "F. Visita", "Motivo", "Estado", "Obs"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tblVisitas.setModel(tableModel);
        tblVisitas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblVisitas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) onTablaSeleccion();
        });

        JScrollPane scrollTabla = new JScrollPane(tblVisitas);

        JPanel center = new JPanel(new BorderLayout());
        center.add(form, BorderLayout.NORTH);
        center.add(scrollTabla, BorderLayout.CENTER);

        // ===== BOTONES =====
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton btnNuevo = UIStyle.backButton();
        btnNuevo.setText("Nuevo");
        btnNuevo.addActionListener(e -> limpiarFormulario());

        JButton btnEliminar = UIStyle.dangerButton("Eliminar");
        btnEliminar.addActionListener(e -> eliminarSeleccionada());

        JButton btnCerrar = UIStyle.backButton();
        btnCerrar.setText("Cerrar");
        btnCerrar.addActionListener(e -> dispose());

        JButton btnGuardar = UIStyle.primaryButton(
                rolActual == RolUsuario.ADMIN ? "Asignar / guardar visita"
                                              : "Guardar visita");
        btnGuardar.addActionListener(e -> guardar());

        footer.add(btnNuevo);
        footer.add(btnEliminar);
        footer.add(btnCerrar);
        footer.add(btnGuardar);

        // Productor NO puede eliminar visitas
        if (modoProductor) {
            btnEliminar.setVisible(false);
        }

        root.add(header, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);
        root.add(footer, BorderLayout.SOUTH);

        setContentPane(root);
    }

    // =================== CARGA DE COMBOS ===================

    private void cargarCombosIniciales() {
        try {
            ProductorDAO prodDAO = new ProductorDAO();
            TecnicoDAO   tecDAO  = new TecnicoDAO();

            // ===== PRODUCTOR =====
            cbProductor.removeAllItems();
            if (modoProductor && idProductorActual != null) {
                Productor p = prodDAO.buscarPorUsuarioId(
                        Sesion.getUsuarioActual().getId()
                );
                if (p != null) {
                    String label = "(" + p.getIdentificacion() + ") " +
                                   p.getNombreCompleto();
                    cbProductor.addItem(new ComboItem(p.getIdProductor(), label));
                    cbProductor.setSelectedIndex(0);
                    // cargar lotes del productor de la sesión
                    cargarLotesParaProductor(p.getIdProductor());
                }
            } else {
                List<Productor> productores = prodDAO.listarProductores();
                for (Productor p : productores) {
                    String label = "(" + p.getIdentificacion() + ") " +
                                   p.getNombreCompleto();
                    cbProductor.addItem(new ComboItem(p.getIdProductor(), label));
                }
            }

            // ===== TÉCNICOS =====
            cbTecnico.removeAllItems();
            // Sólo tiene sentido para ADMIN (productor no ve técnico)
            if (rolActual == RolUsuario.ADMIN) {
                List<Tecnico> tecnicos = tecDAO.listarTecnicos(); // método ya existente
                for (Tecnico t : tecnicos) {
                    String label = "(" + t.getIdentificacion() + ") " +
                                   t.getNombreCompleto();
                    cbTecnico.addItem(new ComboItem(t.getIdTecnico(), label));
                }
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudieron cargar los combos:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarLotesParaProductor(String idProductor) {
        cbLote.removeAllItems();
        if (idProductor == null || idProductor.isEmpty()) return;

        try {
            LoteDAO loteDAO = new LoteDAO();
            List<Lote> lotes = loteDAO.listarPorProductor(idProductor);
            for (Lote l : lotes) {
                String label = "Lote " + l.getNumeroLote() +
                        " (" + l.getAreaHectareas() + " ha)";
                cbLote.addItem(new ComboItem(l.getIdLote(), label));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudieron cargar los lotes del productor:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onCambioProductor() {
        if (modoProductor) return; // el productor de sesión no cambia
        ComboItem prod = (ComboItem) cbProductor.getSelectedItem();
        if (prod != null) {
            cargarLotesParaProductor(prod.getId());
        }
    }

    // =================== TABLA ===================

    private void cargarTabla() {
        try {
            List<Visita> lista = controller.listarVisitasParaUsuarioActual();
            tableModel.setRowCount(0);
            for (Visita v : lista) {
                tableModel.addRow(new Object[]{
                        v.getIdVisita(),
                        v.getIdProductor(),
                        v.getIdLote(),
                        v.getIdTecnico(),
                        v.getFechaSolicitud() != null ? v.getFechaSolicitud().toString() : "",
                        v.getFechaVisita()   != null ? v.getFechaVisita().toString()   : "",
                        v.getMotivo(),
                        v.getEstado(),
                        v.getObservaciones()
                });
            }
            limpiarFormulario();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo cargar la lista de visitas:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
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

    private void onTablaSeleccion() {
        int row = tblVisitas.getSelectedRow();
        if (row == -1) return;

        idVisitaSeleccionada = (String) tableModel.getValueAt(row, 0);

        String idProd = (String) tableModel.getValueAt(row, 1);
        String idLote = (String) tableModel.getValueAt(row, 2);
        String idTec  = (String) tableModel.getValueAt(row, 3);

        if (!modoProductor) {
            seleccionarComboPorId(cbProductor, idProd);
            cargarLotesParaProductor(idProd);
            seleccionarComboPorId(cbLote, idLote);
        } else {
            cargarLotesParaProductor(idProductorActual);
            seleccionarComboPorId(cbLote, idLote);
        }

        if (rolActual == RolUsuario.ADMIN) {
            seleccionarComboPorId(cbTecnico, idTec);
        }

        txtFechaVisita.setText(String.valueOf(tableModel.getValueAt(row, 5)));
        txtMotivo.setText(String.valueOf(tableModel.getValueAt(row, 6)));

        Object est = tableModel.getValueAt(row, 7);
        if (est != null) cbEstado.setSelectedItem(est.toString());

        txtObs.setText(String.valueOf(tableModel.getValueAt(row, 8)));
    }

    private void limpiarFormulario() {
        idVisitaSeleccionada = null;

        txtFechaVisita.setText("");
        txtMotivo.setText("");
        txtObs.setText("");

        if (modoProductor) {
            cbEstado.setSelectedItem("SOLICITADA");
            cbEstado.setEnabled(false);
        } else if (rolActual == RolUsuario.ADMIN) {
            // Valor por defecto cuando admin abre para asignar
            if (cbEstado.getItemCount() > 0) {
                cbEstado.setSelectedIndex(0); // ASIGNADA
            }
        }

        tblVisitas.clearSelection();
    }

    // =================== GUARDAR / ELIMINAR ===================

    private void guardar() {
        try {
            LocalDate fecha = null;
            if (!txtFechaVisita.getText().trim().isEmpty()) {
                fecha = LocalDate.parse(txtFechaVisita.getText().trim());
            }

            String idProductor;
            String idLote;
            String idTecnico = null;

            if (modoProductor) {
                idProductor = idProductorActual;
            } else {
                ComboItem prodItem = (ComboItem) cbProductor.getSelectedItem();
                if (prodItem == null) throw new Exception("Seleccione un productor.");
                idProductor = prodItem.getId();
            }

            ComboItem loteItem = (ComboItem) cbLote.getSelectedItem();
            if (loteItem == null) throw new Exception("Seleccione un lote.");
            idLote = loteItem.getId();

            if (rolActual == RolUsuario.ADMIN) {
                ComboItem tecItem = (ComboItem) cbTecnico.getSelectedItem();
                idTecnico = (tecItem != null) ? tecItem.getId() : null;
            }

            if (idVisitaSeleccionada == null) {
                // === Nueva visita ===
                String estado = modoProductor ? "SOLICITADA" : "ASIGNADA";

                controller.registrarVisita(
                        idProductor,
                        idLote,
                        txtMotivo.getText(),
                        modoProductor ? null : idTecnico,
                        fecha,
                        txtObs.getText()
                );
                // registrarVisita ya fija estado PENDIENTE/SOLICITADA
                JOptionPane.showMessageDialog(this, "Visita registrada.");
            } else {
                // === Actualización ===
                Visita v = new Visita();
                v.setIdVisita(idVisitaSeleccionada);
                v.setIdProductor(idProductor);
                v.setIdLote(idLote);
                v.setIdTecnico(modoProductor ? null : idTecnico);
                v.setFechaVisita(fecha);
                v.setMotivo(txtMotivo.getText());

                if (modoProductor) {
                    // Productor nunca cambia estado desde aquí
                    int row = tblVisitas.getSelectedRow();
                    if (row >= 0) {
                        Object est = tableModel.getValueAt(row, 7);
                        v.setEstado(est != null ? est.toString() : "SOLICITADA");
                    } else {
                        v.setEstado("SOLICITADA");
                    }
                } else {
                    String estadoSel = (String) cbEstado.getSelectedItem();
                    if (rolActual == RolUsuario.ADMIN &&
                            !"ASIGNADA".equals(estadoSel) &&
                            !"CANCELADA".equals(estadoSel)) {
                        throw new Exception("El administrador solo puede marcar la visita como ASIGNADA o CANCELADA.");
                    }
                    v.setEstado(estadoSel);
                }

                v.setObservaciones(txtObs.getText());

                controller.actualizarVisita(v);
                JOptionPane.showMessageDialog(this, "Visita actualizada.");
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
        int row = tblVisitas.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione una visita en la tabla.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id = (String) tableModel.getValueAt(row, 0);

        int opt = JOptionPane.showConfirmDialog(
                this,
                "¿Eliminar la visita " + id + "?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );
        if (opt != JOptionPane.YES_OPTION) return;

        try {
            controller.eliminarVisita(id);
            cargarTabla();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Error al eliminar",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
