# Análisis del enunciado

## Resumen del enunciado
Un municipio ha implementado un servicio público de bicicletas comunes, eléctricas y monopatines eléctricos que se puede compartir,
el cual funciona en estaciones fijas. 
El sistema debe permitir registrar usuarios, gestionar su saldo y sus medios de pago, registrar cuando retiran el vehículo y cuando lo devuelven,
guardar los datos del viaje y su recorrido, y calcular su importe según las tarifas y promociones correspondientes.

Además, el sistema debe gestionar el estado de los vehículos, permitir registrar reportes de fallas, gestionar las órdenes de mantenimiento y registrar la redistribución entre estaciones.
El administrador podrá emitir reportes de viajes por usuario, uso por estación y vehículos con más fallas. 


## Actores

- Usuario
- Operador del sistema
- Técnico de mantenimiento
- Administrador

## Requerimientos funcionales 

### Usuarios y cuentas 

- 1. Registrar usuarios con nombre, documento, email y fecha de alta.
- 2. Gestionar la cuenta de cada usuario, su saldo y los medios de pago seleccionados: tarjeta o billetera virtual.
- 3. Registrar recargas y débitos de la cuenta, conservando la fecha y el importe de cada movimiento.
- 4. Permitir que un usuario se suscriba a un plan mensual.
- 5. Inhabilitar al usuario cuando el saldo sea negativo al finalizar un viaje y habilitarlo al regularizarlo.

### Viajes, tarifas y promociones
- 6. Registrar el retiro de un vehículo en una estación y el inicio del viaje, verificando las condiciones del retiro. 
- 7.  Registrar durante el viaje los puntos del recorrido, con sus coordenadas y su hora.
- 8. Registrar la devolución del vehículo en la misma estación o en otra, verificando las condiciones de devolución.
- 9. Conservar los datos del viaje: usuario, vehículo, estaciones de retiro y devolución, inicio, fin, distancia recorrida y estado.
- 10. Calcular el importe del viaje según la tarifa vigente del tipo de vehículo, el tiempo de uso y las promociones aplicables, contemplando los recargos y períodos sin cargo establecidos en las reglas de negocio.
- 11. Permitir cancelar un retiro y determinar si corresponde cobrarlo según el tiempo transcurrido.
- 12. Reportar el vehículo como perdido y aplicar el cargo fijo cuando no se devuelva en el plazo establecido.
- 13. Registrar notas de ajuste realizadas por el operador para corregir viajes cerrados, sin modificar el viaje original.

### Vehículos y mantenimiento
- 14. Gestionar los datos y estados de bicicletas comunes, bicicletas eléctricas y monopatines eléctricos.
- 15. Permitir que usuarios y operadores registren reportes de fallas de un vehículo, con descripción y fecha.
- 16. Detectar cuándo un vehículo debe pasar a mantenimiento obligatorio por la cantidad de reportes de falla.
- 17. Permitir al operador generar órdenes de mantenimiento y asignarlas a un técnico.
- 18. Registrar en cada orden las fechas de apertura y cierre, las tareas realizadas y los repuestos utilizados, identificando al técnico y verificando las condiciones de cierre.

### Estaciones y redistribución
- 19. Gestionar los datos de las estaciones: código, dirección, coordenadas y capacidad máxima, y controlar su ocupación.
- 20. Generar alertas de redistribución para el operador según los límites de ocupación establecidos.
- 21. Registrar los traslados de vehículos entre estaciones, indicando origen, destino, vehículos y operador responsable.
- 22. Registrar cuando una devolución no puede completarse porque la estación está llena y conceder los 10 minutos adicionales sin cargo establecidos para llegar a otra estación cercana.

### Reportes
- 23. Permitir al administrador emitir reportes de viajes por usuario.
- 24. Permitir al administrador emitir reportes de uso por estación.
- 25. Permitir al administrador emitir reportes de vehículos con más fallas.

## Reglas de negocio

