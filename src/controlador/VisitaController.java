package controlador;

import modelo.RolUsuario;
import modelo.Sesion;
import modelo.dao.ProductorDAO;
import modelo.dao.TecnicoDAO;
import modelo.dao.VisitaDAO;
import modelo.entidades.Productor;
import modelo.entidades.Tecnico;
import modelo.entidades.Visita;
import modelo.ConexionBD;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Controlador para visitas técnicas.
 *
 * Flujo:
 *  - Productor: solicita visita → estado SOLICITADA
 *  - Admin: asigna técnico / cambia estado → estado ASIGNADA
 *  - Técnico: marca REALIZADA y agrega observaciones
 */
public class VisitaController {

    private final VisitaDAO visitaDAO = new VisitaDAO();

    // =========================================================
    //  REGISTRAR NUEVA VISITA (productor o admin)
    // =========================================================
    public void registrarVisita(String idProductor,
                                String idLote,
                                String motivo,
                                String idTecnico,
                                LocalDate fechaVisita,
                                String observaciones) throws Exception {

        if (idProductor == null || idProductor.isBlank()) {
            throw new Exception("Debe indicar el productor.");
        }
        if (idLote == null || idLote.isBlank()) {
            throw new Exception("Debe indicar el lote.");
        }
        if (motivo == null || motivo.isBlank()) {
            throw new Exception("Debe indicar el motivo de la visita.");
        }

        Visita v = new Visita();
        v.setIdProductor(idProductor.trim());
        v.setIdLote(idLote.trim());
        v.setFechaVisita(fechaVisita);
        v.setMotivo(motivo.trim());
        v.setObservaciones(observaciones != null ? observaciones.trim() : null);

        boolean tieneTecnico = (idTecnico != null && !idTecnico.isBlank());
        if (tieneTecnico) {
            v.setIdTecnico(idTecnico.trim());
            v.setEstado("ASIGNADA");      // Admin asigna técnico
        } else {
            v.setIdTecnico(null);
            v.setEstado("SOLICITADA");    // Productor solo solicita
        }

        Connection cn = null;

        try {
            cn = ConexionBD.getAppConnection();
            cn.setAutoCommit(false);

            // 1) Insertar VISITA (misma conexión)
            visitaDAO.insertarVisita(v, cn);

            // 2) Si hay técnico, registrar la asignación del lote
            if (tieneTecnico) {
                registrarAsignacionLote(cn, v.getIdTecnico(), v.getIdLote());
            }

            cn.commit();

        } catch (SQLException ex) {
            rollbackSilencioso(cn);

            if (ex.getErrorCode() == 20001) {
                // Mensaje del trigger
                throw new Exception("No se puede asignar la visita: el técnico ya tiene 5 lotes asignados.", ex);
            } else {
                throw new Exception("Error al registrar la visita: " + ex.getMessage(), ex);
            }

        } finally {
            cerrarConexion(cn);
        }
    }

    // =========================================================
    //  ACTUALIZAR VISITA (usado por ADMIN / TÉCNICO)
    // =========================================================
    public void actualizarVisita(Visita v) throws Exception {
        if (v.getIdVisita() == null || v.getIdVisita().isBlank()) {
            throw new Exception("Falta el ID de la visita.");
        }

        Connection cn = null;

        try {
            cn = ConexionBD.getAppConnection();
            cn.setAutoCommit(false);

            // 1) Actualizar VISITA
            visitaDAO.actualizarVisita(v, cn);

            // 2) Si la visita queda ASIGNADA con técnico, asegurar asignación de lote
            if (v.getIdTecnico() != null
                    && !v.getIdTecnico().isBlank()
                    && "ASIGNADA".equalsIgnoreCase(v.getEstado())) {

                // Evitar duplicar si ya existe esa asignación
                if (!existeAsignacion(cn, v.getIdTecnico(), v.getIdLote())) {
                    registrarAsignacionLote(cn, v.getIdTecnico(), v.getIdLote());
                }
            }

            cn.commit();

        } catch (SQLException ex) {
            rollbackSilencioso(cn);

            if (ex.getErrorCode() == 20001) {
                throw new Exception("No se puede asignar la visita: el técnico ya tiene 5 lotes asignados.", ex);
            } else {
                throw new Exception("Error al actualizar la visita: " + ex.getMessage(), ex);
            }

        } finally {
            cerrarConexion(cn);
        }
    }

    // =========================================================
    //  ELIMINAR
    // =========================================================
    public void eliminarVisita(String idVisita) throws Exception {
        if (idVisita == null || idVisita.isBlank()) {
            throw new Exception("Debe seleccionar una visita.");
        }
        visitaDAO.eliminarVisita(idVisita.trim());
    }

    // =========================================================
    //  LISTADOS
    // =========================================================
    public List<Visita> listarVisitasParaUsuarioActual() throws Exception {
        RolUsuario rol = Sesion.getRolActual();
        int usuarioId = Sesion.getUsuarioActual().getId();

        if (rol == RolUsuario.ADMIN) {
            return visitaDAO.listarTodas();

        } else if (rol == RolUsuario.TECNICO) {
            TecnicoDAO tecDAO = new TecnicoDAO();
            Tecnico t = tecDAO.buscarPorUsuarioId(usuarioId);
            if (t == null) {
                throw new Exception("No se encontró el técnico asociado al usuario actual.");
            }
            return visitaDAO.listarPorTecnico(t.getIdTecnico());

        } else { // PRODUCTOR
            ProductorDAO prodDAO = new ProductorDAO();
            Productor p = prodDAO.buscarPorUsuarioId(usuarioId);
            if (p == null) {
                throw new Exception("No se encontró el productor asociado al usuario actual.");
            }
            return visitaDAO.listarPorProductor(p.getIdProductor());
        }
    }

    // =========================================================
    //  PRIVADOS AUXILIARES
    // =========================================================

    /** Llama al procedimiento ADMINICA.registrar_asignacion dentro de la misma conexión. */
    private void registrarAsignacionLote(Connection cn, String idTecnico, String idLote) throws SQLException {
        try (CallableStatement cs =
                     cn.prepareCall("{ call ADMINICA.registrar_asignacion(?, ?) }")) {
            cs.setString(1, idTecnico);
            cs.setString(2, idLote);
            cs.execute();   // aquí se dispara el trigger de 5 lotes
        }
    }

    /** Verifica si ya existe la asignación (técnico, lote) en ADMINICA.ASIGNACION_LOTE. */
    private boolean existeAsignacion(Connection cn, String idTecnico, String idLote) throws SQLException {
        String sql = "SELECT COUNT(*) FROM ADMINICA.ASIGNACION_LOTE " +
                     "WHERE ID_TECNICO = ? AND ID_LOTE = ?";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, idTecnico);
            ps.setString(2, idLote);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    private void rollbackSilencioso(Connection cn) {
        if (cn != null) {
            try { cn.rollback(); } catch (SQLException ignored) {}
        }
    }

    private void cerrarConexion(Connection cn) {
        if (cn != null) {
            try {
                cn.setAutoCommit(true);
                cn.close();
            } catch (SQLException ignored) {}
        }
    }
}
