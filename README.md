# Sistema de Gestión de Taxis - Cooperativa Multizona

## Descripción
Sistema de consola en Java para gestionar solicitudes de servicio de taxi en una ciudad dividida en zonas geográficas conectadas por vías. Desarrollado para los cursos de POO y Estructuras de Datos (2026-I).

## Requisitos funcionales implementados
- Registro de solicitudes con zona origen, destino, tipo de servicio y timestamp
- Cola de espera FIFO para solicitudes pendientes
- Asignación inteligente de conductores (disponibilidad, tipo de servicio, conectividad)
- Cálculo de tarifa por distancia (Dijkstra sobre grafo de zonas)
- Cálculo de tiempo de recogida (5 min si misma zona, proporcional si diferente)
- Cancelación de solicitudes con motivo obligatorio
- Finalización de servicios con liberación del conductor
- Historial completo de servicios cerrados
- Pila de auditoría de acciones del sistema
- Cierres y habilitaciones de vías dinámicas
- Persistencia en archivos .txt (guardar/cargar)

## Estructuras de datos implementadas manualmente
| Estructura | Archivos | Uso |
|---|---|---|
| Lista Simple Enlazada | `estructuras/Nodo.java`, `ListaSimple.java` | Conductores, historial |
| Cola Dinámica (FIFO) | `estructuras/Cola.java` | Solicitudes en espera |
| Pila Dinámica (LIFO) | `estructuras/Pila.java` | Auditoría de acciones |
| Grafo Ponderado | `estructuras/Vertice.java`, `Arista.java`, `ListaAdyacencia.java`, `Grafo.java` | Red vial de zonas |

## Patrones de diseño aplicados
- **Singleton** (`SistemaTaxis`): garantiza una única instancia que gestiona todo el estado global del sistema. Evita inconsistencias por múltiples instancias concurrentes.
- **Strategy** (`EstrategiaTarifa`): encapsula el algoritmo de cálculo de tarifa por tipo de servicio. Permite agregar nuevos tipos sin modificar `SistemaTaxis`, cumpliendo directamente el principio OCP.

## Principios SOLID aplicados
- **SRP** — cada clase tiene una sola responsabilidad: `modelo` define entidades, `logica` coordina el flujo, `GestorAsignacion` resuelve la búsqueda y cálculos, `persistencia` maneja archivos, `ui` gestiona la interacción con el usuario.
- **OCP** — el sistema es abierto para extensión y cerrado para modificación: agregar un nuevo tipo de servicio solo requiere crear una nueva clase que implemente `EstrategiaTarifa`, sin tocar `SistemaTaxis`.
- **DIP** — `MenuConsola` depende de `SistemaTaxis` como coordinador central, no de los detalles de asignación o cálculo que viven en `GestorAsignacion`.

## Estructura del proyecto
src/
├── Main.java
├── estructuras/
│   ├── Nodo.java
│   ├── ListaSimple.java
│   ├── Cola.java
│   ├── Pila.java
│   ├── Vertice.java
│   ├── Arista.java
│   ├── ListaAdyacencia.java
│   └── Grafo.java
├── modelo/
│   ├── TipoServicio.java
│   ├── EstadoSolicitud.java
│   ├── EstadoConductor.java
│   ├── Conductor.java
│   └── Solicitud.java
├── excepcion/
│   ├── SistemaTaxisException.java
│   ├── ZonaInexistenteException.java
│   ├── SinConectividadException.java
│   ├── ConductorNoDisponibleException.java
│   └── SolicitudInvalidaException.java
├── logica/
│   ├── EstrategiaTarifa.java
│   ├── TarifaEstandar.java
│   ├── TarifaBaul.java
│   ├── TarifaMascotas.java
│   ├── GestorAsignacion.java
│   └── SistemaTaxis.java
├── persistencia/
│   └── Persistencia.java
└── ui/
└── MenuConsola.java

## Instrucciones de compilación y ejecución

### Compilar
```bash
mkdir -p bin
javac -encoding UTF-8 -d bin \
  src/estructuras/*.java \
  src/modelo/*.java \
  src/excepcion/*.java \
  src/logica/*.java \
  src/persistencia/*.java \
  src/ui/*.java \
  src/Main.java
```

### Ejecutar
```bash
java -cp bin Main
```

## Zonas preconfiguradas
Norte, Sur, Centro, Oriente, Occidente, Aeropuerto

## Conductores preconfigurados
| Nombre | Placa | Zona inicial | Servicios habilitados |
|---|---|---|---|
| Carlos Perez | ABC-123 | Centro | Estándar, Baúl |
| Maria Lopez | XYZ-456 | Norte | Estándar, Mascotas |
| Juan Torres | DEF-789 | Sur | Estándar |
| Ana Gomez | GHI-321 | Oriente | Estándar, Baúl, Mascotas |
| Pedro Ruiz | JKL-654 | Aeropuerto | Estándar, Baúl |

## Evidencias
Al ejecutar el sistema se pueden verificar los siguientes flujos:
1. Registrar una solicitud y verificar que queda en la cola de espera
2. Atender la solicitud y confirmar la asignación de conductor con tarifa y tiempos
3. Finalizar el servicio y verificar que el conductor queda disponible en la nueva zona
4. Cancelar una solicitud con motivo y verificar que pasa al historial
5. Cerrar una vía y confirmar que Dijkstra recalcula la ruta evitando esa conexión
6. Revisar la pila de auditoría con todas las acciones en orden inverso
7. Salir del sistema y verificar que se generan los archivos `conductores.txt`, `historial.txt` y `acciones.txt`