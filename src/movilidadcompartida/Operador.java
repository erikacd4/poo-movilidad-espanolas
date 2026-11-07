package movilidadcompartida;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Operador extends Personal {

    public Operador(String nombre, int legajo, String telefono) {
        super(nombre, legajo, telefono);
    }

    public OrdenMantenimiento generarOrden(Vehiculo vehiculo, Tecnico tecnico,
            List<ReporteFalla> reportes) {
        return new OrdenMantenimiento(vehiculo, tecnico, this, reportes);
    }

    public Traslado registrarTraslado(Estacion origen, Estacion destino,
            List<Vehiculo> vehiculos) throws ReglaNegocioException {
        Traslado traslado = new Traslado(origen, destino, vehiculos, this);
        traslado.registrar();
        return traslado;
    }

    public NotaAjuste registrarNotaAjuste(Viaje viaje, String motivo,
            String detalle, double importe) throws ReglaNegocioException {
        NotaAjuste nota = new NotaAjuste(viaje, this, motivo, detalle, importe);
        nota.registrar();
        return nota;
    }

    public List<Viaje> comprobarNoDevueltos(List<Viaje> viajes)
            throws ReglaNegocioException {
        return comprobarNoDevueltos(viajes, LocalDateTime.now());
    }

    public List<Viaje> comprobarNoDevueltos(List<Viaje> viajes,
            LocalDateTime momento) throws ReglaNegocioException {
        if (viajes == null || momento == null) {
            throw new IllegalArgumentException("Los viajes y el momento de comprobación son obligatorios.");
        }
        for (Viaje viaje : viajes) {
            if (viaje == null) {
                throw new IllegalArgumentException("La lista no puede contener viajes nulos.");
            }
        }
        List<Viaje> noDevueltos = new ArrayList<>();
        for (Viaje viaje : viajes) {
            if (viaje.superaPlazoDevolucion(momento)) {
                viaje.getVehiculo().setReportadoPerdido(true);
                if (!viaje.isCargoPerdidaAplicado()) {
                    viaje.getUsuario().getCuenta().debitar(50000, viaje);
                    viaje.marcarCargoPerdidaAplicado();
                }
                viaje.getUsuario().actualizarHabilitacion();
                noDevueltos.add(viaje);
            }
        }
        return noDevueltos;
    }
}
