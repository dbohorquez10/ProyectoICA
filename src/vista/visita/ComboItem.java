/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista.visita;

public class ComboItem {
    private final String id;
    private final String label;

    public ComboItem(String id, String label) {
        this.id = id;
        this.label = label;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return label; // texto que ve el usuario en el combo
    }
}
