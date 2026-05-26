package estructuras;

/**
 * Pila Dinamica (LIFO) implementada manualmente con nodos enlazados.
 * Usada para: historial de acciones y control operativo (cancelaciones, cambios de estado).
 */
public class Pila<T> {
    private Nodo<T> superior; // tope de la pila
    private int size;

    public Pila() {
        this.superior = null;
        this.size = 0;
    }

    /** Apila un nuevo elemento en el tope. */
    public void push(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        nuevo.siguiente = superior;
        superior = nuevo;
        size++;
    }

    /** Saca y retorna el elemento del tope. */
    public T pop() {
        if (estaVacia()) return null;
        T dato = superior.dato;
        superior = superior.siguiente;
        size--;
        return dato;
    }

    /** Retorna el elemento del tope sin sacarlo. */
    public T top() {
        if (estaVacia()) return null;
        return superior.dato;
    }

    /** Verifica si la pila esta vacia. */
    public boolean estaVacia() {
        return superior == null;
    }

    /** Retorna la cantidad de elementos en la pila. */
    public int size() {
        return size;
    }

    /** Recorre e imprime la pila del tope a la base. */
    public void recorrer() {
        if (estaVacia()) {
            System.out.println("  (Pila vacia)");
            return;
        }
        Nodo<T> tmp = superior;
        while (tmp != null) {
            System.out.println("  | " + tmp.dato.toString());
            tmp = tmp.siguiente;
        }
        System.out.println("  |___ (base)");
    }
}
