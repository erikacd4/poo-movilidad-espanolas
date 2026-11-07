package movilidadcompartida;

import java.io.Serializable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Cuenta implements Serializable {

    private double saldo;
    private List<Movimiento> movimientos;
    private List<MedioPago> mediosPago;

    public Cuenta() {
        this.saldo = 0;
        this.movimientos = new ArrayList<>();
        this.mediosPago = new ArrayList<>();
    }

    public void asociarMedioPago(MedioPago medio) {
        if (medio == null) {
            throw new IllegalArgumentException("Debe indicar un medio de pago.");
        }
        if (!tieneMedioPago(medio)) {
            mediosPago.add(medio);
        }
    }

    public boolean tieneMedioPago(MedioPago medio) {
        if (medio == null) {
            return false;
        }
        for (MedioPago actual : mediosPago) {
            if (actual.getTipo() == medio.getTipo()
                    && actual.getIdentificador().equals(medio.getIdentificador())) {
                return true;
            }
        }
        return false;
    }

    public void recargar(double importe, MedioPago medio) {
        if (importe <= 0) {
            throw new IllegalArgumentException("El importe de la recarga debe ser positivo.");
        }
        if (!tieneMedioPago(medio)) {
            throw new IllegalArgumentException("El medio de pago no está asociado a esta cuenta.");
        }
        Movimiento movimiento = new Movimiento(
                LocalDateTime.now(), importe, TipoMovimiento.RECARGA);
        saldo = saldo + importe;
        movimientos.add(movimiento);
    }

    public void debitar(double importe) {
        if (importe < 0) {
            throw new IllegalArgumentException("El importe del débito no puede ser negativo.");
        }
        Movimiento movimiento = new Movimiento(
                LocalDateTime.now(), importe, TipoMovimiento.DEBITO);
        saldo = saldo - importe;
        movimientos.add(movimiento);
    }

    public void registrarAjuste(double importe) {
        Movimiento movimiento = new Movimiento(
                LocalDateTime.now(), importe, TipoMovimiento.AJUSTE);
        saldo = saldo + importe;
        movimientos.add(movimiento);
    }

    public void debitar(double importe, Viaje viaje) {
        if (viaje == null || viaje.getUsuario().getCuenta() != this) {
            throw new IllegalArgumentException("El viaje debe corresponder a esta cuenta.");
        }
        if (importe < 0) {
            throw new IllegalArgumentException("El importe del débito no puede ser negativo.");
        }
        Movimiento movimiento = new Movimiento(
                LocalDateTime.now(), importe, TipoMovimiento.DEBITO, viaje);
        saldo = saldo - importe;
        movimientos.add(movimiento);
    }

    public void registrarAjuste(double importe, Viaje viaje) {
        if (viaje == null || viaje.getUsuario().getCuenta() != this) {
            throw new IllegalArgumentException("El viaje debe corresponder a esta cuenta.");
        }
        Movimiento movimiento = new Movimiento(
                LocalDateTime.now(), importe, TipoMovimiento.AJUSTE, viaje);
        saldo = saldo + importe;
        movimientos.add(movimiento);
    }

    public boolean alcanzaSaldoMinimo(double minimo) {
        if (minimo < 0) {
            throw new IllegalArgumentException("El saldo mínimo no puede ser negativo.");
        }
        return saldo >= minimo;
    }

    public double getSaldo() {
        return saldo;
    }

    public List<Movimiento> getMovimientos() {
        List<Movimiento> copia = new ArrayList<>();
        for (Movimiento movimiento : movimientos) {
            copia.add(movimiento);
        }
        return copia;
    }

    public List<MedioPago> getMediosPago() {
        List<MedioPago> copia = new ArrayList<>();
        for (MedioPago medio : mediosPago) {
            copia.add(medio);
        }
        return copia;
    }
}
