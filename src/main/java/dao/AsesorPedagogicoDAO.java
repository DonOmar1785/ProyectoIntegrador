package dao;

import conexion.Conexion;
import modelo.AsesorPedagogico;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AsesorPedagogicoDAO {

    public boolean insertar(AsesorPedagogico a) {
        String sql = "{CALL SP_INSERTAR_ASESOR(?, ?, ?, ?, ?, ?)}";
        try (Connection con = Conexion.conectar();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setString(1, a.getNombre());
            cs.setString(2, a.getApellido());
            cs.setString(3, a.getDocumento());
            cs.setString(4, a.getCorreo());
            cs.setString(5, a.getContrasena());
            cs.setInt(6, a.getIdInstitucion());
            cs.execute();
            return true;
        } catch (SQLException e) {
            System.out.println("Error insertar Asesor: " + e.getMessage());
            return false;
        }
    }

    public List<AsesorPedagogico> listar() {
        List<AsesorPedagogico> lista = new ArrayList<>();
        String sql = "SELECT IdAsesor, Nombre, Apellido, Documento, Correo, Contrasena, IdInstitucion " +
                     "FROM AsesorPedagogico ORDER BY IdAsesor";
        try (Connection con = Conexion.conectar();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new AsesorPedagogico(
                    rs.getInt("IdAsesor"),
                    rs.getString("Nombre"),
                    rs.getString("Apellido"),
                    rs.getString("Documento"),
                    rs.getString("Correo"),
                    rs.getString("Contrasena"),
                    rs.getInt("IdInstitucion")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error listar Asesores: " + e.getMessage());
        }
        return lista;
    }

    public boolean actualizar(AsesorPedagogico a) {
        String sql = "{CALL SP_ACTUALIZAR_ASESOR(?, ?, ?, ?, ?, ?)}";
        try (Connection con = Conexion.conectar();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, a.getIdAsesor());
            cs.setString(2, a.getNombre());
            cs.setString(3, a.getApellido());
            cs.setString(4, a.getDocumento());
            cs.setString(5, a.getCorreo());
            cs.setInt(6, a.getIdInstitucion());
            cs.execute();
            return true;
        } catch (SQLException e) {
            System.out.println("Error actualizar Asesor: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int id) {
        String sql = "{CALL SP_ELIMINAR_ASESOR(?)}";
        try (Connection con = Conexion.conectar();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, id);
            cs.execute();
            return true;
        } catch (SQLException e) {
            System.out.println("Error eliminar Asesor: " + e.getMessage());
            return false;
        }
    }

    public String insertarConMensaje(AsesorPedagogico a) {
        String sql = "{CALL SP_INSERTAR_ASESOR(?, ?, ?, ?, ?, ?)}";
        try (Connection con = Conexion.conectar();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setString(1, a.getNombre()); cs.setString(2, a.getApellido());
            cs.setString(3, a.getDocumento()); cs.setString(4, a.getCorreo());
            cs.setString(5, a.getContrasena()); cs.setInt(6, a.getIdInstitucion());
            cs.execute();
            return null;
        } catch (SQLException e) { return e.getMessage(); }
    }

    public String actualizarConMensaje(AsesorPedagogico a) {
        String sql = "{CALL SP_ACTUALIZAR_ASESOR(?, ?, ?, ?, ?, ?)}";
        try (Connection con = Conexion.conectar();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, a.getIdAsesor()); cs.setString(2, a.getNombre());
            cs.setString(3, a.getApellido()); cs.setString(4, a.getDocumento());
            cs.setString(5, a.getCorreo()); cs.setInt(6, a.getIdInstitucion());
            cs.execute();
            return null;
        } catch (SQLException e) { return e.getMessage(); }
    }
}