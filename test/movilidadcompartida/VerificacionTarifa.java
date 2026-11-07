
package movilidadcompartida;
import java.time.LocalDate;
public class VerificacionTarifa {
    public static void main(String[] args) {
        LocalDate fecha = LocalDate.now();
        Tarifa comun = new Tarifa(fecha, TipoVehiculo.BICICLETA, 200, 20);
        Tarifa electrica = new Tarifa(fecha, TipoVehiculo.BICICLETA_ELECTRICA, 300, 45);
        Tarifa monopatin = new Tarifa(fecha, TipoVehiculo.MONOPATIN_ELECTRICO, 300, 50);
        if (comun.calcularSaldoMinimo() != 800) { throw new AssertionError("Mínimo común"); }
        if (electrica.calcularSaldoMinimo() != 1650) { throw new AssertionError("Mínimo eléctrica"); }
        if (monopatin.calcularSaldoMinimo() != 1800) { throw new AssertionError("Mínimo monopatín"); }
        System.out.println("OK: saldo mínimo 800, 1650 y 1800 según las tarifas y el supuesto documentado.");
    }
}

