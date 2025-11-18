package controlador;

import modelo.RolUsuario;
import modelo.Sesion;
import modelo.dao.*;
import modelo.entidades.*;

import java.util.List;

public class InspeccionController {

    private final InspeccionDAO inspeccionDAO = new InspeccionDAO();
    private final VisitaDAO visitaDAO         = new VisitaDAO();

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

        // <-- NOMBRE CORRECTO DEL MÉTODO
        inspeccionDAO.insertarInspeccion(i);

        // Al registrar, las visitas ASIGNADAS para ese lote/técnico pasan a REALIZADA
        visitaDAO.marcarRealizadaPorInspeccion(i.getIdLote(), i.getIdTecnico());
    }

    public void actualizarInspeccion(Inspeccion i) throws Exception {
        if (Sesion.getRolActual() != RolUsuario.TECNICO) {
            throw new Exception("Solo el técnico puede actualizar inspecciones.");
        }
        if (i.getIdInspeccion() == null || i.getIdInspeccion().isBlank()) {
            throw new Exception("Falta el ID de la inspección.");
        }

        // <-- NOMBRE CORRECTO DEL MÉTODO
        inspeccionDAO.actualizarInspeccion(i);
    }

    public void eliminarInspeccion(String idInspeccion) throws Exception {
        throw new Exception("No está permitido eliminar inspecciones.");
    }

    /** Lista inspecciones según el rol de la sesión. */
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
