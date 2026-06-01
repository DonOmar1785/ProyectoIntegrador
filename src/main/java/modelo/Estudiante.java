package modelo;

public class Estudiante {
    private int idEstudiante;
    private String nombre;
    private String apellido;
    private String documento;
    private String correo;
    private int idPrograma;

    public Estudiante(int idEstudiante, String nombre, String apellido,
                      String documento, String correo, int idPrograma) {
        this.idEstudiante = idEstudiante;
        this.nombre = nombre;
        this.apellido = apellido;
        this.documento = documento;
        this.correo = correo;
        this.idPrograma = idPrograma;
    }

    public int getIdEstudiante() { return idEstudiante; }
    public String getNombre()    { return nombre; }
    public String getApellido()  { return apellido; }
    public String getDocumento() { return documento; }
    public String getCorreo()    { return correo; }
    public int getIdPrograma()   { return idPrograma; }

    @Override
    public String toString() { return nombre + " " + apellido; }
}
