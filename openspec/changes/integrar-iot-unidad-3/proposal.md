# Integración IoT de la Unidad 3

## Why

El intercambio PING/PONG entre Windows y la VM del profesor ya fue verificado. La entrega llevará ese enlace a Android para solicitar datos ficticios, guardarlos en Firestore y mostrarlos mediante una suscripción MQTT.

## What Changes

- Añadir una prueba serial desde la pantalla autenticada y un historial que se actualice al recibir mensajes del tópico MQTT.
- Generar valores ficticios en la VM y conservar temporalmente las entregas pendientes en ambos extremos.
- Guardar los datos confirmados en Firestore y publicar el aviso MQTT después de confirmar la escritura.
- Verificar seguridad, integridad, recuperación, consumo de Android y evidencia en video según la rúbrica.

El alcance usa datos ficticios. Se excluyen sensores, actuadores y mediciones físicas de sensores.

## Capabilities

### New Capabilities

- `comunicacion-iot` — Intercambio serial bidireccional, integridad y recuperación.
- `sincronizacion-firebase` — Almacenamiento, sincronización y visualización por suscripción MQTT.

### Modified Capabilities

## Impact

Pantalla autenticada en Compose, receptor Python de la VM, Firestore, reglas de acceso y broker MQTT accesible por Internet con autenticación y TLS.