### Usuarios y saldo
- Un usuario solo puede retirar un vehículo si su saldo alcanza el mínimo establecido (por ejemplo, el equivalente a 30 minutos de uso) y si no tiene otro viaje abierto.
- Si al terminar un viaje el saldo queda negativo, el usuario queda inhabilitado hasta regularizarlo.

### Vehículos
- Hay tres tipos: bicicleta común, bicicleta eléctrica y monopatín eléctrico. Los estados son disponible, en uso, en reparación, batería baja y fuera de servicio.
- Solo un vehículo disponible puede retirarse. Los eléctricos con batería menor al 15% pasan a batería baja y no se ofrecen. Las bicicletas comunes no tienen batería.
- Un vehículo con 3 reportes de falla en 30 días pasa a mantenimiento obligatorio.
- Una orden de mantenimiento no se cierra sin registrar el técnico, la fecha y las tareas realizadas.

 ### Estaciones
 - Cada estación tiene una capacidad fija, y no se puede devolver un vehículo en una estación llena ni fuera de una estación.
 - Si el usuario encuentra la estación llena, dispone de 10 minutos adicionales sin cargo para llegar a otra cercana.
 - Una estación con menos del 20% o más del 90% de ocupación genera una alerta de redistribución para el operador.

### Viajes y tarifas
- La tarifa es un cargo de desbloqueo más un precio por minuto según el tipo de vehículo. Valores de referencia: bicicleta común $200 + $20 por minuto, bicicleta eléctrica $300 + $45 por minuto, monopatín $300 + $50 por minuto.
- Un retiro cancelado dentro de los 2 minutos no tiene cargo.
- El tiempo máximo de un viaje es de 2 horas. Pasado ese límite, el minuto se cobra con un recargo del 50%. Si el vehículo no se devuelve en 24 horas, se lo reporta como perdido y se aplica un cargo fijo.
- Promociones: el primer viaje de cada usuario es gratis hasta 15 minutos, y el plan mensual incluye 20 viajes de hasta 30 minutos. No se acumulan: se aplica la más favorable para el usuario.
- Un viaje cerrado no se modifica. Cualquier corrección la hace el operador con una nota de ajuste. 

## Supuestos adoptados
### Registro y cierre de las órdenes de mantenimiento
Consideramos que el técnico asignado a una orden es quien registra las tareas realizadas y los repuestos utilizados, y efectúa su cierre. Para cerrarla, debe quedar registrado el técnico, la fecha de cierre y las tareas realizadas.

### Cobro de viajes con promociones
Consideramos que las promociones del primer viaje y del plan mensual contienen el cargo de desbloqueo y los minutos gratuitos correspondientes: 15 minutos para el primer viaje y 30 minutos para cada uno de los 20 viajes incluidos en el plan mensual. 
Si el viaje supera ese tiempo, se cobran únicamente los minutos excedentes según la tarifa vigente, con los recargos que correspondan.
Las promociones no se acumulan: se aplica la más favorable para el usuario.

### Cobro de fracciones de minuto
Consideramos que las fracciones de minuto se cobran proporcionalmente al tiempo utilizado. Para calcular el importe, expresamos el tiempo cobrable en minutos, incluyendo su parte decimal, y aplicamos el precio por minuto correspondiente. 

### Cancelación de un retiro
Consideramos que, para cancelar un retiro sin cargo, el usuario debe devolver el vehículo a la estación de origen dentro de los 2 minutos desde el inicio del viaje, respetando la capacidad de la estación. 
La cancelación queda registrada. Si se supera ese plazo, la devolución se procesa como la finalización de un viaje y se calcula el importe correspondiente.


### Cargo por vehículo perdido
Consideramos un cargo fijo de $50000 cuando el vehículo no se devuelve en 24 horas. Este importe es un supuesto que hemos sacado como grupo debido a que el enunciado establece el cargo, pero no especifica su valor. 

### Saldo mínimo para retirar un vehículo
Consideramos que el saldo mínimo debe cubrir el cargo de desbloqueo más 30 minutos de uso, según la tarifa vigente del tipo de vehículo que el usuario quiera retirar. 
