package logica;

/**
 * PATRON STRATEGY - Interfaz de estrategia de calculo de tarifa.
 *
 * Problema que resuelve: el algoritmo de calculo varia segun el tipo
 * de servicio. Sin Strategy, SistemaTaxis tendria un if/else gigante
 * que crecer cada vez que se agregue un nuevo tipo de servicio.
 *
 * Con Strategy: cada tipo de servicio tiene su propio algoritmo
 * encapsulado. Agregar un tipo nuevo solo requiere crear una nueva
 * clase que implemente esta interfaz — SistemaTaxis no se toca.
 * Esto aplica directamente el principio OCP de SOLID.
 */
public interface EstrategiaTarifa {

    /**
     * Calcula la tarifa final del servicio.
     * @param distanciaMetros distancia del viaje en metros (origen -> destino)
     * @return tarifa total en pesos COP
     */
    double calcular(int distanciaMetros);
}
