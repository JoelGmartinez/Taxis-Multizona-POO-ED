package excepcion;

/** Se lanza cuando no hay ruta habilitada entre dos zonas. */
public class SinConectividadException extends SistemaTaxisException {
    public SinConectividadException(String origen, String destino) {
        super("No existe conectividad vial habilitada entre '" + origen + "' y '" + destino + "'.");
    }
}
