/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista.cultivo;

import controlador.CultivoPlagaController;
import modelo.entidades.Plaga;
import vista.UIStyle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Diálogo de solo lectura que lista las plagas asociadas a un cultivo.
 * Lo pueden usar ADMIN, TÉCNICO y PRODUCTOR.
 */
public class PlagasPorCultivoDialog extends JDialog {

    private final String idCultivo;
    private final String nombreCultivo;
    private final CultivoPlagaController controller = new CultivoPlagaController();

    private final JTable tblPlagas = new JTable();
    private DefaultTableModel tableModel;

    public PlagasPorCultivoDialog(Window owner,
                                  String idCultivo,
                                  String nombreCultivo) {
        super(owner,
              "Plagas que afectan a " + nombreCultivo,
              Dialog.ModalityType.APPLICATION_MODAL);
        this.idCultivo = idCultivo;
        this.nombreCultivo = nombreCultivo;
        setSize(700, 420);
        setLocationRelativeTo(owner);
        initUI();
        cargarDatos();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel header = UIStyle.sectionHeader(
                "Plagas asociadas al cultivo",
                "Cultivo: " + nombreCultivo + " (" + idCultivo + ")"
        );

        String[] columnas = {"ID", "Nombre común", "Nombre científico", "Tipo"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tblPlagas.setModel(tableModel);
        tblPlagas.setRowHeight(22);

        JScrollPane scroll = new JScrollPane(tblPlagas);
        scroll.setBorder(new EmptyBorder(10, 0, 0, 0));

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setOpaque(false);
        JButton btnCerrar = UIStyle.backButton();
        btnCerrar.setText("Cerrar");
        btnCerrar.addActionListener(e -> dispose());
        footer.add(btnCerrar);

        root.add(header, BorderLayout.NORTH);
        root.add(scroll, BorderLayout.CENTER);
        root.add(footer, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private void cargarDatos() {
        try {
            List<Plaga> plagas = controller.listarPlagasDeCultivo(idCultivo);
            tableModel.setRowCount(0);
            for (Plaga p : plagas) {
                tableModel.addRow(new Object[]{
                        p.getIdPlaga(),
                        p.getNombreComun(),
                        p.getNombreCientifico(),
                        p.getTipoPlaga()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "No se pudieron cargar las plagas del cultivo:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
