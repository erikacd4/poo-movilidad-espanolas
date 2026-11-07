
package movilidadcompartida;
import java.time.LocalDate;
public class VerificacionReportes {
    private static int pruebas;
    private static void comprobar(boolean condicion) {
        if (!condicion) { throw new AssertionError("Comprobación " + (pruebas + 1)); }
        pruebas++;
    }
    public static void main(String[] args) {
        Usuario usuario = new Usuario("Ana", "DOC1", "ana@ejemplo.com", LocalDate.now());
        Vehiculo vehiculo = new Bicicleta("B1", LocalDate.now(), 0, EstadoVehiculo.DISPONIBLE, 26);
        Operador operador = new Operador("Luis", 1, "111");
        ReporteFalla deUsuario = new ReporteFalla(vehiculo, "Falla de frenos", usuario);
        ReporteFalla deOperador = new ReporteFalla(vehiculo, "Falla de rueda", operador);
        comprobar(deUsuario.getUsuario() == usuario && deUsuario.getOperador() == null);
        comprobar(deOperador.getOperador() == operador && deOperador.getUsuario() == null);
        comprobar(deUsuario.getVehiculo() == vehiculo && !deUsuario.isAtendido());
        comprobar(deOperador.getVehiculo() == vehiculo && !deOperador.isAtendido());
        String descripcion = deUsuario.getDescripcion();
        deUsuario.marcarAtendido();
        comprobar(deUsuario.isAtendido() && deUsuario.getDescripcion().equals(descripcion));
        comprobar(deUsuario.getFecha() != null && !deOperador.isAtendido());
        System.out.println("OK: " + pruebas + " comprobaciones de autor, vehículo e historial de fallas.");
    }
}

