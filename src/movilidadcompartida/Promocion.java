package movilidadcompartida;

import java.io.Serializable;

import java.time.LocalDateTime;

public class Promocion implements Serializable {

    private String nombre;
    private LocalDateTime inicioVigencia;
    private LocalDateTime finVigencia;
    private TipoPromocion tipo;

    public Promocion(String nombre, LocalDateTime inicioVigencia,
            LocalDateTime finVigencia, TipoPromocion tipo) {
        if (nombre == null || nombre.equals("")) {
            throw new IllegalArgumentException("El nombre de la promoción es obligatorio.");
        }
        if (inicioVigencia == null || finVigencia == null || tipo == null) {
            throw new IllegalArgumentException("La vigencia y el tipo son obligatorios.");
        }
        if (!finVigencia.isAfter(inicioVigencia)) {
            throw new IllegalArgumentException("El fin debe ser posterior al inicio de vigencia.");
        }
        this.nombre = nombre;
        this.inicioVigencia = inicioVigencia;
        this.finVigencia = finVigencia;
        this.tipo = tipo;
    }

    public boolean esAplicable(Viaje viaje) {
        if (viaje == null || viaje.getEstado() == EstadoViaje.CANCELADO) {
            return false;
        }
        LocalDateTime inicioViaje = viaje.getInicio();
        if (inicioViaje.isBefore(inicioVigencia)
                || !inicioViaje.isBefore(finVigencia)) {
            return false;
        }
        if (tipo == TipoPromocion.PRIMER_VIAJE) {
            return viaje.isPrimerViajeAlInicio();
        }
        return viaje.getSuscripcionAlInicio() != null;
    }

    public double calcularImporte(Viaje viaje) throws ReglaNegocioException {
        if (!esAplicable(viaje)) {
            throw new ReglaNegocioException("La promoción no es aplicable a este viaje.");
        }
        if (tipo == TipoPromocion.PRIMER_VIAJE) {
            return viaje.calcularImporteConMinutosGratis(15);
        }
        return viaje.calcularImporteConMinutosGratis(30);
    }

    public String getNombre() {
        return nombre;
    }

    public LocalDateTime getInicioVigencia() {
        return inicioVigencia;
    }

    public LocalDateTime getFinVigencia() {
        return finVigencia;
    }

    public TipoPromocion getTipo() {
        return tipo;
    }
}
