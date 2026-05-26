package estructuras;

/**
 * Nodo generico usado por Lista, Cola y Pila.
 * Almacena cualquier tipo de dato (T) y apunta al siguiente nodo.
 */
public class Nodo<T> {
    public T dato;
    public Nodo<T> siguiente;

    public Nodo(T dato) {
        this.dato = dato;
        this.siguiente = null;
    }
}
