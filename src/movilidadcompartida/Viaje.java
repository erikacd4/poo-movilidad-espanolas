package movilidadcompartida;

import java.io.Serializable;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Viaje implements Serializable {

    private LocalDateTime inicio;
    private LocalDateTime fin;
    private double distanciaRecorrida;
    private EstadoViaje estado;
    private double importe;
    private LocalDateTime inicioTiempoAdicional;
    private boolean cargoPerdidaAplicado;
    private Usuario usuario;
    private Vehiculo vehiculo;
    private Estacion origen;
    private Estacion destino;
    private Tarifa tarifa;
    private Promocion promocionAplicada;
    private Suscripcion suscripcionAlInicio;
    private boolean primerViajeAlInicio;
    private boolean importeCalculado;
    private List<PuntoRecorrido> recorrido;
    private List<Promocion> promociones;

    public Viaje(Usuario usuario, Vehiculo vehiculo, Estacion origen,
            LocalDateTime inicio, Tarifa tarifa) throws ReglaNegocioException {
        if (usuario == null || vehiculo == null || origen == null
                || inicio == null || tarifa == null) {
            throw new IllegalArgumentException("Los datos de inicio del viaje son obligatorios.");
        }
        if (tarifa.getTipoVehiculo() != vehiculo.obtenerTipo()) {
            throw new ReglaNegocioException("La tarifa no corresponde al tipo de vehículo.");
        }
        this.usuario = usuario;
        this.vehiculo = vehiculo;
        this.origen = origen;
        this.inicio = inicio;
        this.tarifa = tarifa;
        this.fin = null;
        this.destino = null;
        this.estado = EstadoViaje.ABIERTO;
        this.distanciaRecorrida = 0;
        this.importe = 0;
        this.inicioTiempoAdicional = null;
        this.cargoPerdidaAplicado = false;
        this.importeCalculado = false;
        this.primerViajeAlInicio = !usuario.isPrimerViajeRealizado();
        this.suscripcionAlInicio = usuario.buscarSuscripcionVigente(inicio);
        if (suscripcionAlInicio != null
                && suscripcionAlInicio.getViajesDisponibles() == 0) {
            this.suscripcionAlInicio = null;
        }
        this.recorrido = new ArrayList<>();
        this.promociones = new ArrayList<>();
        usuario.registrarViaje(this);
    }

    public void agregarPromocion(Promocion promocion) throws ReglaNegocioException {
        comprobarAbiertoSinFin();
        if (promocion == null) {
            throw new IllegalArgumentException("Debe indicar una promoción.");
        }
        if (!promociones.contains(promocion)) {
            promociones.add(promocion);
        }
    }

    public void registrarPunto(double latitud, double longitud,
            LocalDateTime hora) throws ReglaNegocioException {
        comprobarAbiertoSinFin();
        if (hora == null) {
            throw new IllegalArgumentException("La fecha y hora del punto son obligatorias.");
        }
        if (hora.isBefore(inicio)) {
            throw new ReglaNegocioException("El punto no puede ser anterior al inicio del viaje.");
        }
        if (!recorrido.isEmpty()
                && hora.isBefore(recorrido.get(recorrido.size() - 1).getHora())) {
            throw new ReglaNegocioException("Los puntos deben registrarse en orden de tiempo.");
        }
        recorrido.add(new PuntoRecorrido(latitud, longitud, hora));
    }

    public void finalizar(Estacion destino, double distancia)
            throws ReglaNegocioException {
        finalizar(destino, distancia, LocalDateTime.now());
    }

    public void finalizar(Estacion destino, double distancia,
            LocalDateTime momento) throws ReglaNegocioException {
        comprobarAbiertoSinFin();
        if (destino == null) {
            throw new ReglaNegocioException("El vehículo debe devolverse en una estación.");
        }
        if (!destino.tieneCapacidad()) {
            throw new ReglaNegocioException("La estación de devolución está llena.");
        }
        if (distancia < 0) {
            throw new IllegalArgumentException("La distancia no puede ser negativa.");
        }
        comprobarMomentoFin(momento);
        this.destino = destino;
        this.distanciaRecorrida = distancia;
        this.fin = momento;
    }

    public void cancelar() throws ReglaNegocioException {
        cancelar(LocalDateTime.now());
    }

    public void cancelar(LocalDateTime momento) throws ReglaNegocioException {
        cancelar(momento, 0);
    }

    public void cancelar(LocalDateTime momento, double distancia)
            throws ReglaNegocioException {
        comprobarAbiertoSinFin();
        comprobarMomentoFin(momento);
        if (distancia < 0) {
            throw new IllegalArgumentException("La distancia no puede ser negativa.");
        }
        if (Duration.between(inicio, momento).toMillis() > 120000) {
            throw new ReglaNegocioException("Pasaron los 2 minutos de cancelación sin cargo.");
        }
        if (!origen.tieneCapacidad()) {
            throw new ReglaNegocioException("La estación de origen está llena.");
        }
        this.fin = momento;
        this.destino = origen;
        this.distanciaRecorrida = distancia;
        this.estado = EstadoViaje.CANCELADO;
        this.importe = 0;
        this.importeCalculado = true;
        this.promocionAplicada = null;
    }

    public boolean registrarInicioTiempoAdicional(LocalDateTime momento)
            throws ReglaNegocioException {
        comprobarAbiertoSinFin();
        if (momento == null || momento.isBefore(inicio)) {
            throw new IllegalArgumentException("El momento del intento de devolución no es válido.");
        }
        if (inicioTiempoAdicional != null) {
            return false;
        }
        inicioTiempoAdicional = momento;
        return true;
    }

    public double calcularImporte() throws ReglaNegocioException {
        if (estado == EstadoViaje.CERRADO || estado == EstadoViaje.CANCELADO) {
            return importe;
        }
        comprobarDatosFin();
        importe = calcularCosto(0, true);
        promocionAplicada = null;
        for (Promocion promocion : promociones) {
            if (promocion.esAplicable(this)) {
                double candidato = promocion.calcularImporte(this);
                if (candidato < importe
                        || (candidato == importe
                        && promocion.getTipo() == TipoPromocion.PRIMER_VIAJE)) {
                    importe = candidato;
                    promocionAplicada = promocion;
                }
            }
        }
        importeCalculado = true;
        return importe;
    }

    public double calcularImporteConMinutosGratis(int minutosGratis)
            throws ReglaNegocioException {
        if (minutosGratis < 0) {
            throw new IllegalArgumentException("Los minutos gratuitos no pueden ser negativos.");
        }
        comprobarDatosFin();
        return calcularCosto(minutosGratis, false);
    }

    public void cerrar() throws ReglaNegocioException {
        if (estado != EstadoViaje.ABIERTO || !importeCalculado || fin == null) {
            throw new ReglaNegocioException("El viaje debe tener fin e importe antes de cerrarse.");
        }
        estado = EstadoViaje.CERRADO;
    }

    public boolean superaPlazoDevolucion(LocalDateTime ahora) {
        if (ahora == null || estado != EstadoViaje.ABIERTO || fin != null) {
            return false;
        }
        return Duration.between(inicio, ahora).toMillis() >= 86400000;
    }

    public void marcarCargoPerdidaAplicado() throws ReglaNegocioException {
        comprobarAbiertoSinFin();
        cargoPerdidaAplicado = true;
    }

    private double calcularCosto(int minutosGratis, boolean cobrarDesbloqueo) {
        double total = Duration.between(inicio, fin).toMillis() / 60000.0;
        double finSinRecargo = total;
        if (finSinRecargo > 120) {
            finSinRecargo = 120;
        }
        double inicioConRecargo = 120;
        if (minutosGratis > inicioConRecargo) {
            inicioConRecargo = minutosGratis;
        }
        double normales = minutosCobrables(minutosGratis, finSinRecargo);
        double conRecargo = minutosCobrables(inicioConRecargo, total);
        double costo = normales * tarifa.getPrecioMinuto()
                + conRecargo * tarifa.getPrecioMinuto() * 1.5;
        if (cobrarDesbloqueo) {
            costo = costo + tarifa.getCargoDesbloqueo();
        }
        return costo;
    }

    private double minutosCobrables(double desde, double hasta) {
        if (hasta <= desde) {
            return 0;
        }
        double cantidad = hasta - desde;
        if (inicioTiempoAdicional != null) {
            double principioGratis = Duration.between(inicio,
                    inicioTiempoAdicional).toMillis() / 60000.0;
            double finalGratis = principioGratis + 10;
            if (principioGratis < desde) {
                principioGratis = desde;
            }
            if (finalGratis > hasta) {
                finalGratis = hasta;
            }
            if (finalGratis > principioGratis) {
                cantidad = cantidad - (finalGratis - principioGratis);
            }
        }
        return cantidad;
    }

    private void comprobarAbiertoSinFin() throws ReglaNegocioException {
        if (estado != EstadoViaje.ABIERTO || fin != null) {
            throw new ReglaNegocioException("El viaje ya fue finalizado o cancelado.");
        }
    }

    private void comprobarMomentoFin(LocalDateTime momento)
            throws ReglaNegocioException {
        if (momento == null || momento.isBefore(inicio)) {
            throw new ReglaNegocioException("El fin no puede ser anterior al inicio.");
        }
        if (!recorrido.isEmpty()
                && momento.isBefore(recorrido.get(recorrido.size() - 1).getHora())) {
            throw new ReglaNegocioException("El fin no puede ser anterior al último punto.");
        }
        if (inicioTiempoAdicional != null && momento.isBefore(inicioTiempoAdicional)) {
            throw new ReglaNegocioException("El fin no puede ser anterior al intento de devolución.");
        }
    }

    private void comprobarDatosFin() throws ReglaNegocioException {
        if (fin == null || destino == null || estado == EstadoViaje.CANCELADO) {
            throw new ReglaNegocioException("El viaje no tiene los datos de fin necesarios.");
        }
    }

    public LocalDateTime getInicio() {
        return inicio;
    }

    public LocalDateTime getFin() {
        return fin;
    }

    public double getDistanciaRecorrida() {
        return distanciaRecorrida;
    }

    public EstadoViaje getEstado() {
        return estado;
    }

    public double getImporte() {
        return importe;
    }

    public LocalDateTime getInicioTiempoAdicional() {
        return inicioTiempoAdicional;
    }

    public boolean isCargoPerdidaAplicado() {
        return cargoPerdidaAplicado;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public Estacion getOrigen() {
        return origen;
    }

    public Estacion getDestino() {
        return destino;
    }

    public Tarifa getTarifa() {
        return tarifa;
    }

    public Promocion getPromocionAplicada() {
        return promocionAplicada;
    }

    public Suscripcion getSuscripcionAlInicio() {
        return suscripcionAlInicio;
    }

    public boolean isPrimerViajeAlInicio() {
        return primerViajeAlInicio;
    }

    public List<PuntoRecorrido> getRecorrido() {
        List<PuntoRecorrido> copia = new ArrayList<>();
        for (PuntoRecorrido punto : recorrido) {
            copia.add(punto);
        }
        return copia;
    }
}
