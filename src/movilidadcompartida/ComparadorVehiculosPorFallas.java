package movilidadcompartida;

import java.util.Comparator;
import java.util.List;

public class ComparadorVehiculosPorFallas implements Comparator<Vehiculo> {

    private List<ReporteFalla> reportes;

    public ComparadorVehiculosPorFallas(List<ReporteFalla> reportes) {
        if (reportes == null) {
            throw new IllegalArgumentException("La lista de reportes es obligatoria.");
        }
        this.reportes = reportes;
    }

    public int contarFallas(Vehiculo vehiculo) {
        int cantidad = 0;
        for (ReporteFalla reporte : reportes) {
            if (reporte.getVehiculo() == vehiculo) {
                cantidad = cantidad + 1;
            }
        }
        return cantidad;
    }

    @Override
    public int compare(Vehiculo primero, Vehiculo segundo) {
        return Integer.compare(contarFallas(segundo), contarFallas(primero));
    }
}
