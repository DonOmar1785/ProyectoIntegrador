package dao;

import conexion.Conexion;
import modelo.Usuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO unificado para gestión de usuarios.
 * Maneja la tabla Estudiante. Para Tutor y Asesor se siguen los mismos patrones.
 */
public class UsuarioDAO {

    // INSERT estudiante
    public boolean insertarEstudiante(Usuario u, int idPrograma) {
        String sql = "INSERT INTO Estudiante (IdEstudiante, Nombre, Apellido, Documento, Correo, Contrasena, IdPrograma) " +
                     "VALUES (SEQ_ESTUDIANTE.NEXTVAL, ?, ?, ?, ?, ?, ?)";
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getApellido());
            ps.setString(3, u.getDocumento());
            ps.setString(4, u.getCorreo());
            ps.setString(5, u.getContrasena());
            ps.setInt(6, idPrograma);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error insertar Estudiante: " + e.getMessage());
            return false;
        }
    }

    // SELECT ALL estudiantes
    public List<Usuario> listarEstudiantes() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT e.IdEstudiante, e.Nombre, e.Apellido, e.Documento, e.Correo, p.Nombre AS Programa " +
                     "FROM Estudiante e JOIN Programa p ON e.IdPrograma = p.IdPrograma ORDER BY e.IdEstudiante";
        try (Connection con = Conexion.conectar();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Usuario u = new Usuario();
                u.setIdUsuario(rs.getInt("IdEstudiante"));
                u.setNombre(rs.getString("Nombre"));
                u.setApellido(rs.getString("Apellido"));
                u.setDocumento(rs.getString("Documento"));
                u.setCorreo(rs.getString("Correo"));
                u.setRol(rs.getString("Programa"));
                lista.add(u);
            }
        } catch (SQLException e) {
            System.out.println("Error listar Estudiantes: " + e.getMessage());
        }
        return lista;
    }

    // UPDATE estudiante
    public boolean actualizarEstudiante(Usuario u, int idPrograma) {
        String sql = "UPDATE Estudiante SET Nombre=?, Apellido=?, Documento=?, Correo=?, IdPrograma=? WHERE IdEstudiante=?";
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getApellido());
            ps.setString(3, u.getDocumento());
            ps.setString(4, u.getCorreo());
            ps.setInt(5, idPrograma);
            ps.setInt(6, u.getIdUsuario());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error actualizar Estudiante: " + e.getMessage());
            return false;
        }
    }

    // DELETE estudiante
    public boolean eliminarEstudiante(int id) {
        String sql = "DELETE FROM Estudiante WHERE IdEstudiante=?";
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error eliminar Estudiante: " + e.getMessage());
            return false;
        }
    }
}
