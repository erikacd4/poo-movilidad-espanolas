package movilidadcompartida;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;

public class Traslado implements Serializable {

    private Estacion origen;
    private Estacion destino;
    private List<Vehiculo> vehiculos;
    private Operador operador;
    private boolean registrado;

    public Traslado(Estacion origen, Estacion destino,
            List<Vehiculo> vehiculos, Operador operador) {
        if (origen == null || destino == null || operador == null) {
            throw new IllegalArgumentException("Las estaciones y el operador son obligatorios.");
        }
        if (origen.getCodigo().equals(destino.getCodigo())) {
            throw new IllegalArgumentException("Las estaciones de origen y destino deben ser distintas.");
        }
        if (vehiculos == null || vehiculos.isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar al menos un vehículo.");
        }
        this.vehiculos = new ArrayList<>();
        for (Vehiculo vehiculo : vehiculos) {
            if (vehiculo == null) {
                throw new IllegalArgumentException("La selección no puede contener vehículos nulos.");
            }
            for (Vehiculo anterior : this.vehiculos) {
                if (anterior.getCodigo().equals(vehiculo.getCodigo())) {
                    throw new IllegalArgumentException("La selección contiene un vehículo repetido.");
                }
            }
            this.vehiculos.add(vehiculo);
        }
        this.origen = origen;
        this.destino = destino;
        this.operador = operador;
        this.registrado = false;
    }

    public void registrar() throws ReglaNegocioException {
        if (registrado) {
            throw new ReglaNegocioException("El traslado ya fue registrado.");
        }
        if (destino.getVehiculos().size() + vehiculos.size() > destino.getCapacidadMaxima()) {
            throw new ReglaNegocioException("La estación de destino no tiene capacidad suficiente.");
        }
        List<Vehiculo> enOrigen = origen.getVehiculos();
        List<Vehiculo> enDestino = destino.getVehiculos();
        for (Vehiculo vehiculo : vehiculos) {
            if (!enOrigen.contains(vehiculo)) {
                throw new ReglaNegocioException("Un vehículo seleccionado no está en la estación de origen.");
            }
            if (vehiculo.getEstado() == EstadoVehiculo.EN_USO) {
                throw new ReglaNegocioException("No se puede trasladar un vehículo en uso.");
            }
            for (Vehiculo actual : enDestino) {
                if (actual.getCodigo().equals(vehiculo.getCodigo())) {
                    throw new ReglaNegocioException("Un vehículo seleccionado ya figura en el destino.");
                }
            }
        }
        for (Vehiculo vehiculo : vehiculos) {
            origen.retirarVehiculo(vehiculo);
            destino.agregarVehiculo(vehiculo);
        }
        registrado = true;
    }

    public Estacion getOrigen() {
        return origen;
    }

    public Estacion getDestino() {
        return destino;
    }

    public Operador getOperador() {
        return operador;
    }

    public boolean isRegistrado() {
        return registrado;
    }

    public List<Vehiculo> getVehiculos() {
        List<Vehiculo> copia = new ArrayList<>();
        for (Vehiculo vehiculo : vehiculos) {
            copia.add(vehiculo);
        }
        return copia;
    }
}
