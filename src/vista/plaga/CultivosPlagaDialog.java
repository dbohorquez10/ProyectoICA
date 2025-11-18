package vista.plaga;

import modelo.dao.CultivoDAO;
import modelo.dao.PlagaCultivoDAO;
import modelo.entidades.Cultivo;
import vista.UIStyle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Diálogo para marcar qué cultivos son afectados por una plaga.
 * No hay escritura libre: solo selección por check.
 *
 * Se abre desde PlagaForm, solo para ADMIN.
 */
public class CultivosPlagaDialog extends JDialog {

    private final String idPlaga;

    private final PlagaCultivoDAO plagaCultivoDAO = new PlagaCultivoDAO();
    private final CultivoDAO cultivoDAO = new CultivoDAO();

    private final DefaultListModel<CultivoItem> listModel = new DefaultListModel<>();
    private final JList<CultivoItem> lstCultivos = new JList<>(listModel);

    public CultivosPlagaDialog(Window owner, String idPlaga) {
        super(owner, "Cultivos afectados", ModalityType.APPLICATION_MODAL);
        this.idPlaga = idPlaga;

        setSize(420, 420);
        setLocationRelativeTo(owner);
        initUI();
        cargarDatos();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel header = UIStyle.sectionHeader(
                "Cultivos afectados",
                "Marca los cultivos que pueden ser afectados por esta plaga."
        );

        lstCultivos.setCellRenderer(new CheckRenderer());
        lstCultivos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstCultivos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int idx = lstCultivos.locationToIndex(e.getPoint());
                if (idx >= 0) {
                    CultivoItem item = listModel.getElementAt(idx);
                    item.setSeleccionado(!item.isSeleccionado());
                    lstCultivos.repaint();
                }
            }
        });

        JScrollPane scroll = new JScrollPane(lstCultivos);
        scroll.setBorder(new EmptyBorder(8, 0, 8, 0));

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnCancelar = UIStyle.backButton();
        btnCancelar.setText("Cancelar");
        btnCancelar.addActionListener(e -> dispose());

        JButton btnGuardar = UIStyle.primaryButton("Guardar");
        btnGuardar.addActionListener(e -> guardar());

        footer.add(btnCancelar);
        footer.add(btnGuardar);

        root.add(header, BorderLayout.NORTH);
        root.add(scroll, BorderLayout.CENTER);
        root.add(footer, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private void cargarDatos() {
        try {
            // 1) Todos los cultivos existentes
            List<Cultivo> cultivos = cultivoDAO.listarCultivos();

            // 2) IDs de cultivos ya asociados a esta plaga
            List<String> asociados = plagaCultivoDAO.listarCultivosDePlaga(idPlaga);
            Set<String> setAsociados = new HashSet<>(asociados);

            listModel.clear();
            for (Cultivo c : cultivos) {
                boolean marcado = setAsociados.contains(c.getIdCultivo());
                String etiqueta = c.getNombreComun() + " (" + c.getIdCultivo() + ")";
                listModel.addElement(new CultivoItem(c.getIdCultivo(), etiqueta, marcado));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "No se pudieron cargar los cultivos:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void guardar() {
        try {
            List<String> seleccionados = new ArrayList<>();
            for (int i = 0; i < listModel.size(); i++) {
                CultivoItem item = listModel.getElementAt(i);
                if (item.isSeleccionado()) {
                    seleccionados.add(item.getIdCultivo());
                }
            }

            plagaCultivoDAO.guardarCultivosDePlaga(idPlaga, seleccionados);
            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo guardar la relación plaga-cultivo:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // --------- Clases internas para el checklist ---------

    private static class CultivoItem {
        private final String idCultivo;
        private final String etiqueta;
        private boolean seleccionado;

        public CultivoItem(String idCultivo, String etiqueta, boolean seleccionado) {
            this.idCultivo = idCultivo;
            this.etiqueta = etiqueta;
            this.seleccionado = seleccionado;
        }

        public String getIdCultivo() { return idCultivo; }
        public boolean isSeleccionado() { return seleccionado; }
        public void setSeleccionado(boolean seleccionado) { this.seleccionado = seleccionado; }

        @Override
        public String toString() { return etiqueta; }
    }

    private static class CheckRenderer extends JCheckBox implements ListCellRenderer<CultivoItem> {
        @Override
        public Component getListCellRendererComponent(
                JList<? extends CultivoItem> list,
                CultivoItem value,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {

            setText(value.toString());
            setSelected(value.isSeleccionado());
            setBackground(isSelected ? new Color(0xE8F5E9) : Color.WHITE);
            setForeground(Color.DARK_GRAY);
            return this;
        }
    }
}
