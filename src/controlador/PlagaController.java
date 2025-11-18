/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
// archivo: src/controlador/PlagaController.java
package controlador;

import modelo.dao.PlagaDAO;
import modelo.entidades.Plaga;

import java.sql.SQLException;
import java.util.List;

/**
 * Lógica de negocio para la gestión de plagas:
 *  - registrar nuevas plagas
 *  - listar
 *  - actualizar
 *  - eliminar
 */
public class PlagaController {

    private final PlagaDAO plagaDAO;

    public PlagaController() {
        this.plagaDAO = new PlagaDAO();
    }

    public void registrarPlaga(String nombreCientifico,
                               String nombreComun,
                               String tipoPlaga) throws Exception {

        if (nombreCientifico == null || nombreCientifico.isBlank()) {
            throw new Exception("El nombre científico es obligatorio.");
        }
        if (nombreComun == null || nombreComun.isBlank()) {
            throw new Exception("El nombre común es obligatorio.");
        }

        Plaga p = new Plaga();
        p.setNombreCientifico(nombreCientifico.trim());
        p.setNombreComun(nombreComun.trim());
        p.setTipoPlaga(tipoPlaga != null ? tipoPlaga.trim() : null);

        plagaDAO.registrarPlaga(p);
    }

    public List<Plaga> listarPlagas() throws SQLException {
        return plagaDAO.listarTodas();
    }

    public void actualizarPlaga(Plaga p) throws Exception {
        if (p.getIdPlaga() == null || p.getIdPlaga().isBlank()) {
            throw new Exception("Falta el ID de la plaga a actualizar.");
        }
        plagaDAO.actualizarPlaga(p);
    }

    public void eliminarPlaga(String idPlaga) throws Exception {
        if (idPlaga == null || idPlaga.isBlank()) {
            throw new Exception("Debe seleccionar una plaga.");
        }
        plagaDAO.eliminarPlaga(idPlaga.trim());
    }
}
