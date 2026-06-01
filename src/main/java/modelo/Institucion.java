package modelo;

public class Institucion {
    private int idInstitucion;
    private String nombre;
    private String tipo;
    private String direccion;
    private String contacto;

    public Institucion() {}

    public Institucion(int idInstitucion, String nombre, String tipo, String direccion, String contacto) {
        this.idInstitucion = idInstitucion;
        this.nombre = nombre;
        this.tipo = tipo;
        this.direccion = direccion;
        this.contacto = contacto;
    }

    public int getIdInstitucion() { return idInstitucion; }
    public void setIdInstitucion(int v) { this.idInstitucion = v; }
    public String getNombre() { return nombre; }
    public void setNombre(String v) { this.nombre = v; }
    public String getTipo() { return tipo; }
    public void setTipo(String v) { this.tipo = v; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String v) { this.direccion = v; }
    public String getContacto() { return contacto; }
    public void setContacto(String v) { this.contacto = v; }

    @Override
    public String toString() { return nombre; }
}