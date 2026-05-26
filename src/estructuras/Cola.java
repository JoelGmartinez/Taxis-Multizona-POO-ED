package estructuras;

/**
 * Cola Dinamica (FIFO) implementada manualmente con nodos enlazados.
 * Usada para: solicitudes de servicio en espera.
 */
public class Cola<T> {
    private Nodo<T> frente;
    private Nodo<T> fin;
    private int size;

    public Cola() {
        this.frente = null;
        this.fin = null;
        this.size = 0;
    }

    /** Encola un nuevo elemento al final. */
    public void encolar(T valor) {
        Nodo<T> nuevo = new Nodo<>(valor);
        if (frente == null) {
            frente = nuevo;
            fin = nuevo;
        } else {
            fin.siguiente = nuevo;
            fin = nuevo;
        }
        size++;
    }

    /** Desencola y retorna el elemento al frente (FIFO). */
    public T desencolar() {
        if (estaVacia()) return null;
        T valor = frente.dato;
        frente = frente.siguiente;
        if (frente == null) fin = null;
        size--;
        return valor;
    }

    /** Retorna el elemento al frente sin eliminarlo. */
    public T verFrente() {
        if (estaVacia()) return null;
        return frente.dato;
    }

    /** Verifica si la cola esta vacia. */
    public boolean estaVacia() {
        return frente == null;
    }

    /** Retorna la cantidad de elementos en la cola. */
    public int size() {
        return size;
    }

    /** Recorre e imprime todos los elementos de la cola. */
    public void recorrer() {
        Nodo<T> tmp = frente;
        int i = 1;
        while (tmp != null) {
            System.out.println("  [" + i + "] " + tmp.dato.toString());
            tmp = tmp.siguiente;
            i++;
        }
    }

    /**
     * Permite buscar y eliminar un elemento especifico de la cola
     * (para cancelaciones de solicitudes).
     * Retorna true si se encontro y elimino.
     */
    public boolean eliminar(T dato) {
        if (estaVacia()) return false;

        // Caso: el frente es el elemento buscado
        if (frente.dato.equals(dato)) {
            desencolar();
            return true;
        }

        Nodo<T> tmp = frente;
        while (tmp.siguiente != null) {
            if (tmp.siguiente.dato.equals(dato)) {
                if (tmp.siguiente == fin) {
                    fin = tmp;
                }
                tmp.siguiente = tmp.siguiente.siguiente;
                size--;
                return true;
            }
            tmp = tmp.siguiente;
        }
        return false;
    }
}
