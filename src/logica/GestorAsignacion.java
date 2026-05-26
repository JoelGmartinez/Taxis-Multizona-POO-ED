package logica;

import estructuras.Grafo;
import estructuras.ListaSimple;
import modelo.Conductor;
import modelo.EstadoConductor;
import modelo.Solicitud;

/**
 * Gestiona la busqueda y asignacion de conductores a solicitudes.
 * Separacion de responsabilidades (SRP): esta clase solo se encarga
 * de encontrar el mejor conductor disponible para una solicitud dada.
 * SistemaTaxis coordina el flujo; GestorAsignacion resuelve la logica
 * de seleccion.
 */
public class GestorAsignacion {

    private static final int VELOCIDAD_METROS_POR_MINUTO = 10;

    /**
     * Busca el conductor mas adecuado para una solicitud.
     * Criterios en orden:
     *   1. Estado DISPONIBLE
     *   2. Habilitado para el tipo de servicio solicitado
     *   3. Con ruta habilitada desde su zona hasta el origen
     * Entre los candidatos, prefiere el de menor distancia al origen.
     *
     * @return el mejor conductor encontrado, o null si ninguno califica
     */
    public Conductor buscarConductor(Solicitud solicitud,
                                     ListaSimple<Conductor> conductores,
                                     Grafo grafo) {
        Conductor mejor = null;
        double mejorDistancia = Double.POSITIVE_INFINITY;

        for (int i = 0; i < conductores.size(); i++) {
            Conductor c = conductores.obtener(i);

            if (c.getEstado() != EstadoConductor.DISPONIBLE)         continue;
            if (!c.estaHabilitadoPara(solicitud.getTipoServicio()))   continue;
            if (!grafo.hayConectividad(c.getZonaActual(),
                                       solicitud.getZonaOrigen()))    continue;

            double dist = grafo.dijkstra(c.getZonaActual(),
                                          solicitud.getZonaOrigen());
            if (dist < mejorDistancia) {
                mejorDistancia = dist;
                mejor = c;
            }
        }
        return mejor;
    }

    /**
     * Calcula el tiempo estimado de recogida en minutos.
     * Si el conductor ya esta en la zona de origen: 5 minutos fijos.
     * Si esta en otra zona: distancia / velocidad (redondeado arriba).
     */
    public int calcularTiempoRecogida(Conductor conductor,
                                       Solicitud solicitud,
                                       Grafo grafo) {
        if (conductor.getZonaActual()
                     .equalsIgnoreCase(solicitud.getZonaOrigen()))
            return 5;

        double dist = grafo.dijkstra(conductor.getZonaActual(),
                                      solicitud.getZonaOrigen());
        return (int) Math.ceil(dist / VELOCIDAD_METROS_POR_MINUTO);
    }

    /**
     * Calcula el tiempo estimado del viaje en minutos
     * (desde zona origen hasta zona destino).
     */
    public int calcularTiempoViaje(Solicitud solicitud, Grafo grafo) {
        double dist = grafo.dijkstra(solicitud.getZonaOrigen(),
                                      solicitud.getZonaDestino());
        return (int) Math.ceil(dist / VELOCIDAD_METROS_POR_MINUTO);
    }

    /**
     * Selecciona la estrategia de tarifa segun el tipo de servicio
     * y calcula el costo del viaje.
     */
    public double calcularTarifa(Solicitud solicitud, Grafo grafo) {
        int distancia = (int) grafo.dijkstra(solicitud.getZonaOrigen(),
                                              solicitud.getZonaDestino());
        EstrategiaTarifa estrategia;
        switch (solicitud.getTipoServicio()) {
            case BAUL:     estrategia = new TarifaBaul();     break;
            case MASCOTAS: estrategia = new TarifaMascotas(); break;
            default:       estrategia = new TarifaEstandar(); break;
        }
        return estrategia.calcular(distancia);
    }
}
