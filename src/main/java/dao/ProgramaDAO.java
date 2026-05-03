package dao;

import conexion.Conexion;
import modelo.Programa;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProgramaDAO {

    // INSERT
    public boolean insertar(Programa p) {
        String sql = "INSERT INTO Programa (IdPrograma, Nombre, Facultad, Estado) VALUES (SEQ_PROGRAMA.NEXTVAL, ?, ?, ?)";
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getFacultad());
            ps.setString(3, p.getEstado());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error insertar Programa: " + e.getMessage());
            return false;
        }
    }

    // SELECT ALL
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

    // UPDATE
    public boolean actualizar(Programa p) {
        String sql = "UPDATE Programa SET Nombre=?, Facultad=?, Estado=? WHERE IdPrograma=?";
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getFacultad());
            ps.setString(3, p.getEstado());
            ps.setInt(4, p.getIdPrograma());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error actualizar Programa: " + e.getMessage());
            return false;
        }
    }

    // DELETE
    public boolean eliminar(int id) {
        String sql = "DELETE FROM Programa WHERE IdPrograma=?";
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error eliminar Programa: " + e.getMessage());
            return false;
        }
    }
}
