package modelo;

/**
 * Representa a un conductor y su vehiculo.
 * Cada conductor puede estar habilitado para uno o mas tipos de servicio.
 * Se usa un arreglo booleano (sin colecciones nativas) para los tipos habilitados.
 */
public class Conductor {
    private String cedula;
    private String nombre;
    private String placa;
    private String zonaActual;
    private EstadoConductor estado;

    // Arreglo paralelo a TipoServicio.values() — sin ArrayList ni List
    private boolean[] serviciosHabilitados;

    public Conductor(String cedula, String nombre, String placa, String zonaActual) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.placa = placa;
        this.zonaActual = zonaActual;
        this.estado = EstadoConductor.DISPONIBLE;
        // 3 tipos de servicio: ESTANDAR, BAUL, MASCOTAS
        this.serviciosHabilitados = new boolean[TipoServicio.values().length];
    }

    /** Habilita un tipo de servicio para este conductor. */
    public void habilitarServicio(TipoServicio tipo) {
        serviciosHabilitados[tipo.ordinal()] = true;
    }

    /** Verifica si el conductor puede realizar un tipo de servicio. */
    public boolean estaHabilitadoPara(TipoServicio tipo) {
        return serviciosHabilitados[tipo.ordinal()];
    }

    /** Lista los servicios habilitados como texto. */
    public String getServiciosTexto() {
        StringBuilder sb = new StringBuilder();
        TipoServicio[] tipos = TipoServicio.values();
        for (int i = 0; i < tipos.length; i++) {
            if (serviciosHabilitados[i]) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(tipos[i].getDescripcion());
            }
        }
        return sb.length() == 0 ? "Ninguno" : sb.toString();
    }

    // ── Getters y Setters ─────────────────────────────────────────────────────
    public String getCedula()        { return cedula; }
    public String getNombre()        { return nombre; }
    public String getPlaca()         { return placa; }
    public String getZonaActual()    { return zonaActual; }
    public EstadoConductor getEstado() { return estado; }

    public void setZonaActual(String zona)     { this.zonaActual = zona; }
    public void setEstado(EstadoConductor est) { this.estado = est; }

    @Override
    public String toString() {
        return String.format("Conductor{cedula='%s', nombre='%s', placa='%s', zona='%s', estado=%s, servicios=[%s]}",
                cedula, nombre, placa, zonaActual, estado, getServiciosTexto());
    }
}
