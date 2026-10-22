# Casos de uso 

## Registrar usuario

### Actor

Usuario.

### Precondición

La persona no está registrada como usuario del servicio.

### Flujo principal

1. La persona solicita registrarse.
2. El sistema solicita su nombre, documento y correo electrónico.
3. La persona ingresa los datos solicitados. 
4. El sistema registra al usuario y su fecha de alta.
5. El sistema crea una cuenta asociada al usuario, con saldo inicial cero.
6. El sistema informa que el registro se realizó correctamente.

### Flujos alternativos

- Si faltan datos obligatorios, el sistema solicita completarlos antes de registrar al usuario.


## Retirar un vehículo

### Actor

Usuario.

### Precondición

El usuario está registrado y el vehículo que solicita retirar se encuentra en una estación.

### Flujo principal

1. El usuario solicita retirar un vehículo de una estación.
2. El sistema verifica que el usuario esté habilitado, que no tenga otro viaje abierto y que su saldo alcance el mínimo establecido.
3. El sistema verifica que el vehículo esté disponible y no tenga mantenimiento obligatorio pendiente. Si es eléctrico, comprueba que su batería no sea menor al 15%. 
4. El sistema registra el inicio del viaje, asociando el usuario, el vehículo, la estación de retiro, el momento de inicio y la tarifa vigente correspondiente al tipo de vehículo.
5. El sistema cambia el estado del vehículo a en uso y actualiza la ocupación de la estación.
6. El sistema comprueba la ocupación resultante de la estación. Si es menor al 20% o mayor al 90%, genera una alerta de redistribución para el operador.
7. El sistema informa que el retiro se realizó correctamente.
 
### Flujos alternativos
  
- Si el usuario está inhabilitado, tiene otro viaje abierto o no alcanza el saldo mínimo, el sistema informa el motivo y no registra un nuevo viaje.
- Si el vehículo no está disponible, el sistema informa el motivo y no permite retirarlo.
- Si el vehículo es eléctrico y su batería es menor al 15%, el sistema lo deja en estado batería baja y no permite retirarlo.
- Si el vehículo tiene mantenimiento obligatorio pendiente, el sistema informa el motivo y no permite retirarlo.

### Registro del recorrido durante el viaje

Después de iniciar el viaje y mientras permanezca abierto:

1. El usuario ingresa las coordenadas y la hora de un punto del recorrido.
2. El sistema comprueba que el viaje continúe abierto y que estén presentes los datos del punto.
3. El sistema registra el punto asociado al viaje.
4. El procedimiento se repite por cada punto ingresado.

Si faltan las coordenadas o la hora, el sistema solicita completarlas antes de registrar el punto.

Si el viaje ya está cerrado o cancelado, el sistema no permite incorporar nuevos puntos.

## Devolver un vehículo

### Actor

Usuario.

### Precondición

El usuario tiene un viaje abierto con el vehículo que desea devolver.

### Flujo principal

1. El usuario solicita devolver el vehículo en una estación.
2. El sistema verifica que la estación tenga capacidad disponible.
3. El sistema registra la estación de devolución, el momento de fin y la distancia recorrida del viaje.
4. El sistema calcula el importe mediante el caso de uso Calcular importe del viaje.
5. El sistema descuenta el importe de la cuenta del usuario y registra el movimiento con su fecha e importe.
6. El sistema cierra el viaje y registra el vehículo en la estación.
7. El sistema registra el beneficio utilizado. Si aplicó el plan mensual, descuenta uno de los viajes disponibles. Si es el primer viaje finalizado no cancelado, registra que ese primer viaje ya se realizó.
8. El sistema actualiza el estado del vehículo según su condición. Si es eléctrico y su batería es menor al 15%, queda en batería baja. Si tiene mantenimiento obligatorio pendiente, no queda disponible para nuevos retiros.
9. El sistema actualiza la ocupación de la estación. Si es menor al 20% o mayor al 90%, genera una alerta de redistribución para el operador.
10. El sistema informa la finalización del viaje, el importe y el saldo resultante.

### Flujos alternativos

- Si la estación está llena, el sistema no completa la devolución ni cierra el viaje. En el primer intento rechazado por este motivo,
  registra el inicio del único período adicional de hasta 10 minutos sin cargo. El usuario debe dirigirse a otra estación cercana y solicitar nuevamente la devolución.
