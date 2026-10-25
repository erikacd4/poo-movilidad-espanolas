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
- Clases:
- Ventaja: 




## Grasp 
