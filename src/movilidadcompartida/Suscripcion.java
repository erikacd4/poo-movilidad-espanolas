package movilidadcompartida;

import java.io.Serializable;

import java.time.LocalDateTime;

public class Suscripcion implements Serializable {

    private LocalDateTime inicio;
    private LocalDateTime fin;
    private int viajesDisponibles;

    public Suscripcion(LocalDateTime inicio) {
        if (inicio == null) {
            throw new IllegalArgumentException("El inicio de la suscripción es obligatorio.");
        }
        this.inicio = inicio;
        this.fin = inicio.plusMonths(1);
        this.viajesDisponibles = 20;
    }

    public boolean estaVigente(LocalDateTime momento) {
        if (momento == null) {
            return false;
        }
        return !momento.isBefore(inicio) && momento.isBefore(fin);
    }

    public void consumirViaje() throws ReglaNegocioException {
        if (viajesDisponibles <= 0) {
            throw new ReglaNegocioException("La suscripción no tiene viajes disponibles.");
        }
        viajesDisponibles = viajesDisponibles - 1;
    }

    public LocalDateTime getInicio() {
        return inicio;
    }

    public LocalDateTime getFin() {
        return fin;
    }

    public int getViajesDisponibles() {
        return viajesDisponibles;
    }
}
