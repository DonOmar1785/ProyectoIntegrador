package dao;

import conexion.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AsignacionDAO {

    // ── TUTOR ↔ PRÁCTICA ──────────────────────────────────────
    public boolean asignarTutorPractica(int idTutor, int idPractica) {
        String sql = "{CALL SP_ASIGNAR_TUTOR_PRACTICA(?, ?)}";
        try (Connection con = Conexion.conectar();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, idTutor);
            cs.setInt(2, idPractica);
            cs.execute();
            return true;
        } catch (SQLException e) {
            System.out.println("Error asignar tutor-practica: " + e.getMessage());
            return false;
        }
    }

    public boolean desasignarTutorPractica(int idTutor, int idPractica) {
        String sql = "{CALL SP_DESASIGNAR_TUTOR_PRACTICA(?, ?)}";
        try (Connection con = Conexion.conectar();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, idTutor);
            cs.setInt(2, idPractica);
            cs.execute();
            return true;
        } catch (SQLException e) {
            System.out.println("Error desasignar tutor-practica: " + e.getMessage());
            return false;
        }
    }

    public List<Object[]> listarTutorPractica() {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT t.IdTutor, t.Nombre||' '||t.Apellido AS Tutor, " +
                     "p.IdPractica, p.Nombre AS Practica, p.Semestre, p.Estado " +
                     "FROM TutorPractica tp " +
                     "JOIN Tutor t ON tp.IdTutor = t.IdTutor " +
                     "JOIN Practica p ON tp.IdPractica = p.IdPractica " +
                     "ORDER BY t.Apellido, p.Semestre";
        try (Connection con = Conexion.conectar();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next())
                lista.add(new Object[]{rs.getInt(1), rs.getString(2),
                    rs.getInt(3), rs.getString(4), rs.getInt(5), rs.getString(6)});
        } catch (SQLException e) { System.out.println("Error listar tutor-practica: " + e.getMessage()); }
        return lista;
    }

    // ── ESTUDIANTE ↔ PRÁCTICA ─────────────────────────────────
    public boolean asignarEstudiantePractica(int idEstudiante, int idPractica) {
        String sql = "{CALL SP_ASIGNAR_ESTUDIANTE_PRACTICA(?, ?)}";
        try (Connection con = Conexion.conectar();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, idEstudiante);
            cs.setInt(2, idPractica);
            cs.execute();
            return true;
        } catch (SQLException e) {
            System.out.println("Error asignar estudiante-practica: " + e.getMessage());
            return false;
        }
    }

    public boolean desasignarEstudiantePractica(int idEstudiante, int idPractica) {
        String sql = "{CALL SP_DESASIG_EST_PRACTICA(?, ?)}";
        try (Connection con = Conexion.conectar();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, idEstudiante);
            cs.setInt(2, idPractica);
            cs.execute();
            return true;
        } catch (SQLException e) {
            System.out.println("Error desasignar estudiante-practica: " + e.getMessage());
            return false;
        }
    }

    public List<Object[]> listarEstudiantePractica() {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT e.IdEstudiante, e.Nombre||' '||e.Apellido AS Estudiante, " +
                     "p.IdPractica, p.Nombre AS Practica, p.Semestre, p.Estado " +
                     "FROM EstudiantePractica ep " +
                     "JOIN Estudiante e ON ep.IdEstudiante = e.IdEstudiante " +
                     "JOIN Practica p ON ep.IdPractica = p.IdPractica " +
                     "ORDER BY e.Apellido, p.Semestre";
        try (Connection con = Conexion.conectar();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next())
                lista.add(new Object[]{rs.getInt(1), rs.getString(2),
                    rs.getInt(3), rs.getString(4), rs.getInt(5), rs.getString(6)});
        } catch (SQLException e) { System.out.println("Error listar estudiante-practica: " + e.getMessage()); }
        return lista;
    }

    // ── ESTUDIANTE ↔ INSTITUCIÓN ──────────────────────────────
    public boolean asignarEstudianteInstitucion(int idEstudiante, int idInstitucion) {
        String sql = "{CALL SP_ASIGNAR_EST_INST(?, ?)}";
        try (Connection con = Conexion.conectar();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, idEstudiante);
            cs.setInt(2, idInstitucion);
            cs.execute();
            return true;
        } catch (SQLException e) {
            System.out.println("Error asignar estudiante-institucion: " + e.getMessage());
            return false;
        }
    }

    public boolean desasignarEstudianteInstitucion(int idEstudiante, int idInstitucion) {
        String sql = "{CALL SP_DESASIGNAR_EST_INST(?, ?)}";
        try (Connection con = Conexion.conectar();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, idEstudiante);
            cs.setInt(2, idInstitucion);
            cs.execute();
            return true;
        } catch (SQLException e) {
            System.out.println("Error desasignar estudiante-institucion: " + e.getMessage());
            return false;
        }
    }

    public List<Object[]> listarEstudianteInstitucion() {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT e.IdEstudiante, e.Nombre||' '||e.Apellido AS Estudiante, " +
                     "i.IdInstitucion, i.Nombre AS Institucion, i.Tipo " +
                     "FROM EstudianteInstitucion ei " +
                     "JOIN Estudiante e ON ei.IdEstudiante = e.IdEstudiante " +
                     "JOIN Institucion i ON ei.IdInstitucion = i.IdInstitucion " +
                     "ORDER BY e.Apellido, i.Nombre";
        try (Connection con = Conexion.conectar();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next())
                lista.add(new Object[]{rs.getInt(1), rs.getString(2),
                    rs.getInt(3), rs.getString(4), rs.getString(5)});
        } catch (SQLException e) { System.out.println("Error listar estudiante-institucion: " + e.getMessage()); }
        return lista;
    }

    // ── ASESOR ↔ ESTUDIANTE ───────────────────────────────────
    public boolean asignarAsesorEstudiante(int idAsesor, int idEstudiante) {
        String sql = "{CALL SP_ASIGNAR_ASESOR_ESTUDIANTE(?, ?)}";
        try (Connection con = Conexion.conectar();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, idAsesor);
            cs.setInt(2, idEstudiante);
            cs.execute();
            return true;
        } catch (SQLException e) {
            System.out.println("Error asignar asesor-estudiante: " + e.getMessage());
            return false;
        }
    }

    public boolean desasignarAsesorEstudiante(int idAsesor, int idEstudiante) {
        String sql = "{CALL SP_DESASIG_ASESOR_EST(?, ?)}";
        try (Connection con = Conexion.conectar();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, idAsesor);
            cs.setInt(2, idEstudiante);
            cs.execute();
            return true;
        } catch (SQLException e) {
            System.out.println("Error desasignar asesor-estudiante: " + e.getMessage());
            return false;
        }
    }

    public List<Object[]> listarAsesorEstudiante() {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT a.IdAsesor, a.Nombre||' '||a.Apellido AS Asesor, " +
                     "i.Nombre AS Institucion, " +
                     "e.IdEstudiante, e.Nombre||' '||e.Apellido AS Estudiante " +
                     "FROM AsesorEstudiante ae " +
                     "JOIN AsesorPedagogico a ON ae.IdAsesor = a.IdAsesor " +
                     "JOIN Institucion i ON a.IdInstitucion = i.IdInstitucion " +
                     "JOIN Estudiante e ON ae.IdEstudiante = e.IdEstudiante " +
                     "ORDER BY a.Apellido, e.Apellido";
        try (Connection con = Conexion.conectar();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next())
                lista.add(new Object[]{rs.getInt(1), rs.getString(2),
                    rs.getString(3), rs.getInt(4), rs.getString(5)});
        } catch (SQLException e) { System.out.println("Error listar asesor-estudiante: " + e.getMessage()); }
        return lista;
    }
}