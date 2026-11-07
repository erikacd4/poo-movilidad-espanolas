package movilidadcompartida;

import java.io.Serializable;

import java.time.LocalDateTime;

public class Movimiento implements Serializable {

    private LocalDateTime fecha;
    private double importe;
    private TipoMovimiento tipo;
    private Viaje viaje;

    public Movimiento(LocalDateTime fecha, double importe, TipoMovimiento tipo) {
        if (fecha == null || tipo == null) {
            throw new IllegalArgumentException("La fecha y el tipo son obligatorios.");
        }
        this.fecha = fecha;
        this.importe = importe;
        this.tipo = tipo;
        this.viaje = null;
    }

    public Movimiento(LocalDateTime fecha, double importe,
            TipoMovimiento tipo, Viaje viaje) {
        if (fecha == null || tipo == null || viaje == null) {
            throw new IllegalArgumentException("La fecha, el tipo y el viaje son obligatorios.");
        }
        this.fecha = fecha;
        this.importe = importe;
        this.tipo = tipo;
        this.viaje = viaje;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public double getImporte() {
        return importe;
    }

    public TipoMovimiento getTipo() {
        return tipo;
    }

    public Viaje getViaje() {
        return viaje;
    }
}
