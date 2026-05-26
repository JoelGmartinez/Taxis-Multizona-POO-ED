package excepcion;

/** Se lanza cuando una solicitud tiene datos invalidos o inconsistentes. */
public class SolicitudInvalidaException extends SistemaTaxisException {
    public SolicitudInvalidaException(String mensaje) {
        super("Solicitud invalida: " + mensaje);
    }
}
