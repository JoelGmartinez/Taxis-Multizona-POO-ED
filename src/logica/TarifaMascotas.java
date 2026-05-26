package logica;

/**
 * Estrategia de tarifa para Taxi Pet-Friendly.
 * Aplica la tabla base mas un recargo de $4.000 COP
 * por el transporte de mascotas.
 */
public class TarifaMascotas implements EstrategiaTarifa {

    private static final double TARIFA_BASE      = 5000;
    private static final double RECARGO_MASCOTAS = 4000;

    @Override
    public double calcular(int distanciaMetros) {
        double adicional;
        if      (distanciaMetros <= 1000)  adicional = 2000;
        else if (distanciaMetros <= 3000)  adicional = 4000;
        else if (distanciaMetros <= 6000)  adicional = 7000;
        else if (distanciaMetros <= 10000) adicional = 10000;
        else                               adicional = 12000;
        return TARIFA_BASE + adicional + RECARGO_MASCOTAS;
    }
}
