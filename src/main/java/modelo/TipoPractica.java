package modelo;

public class TipoPractica {
    private int idTipoPrac;
    private String nombre;

    public TipoPractica(int idTipoPrac, String nombre) {
        this.idTipoPrac = idTipoPrac;
        this.nombre = nombre;
    }

    public int getIdTipoPrac() { return idTipoPrac; }
    public String getNombre() { return nombre; }

    @Override
    public String toString() { return nombre; }
}
