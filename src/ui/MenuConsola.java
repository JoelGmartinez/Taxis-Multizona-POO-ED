package ui;

import excepcion.*;
import logica.SistemaTaxis;
import modelo.*;
import persistencia.Persistencia;
import estructuras.*;

import java.util.Scanner;

/**
 * Interfaz de usuario por consola.
 * Responsabilidad unica: interaccion con el usuario (SRP).
 * Usa Scanner para leer entradas, que es la herramienta
 * estandar de Java para lectura de consola.
 */
public class MenuConsola {

    private SistemaTaxis sistema;
    private Scanner sc;
    private ListaSimple<Solicitud> solicitudesActivas;

    public MenuConsola() {
        this.sistema = SistemaTaxis.getInstancia();
        this.sc = new Scanner(System.in);
        this.solicitudesActivas = new ListaSimple<>();
    }

    public void iniciar() {
        mostrarBienvenida();
        int opcion = -1;
        do {
            mostrarMenuPrincipal();
            opcion = leerEntero("Seleccione una opcion: ");
            procesarOpcion(opcion);
        } while (opcion != 0);
        sc.close();
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  MENU PRINCIPAL
    // ─────────────────────────────────────────────────────────────────────────

    private void mostrarMenuPrincipal() {
        System.out.println("\n+========================================+");
        System.out.println("|     COOPERATIVA DE TAXIS - MENU        |");
        System.out.println("+========================================+");
        System.out.println("|  1. Registrar nueva solicitud          |");
        System.out.println("|  2. Atender siguiente solicitud        |");
        System.out.println("|  3. Ver solicitudes en espera          |");
        System.out.println("|  4. Cancelar solicitud                 |");
        System.out.println("|  5. Ver servicios activos              |");
        System.out.println("|  6. Finalizar servicio                 |");
        System.out.println("|  7. Ver historial de servicios         |");
        System.out.println("|  8. Ver auditoria de acciones          |");
        System.out.println("|----------------------------------------|");
        System.out.println("|  9. Gestionar conductores              |");
        System.out.println("| 10. Gestionar red vial                 |");
        System.out.println("|----------------------------------------|");
        System.out.println("|  0. Guardar y salir                    |");
        System.out.println("+========================================+");
    }

    private void procesarOpcion(int opcion) {
        switch (opcion) {
            case 1:  menuRegistrarSolicitud(); break;
            case 2:  menuAtenderSolicitud();   break;
            case 3:  menuVerCola();            break;
            case 4:  menuCancelarSolicitud();  break;
            case 5:  menuVerActivas();         break;
            case 6:  menuFinalizarServicio();  break;
            case 7:  menuVerHistorial();       break;
            case 8:  menuVerAuditoria();       break;
            case 9:  menuConductores();        break;
            case 10: menuRedVial();            break;
            case 0:  menuSalir();              break;
            default: System.out.println("  [!] Opcion no valida.");
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  1. REGISTRAR SOLICITUD
    // ─────────────────────────────────────────────────────────────────────────


    private void menuRegistrarSolicitud() {
        titulo("REGISTRAR NUEVA SOLICITUD");

        System.out.println("Zonas disponibles:");
        sistema.getGrafoVial().listarZonas();

        String origen  = leerTexto("Zona de origen : ") .toUpperCase();
        String destino = leerTexto("Zona de destino: ") .toUpperCase();

        System.out.println("Tipos de servicio:");
        System.out.println("  1. Taxi Estandar");
        System.out.println("  2. Taxi con Baul/Parrilla  (+$3.000)");
        System.out.println("  3. Taxi Pet-Friendly       (+$4.000)");
        int op = leerEntero("Seleccione tipo: ");

        TipoServicio tipo;
        switch (op) {
            case 2:  tipo = TipoServicio.BAUL;     break;
            case 3:  tipo = TipoServicio.MASCOTAS; break;
            default: tipo = TipoServicio.ESTANDAR; break;
        }

        try {
            Solicitud s = sistema.registrarSolicitud(origen, destino, tipo);
            System.out.println("\n  [OK] Solicitud registrada:");
            System.out.println("  " + s.resumenCorto());
        } catch (SistemaTaxisException e) {
            System.out.println("\n  [ERROR] " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  2. ATENDER SIGUIENTE SOLICITUD
    // ─────────────────────────────────────────────────────────────────────────

    private void menuAtenderSolicitud() {
        titulo("ATENDER SIGUIENTE SOLICITUD");

        if (sistema.getColaSolicitudes().estaVacia()) {
            System.out.println("  No hay solicitudes en espera.");
            return;
        }

        System.out.println("  Siguiente: "
                + sistema.getColaSolicitudes().verFrente().resumenCorto());
        String confirmar = leerTexto("  Confirmar atencion? (s/n): ");
        if (!confirmar.equalsIgnoreCase("s")) {
            System.out.println("  Operacion cancelada.");
            return;
        }

        try {
            Solicitud s = sistema.atenderSiguienteSolicitud();
            solicitudesActivas.insertarFin(s);

            System.out.println("\n  +-----------------------------------+");
            System.out.println("  |        SERVICIO ASIGNADO          |");
            System.out.println("  +-----------------------------------+");
            System.out.printf ("  | Solicitud  : #%-20d|%n", s.getId());
            System.out.printf ("  | Conductor  : %-20s |%n", s.getConductorAsignado().getNombre());
            System.out.printf ("  | Cedula     : %-20s |%n", s.getConductorAsignado().getCedula());
            System.out.printf ("  | Placa      : %-20s |%n", s.getConductorAsignado().getPlaca());
            System.out.printf ("  | Origen     : %-20s |%n", s.getZonaOrigen());
            System.out.printf ("  | Destino    : %-20s |%n", s.getZonaDestino());
            System.out.printf ("  | T.Recogida : %-17s min |%n", s.getTiempoEstimadoRecogida());
            System.out.printf ("  | T.Viaje    : %-17s min |%n", s.getTiempoEstimadoViaje());
            System.out.printf ("  | Tarifa     : $%-16.0f COP |%n", s.getTarifaCalculada());
            System.out.println("  +-----------------------------------+");

        } catch (SistemaTaxisException e) {
            System.out.println("\n  [ERROR] " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  3. VER COLA DE ESPERA
    // ─────────────────────────────────────────────────────────────────────────

    private void menuVerCola() {
        titulo("SOLICITUDES EN ESPERA");
        if (sistema.getColaSolicitudes().estaVacia()) {
            System.out.println("  La cola esta vacia.");
        } else {
            System.out.println("  Total: " + sistema.getColaSolicitudes().size());
            sistema.getColaSolicitudes().recorrer();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  4. CANCELAR SOLICITUD
    // ─────────────────────────────────────────────────────────────────────────

    private void menuCancelarSolicitud() {
        titulo("CANCELAR SOLICITUD");

        if (sistema.getColaSolicitudes().estaVacia()) {
            System.out.println("  No hay solicitudes en espera para cancelar.");
            return;
        }

        System.out.println("  Solicitudes en espera:");
        sistema.getColaSolicitudes().recorrer();

        int id     = leerEntero("  ID de la solicitud a cancelar: ");
        String mot = leerTexto ("  Motivo de cancelacion        : ");

        try {
            sistema.cancelarSolicitud(id, mot);
            System.out.println("  [OK] Solicitud #" + id + " cancelada.");
        } catch (SistemaTaxisException e) {
            System.out.println("  [ERROR] " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  5. VER SERVICIOS ACTIVOS
    // ─────────────────────────────────────────────────────────────────────────

    private void menuVerActivas() {
        titulo("SERVICIOS EN ATENCION");
        boolean hayActivos = false;
        for (int i = 0; i < solicitudesActivas.size(); i++) {
            Solicitud s = solicitudesActivas.obtener(i);
            if (s.getEstado() == EstadoSolicitud.EN_ATENCION) {
                System.out.println("  " + s.resumenCorto()
                        + " | Conductor: " + s.getConductorAsignado().getNombre());
                hayActivos = true;
            }
        }
        if (!hayActivos)
            System.out.println("  No hay servicios activos en este momento.");
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  6. FINALIZAR SERVICIO
    // ─────────────────────────────────────────────────────────────────────────

    private void menuFinalizarServicio() {
        titulo("FINALIZAR SERVICIO");

        boolean hayActivos = false;
        for (int i = 0; i < solicitudesActivas.size(); i++) {
            Solicitud s = solicitudesActivas.obtener(i);
            if (s.getEstado() == EstadoSolicitud.EN_ATENCION) {
                System.out.println("  " + s.resumenCorto());
                hayActivos = true;
            }
        }

        if (!hayActivos) {
            System.out.println("  No hay servicios activos para finalizar.");
            return;
        }

        int id = leerEntero("  ID de la solicitud a finalizar: ");

        Solicitud objetivo = null;
        for (int i = 0; i < solicitudesActivas.size(); i++) {
            Solicitud s = solicitudesActivas.obtener(i);
            if (s.getId() == id && s.getEstado() == EstadoSolicitud.EN_ATENCION) {
                objetivo = s;
                break;
            }
        }

        if (objetivo == null) {
            System.out.println("  [ERROR] No se encontro servicio activo con ID #" + id);
            return;
        }

        try {
            sistema.finalizarServicio(objetivo);
            System.out.println("  [OK] Servicio #" + id + " finalizado.");
            System.out.printf ("  Conductor %s ahora en zona %s.%n",
                    objetivo.getConductorAsignado().getNombre(),
                    objetivo.getConductorAsignado().getZonaActual());
        } catch (SistemaTaxisException e) {
            System.out.println("  [ERROR] " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  7. HISTORIAL
    // ─────────────────────────────────────────────────────────────────────────

    private void menuVerHistorial() {
        titulo("HISTORIAL DE SERVICIOS");
        if (sistema.getHistorialServicios().estaVacia()) {
            System.out.println("  El historial esta vacio.");
            return;
        }
        System.out.println("  Total de registros: " + sistema.getHistorialServicios().size());
        separador();
        for (int i = 0; i < sistema.getHistorialServicios().size(); i++) {
            System.out.println(sistema.getHistorialServicios().obtener(i).toString());
            separador();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  8. AUDITORIA (PILA)
    // ─────────────────────────────────────────────────────────────────────────

    private void menuVerAuditoria() {
        titulo("AUDITORIA DE ACCIONES (mas reciente primero)");
        if (sistema.getPilaAcciones().estaVacia())
            System.out.println("  No hay acciones registradas.");
        else
            sistema.getPilaAcciones().recorrer();
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  9. CONDUCTORES
    // ─────────────────────────────────────────────────────────────────────────

    private void menuConductores() {
        int op;
        do {
            System.out.println("\n--- GESTION DE CONDUCTORES ---");
            System.out.println("  1. Ver todos los conductores");
            System.out.println("  2. Registrar nuevo conductor");
            System.out.println("  3. Buscar conductor por cedula");
            System.out.println("  0. Volver");
            op = leerEntero("Opcion: ");
            switch (op) {
                case 1: listarConductores();  break;
                case 2: registrarConductor(); break;
                case 3: buscarConductor();    break;
            }
        } while (op != 0);
    }

    private void listarConductores() {
        titulo("LISTA DE CONDUCTORES");
        if (sistema.getListaConductores().estaVacia()) {
            System.out.println("  No hay conductores registrados.");
            return;
        }
        for (int i = 0; i < sistema.getListaConductores().size(); i++) {
            Conductor c = sistema.getListaConductores().obtener(i);
            System.out.printf("  %d. %-15s | Placa: %-8s | Zona: %-12s | Estado: %-10s | %s%n",
                    i + 1, c.getNombre(), c.getPlaca(), c.getZonaActual(),
                    c.getEstado(), c.getServiciosTexto());
        }
    }

    private void registrarConductor() {
        titulo("REGISTRAR CONDUCTOR");
        String cedula = leerTexto("Cedula  : ");
        if (sistema.buscarConductorPorCedula(cedula) != null) {
            System.out.println("  [ERROR] Ya existe un conductor con esa cedula.");
            return;
        }
        String nombre = leerTexto("Nombre  : ");
        String placa  = leerTexto("Placa   : ");

        System.out.println("Zonas disponibles:");
        sistema.getGrafoVial().listarZonas();
        String zona = leerTexto("Zona actual: ").toUpperCase();

        if (!sistema.getGrafoVial().existeZona(zona)) {
            System.out.println("  [ERROR] Zona no existe.");
            return;
        }

        Conductor c = new Conductor(cedula, nombre, placa, zona);

        System.out.println("Habilitar servicios (s/n):");
        if (leerTexto("  Taxi Estandar  ? ").equalsIgnoreCase("s"))
            c.habilitarServicio(TipoServicio.ESTANDAR);
        if (leerTexto("  Taxi Baul      ? ").equalsIgnoreCase("s"))
            c.habilitarServicio(TipoServicio.BAUL);
        if (leerTexto("  Taxi Mascotas  ? ").equalsIgnoreCase("s"))
            c.habilitarServicio(TipoServicio.MASCOTAS);

        sistema.agregarConductor(c);
        System.out.println("  [OK] Conductor " + nombre + " registrado.");
    }

    private void buscarConductor() {
        String cedula = leerTexto("Cedula del conductor: ");
        Conductor c = sistema.buscarConductorPorCedula(cedula);
        if (c == null)
            System.out.println("  [ERROR] No se encontro conductor con cedula " + cedula);
        else
            System.out.println("  " + c.toString());
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  10. RED VIAL
    // ─────────────────────────────────────────────────────────────────────────

    private void menuRedVial() {
        int op;
        do {
            System.out.println("\n--- GESTION DE RED VIAL ---");
            System.out.println("  1. Ver mapa de zonas y vias");
            System.out.println("  2. Cerrar una via");
            System.out.println("  3. Habilitar una via");
            System.out.println("  4. Consultar ruta entre dos zonas");
            System.out.println("  0. Volver");
            op = leerEntero("Opcion: ");
            switch (op) {
                case 1: verMapa();       break;
                case 2: cerrarVia();     break;
                case 3: habilitarVia();  break;
                case 4: consultarRuta(); break;
            }
        } while (op != 0);
    }

    private void verMapa() {
        titulo("RED VIAL");
        sistema.getGrafoVial().mostrar();
    }

    private void cerrarVia() {
        titulo("CERRAR VIA");
        sistema.getGrafoVial().listarZonas();
        String origen  = leerTexto("Zona origen : ").toUpperCase();
        String destino = leerTexto("Zona destino: ").toUpperCase();
        try {
            sistema.cerrarVia(origen, destino);
            System.out.println("  [OK] Via " + origen + " <-> " + destino + " CERRADA.");
        } catch (SistemaTaxisException e) {
            System.out.println("  [ERROR] " + e.getMessage());
        }
    }

    private void habilitarVia() {
        titulo("HABILITAR VIA");
        sistema.getGrafoVial().listarZonas();
        String origen  = leerTexto("Zona origen : ").toUpperCase();
        String destino = leerTexto("Zona destino: ").toUpperCase();
        try {
            sistema.habilitarVia(origen, destino);
            System.out.println("  [OK] Via " + origen + " <-> " + destino + " HABILITADA.");
        } catch (SistemaTaxisException e) {
            System.out.println("  [ERROR] " + e.getMessage());
        }
    }

    private void consultarRuta() {
        titulo("CONSULTAR RUTA");
        sistema.getGrafoVial().listarZonas();
        String origen  = leerTexto("Zona origen : ").toUpperCase();
        String destino = leerTexto("Zona destino: ").toUpperCase();

        if (!sistema.getGrafoVial().existeZona(origen) ||
            !sistema.getGrafoVial().existeZona(destino)) {
            System.out.println("  [ERROR] Una o ambas zonas no existen.");
            return;
        }

        double dist = sistema.getGrafoVial().dijkstra(origen, destino);
        if (dist == Double.POSITIVE_INFINITY) {
            System.out.println("  [!] No hay ruta habilitada entre " + origen + " y " + destino);
        } else {
            System.out.println("  Distancia optima: " + (int) dist + " metros");
            sistema.getGrafoVial().mostrarDistanciasDijkstra(origen);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  SALIDA
    // ─────────────────────────────────────────────────────────────────────────

    private void menuSalir() {
        System.out.println("\n  Guardando datos del sistema...");
        Persistencia.guardarTodo(sistema);
        System.out.println("  Hasta luego.");
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  UTILIDADES
    // ─────────────────────────────────────────────────────────────────────────

    private void mostrarBienvenida() {
        System.out.println("+==========================================+");
        System.out.println("|   SISTEMA DE GESTION - COOPERATIVA TAXI  |");
        System.out.println("|           Version 2.0 - 2026             |");
        System.out.println("+==========================================+");
    }

    private void titulo(String texto) {
        System.out.println("\n=== " + texto + " ===");
    }

    private void separador() {
        System.out.println("  ----------------------------------------");
    }

    private int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("  [!] Ingrese un numero entero valido.");
            }
        }
    }

    private String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return sc.nextLine().trim();
    }
}
