package dao;

import conexion.Conexion;
import modelo.UsuarioLogin;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioLoginDAO {

    public UsuarioLogin login(String correo, String contrasena) {
        String sql = "SELECT * FROM Usuario WHERE Correo=? AND Contrasena=? AND Estado='Activo'";
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, correo);
            ps.setString(2, contrasena);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new UsuarioLogin(
                    rs.getInt("IdUsuario"),
                    rs.getString("Nombre"),
                    rs.getString("Apellido"),
                    rs.getString("Documento"),
                    rs.getString("Correo"),
                    rs.getString("Contrasena"),
                    rs.getString("Estado"),
                    rs.getString("Rol")
                );
            }
        } catch (SQLException e) {
            System.out.println("Error login: " + e.getMessage());
        }
        return null;
    }

    /**
     * Inserta en la tabla Usuario Y en la tabla específica según el rol.
     * @param u       datos del usuario
     * @param idExtra IdPrograma si ESTUDIANTE o TUTOR; IdInstitucion si ASESOR; -1 si ADMIN
     */
    public boolean insertar(UsuarioLogin u, int idExtra) {
        String sqlUsuario = "{CALL SP_INSERTAR_USUARIO(?, ?, ?, ?, ?, ?, ?)}";
        try (Connection con = Conexion.conectar();
             CallableStatement cs = con.prepareCall(sqlUsuario)) {
            cs.setString(1, u.getNombre());
            cs.setString(2, u.getApellido());
            cs.setString(3, u.getDocumento());
            cs.setString(4, u.getCorreo());
            cs.setString(5, u.getContrasena());
            cs.setString(6, u.getEstado());
            cs.setString(7, u.getRol());
            cs.execute();
        } catch (SQLException e) {
            System.out.println("Error insertar Usuario: " + e.getMessage());
            return false;
        }

        try (Connection con = Conexion.conectar()) {
            CallableStatement cs;
            switch (u.getRol()) {
                case "ESTUDIANTE":
                    cs = con.prepareCall("{CALL SP_INSERTAR_ESTUDIANTE(?, ?, ?, ?, ?, ?)}");
                    cs.setString(1, u.getNombre());
                    cs.setString(2, u.getApellido());
                    cs.setString(3, u.getDocumento());
                    cs.setString(4, u.getCorreo());
                    cs.setString(5, u.getContrasena());
                    cs.setInt(6, idExtra);
                    cs.execute();
                    break;
                case "TUTOR":
                    cs = con.prepareCall("{CALL SP_INSERTAR_TUTOR(?, ?, ?, ?, ?, ?)}");
                    cs.setString(1, u.getNombre());
                    cs.setString(2, u.getApellido());
                    cs.setString(3, u.getDocumento());
                    cs.setString(4, u.getCorreo());
                    cs.setString(5, u.getContrasena());
                    cs.setInt(6, idExtra);
                    cs.execute();
                    break;
                case "ASESOR":
                    cs = con.prepareCall("{CALL SP_INSERTAR_ASESOR(?, ?, ?, ?, ?, ?)}");
                    cs.setString(1, u.getNombre());
                    cs.setString(2, u.getApellido());
                    cs.setString(3, u.getDocumento());
                    cs.setString(4, u.getCorreo());
                    cs.setString(5, u.getContrasena());
                    cs.setInt(6, idExtra);
                    cs.execute();
                    break;
                default:
                    break; // ADMIN: solo en tabla Usuario
            }
            return true;
        } catch (SQLException e) {
            System.out.println("Error insertar tabla específica (" + u.getRol() + "): " + e.getMessage());
            return false;
        }
    }

    public boolean insertar(UsuarioLogin u) {
        return insertar(u, -1);
    }

    public List<UsuarioLogin> listar() {
        List<UsuarioLogin> lista = new ArrayList<>();
        String sql = "SELECT * FROM Usuario ORDER BY IdUsuario";
        try (Connection con = Conexion.conectar();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new UsuarioLogin(
                    rs.getInt("IdUsuario"),
                    rs.getString("Nombre"),
                    rs.getString("Apellido"),
                    rs.getString("Documento"),
                    rs.getString("Correo"),
                    rs.getString("Contrasena"),
                    rs.getString("Estado"),
                    rs.getString("Rol")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error listar Usuarios: " + e.getMessage());
        }
        return lista;
    }

    public boolean actualizar(UsuarioLogin u) {
        String sql = "{CALL SP_ACTUALIZAR_USUARIO(?, ?, ?, ?, ?, ?, ?)}";
        try (Connection con = Conexion.conectar();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, u.getIdUsuario());
            cs.setString(2, u.getNombre());
            cs.setString(3, u.getApellido());
            cs.setString(4, u.getDocumento());
            cs.setString(5, u.getCorreo());
            cs.setString(6, u.getEstado());
            cs.setString(7, u.getRol());
            cs.execute();
            return true;
        } catch (SQLException e) {
            System.out.println("Error actualizar Usuario: " + e.getMessage());
            return false;
        }
    }

    public boolean cambiarEstado(int id, String estado) {
        String sql = "{CALL SP_CAMBIAR_ESTADO_USUARIO(?, ?)}";
        try (Connection con = Conexion.conectar();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, id);
            cs.setString(2, estado);
            cs.execute();
            return true;
        } catch (SQLException e) {
            System.out.println("Error cambiar estado Usuario: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int id) {
        String sql = "{CALL SP_ELIMINAR_USUARIO(?)}";
        try (Connection con = Conexion.conectar();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, id);
            cs.execute();
            return true;
        } catch (SQLException e) {
            System.out.println("Error eliminar Usuario: " + e.getMessage());
            return false;
        }
    }
    // Método que retorna null si OK, o el mensaje de error SQL si falla
    public String insertarConMensaje(UsuarioLogin u, int idExtra) {
        String sqlUsuario = "{CALL SP_INSERTAR_USUARIO(?, ?, ?, ?, ?, ?, ?)}";
        try (Connection con = Conexion.conectar();
             CallableStatement cs = con.prepareCall(sqlUsuario)) {
            cs.setString(1, u.getNombre());
            cs.setString(2, u.getApellido());
            cs.setString(3, u.getDocumento());
            cs.setString(4, u.getCorreo());
            cs.setString(5, u.getContrasena());
            cs.setString(6, u.getEstado());
            cs.setString(7, u.getRol());
            cs.execute();
        } catch (SQLException e) {
            return e.getMessage();
        }

        try (Connection con = Conexion.conectar()) {
            CallableStatement cs;
            switch (u.getRol()) {
                case "ESTUDIANTE":
                    cs = con.prepareCall("{CALL SP_INSERTAR_ESTUDIANTE(?, ?, ?, ?, ?, ?)}");
                    cs.setString(1, u.getNombre()); cs.setString(2, u.getApellido());
                    cs.setString(3, u.getDocumento()); cs.setString(4, u.getCorreo());
                    cs.setString(5, u.getContrasena()); cs.setInt(6, idExtra);
                    cs.execute(); break;
                case "TUTOR":
                    cs = con.prepareCall("{CALL SP_INSERTAR_TUTOR(?, ?, ?, ?, ?, ?)}");
                    cs.setString(1, u.getNombre()); cs.setString(2, u.getApellido());
                    cs.setString(3, u.getDocumento()); cs.setString(4, u.getCorreo());
                    cs.setString(5, u.getContrasena()); cs.setInt(6, idExtra);
                    cs.execute(); break;
                case "ASESOR":
                    cs = con.prepareCall("{CALL SP_INSERTAR_ASESOR(?, ?, ?, ?, ?, ?)}");
                    cs.setString(1, u.getNombre()); cs.setString(2, u.getApellido());
                    cs.setString(3, u.getDocumento()); cs.setString(4, u.getCorreo());
                    cs.setString(5, u.getContrasena()); cs.setInt(6, idExtra);
                    cs.execute(); break;
                default: break;
            }
            return null; // OK
        } catch (SQLException e) {
            return e.getMessage();
        }
    }
}