
package movilidadcompartida;
import java.time.LocalDate;
public class VerificacionUsuario {
    private static int pruebas;
    private static void comprobar(boolean condicion) {
        if (!condicion) { throw new AssertionError("Comprobación " + (pruebas + 1)); }
        pruebas++;
    }
    public static void main(String[] args) {
        Usuario usuario = new Usuario("Ana", "DOC1", "ana@ejemplo.com", LocalDate.now());
        comprobar(usuario.getCuenta().getSaldo() == 0 && usuario.estaHabilitado());
        comprobar(!usuario.isPrimerViajeRealizado());
        MedioPago tarjeta = new MedioPago(TipoMedioPago.TARJETA, "T1");
        usuario.getCuenta().asociarMedioPago(tarjeta);
        usuario.getCuenta().recargar(1000, tarjeta);
        usuario.actualizarHabilitacion();
        comprobar(usuario.estaHabilitado() && !usuario.isPrimerViajeRealizado());
        usuario.getCuenta().debitar(1200);
        usuario.actualizarHabilitacion();
        comprobar(!usuario.estaHabilitado() && usuario.getCuenta().getSaldo() == -200);
        usuario.getCuenta().recargar(100, tarjeta);
        usuario.actualizarHabilitacion();
        comprobar(!usuario.estaHabilitado());
        usuario.getCuenta().recargar(100, tarjeta);
        usuario.actualizarHabilitacion();
        comprobar(usuario.estaHabilitado() && usuario.getCuenta().getSaldo() == 0);
        usuario.marcarPrimerViajeRealizado();
        comprobar(usuario.isPrimerViajeRealizado());
        System.out.println("OK: " + pruebas + " comprobaciones de cuenta, habilitación y primer viaje.");
    }
}

