package modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Representa una solicitud de servicio de taxi.
 * Registra origen, destino, tipo de servicio, estado y metricas calculadas.
 */
public class Solicitud {
    private static int contadorId = 1;

    private int id;
    private String zonaOrigen;
    private String zonaDestino;
    private TipoServicio tipoServicio;
    private EstadoSolicitud estado;
    private String fechaHoraRegistro;

    private Conductor conductorAsignado;
    private double tarifaCalculada;
    private int tiempoEstimadoRecogida;
    private int tiempoEstimadoViaje;
    private String motivoCancelacion;

    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public Solicitud(String zonaOrigen, String zonaDestino, TipoServicio tipoServicio) {
        this.id                  = contadorId++;
        this.zonaOrigen          = zonaOrigen;
        this.zonaDestino         = zonaDestino;
        this.tipoServicio        = tipoServicio;
        this.estado              = EstadoSolicitud.EN_ESPERA;
        this.fechaHoraRegistro   = LocalDateTime.now().format(FORMATO);
        this.conductorAsignado   = null;
        this.tarifaCalculada     = 0;
        this.tiempoEstimadoRecogida = 0;
        this.tiempoEstimadoViaje    = 0;
    }

    // ── Getters ────────────────────────────────────────────────────────────────
    public int getId()                        { return id; }
    public String getZonaOrigen()             { return zonaOrigen; }
    public String getZonaDestino()            { return zonaDestino; }
    public TipoServicio getTipoServicio()     { return tipoServicio; }
    public EstadoSolicitud getEstado()        { return estado; }
    public String getFechaHoraRegistro()      { return fechaHoraRegistro; }
    public Conductor getConductorAsignado()   { return conductorAsignado; }
    public double getTarifaCalculada()        { return tarifaCalculada; }
    public int getTiempoEstimadoRecogida()    { return tiempoEstimadoRecogida; }
    public int getTiempoEstimadoViaje()       { return tiempoEstimadoViaje; }
    public String getMotivoCancelacion()      { return motivoCancelacion; }

    // ── Setters ────────────────────────────────────────────────────────────────
    public void setEstado(EstadoSolicitud estado)         { this.estado = estado; }
    public void setConductorAsignado(Conductor conductor) { this.conductorAsignado = conductor; }
    public void setTarifaCalculada(double tarifa)         { this.tarifaCalculada = tarifa; }
    public void setTiempoEstimadoRecogida(int min)        { this.tiempoEstimadoRecogida = min; }
    public void setTiempoEstimadoViaje(int min)           { this.tiempoEstimadoViaje = min; }
    public void setMotivoCancelacion(String motivo)       { this.motivoCancelacion = motivo; }

    public String resumenCorto() {
        return String.format("#%d | %s -> %s | %s | %s",
                id, zonaOrigen, zonaDestino,
                tipoServicio.getDescripcion(), fechaHoraRegistro);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Solicitud #%d\n", id));
        sb.append(String.format("  Origen  : %s\n", zonaOrigen));
        sb.append(String.format("  Destino : %s\n", zonaDestino));
        sb.append(String.format("  Servicio: %s\n", tipoServicio.getDescripcion()));
        sb.append(String.format("  Estado  : %s\n", estado));
        sb.append(String.format("  Registro: %s\n", fechaHoraRegistro));
        if (conductorAsignado != null) {
            sb.append(String.format("  Conductor: %s (%s) - Placa: %s\n",
                    conductorAsignado.getNombre(),
                    conductorAsignado.getCedula(),
                    conductorAsignado.getPlaca()));
            sb.append(String.format("  Tarifa    : $%.0f COP\n", tarifaCalculada));
            sb.append(String.format("  T.Recogida: %d min | T.Viaje: %d min\n",
                    tiempoEstimadoRecogida, tiempoEstimadoViaje));
        }
        if (motivoCancelacion != null)
            sb.append(String.format("  Motivo cancelacion: %s\n", motivoCancelacion));
        return sb.toString();
    }
}
