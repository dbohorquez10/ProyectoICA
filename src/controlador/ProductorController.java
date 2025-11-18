/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import modelo.dao.ProductorDAO;
import modelo.entidades.Productor;

import java.util.List;

/**
 * Controlador para gestionar productores:
 * - Registro de nuevos productores (procedimiento registrar_productor).
 * - Listado de productores.
 * - Activar / desactivar cuenta.
 * - Consultar productor por usuario.
 */
public class ProductorController {

    private final ProductorDAO productorDAO;

    public ProductorController() {
        this.productorDAO = new ProductorDAO();
    }

    /**
     * Registra un nuevo productor usando el procedimiento almacenado
     * ADMINICA.registrar_productor.
     */
    public void registrarProductor(String username,
                                   String password,
                                   String nombreCompleto,
                                   String identificacion,
                                   String departamento,
                                   String direccion,
                                   String telefono,
                                   String correo) throws Exception {

        // Validaciones básicas
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
        if (correo == null || correo.isBlank()) {
            throw new Exception("El correo electrónico es obligatorio.");
        }

        productorDAO.registrarProductor(
                username.trim(),
                password.trim(),
                nombreCompleto.trim(),
                identificacion.trim(),
                direccion != null ? direccion.trim() : null,
                departamento != null ? departamento.trim() : null,
                telefono != null ? telefono.trim() : null,
                correo.trim()
        );
    }

    /**
     * Lista TODOS los productores (activos e inactivos).
     * Lo usa la pantalla de "Aprobación de usuarios".
     */
    public List<Productor> listarProductores() throws Exception {
        return productorDAO.listarProductores();
    }

    /**
     * Activa o desactiva la cuenta del productor (columna ACTIVO en USUARIO).
     */
    public void actualizarActivo(String idProductor, boolean activo) throws Exception {
        if (idProductor == null || idProductor.isBlank()) {
            throw new Exception("Debe seleccionar un productor.");
        }
        productorDAO.actualizarActivo(idProductor.trim(), activo);
    }

    /**
     * Devuelve el productor asociado a un USUARIO.ID concreto (login).
     * Lo usan pantallas como "Mis lotes" o "Visitas".
     */
    public Productor buscarPorUsuarioId(int usuarioId) throws Exception {
        return productorDAO.buscarPorUsuarioId(usuarioId);
    }
}


