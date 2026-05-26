package estructuras;

/**
 * Lista Simple Enlazada implementada manualmente.
 * Usada para: conductores, operadores e historial.
 */
public class ListaSimple<T> {
    private Nodo<T> frente;
    private Nodo<T> fin;
    private int size;

    public ListaSimple() {
        this.frente = null;
        this.fin = null;
        this.size = 0;
    }

    /** Inserta al final de la lista. */
    public void insertarFin(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        if (frente == null) {
            frente = nuevo;
            fin = nuevo;
        } else {
            fin.siguiente = nuevo;
            fin = nuevo;
        }
        size++;
    }

    /** Inserta al inicio de la lista. */
    public void insertarInicio(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        if (frente == null) {
            frente = nuevo;
            fin = nuevo;
        } else {
            nuevo.siguiente = frente;
            frente = nuevo;
        }
        size++;
    }

    /** Elimina y retorna el dato del inicio. */
    public T borrarInicio() {
        if (frente == null) return null;
        T valor = frente.dato;
        frente = frente.siguiente;
        if (frente == null) fin = null;
        size--;
        return valor;
    }

    /** Retorna el nodo en una posicion dada (0-indexed). */
    public T obtener(int indice) {
        Nodo<T> tmp = frente;
        int i = 0;
        while (tmp != null) {
            if (i == indice) return tmp.dato;
            tmp = tmp.siguiente;
            i++;
        }
        return null;
    }

    /** Verifica si la lista esta vacia. */
    public boolean estaVacia() {
        return frente == null;
    }

    /** Retorna la cantidad de elementos. */
    public int size() {
        return size;
    }

    /** Devuelve el nodo frente sin eliminarlo. */
    public T verFrente() {
        if (frente == null) return null;
        return frente.dato;
    }

    /** Recorre la lista e imprime cada elemento. */
    public void recorrer() {
        Nodo<T> tmp = frente;
        while (tmp != null) {
            System.out.println("  -> " + tmp.dato.toString());
            tmp = tmp.siguiente;
        }
    }
}
