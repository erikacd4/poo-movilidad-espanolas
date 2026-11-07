package movilidadcompartida;

import java.io.Serializable;

import java.time.LocalDateTime;

public class PuntoRecorrido implements Serializable {

    private double latitud;
    private double longitud;
    private LocalDateTime hora;

    public PuntoRecorrido(double latitud, double longitud, LocalDateTime hora) {
        if (hora == null) {
            throw new IllegalArgumentException("La fecha y hora son obligatorias.");
        }
        this.latitud = latitud;
        this.longitud = longitud;
        this.hora = hora;
    }

    public double getLatitud() {
        return latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public LocalDateTime getHora() {
        return hora;
    }
}
