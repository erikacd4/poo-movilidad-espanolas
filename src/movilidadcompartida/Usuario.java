package movilidadcompartida;

import java.io.Serializable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Usuario implements Serializable {

    private String nombre;
    private String documento;
    private String correoElectronico;
    private LocalDate fechaAlta;
    private boolean habilitado;
    private boolean primerViajeRealizado;
    private Cuenta cuenta;
    private List<Viaje> viajes;
    private List<Suscripcion> suscripciones;

    public Usuario(String nombre, String documento, String correoElectronico,
            LocalDate fechaAlta) {
        if (nombre == null || nombre.equals("")) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        if (documento == null || documento.equals("")) {
            throw new IllegalArgumentException("El documento es obligatorio.");
        }
        if (correoElectronico == null || correoElectronico.equals("")) {
            throw new IllegalArgumentException("El correo electrónico es obligatorio.");
        }
        if (fechaAlta == null) {
            throw new IllegalArgumentException("La fecha de alta es obligatoria.");
        }
        this.nombre = nombre;
        this.documento = documento;
        this.correoElectronico = correoElectronico;
        this.fechaAlta = fechaAlta;
        this.cuenta = new Cuenta();
        this.habilitado = true;
        this.primerViajeRealizado = false;
        this.viajes = new ArrayList<>();
        this.suscripciones = new ArrayList<>();
    }

    public void actualizarHabilitacion() {
        if (cuenta.getSaldo() < 0) {
            habilitado = false;
        } else {
            habilitado = true;
        }
    }

    public boolean estaHabilitado() {
        return habilitado;
    }

    public void marcarPrimerViajeRealizado() {
        primerViajeRealizado = true;
    }

    public boolean tieneViajeAbierto() {
        for (Viaje viaje : viajes) {
            if (viaje.getEstado() == EstadoViaje.ABIERTO) {
                return true;
            }
        }
        return false;
    }

    public void registrarViaje(Viaje viaje) throws ReglaNegocioException {
        if (viaje == null || viaje.getUsuario() != this) {
            throw new IllegalArgumentException("El viaje debe corresponder a este usuario.");
        }
        if (viajes.contains(viaje)) {
            return;
        }
        if (viaje.getEstado() == EstadoViaje.ABIERTO && tieneViajeAbierto()) {
            throw new ReglaNegocioException("El usuario ya tiene un viaje abierto.");
        }
        viajes.add(viaje);
    }

    public void agregarSuscripcion(Suscripcion suscripcion)
            throws ReglaNegocioException {
        if (suscripcion == null) {
            throw new IllegalArgumentException("Debe indicar una suscripción.");
        }
        if (suscripciones.contains(suscripcion)) {
            return;
        }
        for (Suscripcion actual : suscripciones) {
            if (suscripcion.getInicio().isBefore(actual.getFin())
                    && actual.getInicio().isBefore(suscripcion.getFin())) {
                throw new ReglaNegocioException("El usuario ya tiene una suscripción en ese período.");
            }
        }
        suscripciones.add(suscripcion);
    }

    public Suscripcion buscarSuscripcionVigente(LocalDateTime momento) {
        for (Suscripcion suscripcion : suscripciones) {
            if (suscripcion.estaVigente(momento)) {
                return suscripcion;
            }
        }
        return null;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDocumento() {
        return documento;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public LocalDate getFechaAlta() {
        return fechaAlta;
    }

    public Cuenta getCuenta() {
        return cuenta;
    }

    public boolean isPrimerViajeRealizado() {
        return primerViajeRealizado;
    }

    public List<Viaje> getViajes() {
        List<Viaje> copia = new ArrayList<>();
        for (Viaje viaje : viajes) {
            copia.add(viaje);
        }
        return copia;
    }

    public List<Suscripcion> getSuscripciones() {
        List<Suscripcion> copia = new ArrayList<>();
        for (Suscripcion suscripcion : suscripciones) {
            copia.add(suscripcion);
        }
        return copia;
    }
}
