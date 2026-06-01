package modelo;

import java.util.Date;

public class Practica {
    private int idPractica;
    private String nombre;
    private int semestre;
    private Date fechaInicio;
    private Date fechaFin;
    private String estado;
    private int idPrograma;
    private int idTipoPrac;

    public Practica() {}

    public Practica(int idPractica, String nombre, int semestre, Date fechaInicio,
                    Date fechaFin, String estado, int idPrograma, int idTipoPrac) {
        this.idPractica = idPractica;
        this.nombre = nombre;
        this.semestre = semestre;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.estado = estado;
        this.idPrograma = idPrograma;
        this.idTipoPrac = idTipoPrac;
    }

    public int getIdPractica() { return idPractica; }
    public void setIdPractica(int idPractica) { this.idPractica = idPractica; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getSemestre() { return semestre; }
    public void setSemestre(int semestre) { this.semestre = semestre; }

    public Date getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(Date fechaInicio) { this.fechaInicio = fechaInicio; }

    public Date getFechaFin() { return fechaFin; }
    public void setFechaFin(Date fechaFin) { this.fechaFin = fechaFin; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public int getIdPrograma() { return idPrograma; }
    public void setIdPrograma(int idPrograma) { this.idPrograma = idPrograma; }

    public int getIdTipoPrac() { return idTipoPrac; }
    public void setIdTipoPrac(int idTipoPrac) { this.idTipoPrac = idTipoPrac; }

    @Override
    public String toString() { return nombre + " (Sem. " + semestre + ")"; }
}