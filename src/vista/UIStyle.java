/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
 * Utilidades de estilo para la UI (Swing).
 * MVC: esta clase se usa desde la capa "Vista".
 *
 * Objetivo:
 *  - Un tema verde consistente, legible y agradable.
 *  - Botones con buen contraste (texto blanco).
 *  - Encabezados de sección reutilizables.
 *  - Tarjetas de menú "clickables" (menuCard) con hover y cursor de mano.
 *
 * Notas:
 *  - Se usa sólo Swing puro para máxima compatibilidad con NetBeans.
 */

/*
 * Utilidades de estilo para la UI (Swing).
 * MVC: esta clase se usa desde la capa "Vista".
 *
 * Objetivo:
 *  - Un tema verde consistente, legible y agradable.
 *  - Botones con buen contraste (texto blanco).
 *  - Encabezados de sección reutilizables.
 *  - Tarjetas de menú "clickables" (menuCard) con hover y cursor de mano.
 *
 * Notas:
 *  - Se usa sólo Swing puro para máxima compatibilidad con NetBeans.
 */

package vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Window;

/**
 * Estilo visual centralizado para toda la aplicación ICA-FITO.
 *
 * Idea:
 *  - Fondo gris muy claro.
 *  - Tarjetas blancas con bordes suaves y sombra ligera.
 *  - Verde solo en acentos (no bloques enormes).
 *  - Campos de formulario con buen padding.
 */
public final class UIStyle {

    // Paleta
    public static final Color GREEN        = new Color(0x2e7d32);
    public static final Color GREEN_HOVER  = new Color(0x276c2b);
    public static final Color GREEN_SOFT   = new Color(0xe8f5e9);
    public static final Color BACKGROUND   = new Color(0xf3f5f4);
    public static final Color ON_PRIMARY   = Color.WHITE;
    public static final Color DANGER       = new Color(0xc62828);
    public static final Color DANGER_HOVER = new Color(0xab2222);
    public static final Color TEXT         = new Color(0x263238);
    public static final Color SUBTEXT      = new Color(0x607d8b);

    private UIStyle() {}

    // ================== TEMA GLOBAL ==================
public static void setAppIcon(Window w) {
        try {
            Image icon = Toolkit.getDefaultToolkit().getImage(
                    UIStyle.class.getResource("/modelo/logo_ica.png"));
            w.setIconImage(icon);
        } catch (Exception e) {
            System.err.println("No se pudo cargar el icono: " + e.getMessage());
        }
    }
public static void applyLogo(JFrame frame) {
    try {
        ImageIcon icon = new ImageIcon(UIStyle.class.getResource("/modelo/logo_ica.png"));
        frame.setIconImage(icon.getImage());
    } catch (Exception ignored) {
        // si falla no pasa nada
    }
}

    public static void applyGlobalTheme() {
        Font base     = new Font("Segoe UI", Font.PLAIN, 13);
        Font baseBold = base.deriveFont(Font.BOLD);

        // Fuentes
        UIManager.put("Label.font", base);
        UIManager.put("Button.font", base);
        UIManager.put("TextField.font", base);
        UIManager.put("PasswordField.font", base);
        UIManager.put("TextArea.font", base);
        UIManager.put("ComboBox.font", base);
        UIManager.put("Table.font", base);
        UIManager.put("TableHeader.font", baseBold);
        UIManager.put("OptionPane.messageFont", base);
        UIManager.put("OptionPane.buttonFont", base);

        // Fondos
        UIManager.put("Panel.background", BACKGROUND);
        UIManager.put("OptionPane.background", BACKGROUND);
        UIManager.put("OptionPane.messageForeground", TEXT);

        UIManager.put("TextField.background", Color.WHITE);
        UIManager.put("PasswordField.background", Color.WHITE);
        UIManager.put("TextArea.background", Color.WHITE);
        UIManager.put("ComboBox.background", Color.WHITE);

        // Tablas
        UIManager.put("Table.showVerticalLines", Boolean.FALSE);
        UIManager.put("Table.gridColor", new Color(0xE0E0E0));
    }

    // ================== CABECERA ==================

