package movilidadcompartida;

import java.io.Serializable;

public abstract class Personal implements Comparable<Personal>, Serializable {

    private String nombre;
    private int legajo;
    private String telefono;

    public Personal(String nombre, int legajo, String telefono) {
        if (nombre == null || nombre.equals("")) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        if (telefono == null || telefono.equals("")) {
            throw new IllegalArgumentException("El teléfono es obligatorio.");
        }
        this.nombre = nombre;
        this.legajo = legajo;
        this.telefono = telefono;
    }

    public String getNombre() {
        return nombre;
    }

    public int getLegajo() {
        return legajo;
    }

    public String getTelefono() {
        return telefono;
    }

    @Override
    public int compareTo(Personal otro) {
        return Integer.compare(legajo, otro.legajo);
    }
}
