/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import modelo.dao.ReportePlagaDAO;
import modelo.entidades.ReportePlaga;

import java.util.List;

/**
 * Controlador para la gestión de reportes de plagas por cultivo.
 */
public class ReportePlagaController {

    private final ReportePlagaDAO reporteDAO;

    /**
     * Crea una instancia de ReportePlagaController.
     */
    public ReportePlagaController() {
        this.reporteDAO = new ReportePlagaDAO();
    }

    /**
     * Registra un nuevo reporte de plaga para un cultivo.
     *
     * @param idCultivo        cultivo
     * @param idPlaga          plaga
     * @param periodo          periodo del reporte (ej. "2025-01")
     * @param nivelInfestacion nivel numérico
     * @param porcentajeAfecta porcentaje de área afectada
     * @param alertasActivas   indicador de alertas ("SI", "NO", etc.)
     * @throws Exception si los datos son inválidos o hay error de BD
     */
    public void registrarReporte(String idCultivo,
                                 String idPlaga,
                                 String periodo,
                                 double nivelInfestacion,
                                 double porcentajeAfecta,
                                 String alertasActivas) throws Exception {

        if (idCultivo == null || idCultivo.isBlank()) {
            throw new Exception("Debe seleccionar un cultivo.");
        }
        if (idPlaga == null || idPlaga.isBlank()) {
            throw new Exception("Debe seleccionar una plaga.");
        }
        if (periodo == null || periodo.isBlank()) {
            throw new Exception("Debe indicar el periodo del reporte.");
        }

        ReportePlaga r = new ReportePlaga();
        r.setIdCultivo(idCultivo.trim());
        r.setIdPlaga(idPlaga.trim());
        r.setPeriodoReporte(periodo.trim());
        r.setNivelInfestacion(nivelInfestacion);
        r.setPorcentajeAfectacion(porcentajeAfecta);
        r.setAlertasActivas(alertasActivas != null ? alertasActivas.trim() : null);

        reporteDAO.insertarReporte(r);
    }

    /**
     * Lista los reportes de plagas de un cultivo.
     *
     * @param idCultivo identificador del cultivo
     * @return lista de reportes
     * @throws Exception si ocurre un error de BD
     */
    public List<ReportePlaga> listarReportesPorCultivo(String idCultivo) throws Exception {
        if (idCultivo == null || idCultivo.isBlank()) {
            throw new Exception("Debe seleccionar un cultivo.");
        }
        return reporteDAO.listarPorCultivo(idCultivo.trim());
    }
}

