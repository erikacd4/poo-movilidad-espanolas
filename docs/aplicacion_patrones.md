# Aplicación de patrones 

Los principios de Solid elegidos: 
- Responsabilidad única
- Abierto/Cerrado
- Sustitución de Liskov

Los principios de Grasp elegidos:
- Experto
- Creador
- Controlador
- Polimorfismo

## Solid 

### Responsabilidad única 

- Problema detectado: si Usuario guarda además de un saldo, los movimientos, medios de pago y suscripción, y si viaje tuviese las reglas de tarifas y promociones, cada clase cambiaría por motivos muy distintos. Cambiar una promoción obligaría a cambiar el viaje.
- Decisión tomada: repartir las responsabilidades entre clases. Usuario guarda sus datos personales y su habilitación. Cuenta gestionaa el saldo y los movimientos (recargar, debitar, registrarAjuste). MedioPago y Suscripción son clases propias. Tarifa guarda el cargo de desbloqueo y el precio por minuto, Promoción decide si se aplica (esAplicable) y cuanto cuesta el viaje con ella (calcularImporte), y NotaAjuste registra las correcciones sin modificar el viaje cerrado.
- Clases: Usuario, Cuenta, Movimiento, MedioPago, Suscripción, Trifa, Promoción, Viaje, NotaAjuste. 
- Ventaja: Cambio en una regla afecta solo a su clase, las clases son más pequeñas y más fáciles de probar con pruebas unitarias.

### Abierto/cerrado

- Problema detectado: los vehículos se comportan distinto según su tipo. Si Sistema o Estación deciden con condicionales según el tipo, añadir un tipo nuevo obligaría a modificar esos condiconales.
- Decisión tomada: Vehículo es una clase abstracta con el método puedeRetirarse(). Cada subclase aporta su propia comprobación, el resto del sistema trabaja siempre con Vehiculo, sin preguntar de que tipo es.
- Clases: Vehiculo, BicicletaComun, BicicletaElectrica, MonopatinElectrico, y las que lo usan: Estacion, Traslado y Sistema
- Ventaja: Se puede añadir un tipo nuevo de vehículo creando una subclase sin modificar las clases que ya funcionan.

### Sustitución de Liskov

- Problema detectado: Si una subclase cambiara el significado de un método heredado, el código que usa Vehiculo dejaría de funcionar al recibir esa subclase.
- Decisión tomada: Las tres subclases pueden usarse en cualquier lugar donde se espera un Vehiculo. Puederetirarse() siempre devuelve un booleano con el mismo significado. Las eléctricas añaden la comprobación de batería sin eliminar las comprobaciones de la clase base.
- Clases: Vehiculo, BicicletaComun, BicicletaElectrica, MonopatinElectrico, Estacion y Traslado.
- Ventaja: Estacion, Traslado y Sistema tratan igual a cualquier vehiculo, sin comprobar su tipo. 

## Grasp 

### Experto 

- Problema detectado: Si Sistema calculará por sí mismo la ocupación, el saldo suficiente o importe, tendría que conocer los datos internos de las demás clases.
- Decisión tomada: Cada tarea la hace la clase que tiene los datos necesarios. Estacion calcula su ocupación y su capacidad (tieneCapacidad, calcularOcupacion) porque conoce su capacidad máxima y sus vehículos cuenta comprueba el saldo mínimo. Viaje calcula su importe porque conoce su inicio, fin y su tarifa y su promoción. Suscripcion sabe si está vigente y descuenta viajes. 
- Clases:Estacion, Cuenta, Viaje y Suscripción.
- Ventajas: cada dato se trata donde está y se ve en los diagramas de secuencia, donde sistema solo pregunta y cada objeto responde

### Creador

- Problema detectado: Si cualquier clase pudiera crear cualquier objeto, el conocimiento sobre como se construyen quedaría repartido y habría dependencias innecesarias. 
- Solución tomada: Crear un objeto que la clase lo contiene, lo registra o tiene los datos para inicializarlo, Cuenta crea sus Movimiento al recargar, o controlar un ajuste. Viaje crea sus PuntoRecorrido en registrarPunto. En retirar un vehículo Sistema crea su viaje, porque reúne el usuario, vehículo, tarida y estación. 
- Clases: Cuenta, Movimiento, Viaje, PuntoRecorrido, Sistema
- Ventaja: Los objetos se crean ya relacionados con su contenedor, un movimiento no existe sin su cuenta ni un punto sin su viaje, lo que coincide con las composiciones del diagrama de clases.

### Controlador

- Problema detectado: sin una clase que recibe las peticiones, la interfaz tendría que conocer y coordinar directamente muchas clases de dominio, mezclando interfaz y lógica. 
- Decisión tomada:Sistema representa al sistema completo. Recibe las peticiones del usuario y las delega en los objetos del domino, sin contener la lógica, aparece en los diagramas de secuencia.
- Clase: Sistema
- Ventaja: La interfaz solo habla con Ssitema, así que se puede cambiar de consola a interfaz gráfica sin tocar el dominio.

### Polimorfismo 

- Problema detectado: El comportamiento varia según el tipo (comprobar la batería) se resolvería con condicionales sobre el tipo de vehículo. 
- Decisión tomada: puedeRetirarse() se declara en vehículo y se sobreescribe en BicicletaElectrica y MonopatinElectrico. Sistema lo invoca igual para cualquier vehículo, como se ve en el diagrama de secuencia. 
- Clases: Vehiculo, BicicletaElectrica, MonopatinElectrica
- Ventaja: Sería que desaparecen los condicionales por tipo, y cada subclase se ocupa de su propia regla. 
