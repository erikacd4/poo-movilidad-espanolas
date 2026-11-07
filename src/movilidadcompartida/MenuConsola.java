package movilidadcompartida;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MenuConsola {

    private Sistema sistema;
    private ArchivoSistema archivo;
    private String ruta;
    private Scanner teclado;

    public MenuConsola(Sistema sistema, ArchivoSistema archivo, String ruta) {
        this.sistema = sistema;
        this.archivo = archivo;
        this.ruta = ruta;
        teclado = new Scanner(System.in);
    }

    public void ejecutar() {
        boolean salir = false;
        try {
            while (!salir) {
                mostrarMenu();
                if (!teclado.hasNextLine()) {
                    System.out.println("Terminó la entrada. Los cambios no guardados no se conservaron.");
                    break;
                }
                try {
                    int opcion = entero("Opción:");
                    switch (opcion) {
                        case 32:
                            archivo.guardar(sistema, ruta);
                            System.out.println("Datos guardados. Fin del programa.");
                            salir = true;
                            break;
                        case 1:
                            registrarUsuario();
                            break;
                        case 2:
                            registrarEstacion();
                            break;
                        case 3:
                            registrarVehiculo();
                            break;
                        case 4:
                            registrarPersonal();
                            break;
                        case 5:
                            registrarTarifa();
                            break;
                        case 6:
                            registrarPromocion();
                            break;
                        case 7:
                            asociarMedio();
                            break;
                        case 8:
                            recargar();
                            break;
                        case 9:
                            usuario().agregarSuscripcion(new Suscripcion(LocalDateTime.now()));
                            System.out.println("Suscripción registrada por un mes desde ahora.");
                            break;
                        case 10:
                            sistema.retirarVehiculo(usuario(), vehiculo(), estacion());
                            System.out.println("Retiro registrado.");
                            break;
                        case 11:
                            registrarPunto();
                            break;
                        case 12:
                            devolver(false);
                            break;
                        case 13:
                            devolver(true);
                            break;
                        case 14:
                            sistema.reportarFalla(usuario(), vehiculo(), texto("Descripción de la falla:"));
                            System.out.println("Falla registrada.");
                            break;
                        case 15:
                            sistema.reportarFalla(operador(), vehiculo(), texto("Descripción de la falla:"));
                            System.out.println("Falla registrada.");
                            break;
                        case 16:
                            generarOrden();
                            break;
                        case 17:
                            sistema.registrarTrabajo(tecnico(), orden(), texto("Tareas realizadas:"), texto("Repuestos utilizados, o vacío:"));
                            System.out.println("Trabajo registrado.");
                            break;
                        case 18:
                            cerrarOrden();
                            break;
                        case 19:
                            sistema.gestionarEstado(operador(), vehiculo(), estado());
                            System.out.println("Estado actualizado.");
                            break;
                        case 20:
                            sistema.actualizarBateria(operador(), vehiculo(), decimal("Nivel de batería de 0 a 100:"));
                            System.out.println("Batería actualizada. La habilitación se revisa mediante cambiar estado.");
                            break;
                        case 21:
                            trasladar();
                            break;
                        case 22:
                            ajustar();
                            break;
                        case 23:
                            System.out.println("Viajes no devueltos: " + sistema.comprobarNoDevueltos(operador()).size());
                            break;
                        case 24:
                            System.out.println(administrador().reporteViajesUsuario(usuario()));
                            break;
                        case 25:
                            System.out.println(administrador().reporteUsoEstacion(sistema.getEstaciones(), sistema.getViajes()));
                            break;
                        case 26:
                            System.out.println(administrador().reporteVehiculosFallas(sistema.getVehiculos(), sistema.getReportesFalla()));
                            break;
                        case 27:
                            listarVehiculos();
                            break;
                        case 28:
                            listarUsuarios();
                            break;
                        case 29:
                            listarOrdenes();
                            break;
                        case 30:
                            if (sistema.getAlertas().isEmpty()) {
                                System.out.println("No hay alertas registradas.");
                            }
                            for (String alerta : sistema.getAlertas()) {
                                System.out.println(alerta);
                            }
                            break;
                        case 31:
                            archivo.guardar(sistema, ruta);
                            System.out.println("Datos guardados.");
                            break;
                        case 33:
                            salir = true;
                            System.out.println("Fin sin guardar los cambios posteriores al último guardado.");
                            break;
                        default:
                            System.out.println("La opción no existe.");
                    }
                } catch (ReglaNegocioException e) {
                    System.out.println("No se pudo completar la operación: " + e.getMessage());
                } catch (IllegalArgumentException e) {
                    System.out.println("Dato incorrecto: " + e.getMessage());
                } catch (IOException e) {
                    System.out.println("No se pudo guardar: " + e.getMessage());
                    System.out.println("El programa sigue abierto. Puede volver a intentar guardar.");
                }
            }
        } finally {
            teclado.close();
        }
    }

    private void mostrarMenu() {
        System.out.println("\nSISTEMA DE MOVILIDAD COMPARTIDA");
        System.out.println("1 Registrar usuario | 2 Registrar estación | 3 Registrar vehículo");
        System.out.println("4 Registrar personal | 5 Registrar tarifa | 6 Registrar promoción");
        System.out.println("7 Asociar medio de pago | 8 Recargar saldo | 9 Suscribirse al plan mensual");
        System.out.println("10 Retirar vehículo | 11 Registrar punto del recorrido | 12 Devolver vehículo");
        System.out.println("13 Cancelar retiro | 14 Reportar falla como usuario | 15 Reportar falla como operador");
        System.out.println("16 Generar orden | 17 Registrar trabajo | 18 Cerrar orden");
        System.out.println("19 Cambiar estado | 20 Actualizar batería | 21 Redistribuir vehículos");
        System.out.println("22 Registrar nota de ajuste | 23 Comprobar vehículos no devueltos");
        System.out.println("24 Reporte de viajes por usuario | 25 Uso por estación | 26 Vehículos con más fallas");
        System.out.println("27 Consultar vehículos | 28 Consultar usuarios | 29 Consultar órdenes | 30 Consultar alertas");
        System.out.println("31 Guardar | 32 Guardar y salir | 33 Salir sin guardar");
    }

    private String texto(String mensaje) {
        System.out.println(mensaje);
        if (!teclado.hasNextLine()) {
            throw new IllegalArgumentException("Terminó la entrada de datos.");
        }
        return teclado.nextLine();
    }

    private int entero(String mensaje) {
        return Integer.parseInt(texto(mensaje));
    }

    private double decimal(String mensaje) {
        double numero = Double.parseDouble(texto(mensaje));
        if (numero != numero || numero == Double.POSITIVE_INFINITY || numero == Double.NEGATIVE_INFINITY) {
            throw new IllegalArgumentException("Debe ingresar un número finito.");
        }
        return numero;
    }

    private int indice(int cantidad) {
        if (cantidad == 0) {
            throw new IllegalArgumentException("No hay elementos registrados para seleccionar.");
        }
        int numero = entero("Seleccione el número:");
        if (numero < 1 || numero > cantidad) {
            throw new IllegalArgumentException("El número seleccionado no existe.");
        }
        return numero - 1;
    }

    private Usuario usuario() {
        List<Usuario> lista = sistema.getUsuarios();
        for (int i = 0; i < lista.size(); i++) {
            System.out.println((i + 1) + " " + lista.get(i).getDocumento() + " " + lista.get(i).getNombre());
        }
        return lista.get(indice(lista.size()));
    }

    private Vehiculo vehiculo() {
        List<Vehiculo> lista = sistema.getVehiculos();
        for (int i = 0; i < lista.size(); i++) {
            System.out.println((i + 1) + " " + lista.get(i).getCodigo() + " " + lista.get(i).getEstado());
        }
        return lista.get(indice(lista.size()));
    }

    private Estacion estacion() {
        List<Estacion> lista = sistema.getEstaciones();
        for (int i = 0; i < lista.size(); i++) {
            Estacion e = lista.get(i);
            System.out.println((i + 1) + " " + e.getCodigo() + " " + e.getDireccion()
                    + " | Vehículos: " + e.getVehiculos().size() + "/" + e.getCapacidadMaxima());
        }
        return lista.get(indice(lista.size()));
    }

    private Personal empleado() {
        List<Personal> lista = sistema.getPersonal();
        for (int i = 0; i < lista.size(); i++) {
            System.out.println((i + 1) + " " + lista.get(i).getLegajo() + " " + lista.get(i).getNombre());
        }
        return lista.get(indice(lista.size()));
    }

    private Operador operador() {
        System.out.println("Seleccione al operador:");
        Personal p = empleado();
        if (!(p instanceof Operador)) {
            throw new IllegalArgumentException("Debe seleccionar un operador.");
        }
        return (Operador) p;
    }

    private Tecnico tecnico() {
        System.out.println("Seleccione al técnico:");
        Personal p = empleado();
        if (!(p instanceof Tecnico)) {
            throw new IllegalArgumentException("Debe seleccionar un técnico.");
        }
        return (Tecnico) p;
    }

    private Administrador administrador() {
        System.out.println("Seleccione al administrador:");
        Personal p = empleado();
        if (!(p instanceof Administrador)) {
            throw new IllegalArgumentException("Debe seleccionar un administrador.");
        }
        return (Administrador) p;
    }

    private OrdenMantenimiento orden() {
        listarOrdenes();
        List<OrdenMantenimiento> lista = sistema.getOrdenes();
        return lista.get(indice(lista.size()));
    }

    private TipoVehiculo tipoVehiculo() {
        int tipo = entero("Tipo: 1 bicicleta común, 2 bicicleta eléctrica, 3 monopatín eléctrico:");
        if (tipo == 1) return TipoVehiculo.BICICLETA;
        if (tipo == 2) return TipoVehiculo.BICICLETA_ELECTRICA;
        if (tipo == 3) return TipoVehiculo.MONOPATIN_ELECTRICO;
        throw new IllegalArgumentException("El tipo no existe.");
    }

    private EstadoVehiculo estado() {
        int tipo = entero("Estado: 1 disponible, 2 en uso, 3 en reparación, 4 batería baja, 5 fuera de servicio:");
        if (tipo == 1) return EstadoVehiculo.DISPONIBLE;
        if (tipo == 2) return EstadoVehiculo.EN_USO;
        if (tipo == 3) return EstadoVehiculo.EN_REPARACION;
        if (tipo == 4) return EstadoVehiculo.BATERIA_BAJA;
        if (tipo == 5) return EstadoVehiculo.FUERA_DE_SERVICIO;
        throw new IllegalArgumentException("El estado no existe.");
    }

    private void registrarUsuario() throws ReglaNegocioException {
        Usuario u = new Usuario(texto("Nombre:"), texto("Documento:"), texto("Correo electrónico:"), LocalDate.now());
        sistema.registrarUsuario(u);
        System.out.println("Usuario registrado con saldo inicial cero.");
    }

    private void registrarEstacion() throws ReglaNegocioException {
        Estacion e = new Estacion(texto("Código:"), texto("Dirección:"), decimal("Latitud:"), decimal("Longitud:"), entero("Capacidad máxima:"));
        sistema.registrarEstacion(e);
        System.out.println("Estación registrada.");
    }

    private void registrarVehiculo() throws ReglaNegocioException {
        TipoVehiculo tipo = tipoVehiculo();
        String codigo = texto("Código:");
        double kilometros = decimal("Kilómetros recorridos:");
        EstadoVehiculo estadoInicial = estado();
        Vehiculo v;
        if (tipo == TipoVehiculo.BICICLETA) {
            if (estadoInicial == EstadoVehiculo.BATERIA_BAJA) {
                throw new IllegalArgumentException("Una bicicleta común no tiene batería.");
            }
            v = new Bicicleta(codigo, LocalDate.now(), kilometros, estadoInicial, entero("Rodado:"));
        } else if (tipo == TipoVehiculo.BICICLETA_ELECTRICA) {
            v = new BicicletaElectrica(codigo, LocalDate.now(), kilometros, estadoInicial, decimal("Batería de 0 a 100:"), decimal("Autonomía:"));
        } else {
            v = new MonopatinElectrico(codigo, LocalDate.now(), kilometros, estadoInicial, decimal("Batería de 0 a 100:"), decimal("Velocidad máxima:"));
        }
        sistema.registrarVehiculo(v, estacion());
        System.out.println("Vehículo registrado en la estación.");
    }

    private void registrarPersonal() throws ReglaNegocioException {
        int rol = entero("Rol: 1 operador, 2 técnico, 3 administrador:");
        String nombre = texto("Nombre:");
        int legajo = entero("Legajo:");
        String telefono = texto("Teléfono:");
        Personal p;
        if (rol == 1) p = new Operador(nombre, legajo, telefono);
        else if (rol == 2) p = new Tecnico(nombre, legajo, telefono);
        else if (rol == 3) p = new Administrador(nombre, legajo, telefono);
        else throw new IllegalArgumentException("El rol no existe.");
        sistema.registrarPersonal(p);
        System.out.println("Personal registrado.");
    }

    private void registrarTarifa() throws ReglaNegocioException {
        Tarifa tarifa = new Tarifa(LocalDate.now(), tipoVehiculo(), decimal("Cargo de desbloqueo:"), decimal("Precio por minuto:"));
        sistema.registrarTarifa(tarifa);
        System.out.println("Tarifa registrada con vigencia desde hoy.");
    }

    private void registrarPromocion() {
        String nombre = texto("Nombre de la promoción:");
        int tipo = entero("Tipo: 1 primer viaje, 2 plan mensual:");
        TipoPromocion promocion;
        if (tipo == 1) promocion = TipoPromocion.PRIMER_VIAJE;
        else if (tipo == 2) promocion = TipoPromocion.PLAN_MENSUAL;
        else throw new IllegalArgumentException("El tipo no existe.");
        int meses = entero("Meses de vigencia de la promoción desde ahora:");
        if (meses <= 0) throw new IllegalArgumentException("La duración debe ser positiva.");
        LocalDateTime inicio = LocalDateTime.now();
        sistema.registrarPromocion(new Promocion(nombre, inicio, inicio.plusMonths(meses), promocion));
        System.out.println("Promoción registrada. Esta vigencia no modifica la duración mensual de la suscripción.");
    }

    private void asociarMedio() {
        Usuario u = usuario();
        int tipo = entero("Tipo: 1 tarjeta, 2 billetera virtual:");
        TipoMedioPago medio;
        if (tipo == 1) medio = TipoMedioPago.TARJETA;
        else if (tipo == 2) medio = TipoMedioPago.BILLETERA_VIRTUAL;
        else throw new IllegalArgumentException("El tipo no existe.");
        u.getCuenta().asociarMedioPago(new MedioPago(medio, texto("Identificador del medio de pago:")));
        System.out.println("Medio de pago asociado.");
    }

    private void recargar() {
        Usuario u = usuario();
        List<MedioPago> medios = u.getCuenta().getMediosPago();
        for (int i = 0; i < medios.size(); i++) {
            System.out.println((i + 1) + " " + medios.get(i).getTipo() + " " + medios.get(i).getIdentificador());
        }
        MedioPago medio = medios.get(indice(medios.size()));
        u.getCuenta().recargar(decimal("Importe de la recarga:"), medio);
        u.actualizarHabilitacion();
        System.out.println("Recarga registrada. Saldo: " + u.getCuenta().getSaldo());
    }

    private void registrarPunto() throws ReglaNegocioException {
        Viaje viaje = sistema.buscarViajeAbierto(usuario(), vehiculo());
        viaje.registrarPunto(decimal("Latitud:"), decimal("Longitud:"), LocalDateTime.now());
        System.out.println("Punto registrado con la fecha y hora actuales.");
    }

    private void devolver(boolean cancelar) throws ReglaNegocioException {
        Usuario u = usuario();
        Vehiculo v = vehiculo();
        Viaje viaje = sistema.buscarViajeAbierto(u, v);
        Estacion e = estacion();
        double distancia = decimal("Distancia recorrida en kilómetros:");
        boolean completado;
        if (cancelar) completado = sistema.cancelarRetiro(u, v, e, distancia);
        else completado = sistema.devolverVehiculo(u, v, e, distancia);
        if (completado) {
            System.out.println("Viaje " + viaje.getEstado() + " | Importe: " + viaje.getImporte() + " | Saldo: " + u.getCuenta().getSaldo());
        } else {
            System.out.println("La estación está llena. El viaje sigue abierto. Solicite la devolución en otra estación cercana.");
            System.out.println("El período adicional único de hasta 10 minutos comenzó: " + viaje.getInicioTiempoAdicional());
        }
    }

    private List<ReporteFalla> seleccionarReportes(List<ReporteFalla> opciones) {
        for (int i = 0; i < opciones.size(); i++) {
            ReporteFalla r = opciones.get(i);
            System.out.println((i + 1) + " " + r.getDescripcion() + " | Atendido: " + r.isAtendido());
        }
        String entrada = texto("Números separados por coma sin espacios; vacío para ninguno:");
        List<ReporteFalla> elegidos = new ArrayList<>();
        if (!entrada.equals("")) {
            String[] numeros = entrada.split(",");
            for (String numero : numeros) {
                int i = Integer.parseInt(numero) - 1;
                if (i < 0 || i >= opciones.size()) throw new IllegalArgumentException("El reporte no existe.");
                if (!elegidos.contains(opciones.get(i))) elegidos.add(opciones.get(i));
            }
        }
        return elegidos;
    }

    private void generarOrden() throws ReglaNegocioException {
        Operador op = operador();
        Vehiculo v = vehiculo();
        Tecnico te = tecnico();
        List<ReporteFalla> opciones = new ArrayList<>();
        for (ReporteFalla reporte : sistema.getReportesFalla()) {
            if (reporte.getVehiculo() == v && !reporte.isAtendido()) opciones.add(reporte);
        }
        sistema.generarOrden(op, v, te, seleccionarReportes(opciones));
        System.out.println("Orden generada y asignada al técnico.");
    }

    private void cerrarOrden() throws ReglaNegocioException {
        Tecnico te = tecnico();
        OrdenMantenimiento o = orden();
        sistema.cerrarOrden(te, o, seleccionarReportes(o.getReportes()));
        System.out.println("Orden cerrada. La habilitación del vehículo se revisa mediante cambiar estado.");
    }

    private void trasladar() throws ReglaNegocioException {
        Operador op = operador();
        System.out.println("Estación de origen:");
        Estacion origen = estacion();
        System.out.println("Estación de destino:");
        Estacion destino = estacion();
        int cantidad = entero("Cantidad de vehículos a trasladar:");
        if (cantidad <= 0) throw new IllegalArgumentException("La cantidad debe ser positiva.");
        List<Vehiculo> lista = new ArrayList<>();
        for (int i = 0; i < cantidad; i++) lista.add(vehiculo());
        sistema.registrarTraslado(op, origen, destino, lista);
        System.out.println("Traslado registrado.");
    }

    private void ajustar() throws ReglaNegocioException {
        Operador op = operador();
        List<Viaje> lista = sistema.getViajes();
        for (int i = 0; i < lista.size(); i++) {
            Viaje v = lista.get(i);
            System.out.println((i + 1) + " " + v.getUsuario().getNombre() + " " + v.getInicio() + " " + v.getEstado());
        }
        Viaje viaje = lista.get(indice(lista.size()));
        sistema.registrarNotaAjuste(op, viaje, texto("Motivo:"), texto("Detalle:"), decimal("Importe compensatorio: positivo acredita, negativo debita, cero sin cambio económico:"));
        System.out.println("Nota registrada. El viaje original se conserva.");
    }

    private void listarVehiculos() {
        for (Vehiculo v : sistema.getVehiculos()) {
            String ubicacion = "Sin estación";
            for (Estacion e : sistema.getEstaciones()) {
                if (e.getVehiculos().contains(v)) ubicacion = e.getCodigo();
            }
            System.out.println(v.getCodigo() + " " + v.obtenerTipo() + " | " + v.getEstado()
                    + " | Estación: " + ubicacion + " | Mantenimiento: " + v.isMantenimientoObligatorio()
                    + " | Perdido: " + v.isReportadoPerdido());
        }
    }

    private void listarUsuarios() {
        for (Usuario u : sistema.getUsuarios()) {
            System.out.println(u.getDocumento() + " " + u.getNombre() + " | Saldo: "
                    + u.getCuenta().getSaldo() + " | Habilitado: " + u.estaHabilitado());
        }
    }

    private void listarOrdenes() {
        List<OrdenMantenimiento> lista = sistema.getOrdenes();
        for (int i = 0; i < lista.size(); i++) {
            OrdenMantenimiento o = lista.get(i);
            System.out.println((i + 1) + " " + o.getVehiculo().getCodigo() + " | Técnico: "
                    + o.getTecnico().getNombre() + " | Apertura: " + o.getApertura() + " | Cerrada: " + o.isCerrada());
        }
    }
}
