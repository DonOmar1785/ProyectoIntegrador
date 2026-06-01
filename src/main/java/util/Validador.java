package util;

public class Validador {

    public static String validarNombre(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty())
            return campo + " es obligatorio.";
        if (valor.trim().length() < 2)
            return campo + " debe tener al menos 2 caracteres.";
        if (!valor.trim().matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+"))
            return campo + " solo puede contener letras y espacios.";
        return null;
    }

    public static String validarDocumento(String documento) {
        if (documento == null || documento.trim().isEmpty())
            return "El documento es obligatorio.";
        if (!documento.trim().matches("\\d+"))
            return "El documento solo puede contener dígitos.";
        int len = documento.trim().length();
        if (len < 7 || len > 15)
            return "El documento debe tener entre 7 y 15 dígitos.";
        return null;
    }

    public static String validarCorreo(String correo) {
        if (correo == null || correo.trim().isEmpty())
            return "El correo es obligatorio.";
        if (!correo.trim().matches("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$"))
            return "El correo ingresado no es válido (debe tener formato usuario@dominio.ext).";
        return null;
    }

    public static String validarContrasena(String contrasena) {
        if (contrasena == null || contrasena.trim().isEmpty())
            return "La contraseña es obligatoria.";
        if (contrasena.trim().length() < 6)
            return "La contraseña debe tener al menos 6 caracteres.";
        return null;
    }

    public static String validarTexto(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty())
            return campo + " es obligatorio.";
        return null;
    }

    /**
     * Valida que el semestre esté entre 1 y 8 (máximo permitido por el programa).
     */
    public static String validarSemestre(int semestre) {
        if (semestre < 1 || semestre > 8)
            return "El semestre debe estar entre 1 y 8.";
        return null;
    }

    /**
     * Valida que las fechas de una práctica sean coherentes y estén en el rango permitido.
     * Rango: 2020-2035 (cubre registros históricos y proyección futura).
     */
    public static String validarFechasPractica(java.util.Date inicio, java.util.Date fin) {
        if (inicio == null || fin == null)
            return "Debe seleccionar fecha de inicio y fecha de fin.";
        if (fin.before(inicio) || fin.equals(inicio))
            return "La fecha de fin debe ser posterior a la fecha de inicio.";
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(2020, 0, 1, 0, 0, 0);
        java.util.Date limiteMin = cal.getTime();
        cal.set(2035, 11, 31, 23, 59, 59);
        java.util.Date limiteMax = cal.getTime();
        if (inicio.before(limiteMin))
            return "La fecha de inicio no puede ser anterior al año 2020.";
        if (fin.after(limiteMax))
            return "La fecha de fin no puede ser posterior al año 2035.";
        return null;
    }

    /**
     * Sugiere el tipo de práctica según el semestre:
     * Semestres 1-3 → Práctica Pedagógica
     * Semestres 4-8 → Práctica Pedagógica Investigativa
     */
    public static String sugerirTipoPractica(int semestre) {
        if (semestre >= 1 && semestre <= 3)
            return "Práctica Pedagógica";
        else if (semestre >= 4 && semestre <= 8)
            return "Práctica Pedagógica Investigativa";
        return null;
    }

    // Interpreta errores de Oracle y devuelve mensaje amigable
    public static String interpretarErrorOracle(String mensajeSQL) {
        if (mensajeSQL == null) return "Error desconocido.";
        String msg = mensajeSQL.toUpperCase();

        if (msg.contains("ORA-00001") || msg.contains("UNIQUE") || msg.contains("UK_") || msg.contains("UQ_")) {
            if (msg.contains("DOCUMENTO") || msg.contains("DOC"))
                return "El documento ingresado ya está registrado en el sistema.";
            if (msg.contains("CORREO") || msg.contains("EMAIL"))
                return "El correo ingresado ya está registrado en el sistema.";
            return "Ya existe un registro con esos datos. Verificá el documento o correo.";
        }
        if (msg.contains("ORA-02291") || msg.contains("PARENT KEY NOT FOUND"))
            return "El programa o institución seleccionado no existe.";
        if (msg.contains("ORA-20001"))
            return "No se puede eliminar: el programa tiene prácticas asociadas.";
        if (msg.contains("ORA-20002"))
            return "Estado inválido. Use: Activo o Inactivo.";
        if (msg.contains("ORA-20003"))
            return "Rol inválido.";
        if (msg.contains("ORA-20005"))
            return "Ya existe un estudiante con ese número de documento.";
        if (msg.contains("ORA-20006"))
            return "La fecha de fin no puede ser anterior a la fecha de inicio.";
        if (msg.contains("ORA-20007"))
            return "Estado de práctica inválido. Use: Abierta o Cerrada.";
        if (msg.contains("ORA-20008"))
            return "Ya existe una institución con ese nombre.";
        if (msg.contains("ORA-20010"))
            return "No se puede asignar un asesor a una práctica cerrada.";
        if (msg.contains("ORA-20011"))
            return "No se puede asignar un estudiante a una práctica cerrada.";
        if (msg.contains("ORA-20012"))
            return "No se puede asignar un tutor a una práctica cerrada.";
        if (msg.contains("ORA-20013"))
            return "Ya existe una práctica en ese semestre para este programa. Solo puede haber una por semestre.";
        if (msg.contains("ORA-20014"))
            return "El semestre debe estar entre 1 y 8.";
        if (msg.contains("ORA-20015"))
            return "El programa seleccionado está inactivo. No se pueden crear prácticas para programas inactivos.";
        if (msg.contains("ORA-20020"))
            return "No se puede eliminar: el estudiante tiene asignaciones activas. Desasígnelo primero.";
        if (msg.contains("ORA-20021"))
            return "No se puede eliminar: el tutor tiene prácticas asignadas. Desasígnelo primero.";
        if (msg.contains("ORA-20030"))
            return "Ya existe un estudiante con ese documento o correo.";
        if (msg.contains("ORA-20031"))
            return "Ya existe un tutor con ese documento o correo.";
        if (msg.contains("ORA-20032"))
            return "Ya existe un asesor con ese documento o correo.";
        if (msg.contains("ORA-20033"))
            return "Ya existe un usuario con ese documento o correo.";

        return "Error de base de datos: " + mensajeSQL;
    }
}