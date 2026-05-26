package logica;

/**
 * Estrategia de tarifa para Taxi con Baul/Parrilla.
 * Aplica la tabla base mas un recargo de $3.000 COP
 * por el manejo de equipaje voluminoso.
 */
public class TarifaBaul implements EstrategiaTarifa {

    private static final double TARIFA_BASE   = 5000;
    private static final double RECARGO_BAUL  = 3000;

    @Override
    public double calcular(int distanciaMetros) {
        double adicional;
        if      (distanciaMetros <= 1000)  adicional = 2000;
        else if (distanciaMetros <= 3000)  adicional = 4000;
        else if (distanciaMetros <= 6000)  adicional = 7000;
        else if (distanciaMetros <= 10000) adicional = 10000;
        else                               adicional = 12000;
        return TARIFA_BASE + adicional + RECARGO_BAUL;
    }
}
