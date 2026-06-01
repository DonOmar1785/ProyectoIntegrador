package util;

import javax.swing.*;
import java.awt.*;
import java.util.Calendar;

/**
 * Componente reutilizable para seleccionar fecha con 3 combos:
 * día, mes y año. Se usa igual que cualquier JPanel.
 * 
 * Uso:
 *   SelectorFecha selector = new SelectorFecha();
 *   java.util.Date fecha = selector.getFecha(); // null si no seleccionó
 *   selector.setFecha(unaDate);                 // para precargar al editar
 */
public class SelectorFecha extends JPanel {

    private JComboBox<String> cmbDia;
    private JComboBox<String> cmbMes;
    private JComboBox<String> cmbAnio;

    private static final String[] MESES = {
        "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    };

    public SelectorFecha() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 4, 0));
        setOpaque(false);

        // Días 1-31
        String[] dias = new String[32];
        dias[0] = "Día";
        for (int i = 1; i <= 31; i++) dias[i] = String.valueOf(i);
        cmbDia = new JComboBox<>(dias);

        // Meses
        String[] meses = new String[13];
        meses[0] = "Mes";
        System.arraycopy(MESES, 0, meses, 1, 12);
        cmbMes = new JComboBox<>(meses);

        // Años: 2020–2035 (cubre digitalización de registros históricos y proyección del programa)
        final int ANIO_MIN = 2020;
        final int ANIO_MAX = 2035;
        int totalAnios = ANIO_MAX - ANIO_MIN + 1;
        String[] anios = new String[totalAnios + 1];
        anios[0] = "Año";
        for (int i = 1; i <= totalAnios; i++) anios[i] = String.valueOf(ANIO_MIN + i - 1);
        cmbAnio = new JComboBox<>(anios);

        // Actualizar días válidos al cambiar mes/año
        cmbMes.addActionListener(e -> actualizarDias());
        cmbAnio.addActionListener(e -> actualizarDias());

        add(cmbDia);
        add(new JLabel("/"));
        add(cmbMes);
        add(new JLabel("/"));
        add(cmbAnio);
    }

    /** Ajusta los días disponibles según el mes y año seleccionados */
    private void actualizarDias() {
        int mesIdx = cmbMes.getSelectedIndex(); // 0 = "Mes", 1 = Enero...
        int anioIdx = cmbAnio.getSelectedIndex();
        if (mesIdx == 0 || anioIdx == 0) return;

        int anio = Integer.parseInt((String) cmbAnio.getSelectedItem());
        Calendar cal = Calendar.getInstance();
        cal.set(anio, mesIdx - 1, 1);
        int maxDias = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        int diaActual = cmbDia.getSelectedIndex(); // guardar selección
        cmbDia.removeAllItems();
        cmbDia.addItem("Día");
        for (int i = 1; i <= maxDias; i++) cmbDia.addItem(String.valueOf(i));

        // Restaurar selección si sigue siendo válida
        if (diaActual > 0 && diaActual <= maxDias) cmbDia.setSelectedIndex(diaActual);
    }

    /**
     * Retorna la fecha seleccionada como java.util.Date.
     * Retorna null si algún combo no está seleccionado.
     */
    public java.util.Date getFecha() {
        if (cmbDia.getSelectedIndex() == 0 ||
            cmbMes.getSelectedIndex() == 0 ||
            cmbAnio.getSelectedIndex() == 0) return null;

        int dia  = Integer.parseInt((String) cmbDia.getSelectedItem());
        int mes  = cmbMes.getSelectedIndex() - 1; // 0-based para Calendar
        int anio = Integer.parseInt((String) cmbAnio.getSelectedItem());

        Calendar cal = Calendar.getInstance();
        cal.set(anio, mes, dia, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * Precarga una fecha (útil al seleccionar fila para editar).
     */
    public void setFecha(java.util.Date fecha) {
        if (fecha == null) { limpiar(); return; }
        Calendar cal = Calendar.getInstance();
        cal.setTime(fecha);
        int dia  = cal.get(Calendar.DAY_OF_MONTH);
        int mes  = cal.get(Calendar.MONTH) + 1; // 1-based para el combo
        int anio = cal.get(Calendar.YEAR);

        // Seleccionar año
        for (int i = 0; i < cmbAnio.getItemCount(); i++) {
            if (cmbAnio.getItemAt(i).equals(String.valueOf(anio))) {
                cmbAnio.setSelectedIndex(i); break;
            }
        }
        cmbMes.setSelectedIndex(mes); // 1=Enero coincide con el índice
        cmbDia.setSelectedItem(String.valueOf(dia));
    }

    /** Resetea los tres combos al placeholder */
    public void limpiar() {
        cmbDia.setSelectedIndex(0);
        cmbMes.setSelectedIndex(0);
        cmbAnio.setSelectedIndex(0);
    }
}