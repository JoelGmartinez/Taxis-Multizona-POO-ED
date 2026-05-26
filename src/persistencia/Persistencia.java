package persistencia;

import modelo.*;
import logica.SistemaTaxis;
import estructuras.ListaSimple;
import estructuras.Pila;

import java.io.*;

/**
 * Gestiona la persistencia del sistema usando archivos de texto.
 * Usa PrintWriter y BufferedReader de java.io para lectura/escritura legible.
 * Responsabilidad unica: I/O de archivos (SRP).
 */
public class Persistencia {

    private static final String ARCHIVO_CONDUCTORES = "conductores.txt";
    private static final String ARCHIVO_HISTORIAL   = "historial.txt";
    private static final String ARCHIVO_ACCIONES    = "acciones.txt";

    // ── Guardar conductores ────────────────────────────────────────────────────

    public static void guardarConductores(ListaSimple<Conductor> lista) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO_CONDUCTORES))) {
            for (int i = 0; i < lista.size(); i++) {
                Conductor c = lista.obtener(i);
                // formato: cedula|nombre|placa|zona|estado|ESTANDAR|BAUL|MASCOTAS
                pw.println(c.getCedula() + "|" + c.getNombre() + "|" +
                           c.getPlaca() + "|" + c.getZonaActual() + "|" +
                           c.getEstado().name() + "|" +
                           (c.estaHabilitadoPara(TipoServicio.ESTANDAR) ? "1" : "0") + "|" +
                           (c.estaHabilitadoPara(TipoServicio.BAUL)     ? "1" : "0") + "|" +
                           (c.estaHabilitadoPara(TipoServicio.MASCOTAS) ? "1" : "0"));
            }
            System.out.println("[Persistencia] Conductores guardados -> " + ARCHIVO_CONDUCTORES);
        } catch (IOException e) {
            System.out.println("[Persistencia] Error al guardar conductores: " + e.getMessage());
        }
    }

    // ── Cargar conductores ─────────────────────────────────────────────────────

    public static ListaSimple<Conductor> cargarConductores() {
        ListaSimple<Conductor> lista = new ListaSimple<>();
        File f = new File(ARCHIVO_CONDUCTORES);
        if (!f.exists()) {
            System.out.println("[Persistencia] " + ARCHIVO_CONDUCTORES + " no encontrado. Usando datos iniciales.");
            return lista;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] p = linea.split("\\|");
                if (p.length < 8) continue;
                Conductor c = new Conductor(p[0], p[1], p[2], p[3]);
                try { c.setEstado(EstadoConductor.valueOf(p[4])); }
                catch (Exception ignored) {}
                if ("1".equals(p[5])) c.habilitarServicio(TipoServicio.ESTANDAR);
                if ("1".equals(p[6])) c.habilitarServicio(TipoServicio.BAUL);
                if ("1".equals(p[7])) c.habilitarServicio(TipoServicio.MASCOTAS);
                lista.insertarFin(c);
            }
            System.out.println("[Persistencia] " + lista.size() + " conductores cargados.");
        } catch (IOException e) {
            System.out.println("[Persistencia] Error al cargar conductores: " + e.getMessage());
        }
        return lista;
    }

    // ── Guardar historial ──────────────────────────────────────────────────────

    public static void guardarHistorial(ListaSimple<Solicitud> historial) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO_HISTORIAL))) {
            pw.println("===== HISTORIAL DE SERVICIOS =====");
            for (int i = 0; i < historial.size(); i++) {
                pw.println(historial.obtener(i).toString());
                pw.println("----------------------------------");
            }
            System.out.println("[Persistencia] Historial guardado -> " + ARCHIVO_HISTORIAL);
        } catch (IOException e) {
            System.out.println("[Persistencia] Error al guardar historial: " + e.getMessage());
        }
    }

    // ── Guardar auditoria (pila de acciones) ───────────────────────────────────

    public static void guardarAcciones(Pila<String> pila) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO_ACCIONES, true))) {
            pw.println("===== SESION =====");
            // Vaciamos y restauramos la pila usando una auxiliar
            Pila<String> aux = new Pila<>();
            while (!pila.estaVacia()) aux.push(pila.pop());
            while (!aux.estaVacia()) {
                String accion = aux.pop();
                pw.println(accion);
                pila.push(accion);
            }
            System.out.println("[Persistencia] Acciones guardadas -> " + ARCHIVO_ACCIONES);
        } catch (IOException e) {
            System.out.println("[Persistencia] Error al guardar acciones: " + e.getMessage());
        }
    }

    // ── Guardar todo ───────────────────────────────────────────────────────────

    public static void guardarTodo(SistemaTaxis sistema) {
        guardarConductores(sistema.getListaConductores());
        guardarHistorial(sistema.getHistorialServicios());
        guardarAcciones(sistema.getPilaAcciones());
    }
}
