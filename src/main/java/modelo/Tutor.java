package modelo;

public class Tutor {
    private int idTutor;
    private String nombre;
    private String apellido;
    private String documento;
    private String correo;
    private String contrasena;
    private int idPrograma;

    public Tutor() {}

    public Tutor(int idTutor, String nombre, String apellido, String documento,
                 String correo, String contrasena, int idPrograma) {
        this.idTutor = idTutor;
        this.nombre = nombre;
        this.apellido = apellido;
        this.documento = documento;
        this.correo = correo;
        this.contrasena = contrasena;
        this.idPrograma = idPrograma;
    }

    public int getIdTutor() { return idTutor; }
    public void setIdTutor(int v) { this.idTutor = v; }
    public String getNombre() { return nombre; }
    public void setNombre(String v) { this.nombre = v; }
    public String getApellido() { return apellido; }
    public void setApellido(String v) { this.apellido = v; }
    public String getDocumento() { return documento; }
    public void setDocumento(String v) { this.documento = v; }
    public String getCorreo() { return correo; }
    public void setCorreo(String v) { this.correo = v; }
    public String getContrasena() { return contrasena; }
    public void setContrasena(String v) { this.contrasena = v; }
    public int getIdPrograma() { return idPrograma; }
    public void setIdPrograma(int v) { this.idPrograma = v; }

    @Override
    public String toString() { return nombre + " " + apellido; }
}
