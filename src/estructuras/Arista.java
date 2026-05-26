package estructuras;

// Arista o Arco del grafo
// Se agrega 'habilitada' para simular cierres viales sin eliminar la arista
public class Arista {
    Object peso = null;
    boolean habilitada = true;
    Arista siguiente = null;
    Vertice destino = null;

    public Arista(Vertice destino, Object peso) {
        this.destino = destino;
        this.peso = peso;
    }

    public Arista(Vertice destino) {
        this.destino = destino;
    }
}
