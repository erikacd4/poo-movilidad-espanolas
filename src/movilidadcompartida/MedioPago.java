package movilidadcompartida;

import java.io.Serializable;

public class MedioPago implements Serializable {

    private TipoMedioPago tipo;
    private String identificador;

    public MedioPago(TipoMedioPago tipo, String identificador) {
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de medio de pago es obligatorio.");
        }
        if (identificador == null || identificador.equals("")) {
            throw new IllegalArgumentException("El identificador es obligatorio.");
        }
        this.tipo = tipo;
        this.identificador = identificador;
    }

    public TipoMedioPago getTipo() {
        return tipo;
    }

    public String getIdentificador() {
        return identificador;
    }
}
