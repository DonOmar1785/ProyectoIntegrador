package dao;

import conexion.Conexion;
import modelo.Usuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public boolean insertarEstudiante(Usuario u, int idPrograma) {
        String sql = "{CALL SP_INSERTAR_ESTUDIANTE(?, ?, ?, ?, ?, ?)}";
        try (Connection con = Conexion.conectar();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setString(1, u.getNombre());
            cs.setString(2, u.getApellido());
            cs.setString(3, u.getDocumento());
            cs.setString(4, u.getCorreo());
            cs.setString(5, u.getContrasena());
            cs.setInt(6, idPrograma);
            cs.execute();
            return true;
        } catch (SQLException e) {
            System.out.println("Error insertar Estudiante: " + e.getMessage());
            return false;
        }
    }

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

    public boolean actualizarEstudiante(Usuario u, int idPrograma) {
        String sql = "{CALL SP_ACTUALIZAR_ESTUDIANTE(?, ?, ?, ?, ?, ?)}";
        try (Connection con = Conexion.conectar();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, u.getIdUsuario());
            cs.setString(2, u.getNombre());
            cs.setString(3, u.getApellido());
            cs.setString(4, u.getDocumento());
            cs.setString(5, u.getCorreo());
            cs.setInt(6, idPrograma);
            cs.execute();
            return true;
        } catch (SQLException e) {
            System.out.println("Error actualizar Estudiante: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarEstudiante(int id) {
        String sql = "{CALL SP_ELIMINAR_ESTUDIANTE(?)}";
        try (Connection con = Conexion.conectar();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, id);
            cs.execute();
            return true;
        } catch (SQLException e) {
            System.out.println("Error eliminar Estudiante: " + e.getMessage());
            return false;
        }
    }

    public String insertarEstudianteConMensaje(Usuario u, int idPrograma) {
        String sql = "{CALL SP_INSERTAR_ESTUDIANTE(?, ?, ?, ?, ?, ?)}";
        try (Connection con = Conexion.conectar();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setString(1, u.getNombre()); cs.setString(2, u.getApellido());
            cs.setString(3, u.getDocumento()); cs.setString(4, u.getCorreo());
            cs.setString(5, u.getContrasena()); cs.setInt(6, idPrograma);
            cs.execute();
            return null;
        } catch (SQLException e) { return e.getMessage(); }
    }

    public String actualizarEstudianteConMensaje(Usuario u, int idPrograma) {
        String sql = "{CALL SP_ACTUALIZAR_ESTUDIANTE(?, ?, ?, ?, ?, ?)}";
        try (Connection con = Conexion.conectar();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, u.getIdUsuario()); cs.setString(2, u.getNombre());
            cs.setString(3, u.getApellido()); cs.setString(4, u.getDocumento());
            cs.setString(5, u.getCorreo()); cs.setInt(6, idPrograma);
            cs.execute();
            return null;
        } catch (SQLException e) { return e.getMessage(); }
    }
}