package movilidadcompartida;

import java.io.Serializable;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Sistema implements Serializable {

    private List<Usuario> usuarios;
    private List<Vehiculo> vehiculos;
    private List<Estacion> estaciones;
    private List<Tarifa> tarifas;
    private List<Promocion> promociones;
    private List<Viaje> viajes;
    private List<String> alertas;
    private List<Personal> personal;
    private List<ReporteFalla> reportesFalla;
    private List<ReporteFalla> causasMantenimiento;
    private List<OrdenMantenimiento> ordenes;
    private List<Traslado> traslados;
    private List<NotaAjuste> notasAjuste;

    public Sistema() {
        usuarios = new ArrayList<>();
        vehiculos = new ArrayList<>();
        estaciones = new ArrayList<>();
        tarifas = new ArrayList<>();
        promociones = new ArrayList<>();
        viajes = new ArrayList<>();
        alertas = new ArrayList<>();
        personal = new ArrayList<>();
        reportesFalla = new ArrayList<>();
        causasMantenimiento = new ArrayList<>();
        ordenes = new ArrayList<>();
        traslados = new ArrayList<>();
        notasAjuste = new ArrayList<>();
    }

    public void registrarUsuario(Usuario usuario) throws ReglaNegocioException {
        if (usuario == null) {
            throw new IllegalArgumentException("Debe indicar un usuario.");
        }
        if (buscarUsuario(usuario.getDocumento()) != null) {
            throw new ReglaNegocioException("Ya existe un usuario con ese documento.");
        }
        usuarios.add(usuario);
    }

    public void registrarEstacion(Estacion estacion) throws ReglaNegocioException {
        if (estacion == null) {
            throw new IllegalArgumentException("Debe indicar una estación.");
        }
        if (buscarEstacion(estacion.getCodigo()) != null) {
            throw new ReglaNegocioException("Ya existe una estación con ese código.");
        }
        if (!estacion.getVehiculos().isEmpty()) {
            throw new ReglaNegocioException("Registre primero la estación y luego sus vehículos.");
        }
        estaciones.add(estacion);
    }

    public void registrarVehiculo(Vehiculo vehiculo, Estacion estacion)
            throws ReglaNegocioException {
        comprobarEstacion(estacion);
        if (vehiculo == null) {
            throw new IllegalArgumentException("Debe indicar un vehículo.");
        }
        if (buscarVehiculo(vehiculo.getCodigo()) != null) {
            throw new ReglaNegocioException("Ya existe un vehículo con ese código.");
        }
        if (vehiculo.getEstado() == EstadoVehiculo.EN_USO) {
            throw new ReglaNegocioException("Un vehículo nuevo en una estación no puede estar en uso.");
        }
        if (!estacion.tieneCapacidad()) {
            throw new ReglaNegocioException("La estación está llena.");
        }
        estacion.agregarVehiculo(vehiculo);
        vehiculos.add(vehiculo);
        if (vehiculo.tieneBateriaBaja()
                && vehiculo.getEstado() == EstadoVehiculo.DISPONIBLE) {
            vehiculo.cambiarEstado(EstadoVehiculo.BATERIA_BAJA);
        }
        generarAlertaRedistribucion(estacion);
    }

    public void registrarTarifa(Tarifa tarifa) throws ReglaNegocioException {
        if (tarifa == null) {
            throw new IllegalArgumentException("Debe indicar una tarifa.");
        }
        for (Tarifa actual : tarifas) {
            if (actual.getTipoVehiculo() == tarifa.getTipoVehiculo()
                    && actual.getVigenteDesde().equals(tarifa.getVigenteDesde())) {
                throw new ReglaNegocioException("Ya existe una tarifa para ese tipo y fecha.");
            }
        }
        tarifas.add(tarifa);
    }

    public void registrarPromocion(Promocion promocion) {
        if (promocion == null) {
            throw new IllegalArgumentException("Debe indicar una promoción.");
        }
        if (!promociones.contains(promocion)) {
            promociones.add(promocion);
        }
    }

    public Usuario buscarUsuario(String documento) {
        for (Usuario usuario : usuarios) {
            if (usuario.getDocumento().equals(documento)) {
                return usuario;
            }
        }
        return null;
    }

    public Vehiculo buscarVehiculo(String codigo) {
        for (Vehiculo vehiculo : vehiculos) {
            if (vehiculo.getCodigo().equals(codigo)) {
                return vehiculo;
            }
        }
        return null;
    }

    public Estacion buscarEstacion(String codigo) {
        for (Estacion estacion : estaciones) {
            if (estacion.getCodigo().equals(codigo)) {
                return estacion;
            }
        }
        return null;
    }

    public Tarifa buscarTarifaVigente(Vehiculo vehiculo)
            throws ReglaNegocioException {
        return buscarTarifaVigente(vehiculo, LocalDate.now());
    }

    public Tarifa buscarTarifaVigente(Vehiculo vehiculo, LocalDate fecha)
            throws ReglaNegocioException {
        comprobarVehiculo(vehiculo);
        if (fecha == null) {
            throw new IllegalArgumentException("Debe indicar una fecha.");
        }
        Tarifa elegida = null;
        for (Tarifa tarifa : tarifas) {
            if (tarifa.getTipoVehiculo() == vehiculo.obtenerTipo()
                    && !tarifa.getVigenteDesde().isAfter(fecha)) {
                if (elegida == null
                        || tarifa.getVigenteDesde().isAfter(elegida.getVigenteDesde())) {
                    elegida = tarifa;
                }
            }
        }
        if (elegida == null) {
            throw new ReglaNegocioException("No hay tarifa vigente para este vehículo.");
        }
        return elegida;
    }

    public Viaje retirarVehiculo(Usuario usuario, Vehiculo vehiculo,
            Estacion estacion) throws ReglaNegocioException {
        return retirarVehiculo(usuario, vehiculo, estacion, LocalDateTime.now());
    }

    public Viaje retirarVehiculo(Usuario usuario, Vehiculo vehiculo,
            Estacion estacion, LocalDateTime momento) throws ReglaNegocioException {
        comprobarUsuario(usuario);
        comprobarVehiculo(vehiculo);
        comprobarEstacion(estacion);
        if (momento == null) {
            throw new IllegalArgumentException("Debe indicar el momento del retiro.");
        }
        usuario.actualizarHabilitacion();
        if (!usuario.estaHabilitado() || usuario.tieneViajeAbierto()) {
            throw new ReglaNegocioException("El usuario está inhabilitado o tiene un viaje abierto.");
        }
        if (!estacion.getVehiculos().contains(vehiculo)) {
            throw new ReglaNegocioException("El vehículo no está en esa estación.");
        }
        for (Viaje viaje : viajes) {
            if (viaje.getVehiculo() == vehiculo && viaje.getEstado() == EstadoViaje.ABIERTO) {
                throw new ReglaNegocioException("El vehículo ya tiene un viaje abierto.");
            }
        }
        Tarifa tarifa = buscarTarifaVigente(vehiculo, momento.toLocalDate());
        if (!usuario.getCuenta().alcanzaSaldoMinimo(tarifa.calcularSaldoMinimo())) {
            throw new ReglaNegocioException("El saldo no alcanza el mínimo requerido.");
        }
        if (vehiculo.tieneBateriaBaja()
                && vehiculo.getEstado() == EstadoVehiculo.DISPONIBLE) {
            vehiculo.cambiarEstado(EstadoVehiculo.BATERIA_BAJA);
        }
        if (!vehiculo.puedeRetirarse()) {
            throw new ReglaNegocioException("El vehículo no está disponible para retirar.");
        }
        Viaje viaje = new Viaje(usuario, vehiculo, estacion, momento, tarifa);
        for (Promocion promocion : promociones) {
            viaje.agregarPromocion(promocion);
        }
        estacion.retirarVehiculo(vehiculo);
        vehiculo.cambiarEstado(EstadoVehiculo.EN_USO);
        viajes.add(viaje);
        generarAlertaRedistribucion(estacion);
        return viaje;
    }

    public boolean devolverVehiculo(Usuario usuario, Vehiculo vehiculo,
            Estacion estacion, double distancia) throws ReglaNegocioException {
        return devolverVehiculo(usuario, vehiculo, estacion, distancia, LocalDateTime.now());
    }

    public boolean devolverVehiculo(Usuario usuario, Vehiculo vehiculo,
            Estacion estacion, double distancia, LocalDateTime momento)
            throws ReglaNegocioException {
        comprobarEstacion(estacion);
        Viaje viaje = buscarViajeAbierto(usuario, vehiculo);
        comprobarDevolucion(viaje, distancia, momento);
        if (!estacion.tieneCapacidad()) {
            viaje.registrarInicioTiempoAdicional(momento);
            return false;
        }
        viaje.finalizar(estacion, distancia, momento);
        double importe = viaje.calcularImporte();
        usuario.getCuenta().debitar(importe, viaje);
        viaje.cerrar();
        usuario.actualizarHabilitacion();
        Promocion aplicada = viaje.getPromocionAplicada();
        if (aplicada != null && aplicada.getTipo() == TipoPromocion.PLAN_MENSUAL) {
            viaje.getSuscripcionAlInicio().consumirViaje();
        }
        usuario.marcarPrimerViajeRealizado();
        completarIngreso(vehiculo, estacion, distancia);
        return true;
    }

    public boolean cancelarRetiro(Usuario usuario, Vehiculo vehiculo,
            Estacion estacion, double distancia) throws ReglaNegocioException {
        return cancelarRetiro(usuario, vehiculo, estacion, distancia, LocalDateTime.now());
    }

    public boolean cancelarRetiro(Usuario usuario, Vehiculo vehiculo,
            Estacion estacion, double distancia, LocalDateTime momento)
            throws ReglaNegocioException {
        comprobarEstacion(estacion);
        Viaje viaje = buscarViajeAbierto(usuario, vehiculo);
        comprobarDevolucion(viaje, distancia, momento);
        if (estacion != viaje.getOrigen()
                || Duration.between(viaje.getInicio(), momento).toMillis() > 120000) {
            return devolverVehiculo(usuario, vehiculo, estacion, distancia, momento);
        }
        if (!estacion.tieneCapacidad()) {
            viaje.registrarInicioTiempoAdicional(momento);
            return false;
        }
        viaje.cancelar(momento, distancia);
        completarIngreso(vehiculo, estacion, distancia);
        return true;
    }

    public Viaje buscarViajeAbierto(Usuario usuario, Vehiculo vehiculo)
            throws ReglaNegocioException {
        comprobarUsuario(usuario);
        comprobarVehiculo(vehiculo);
        for (Viaje viaje : viajes) {
            if (viaje.getUsuario() == usuario && viaje.getVehiculo() == vehiculo
                    && viaje.getEstado() == EstadoViaje.ABIERTO) {
                return viaje;
            }
        }
        throw new ReglaNegocioException("No hay un viaje abierto de ese usuario con ese vehículo.");
    }

    public void generarAlertaRedistribucion(Estacion estacion)
            throws ReglaNegocioException {
        comprobarEstacion(estacion);
        double ocupacion = estacion.calcularOcupacion();
        if (ocupacion < 20 || ocupacion > 90) {
            alertas.add("Redistribución: estación " + estacion.getCodigo()
                    + ", ocupación " + ocupacion + " %.");
        }
    }

    private void completarIngreso(Vehiculo vehiculo, Estacion estacion,
            double distancia) throws ReglaNegocioException {
        estacion.agregarVehiculo(vehiculo);
        vehiculo.agregarKilometros(distancia);
        vehiculo.setReportadoPerdido(false);
        vehiculo.actualizarEstadoTrasDevolucion();
        generarAlertaRedistribucion(estacion);
    }

    private void comprobarDevolucion(Viaje viaje, double distancia,
            LocalDateTime momento) throws ReglaNegocioException {
        if (distancia < 0 || momento == null) {
            throw new IllegalArgumentException("La distancia y el momento de devolución no son válidos.");
        }
        if (momento.isBefore(viaje.getInicio())) {
            throw new ReglaNegocioException("La devolución no puede ser anterior al retiro.");
        }
        List<PuntoRecorrido> puntos = viaje.getRecorrido();
        if (!puntos.isEmpty() && momento.isBefore(puntos.get(puntos.size() - 1).getHora())) {
            throw new ReglaNegocioException("La devolución no puede ser anterior al último punto.");
        }
        if (viaje.getInicioTiempoAdicional() != null
                && momento.isBefore(viaje.getInicioTiempoAdicional())) {
            throw new ReglaNegocioException("La devolución no puede ser anterior al intento previo.");
        }
    }

    private void comprobarUsuario(Usuario usuario) throws ReglaNegocioException {
        if (usuario == null || !usuarios.contains(usuario)) {
            throw new ReglaNegocioException("El usuario no está registrado en el sistema.");
        }
    }

    private void comprobarVehiculo(Vehiculo vehiculo) throws ReglaNegocioException {
        if (vehiculo == null || !vehiculos.contains(vehiculo)) {
            throw new ReglaNegocioException("El vehículo no está registrado en el sistema.");
        }
    }

    private void comprobarEstacion(Estacion estacion) throws ReglaNegocioException {
        if (estacion == null || !estaciones.contains(estacion)) {
            throw new ReglaNegocioException("Debe seleccionar una estación registrada.");
        }
    }

    public void registrarPersonal(Personal empleado) throws ReglaNegocioException {
        if (empleado == null) {
            throw new IllegalArgumentException("Debe indicar una persona del personal.");
        }
        for (Personal actual : personal) {
            if (actual.getLegajo() == empleado.getLegajo()) {
                throw new ReglaNegocioException("Ya existe personal con ese legajo.");
            }
        }
        personal.add(empleado);
    }

    public ReporteFalla reportarFalla(Usuario usuario, Vehiculo vehiculo,
            String descripcion) throws ReglaNegocioException {
        comprobarUsuario(usuario);
        comprobarVehiculo(vehiculo);
        ReporteFalla reporte = new ReporteFalla(vehiculo, descripcion, usuario);
        reportesFalla.add(reporte);
        actualizarMantenimiento(vehiculo, reporte.getFecha());
        return reporte;
    }

    public ReporteFalla reportarFalla(Operador operador, Vehiculo vehiculo,
            String descripcion) throws ReglaNegocioException {
        comprobarPersonal(operador);
        comprobarVehiculo(vehiculo);
        ReporteFalla reporte = new ReporteFalla(vehiculo, descripcion, operador);
        reportesFalla.add(reporte);
        actualizarMantenimiento(vehiculo, reporte.getFecha());
        return reporte;
    }

    private void actualizarMantenimiento(Vehiculo vehiculo, LocalDateTime momento) {
        List<ReporteFalla> recientes = new ArrayList<>();
        for (ReporteFalla reporte : reportesFalla) {
            long tiempo = Duration.between(reporte.getFecha(), momento).toMillis();
            if (reporte.getVehiculo() == vehiculo && tiempo >= 0 && tiempo <= 2592000000L) {
                recientes.add(reporte);
            }
        }
        if (recientes.size() >= 3) {
            for (ReporteFalla reporte : recientes) {
                if (!reporte.isAtendido() && !causasMantenimiento.contains(reporte)) {
                    causasMantenimiento.add(reporte);
                }
            }
        }
        boolean pendiente = false;
        for (ReporteFalla reporte : causasMantenimiento) {
            if (reporte.getVehiculo() == vehiculo && !reporte.isAtendido()) {
                pendiente = true;
            }
        }
        vehiculo.setMantenimientoObligatorio(pendiente);
        if (pendiente && (vehiculo.getEstado() == EstadoVehiculo.DISPONIBLE
                || vehiculo.getEstado() == EstadoVehiculo.BATERIA_BAJA)) {
            vehiculo.cambiarEstado(EstadoVehiculo.FUERA_DE_SERVICIO);
        }
    }

    public OrdenMantenimiento generarOrden(Operador operador, Vehiculo vehiculo,
            Tecnico tecnico, List<ReporteFalla> reportes) throws ReglaNegocioException {
        comprobarPersonal(operador);
        comprobarPersonal(tecnico);
        comprobarVehiculo(vehiculo);
        if (reportes == null || reportes.isEmpty()) {
            throw new ReglaNegocioException("Debe seleccionar reportes de falla.");
        }
        for (ReporteFalla reporte : reportes) {
            if (!reportesFalla.contains(reporte) || reporte.getVehiculo() != vehiculo) {
                throw new ReglaNegocioException("Los reportes deben estar registrados para ese vehículo.");
            }
        }
        OrdenMantenimiento orden = operador.generarOrden(vehiculo, tecnico, reportes);
        ordenes.add(orden);
        return orden;
    }

    public void registrarTrabajo(Tecnico tecnico, OrdenMantenimiento orden,
            String tareas, String repuestos) throws ReglaNegocioException {
        comprobarPersonal(tecnico);
        comprobarOrden(orden);
        if (tieneViajeAbierto(orden.getVehiculo())) {
            throw new ReglaNegocioException("No puede reparar un vehículo con un viaje abierto.");
        }
        tecnico.registrarTrabajo(orden, tareas, repuestos);
        orden.getVehiculo().cambiarEstado(EstadoVehiculo.EN_REPARACION);
    }

    public void cerrarOrden(Tecnico tecnico, OrdenMantenimiento orden,
            List<ReporteFalla> resueltos) throws ReglaNegocioException {
        comprobarPersonal(tecnico);
        comprobarOrden(orden);
        tecnico.cerrarOrden(orden, resueltos);
        actualizarMantenimiento(orden.getVehiculo(), LocalDateTime.now());
    }

    public Traslado registrarTraslado(Operador operador, Estacion origen,
            Estacion destino, List<Vehiculo> seleccionados) throws ReglaNegocioException {
        comprobarPersonal(operador);
        comprobarEstacion(origen);
        comprobarEstacion(destino);
        if (seleccionados == null || seleccionados.isEmpty()) {
            throw new ReglaNegocioException("Debe seleccionar vehículos para trasladar.");
        }
        for (Vehiculo vehiculo : seleccionados) {
            comprobarVehiculo(vehiculo);
            if (tieneViajeAbierto(vehiculo)) {
                throw new ReglaNegocioException("No puede trasladar un vehículo con un viaje abierto.");
            }
        }
        Traslado traslado = operador.registrarTraslado(origen, destino, seleccionados);
        traslados.add(traslado);
        generarAlertaRedistribucion(origen);
        generarAlertaRedistribucion(destino);
        return traslado;
    }

    public NotaAjuste registrarNotaAjuste(Operador operador, Viaje viaje,
            String motivo, String detalle, double importe) throws ReglaNegocioException {
        comprobarPersonal(operador);
        if (viaje == null || !viajes.contains(viaje)) {
            throw new ReglaNegocioException("Debe seleccionar un viaje registrado.");
        }
        NotaAjuste nota = operador.registrarNotaAjuste(viaje, motivo, detalle, importe);
        notasAjuste.add(nota);
        return nota;
    }

    public List<Viaje> comprobarNoDevueltos(Operador operador)
            throws ReglaNegocioException {
        return comprobarNoDevueltos(operador, LocalDateTime.now());
    }

    public List<Viaje> comprobarNoDevueltos(Operador operador, LocalDateTime momento)
            throws ReglaNegocioException {
        comprobarPersonal(operador);
        return operador.comprobarNoDevueltos(viajes, momento);
    }

    public void gestionarEstado(Operador operador, Vehiculo vehiculo,
            EstadoVehiculo estado) throws ReglaNegocioException {
        comprobarPersonal(operador);
        comprobarVehiculo(vehiculo);
        if (estado == null) {
            throw new IllegalArgumentException("Debe indicar un estado.");
        }
        boolean enViaje = tieneViajeAbierto(vehiculo);
        if (enViaje && estado != EstadoVehiculo.EN_USO) {
            throw new ReglaNegocioException("El vehículo tiene un viaje abierto.");
        }
        if (!enViaje && estado == EstadoVehiculo.EN_USO) {
            throw new ReglaNegocioException("No hay un viaje que justifique el estado en uso.");
        }
        if (estado == EstadoVehiculo.DISPONIBLE
                && (vehiculo.isMantenimientoObligatorio() || vehiculo.isReportadoPerdido()
                || vehiculo.tieneBateriaBaja() || tieneOrdenEnTrabajo(vehiculo))) {
            throw new ReglaNegocioException("El vehículo tiene condiciones pendientes que impiden habilitarlo.");
        }
        if (estado == EstadoVehiculo.BATERIA_BAJA && !vehiculo.tieneBateriaBaja()) {
            throw new ReglaNegocioException("Ese vehículo no tiene batería baja.");
        }
        vehiculo.cambiarEstado(estado);
    }

    private boolean tieneOrdenEnTrabajo(Vehiculo vehiculo) {
        for (OrdenMantenimiento orden : ordenes) {
            if (orden.getVehiculo() == vehiculo && !orden.isCerrada()
                    && !orden.getTareasRealizadas().equals("")) {
                return true;
            }
        }
        return false;
    }

    public void actualizarBateria(Operador operador, Vehiculo vehiculo, double nivel)
            throws ReglaNegocioException {
        comprobarPersonal(operador);
        comprobarVehiculo(vehiculo);
        if (vehiculo instanceof BicicletaElectrica) {
            ((BicicletaElectrica) vehiculo).setNivelBateria(nivel);
        } else if (vehiculo instanceof MonopatinElectrico) {
            ((MonopatinElectrico) vehiculo).setNivelBateria(nivel);
        } else {
            throw new ReglaNegocioException("La bicicleta común no tiene batería.");
        }
        if (vehiculo.tieneBateriaBaja()
                && vehiculo.getEstado() == EstadoVehiculo.DISPONIBLE) {
            vehiculo.cambiarEstado(EstadoVehiculo.BATERIA_BAJA);
        }
    }

    private boolean tieneViajeAbierto(Vehiculo vehiculo) {
        for (Viaje viaje : viajes) {
            if (viaje.getVehiculo() == vehiculo && viaje.getEstado() == EstadoViaje.ABIERTO) {
                return true;
            }
        }
        return false;
    }

    private void comprobarPersonal(Personal empleado) throws ReglaNegocioException {
        if (empleado == null || !personal.contains(empleado)) {
            throw new ReglaNegocioException("La persona no está registrada en el personal.");
        }
    }

    private void comprobarOrden(OrdenMantenimiento orden) throws ReglaNegocioException {
        if (orden == null || !ordenes.contains(orden)) {
            throw new ReglaNegocioException("La orden no está registrada en el sistema.");
        }
    }

    public List<Personal> getPersonal() {
        List<Personal> copia = new ArrayList<>();
        for (Personal empleado : personal) {
            copia.add(empleado);
        }
        Collections.sort(copia);
        return copia;
    }

    public List<ReporteFalla> getReportesFalla() {
        List<ReporteFalla> copia = new ArrayList<>();
        for (ReporteFalla reporte : reportesFalla) {
            copia.add(reporte);
        }
        return copia;
    }

    public List<OrdenMantenimiento> getOrdenes() {
        List<OrdenMantenimiento> copia = new ArrayList<>();
        for (OrdenMantenimiento orden : ordenes) {
            copia.add(orden);
        }
        return copia;
    }

    public List<Traslado> getTraslados() {
        List<Traslado> copia = new ArrayList<>();
        for (Traslado traslado : traslados) {
            copia.add(traslado);
        }
        return copia;
    }

    public List<NotaAjuste> getNotasAjuste() {
        List<NotaAjuste> copia = new ArrayList<>();
        for (NotaAjuste nota : notasAjuste) {
            copia.add(nota);
        }
        return copia;
    }

    public List<Usuario> getUsuarios() {
        List<Usuario> copia = new ArrayList<>();
        for (Usuario usuario : usuarios) {
            copia.add(usuario);
        }
        return copia;
    }

    public List<Vehiculo> getVehiculos() {
        List<Vehiculo> copia = new ArrayList<>();
        for (Vehiculo vehiculo : vehiculos) {
            copia.add(vehiculo);
        }
        return copia;
    }

    public List<Estacion> getEstaciones() {
        List<Estacion> copia = new ArrayList<>();
        for (Estacion estacion : estaciones) {
            copia.add(estacion);
        }
        return copia;
    }

    public List<Viaje> getViajes() {
        List<Viaje> copia = new ArrayList<>();
        for (Viaje viaje : viajes) {
            copia.add(viaje);
        }
        return copia;
    }

    public List<String> getAlertas() {
        List<String> copia = new ArrayList<>();
        for (String alerta : alertas) {
            copia.add(alerta);
        }
        return copia;
    }
}
