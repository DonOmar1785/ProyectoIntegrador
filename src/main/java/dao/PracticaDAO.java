package dao;

import conexion.Conexion;
import modelo.Practica;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PracticaDAO {

    // INSERT
    public boolean insertar(Practica p) {
        String sql = "INSERT INTO Practica (IdPractica, Nombre, Semestre, FechaInicio, FechaFin, Estado, IdPrograma, IdTipoPrac) " +
                     "VALUES (SEQ_PRACTICA.NEXTVAL, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getNombre());
            ps.setInt(2, p.getSemestre());
            ps.setDate(3, new java.sql.Date(p.getFechaInicio().getTime()));
            ps.setDate(4, new java.sql.Date(p.getFechaFin().getTime()));
            ps.setString(5, p.getEstado());
            ps.setInt(6, p.getIdPrograma());
            ps.setInt(7, p.getIdTipoPrac());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error insertar Practica: " + e.getMessage());
            return false;
        }
    }

    // SELECT ALL
    public List<Practica> listar() {
        List<Practica> lista = new ArrayList<>();
        String sql = "SELECT p.IdPractica, p.Nombre, p.Semestre, p.FechaInicio, p.FechaFin, p.Estado, " +
                     "p.IdPrograma, p.IdTipoPrac FROM Practica p ORDER BY p.IdPractica";
        try (Connection con = Conexion.conectar();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Practica(
                    rs.getInt("IdPractica"),
                    rs.getString("Nombre"),
                    rs.getInt("Semestre"),
                    rs.getDate("FechaInicio"),
                    rs.getDate("FechaFin"),
                    rs.getString("Estado"),
                    rs.getInt("IdPrograma"),
                    rs.getInt("IdTipoPrac")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error listar Practicas: " + e.getMessage());
        }
        return lista;
    }

    // UPDATE estado (abrir/cerrar)
    public boolean cambiarEstado(int idPractica, String nuevoEstado) {
        String sql = "UPDATE Practica SET Estado=? WHERE IdPractica=?";
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setInt(2, idPractica);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error cambiar estado Practica: " + e.getMessage());
            return false;
        }
    }

    // UPDATE completo
    public boolean actualizar(Practica p) {
        String sql = "UPDATE Practica SET Nombre=?, Semestre=?, FechaInicio=?, FechaFin=?, Estado=?, IdPrograma=?, IdTipoPrac=? " +
                     "WHERE IdPractica=?";
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getNombre());
            ps.setInt(2, p.getSemestre());
            ps.setDate(3, new java.sql.Date(p.getFechaInicio().getTime()));
            ps.setDate(4, new java.sql.Date(p.getFechaFin().getTime()));
            ps.setString(5, p.getEstado());
            ps.setInt(6, p.getIdPrograma());
            ps.setInt(7, p.getIdTipoPrac());
            ps.setInt(8, p.getIdPractica());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error actualizar Practica: " + e.getMessage());
            return false;
        }
    }

    // DELETE
    public boolean eliminar(int id) {
        String sql = "DELETE FROM Practica WHERE IdPractica=?";
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error eliminar Practica: " + e.getMessage());
            return false;
        }
    }
}
