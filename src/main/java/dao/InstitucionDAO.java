package dao;

import conexion.Conexion;
import modelo.Institucion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InstitucionDAO {

    public boolean insertar(Institucion i) {
        String sql = "{CALL SP_INSERTAR_INSTITUCION(?, ?, ?, ?)}";
        try (Connection con = Conexion.conectar();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setString(1, i.getNombre());
            cs.setString(2, i.getTipo());
            cs.setString(3, i.getDireccion());
            cs.setString(4, i.getContacto());
            cs.execute();
            return true;
        } catch (SQLException e) {
            System.out.println("Error insertar Institución: " + e.getMessage());
            return false;
        }
    }

    public List<Institucion> listar() {
        List<Institucion> lista = new ArrayList<>();
        String sql = "SELECT * FROM Institucion ORDER BY IdInstitucion";
        try (Connection con = Conexion.conectar();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Institucion(
                    rs.getInt("IdInstitucion"),
                    rs.getString("Nombre"),
                    rs.getString("Tipo"),
                    rs.getString("Direccion"),
                    rs.getString("Contacto")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error listar Instituciones: " + e.getMessage());
        }
        return lista;
    }

    public boolean actualizar(Institucion i) {
        String sql = "{CALL SP_ACTUALIZAR_INSTITUCION(?, ?, ?, ?, ?)}";
        try (Connection con = Conexion.conectar();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, i.getIdInstitucion());
            cs.setString(2, i.getNombre());
            cs.setString(3, i.getTipo());
            cs.setString(4, i.getDireccion());
            cs.setString(5, i.getContacto());
            cs.execute();
            return true;
        } catch (SQLException e) {
            System.out.println("Error actualizar Institución: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int id) {
        String sql = "{CALL SP_ELIMINAR_INSTITUCION(?)}";
        try (Connection con = Conexion.conectar();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, id);
            cs.execute();
            return true;
        } catch (SQLException e) {
            System.out.println("Error eliminar Institución: " + e.getMessage());
            return false;
        }
    }
}
