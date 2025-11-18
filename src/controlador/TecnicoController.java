/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import modelo.dao.TecnicoDAO;
import modelo.entidades.Tecnico;

import java.util.List;

/**
 * Controlador para gestionar técnicos ICA:
 * - Registro de nuevos técnicos.
 * - Listado de técnicos.
 * - Activar / desactivar cuenta.
 * - Actualizar registro ICA.
 * - Consultar técnico por usuario.
 */
public class TecnicoController {

    private final TecnicoDAO tecnicoDAO;

    public TecnicoController() {
        this.tecnicoDAO = new TecnicoDAO();
    }

    public void registrarTecnico(String username,
                                 String password,
                                 String nombreCompleto,
                                 String identificacion,
                                 String tarjetaProfesional,
                                 String departamento,
                                 String direccion,
                                 String telefono,
                                 String correo) throws Exception {

        // Validaciones...
        if (username == null || username.isBlank() ||
            password == null || password.isBlank()) {
            throw new Exception("Debe especificar usuario y contraseña.");
        }
        if (nombreCompleto == null || nombreCompleto.isBlank()) {
            throw new Exception("El nombre completo es obligatorio.");
        }
        if (identificacion == null || identificacion.isBlank()) {
            throw new Exception("La identificación es obligatoria.");
        }
        if (tarjetaProfesional == null || tarjetaProfesional.isBlank()) {
            throw new Exception("La tarjeta profesional es obligatoria.");
        }
        if (correo == null || correo.isBlank()) {
            throw new Exception("El correo electrónico es obligatorio.");
        }

        tecnicoDAO.registrarTecnico(
                username.trim(),
                password.trim(),
                nombreCompleto.trim(),
                identificacion.trim(),
                tarjetaProfesional.trim(),
                direccion != null ? direccion.trim() : null,
                departamento != null ? departamento.trim() : null,
                telefono != null ? telefono.trim() : null,
                correo.trim()
        );
    }

    public List<Tecnico> listarTecnicos() throws Exception {
        return tecnicoDAO.listarTecnicos();
    }

    public Tecnico buscarPorUsuarioId(int usuarioId) throws Exception {
        return tecnicoDAO.buscarPorUsuarioId(usuarioId);
    }

    public void actualizarActivo(String idTecnico, boolean activo) throws Exception {
        if (idTecnico == null || idTecnico.isBlank()) {
            throw new Exception("Debe seleccionar un técnico.");
        }
        tecnicoDAO.actualizarActivo(idTecnico.trim(), activo);
    }

    public void actualizarRegistroIca(String idTecnico, String registroIca) throws Exception {
        if (idTecnico == null || idTecnico.isBlank()) {
            throw new Exception("Debe seleccionar un técnico.");
        }
        tecnicoDAO.actualizarRegistroIca(
                idTecnico.trim(),
                registroIca != null ? registroIca.trim() : null
        );
    }
}


