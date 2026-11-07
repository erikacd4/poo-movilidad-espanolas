package movilidadcompartida;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ArchivoSistema {

    public void guardar(Sistema sistema, String ruta) throws IOException {
        if (sistema == null || ruta == null || ruta.equals("")) {
            throw new IllegalArgumentException("El sistema y la ruta son obligatorios.");
        }
        try (ObjectOutputStream salida = new ObjectOutputStream(new FileOutputStream(ruta))) {
            salida.writeObject(sistema);
        }
    }

    public Sistema cargar(String ruta) throws IOException, ClassNotFoundException {
        if (ruta == null || ruta.equals("")) {
            throw new IllegalArgumentException("La ruta es obligatoria.");
        }
        if (!new File(ruta).exists()) {
            return new Sistema();
        }
        try (ObjectInputStream entrada = new ObjectInputStream(new FileInputStream(ruta))) {
            Object dato = entrada.readObject();
            if (!(dato instanceof Sistema)) {
                throw new IOException("El archivo no contiene un sistema de movilidad.");
            }
            return (Sistema) dato;
        }
    }
}
