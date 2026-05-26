package logica;

import estructuras.*;
import modelo.*;
import excepcion.*;

/**
 * PATRON SINGLETON: Una sola instancia del sistema gestiona el estado global.
 *
 * SistemaTaxis coordina el flujo general del sistema.
 * La logica de busqueda y calculo esta delegada en GestorAsignacion (SRP).
 * El calculo de tarifa usa el patron Strategy a traves de GestorAsignacion.
 */
public class SistemaTaxis {

    // ── Singleton ──────────────────────────────────────────────────────────────
    private static SistemaTaxis instancia;

    public static SistemaTaxis getInstancia() {
        if (instancia == null)
            instancia = new SistemaTaxis();
        return instancia;
    }

    // ── Estructuras de datos manuales ──────────────────────────────────────────
    private ListaSimple<Conductor> listaConductores;
    private Cola<Solicitud>        colaSolicitudes;
    private ListaSimple<Solicitud> historialServicios;
    private Pila<String>           pilaAcciones;
    private Grafo                  grafoVial;

    // ── Colaboradores ──────────────────────────────────────────────────────────
    private GestorAsignacion gestor;

    // ── Constructor privado ────────────────────────────────────────────────────
    private SistemaTaxis() {
        listaConductores   = new ListaSimple<>();
        colaSolicitudes    = new Cola<>();
        historialServicios = new ListaSimple<>();
        pilaAcciones       = new Pila<>();
        grafoVial          = new Grafo();
        gestor             = new GestorAsignacion();
        inicializarDatos();
    }