- Si intenta devolver el vehículo fuera de una estación, el sistema rechaza la devolución y mantiene el viaje abierto.
- Si el saldo resulta negativo después del cobro, el sistema inhabilita al usuario hasta que lo regularice. La devolución y el cierre del viaje se completan.

## Calcular importe del viaje

### Actor participante

Usuario, mediante el caso Devolver un vehículo.

### Precondición

Se conocen los momentos de inicio y fin del viaje y la tarifa asociada al retiro.

### Flujo principal

1. El sistema determina la duración del viaje a partir de sus momentos de inicio y fin.
2. El sistema utiliza la tarifa asociada al viaje al momento del retiro, con su cargo de desbloqueo y precio por minuto.
3. El sistema identifica los períodos sin cargo que correspondan por una devolución rechazada debido a una estación llena.
4. El sistema calcula el importe sin promociones: cargo de desbloqueo más tiempo cobrable al precio por minuto. Las fracciones de minuto se cobran proporcionalmente.
5. Para el tiempo posterior a las 2 horas desde el inicio, el precio por minuto lleva un recargo del 50%. Los períodos sin cargo no se cobran.
6. El sistema comprueba si corresponde la promoción del primer viaje y si, al iniciar el viaje, el usuario tenía un plan mensual vigente con viajes disponibles.
7. El sistema calcula el importe con cada promoción aplicable, respetando los minutos incluidos y el supuesto de cobro de excedentes.
8. El sistema selecciona la opción más favorable para el usuario, sin acumular promociones, y registra el importe y la promoción aplicada, si corresponde.

Los minutos gratuitos de las promociones corresponden al comienzo del viaje. Si coinciden con el período gratuito por estación llena, ese tiempo no se descuenta dos veces. El recargo del 50 % se aplica únicamente al tiempo cobrable posterior a las 2 horas desde el inicio.

### Flujos alternativos

- Si no corresponde ninguna promoción, el sistema utiliza el importe calculado sin promociones.
- Si el viaje queda completamente cubierto por una promoción, el importe resultante es cero.

## Asociar un medio de pago

### Actor

Usuario.

### Precondición

El usuario está registrado y tiene una cuenta en el servicio.

### Flujo principal

1. El usuario solicita asociar un medio de pago a su cuenta.
2. El sistema permite seleccionar tarjeta o billetera virtual.
3. El usuario selecciona el tipo de medio de pago e ingresa los datos correspondientes.
4. El sistema comprueba que los datos estén completos.
5. El sistema registra el medio de pago asociado a la cuenta del usuario.
6. El sistema informa que la asociación se realizó correctamente.

### Flujos alternativos

- Si faltan datos, el sistema solicita completarlos antes de registrar el medio de pago.

## Recargar saldo

### Actor

Usuario.

### Precondición

El usuario está registrado, tiene una cuenta y al menos un medio de pago asociado.

### Flujo principal

1. El usuario solicita recargar saldo.
2. El sistema muestra los medios de pago asociados a su cuenta.
3. El usuario selecciona un medio de pago e ingresa el importe.
4. El sistema verifica que el importe sea mayor que cero.
5. El sistema registra la recarga con su fecha e importe y actualiza el saldo de la cuenta.
6. Si el usuario estaba inhabilitado por saldo negativo y el nuevo saldo es mayor o igual a cero, el sistema lo habilita.
7. El sistema informa que la recarga se realizó correctamente y muestra el saldo actualizado.

### Flujos alternativos

- Si el importe es menor o igual a cero, el sistema informa que debe ingresar un importe positivo y no realiza la recarga.
- Si después de la recarga el saldo continúa siendo negativo, el sistema mantiene al usuario inhabilitado e informa el saldo pendiente.

## Cancelar un retiro

### Actor

Usuario.

### Precondición

El usuario tiene un viaje abierto con el vehículo cuyo retiro desea cancelar.

### Flujo principal

1. El usuario solicita cancelar el retiro y devuelve el vehículo en la estación de origen.
2. El sistema verifica que no hayan transcurrido más de 2 minutos desde el inicio del viaje.
3. El sistema comprueba que la estación sea la de origen y tenga capacidad disponible.
4. El sistema marca el viaje como cancelado y registra el momento de cancelación, conservando sus datos y sin aplicar cargos. El sistema no consume beneficios de promociones. 
5. El sistema registra el vehículo en la estación y actualiza su estado según su condición.
6. El sistema actualiza la ocupación de la estación. Si es menor al 20% o mayor al 90%, genera una alerta de redistribución para el operador.
7. El sistema informa que el retiro se canceló sin cargo.

