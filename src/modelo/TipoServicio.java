package modelo;

/** Tipos de servicio que ofrece la cooperativa. */
public enum TipoServicio {
    ESTANDAR("Taxi Estandar"),
    BAUL("Taxi con Baul/Parrilla"),
    MASCOTAS("Taxi Pet-Friendly");

    private final String descripcion;

    TipoServicio(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() { return descripcion; }

    @Override
    public String toString() { return descripcion; }
}
