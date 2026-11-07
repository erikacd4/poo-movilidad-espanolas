package movilidadcompartida;

import java.io.Serializable;

import java.time.LocalDate;

public class Tarifa implements Serializable {

    private LocalDate vigenteDesde;
    private TipoVehiculo tipoVehiculo;
    private double cargoDesbloqueo;
    private double precioMinuto;

    public Tarifa(LocalDate vigenteDesde, TipoVehiculo tipoVehiculo,
            double cargoDesbloqueo, double precioMinuto) {
        if (vigenteDesde == null || tipoVehiculo == null) {
            throw new IllegalArgumentException("La fecha y el tipo de vehículo son obligatorios.");
        }
        if (cargoDesbloqueo < 0 || precioMinuto < 0) {
            throw new IllegalArgumentException("Los importes de la tarifa no pueden ser negativos.");
        }
        this.vigenteDesde = vigenteDesde;
        this.tipoVehiculo = tipoVehiculo;
        this.cargoDesbloqueo = cargoDesbloqueo;
        this.precioMinuto = precioMinuto;
    }

    public double calcularSaldoMinimo() {
        return cargoDesbloqueo + 30 * precioMinuto;
    }

    public LocalDate getVigenteDesde() {
        return vigenteDesde;
    }

    public TipoVehiculo getTipoVehiculo() {
        return tipoVehiculo;
    }

    public double getCargoDesbloqueo() {
        return cargoDesbloqueo;
    }

    public double getPrecioMinuto() {
        return precioMinuto;
    }
}
