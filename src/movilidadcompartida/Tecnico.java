package movilidadcompartida;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Tecnico extends Personal {

    public Tecnico(String nombre, int legajo, String telefono) {
        super(nombre, legajo, telefono);
    }

    public void registrarTrabajo(OrdenMantenimiento orden, String tareas,
            String repuestos) throws ReglaNegocioException {
        comprobarAsignacion(orden);
        orden.registrarTrabajo(tareas, repuestos);
    }

    public void cerrarOrden(OrdenMantenimiento orden) throws ReglaNegocioException {
        cerrarOrden(orden, new ArrayList<ReporteFalla>());
    }

    public void cerrarOrden(OrdenMantenimiento orden, List<ReporteFalla> resueltos)
            throws ReglaNegocioException {
        comprobarAsignacion(orden);
        orden.cerrar(LocalDateTime.now(), resueltos);
    }

    private void comprobarAsignacion(OrdenMantenimiento orden)
            throws ReglaNegocioException {
        if (orden == null) {
            throw new IllegalArgumentException("Debe indicar una orden.");
        }
        if (orden.getTecnico() != this) {
            throw new ReglaNegocioException("La orden no está asignada a este técnico.");
        }
    }
}
