# Aplicación de principios y patrones

Seleccionamos tres principios SOLID: responsabilidad única, abierto/cerrado y sustitución de Liskov. Los cuatro patrones GRASP son Experto, Creador, Controlador y Polimorfismo.

La revisión parte del diagrama inicial. Algunas separaciones y la jerarquía de vehículos ya estaban presentes: se mantienen y se justifican. Las incorporaciones y precisiones finales se detallan en [Cambios del diseño](cambios_diseno.md).

## Principios SOLID

### Responsabilidad única

- Problema detectado: el cálculo del viaje y la gestión del saldo requieren colaborar, pero no deben concentrarse en una sola clase ni trasladarse a la interfaz. Una corrección económica tampoco debe modificar un viaje cerrado.
- Decisión tomada: conservar la separación inicial. Cuenta gestiona saldo y movimientos mediante recargar, debitar y registrarAjuste. Viaje reúne los datos del viaje y coordina su cálculo; Tarifa conserva los precios y Promocion evalúa su aplicación e importe. NotaAjuste conserva la corrección separada del viaje. En el diseño final, Sistema coordina las peticiones sin asumir esas responsabilidades del dominio.
- Clases: Usuario, Cuenta, Movimiento, Viaje, Tarifa, Promocion, NotaAjuste y Sistema.
- Ventaja: cada cambio se concentra en su responsabilidad y las reglas pueden comprobarse por separado.

### Abierto/cerrado

- Problema detectado: repetir en los clientes las condiciones de retiro según el tipo concreto de vehículo obligaría a modificar esas comprobaciones cada vez que cambie una variante.
- Decisión tomada: conservar la jerarquía de Vehiculo y puedeRetirarse(). Las variantes eléctricas sobrescriben la operación e incorporan la regla de batería. En el diseño final también se sobrescribe actualizarEstadoTrasDevolucion() donde cambia ese comportamiento.
- Clases: Vehiculo, BicicletaComun, BicicletaElectrica, MonopatinElectrico y Sistema.
- Ventaja: los clientes mantienen la misma llamada para esas variaciones. Esta ventaja se limita a esos comportamientos: agregar otro tipo también puede requerir actualizar tarifas y tipos registrados.

### Sustitución de Liskov

- Problema detectado: compartir una firma no garantiza una sustitución correcta. Una subclase que permita retirar un vehículo no disponible rompería las reglas comunes.
- Decisión tomada: precisar el contrato de puedeRetirarse() para toda la jerarquía. Devuelve si el vehículo cumple las condiciones de retiro, sin iniciar un viaje ni retirarlo de la estación. Todas las variantes respetan disponibilidad, ausencia de mantenimiento obligatorio pendiente y ausencia de pérdida registrada. Las eléctricas también respetan la regla de batería. El resultado falso es una respuesta normal: el cliente no tiene que conocer la subclase antes de consultar.
- Clases: Vehiculo, sus tres subclases y los clientes que utilizan Vehiculo.
- Ventaja: Sistema consulta cualquier vehículo mediante la misma operación y trata el resultado con el mismo flujo. En la implementación deberá verificarse este contrato; devolver un booleano no basta por sí solo.

## Patrones GRASP

### Experto

- Problema detectado: si Sistema calculara la ocupación, el saldo suficiente y el importe, necesitaría conocer los datos internos de muchas clases.
- Decisión tomada: Estacion ofrece tieneCapacidad() y calcularOcupacion(); Cuenta compara el saldo con alcanzaSaldoMinimo(minimo). Tarifa incorpora calcularSaldoMinimo() porque conoce los precios para aplicar el supuesto de desbloqueo más 30 minutos. Viaje calcula su importe con su duración, tarifa y promoción. Suscripcion consulta su vigencia y descuenta los viajes utilizados.
- Clases: Estacion, Cuenta, Tarifa, Viaje, Promocion y Suscripcion.
- Ventaja: las secuencias muestran mensajes a los objetos responsables en lugar de cálculos del controlador sobre datos ajenos.

### Creador

- Problema detectado: crear objetos desde clases que no tienen sus datos iniciales produciría objetos incompletos y dependencias innecesarias.
- Decisión tomada: Cuenta crea sus Movimiento al registrar recargas, débitos y ajustes. Viaje crea sus PuntoRecorrido en registrarPunto. Sistema crea el nuevo Viaje después de validar el retiro, porque reúne usuario, vehículo, estación de origen, inicio y tarifa.
- Clases: Cuenta, Movimiento, Viaje, PuntoRecorrido y Sistema.
- Ventaja: los objetos se crean con sus datos y relaciones. Esto es coherente con las composiciones cuenta-movimientos y viaje-puntos.

### Controlador

- Problema detectado: el diagrama inicial no explicitaba qué objeto recibe una solicitud de retiro o devolución y ordena la colaboración.
- Decisión tomada: incorporar Sistema como controlador. Recibe retirarVehiculo(...) y devolverVehiculo(...), consulta los objetos del dominio y coordina los cambios. No sustituye a Viaje en el cálculo del importe ni a Cuenta en el saldo. Sus relaciones de uso puntual se representan como dependencias.
- Clases: Sistema y los objetos a los que delega.
- Ventaja: la coordinación queda identificada y la interfaz no tiene que dirigir todos los pasos.

### Polimorfismo

- Problema detectado: las condiciones de batería afectan a los vehículos eléctricos y no a las bicicletas comunes. Distribuir esa diferencia en los clientes repetiría la lógica.
- Decisión tomada: Sistema invoca puedeRetirarse() sobre una referencia Vehiculo. BicicletaElectrica y MonopatinElectrico sobrescriben la operación conservando las condiciones comunes. actualizarEstadoTrasDevolucion() sigue el mismo criterio para las variaciones del estado tras la devolución.
- Clases: Vehiculo, BicicletaComun, BicicletaElectrica, MonopatinElectrico y Sistema.
- Ventaja: las variaciones se resuelven en el vehículo concreto y el controlador utiliza la operación común.
