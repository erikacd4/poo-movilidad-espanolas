package movilidadcompartida;

import java.io.Serializable;

import java.time.LocalDateTime;
import java.util.List;

public class NotaAjuste implements Serializable {

    private LocalDateTime fecha;
    private String motivo;
    private String detalle;
    private double importeAjuste;
    private Viaje viaje;
    private Operador operador;
    private Movimiento movimiento;
    private boolean registrada;

    public NotaAjuste(Viaje viaje, Operador operador, String motivo,
            String detalle, double importeAjuste) throws ReglaNegocioException {
        if (viaje == null || operador == null) {
            throw new IllegalArgumentException("El viaje y el operador son obligatorios.");
        }
        if (viaje.getEstado() != EstadoViaje.CERRADO) {
            throw new ReglaNegocioException("La nota de ajuste corresponde a un viaje cerrado.");
        }
        if (motivo == null || motivo.equals("") || detalle == null || detalle.equals("")) {
            throw new IllegalArgumentException("Debe indicar el motivo y el detalle.");
        }
        this.fecha = LocalDateTime.now();
        this.motivo = motivo;
        this.detalle = detalle;
        this.importeAjuste = importeAjuste;
        this.viaje = viaje;
        this.operador = operador;
        this.movimiento = null;
        this.registrada = false;
    }

    public void registrar() throws ReglaNegocioException {
        if (registrada) {
            throw new ReglaNegocioException("La nota de ajuste ya fue registrada.");
        }
        if (importeAjuste != 0) {
            Cuenta cuenta = viaje.getUsuario().getCuenta();
            cuenta.registrarAjuste(importeAjuste, viaje);
            List<Movimiento> movimientos = cuenta.getMovimientos();
            movimiento = movimientos.get(movimientos.size() - 1);
        }
        viaje.getUsuario().actualizarHabilitacion();
        registrada = true;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public String getMotivo() {
        return motivo;
    }

    public String getDetalle() {
        return detalle;
    }

    public double getImporteAjuste() {
        return importeAjuste;
    }

    public Viaje getViaje() {
        return viaje;
    }

    public Operador getOperador() {
        return operador;
    }

    public Movimiento getMovimiento() {
        return movimiento;
    }

    public boolean isRegistrada() {
        return registrada;
    }
}
