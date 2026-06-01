package dao;

import conexion.Conexion;
import modelo.Programa;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProgramaDAO {

    public boolean insertar(Programa p) {
        String sql = "{CALL SP_INSERTAR_PROGRAMA(?, ?, ?)}";
        try (Connection con = Conexion.conectar();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setString(1, p.getNombre());
            cs.setString(2, p.getFacultad());
            cs.setString(3, p.getEstado());
            cs.execute();
            return true;
        } catch (SQLException e) {
            System.out.println("Error insertar Programa: " + e.getMessage());
            return false;
        }
    }

    public List<Programa> listar() {
        List<Programa> lista = new ArrayList<>();
        String sql = "SELECT * FROM Programa ORDER BY IdPrograma";
        try (Connection con = Conexion.conectar();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Programa(
                    rs.getInt("IdPrograma"),
                    rs.getString("Nombre"),
                    rs.getString("Facultad"),
                    rs.getString("Estado")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error listar Programas: " + e.getMessage());
        }
        return lista;
    }

    public boolean actualizar(Programa p) {
        String sql = "{CALL SP_ACTUALIZAR_PROGRAMA(?, ?, ?, ?)}";
        try (Connection con = Conexion.conectar();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, p.getIdPrograma());
            cs.setString(2, p.getNombre());
            cs.setString(3, p.getFacultad());
            cs.setString(4, p.getEstado());
            cs.execute();
            return true;
        } catch (SQLException e) {
            System.out.println("Error actualizar Programa: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int id) {
        String sql = "{CALL SP_ELIMINAR_PROGRAMA(?)}";
        try (Connection con = Conexion.conectar();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, id);
            cs.execute();
            return true;
        } catch (SQLException e) {
            System.out.println("Error eliminar Programa: " + e.getMessage());
            return false;
        }
    }
}