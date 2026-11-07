package movilidadcompartida;

import java.time.LocalDate;

public class MonopatinElectrico extends Vehiculo {

    private double nivelBateria;
    private double velocidadMaxima;

    public MonopatinElectrico(String codigo, LocalDate fechaAlta,
            double kilometrosRecorridos, EstadoVehiculo estado,
            double nivelBateria, double velocidadMaxima) {
        super(codigo, fechaAlta, kilometrosRecorridos, estado);
        if (velocidadMaxima <= 0) {
            throw new IllegalArgumentException("La velocidad máxima debe ser positiva.");
        }
        setNivelBateria(nivelBateria);
        this.velocidadMaxima = velocidadMaxima;
    }

    @Override
    public boolean puedeRetirarse() {
        return super.puedeRetirarse() && nivelBateria >= 15;
    }

    @Override
    public TipoVehiculo obtenerTipo() {
        return TipoVehiculo.MONOPATIN_ELECTRICO;
    }

    @Override
    public boolean tieneBateriaBaja() {
        return nivelBateria < 15;
    }

    @Override
    public void actualizarEstadoTrasDevolucion() {
        super.actualizarEstadoTrasDevolucion();
        if (getEstado() == EstadoVehiculo.DISPONIBLE && tieneBateriaBaja()) {
            cambiarEstado(EstadoVehiculo.BATERIA_BAJA);
        }
    }

    public double getNivelBateria() {
        return nivelBateria;
    }

    public void setNivelBateria(double nivelBateria) {
        if (nivelBateria < 0 || nivelBateria > 100) {
            throw new IllegalArgumentException("La batería debe estar entre 0 y 100 %.");
        }
        this.nivelBateria = nivelBateria;
    }

    public double getVelocidadMaxima() {
        return velocidadMaxima;
    }
}
