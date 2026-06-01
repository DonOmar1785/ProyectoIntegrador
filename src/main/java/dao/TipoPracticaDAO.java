package dao;

import conexion.Conexion;
import modelo.TipoPractica;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TipoPracticaDAO {

    public List<TipoPractica> listar() {
        List<TipoPractica> lista = new ArrayList<>();
        String sql = "SELECT IdTipoPrac, Nombre FROM TipoPractica ORDER BY IdTipoPrac";
        try (Connection con = Conexion.conectar();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new TipoPractica(rs.getInt("IdTipoPrac"), rs.getString("Nombre")));
            }
        } catch (SQLException e) {
            System.out.println("Error listar TipoPractica: " + e.getMessage());
        }
        return lista;
    }
}
