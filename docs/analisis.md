# Análisis del enunciado

## Resumen del enunciado
Un municipio ha implementado un servicio público de bicicletas comunes, eléctricas y monopatines eléctricos que se puede compartir,
el cual funciona en estaciones fijas. 
El sistema debe permitir registrar usuarios, gestionar su saldo y sus medios de pago, registrar cuando retiran el vehículo y cuando lo devuelven,
guardar los datos del viaje y su recorrido, y calcular su importe según las tarifas y promociones correspondientes.

Por último, debe gestionar el estado de los vehículos, permite registrar reporte de fallas en los vehículos, las órdenes de mantenimiento y la redistribución entre estaciones.
El administrador podrá emitir reportes de viajes por usuario, uso por estación y vehículos con más fallas. 


## Actores

- Usuario
- Operador del sistema
- Técnico de mantenimiento
- Administrador

## Requerimientos funcionales 

## Usuarios y cuentas 

- 1. Registrar usuarios con nombre, id, email y fecha de alta.
- 2. Gestionar la cuenta de cada usuario, su saldo y los medios de pago seleccionados: tarjeta o billetera virtual.
- 3. Registrar recargas y débidos de la cuenta, conservando la fecha y el importe de cada movimiento.
- 4. Permitir que un usuario se suscriba a un plan mensual.
- 5. Inhabilitar al usuario cuando el saldo sea negativo al finalizar un viaje y habilitarlo al regularizarlo.

## Viajes, tarifas y promociones
- 6. Registrar el retiro de un vehículo en una estación y el inicio del viaje, verificando las condiciones del retiro. 
- 7.  Registrar durante el viaje los puntos del recorrido, con sus coordenadas y su hora.
- 8. Registrar la devolución del vehículo en la misma estación o en otra, verificando las condiciones de devolución.
- 9. Conservar los datos del viaje: usuario, vehículo, estaciones de retiro y devolución, inicio, fin, distancia recorrida y estado.
- 10. Calcular el importe del viaje según la tarifa vigente del tipo de vehículo, el tiempo de uso y las promociones aplicables, contemplando los recargos y períodos sin cargo establecidos en las reglas de negocio.
- 11. Permitir cancelar un retiro y determinar si corresponde cobrarlo según el tiempo trascurrido.
- 12. Reportar el vehículo como perdido y aplicar el cargo fijo cuando no se devuelva en el plazo establecido.
- 13. Registrar notas de ajuste realizadas por el operador para corregir viajes cerrados, sin modificar el viaje original.

## Vehículos y mantenimiento
- 14. Gestionar los datos y estados de bicicletas comunes, bicicletas estáticas y monopatines eléctricos.
- 15. Permitir que usuarios y operaciones registren reportes de fallas de un vehículo, con descripción y fecha.
- 16. Detectar cuándo un vehículo debe pasar a mantenimiento obligatorio por la cantidad de reportes de falla.
- 17. Permitir al operador generar órdenes de mantemiento y asignarlas a un técnico.
- 18. Registrar en cada orden las fechas de apertura y cierre, las tareas realizadas y los repuestos utilizados, identificando al técnico y verificando las condiciones de cierre.

## Estaciones y redistribución
- 19. Gestionar los datos de las estaciones: código, dirección, coordenadas y capacidad mázima, y controlar su ocupación.
- 20. Generar alertas de redistribución para el operador según los límites de ocupación establecidos.
- 21. Registrar los traslados de vehículos entre estaciones, indicado origen, destino, vehículos y operador responsable.

## Reportes
- 22. Permitir al administrador emitir reportes de viajes por usuario.
- 23. Permitir al administrador emitir reportes de uso por estación.
- 24. Permitir al administrador emitir reportes de vehículos con más fallas.
