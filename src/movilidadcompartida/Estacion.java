package movilidadcompartida;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;

public class Estacion implements Serializable {

    private String codigo;
    private String direccion;
    private double latitud;
    private double longitud;
    private int capacidadMaxima;
    private List<Vehiculo> vehiculos;

    public Estacion(String codigo, String direccion, double latitud,
            double longitud, int capacidadMaxima) {
        if (codigo == null || codigo.equals("")) {
            throw new IllegalArgumentException("El código de la estación es obligatorio.");
        }
        if (direccion == null || direccion.equals("")) {
            throw new IllegalArgumentException("La dirección es obligatoria.");
        }
        if (capacidadMaxima <= 0) {
            throw new IllegalArgumentException("La capacidad máxima debe ser mayor que cero.");
        }
        this.codigo = codigo;
        this.direccion = direccion;
        this.latitud = latitud;
        this.longitud = longitud;
        this.capacidadMaxima = capacidadMaxima;
        this.vehiculos = new ArrayList<>();
    }

    public boolean tieneCapacidad() {
        return vehiculos.size() < capacidadMaxima;
    }

    public double calcularOcupacion() {
        return vehiculos.size() * 100.0 / capacidadMaxima;
    }

    public void agregarVehiculo(Vehiculo vehiculo) {
        if (vehiculo == null) {
            throw new IllegalArgumentException("Debe indicar un vehículo.");
        }
        for (Vehiculo actual : vehiculos) {
            if (actual.getCodigo().equals(vehiculo.getCodigo())) {
                throw new IllegalArgumentException("El vehículo ya está en esta estación.");
            }
        }
        if (!tieneCapacidad()) {
            throw new IllegalArgumentException("La estación está llena.");
        }
        vehiculos.add(vehiculo);
    }

    public void retirarVehiculo(Vehiculo vehiculo) {
        if (vehiculo == null) {
            throw new IllegalArgumentException("Debe indicar un vehículo.");
        }
        for (int i = 0; i < vehiculos.size(); i++) {
            if (vehiculos.get(i).getCodigo().equals(vehiculo.getCodigo())) {
                vehiculos.remove(i);
                return;
            }
        }
        throw new IllegalArgumentException("El vehículo no se encuentra en esta estación.");
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDireccion() {
        return direccion;
    }

    public double getLatitud() {
        return latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public int getCapacidadMaxima() {
        return capacidadMaxima;
    }

    public List<Vehiculo> getVehiculos() {
        List<Vehiculo> copia = new ArrayList<>();
        for (Vehiculo vehiculo : vehiculos) {
            copia.add(vehiculo);
        }
        return copia;
    }
}
