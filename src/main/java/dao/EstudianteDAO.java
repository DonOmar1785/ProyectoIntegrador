package dao;

import conexion.Conexion;
import modelo.Estudiante;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EstudianteDAO {

    public List<Estudiante> listar() {
        List<Estudiante> lista = new ArrayList<>();
        String sql = "SELECT IdEstudiante, Nombre, Apellido, Documento, Correo, IdPrograma " +
                     "FROM Estudiante ORDER BY Apellido";
        try (Connection con = Conexion.conectar();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next())
                lista.add(new Estudiante(rs.getInt(1), rs.getString(2),
                    rs.getString(3), rs.getString(4), rs.getString(5), rs.getInt(6)));
        } catch (SQLException e) { System.out.println("Error listar Estudiantes: " + e.getMessage()); }
        return lista;
    }
}
