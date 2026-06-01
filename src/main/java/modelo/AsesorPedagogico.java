package modelo;

public class AsesorPedagogico {
    private int idAsesor;
    private String nombre;
    private String apellido;
    private String documento;
    private String correo;
    private String contrasena;
    private int idInstitucion;

    public AsesorPedagogico() {}

    public AsesorPedagogico(int idAsesor, String nombre, String apellido, String documento,
                             String correo, String contrasena, int idInstitucion) {
        this.idAsesor = idAsesor;
        this.nombre = nombre;
        this.apellido = apellido;
        this.documento = documento;
        this.correo = correo;
        this.contrasena = contrasena;
        this.idInstitucion = idInstitucion;
    }

    public int getIdAsesor() { return idAsesor; }
    public void setIdAsesor(int v) { this.idAsesor = v; }
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
    public int getIdInstitucion() { return idInstitucion; }
    public void setIdInstitucion(int v) { this.idInstitucion = v; }

    @Override
    public String toString() { return nombre + " " + apellido; }
}