### Flujos alternativos

- Si transcurrieron más de 2 minutos, no corresponde la cancelación sin cargo. La devolución se procesa mediante el caso de uso Devolver un vehículo.
- Si el usuario devuelve el vehículo en una estación distinta de la de origen, la devolución se procesa mediante el caso de uso Devolver un vehículo.
- Si la estación de origen está llena, no se completa la cancelación y el viaje permanece abierto. Se aplica la regla de estación llena descrita en Devolver un vehículo.
- Si el usuario intenta cancelar sin devolver el vehículo en una estación, el sistema no completa la cancelación y mantiene el viaje abierto.

## Reportar una falla

### Actores

Usuario y operador del sistema.

### Precondición

La persona que presenta el reporte está registrada como usuario o como operador del sistema.

### Flujo principal

1. El usuario o el operador solicita reportar una falla.
2. El sistema solicita identificar el vehículo e ingresar una descripción de la falla.
3. El actor identifica el vehículo e ingresa la descripción.
4. El sistema comprueba que el vehículo exista y que la descripción esté completa.
5. El sistema registra el reporte asociado al vehículo, con su descripción y fecha.
6. El sistema consulta los reportes del vehículo registrados en los últimos 30 días y distingue cuáles fueron atendidos.
7. Si existen al menos 3 reportes pendientes de atención en ese período, el sistema registra la obligación de mantenimiento. Si esa obligación ya estaba pendiente, la mantiene hasta que se registre su resolución. Mientras esté pendiente, impide nuevos retiros.
8. El sistema informa que el reporte se registró correctamente.

### Flujos alternativos

- Si el vehículo no está registrado, el sistema informa la situación y no registra el reporte.
- Si falta la descripción, el sistema solicita completarla antes de registrar el reporte.

## Generar una orden de mantenimiento

### Actor

Operador del sistema.

### Precondición

Existe un reporte de falla registrado para el vehículo y un técnico registrado al que se puede asignar la orden.

### Flujo principal

1. El operador solicita generar una orden de mantenimiento.
2. El sistema muestra los reportes de falla registrados.
3. El operador selecciona el vehículo y los reportes que motivan la orden.
4. El operador selecciona al técnico responsable.
5. El sistema registra la orden asociada al vehículo y al técnico, con su fecha de apertura y los reportes correspondientes.
6. El sistema informa que la orden se generó correctamente.

### Flujos alternativos

- Si no se selecciona un técnico, el sistema solicita asignarlo antes de registrar la orden.

### Aclaración
La orden puede generarse antes de alcanzar 3 reportes. Al alcanzar 3 reportes en 30 días, el mantenimiento es obligatorio.

## Registrar y cerrar una orden de mantenimiento

### Actor

Técnico de mantenimiento.

### Precondición 

El técnico y la orden de mantenimiento están registrados en el sistema. 

### Flujo principal

1. El técnico selecciona la orden de mantenimiento.
2. El sistema comprueba que la orden esté abierta y asignada al técnico.
3. El sistema muestra los datos de la orden y del vehículo.
4. El técnico registra las tareas realizadas y los repuestos utilizados, si corresponde.
5. El técnico solicita cerrar la orden.
6. El sistema comprueba que estén registrados el técnico responsable y las tareas realizadas.
7. El sistema registra la fecha de cierre y marca la orden como cerrada.
8. El sistema registra como atendidos los reportes de falla cuya resolución confirma el técnico, conservando su historial.
9. Si se resolvieron las fallas que motivaron el mantenimiento obligatorio y no quedan otras pendientes que lo exijan, el sistema registra que esa obligación fue atendida.
10. El sistema informa que la orden se cerró correctamente. El vehículo no pasa automáticamente a disponible: debe cumplir las condiciones de disponibilidad.

### Flujos alternativos

- Si la orden ya está cerrada, el sistema informa la situación y no permite cerrarla nuevamente.
- Si la orden no está asignada al técnico, el sistema no permite que registre el trabajo ni efectúe el cierre.
- Si faltan las tareas realizadas, el sistema solicita completarlas y mantiene la orden abierta.
- Si el trabajo registrado no resuelve todas las fallas que requieren mantenimiento obligatorio, el vehículo continúa bloqueado para nuevos retiros.

## Redistribuir vehículos entre estaciones

### Actor 
Operador del sistema.

### Precondición

Las estaciones de origen y destino están registradas y existen vehículos en la estación de origen.

