package dao;

import conexion.Conexion;
import modelo.UsuarioLogin;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioLoginDAO {

    // LOGIN - verifica correo y contraseña
    public UsuarioLogin login(String correo, String contrasena) {
        String sql = "SELECT * FROM Usuario WHERE Correo=? AND Contrasena=? AND Estado='Activo'";
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, correo);
            ps.setString(2, contrasena);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new UsuarioLogin(
                    rs.getInt("IdUsuario"),
                    rs.getString("Nombre"),
                    rs.getString("Apellido"),
                    rs.getString("Documento"),
                    rs.getString("Correo"),
                    rs.getString("Contrasena"),
                    rs.getString("Estado"),
                    rs.getString("Rol")
                );
            }
        } catch (SQLException e) {
            System.out.println("Error login: " + e.getMessage());
        }
        return null;
    }

    // INSERT
    public boolean insertar(UsuarioLogin u) {
        String sql = "INSERT INTO Usuario (IdUsuario, Nombre, Apellido, Documento, Correo, Contrasena, Estado, Rol) " +
                     "VALUES (SEQ_USUARIO.NEXTVAL, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getApellido());
            ps.setString(3, u.getDocumento());
            ps.setString(4, u.getCorreo());
            ps.setString(5, u.getContrasena());
            ps.setString(6, u.getEstado());
            ps.setString(7, u.getRol());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error insertar Usuario: " + e.getMessage());
            return false;
        }
    }

    // SELECT ALL
    public List<UsuarioLogin> listar() {
        List<UsuarioLogin> lista = new ArrayList<>();
        String sql = "SELECT * FROM Usuario ORDER BY IdUsuario";
        try (Connection con = Conexion.conectar();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new UsuarioLogin(
                    rs.getInt("IdUsuario"),
                    rs.getString("Nombre"),
                    rs.getString("Apellido"),
                    rs.getString("Documento"),
                    rs.getString("Correo"),
                    rs.getString("Contrasena"),
                    rs.getString("Estado"),
                    rs.getString("Rol")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error listar Usuarios: " + e.getMessage());
        }
        return lista;
    }

    // UPDATE
    public boolean actualizar(UsuarioLogin u) {
        String sql = "UPDATE Usuario SET Nombre=?, Apellido=?, Documento=?, Correo=?, Estado=?, Rol=? WHERE IdUsuario=?";
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getApellido());
            ps.setString(3, u.getDocumento());
            ps.setString(4, u.getCorreo());
            ps.setString(5, u.getEstado());
            ps.setString(6, u.getRol());
            ps.setInt(7, u.getIdUsuario());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error actualizar Usuario: " + e.getMessage());
            return false;
        }
    }

    // ACTIVAR / DESACTIVAR
    public boolean cambiarEstado(int id, String estado) {
        String sql = "UPDATE Usuario SET Estado=? WHERE IdUsuario=?";
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setInt(2, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error cambiar estado Usuario: " + e.getMessage());
            return false;
        }
    }

    // DELETE
    public boolean eliminar(int id) {
        String sql = "DELETE FROM Usuario WHERE IdUsuario=?";
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error eliminar Usuario: " + e.getMessage());
            return false;
        }
    }
}
