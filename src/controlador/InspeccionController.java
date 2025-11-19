package controlador;

import modelo.RolUsuario;
import modelo.Sesion;
import modelo.dao.*;
import modelo.entidades.*;
import modelo.ConexionBD;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class InspeccionController {

    private final InspeccionDAO inspeccionDAO = new InspeccionDAO();
    private final VisitaDAO visitaDAO         = new VisitaDAO();

    // =========================================================
    //  REGISTRAR INSPECCIÓN
    // =========================================================
    public void registrarInspeccion(Inspeccion i) throws Exception {
        // Solo técnico puede registrar
        if (Sesion.getRolActual() != RolUsuario.TECNICO) {
            throw new Exception("Solo el técnico puede registrar inspecciones.");
        }

        if (i.getIdLote() == null || i.getIdLote().isBlank()) {
            throw new Exception("Debe seleccionar un lote.");
        }
        if (i.getIdTecnico() == null || i.getIdTecnico().isBlank()) {
            throw new Exception("No se encontró el técnico.");
        }

        Connection cn = null;

        try {
            // Conexión con el usuario actual (TECNICO_ICA)
            cn = ConexionBD.getConexionActual();
            cn.setAutoCommit(false);

            // 1) Insertar inspección (llama a ADMINICA.registrar_inspeccion)
            inspeccionDAO.registrarInspeccion(
                    i.getIdLote(),
                    i.getIdTecnico(),
                    i.getTotalPlantas(),
                    i.getPlantasAfectadas(),
                    i.getEstadoFenologico(),
                    i.getPorcentajeInfestacion(),
                    i.getObservaciones(),
                    cn
            );

            // 2) Marcar visita como REALIZADA y liberar asignación de lote
            visitaDAO.marcarRealizadaPorInspeccion(
                    i.getIdLote(),
                    i.getIdTecnico(),
                    cn      // MISMA conexión
            );

            cn.commit();

        } catch (SQLException ex) {
            if (cn != null) {
                try { cn.rollback(); } catch (SQLException ignored) {}
            }

            // Errores típicos
            if (ex.getErrorCode() == 942) {        // ORA-00942
                throw new Exception(
                        "Error de acceso a tablas (ORA-00942). " +
                        "Revisa que el rol del técnico tenga permisos sobre INSPECCION / VISITA / ASIGNACION_LOTE.",
                        ex
                );
            } else if (ex.getErrorCode() == 20001) {
                // Por si algún trigger de negocio lanza RAISE_APPLICATION_ERROR
                throw new Exception(ex.getMessage(), ex);
            } else {
                throw new Exception("Error al registrar la inspección: " + ex.getMessage(), ex);
            }

        } finally {
            if (cn != null) {
                try {
                    cn.setAutoCommit(true);
                    cn.close();
                } catch (SQLException ignored) {}
            }
        }
    }

    // =========================================================
    //  ACTUALIZAR
    // =========================================================
    public void actualizarInspeccion(Inspeccion i) throws Exception {
        if (Sesion.getRolActual() != RolUsuario.TECNICO) {
            throw new Exception("Solo el técnico puede actualizar inspecciones.");
        }
        if (i.getIdInspeccion() == null || i.getIdInspeccion().isBlank()) {
            throw new Exception("Falta el ID de la inspección.");
        }

        inspeccionDAO.actualizarInspeccion(i);
    }

    // =========================================================
    //  ELIMINAR (NO PERMITIDO)
    // =========================================================
    public void eliminarInspeccion(String idInspeccion) throws Exception {
        throw new Exception("No está permitido eliminar inspecciones.");
    }

    // =========================================================
    //  LISTADOS SEGÚN ROL
    // =========================================================
    public List<Inspeccion> listarInspeccionesParaUsuarioActual() throws Exception {
        RolUsuario rol = Sesion.getRolActual();
        int usuarioId = Sesion.getUsuarioActual().getId();

        if (rol == RolUsuario.ADMIN) {
            return inspeccionDAO.listarTodas();

        } else if (rol == RolUsuario.TECNICO) {
            TecnicoDAO tecDAO = new TecnicoDAO();
            Tecnico t = tecDAO.buscarPorUsuarioId(usuarioId);
            if (t == null) {
                throw new Exception("No se encontró el técnico de la sesión.");
            }
            return inspeccionDAO.listarPorTecnico(t.getIdTecnico());

        } else { // PRODUCTOR
            ProductorDAO prodDAO = new ProductorDAO();
            Productor p = prodDAO.buscarPorUsuarioId(usuarioId);
            if (p == null) {
                throw new Exception("No se encontró el productor de la sesión.");
            }
            return inspeccionDAO.listarPorProductor(p.getIdProductor());
        }
    }
}
