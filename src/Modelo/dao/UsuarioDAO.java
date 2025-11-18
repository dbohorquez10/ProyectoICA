/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo.dao;

import modelo.ConexionBD;
import modelo.RolUsuario;
import modelo.entidades.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la entidad USUARIO.
 * <p>
 * Se encarga de:
 * - Autenticar usuarios (login).
 * - Crear nuevos usuarios (para técnicos, admins, etc.).
 * - Consultar usuarios para gestión del administrador.
 */
public class UsuarioDAO {

    /**
     * Autentica un usuario por username y contraseña.
     * <p>
     * Usa la conexión del dueño del esquema (ADMINICA), ya que esta
     * operación debe poder ejecutarse antes de tener sesión.
     *
     * @param username nombre de usuario
     * @param password contraseña SIN encriptar (por ahora)
     * @return objeto Usuario si las credenciales son correctas, o null si no lo son
     * @throws SQLException si ocurre algún error de BD
     */
    public Usuario login(String username, String password) throws SQLException {
        String sql = "SELECT ID, USERNAME, PASSWORD_HASH, NOMBRE, ROL, ACTIVO " +
                     "FROM USUARIO " +
                     "WHERE USERNAME = ? AND PASSWORD_HASH = ?";

        try (Connection con = ConexionBD.getConexionAppOwner();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null; // usuario o contraseña incorrectos
                }

                Usuario u = new Usuario();
                u.setId(rs.getInt("ID"));
                u.setUsername(rs.getString("USERNAME"));
                u.setPasswordHash(rs.getString("PASSWORD_HASH"));
                u.setNombre(rs.getString("NOMBRE"));
                u.setRol(RolUsuario.fromString(rs.getString("ROL")));
                u.setActivo("S".equals(rs.getString("ACTIVO")));
                return u;
            }
        }
    }

    /**
     * Crea un nuevo registro en la tabla USUARIO.
     * <p>
     * Usa la secuencia seq_usuario para generar el ID.
     * Devuelve el ID generado para poder usarlo en TECNICO, PRODUCTOR, etc.
     *
     * @param usuario datos del usuario (sin ID)
     * @return ID generado
     * @throws SQLException si ocurre un error de BD
     */
    public int crearUsuario(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO USUARIO (ID, USERNAME, PASSWORD_HASH, NOMBRE, ROL, ACTIVO) "
                   + "VALUES (seq_usuario.NEXTVAL, ?, ?, ?, ?, ?)";

        // Pedimos al driver que nos devuelva la columna ID generada
        try (Connection con = ConexionBD.getConexionAppOwner();
             PreparedStatement ps = con.prepareStatement(sql, new String[]{"ID"})) {

            ps.setString(1, usuario.getUsername());
            ps.setString(2, usuario.getPasswordHash());
            ps.setString(3, usuario.getNombre());
            ps.setString(4, usuario.getRol().toDbValue());
            ps.setString(5, usuario.isActivo() ? "S" : "N");

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int idGenerado = rs.getInt(1);
                    usuario.setId(idGenerado);
                    return idGenerado;
                } else {
                    throw new SQLException("No se pudo obtener el ID generado del usuario");
                }
            }
        }
    }

    /**
     * Lista todos los usuarios registrados en el sistema.
     *
     * @return lista de usuarios
     * @throws SQLException si ocurre un error de BD
     */
    public List<Usuario> listarTodos() throws SQLException {
        List<Usuario> lista = new ArrayList<>();

        String sql = "SELECT ID, USERNAME, NOMBRE, ROL, ACTIVO " +
                     "FROM USUARIO ORDER BY ID";

        try (Connection con = ConexionBD.getConexionAppOwner();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getInt("ID"));
                u.setUsername(rs.getString("USERNAME"));
                u.setNombre(rs.getString("NOMBRE"));
                u.setRol(RolUsuario.fromString(rs.getString("ROL")));
                u.setActivo("S".equals(rs.getString("ACTIVO")));
                lista.add(u);
            }
        }
        return lista;
    }

    /**
     * Busca un usuario por su ID.
     *
     * @param id identificador del usuario
     * @return usuario encontrado o null si no existe
     * @throws SQLException si ocurre un error de BD
     */
    public Usuario buscarPorId(int id) throws SQLException {
        String sql = "SELECT ID, USERNAME, PASSWORD_HASH, NOMBRE, ROL, ACTIVO " +
                     "FROM USUARIO WHERE ID = ?";

        try (Connection con = ConexionBD.getConexionAppOwner();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                Usuario u = new Usuario();
                u.setId(rs.getInt("ID"));
                u.setUsername(rs.getString("USERNAME"));
                u.setPasswordHash(rs.getString("PASSWORD_HASH"));
                u.setNombre(rs.getString("NOMBRE"));
                u.setRol(RolUsuario.fromString(rs.getString("ROL")));
                u.setActivo("S".equals(rs.getString("ACTIVO")));
                return u;
            }
        }
    }

    /**
     * Actualiza los datos básicos de un usuario.
     *
     * @param usuario usuario con los datos actualizados (incluye ID)
     * @throws SQLException si ocurre un error de BD
     */
    public void actualizarUsuario(Usuario usuario) throws SQLException {
        String sql = "UPDATE USUARIO " +
                     "SET USERNAME = ?, PASSWORD_HASH = ?, NOMBRE = ?, ROL = ?, ACTIVO = ? " +
                     "WHERE ID = ?";

        try (Connection con = ConexionBD.getConexionAppOwner();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, usuario.getUsername());
            ps.setString(2, usuario.getPasswordHash());
            ps.setString(3, usuario.getNombre());
            ps.setString(4, usuario.getRol().toDbValue());
            ps.setString(5, usuario.isActivo() ? "S" : "N");
            ps.setInt(6, usuario.getId());

            ps.executeUpdate();
        }
    }

    /**
     * Elimina lógicamente un usuario marcándolo como inactivo.
     *
     * @param id identificador del usuario
     * @throws SQLException si ocurre un error de BD
     */
    public void desactivarUsuario(int id) throws SQLException {
        String sql = "UPDATE USUARIO SET ACTIVO = 'N' WHERE ID = ?";

        try (Connection con = ConexionBD.getConexionAppOwner();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}

