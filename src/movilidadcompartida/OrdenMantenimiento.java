package movilidadcompartida;

import java.io.Serializable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrdenMantenimiento implements Serializable {

    private LocalDateTime apertura;
    private LocalDateTime cierre;
    private String tareasRealizadas;
    private String repuestosUtilizados;
    private Vehiculo vehiculo;
    private Tecnico tecnico;
    private Operador operador;
    private List<ReporteFalla> reportes;

    public OrdenMantenimiento(Vehiculo vehiculo, Tecnico tecnico,
            Operador operador, List<ReporteFalla> reportes) {
        if (vehiculo == null || tecnico == null || operador == null) {
            throw new IllegalArgumentException("El vehículo, el técnico y el operador son obligatorios.");
        }
        if (reportes == null || reportes.isEmpty()) {
            throw new IllegalArgumentException("Debe indicar al menos un reporte de falla.");
        }
        this.reportes = new ArrayList<>();
        for (ReporteFalla reporte : reportes) {
            if (reporte == null || reporte.getVehiculo() != vehiculo) {
                throw new IllegalArgumentException("Los reportes deben corresponder al vehículo de la orden.");
            }
            if (!this.reportes.contains(reporte)) {
                this.reportes.add(reporte);
            }
        }
        this.vehiculo = vehiculo;
        this.tecnico = tecnico;
        this.operador = operador;
        this.apertura = LocalDateTime.now();
        this.cierre = null;
        this.tareasRealizadas = "";
        this.repuestosUtilizados = "";
    }

    public void registrarTrabajo(String tareas, String repuestos)
            throws ReglaNegocioException {
        if (isCerrada()) {
            throw new ReglaNegocioException("La orden ya está cerrada.");
        }
        if (tareas == null || tareas.equals("")) {
            throw new ReglaNegocioException("Debe registrar las tareas realizadas.");
        }
        this.tareasRealizadas = tareas;
        if (repuestos == null) {
            this.repuestosUtilizados = "";
        } else {
            this.repuestosUtilizados = repuestos;
        }
    }

    public void cerrar(LocalDateTime fecha) throws ReglaNegocioException {
        cerrar(fecha, new ArrayList<ReporteFalla>());
    }

    public void cerrar(LocalDateTime fecha, List<ReporteFalla> resueltos)
            throws ReglaNegocioException {
        if (isCerrada()) {
            throw new ReglaNegocioException("La orden ya está cerrada.");
        }
        if (tareasRealizadas.equals("")) {
            throw new ReglaNegocioException("No se puede cerrar sin registrar las tareas.");
        }
        if (fecha == null || fecha.isBefore(apertura)) {
            throw new ReglaNegocioException("La fecha de cierre no es válida.");
        }
        if (resueltos == null) {
            throw new IllegalArgumentException("La lista de reportes resueltos no puede ser nula.");
        }
        for (ReporteFalla reporte : resueltos) {
            if (reporte == null || !reportes.contains(reporte)) {
                throw new ReglaNegocioException("Solo pueden resolverse reportes asociados a esta orden.");
            }
        }
        cierre = fecha;
        for (ReporteFalla reporte : resueltos) {
            reporte.marcarAtendido();
        }
    }

    public boolean isCerrada() {
        return cierre != null;
    }

    public LocalDateTime getApertura() {
        return apertura;
    }

    public LocalDateTime getCierre() {
        return cierre;
    }

    public String getTareasRealizadas() {
        return tareasRealizadas;
    }

    public String getRepuestosUtilizados() {
        return repuestosUtilizados;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public Tecnico getTecnico() {
        return tecnico;
    }

    public Operador getOperador() {
        return operador;
    }

    public List<ReporteFalla> getReportes() {
        List<ReporteFalla> copia = new ArrayList<>();
        for (ReporteFalla reporte : reportes) {
            copia.add(reporte);
        }
        return copia;
    }
}
