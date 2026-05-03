package modelo;

public class Programa {
    private int idPrograma;
    private String nombre;
    private String facultad;
    private String estado;

    public Programa() {}

    public Programa(int idPrograma, String nombre, String facultad, String estado) {
        this.idPrograma = idPrograma;
        this.nombre = nombre;
        this.facultad = facultad;
        this.estado = estado;
    }

    public int getIdPrograma() { return idPrograma; }
    public void setIdPrograma(int idPrograma) { this.idPrograma = idPrograma; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getFacultad() { return facultad; }
    public void setFacultad(String facultad) { this.facultad = facultad; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    @Override
    public String toString() { return nombre; }
}
