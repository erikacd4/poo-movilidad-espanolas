# Diagramas de Secuencia

En este apartado vamos a desarrollar dos casos del sistema de movilidad compartida
  1. Retirar un vehículo
  2. Devolver un vehículo

En ambos el actor es el usuario, incluye un participante llamado Sistema que recibe y coordina las llamadas que quedará reflejado en el diagrama de clases final. 

## 1. Retirar un vehículo 

### Que muestra

El usuario solicita retirar un vehículo de una estación, el sistema comprueba que el usuario este habilitado y no tenga otro viaje abierto además de que su saldo alcance el minimo (alcanzaSaldoMinimo de Cuenta). Después pregunta al vehículo si se puede retirar. 

Se todo es correcto, se crea el viaje con el usuario, vehículo, la estación, el momento incial y la tarifa vigente. Después de ello, el vehículo pasa a estado EN USO (cambiarEstado), se retira de la estación (retirarVehiculo) y se cacula la ocupación (calcularOcupacion), en el caso de la que la ocupación quede por debajo del 20% o encima del 90% se genera una alerta para el operador. Por último, se informa al usuario del retiro correcto del vehículo. 

### Flujos alternativos o de error 

- Si el usuario esta inhabilitado, tiene otro viaje abierto o no alcanza el saldo minimo, el sistema informa el motivo y no registra el viaje y retiro del vehículo.
- Si el vehículo no se puede retirar, el sistema informa el motivo. Si es eléctrico y su batería es menor al 15% además se cambiará a BATERIA BAJA

### Fragmentos utilizados

- atl 1: el usuario no puede retirar o puede retirar
- alt 2: el vehículo no puede retirarses o el vehiculo puede retirarse
- opt: cambio a BATERIA BAJA si es eléctrico y tiene bateria menor a 15%
- opt: alerta si la ocupacion es menor al 20% o mayor al 90%

### Relación con el diagrama de clases 



## Devolver un vehículo 




