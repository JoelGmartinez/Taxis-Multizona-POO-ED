package estructuras;

// Vértice o Nodo del grafo
public class Vertice {
    Object dato;
    Vertice siguiente = null;
    ListaAdyacencia listaAdyacencia = new ListaAdyacencia();

    public Vertice(Object dato) {
        this.dato = dato;
    }

    public String toString() {
        return dato.toString();
    }
}
