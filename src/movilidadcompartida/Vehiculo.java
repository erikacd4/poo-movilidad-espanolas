package movilidadcompartida;

import java.io.Serializable;

import java.time.LocalDate;

public abstract class Vehiculo implements Serializable {

    private String codigo;
    private LocalDate fechaAlta;
    private double kilometrosRecorridos;
    private EstadoVehiculo estado;
    private boolean mantenimientoObligatorio;
    private boolean reportadoPerdido;

    public Vehiculo(String codigo, LocalDate fechaAlta,
            double kilometrosRecorridos, EstadoVehiculo estado) {
        if (codigo == null || codigo.equals("")) {
            throw new IllegalArgumentException("El código es obligatorio.");
        }
        if (fechaAlta == null || estado == null) {
            throw new IllegalArgumentException("La fecha y el estado son obligatorios.");
        }
        if (kilometrosRecorridos < 0) {
            throw new IllegalArgumentException("Los kilómetros no pueden ser negativos.");
        }
        this.codigo = codigo;
        this.fechaAlta = fechaAlta;
        this.kilometrosRecorridos = kilometrosRecorridos;
        this.estado = estado;
        this.mantenimientoObligatorio = false;
        this.reportadoPerdido = false;
    }

    public boolean puedeRetirarse() {
        return estado == EstadoVehiculo.DISPONIBLE
                && !mantenimientoObligatorio && !reportadoPerdido;
    }

    public void cambiarEstado(EstadoVehiculo nuevoEstado) {
        if (nuevoEstado == null) {
            throw new IllegalArgumentException("El estado es obligatorio.");
        }
        this.estado = nuevoEstado;
    }

    public void agregarKilometros(double distancia) {
        if (distancia < 0) {
            throw new IllegalArgumentException("La distancia no puede ser negativa.");
        }
        kilometrosRecorridos = kilometrosRecorridos + distancia;
    }

    public abstract TipoVehiculo obtenerTipo();

    public boolean tieneBateriaBaja() {
        return false;
    }

    public void actualizarEstadoTrasDevolucion() {
        if (mantenimientoObligatorio || reportadoPerdido) {
            estado = EstadoVehiculo.FUERA_DE_SERVICIO;
        } else if (estado != EstadoVehiculo.EN_REPARACION
                && estado != EstadoVehiculo.FUERA_DE_SERVICIO) {
            estado = EstadoVehiculo.DISPONIBLE;
        }
    }

    public String getCodigo() {
        return codigo;
    }

    public LocalDate getFechaAlta() {
        return fechaAlta;
    }

    public double getKilometrosRecorridos() {
        return kilometrosRecorridos;
    }

    public EstadoVehiculo getEstado() {
        return estado;
    }

    public boolean isMantenimientoObligatorio() {
        return mantenimientoObligatorio;
    }

    public void setMantenimientoObligatorio(boolean mantenimientoObligatorio) {
        this.mantenimientoObligatorio = mantenimientoObligatorio;
    }

    public boolean isReportadoPerdido() {
        return reportadoPerdido;
    }

    public void setReportadoPerdido(boolean reportadoPerdido) {
        this.reportadoPerdido = reportadoPerdido;
    }
}
