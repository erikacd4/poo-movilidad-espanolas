package movilidadcompartida;

import java.io.Serializable;

import java.time.LocalDateTime;

public class ReporteFalla implements Serializable {

    private LocalDateTime fecha;
    private String descripcion;
    private boolean atendido;
    private Vehiculo vehiculo;
    private Usuario usuario;
    private Operador operador;

    public ReporteFalla(Vehiculo vehiculo, String descripcion, Usuario usuario) {
        if (vehiculo == null || usuario == null) {
            throw new IllegalArgumentException("Debe indicar el vehículo y el usuario.");
        }
        if (descripcion == null || descripcion.equals("")) {
            throw new IllegalArgumentException("La descripción de la falla es obligatoria.");
        }
        this.fecha = LocalDateTime.now();
        this.descripcion = descripcion;
        this.atendido = false;
        this.vehiculo = vehiculo;
        this.usuario = usuario;
        this.operador = null;
    }

    public ReporteFalla(Vehiculo vehiculo, String descripcion, Operador operador) {
        if (vehiculo == null || operador == null) {
            throw new IllegalArgumentException("Debe indicar el vehículo y el operador.");
        }
        if (descripcion == null || descripcion.equals("")) {
            throw new IllegalArgumentException("La descripción de la falla es obligatoria.");
        }
        this.fecha = LocalDateTime.now();
        this.descripcion = descripcion;
        this.atendido = false;
        this.vehiculo = vehiculo;
        this.usuario = null;
        this.operador = operador;
    }

    public void marcarAtendido() {
        atendido = true;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public boolean isAtendido() {
        return atendido;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Operador getOperador() {
        return operador;
    }
}
