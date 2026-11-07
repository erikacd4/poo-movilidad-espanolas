package movilidadcompartida;

import java.time.LocalDate;

public class Bicicleta extends Vehiculo {

    private int rodado;

    public Bicicleta(String codigo, LocalDate fechaAlta,
            double kilometrosRecorridos, EstadoVehiculo estado, int rodado) {
        super(codigo, fechaAlta, kilometrosRecorridos, estado);
        if (rodado <= 0) {
            throw new IllegalArgumentException("El rodado debe ser positivo.");
        }
        this.rodado = rodado;
    }

    @Override
    public TipoVehiculo obtenerTipo() {
        return TipoVehiculo.BICICLETA;
    }

    public int getRodado() {
        return rodado;
    }
}
