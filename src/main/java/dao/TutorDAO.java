package dao;

import conexion.Conexion;
import modelo.Tutor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TutorDAO {

    public boolean insertar(Tutor t) {
        String sql = "{CALL SP_INSERTAR_TUTOR(?, ?, ?, ?, ?, ?)}";
        try (Connection con = Conexion.conectar();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setString(1, t.getNombre());
            cs.setString(2, t.getApellido());
            cs.setString(3, t.getDocumento());
            cs.setString(4, t.getCorreo());
            cs.setString(5, t.getContrasena());
            cs.setInt(6, t.getIdPrograma());
            cs.execute();
            return true;
        } catch (SQLException e) {
            System.out.println("Error insertar Tutor: " + e.getMessage());
            return false;
        }
    }

    public List<Tutor> listar() {
        List<Tutor> lista = new ArrayList<>();
        String sql = "SELECT t.IdTutor, t.Nombre, t.Apellido, t.Documento, t.Correo, t.Contrasena, t.IdPrograma " +
                     "FROM Tutor t ORDER BY t.IdTutor";
        try (Connection con = Conexion.conectar();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Tutor(
                    rs.getInt("IdTutor"),
                    rs.getString("Nombre"),
                    rs.getString("Apellido"),
                    rs.getString("Documento"),
                    rs.getString("Correo"),
                    rs.getString("Contrasena"),
                    rs.getInt("IdPrograma")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error listar Tutores: " + e.getMessage());
        }
        return lista;
    }

    public boolean actualizar(Tutor t) {
        String sql = "{CALL SP_ACTUALIZAR_TUTOR(?, ?, ?, ?, ?, ?)}";
        try (Connection con = Conexion.conectar();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, t.getIdTutor());
            cs.setString(2, t.getNombre());
            cs.setString(3, t.getApellido());
            cs.setString(4, t.getDocumento());
            cs.setString(5, t.getCorreo());
            cs.setInt(6, t.getIdPrograma());
            cs.execute();
            return true;
        } catch (SQLException e) {
            System.out.println("Error actualizar Tutor: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int id) {
        String sql = "{CALL SP_ELIMINAR_TUTOR(?)}";
        try (Connection con = Conexion.conectar();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, id);
            cs.execute();
            return true;
        } catch (SQLException e) {
            System.out.println("Error eliminar Tutor: " + e.getMessage());
            return false;
        }
    }

    public String insertarConMensaje(Tutor t) {
        String sql = "{CALL SP_INSERTAR_TUTOR(?, ?, ?, ?, ?, ?)}";
        try (Connection con = Conexion.conectar();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setString(1, t.getNombre()); cs.setString(2, t.getApellido());
            cs.setString(3, t.getDocumento()); cs.setString(4, t.getCorreo());
            cs.setString(5, t.getContrasena()); cs.setInt(6, t.getIdPrograma());
            cs.execute();
            return null;
        } catch (SQLException e) { return e.getMessage(); }
    }

    public String actualizarConMensaje(Tutor t) {
        String sql = "{CALL SP_ACTUALIZAR_TUTOR(?, ?, ?, ?, ?, ?)}";
        try (Connection con = Conexion.conectar();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, t.getIdTutor()); cs.setString(2, t.getNombre());
            cs.setString(3, t.getApellido()); cs.setString(4, t.getDocumento());
            cs.setString(5, t.getCorreo()); cs.setInt(6, t.getIdPrograma());
            cs.execute();
            return null;
        } catch (SQLException e) { return e.getMessage(); }
    }
}