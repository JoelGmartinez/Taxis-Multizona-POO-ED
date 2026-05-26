package excepcion;

/**
 * Excepcion base del sistema de taxis.
 * Todas las excepciones personalizadas heredan de esta.
 */
public class SistemaTaxisException extends Exception {
    public SistemaTaxisException(String mensaje) {
        super(mensaje);
    }
}
