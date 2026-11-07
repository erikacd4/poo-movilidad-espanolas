
package movilidadcompartida;
import java.time.LocalDate;
public class VerificacionVehiculos {
    private static int comprobaciones;
    private static void comprobar(boolean condicion) {
        if (!condicion) { throw new AssertionError("Comprobación " + (comprobaciones + 1)); }
        comprobaciones++;
    }
    public static void main(String[] args) {
        LocalDate fecha = LocalDate.of(2026, 11, 1);
        Vehiculo comun = new Bicicleta("C1", fecha, 10, EstadoVehiculo.DISPONIBLE, 26);
        BicicletaElectrica bici = new BicicletaElectrica("E1", fecha, 0, EstadoVehiculo.DISPONIBLE, 15, 40);
        MonopatinElectrico mono = new MonopatinElectrico("M1", fecha, 0, EstadoVehiculo.DISPONIBLE, 15, 25);
        Vehiculo[] vehiculos = {comun, bici, mono};
        for (Vehiculo v : vehiculos) {
            comprobar(v.puedeRetirarse());
            v.setMantenimientoObligatorio(true);
            comprobar(!v.puedeRetirarse());
            v.setMantenimientoObligatorio(false);
            v.setReportadoPerdido(true);
            comprobar(!v.puedeRetirarse());
            v.setReportadoPerdido(false);
            v.cambiarEstado(EstadoVehiculo.EN_USO);
            comprobar(!v.puedeRetirarse());
            v.cambiarEstado(EstadoVehiculo.DISPONIBLE);
        }
        bici.setNivelBateria(14.99);
        mono.setNivelBateria(14.99);
        comprobar(!vehiculos[1].puedeRetirarse());
        comprobar(!vehiculos[2].puedeRetirarse());
        comprobar(bici.getEstado() == EstadoVehiculo.DISPONIBLE);
        bici.setNivelBateria(15);
        mono.setNivelBateria(15);
        comprobar(bici.puedeRetirarse() && mono.puedeRetirarse());
        comun.agregarKilometros(2.5);
        comprobar(comun.getKilometrosRecorridos() == 12.5);
        comprobar(comun.obtenerTipo() == TipoVehiculo.BICICLETA);
        comprobar(bici.obtenerTipo() == TipoVehiculo.BICICLETA_ELECTRICA);
        comprobar(mono.obtenerTipo() == TipoVehiculo.MONOPATIN_ELECTRICO);
        System.out.println("OK: " + comprobaciones + " comprobaciones de retiro, batería, estado y kilómetros.");
    }
}

