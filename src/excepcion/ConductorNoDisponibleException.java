package excepcion;

/** Se lanza cuando no hay conductores disponibles para el servicio solicitado. */
public class ConductorNoDisponibleException extends SistemaTaxisException {
    public ConductorNoDisponibleException(String tipoServicio) {
        super("No hay conductores disponibles habilitados para: " + tipoServicio);
    }
}
