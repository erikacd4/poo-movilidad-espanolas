
package movilidadcompartida;
import java.time.LocalDate;
public class VerificacionEstacion {
    private static int pruebas;
    private static void comprobar(boolean dato) {
        if (!dato) { throw new AssertionError("Comprobación " + (pruebas + 1)); }
        pruebas++;
    }
    public static void main(String[] args) {
        LocalDate fecha = LocalDate.of(2026, 11, 1);
        Estacion e = new Estacion("E1", "Estación centro", -34.6, -58.4, 2);
        Vehiculo a = new Bicicleta("B1", fecha, 0, EstadoVehiculo.DISPONIBLE, 26);
        Vehiculo b = new Bicicleta("B2", fecha, 0, EstadoVehiculo.DISPONIBLE, 26);
        Vehiculo c = new Bicicleta("B3", fecha, 0, EstadoVehiculo.DISPONIBLE, 26);
        comprobar(e.calcularOcupacion() == 0 && e.tieneCapacidad());
        e.agregarVehiculo(a);
        comprobar(e.calcularOcupacion() == 50 && e.tieneCapacidad());
        try { e.agregarVehiculo(a); throw new AssertionError("Duplicado aceptado"); }
        catch (IllegalArgumentException ex) { comprobar(e.calcularOcupacion() == 50); }
        e.agregarVehiculo(b);
        comprobar(e.calcularOcupacion() == 100 && !e.tieneCapacidad());
        try { e.agregarVehiculo(c); throw new AssertionError("Se excedió la capacidad"); }
        catch (IllegalArgumentException ex) { comprobar(e.getVehiculos().size() == 2); }
        e.getVehiculos().clear();
        comprobar(e.getVehiculos().size() == 2);
        a.cambiarEstado(EstadoVehiculo.EN_USO);
        e.retirarVehiculo(a);
        comprobar(e.getVehiculos().get(0) == b && e.calcularOcupacion() == 50);
        try { e.retirarVehiculo(c); throw new AssertionError("Retiro inexistente"); }
        catch (IllegalArgumentException ex) { comprobar(e.getVehiculos().size() == 1); }
        e.retirarVehiculo(b);
        comprobar(e.getVehiculos().isEmpty() && e.calcularOcupacion() == 0);
        System.out.println("OK: " + pruebas + " comprobaciones de capacidad, ocupación y lista.");
    }
}

