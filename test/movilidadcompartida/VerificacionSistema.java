package movilidadcompartida;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class VerificacionSistema {
    private static int pruebas;
    private static void comprobar(boolean condicion) {
        if (!condicion) throw new AssertionError("Prueba " + (pruebas + 1));
        pruebas++;
    }
    public static void main(String[] args) throws Exception {
        LocalDate fecha = LocalDate.of(2026, 11, 1);
        LocalDateTime inicio = LocalDateTime.of(2026, 11, 1, 10, 0);
        Sistema s = new Sistema();
        Usuario u = new Usuario("Ana", "1", "ana@mail", fecha);
        Usuario otro = new Usuario("Eva", "2", "eva@mail", fecha);
        s.registrarUsuario(u);
        s.registrarUsuario(otro);
        Estacion e = new Estacion("E1", "Centro", 0, 0, 2);
        Estacion llena = new Estacion("E2", "Norte", 0, 0, 1);
        s.registrarEstacion(e);
        s.registrarEstacion(llena);
        Vehiculo b = new Bicicleta("B1", fecha, 0, EstadoVehiculo.DISPONIBLE, 26);
        Vehiculo c = new Bicicleta("B2", fecha, 0, EstadoVehiculo.DISPONIBLE, 26);
        s.registrarVehiculo(b, e);
        s.registrarVehiculo(c, llena);
        comprobar(s.buscarUsuario("1") == u && s.buscarVehiculo("B1") == b);
        try { s.registrarUsuario(u); throw new AssertionError(); }
        catch (ReglaNegocioException ex) { comprobar(s.getUsuarios().size() == 2); }
        Tarifa t = new Tarifa(fecha, TipoVehiculo.BICICLETA, 200, 20);
        s.registrarTarifa(t);
        s.registrarTarifa(new Tarifa(LocalDate.of(2026, 12, 1), TipoVehiculo.BICICLETA, 300, 30));
        comprobar(s.buscarTarifaVigente(b, fecha) == t);
        try { s.retirarVehiculo(u, b, e, inicio); throw new AssertionError(); }
        catch (ReglaNegocioException ex) { comprobar(s.getViajes().isEmpty() && e.getVehiculos().contains(b)); }
        MedioPago medio = new MedioPago(TipoMedioPago.TARJETA, "111");
        u.getCuenta().asociarMedioPago(medio);
        u.getCuenta().recargar(800, medio);
        Viaje v = s.retirarVehiculo(u, b, e, inicio);
        comprobar(b.getEstado() == EstadoVehiculo.EN_USO && !e.getVehiculos().contains(b));
        comprobar(v.getTarifa() == t && s.getViajes().size() == 1);
        try { s.retirarVehiculo(u, c, llena, inicio); throw new AssertionError(); }
        catch (ReglaNegocioException ex) { comprobar(s.getViajes().size() == 1); }
        try { s.devolverVehiculo(otro, b, e, 1, inicio.plusMinutes(5)); throw new AssertionError(); }
        catch (ReglaNegocioException ex) { comprobar(v.getEstado() == EstadoViaje.ABIERTO); }
        comprobar(!s.devolverVehiculo(u, b, llena, 1, inicio.plusMinutes(10)));
        comprobar(v.getFin() == null && u.getCuenta().getSaldo() == 800);
        comprobar(!s.devolverVehiculo(u, b, llena, 1, inicio.plusMinutes(12)));
        comprobar(v.getInicioTiempoAdicional().equals(inicio.plusMinutes(10)));
        comprobar(s.devolverVehiculo(u, b, e, 2, inicio.plusMinutes(20)));
        comprobar(v.getEstado() == EstadoViaje.CERRADO && v.getImporte() == 400);
        comprobar(u.getCuenta().getSaldo() == 400 && b.getKilometrosRecorridos() == 2);
        comprobar(b.getEstado() == EstadoVehiculo.DISPONIBLE && u.isPrimerViajeRealizado());
        u.getCuenta().recargar(400, medio);
        Viaje cancelado = s.retirarVehiculo(u, b, e, inicio.plusHours(1));
        int movimientos = u.getCuenta().getMovimientos().size();
        comprobar(s.cancelarRetiro(u, b, e, 0.1, inicio.plusHours(1).plusMinutes(2)));
        comprobar(cancelado.getEstado() == EstadoViaje.CANCELADO && cancelado.getImporte() == 0);
        comprobar(u.getCuenta().getMovimientos().size() == movimientos);
        Viaje largo = s.retirarVehiculo(u, b, e, inicio.plusHours(2));
        comprobar(s.cancelarRetiro(u, b, e, 3, inicio.plusHours(3)));
        comprobar(largo.getEstado() == EstadoViaje.CERRADO && largo.getImporte() == 1400);
        comprobar(!u.estaHabilitado() && u.getCuenta().getSaldo() == -600);
        comprobar(!s.getAlertas().isEmpty());
        s.getViajes().clear();
        comprobar(s.getViajes().size() == 3);
        Sistema electrico = new Sistema();
        electrico.registrarUsuario(otro);
        Estacion est = new Estacion("E3", "Sur", 0, 0, 2);
        electrico.registrarEstacion(est);
        BicicletaElectrica be = new BicicletaElectrica("BE1", fecha, 0, EstadoVehiculo.DISPONIBLE, 15, 50);
        electrico.registrarVehiculo(be, est);
        electrico.registrarTarifa(new Tarifa(fecha, TipoVehiculo.BICICLETA_ELECTRICA, 300, 45));
        otro.getCuenta().asociarMedioPago(medio);
        otro.getCuenta().recargar(1650, medio);
        electrico.registrarPromocion(new Promocion("Primero", inicio, inicio.plusMonths(1), TipoPromocion.PRIMER_VIAJE));
        Viaje ev = electrico.retirarVehiculo(otro, be, est, inicio);
        comprobar(ev.getEstado() == EstadoViaje.ABIERTO);
        be.setNivelBateria(14);
        comprobar(electrico.devolverVehiculo(otro, be, est, 1, inicio.plusMinutes(10)));
        comprobar(ev.getImporte() == 0 && be.getEstado() == EstadoVehiculo.BATERIA_BAJA);
        try { electrico.retirarVehiculo(otro, be, est, inicio.plusHours(1)); throw new AssertionError(); }
        catch (ReglaNegocioException ex) { comprobar(electrico.getViajes().size() == 1); }
        be.setNivelBateria(100);
        be.cambiarEstado(EstadoVehiculo.DISPONIBLE);
        Suscripcion plan = new Suscripcion(inicio);
        otro.agregarSuscripcion(plan);
        electrico.registrarPromocion(new Promocion("Mensual", inicio, inicio.plusMonths(1), TipoPromocion.PLAN_MENSUAL));
        Viaje gratis = electrico.retirarVehiculo(otro, be, est, inicio.plusHours(2));
        electrico.cancelarRetiro(otro, be, est, 0, inicio.plusHours(2).plusMinutes(1));
        comprobar(gratis.getEstado() == EstadoViaje.CANCELADO && plan.getViajesDisponibles() == 20);
        Viaje mensual = electrico.retirarVehiculo(otro, be, est, inicio.plusHours(3));
        electrico.devolverVehiculo(otro, be, est, 2, inicio.plusHours(3).plusMinutes(20));
        comprobar(mensual.getImporte() == 0 && plan.getViajesDisponibles() == 19);
        comprobar(mensual.getPromocionAplicada().getTipo() == TipoPromocion.PLAN_MENSUAL);
        System.out.println("OK: " + pruebas + " comprobaciones de integración de Sistema.");
    }
}

