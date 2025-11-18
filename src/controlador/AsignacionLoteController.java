/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import modelo.dao.AsignacionLoteDAO;

import java.sql.SQLException;

/**
 * Controlador para la asignación de lotes a técnicos.
 */
public class AsignacionLoteController {

    private final AsignacionLoteDAO asignacionDAO;

    /**
     * Crea una instancia de AsignacionLoteController.
     */
    public AsignacionLoteController() {
        this.asignacionDAO = new AsignacionLoteDAO();
    }

    /**
     * Asigna un lote a un técnico.
     * <p>
     * El trigger trg_limite_asignaciones se encarga de impedir que un técnico
     * tenga más de 5 lotes asignados. Si se intenta violar esa regla,
     * Oracle lanzará un error -20001 que se puede traducir a un mensaje
     * amigable para el usuario.
     *
     * @param idTecnico ID del técnico (TEC-#)
     * @param idLote    ID del lote (LOT-#)
     * @throws Exception si los datos son inválidos o ocurre un error de BD
     */
    public void asignarLoteATecnico(String idTecnico, String idLote) throws Exception {
        if (idTecnico == null || idTecnico.isBlank()) {
            throw new Exception("Debe seleccionar un técnico.");
        }
        if (idLote == null || idLote.isBlank()) {
            throw new Exception("Debe seleccionar un lote.");
        }

        try {
            asignacionDAO.registrarAsignacion(idTecnico.trim(), idLote.trim());
        } catch (SQLException ex) {
            // Interpretar el error del trigger de límite de asignaciones
            if (ex.getMessage() != null && ex.getMessage().contains("El técnico ya tiene 5 lotes asignados")) {
                throw new Exception("No se puede asignar el lote: el técnico ya tiene 5 lotes asignados.");
            }
            throw ex;
        }
    }
}

