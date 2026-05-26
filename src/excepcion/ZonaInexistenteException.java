package excepcion;

/** Se lanza cuando una zona no existe en el grafo. */
public class ZonaInexistenteException extends SistemaTaxisException {
    public ZonaInexistenteException(String zona) {
        super("La zona '" + zona + "' no existe en el sistema.");
    }
}
