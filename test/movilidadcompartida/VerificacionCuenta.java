
package movilidadcompartida;
public class VerificacionCuenta {
    private static int pruebas;
    private static void comprobar(boolean condicion) {
        if (!condicion) { throw new AssertionError("Comprobación " + (pruebas + 1)); }
        pruebas++;
    }
    public static void main(String[] args) {
        Cuenta cuenta = new Cuenta();
        MedioPago tarjeta = new MedioPago(TipoMedioPago.TARJETA, "T1");
        MedioPago otra = new MedioPago(TipoMedioPago.BILLETERA_VIRTUAL, "B1");
        comprobar(cuenta.getSaldo() == 0 && cuenta.getMovimientos().isEmpty());
        try { cuenta.recargar(100, tarjeta); throw new AssertionError("Medio sin asociar"); }
        catch (IllegalArgumentException ex) { comprobar(cuenta.getSaldo() == 0 && cuenta.getMovimientos().isEmpty()); }
        cuenta.asociarMedioPago(tarjeta);
        cuenta.asociarMedioPago(new MedioPago(TipoMedioPago.TARJETA, "T1"));
        comprobar(cuenta.getMediosPago().size() == 1);
        comprobar(cuenta.tieneMedioPago(tarjeta) && !cuenta.tieneMedioPago(otra));
        cuenta.recargar(100, tarjeta);
        comprobar(cuenta.getSaldo() == 100 && cuenta.alcanzaSaldoMinimo(100));
        comprobar(!cuenta.alcanzaSaldoMinimo(100.01));
        Movimiento recarga = cuenta.getMovimientos().get(0);
        comprobar(recarga.getTipo() == TipoMovimiento.RECARGA && recarga.getImporte() == 100 && recarga.getFecha() != null);
        try { cuenta.recargar(0, tarjeta); throw new AssertionError("Recarga cero"); }
        catch (IllegalArgumentException ex) { comprobar(cuenta.getSaldo() == 100 && cuenta.getMovimientos().size() == 1); }
        try { cuenta.recargar(-1, tarjeta); throw new AssertionError("Recarga negativa"); }
        catch (IllegalArgumentException ex) { comprobar(cuenta.getSaldo() == 100); }
        cuenta.debitar(150);
        comprobar(cuenta.getSaldo() == -50 && !cuenta.alcanzaSaldoMinimo(0));
        comprobar(cuenta.getMovimientos().get(1).getTipo() == TipoMovimiento.DEBITO);
        try { cuenta.debitar(-10); throw new AssertionError("Débito negativo"); }
        catch (IllegalArgumentException ex) { comprobar(cuenta.getSaldo() == -50 && cuenta.getMovimientos().size() == 2); }
        cuenta.debitar(0);
        comprobar(cuenta.getSaldo() == -50 && cuenta.getMovimientos().size() == 3);
        cuenta.registrarAjuste(70);
        comprobar(cuenta.getSaldo() == 20);
        cuenta.registrarAjuste(-10);
        comprobar(cuenta.getSaldo() == 10 && cuenta.getMovimientos().size() == 5);
        comprobar(cuenta.getMovimientos().get(4).getTipo() == TipoMovimiento.AJUSTE && cuenta.getMovimientos().get(4).getImporte() == -10);
        cuenta.getMovimientos().clear();
        cuenta.getMediosPago().clear();
        comprobar(cuenta.getMovimientos().size() == 5 && cuenta.getMediosPago().size() == 1);
        comprobar(cuenta.getMovimientos().get(0) == recarga && recarga.getImporte() == 100);
        System.out.println("OK: " + pruebas + " comprobaciones de saldo, medios, recargas, débitos y ajustes.");
    }
}

