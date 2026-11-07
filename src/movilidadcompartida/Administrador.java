package movilidadcompartida;

import java.util.ArrayList;
import java.util.List;

public class Administrador extends Personal {

    public Administrador(String nombre, int legajo, String telefono) {
        super(nombre, legajo, telefono);
    }

    public String reporteViajesUsuario(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("Debe seleccionar un usuario.");
        }
        List<Viaje> viajes = usuario.getViajes();
        if (viajes.isEmpty()) {
            return "El usuario no tiene viajes registrados.";
        }
        String texto = "Viajes de " + usuario.getNombre() + " (" + usuario.getDocumento() + ")\n";
        for (Viaje viaje : viajes) {
            String destino = "Sin devolución";
            if (viaje.getDestino() != null) {
                destino = viaje.getDestino().getCodigo();
            }
            texto = texto + "Vehículo: " + viaje.getVehiculo().getCodigo()
                    + " | Origen: " + viaje.getOrigen().getCodigo()
                    + " | Destino: " + destino
                    + " | Inicio: " + viaje.getInicio()
                    + " | Fin: " + viaje.getFin()
                    + " | Distancia: " + viaje.getDistanciaRecorrida()
                    + " | Estado: " + viaje.getEstado() + "\n";
        }
        return texto;
    }

    public String reporteUsoEstacion(List<Estacion> estaciones, List<Viaje> viajes) {
        if (estaciones == null || viajes == null) {
            throw new IllegalArgumentException("Las estaciones y los viajes son obligatorios.");
        }
        if (estaciones.isEmpty()) {
            return "No hay estaciones registradas.";
        }
        String texto = "Uso por estación\n";
        for (Estacion estacion : estaciones) {
            int retiros = 0;
            int devoluciones = 0;
            for (Viaje viaje : viajes) {
                if (viaje.getOrigen() == estacion) {
                    retiros = retiros + 1;
                }
                if (viaje.getDestino() == estacion
                        && viaje.getEstado() != EstadoViaje.ABIERTO) {
                    devoluciones = devoluciones + 1;
                }
            }
            texto = texto + estacion.getCodigo() + " | " + estacion.getDireccion()
                    + " | Retiros: " + retiros + " | Devoluciones: " + devoluciones + "\n";
        }
        return texto;
    }

    public String reporteVehiculosFallas(List<Vehiculo> vehiculos,
            List<ReporteFalla> reportes) {
        if (vehiculos == null || reportes == null) {
            throw new IllegalArgumentException("Los vehículos y los reportes son obligatorios.");
        }
        if (reportes.isEmpty()) {
            return "No hay fallas registradas.";
        }
        List<Vehiculo> ordenados = new ArrayList<>();
        for (Vehiculo vehiculo : vehiculos) {
            ordenados.add(vehiculo);
        }
        ComparadorVehiculosPorFallas comparador = new ComparadorVehiculosPorFallas(reportes);
        ordenados.sort(comparador);
        String texto = "Vehículos con más fallas\n";
        for (Vehiculo vehiculo : ordenados) {
            texto = texto + vehiculo.getCodigo() + " | " + vehiculo.obtenerTipo()
                    + " | Fallas: " + comparador.contarFallas(vehiculo) + "\n";
        }
        return texto;
    }
}
