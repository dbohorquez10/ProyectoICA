/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo.dao;

import modelo.ConexionBD;
import modelo.entidades.ReportePlaga;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la entidad REPORTE_PLAGA.
 * <p>
 * No hay procedimiento almacenado específico en el script, así que se usan
 * sentencias SQL directas para insertar y consultar.
 */
public class ReportePlagaDAO {

    /**
     * Inserta un nuevo reporte de plaga.
     * <p>
     * El ID_REPORTE se genera con la secuencia seq_reporte
     * usando el patrón REP-#.
     *
     * @param reporte datos del reporte (sin ID_REPORTE)
     * @throws SQLException si ocurre un error de BD
     */
    public void insertarReporte(ReportePlaga reporte) throws SQLException {
        String sql = "INSERT INTO REPORTE_PLAGA (" +
                     "ID_REPORTE, ID_CULTIVO, ID_PLAGA, PERIODO_REPORTE, " +
                     "NIVEL_INFESTACION, PORCENTAJE_AFECTACION, ALERTAS_ACTIVAS" +
                     ") VALUES (" +
                     "'REP-' || seq_reporte.NEXTVAL, ?, ?, ?, ?, ?, ?)";

        try (Connection con = ConexionBD.getConexionActual();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, reporte.getIdCultivo());
            ps.setString(2, reporte.getIdPlaga());
            ps.setString(3, reporte.getPeriodoReporte());
            ps.setDouble(4, reporte.getNivelInfestacion());
            ps.setDouble(5, reporte.getPorcentajeAfectacion());
            ps.setString(6, reporte.getAlertasActivas());

            ps.executeUpdate();
        }
    }

    /**
     * Lista los reportes de plaga de un cultivo.
     *
     * @param idCultivo identificador del cultivo
     * @return lista de reportes
     * @throws SQLException si ocurre un error de BD
     */
    public List<ReportePlaga> listarPorCultivo(String idCultivo) throws SQLException {
        List<ReportePlaga> lista = new ArrayList<>();

        String sql = "SELECT ID_REPORTE, ID_CULTIVO, ID_PLAGA, PERIODO_REPORTE, " +
                     "NIVEL_INFESTACION, PORCENTAJE_AFECTACION, ALERTAS_ACTIVAS " +
                     "FROM REPORTE_PLAGA WHERE ID_CULTIVO = ? ORDER BY PERIODO_REPORTE DESC";

        try (Connection con = ConexionBD.getConexionActual();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, idCultivo);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ReportePlaga r = new ReportePlaga();
                    r.setIdReporte(rs.getString("ID_REPORTE"));
                    r.setIdCultivo(rs.getString("ID_CULTIVO"));
                    r.setIdPlaga(rs.getString("ID_PLAGA"));
                    r.setPeriodoReporte(rs.getString("PERIODO_REPORTE"));
                    r.setNivelInfestacion(rs.getDouble("NIVEL_INFESTACION"));
                    r.setPorcentajeAfectacion(rs.getDouble("PORCENTAJE_AFECTACION"));
                    r.setAlertasActivas(rs.getString("ALERTAS_ACTIVAS"));
                    lista.add(r);
                }
            }
        }
        return lista;
    }
}

