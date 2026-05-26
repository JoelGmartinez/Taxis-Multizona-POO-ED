package estructuras;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Grafo ponderado con lista de adyacencia manual.
 * Vertices = Zonas de la ciudad. Aristas = Vias con distancia en metros.
 *
 * Importaciones usadas:
 *   - HashMap  : distancias y visitados en Dijkstra y BellmanFord
 *   - HashSet  : control de visitados en BFS y DFS
 *   - ArrayList: lista de vertices y matriz en FloydWarshall
 *   - Comparator y Map: apoyo a la ColaConPrioridad de Dijkstra
 *
 * ColaConPrioridad usa la Cola manual del proyecto
 * en lugar de ArrayList para respetar las estructuras propias.
 *
 * Se agregan dos metodos al Grafo original:
 *   - agregarVia()       : arista bidireccional (necesaria para el sistema de taxis)
 *   - cambiarEstadoVia() : habilita o deshabilita una via (cierres viales)
 */
public class Grafo {

    // ─────────────────────────────────────────────────────────────────────────
    // Cola con prioridad
    // Igual al archivo original pero usa Cola<T> manual en lugar de ArrayList
    // ─────────────────────────────────────────────────────────────────────────
    private static class ColaConPrioridad<T> {
        private final Cola<T> cola = new Cola<>();
        private final Comparator<T> comparador;

        public ColaConPrioridad(Comparator<T> comparador) {
            this.comparador = comparador;
        }

        public void encolar(T elemento) {
            // Reconstruimos la cola insertando en la posicion correcta
            Cola<T> auxiliar = new Cola<>();
            boolean insertado = false;

            while (!cola.estaVacia()) {
                T actual = cola.desencolar();
                if (!insertado && comparador.compare(elemento, actual) < 0) {
                    auxiliar.encolar(elemento);
                    insertado = true;
                }
                auxiliar.encolar(actual);
            }
            if (!insertado) auxiliar.encolar(elemento);

            // Restaurar
            while (!auxiliar.estaVacia())
                cola.encolar(auxiliar.desencolar());
        }

        public T desencolar() {
            return cola.desencolar();
        }