### Flujo principal

1. El operador solicita registrar una redistribución de vehículos.
2. El sistema muestra las estaciones, su ocupación y las alertas de redistribución existentes.
3. El operador selecciona la estación de origen, la estación de destino y los vehículos que desea trasladar.
4. El sistema comprueba que las estaciones sean distintas, que los vehículos se encuentren en la estación de origen y que exista capacidad suficiente en la estación de destino.
5. El operador confirma el traslado realizado.
6. El sistema registra el traslado con su origen, destino, vehículos trasladados y operador responsable.
7. El sistema actualiza la ubicación de los vehículos y la ocupación de ambas estaciones.
8. El sistema comprueba la ocupación resultante de las estaciones y genera las alertas correspondientes si es menor al 20% o mayor al 90%.
9. El sistema informa que la redistribución se registró correctamente.

### Flujos alternativos

- Si la estación de origen coincide con la de destino, el sistema solicita seleccionar otra estación de destino.
- Si alguno de los vehículos no se encuentra en la estación de origen, el sistema informa la situación y solicita corregir la selección antes de registrar el traslado.
- Si la estación de destino no tiene capacidad suficiente, el sistema no registra el traslado y solicita modificar el destino o la cantidad de vehículos.
- Si no se seleccionan vehículos, el sistema solicita seleccionar al menos uno antes de registrar el traslado.

## Suscribirse al plan mensual

### Actor 

Usuario.

### Precondición

El usuario está registrado en el servicio.

### Flujo principal

1. El usuario solicita suscribirse al plan mensual.
2. El sistema muestra las condiciones del plan: incluye 20 viajes de hasta 30 minutos y sus beneficios no se acumulan con otras promociones.
3. El usuario confirma la suscripción.
4. El sistema registra la suscripción del usuario  con sus fechas de inicio y fin de vigencia, e inicializa la cantidad de viajes incluidos en 20.
5. El sistema informa que la suscripción se registró correctamente.

### Flujos alternativos

- Si el usuario no confirma la suscripción, el sistema no la registra.
- Si el usuario ya tiene una suscripción vigente, el sistema informa la situación y no registra otra suscripción simultánea.

### Suscripciones simultáneas

Consideramos que cada usuario puede tener una única suscripción vigente al plan mensual.

### Vigencia del plan mensual

Consideramos que la suscripción tiene vigencia durante un mes desde la fecha de inicio de la suscripción. Incluye 20 viajes durante ese período. Los viajes no utilizados no se acumulan para el siguiente período.


## Gestionar el estado de un vehículo

### Actor

Operador del sistema.

### Precondición 

El vehículo está registrado en el sistema.

### Flujo principal

1. El operador solicita actualizar el estado de un vehículo.
2. El sistema muestra sus datos y su estado actual.
3. El operador selecciona el nuevo estado.
4. El sistema comprueba que el cambio sea compatible con la situación del vehículo y las reglas de negocio.
5. El sistema actualiza el estado.
6. El sistema informa que el cambio se realizó correctamente.

### Flujos alternativos

- Si el vehículo tiene un viaje abierto, el sistema rechaza un cambio manual que lo deje disponible.
- Si un vehículo eléctrico tiene batería menor al 15%, el sistema no permite marcarlo como disponible.
- Si se intenta asignar el estado batería baja a una bicicleta común, el sistema rechaza el cambio porque no tiene batería.
- Si el vehículo requiere mantenimiento obligatorio, el sistema no permite marcarlo como disponible mientras esa condición continúe pendiente.

### Actualización manual del estado de los vehículos

Consideramos que el operador del sistema registra los cambios manuales de estado, respetando las reglas de negocio. El retiro y la devolución actualizan el estado como parte de sus respectivos procedimientos.

## Registrar una nota de ajuste

### Actor

Operador del sistema.

### Precondición

Existe un viaje registrado que requiere una corrección.

### Flujo principal

1. El operador solicita registrar una nota de ajuste.
2. El sistema solicita identificar el viaje.
3. El operador selecciona el viaje e indica el motivo y el detalle de la corrección. Si es económica, indica el importe que  corresponde acreditar o debitar.
4. El sistema comprueba que el viaje esté cerrado y que la información esté completa.
5. El sistema registra la nota asociada al viaje, conservando los datos originales.
6. Si existe una corrección económica, el sistema registra un movimiento compensatorio asociado a la nota, con su fecha e importe, y actualiza el saldo.
7. El sistema actualiza la habilitación del usuario según el saldo resultante: lo inhabilita si queda negativo y elimina la inhabilitación por deuda si se regulariza.
8. El sistema informa que la nota se registró correctamente.