    /**
     * Cabecera simple: título grande + subtítulo pequeño.
     */
    public static JPanel sectionHeader(String title, String subtitle) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(4, 4, 10, 4));

        JLabel h = new JLabel(title);
        h.setFont(new Font("Segoe UI", Font.BOLD, 22));
        h.setForeground(GREEN.darker());

        JLabel s = new JLabel(subtitle);
        s.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        s.setForeground(SUBTEXT);

        p.add(h);
        p.add(Box.createVerticalStrut(2));
        p.add(s);

        return p;
    }

    // ================== TARJETAS / CONTENEDORES ==================

    /**
     * Envuelve un componente en una "tarjeta" blanca con borde suave.
     * Úsalo para formularios o bloques completos.
     */
    public static JPanel wrapInCard(JComponent inner) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(0xDDDDDD), 1, true),
                new EmptyBorder(16, 18, 16, 18)
        ));
        card.add(inner, BorderLayout.CENTER);
        return card;
    }

    /**
     * Tarjeta de menú clicable (para el MainFrame).
     */
    public static JPanel menuCard(String title, String subtitle) {
        JPanel card = new JPanel(new BorderLayout(8, 4));
        card.setOpaque(true);
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(0xE0E0E0), 1, true),
                new EmptyBorder(14, 16, 14, 16)
        ));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel h = new JLabel(title);
        h.setFont(new Font("Segoe UI", Font.BOLD, 15));
        h.setForeground(TEXT);

        JLabel s = new JLabel("<html><div style='width:230px;'>" + subtitle + "</div></html>");
        s.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        s.setForeground(SUBTEXT);

        card.add(h, BorderLayout.NORTH);
        card.add(s, BorderLayout.CENTER);

        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                card.setBackground(GREEN_SOFT);
                card.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(GREEN, 1, true),
                        new EmptyBorder(14, 16, 14, 16)
                ));
            }
            @Override public void mouseExited(MouseEvent e) {
                card.setBackground(Color.WHITE);
                card.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(new Color(0xE0E0E0), 1, true),
                        new EmptyBorder(14, 16, 14, 16)
                ));
            }
        });

        return card;
    }

    // ================== BOTONES ==================

    public static JButton primaryButton(String text) {
        JButton b = new JButton(text);
        stylePrimary(b);
        return b;
    }

    public static JButton dangerButton(String text) {
        JButton b = new JButton(text);
        styleDanger(b);
        return b;
    }

    public static JButton ghostButton(String text) {
        JButton b = new JButton(text);
        styleGhost(b);
        return b;
    }

    /**
     * Botón estándar para "Volver" / "Cerrar" que usan muchas vistas.
     * Reutiliza el mismo estilo que ghostButton para no romper código existente.
     */
    public static JButton backButton() {
        JButton b = new JButton("Volver");
        styleGhost(b);
        return b;
    }

    private static void stylePrimary(JButton b) {
        b.setFocusPainted(false);
        b.setBackground(GREEN);
        b.setForeground(ON_PRIMARY);
        b.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(GREEN.darker(), 1, true),
                new EmptyBorder(7, 18, 7, 18)
        ));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { b.setBackground(GREEN_HOVER); }
            @Override public void mouseExited(MouseEvent e)  { b.setBackground(GREEN); }
        });
    }

    private static void styleDanger(JButton b) {
        b.setFocusPainted(false);
        b.setBackground(DANGER);
        b.setForeground(ON_PRIMARY);
        b.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(DANGER_HOVER, 1, true),
                new EmptyBorder(7, 18, 7, 18)
        ));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { b.setBackground(DANGER_HOVER); }
            @Override public void mouseExited(MouseEvent e)  { b.setBackground(DANGER); }
        });
    }

    private static void styleGhost(JButton b) {
        b.setFocusPainted(false);
        b.setBackground(Color.WHITE);
        b.setForeground(GREEN.darker());
        b.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(GREEN, 1, true),
                new EmptyBorder(7, 16, 7, 16)
        ));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                b.setBackground(GREEN_SOFT);
            }
            @Override public void mouseExited(MouseEvent e) {
                b.setBackground(Color.WHITE);
            }
        });
    }

    // ================== CAMPOS DE FORMULARIO ==================

    public static void styleTextField(JTextField field) {
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(0xCFCFCF), 1, true),
                new EmptyBorder(5, 8, 5, 8)
        ));
        field.setBackground(Color.WHITE);
    }

    public static void stylePasswordField(JPasswordField field) {
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(0xCFCFCF), 1, true),
                new EmptyBorder(5, 8, 5, 8)
        ));
        field.setBackground(Color.WHITE);
    }

    public static void styleTextArea(JTextArea area) {
        area.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(0xCFCFCF), 1, true),
                new EmptyBorder(5, 8, 5, 8)
        ));
        area.setBackground(Color.WHITE);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
    }

    public static JLabel formLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(TEXT);
        return l;
    }
}