        public boolean estaVacia() {
            return cola.estaVacia();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Atributos del grafo
    // ─────────────────────────────────────────────────────────────────────────
    private Vertice primero = null;
    private Vertice ultimo  = null;

    // ─────────────────────────────────────────────────────────────────────────
    // Gestion de aristas — igual al archivo original
    // ─────────────────────────────────────────────────────────────────────────

    public void agregarArista(Object origen, Object destino) {
        Vertice verticeOrigen  = buscarVertice(origen);
        Vertice verticeDestino = buscarVertice(destino);
        if (verticeOrigen != null && verticeDestino != null)
            verticeOrigen.listaAdyacencia.agregar(verticeDestino);
    }

    public void agregarArista(Object origen, Object destino, Object peso) {
        Vertice verticeOrigen  = buscarVertice(origen);
        Vertice verticeDestino = buscarVertice(destino);
        if (verticeOrigen != null && verticeDestino != null)
            verticeOrigen.listaAdyacencia.agregar(verticeDestino, peso);
    }

    public void eliminarArista(Object origen, Object destino) {
        Vertice verticeOrigen  = buscarVertice(origen);
        Vertice verticeDestino = buscarVertice(destino);
        if (verticeOrigen != null && verticeDestino != null)
            verticeOrigen.listaAdyacencia.eliminar(verticeDestino);
    }

    // Agrega via bidireccional — necesaria para el sistema de taxis
    public void agregarVia(Object zonaA, Object zonaB, Object distancia) {
        agregarArista(zonaA, zonaB, distancia);
        agregarArista(zonaB, zonaA, distancia);
    }

    // Habilita o deshabilita una via en ambas direcciones (cierre vial)
    public boolean cambiarEstadoVia(Object zonaA, Object zonaB, boolean habilitar) {
        boolean a = setEstadoArista(zonaA, zonaB, habilitar);
        boolean b = setEstadoArista(zonaB, zonaA, habilitar);
        return a && b;
    }

    private boolean setEstadoArista(Object origen, Object destino, boolean estado) {
        Vertice v = buscarVertice(origen);
        if (v == null) return false;
        Arista a = v.listaAdyacencia.primera;
        while (a != null) {
            if (a.destino.toString().equalsIgnoreCase(destino.toString())) {
                a.habilitada = estado;
                return true;
            }
            a = a.siguiente;
        }
        return false;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Gestion de vertices — igual al archivo original
    // ─────────────────────────────────────────────────────────────────────────

    public void agregarVertice(Object dato) {
        if (buscarVertice(dato) != null)
            return;

        Vertice nuevoVertice = new Vertice(dato);
        if (esVacio()) {
            primero = nuevoVertice;
            ultimo  = nuevoVertice;
            return;
        }
        String nuevoDato = dato.toString();
        if (nuevoDato.compareTo(primero.toString()) < 0) {
            nuevoVertice.siguiente = primero;
            primero = nuevoVertice;
            return;
        }

        if (nuevoDato.compareTo(primero.toString()) > 0) {
            ultimo.siguiente = nuevoVertice;
            ultimo = nuevoVertice;
            return;
        }

        Vertice temporal = primero;
        while (temporal.siguiente != null &&
                nuevoDato.compareTo(temporal.toString()) > 0)
            temporal = temporal.siguiente;

        nuevoVertice.siguiente = temporal.siguiente;
        temporal.siguiente = nuevoVertice;
    }

    public void eliminarVertice(Object dato) {
        if (esVacio())
            return;

        Vertice verticeBorrar = null;

        if (primero.toString().equalsIgnoreCase(dato.toString())) {
            verticeBorrar = primero;
            primero = primero.siguiente;
            if (primero == null)
                ultimo = null;
        } else {
            Vertice temporal = primero;
            while (temporal.siguiente != null &&
                    !temporal.siguiente.toString().equalsIgnoreCase(dato.toString())) {
                temporal = temporal.siguiente;
            }

            if (temporal.siguiente != null) {
                verticeBorrar = temporal.siguiente;
                temporal.siguiente = temporal.siguiente.siguiente;
                if (temporal.siguiente == null)
                    ultimo = temporal;
            }
        }

        if (verticeBorrar != null) {
            Vertice temporal = primero;
            while (temporal != null) {
                temporal.listaAdyacencia.eliminar(verticeBorrar);
                temporal = temporal.siguiente;
            }
        }
    }

    private boolean esVacio() {
        return primero == null;
    }

    public boolean existeZona(Object dato) {
        return buscarVertice(dato) != null;
    }

    public Vertice buscarVertice(Object dato) {
        Vertice temporal = primero;
        while (temporal != null) {
            if (temporal.toString().equalsIgnoreCase(dato.toString()))
                return temporal;
            temporal = temporal.siguiente;
        }
        return null;
    }

    public String toString() {
        String resultado = "";
        Vertice temporal = primero;
        while (temporal != null) {
            resultado += temporal.toString() + " -> " +
                    temporal.listaAdyacencia.toString() + "\n";
            temporal = temporal.siguiente;
        }
        return resultado;
    }

    public void mostrar() {
        System.out.println(this.toString());
    }

    public void listarZonas() {
        Vertice temporal = primero;
        int i = 1;
        while (temporal != null) {
            System.out.println("  " + i + ". " + temporal.dato);
            temporal = temporal.siguiente;
            i++;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Recorrido en anchura (BFS) — igual al archivo original, usa HashSet
    // ─────────────────────────────────────────────────────────────────────────

    public void recorridoAnchura(Object dato) {
        Vertice vertice = buscarVertice(dato);
        if (vertice == null)
            return;

        HashSet<Vertice> visitados = new HashSet<>();
        visitados.add(vertice);
        Cola<Vertice> cola = new Cola<>();
        cola.encolar(vertice);

        while (!cola.estaVacia()) {
            Vertice verticeActual = cola.desencolar();
            System.out.print(verticeActual + ",");
            Arista temporal = verticeActual.listaAdyacencia.primera;
            while (temporal != null) {
                if (!visitados.contains(temporal.destino)) {
                    visitados.add(temporal.destino);
                    cola.encolar(temporal.destino);
                }
                temporal = temporal.siguiente;
            }
        }
        System.out.println();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Recorrido en profundidad (DFS) — igual al archivo original, usa HashSet
    // ─────────────────────────────────────────────────────────────────────────

    public void recorridoProfundidad(Object dato) {
        Vertice vertice = buscarVertice(dato);
        if (vertice == null)
            return;

        HashSet<Vertice> visitados = new HashSet<>();
        recorridoProfundidad(vertice, visitados);
        System.out.println();
    }

    private void recorridoProfundidad(Vertice vertice, HashSet<Vertice> visitados) {
        System.out.print(vertice.toString() + ",");
        visitados.add(vertice);

        Arista aristaActual = vertice.listaAdyacencia.primera;
        while (aristaActual != null) {
            if (!visitados.contains(aristaActual.destino))
                recorridoProfundidad(aristaActual.destino, visitados);
            aristaActual = aristaActual.siguiente;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Dijkstra — igual al archivo original, usa HashMap y ColaConPrioridad
    // Solo recorre aristas habilitadas (respeta cierres viales)
    // ─────────────────────────────────────────────────────────────────────────

    public HashMap<Vertice, Double> Dijkstra(Object origen) {
        Vertice verticeOrigen = buscarVertice(origen);
        if (verticeOrigen == null)
            return null;

        HashMap<Vertice, Boolean> visitados  = new HashMap<>();
        HashMap<Vertice, Double>  distancias = new HashMap<>();

        Vertice temporal = primero;
        while (temporal != null) {
            distancias.put(temporal, Double.POSITIVE_INFINITY);
            visitados.put(temporal, false);
            temporal = temporal.siguiente;
        }

        distancias.put(verticeOrigen, 0.0);

        ColaConPrioridad<Vertice> cola = new ColaConPrioridad<>(
                Comparator.comparingDouble(distancias::get));
        cola.encolar(verticeOrigen);

        while (!cola.estaVacia()) {
            Vertice actual = cola.desencolar();

            if (visitados.get(actual))
                continue;

            visitados.put(actual, true);

            Arista aristaActual = actual.listaAdyacencia.primera;
            while (aristaActual != null) {
                // Solo aristas habilitadas
                if (aristaActual.habilitada && !visitados.get(aristaActual.destino)) {
                    double nuevaDistancia = distancias.get(actual)
                            + ((Number) aristaActual.peso).doubleValue();

                    if (nuevaDistancia < distancias.get(aristaActual.destino)) {
                        distancias.put(aristaActual.destino, nuevaDistancia);
                        cola.encolar(aristaActual.destino);
                    }
                }
                aristaActual = aristaActual.siguiente;
            }
        }
        return distancias;
    }

    // Retorna la distancia minima entre dos zonas, o POSITIVE_INFINITY si no hay ruta
    public double dijkstra(Object origen, Object destino) {
        HashMap<Vertice, Double> distancias = Dijkstra(origen);
        if (distancias == null) return Double.POSITIVE_INFINITY;
        Vertice vDestino = buscarVertice(destino);
        if (vDestino == null) return Double.POSITIVE_INFINITY;
        return distancias.getOrDefault(vDestino, Double.POSITIVE_INFINITY);
    }

    public boolean hayConectividad(Object origen, Object destino) {
        return dijkstra(origen, destino) != Double.POSITIVE_INFINITY;
    }

    public void mostrarDistanciasDijkstra(Object origen) {
        HashMap<Vertice, Double> distancias = Dijkstra(origen);
        if (distancias != null) {
            System.out.println("  Distancias desde: " + origen.toString());
            for (Map.Entry<Vertice, Double> entry : distancias.entrySet()) {
                double valor = entry.getValue();
                System.out.println("    " + entry.getKey().dato + ": " +
                        (valor == Double.POSITIVE_INFINITY ? "SIN RUTA" : (int) valor + " m"));
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // BellmanFord — igual al archivo original, usa HashMap
    // ─────────────────────────────────────────────────────────────────────────

    public HashMap<Vertice, Double> BellmanFord(Object origen) {
        Vertice verticeOrigen = buscarVertice(origen);
        if (verticeOrigen == null)
            return null;

        HashMap<Vertice, Double> distancias = new HashMap<>();

        Vertice temporal = primero;
        while (temporal != null) {
            distancias.put(temporal, Double.POSITIVE_INFINITY);
            temporal = temporal.siguiente;
        }

        distancias.put(verticeOrigen, 0.0);

        for (int i = 1; i < distancias.size(); i++) {
            temporal = primero;
            while (temporal != null) {
                Arista aristaActual = temporal.listaAdyacencia.primera;
                while (aristaActual != null) {
                    double nueva = distancias.get(temporal)
                            + ((Number) aristaActual.peso).doubleValue();
                    if (nueva < distancias.get(aristaActual.destino))
                        distancias.put(aristaActual.destino, nueva);
                    aristaActual = aristaActual.siguiente;
                }
                temporal = temporal.siguiente;
            }
        }

        // Verificar ciclos negativos
        temporal = primero;
        while (temporal != null) {
            Arista aristaActual = temporal.listaAdyacencia.primera;
            while (aristaActual != null) {
                double nueva = distancias.get(temporal)
                        + ((Number) aristaActual.peso).doubleValue();
                if (nueva < distancias.get(aristaActual.destino))
                    return null;
                aristaActual = aristaActual.siguiente;
            }
            temporal = temporal.siguiente;
        }

        return distancias;
    }

    public void mostrarDistanciaBellmanFord(Object origen) {
        HashMap<Vertice, Double> distancias = BellmanFord(origen);
        if (distancias != null) {
            System.out.println("Distancias desde: " + origen.toString());
            for (Map.Entry<Vertice, Double> entry : distancias.entrySet())
                System.out.println(entry.getKey().dato + ": " + entry.getValue());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // FloydWarshall — igual al archivo original, usa ArrayList
    // ─────────────────────────────────────────────────────────────────────────

    public ArrayList<ArrayList<Double>> FloydWarshall() {
        ArrayList<Vertice> vertices = new ArrayList<>();
        Vertice actual = primero;
        while (actual != null) {
            vertices.add(actual);
            actual = actual.siguiente;
        }

        int n = vertices.size();
        ArrayList<ArrayList<Double>> distancias = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            ArrayList<Double> fila = new ArrayList<>();
            for (int j = 0; j < n; j++)
                fila.add(i == j ? 0.0 : Double.POSITIVE_INFINITY);
            distancias.add(fila);
        }

        for (int i = 0; i < n; i++) {
            Arista aristaActual = vertices.get(i).listaAdyacencia.primera;
            while (aristaActual != null) {
                int j = vertices.indexOf(aristaActual.destino);
                distancias.get(i).set(j, ((Number) aristaActual.peso).doubleValue());
                aristaActual = aristaActual.siguiente;
            }
        }

        for (int k = 0; k < n; k++)
            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++)
                    if (distancias.get(i).get(k) != Double.POSITIVE_INFINITY &&
                        distancias.get(k).get(j) != Double.POSITIVE_INFINITY) {
                        double nuevo = distancias.get(i).get(k) + distancias.get(k).get(j);
                        if (nuevo < distancias.get(i).get(j))
                            distancias.get(i).set(j, nuevo);
                    }

        return distancias;
    }

    public void mostrarDistanciasFloydWarshall() {
        ArrayList<ArrayList<Double>> distancias = FloydWarshall();
        ArrayList<Vertice> vertices = new ArrayList<>();
        Vertice temp = primero;
        while (temp != null) {
            vertices.add(temp);
            temp = temp.siguiente;
        }

        int n = vertices.size();

        System.out.print("\t");
        for (Vertice v : vertices)
            System.out.print(v.dato + "\t");
        System.out.println();

        for (int i = 0; i < n; i++) {
            System.out.print(vertices.get(i).dato + "\t");
            for (int j = 0; j < n; j++) {
                double valor = distancias.get(i).get(j);
                System.out.print(valor == Double.POSITIVE_INFINITY ? "INF\t" : (int) valor + "\t");
            }
            System.out.println();
        }
    }
}