### Flujos alternativos

- Si el viaje no existe, el sistema informa la situación y no registra la nota.
- Si el viaje no está cerrado, el sistema informa que este procedimiento corresponde a viajes cerrados y no registra la nota.
- Si falta el motivo o el detalle de la corrección, el sistema solicita completar la información antes de registrar la nota.

## Emitir reporte de viajes por usuario

### Actor

Administrador.

### Precondición

El usuario sobre el que se solicita el reporte está registrado en el sistema.

### Flujo principal

1. El administrador solicita emitir un reporte de viajes por usuario.
2. El sistema solicita seleccionar al usuario.
3. El administrador selecciona al usuario.
4. El sistema consulta los viajes asociados a ese usuario.
5. El sistema genera y muestra el reporte con los datos registrados de cada viaje: vehículo, estaciones de retiro y devolución, inicio, fin, distancia recorrida y estado.

### Flujos alternativos

- Si el usuario no tiene viajes registrados, el sistema informa que no existen viajes para mostrar.
- Si no se selecciona un usuario, el sistema solicita seleccionarlo antes de generar el reporte.

## Emitir un reporte de uso por estación

### Actor

Administrador.

### Precondición

Existen estaciones registradas en el sistema.

### Flujo principal

1. El administrador solicita emitir el reporte de uso por estación.
2. El sistema consulta las estaciones y los viajes registrados.
3. El sistema calcula para cada estación la cantidad de retiros y la cantidad de devoluciones correspondientes a esos viajes.
4. El sistema genera y muestra el reporte, identificando cada estación con su código y dirección.

### Flujos alternativos

- Si una estación no tiene retiros ni devoluciones registrados, el sistema la muestra con ambas cantidades en cero.

### Reporte de uso por estación

Consideramos que el uso de cada estación se representa mediante la cantidad de retiros y devoluciones de viajes registrados. Los traslados realizados por el operador se registran como redistribuciones y no se contabilizan como viajes.

## Emitir reporte de vehículos con más fallas

### Actor
Administrador.

### Precondición

Existen vehículos registrados en el sistema.

### Flujo principal

1. El administrador solicita emitir el reporte de vehículos con más fallas.
2. El sistema consulta los vehículos y sus reportes de falla.
3. El sistema cuenta los reportes registrados para cada vehículo.
4. El sistema ordena los vehículos de mayor a menor cantidad de reportes.
5. El sistema genera y muestra el reporte con el código, el tipo de vehículo y la cantidad de reportes de falla.

### Flujos alternativos

- Si no existen reportes de falla, el sistema informa que no hay fallas registradas.

### Reporte de vehículos con más fallas

Consideramos que el reporte utiliza todos los reportes de falla conservados y ordena los vehículos de mayor a menor cantidad de reportes registrados.

## Comprobar vehículos no devueltos

### Actor

Operador del sistema.

### Precondición

El operador está registrado en el sistema.

### Flujo principal

### Flujo principal

1. El operador solicita comprobar los viajes abiertos.
2. El sistema compara el momento actual con el inicio de cada viaje abierto.
3. Para cada viaje que alcanza las 24 horas sin devolución, el sistema registra el vehículo como reportado perdido e impide que se ofrezca para nuevos retiros.
4. Si ese viaje todavía no tiene aplicado el cargo por pérdida,el sistema debita $50.000 de la cuenta del usuario y registra el movimiento con su fecha, importe y viaje asociado.
5. El sistema registra que el cargo por pérdida fue aplicado, para evitar repetirlo en otras comprobaciones.
6. Si se aplicó el cargo y el saldo resulta negativo, el sistema inhabilita al usuario hasta que lo regularice.
7. El sistema muestra los vehículos reportados como perdidos.

### Flujos alternativos

- Si no existen viajes abiertos, el sistema informa que no hay viajes pendientes de devolución.
- Si ningún viaje alcanza las 24 horas, el sistema informa que no se detectaron nuevos vehículos perdidos.
- Si un viaje ya tiene registrado el cargo por pérdida, el sistema no vuelve a aplicarlo.

### Aclaración

Reportar el vehículo como perdido no equivale a registrar una devolución. No se inventan una estación de devolución ni un momento de fin del viaje.
