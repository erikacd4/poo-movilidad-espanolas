package movilidadcompartida;

import java.io.IOException;

public class Principal {
    public static void main(String[] args) {
        ArchivoSistema archivo = new ArchivoSistema();
        try {
            Sistema sistema = archivo.cargar("movilidad.dat");
            MenuConsola menu = new MenuConsola(sistema, archivo, "movilidad.dat");
            menu.ejecutar();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("No se pudo cargar el archivo: " + e.getMessage());
            System.out.println("No se inició un sistema vacío para evitar reemplazar los datos existentes.");
        }
    }
}
