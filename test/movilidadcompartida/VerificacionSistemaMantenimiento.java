package movilidadcompartida;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class VerificacionSistemaMantenimiento {
    private static int pruebas;
    private static void comprobar(boolean condicion) {
        if (!condicion) throw new AssertionError("Prueba " + (pruebas + 1));
        pruebas++;
    }
    public static void main(String[] args) throws Exception {
        Sistema s = new Sistema();
        LocalDate fecha = LocalDate.now();
        LocalDateTime inicio = LocalDateTime.now();
        Usuario u = new Usuario("Ana", "1", "ana@mail", fecha);
        s.registrarUsuario(u);
        Operador op = new Operador("Eva", 3, "111");
        Tecnico te = new Tecnico("Luis", 2, "222");
        Tecnico otro = new Tecnico("Juan", 1, "333");
        s.registrarPersonal(op);
        s.registrarPersonal(te);
        s.registrarPersonal(otro);
        comprobar(s.getPersonal().get(0) == otro);
        try { s.registrarPersonal(te); throw new AssertionError(); }
        catch (ReglaNegocioException ex) { comprobar(s.getPersonal().size() == 3); }
        Estacion a = new Estacion("A", "Centro", 0, 0, 2);
        Estacion b = new Estacion("B", "Norte", 0, 0, 2);
        s.registrarEstacion(a);
        s.registrarEstacion(b);
        Vehiculo v = new Bicicleta("V1", fecha, 0, EstadoVehiculo.DISPONIBLE, 26);
        s.registrarVehiculo(v, a);
        s.reportarFalla(u, v, "Frenos");
        s.reportarFalla(op, v, "Cadena");
        comprobar(!v.isMantenimientoObligatorio());
        s.reportarFalla(u, v, "Rueda");
        comprobar(v.isMantenimientoObligatorio() && v.getEstado() == EstadoVehiculo.FUERA_DE_SERVICIO);
        try { s.gestionarEstado(op, v, EstadoVehiculo.DISPONIBLE); throw new AssertionError(); }
        catch (ReglaNegocioException ex) { comprobar(v.isMantenimientoObligatorio()); }
        List<ReporteFalla> fallas = s.getReportesFalla();
        OrdenMantenimiento orden = s.generarOrden(op, v, te, fallas);
        try { s.registrarTrabajo(otro, orden, "Revisión", ""); throw new AssertionError(); }
        catch (ReglaNegocioException ex) { comprobar(orden.getTareasRealizadas().equals("")); }
        try { s.cerrarOrden(te, orden, fallas); throw new AssertionError(); }
        catch (ReglaNegocioException ex) { comprobar(!orden.isCerrada()); }
        s.registrarTrabajo(te, orden, "Frenos reparados", "Pastillas");
        comprobar(v.getEstado() == EstadoVehiculo.EN_REPARACION);
        List<ReporteFalla> primera = new ArrayList<>();
        primera.add(fallas.get(0));
        s.cerrarOrden(te, orden, primera);
        comprobar(orden.isCerrada() && v.isMantenimientoObligatorio());
        comprobar(fallas.get(0).isAtendido() && !fallas.get(1).isAtendido());
        OrdenMantenimiento segunda = s.generarOrden(op, v, te, fallas);
        s.registrarTrabajo(te, segunda, "Cadena y rueda reparadas", "Cadena");
        s.cerrarOrden(te, segunda, fallas);
        comprobar(!v.isMantenimientoObligatorio() && v.getEstado() == EstadoVehiculo.EN_REPARACION);
        s.gestionarEstado(op, v, EstadoVehiculo.DISPONIBLE);
        comprobar(v.puedeRetirarse() && s.getReportesFalla().size() == 3);
        List<Vehiculo> seleccion = new ArrayList<>();
        seleccion.add(v);
        s.registrarTraslado(op, a, b, seleccion);
        comprobar(s.getTraslados().size() == 1 && b.getVehiculos().contains(v));
        comprobar(s.getViajes().isEmpty() && a.getVehiculos().isEmpty());
        try { s.registrarTraslado(op, a, b, seleccion); throw new AssertionError(); }
        catch (ReglaNegocioException ex) { comprobar(s.getTraslados().size() == 1); }
        MedioPago medio = new MedioPago(TipoMedioPago.TARJETA, "1");
        u.getCuenta().asociarMedioPago(medio);
        u.getCuenta().recargar(1000, medio);
        s.registrarTarifa(new Tarifa(fecha, TipoVehiculo.BICICLETA, 200, 20));
        Viaje viaje = s.retirarVehiculo(u, v, b, inicio);
        try { s.gestionarEstado(op, v, EstadoVehiculo.FUERA_DE_SERVICIO); throw new AssertionError(); }
        catch (ReglaNegocioException ex) { comprobar(v.getEstado() == EstadoVehiculo.EN_USO); }
        s.devolverVehiculo(u, v, b, 2, inicio.plusMinutes(10));
        double importe = viaje.getImporte();
        double saldo = u.getCuenta().getSaldo();
        int movimientos = u.getCuenta().getMovimientos().size();
        NotaAjuste nota = s.registrarNotaAjuste(op, viaje, "Corrección", "Crédito", 100);
        comprobar(nota.isRegistrada() && s.getNotasAjuste().size() == 1);
        comprobar(viaje.getImporte() == importe && u.getCuenta().getSaldo() == saldo + 100);
        comprobar(u.getCuenta().getMovimientos().size() == movimientos + 1);
        u.getCuenta().recargar(100, medio);
        Viaje perdido = s.retirarVehiculo(u, v, b, inicio.plusHours(1));
        comprobar(s.comprobarNoDevueltos(op, inicio.plusHours(25)).contains(perdido));
        double saldoPerdida = u.getCuenta().getSaldo();
        s.comprobarNoDevueltos(op, inicio.plusHours(26));
        comprobar(u.getCuenta().getSaldo() == saldoPerdida && perdido.isCargoPerdidaAplicado());
        comprobar(perdido.getEstado() == EstadoViaje.ABIERTO && perdido.getFin() == null);
        comprobar(v.isReportadoPerdido() && !u.estaHabilitado());
        try { s.reportarFalla(new Operador("Ajeno", 9, "9"), v, "Falla"); throw new AssertionError(); }
        catch (ReglaNegocioException ex) { comprobar(s.getReportesFalla().size() == 3); }
        s.getOrdenes().clear();
        comprobar(s.getOrdenes().size() == 2);
        System.out.println("OK: " + pruebas + " comprobaciones de mantenimiento integrado.");
    }
}

