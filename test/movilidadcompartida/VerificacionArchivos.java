package movilidadcompartida;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class VerificacionArchivos {
    private static int pruebas;
    private static void comprobar(boolean condicion) {
        if (!condicion) throw new AssertionError("Prueba " + (pruebas + 1));
        pruebas++;
    }
    public static void main(String[] args) throws Exception {
        ArchivoSistema archivo = new ArchivoSistema();
        File datos = File.createTempFile("movilidad-prueba-", ".dat");
        datos.delete();
        Sistema s = archivo.cargar(datos.getPath());
        comprobar(s.getUsuarios().isEmpty());
        LocalDate fecha = LocalDate.now();
        LocalDateTime ahora = LocalDateTime.now();
        Usuario u = new Usuario("Ana", "1", "ana@mail", fecha);
        Operador op = new Operador("Eva", 1, "111");
        Tecnico te = new Tecnico("Luis", 2, "222");
        s.registrarUsuario(u);
        s.registrarPersonal(op);
        s.registrarPersonal(te);
        Estacion a = new Estacion("A", "Centro", 0, 0, 5);
        Estacion b = new Estacion("B", "Norte", 0, 0, 5);
        s.registrarEstacion(a);
        s.registrarEstacion(b);
        Vehiculo v = new Bicicleta("V1", fecha, 0, EstadoVehiculo.DISPONIBLE, 26);
        Vehiculo otro = new MonopatinElectrico("V2", fecha, 0, EstadoVehiculo.DISPONIBLE, 80, 25);
        s.registrarVehiculo(v, a);
        s.registrarVehiculo(otro, a);
        MedioPago medio = new MedioPago(TipoMedioPago.TARJETA, "1");
        u.getCuenta().asociarMedioPago(medio);
        u.getCuenta().recargar(10000, medio);
        u.agregarSuscripcion(new Suscripcion(ahora));
        s.registrarTarifa(new Tarifa(fecha, TipoVehiculo.BICICLETA, 200, 20));
        s.registrarPromocion(new Promocion("Mensual", ahora, ahora.plusMonths(1), TipoPromocion.PLAN_MENSUAL));
        Viaje cerrado = s.retirarVehiculo(u, v, a, ahora);
        s.devolverVehiculo(u, v, a, 2, ahora.plusMinutes(20));
        s.registrarNotaAjuste(op, cerrado, "Corrección", "Crédito", 100);
        s.reportarFalla(u, otro, "Rueda");
        s.reportarFalla(op, otro, "Freno");
        s.reportarFalla(u, otro, "Batería");
        OrdenMantenimiento orden = s.generarOrden(op, otro, te, s.getReportesFalla());
        s.registrarTrabajo(te, orden, "Revisado", "");
        List<Vehiculo> seleccion = new ArrayList<>();
        seleccion.add(v);
        s.registrarTraslado(op, a, b, seleccion);
        Viaje abierto = s.retirarVehiculo(u, v, b, ahora.plusHours(1));
        abierto.registrarPunto(-34, -58, ahora.plusHours(1));
        archivo.guardar(s, datos.getPath());
        Sistema recuperado = archivo.cargar(datos.getPath());
        comprobar(recuperado != s);
        Usuario ru = recuperado.buscarUsuario("1");
        Vehiculo rv = recuperado.buscarVehiculo("V1");
        comprobar(ru != u && rv != v);
        comprobar(ru.getCuenta().getSaldo() == u.getCuenta().getSaldo());
        comprobar(ru.getSuscripciones().get(0).getViajesDisponibles() == 19);
        comprobar(recuperado.getViajes().size() == 2);
        Viaje ra = recuperado.buscarViajeAbierto(ru, rv);
        comprobar(ra.getRecorrido().size() == 1 && ra.getUsuario() == ru);
        comprobar(ru.getViajes().get(1) == ra);
        comprobar(recuperado.getNotasAjuste().get(0).getViaje() == ru.getViajes().get(0));
        comprobar(recuperado.getNotasAjuste().get(0).getMovimiento().getViaje() == ru.getViajes().get(0));
        comprobar(recuperado.getTraslados().get(0).getVehiculos().get(0) == rv);
        comprobar(recuperado.getOrdenes().get(0).getReportes().get(0) == recuperado.getReportesFalla().get(0));
        comprobar(recuperado.buscarVehiculo("V2").isMantenimientoObligatorio());
        comprobar(recuperado.getOrdenes().get(0).getTecnico() == recuperado.getPersonal().get(1));
        recuperado.devolverVehiculo(ru, rv, recuperado.buscarEstacion("B"), 1, ahora.plusHours(1).plusMinutes(10));
        comprobar(ra.getEstado() == EstadoViaje.CERRADO);
        Tecnico rt = (Tecnico) recuperado.getPersonal().get(1);
        recuperado.cerrarOrden(rt, recuperado.getOrdenes().get(0), recuperado.getReportesFalla());
        comprobar(!recuperado.buscarVehiculo("V2").isMantenimientoObligatorio());
        archivo.guardar(recuperado, datos.getPath());
        comprobar(archivo.cargar(datos.getPath()).getViajes().get(1).getEstado() == EstadoViaje.CERRADO);
        try (ObjectOutputStream salida = new ObjectOutputStream(new FileOutputStream(datos))) {
            salida.writeObject("No es un sistema");
        }
        try { archivo.cargar(datos.getPath()); throw new AssertionError(); }
        catch (IOException ex) { comprobar(true); }
        datos.delete();
        System.out.println("OK: " + pruebas + " comprobaciones de guardado y recuperación.");
    }
}

