package movilidadcompartida;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VerificacionAdministrador {
    private static int pruebas;
    private static void comprobar(boolean condicion) {
        if (!condicion) throw new AssertionError("Prueba " + (pruebas + 1));
        pruebas++;
    }
    public static void main(String[] args) throws Exception {
        LocalDate fecha = LocalDate.of(2026, 11, 1);
        LocalDateTime inicio = fecha.atStartOfDay();
        Administrador admin = new Administrador("Ana", 20, "111");
        Usuario u = new Usuario("Eva", "1", "eva@mail", fecha);
        Estacion e = new Estacion("E1", "Centro", 0, 0, 10);
        Estacion vacia = new Estacion("E2", "Norte", 0, 0, 10);
        Vehiculo a = new Bicicleta("B1", fecha, 0, EstadoVehiculo.DISPONIBLE, 26);
        Vehiculo b = new Bicicleta("B2", fecha, 0, EstadoVehiculo.DISPONIBLE, 26);
        comprobar(admin.reporteViajesUsuario(u).contains("no tiene viajes"));
        Tarifa t = new Tarifa(fecha, TipoVehiculo.BICICLETA, 200, 20);
        Viaje cancelado = new Viaje(u, a, e, inicio, t);
        cancelado.cancelar(inicio.plusSeconds(30));
        Viaje abierto = new Viaje(u, b, e, inicio.plusHours(1), t);
        List<Viaje> viajes = new ArrayList<>();
        viajes.add(cancelado);
        viajes.add(abierto);
        String detalle = admin.reporteViajesUsuario(u);
        comprobar(detalle.contains("CANCELADO") && detalle.contains("ABIERTO"));
        comprobar(detalle.contains("Sin devolución"));
        List<Estacion> estaciones = new ArrayList<>();
        estaciones.add(e);
        estaciones.add(vacia);
        String uso = admin.reporteUsoEstacion(estaciones, viajes);
        comprobar(uso.contains("E1 | Centro | Retiros: 2 | Devoluciones: 1"));
        comprobar(uso.contains("E2 | Norte | Retiros: 0 | Devoluciones: 0"));
        List<Vehiculo> vehiculos = new ArrayList<>();
        vehiculos.add(a);
        vehiculos.add(b);
        List<ReporteFalla> fallas = new ArrayList<>();
        ReporteFalla atendida = new ReporteFalla(b, "Frenos", u);
        atendida.marcarAtendido();
        fallas.add(atendida);
        fallas.add(new ReporteFalla(b, "Rueda", u));
        fallas.add(new ReporteFalla(a, "Cadena", u));
        String ranking = admin.reporteVehiculosFallas(vehiculos, fallas);
        comprobar(ranking.indexOf("B2") < ranking.indexOf("B1"));
        comprobar(ranking.contains("Fallas: 2"));
        comprobar(vehiculos.get(0) == a);
        List<Personal> personal = new ArrayList<>();
        personal.add(admin);
        personal.add(new Operador("Luis", 2, "222"));
        Collections.sort(personal);
        comprobar(personal.get(0).getLegajo() == 2);
        comprobar(admin.reporteVehiculosFallas(vehiculos, new ArrayList<>()).contains("No hay fallas"));
        System.out.println("OK: " + pruebas + " comprobaciones de reportes y ordenamiento.");
    }
}

