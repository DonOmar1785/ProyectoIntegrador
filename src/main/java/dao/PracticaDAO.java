package dao;

import conexion.Conexion;
import modelo.Practica;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PracticaDAO {

    public boolean insertar(Practica p) {
        String sql = "{CALL SP_INSERTAR_PRACTICA(?, ?, ?, ?, ?, ?, ?)}";
        try (Connection con = Conexion.conectar();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setString(1, p.getNombre());
            cs.setInt(2, p.getSemestre());
            cs.setDate(3, new java.sql.Date(p.getFechaInicio().getTime()));
            cs.setDate(4, new java.sql.Date(p.getFechaFin().getTime()));
            cs.setString(5, p.getEstado());
            cs.setInt(6, p.getIdPrograma());
            cs.setInt(7, p.getIdTipoPrac());
            cs.execute();
            return true;
        } catch (SQLException e) {
            System.out.println("Error insertar Practica: " + e.getMessage());
            return false;
        }
    }

    public List<Practica> listar() {
        List<Practica> lista = new ArrayList<>();
        String sql = "SELECT IdPractica, Nombre, Semestre, FechaInicio, FechaFin, Estado, IdPrograma, IdTipoPrac " +
                     "FROM Practica ORDER BY IdPractica";
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

    public boolean actualizar(Practica p) {
        String sql = "{CALL SP_ACTUALIZAR_PRACTICA(?, ?, ?, ?, ?, ?, ?, ?)}";
        try (Connection con = Conexion.conectar();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, p.getIdPractica());
            cs.setString(2, p.getNombre());
            cs.setInt(3, p.getSemestre());
            cs.setDate(4, new java.sql.Date(p.getFechaInicio().getTime()));
            cs.setDate(5, new java.sql.Date(p.getFechaFin().getTime()));
            cs.setString(6, p.getEstado());
            cs.setInt(7, p.getIdPrograma());
            cs.setInt(8, p.getIdTipoPrac());
            cs.execute();
            return true;
        } catch (SQLException e) {
            System.out.println("Error actualizar Practica: " + e.getMessage());
            return false;
        }
    }

    public boolean cambiarEstado(int idPractica, String nuevoEstado) {
        String sql = "{CALL SP_CAMBIAR_ESTADO_PRACTICA(?, ?)}";
        try (Connection con = Conexion.conectar();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, idPractica);
            cs.setString(2, nuevoEstado);
            cs.execute();
            return true;
        } catch (SQLException e) {
            System.out.println("Error cambiar estado Practica: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int id) {
        String sql = "{CALL SP_ELIMINAR_PRACTICA(?)}";
        try (Connection con = Conexion.conectar();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, id);
            cs.execute();
            return true;
        } catch (SQLException e) {
            System.out.println("Error eliminar Practica: " + e.getMessage());
            return false;
        }
    }
}