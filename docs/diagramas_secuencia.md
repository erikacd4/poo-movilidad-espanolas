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

En ambos intervienen las clases "Cuenta", "Vehículo", "Estación" y "Viaje". 

## Devolver un vehículo 

### Que muestra

Este diagrama de secuencias muestra el flujo de la devolución de un vehículo. 

El usuario solicita devolver el vhíiculo en una estación y el sistema compuebra que tenga la capacidad para ello (tieneCapacidad), si la tiene, finaliza el viaje registrando la estación el que se devuelve y la distancia recorrida (finalizar) y calcula el importe (calcularImporte), que corresponde al caso de uso incluido Calcular importe del viaje. 

Una vez hecho esto, se descuenta el importe de la cuenta (debitar), que registra el movimiento, y el vehículo se agrega a la estación (agregarVehículo), y el viaje queda cerrado. Si aplicó el mes mensual, se descuenta uno de sus viajes (consumirViaje), y si es el primer viaje finalizado y no cancelado se registra que ya se realizó. 

El estado del vehículo cambia según su condición (cambiarEstado) y se calcula la ocupación de la estación (calcularOcupación), con alerta si es menor del 20% o mayor del 90%. Lo último es que se informa la finalización del viaje, importe y saldo resultante. 

### Flujo alternativo 

Si la estación en la que se devuelve el vehículo esta llena, el sistema no completa la devolución ni cierra el viaje. En el primer intento rechazado por este motivo se registra el inicio del periodo adicional de hasta 10 minutos sin cargo, y se informa al usuario de que debe dirigirse a otra estacion y solicitar de nuevo la devolución. 


### Fragmentos utilizados

- alt: la estacion tiene capacidad o está llena
- opt: inhabilitar al usuario si saldo negativo después del cobro
- opt: descontar un viaje si se aplicó el plan mensual
- opt: registrar el primer viaje realizado
- opt: alerta segun la ocupación
- opt: registrar el inicio de periodo adicional solo en el primer intento rechazado.

### Relación con el diagrama de clases

