    // ── Datos iniciales de prueba ──────────────────────────────────────────────
    private void inicializarDatos() {
        // Zonas de la ciudad
        grafoVial.agregarVertice("Norte");
        grafoVial.agregarVertice("Sur");
        grafoVial.agregarVertice("Centro");
        grafoVial.agregarVertice("Oriente");
        grafoVial.agregarVertice("Occidente");
        grafoVial.agregarVertice("Aeropuerto");

        // Vias bidireccionales (distancia en metros)
        grafoVial.agregarVia("Norte",     "Centro",     3500);
        grafoVial.agregarVia("Sur",       "Centro",     4000);
        grafoVial.agregarVia("Centro",    "Oriente",    2500);
        grafoVial.agregarVia("Centro",    "Occidente",  3000);
        grafoVial.agregarVia("Norte",     "Oriente",    5000);
        grafoVial.agregarVia("Sur",       "Occidente",  4500);
        grafoVial.agregarVia("Oriente",   "Aeropuerto", 8000);
        grafoVial.agregarVia("Occidente", "Aeropuerto", 7000);
        grafoVial.agregarVia("Norte",     "Aeropuerto", 11000);

        // Conductores
        Conductor c1 = new Conductor("12345678", "Carlos Perez", "ABC-123", "Centro");
        c1.habilitarServicio(TipoServicio.ESTANDAR);
        c1.habilitarServicio(TipoServicio.BAUL);

        Conductor c2 = new Conductor("87654321", "Maria Lopez", "XYZ-456", "Norte");
        c2.habilitarServicio(TipoServicio.ESTANDAR);
        c2.habilitarServicio(TipoServicio.MASCOTAS);

        Conductor c3 = new Conductor("11223344", "Juan Torres", "DEF-789", "Sur");
        c3.habilitarServicio(TipoServicio.ESTANDAR);

        Conductor c4 = new Conductor("99887766", "Ana Gomez", "GHI-321", "Oriente");
        c4.habilitarServicio(TipoServicio.ESTANDAR);
        c4.habilitarServicio(TipoServicio.BAUL);
        c4.habilitarServicio(TipoServicio.MASCOTAS);

        Conductor c5 = new Conductor("55443322", "Pedro Ruiz", "JKL-654", "Aeropuerto");
        c5.habilitarServicio(TipoServicio.ESTANDAR);
        c5.habilitarServicio(TipoServicio.BAUL);

        listaConductores.insertarFin(c1);
        listaConductores.insertarFin(c2);
        listaConductores.insertarFin(c3);
        listaConductores.insertarFin(c4);
        listaConductores.insertarFin(c5);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  GESTION DE SOLICITUDES
    // ═══════════════════════════════════════════════════════════════════════════

    public Solicitud registrarSolicitud(String zonaOrigen, String zonaDestino,
                                        TipoServicio tipo)
            throws SistemaTaxisException {

        if (zonaOrigen == null || zonaOrigen.trim().isEmpty() ||
            zonaDestino == null || zonaDestino.trim().isEmpty())
            throw new SolicitudInvalidaException("Las zonas no pueden estar vacias.");

        if (!grafoVial.existeZona(zonaOrigen))
            throw new ZonaInexistenteException(zonaOrigen);

        if (!grafoVial.existeZona(zonaDestino))
            throw new ZonaInexistenteException(zonaDestino);

        if (zonaOrigen.equalsIgnoreCase(zonaDestino))
            throw new SolicitudInvalidaException("El origen y el destino no pueden ser la misma zona.");

        Solicitud s = new Solicitud(zonaOrigen, zonaDestino, tipo);
        colaSolicitudes.encolar(s);
        pilaAcciones.push("REGISTRO solicitud #" + s.getId()
                + " | " + zonaOrigen + " -> " + zonaDestino);
        return s;
    }

    public Solicitud atenderSiguienteSolicitud() throws SistemaTaxisException {
        if (colaSolicitudes.estaVacia())
            throw new SolicitudInvalidaException("No hay solicitudes en espera.");

        Solicitud solicitud = colaSolicitudes.desencolar();
        solicitud.setEstado(EstadoSolicitud.EN_ATENCION);

        // Buscar conductor usando GestorAsignacion
        Conductor conductor = gestor.buscarConductor(solicitud, listaConductores, grafoVial);

        if (conductor == null) {
            colaSolicitudes.encolar(solicitud);
            solicitud.setEstado(EstadoSolicitud.EN_ESPERA);
            throw new ConductorNoDisponibleException(solicitud.getTipoServicio().getDescripcion());
        }

        // Verificar conectividad origen -> destino
        if (!grafoVial.hayConectividad(solicitud.getZonaOrigen(), solicitud.getZonaDestino())) {
            colaSolicitudes.encolar(solicitud);
            solicitud.setEstado(EstadoSolicitud.EN_ESPERA);
            throw new SinConectividadException(solicitud.getZonaOrigen(), solicitud.getZonaDestino());
        }

        // Calcular tiempos y tarifa delegando en GestorAsignacion (Strategy interno)
        int tiempoRecogida = gestor.calcularTiempoRecogida(conductor, solicitud, grafoVial);
        int tiempoViaje    = gestor.calcularTiempoViaje(solicitud, grafoVial);
        double tarifa      = gestor.calcularTarifa(solicitud, grafoVial);

        // Asignar datos a la solicitud
        solicitud.setConductorAsignado(conductor);
        solicitud.setTiempoEstimadoRecogida(tiempoRecogida);
        solicitud.setTiempoEstimadoViaje(tiempoViaje);
        solicitud.setTarifaCalculada(tarifa);

        conductor.setEstado(EstadoConductor.OCUPADO);

        pilaAcciones.push("ATENCION solicitud #" + solicitud.getId()
                + " -> conductor " + conductor.getNombre());
        return solicitud;
    }

    public void cancelarSolicitud(int idSolicitud, String motivo)
            throws SistemaTaxisException {

        if (motivo == null || motivo.trim().isEmpty())
            throw new SolicitudInvalidaException("Debe indicar un motivo de cancelacion.");

        Solicitud encontrada = buscarEnCola(idSolicitud);
        if (encontrada == null)
            throw new SolicitudInvalidaException("No se encontro la solicitud #"
                    + idSolicitud + " en la cola de espera.");

        colaSolicitudes.eliminar(encontrada);
        encontrada.setEstado(EstadoSolicitud.CANCELADA);
        encontrada.setMotivoCancelacion(motivo);
        historialServicios.insertarFin(encontrada);
        pilaAcciones.push("CANCELACION solicitud #" + idSolicitud + " | motivo: " + motivo);
    }

    public void finalizarServicio(Solicitud solicitud) throws SistemaTaxisException {
        if (solicitud.getEstado() != EstadoSolicitud.EN_ATENCION)
            throw new SolicitudInvalidaException("La solicitud #" + solicitud.getId()
                    + " no esta en estado EN_ATENCION.");

        solicitud.setEstado(EstadoSolicitud.FINALIZADA);

        if (solicitud.getConductorAsignado() != null) {
            solicitud.getConductorAsignado().setEstado(EstadoConductor.DISPONIBLE);
            solicitud.getConductorAsignado().setZonaActual(solicitud.getZonaDestino());
        }

        historialServicios.insertarFin(solicitud);
        pilaAcciones.push("FINALIZACION solicitud #" + solicitud.getId());
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  GESTION DE CONDUCTORES
    // ═══════════════════════════════════════════════════════════════════════════

    public void agregarConductor(Conductor c) {
        listaConductores.insertarFin(c);
        pilaAcciones.push("REGISTRO conductor " + c.getNombre()
                + " | cedula " + c.getCedula());
    }

    public Conductor buscarConductorPorCedula(String cedula) {
        for (int i = 0; i < listaConductores.size(); i++) {
            Conductor c = listaConductores.obtener(i);
            if (c.getCedula().equals(cedula)) return c;
        }
        return null;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  GESTION DE RED VIAL
    // ═══════════════════════════════════════════════════════════════════════════

    public void cerrarVia(String origen, String destino) throws SistemaTaxisException {
        if (!grafoVial.existeZona(origen))  throw new ZonaInexistenteException(origen);
        if (!grafoVial.existeZona(destino)) throw new ZonaInexistenteException(destino);
        grafoVial.cambiarEstadoVia(origen, destino, false);
        pilaAcciones.push("CIERRE_VIA " + origen + " <-> " + destino);
    }

    public void habilitarVia(String origen, String destino) throws SistemaTaxisException {
        if (!grafoVial.existeZona(origen))  throw new ZonaInexistenteException(origen);
        if (!grafoVial.existeZona(destino)) throw new ZonaInexistenteException(destino);
        grafoVial.cambiarEstadoVia(origen, destino, true);
        pilaAcciones.push("APERTURA_VIA " + origen + " <-> " + destino);
    }

    // ── Utilidad interna ───────────────────────────────────────────────────────

    private Solicitud buscarEnCola(int id) {
        Cola<Solicitud> auxiliar = new Cola<>();
        Solicitud encontrada = null;

        while (!colaSolicitudes.estaVacia()) {
            Solicitud s = colaSolicitudes.desencolar();
            if (s.getId() == id) encontrada = s;
            auxiliar.encolar(s);
        }
        while (!auxiliar.estaVacia())
            colaSolicitudes.encolar(auxiliar.desencolar());

        return encontrada;
    }

    // ── Getters ────────────────────────────────────────────────────────────────
    public ListaSimple<Conductor> getListaConductores()   { return listaConductores; }
    public Cola<Solicitud>        getColaSolicitudes()    { return colaSolicitudes; }
    public ListaSimple<Solicitud> getHistorialServicios() { return historialServicios; }
    public Pila<String>           getPilaAcciones()       { return pilaAcciones; }
    public Grafo                  getGrafoVial()          { return grafoVial; }
}
