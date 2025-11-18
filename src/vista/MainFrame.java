/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista;

import modelo.RolUsuario;
import modelo.Sesion;
import modelo.entidades.Usuario;
import vista.cultivo.CultivoForm;
import vista.inspeccion.InspeccionForm;
import vista.lote.LoteForm;
import vista.plaga.PlagaForm;
import vista.visita.VisitaForm;
import vista.admin.AprobacionUsuariosForm;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Ventana principal después del login.
 * Muestra un menú distinto según el rol del usuario.
 */
public class MainFrame extends JFrame {

    private final Usuario usuario;

    public MainFrame(Usuario usuario) {
        this.usuario = usuario;

        // La sesión ya se establece en AuthControl.login(...)
        UIStyle.applyGlobalTheme();
        UIStyle.setAppIcon(this);   // <<< aquí

        setTitle("ICA-FITO · " + usuario.getRol()); // RolUsuario.toString()
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(new EmptyBorder(12, 12, 12, 12));

        String saludo = "Hola, " + usuario.getNombre();
        String desc;

        RolUsuario rol = Sesion.getRolActual();
        if (rol == null) {
            desc = "";
        } else {
            switch (rol) {
                case ADMIN:
                    desc = "Administra técnicos, productores, cultivos, plagas y lotes.";
                    break;
                case TECNICO:
                    // Ya no menciona visitas técnicas
                    desc = "Registra inspecciones y consulta cultivos y plagas.";
                    break;
                case PRODUCTOR:
                    // Productor ahora también ve inspecciones
                    desc = "Gestiona tus lotes, visitas e inspecciones técnicas.";
                    break;
                default:
                    desc = "";
            }
        }

        JPanel header = UIStyle.sectionHeader(saludo, desc);

        // Botón cerrar sesión a la derecha
        JButton btnLogout = UIStyle.backButton();
        btnLogout.setText("Cerrar sesión");
        btnLogout.addActionListener(e -> {
            Sesion.cerrarSesion();
            dispose();
            new LoginFrame().setVisible(true);
        });

        JPanel headerWrapper = new JPanel(new BorderLayout());
        headerWrapper.add(header, BorderLayout.CENTER);
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.setOpaque(false);
        right.add(btnLogout);
        headerWrapper.add(right, BorderLayout.EAST);

        JPanel cardsPanel = new JPanel();
        cardsPanel.setOpaque(false);
        cardsPanel.setLayout(new GridLayout(0, 2, 16, 16));

        if (rol != null) {
            switch (rol) {
                case ADMIN:
                    construirMenuAdmin(cardsPanel);
                    break;
                case TECNICO:
                    construirMenuTecnico(cardsPanel);
                    break;
                case PRODUCTOR:
                    construirMenuProductor(cardsPanel);
                    break;
                default:
                    // nada
            }
        }

        root.add(headerWrapper, BorderLayout.NORTH);
        root.add(cardsPanel, BorderLayout.CENTER);

        setContentPane(root);
    }

    // ====================== MENÚS POR ROL =======================

    private void construirMenuAdmin(JPanel parent) {
        // Aprobación de usuarios (técnicos + productores)
        parent.add(card("Aprobación de usuarios",
                "Aprobar técnicos ICA y activar/desactivar productores.",
                () -> new AprobacionUsuariosForm(MainFrame.this).setVisible(true)));

        // Lotes
        parent.add(card("Lotes",
                "Gestionar y consultar lotes de los productores.",
                () -> new LoteForm(MainFrame.this).setVisible(true)));

        // Cultivos
        parent.add(card("Cultivos",
                "Administrar catálogo de cultivos por cultivo.",
                () -> new CultivoForm(MainFrame.this).setVisible(true)));

        // Plagas
        parent.add(card("Plagas",
                "Administrar catálogo de plagas.",
                () -> new PlagaForm(MainFrame.this).setVisible(true)));

        // Inspecciones (solo consulta)
        parent.add(card("Inspecciones",
                "Consultar inspecciones registradas en los lotes.",
                () -> new InspeccionForm(MainFrame.this).setVisible(true)));

        // Visitas técnicas
        parent.add(card("Visitas técnicas",
                "Consultar y gestionar visitas técnicas.",
                () -> new VisitaForm(MainFrame.this, false).setVisible(true)));
    }

    private void construirMenuTecnico(JPanel parent) {
        // Inspecciones y reportes (CRUD para el técnico)
        parent.add(card("Inspecciones y reportes",
                "Registrar inspecciones y reportes fitosanitarios.",
                () -> new InspeccionForm(MainFrame.this).setVisible(true)));

        // *** OJO: se elimina el acceso a Visitas técnicas para el técnico ***
        // Cultivos (solo consulta)
        parent.add(card("Cultivos",
                "Consultar catálogo de cultivos.",
                () -> new CultivoForm(MainFrame.this).setVisible(true)));

        // Plagas (solo consulta)
        parent.add(card("Plagas",
                "Consultar catálogo de plagas.",
                () -> new PlagaForm(MainFrame.this).setVisible(true)));
    }

    private void construirMenuProductor(JPanel parent) {
        // Mis lotes
        parent.add(card("Mis lotes",
                "Registrar y administrar tus lotes.",
                () -> new LoteForm(MainFrame.this).setVisible(true)));

        // Visitas
        parent.add(card("Visitas técnicas",
                "Solicitar y revisar visitas técnicas para tus lotes.",
                () -> new VisitaForm(MainFrame.this, false).setVisible(true)));

        // Cultivos (solo consulta)
        parent.add(card("Cultivos",
                "Consultar catálogo de cultivos.",
                () -> new CultivoForm(MainFrame.this).setVisible(true)));

        // Plagas (solo consulta)
        parent.add(card("Plagas",
                "Consultar catálogo de plagas.",
                () -> new PlagaForm(MainFrame.this).setVisible(true)));

        // NUEVO: Inspecciones (solo consulta para productor)
        parent.add(card("Inspecciones",
                "Consultar inspecciones realizadas en tus lotes.",
                () -> new InspeccionForm(MainFrame.this).setVisible(true)));
    }

    // ====================== UTILIDAD TARJETAS =======================

    private JPanel card(String titulo, String subtitulo, final Runnable onClick) {
        JPanel card = UIStyle.menuCard(titulo, subtitulo);
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onClick.run();
            }
        });
        return card;
    }
}
