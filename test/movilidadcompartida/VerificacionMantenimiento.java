package movilidadcompartida;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class VerificacionMantenimiento {

    private static int pruebas;
    private static final LocalDateTime INICIO = LocalDateTime.of(2026, 1, 1, 10, 0);

    private static void comprobar(boolean condicion) {
        if (!condicion) {
            throw new AssertionError("Comprobación " + (pruebas + 1));
        }
        pruebas++;
    }

    private static Vehiculo bicicleta(String codigo) {
        return new Bicicleta(codigo, LocalDate.now(), 0, EstadoVehiculo.DISPONIBLE, 26);
    }

    private static Viaje viaje(boolean cerrado) throws Exception {
        Usuario u = new Usuario("Ana", "DOC1", "ana@ejemplo.com", LocalDate.now());
        MedioPago medio = new MedioPago(TipoMedioPago.TARJETA, "T1");
        u.getCuenta().asociarMedioPago(medio);
        u.getCuenta().recargar(1000, medio);
        Vehiculo v = bicicleta("B1");
        Estacion e = new Estacion("E1", "Centro", 0, 0, 2);
        Tarifa t = new Tarifa(LocalDate.now(), TipoVehiculo.BICICLETA, 200, 20);
        Viaje j = new Viaje(u, v, e, INICIO, t);
        v.cambiarEstado(EstadoVehiculo.EN_USO);
        if (cerrado) {
            j.finalizar(e, 1, INICIO.plusMinutes(50));
            u.getCuenta().debitar(j.calcularImporte(), j);
            j.cerrar();
            u.actualizarHabilitacion();
        }
        return j;
    }

    public static void main(String[] args) throws Exception {
        Operador op = new Operador("Luis", 1, "111");
        Tecnico tecnico = new Tecnico("Eva", 2, "222");
        Tecnico otroTecnico = new Tecnico("María", 3, "333");
        Vehiculo v = bicicleta("M1");
        v.setMantenimientoObligatorio(true);
        ReporteFalla r1 = new ReporteFalla(v, "Frenos", op);
        ReporteFalla r2 = new ReporteFalla(v, "Rueda", op);
        ReporteFalla ajeno = new ReporteFalla(bicicleta("M2"), "Cadena", op);
        List<ReporteFalla> reportes = new ArrayList<>();
        reportes.add(r1);
        reportes.add(r2);
        OrdenMantenimiento orden = op.generarOrden(v, tecnico, reportes);
        comprobar(orden.getTecnico() == tecnico && orden.getOperador() == op && !orden.isCerrada());
        try {
            tecnico.cerrarOrden(orden, reportes);
            throw new AssertionError("Cerró sin tareas");
        } catch (ReglaNegocioException ex) {
            comprobar(!orden.isCerrada() && !r1.isAtendido());
        }
        try {
            otroTecnico.registrarTrabajo(orden, "Trabajo ajeno", "");
            throw new AssertionError("Otro técnico modificó la orden");
        } catch (ReglaNegocioException ex) {
            comprobar(orden.getTareasRealizadas().equals(""));
        }
        tecnico.registrarTrabajo(orden, "Se repararon los frenos", null);
        comprobar(orden.getRepuestosUtilizados().equals(""));
        List<ReporteFalla> resueltos = new ArrayList<>();
        resueltos.add(ajeno);
        try {
            tecnico.cerrarOrden(orden, resueltos);
            throw new AssertionError("Resolvió un reporte de otro vehículo");
        } catch (ReglaNegocioException ex) {
            comprobar(!orden.isCerrada() && !ajeno.isAtendido());
        }
        resueltos.clear();
        resueltos.add(r1);
        tecnico.cerrarOrden(orden, resueltos);
        comprobar(orden.isCerrada() && r1.isAtendido() && !r2.isAtendido());
        comprobar(orden.getCierre() != null && !orden.getCierre().isBefore(orden.getApertura()));
        comprobar(v.isMantenimientoObligatorio());
        orden.getReportes().clear();
        comprobar(orden.getReportes().size() == 2);
        try {
            tecnico.cerrarOrden(orden, resueltos);
            throw new AssertionError("Cerró dos veces");
        } catch (ReglaNegocioException ex) {
            comprobar(!r2.isAtendido());
        }
        try {
            tecnico.registrarTrabajo(orden, "Otro trabajo", "");
            throw new AssertionError("Alteró una orden cerrada");
        } catch (ReglaNegocioException ex) {
            comprobar(orden.getTareasRealizadas().equals("Se repararon los frenos"));
        }

        Estacion origen = new Estacion("A", "Origen", 0, 0, 3);
        Estacion chico = new Estacion("B", "Destino chico", 0, 0, 1);
        Estacion destino = new Estacion("C", "Destino", 0, 0, 3);
        Vehiculo b1 = bicicleta("B1");
        Vehiculo b2 = new BicicletaElectrica("B2", LocalDate.now(), 0,
                EstadoVehiculo.BATERIA_BAJA, 10, 30);
        origen.agregarVehiculo(b1);
        origen.agregarVehiculo(b2);
        List<Vehiculo> seleccion = new ArrayList<>();
        seleccion.add(b1);
        seleccion.add(b2);
        try {
            op.registrarTraslado(origen, chico, seleccion);
            throw new AssertionError("Traslado excedió capacidad");
        } catch (ReglaNegocioException ex) {
            comprobar(origen.getVehiculos().size() == 2 && chico.getVehiculos().isEmpty());
        }
        seleccion.clear();
        seleccion.add(b1);
        seleccion.add(bicicleta("AJENO"));
        try {
            op.registrarTraslado(origen, destino, seleccion);
            throw new AssertionError("Trasladó un vehículo ajeno");
        } catch (ReglaNegocioException ex) {
            comprobar(origen.getVehiculos().size() == 2 && destino.getVehiculos().isEmpty());
        }
        seleccion.clear();
        seleccion.add(b1);
        seleccion.add(b1);
        try {
            new Traslado(origen, destino, seleccion, op);
            throw new AssertionError("Aceptó una selección duplicada");
        } catch (IllegalArgumentException ex) {
            comprobar(origen.getVehiculos().size() == 2);
        }
        seleccion.clear();
        seleccion.add(b1);
        seleccion.add(b2);
        Traslado traslado = op.registrarTraslado(origen, destino, seleccion);
        comprobar(traslado.isRegistrado() && traslado.getOperador() == op);
        comprobar(origen.getVehiculos().isEmpty() && destino.getVehiculos().size() == 2);
        comprobar(b2.getEstado() == EstadoVehiculo.BATERIA_BAJA && b1.getKilometrosRecorridos() == 0);
        try {
            traslado.registrar();
            throw new AssertionError("Registró dos veces el traslado");
        } catch (ReglaNegocioException ex) {
            comprobar(destino.getVehiculos().size() == 2);
        }

        Viaje cerrado = viaje(true);
        Cuenta cuenta = cerrado.getUsuario().getCuenta();
        comprobar(cuenta.getSaldo() == -200 && !cerrado.getUsuario().estaHabilitado());
        Movimiento debito = cuenta.getMovimientos().get(1);
        comprobar(debito.getViaje() == cerrado && debito.getImporte() == 1200);
        NotaAjuste positiva = op.registrarNotaAjuste(cerrado, "Corrección", "Devolución parcial", 200);
        comprobar(cuenta.getSaldo() == 0 && cerrado.getUsuario().estaHabilitado());
        comprobar(positiva.getMovimiento().getViaje() == cerrado && positiva.getMovimiento().getTipo() == TipoMovimiento.AJUSTE);
        comprobar(cerrado.getImporte() == 1200 && debito.getImporte() == 1200);
        int cantidad = cuenta.getMovimientos().size();
        try {
            positiva.registrar();
            throw new AssertionError("Duplicó el ajuste");
        } catch (ReglaNegocioException ex) {
            comprobar(cuenta.getSaldo() == 0 && cuenta.getMovimientos().size() == cantidad);
        }
        NotaAjuste negativa = op.registrarNotaAjuste(cerrado, "Corrección", "Cargo complementario", -50);
        comprobar(cuenta.getSaldo() == -50 && !cerrado.getUsuario().estaHabilitado());
        comprobar(negativa.getMovimiento().getImporte() == -50);
        cantidad = cuenta.getMovimientos().size();
        NotaAjuste informativa = op.registrarNotaAjuste(cerrado, "Dato", "Aclaración de datos", 0);
        comprobar(informativa.isRegistrada() && informativa.getMovimiento() == null);
        comprobar(cuenta.getMovimientos().size() == cantidad && cerrado.getImporte() == 1200);
        Viaje abierto = viaje(false);
        try {
            op.registrarNotaAjuste(abierto, "Corrección", "Ajuste", 200);
            throw new AssertionError("Ajustó un viaje abierto");
        } catch (ReglaNegocioException ex) {
            comprobar(abierto.getUsuario().getCuenta().getSaldo() == 1000);
        }
        try {
            cuenta.debitar(10, abierto);
            throw new AssertionError("Asoció el cargo a un viaje de otra cuenta");
        } catch (IllegalArgumentException ex) {
            comprobar(cuenta.getSaldo() == -50);
        }

        List<Viaje> viajes = new ArrayList<>();
        viajes.add(abierto);
        viajes.add(cerrado);
        comprobar(op.comprobarNoDevueltos(viajes, INICIO.plusMinutes(1439)).isEmpty());
        List<Viaje> perdidos = op.comprobarNoDevueltos(viajes, INICIO.plusHours(24));
        comprobar(perdidos.size() == 1 && perdidos.get(0) == abierto);
        comprobar(abierto.getUsuario().getCuenta().getSaldo() == -49000);
        comprobar(abierto.isCargoPerdidaAplicado() && abierto.getVehiculo().isReportadoPerdido());
        comprobar(!abierto.getUsuario().estaHabilitado() && abierto.getUsuario().tieneViajeAbierto());
        comprobar(abierto.getFin() == null && abierto.getEstado() == EstadoViaje.ABIERTO);
        Movimiento perdida = abierto.getUsuario().getCuenta().getMovimientos().get(1);
        comprobar(perdida.getViaje() == abierto && perdida.getImporte() == 50000);
        cantidad = abierto.getUsuario().getCuenta().getMovimientos().size();
        op.comprobarNoDevueltos(viajes, INICIO.plusHours(25));
        comprobar(abierto.getUsuario().getCuenta().getSaldo() == -49000
                && abierto.getUsuario().getCuenta().getMovimientos().size() == cantidad);
        comprobar(cerrado.getImporte() == 1200 && cuenta.getSaldo() == -50);
        System.out.println("OK: " + pruebas + " comprobaciones de órdenes, técnicos, traslados, ajustes y pérdidas.");
    }
}

