import ui.MenuConsola;

/**
 * Punto de entrada del sistema de gestion de taxis.
 * Instancia el menu de consola y lo inicia.
 */
public class Main {
    public static void main(String[] args) {
        MenuConsola menu = new MenuConsola();
        menu.iniciar();
    }
}
