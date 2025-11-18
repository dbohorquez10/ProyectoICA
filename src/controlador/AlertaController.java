/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import modelo.dao.AlertaInfestacionDAO;
import modelo.entidades.AlertaInfestacion;

import java.util.List;

/**
 * Controlador para la gestión de alertas de infestación.
 * <p>
 * Permite registrar alertas manuales y consultar las alertas asociadas
 * a un lote (las automáticas ya las generan los triggers).
 */
public class AlertaController {

    private final AlertaInfestacionDAO alertaDAO;

    /**
     * Crea una instancia de AlertaController.
     */
    public AlertaController() {
        this.alertaDAO = new AlertaInfestacionDAO();
    }

    /**
     * Registra una alerta manual sobre una inspección concreta.
     *
     * @param idInspeccion ID de la inspección
     * @param nivel        nivel crítico
     * @param mensaje      mensaje descriptivo
     * @throws Exception si los datos son inválidos o hay error de BD
     */
    public void registrarAlertaManual(String idInspeccion,
                                      double nivel,
                                      String mensaje) throws Exception {

        if (idInspeccion == null || idInspeccion.isBlank()) {
            throw new Exception("Debe seleccionar una inspección.");
        }
        if (nivel <= 0) {
            throw new Exception("El nivel crítico debe ser mayor que cero.");
        }
        if (mensaje == null || mensaje.isBlank()) {
            throw new Exception("Debe ingresar un mensaje para la alerta.");
        }

        alertaDAO.registrarAlertaManual(idInspeccion.trim(), nivel, mensaje.trim());
    }

    /**
     * Lista las alertas asociadas a un lote.
     *
     * @param idLote identificador del lote
     * @return lista de alertas
     * @throws Exception si ocurre un error de BD
     */
    public List<AlertaInfestacion> listarAlertasPorLote(String idLote) throws Exception {
        if (idLote == null || idLote.isBlank()) {
            throw new Exception("Debe seleccionar un lote.");
        }
        return alertaDAO.listarPorLote(idLote.trim());
    }
}

