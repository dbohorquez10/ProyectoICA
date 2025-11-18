/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import modelo.Sesion;
import modelo.dao.UsuarioDAO;
import modelo.entidades.Usuario;

/**
 * Controlador responsable de la autenticación y cierre de sesión.
 * <p>
 * Desde las vistas (por ejemplo LoginFrame) se debe usar este controlador
 * para realizar el login en lugar de acceder directamente al DAO.
 */
public class AuthController {

    private final UsuarioDAO usuarioDAO;

    /**
     * Crea una instancia del controlador de autenticación.
     */
    public AuthController() {
        this.usuarioDAO = new UsuarioDAO();
    }

    /**
     * Intenta autenticar al usuario con el username y password dados.
     * <p>
     * Si las credenciales son válidas, se inicializa la sesión global
     * (clase {@link modelo.Sesion}) y se devuelve el usuario autenticado.
     *
     * @param username nombre de usuario
     * @param password contraseña
     * @return usuario autenticado
     * @throws Exception si los datos son inválidos o las credenciales no coinciden
     */
    public Usuario login(String username, String password) throws Exception {
        if (username == null || username.isBlank()) {
            throw new Exception("Debe ingresar el nombre de usuario.");
        }
        if (password == null || password.isBlank()) {
            throw new Exception("Debe ingresar la contraseña.");
        }

        Usuario u = usuarioDAO.login(username.trim(), password);

        if (u == null) {
            throw new Exception("Usuario o contraseña incorrectos.");
        }
        if (!u.isActivo()) {
            throw new Exception("El usuario está inactivo. Contacte al administrador.");
        }

        // Abrimos sesión
        Sesion.iniciarSesion(u);
        return u;
    }

    /**
     * Cierra la sesión actual, si existe.
     */
    public void logout() {
        Sesion.cerrarSesion();
    }
}

