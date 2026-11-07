package movilidadcompartida;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class VerificacionViajes {

    private static int pruebas;
    private static final LocalDateTime INICIO = LocalDateTime.of(2026, 1, 31, 10, 0);

    private static void comprobar(boolean condicion) {
        if (!condicion) {
            throw new AssertionError("Comprobación " + (pruebas + 1));
        }
        pruebas++;
    }

    private static void importe(double actual, double esperado) {
        comprobar(Math.abs(actual - esperado) < 0.000001);
    }

    private static Viaje nuevo(boolean primero, boolean plan,
            boolean promocionPrimero, boolean promocionPlan) throws Exception {
        Usuario u = new Usuario("Ana", "DOC1", "ana@ejemplo.com", LocalDate.now());
        if (!primero) {
            u.marcarPrimerViajeRealizado();
        }
        if (plan) {
            u.agregarSuscripcion(new Suscripcion(INICIO));
        }
        Vehiculo v = new Bicicleta("B1", LocalDate.now(), 0,
                EstadoVehiculo.DISPONIBLE, 26);
        Estacion e = new Estacion("E1", "Centro", -34, -58, 2);
        Tarifa t = new Tarifa(LocalDate.now(), TipoVehiculo.BICICLETA, 200, 20);
        Viaje viaje = new Viaje(u, v, e, INICIO, t);
        if (promocionPrimero) {
            viaje.agregarPromocion(new Promocion("Primer viaje", INICIO,
                    INICIO.plusMonths(2), TipoPromocion.PRIMER_VIAJE));
        }
        if (promocionPlan) {
            viaje.agregarPromocion(new Promocion("Plan mensual", INICIO,
                    INICIO.plusMonths(2), TipoPromocion.PLAN_MENSUAL));
        }
        return viaje;
    }

    private static void cerrar(Viaje v, long segundos, double esperado) throws Exception {
        v.finalizar(v.getOrigen(), 2.5, INICIO.plusSeconds(segundos));
        importe(v.calcularImporte(), esperado);
        v.getUsuario().getCuenta().debitar(v.getImporte());
        v.cerrar();
        v.getUsuario().actualizarHabilitacion();
        if (v.getPromocionAplicada() != null
                && v.getPromocionAplicada().getTipo() == TipoPromocion.PLAN_MENSUAL) {
            v.getSuscripcionAlInicio().consumirViaje();
        }
        if (v.isPrimerViajeAlInicio()) {
            v.getUsuario().marcarPrimerViajeRealizado();
        }
        comprobar(!v.getUsuario().tieneViajeAbierto());
    }

    public static void main(String[] args) throws Exception {
        cerrar(nuevo(false, false, false, false), 0, 200);
        cerrar(nuevo(false, false, false, false), 30, 210);
        cerrar(nuevo(false, false, false, false), 1800, 800);
        cerrar(nuevo(false, false, false, false), 7200, 2600);
        cerrar(nuevo(false, false, false, false), 7800, 2900);
        cerrar(nuevo(true, false, true, false), 900, 0);
        cerrar(nuevo(true, false, true, false), 930, 10);
        cerrar(nuevo(true, false, true, false), 1800, 300);
        cerrar(nuevo(true, false, true, false), 7800, 2400);
        Viaje mensual = nuevo(false, true, true, true);
        cerrar(mensual, 1800, 0);
        comprobar(mensual.getSuscripcionAlInicio().getViajesDisponibles() == 19);
        cerrar(nuevo(false, true, false, true), 2400, 200);
        cerrar(nuevo(false, true, false, true), 7800, 2100);
        Viaje empate = nuevo(true, true, true, true);
        cerrar(empate, 600, 0);
        comprobar(empate.getPromocionAplicada().getTipo() == TipoPromocion.PRIMER_VIAJE);
        comprobar(empate.getSuscripcionAlInicio().getViajesDisponibles() == 20);
        Viaje mejorPlan = nuevo(true, true, true, true);
        cerrar(mejorPlan, 1200, 0);
        comprobar(mejorPlan.getPromocionAplicada().getTipo() == TipoPromocion.PLAN_MENSUAL);

        Viaje adicional = nuevo(false, false, false, false);
        comprobar(adicional.registrarInicioTiempoAdicional(INICIO.plusMinutes(5)));
        comprobar(!adicional.registrarInicioTiempoAdicional(INICIO.plusMinutes(25)));
        cerrar(adicional, 1800, 600);
        Viaje superpuesto = nuevo(true, false, true, false);
        superpuesto.registrarInicioTiempoAdicional(INICIO.plusMinutes(5));
        cerrar(superpuesto, 1200, 100);
        Viaje superpuesto2 = nuevo(true, false, true, false);
        superpuesto2.registrarInicioTiempoAdicional(INICIO.plusMinutes(10));
        cerrar(superpuesto2, 1200, 0);
        Viaje cruceRecargo = nuevo(false, false, false, false);
        cruceRecargo.registrarInicioTiempoAdicional(INICIO.plusMinutes(115));
        cerrar(cruceRecargo, 7800, 2650);
        Viaje topeAdicional = nuevo(false, false, false, false);
        topeAdicional.registrarInicioTiempoAdicional(INICIO.plusMinutes(120));
        topeAdicional.registrarInicioTiempoAdicional(INICIO.plusMinutes(135));
        cerrar(topeAdicional, 9000, 3200);

        Suscripcion s = new Suscripcion(INICIO);
        comprobar(s.getFin().equals(LocalDateTime.of(2026, 2, 28, 10, 0)));
        comprobar(s.estaVigente(INICIO));
        comprobar(!s.estaVigente(s.getFin()));
        comprobar(!s.estaVigente(INICIO.minusSeconds(1)));
        for (int i = 0; i < 20; i++) {
            s.consumirViaje();
        }
        try {
            s.consumirViaje();
            throw new AssertionError("No rechazó el viaje número 21");
        } catch (ReglaNegocioException ex) {
            comprobar(s.getViajesDisponibles() == 0);
        }
        Suscripcion bisiesto = new Suscripcion(LocalDateTime.of(2024, 1, 31, 10, 0));
        comprobar(bisiesto.getFin().equals(LocalDateTime.of(2024, 2, 29, 10, 0)));

        Viaje expiraDurante = nuevo(false, true, false, true);
        LocalDateTime antesFin = expiraDurante.getSuscripcionAlInicio().getFin().minusMinutes(10);
        Usuario otro = new Usuario("Luis", "DOC2", "luis@ejemplo.com", LocalDate.now());
        Suscripcion antigua = new Suscripcion(INICIO);
        otro.agregarSuscripcion(antigua);
        Viaje despues = new Viaje(otro, expiraDurante.getVehiculo(), expiraDurante.getOrigen(),
                antesFin, expiraDurante.getTarifa());
        despues.agregarPromocion(new Promocion("Plan", INICIO, INICIO.plusMonths(2), TipoPromocion.PLAN_MENSUAL));
        despues.finalizar(despues.getOrigen(), 1, antesFin.plusMinutes(20));
        importe(despues.calcularImporte(), 0);
        comprobar(despues.getSuscripcionAlInicio() == antigua);

        Usuario sinSolapamiento = new Usuario("Sofía", "DOC3", "s@ejemplo.com", LocalDate.now());
        sinSolapamiento.agregarSuscripcion(new Suscripcion(INICIO));
        try {
            sinSolapamiento.agregarSuscripcion(new Suscripcion(INICIO.plusDays(10)));
            throw new AssertionError("Suscripciones superpuestas");
        } catch (ReglaNegocioException ex) {
            comprobar(sinSolapamiento.getSuscripciones().size() == 1);
        }
        sinSolapamiento.agregarSuscripcion(new Suscripcion(INICIO.plusMonths(1)));
        comprobar(sinSolapamiento.getSuscripciones().size() == 2);

        Viaje cancelado = nuevo(true, true, true, true);
        cancelado.cancelar(INICIO.plusMinutes(2));
        comprobar(cancelado.getEstado() == EstadoViaje.CANCELADO);
        comprobar(!cancelado.getUsuario().tieneViajeAbierto());
        comprobar(!cancelado.getUsuario().isPrimerViajeRealizado());
        comprobar(cancelado.getSuscripcionAlInicio().getViajesDisponibles() == 20);
        importe(cancelado.calcularImporte(), 0);
        Viaje tarde = nuevo(true, false, true, false);
        try {
            tarde.cancelar(INICIO.plusSeconds(121));
            throw new AssertionError("Cancelación tardía aceptada");
        } catch (ReglaNegocioException ex) {
            comprobar(tarde.getEstado() == EstadoViaje.ABIERTO && tarde.getFin() == null);
        }

        Viaje abierto = nuevo(false, false, false, false);
        try {
            new Viaje(abierto.getUsuario(), abierto.getVehiculo(), abierto.getOrigen(),
                    INICIO, abierto.getTarifa());
            throw new AssertionError("Segundo viaje abierto");
        } catch (ReglaNegocioException ex) {
            comprobar(abierto.getUsuario().getViajes().size() == 1);
        }
        abierto.registrarPunto(-34, -58, INICIO.plusMinutes(1));
        abierto.getRecorrido().clear();
        comprobar(abierto.getRecorrido().size() == 1);
        try {
            abierto.registrarPunto(-34, -58, INICIO);
            throw new AssertionError("Punto retrocede en el tiempo");
        } catch (ReglaNegocioException ex) {
            comprobar(abierto.getRecorrido().size() == 1);
        }
        abierto.finalizar(abierto.getOrigen(), 1, INICIO.plusMinutes(30));
        comprobar(abierto.getUsuario().tieneViajeAbierto());
        try {
            abierto.cerrar();
            throw new AssertionError("Cerró sin calcular");
        } catch (ReglaNegocioException ex) {
            comprobar(abierto.getEstado() == EstadoViaje.ABIERTO);
        }
        importe(abierto.calcularImporte(), 800);
        abierto.cerrar();
        abierto.getUsuario().marcarPrimerViajeRealizado();
        importe(abierto.calcularImporte(), 800);
        try {
            abierto.registrarPunto(-34, -58, INICIO.plusMinutes(31));
            throw new AssertionError("Alteró un viaje cerrado");
        } catch (ReglaNegocioException ex) {
            comprobar(abierto.getRecorrido().size() == 1);
        }
        try {
            abierto.finalizar(abierto.getOrigen(), 99, INICIO.plusMinutes(50));
            throw new AssertionError("Modificó fin de viaje cerrado");
        } catch (ReglaNegocioException ex) {
            comprobar(abierto.getDistanciaRecorrida() == 1);
        }

        Viaje sinDevolver = nuevo(false, false, false, false);
        comprobar(!sinDevolver.superaPlazoDevolucion(INICIO.plusMinutes(1439)));
        comprobar(sinDevolver.superaPlazoDevolucion(INICIO.plusHours(24)));
        sinDevolver.marcarCargoPerdidaAplicado();
        comprobar(sinDevolver.isCargoPerdidaAplicado());

        Viaje lleno = nuevo(false, false, false, false);
        Estacion estacionLlena = new Estacion("LL", "Llena", 0, 0, 1);
        estacionLlena.agregarVehiculo(lleno.getVehiculo());
        try {
            lleno.finalizar(estacionLlena, 1, INICIO.plusMinutes(10));
            throw new AssertionError("Devolución en estación llena");
        } catch (ReglaNegocioException ex) {
            comprobar(lleno.getFin() == null && lleno.getEstado() == EstadoViaje.ABIERTO);
        }
        lleno.registrarInicioTiempoAdicional(INICIO.plusMinutes(10));
        cerrar(lleno, 1200, 400);

        Viaje promocionesFuera = nuevo(true, false, false, false);
        promocionesFuera.agregarPromocion(new Promocion("Fuera de vigencia", INICIO.minusMonths(1), INICIO,
                TipoPromocion.PRIMER_VIAJE));
        cerrar(promocionesFuera, 600, 400);
        comprobar(promocionesFuera.getPromocionAplicada() == null);

        System.out.println("OK: " + pruebas + " comprobaciones de viajes, promociones, suscripciones y fechas.");
    }
}

