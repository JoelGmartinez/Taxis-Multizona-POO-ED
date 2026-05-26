package estructuras;

// Lista de Adyacencia — igual al archivo original
public class ListaAdyacencia {
    Arista primera = null;
    Arista ultima = null;

    private boolean esVacia() {
        return primera == null;
    }

    private boolean buscarAdyacencia(Vertice destino) {
        Arista temporal = primera;
        while (temporal != null) {
            if (temporal.destino.toString().equals(destino.toString()))
                return true;
            temporal = temporal.siguiente;
        }
        return false;
    }

    public void agregar(Vertice destino) {
        if (!buscarAdyacencia(destino))
            agregarArista(new Arista(destino));
    }

    public void agregar(Vertice destino, Object peso) {
        if (!buscarAdyacencia(destino))
            agregarArista(new Arista(destino, peso));
    }

    private void agregarArista(Arista nuevaArista) {
        if (esVacia()) {
            primera = nuevaArista;
            ultima = nuevaArista;
            return;
        }
        String dato = nuevaArista.destino.toString();
        if (dato.compareTo(primera.destino.toString()) < 0) {
            nuevaArista.siguiente = primera;
            primera = nuevaArista;
            return;
        }

        if (dato.compareTo(primera.destino.toString()) > 0) {
            ultima.siguiente = nuevaArista;
            ultima = nuevaArista;
            return;
        }

        Arista temporal = primera;
        while (temporal.siguiente != null &&
                dato.compareTo(temporal.destino.toString()) > 0)
            temporal = temporal.siguiente;

        nuevaArista.siguiente = temporal.siguiente;
        temporal.siguiente = nuevaArista;
    }

    public void eliminar(Vertice destino) {
        if (esVacia())
            return;

        if (primera.destino.toString().equals(destino.toString())) {
            primera = primera.siguiente;
            if (primera == null)
                ultima = null;
            return;
        }

        Arista temporal = primera;
        while (temporal.siguiente != null &&
                !temporal.siguiente.destino.toString().equals(destino.toString())) {
            temporal = temporal.siguiente;
        }

        if (temporal.siguiente != null) {
            temporal.siguiente = temporal.siguiente.siguiente;
            if (temporal.siguiente == null)
                ultima = temporal;
        }
    }

    public String toString() {
        String resultado = "";
        Arista temporal = primera;
        while (temporal != null) {
            resultado += temporal.destino.toString();
            if (temporal.peso != null)
                resultado += "(" + temporal.peso.toString()
                          + "," + (temporal.habilitada ? "OK" : "CERRADA") + ")";
            resultado += " -> ";
            temporal = temporal.siguiente;
        }
        resultado += "NULL";
        return resultado;
    }
}
