# Cambios del diseño inicial al final

Se mantienen las clases del dominio, sus datos y asociaciones principales, las composiciones Cuenta-Movimiento y Viaje-PuntoRecorrido y las jerarquías de vehículos y personal.

## Coordinación de las operaciones

Se incorpora Sistema como controlador de retiro y devolución. Esa coordinación no estaba explicitada como clase en el diseño inicial. La decisión aplica Controlador y evita trasladar el procedimiento a la interfaz. Sistema delega las consultas y cambios en los objetos responsables.

## Consultas y beneficios del usuario

Se incorporan tieneViajeAbierto() y estaHabilitado() para expresar las consultas previas al retiro sin acceder directamente a datos privados. Se agregan primerViajeRealizado y marcarPrimerViajeRealizado() para conservar el consumo de la promoción del primer viaje finalizado no cancelado, según los supuestos documentados.

## Saldo mínimo y tipo de vehículo

Tarifa incorpora calcularSaldoMinimo(), siguiendo Experto: conoce los precios necesarios para el supuesto de desbloqueo más 30 minutos. Se incorpora obtenerTipo() en la jerarquía de vehículos para identificar el tipo tarifario. El cálculo del mínimo queda en Tarifa y la comparación con el saldo disponible, en Cuenta.

## Estado tras la devolución

Se incorpora actualizarEstadoTrasDevolucion() en Vehiculo y se sobrescribe en las variantes eléctricas. Aplica Polimorfismo y abierto/cerrado a ese comportamiento. La obligación de mantenimiento no se equipara automáticamente con que una reparación ya haya comenzado.

## Fin y cierre del viaje

Viaje distingue finalizar(destino, distancia), que registra los datos de fin, y cerrar(), que deja el viaje cerrado después del cálculo y cobro. Sistema.devolverVehiculo(...) recibe la distancia utilizada por Viaje y Vehiculo. Se explicita el orden de responsabilidades sin agregar reglas de cobro.

## Tiempo adicional por estación llena

Se incorpora registrarInicioTiempoAdicional(momento) en Viaje. El dato inicioTiempoAdicional ya estaba en el diseño inicial. La operación expresa el único período adicional descrito en los supuestos y en la alternativa de la devolución.

## Estados y relaciones

Se explicitan EstadoViaje, TipoVehiculo y TipoPromocion. La versión final usa ABIERTO, CERRADO y CANCELADO para los estados del viaje. Son nombres elegidos por el grupo: el enunciado no fija sus literales. Los cinco estados de vehículos se mantienen.

La imagen final reúne cada clase una sola vez. Las cajas de referencia que dividían el dibujo inicial en vistas no eran clases nuevas. En la imagen final las líneas se conectan con las clases originales. El uso puntual de objetos desde Sistema se representa con dependencias, sin rombos de agregación.

## Decisiones conservadas

La separación entre Usuario, Cuenta, Movimiento, Tarifa y Promocion, así como la herencia de vehículos y la sobrescritura de puedeRetirarse() en las variantes eléctricas, ya estaban en el diseño inicial. Se mantienen y fundamentan responsabilidad única, Creador y Polimorfismo. Se precisa el contrato común de retiro para justificar sustitución de Liskov.
