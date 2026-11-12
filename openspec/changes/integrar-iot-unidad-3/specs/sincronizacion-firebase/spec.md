## Purpose

Guarda los datos ficticios en Firestore y actualiza el historial de Android mediante una suscripción a un tópico MQTT autorizado.

## ADDED Requirements

### Requirement: Persistencia y sincronización por usuario

Android SHALL conservar los datos pendientes en una cola local persistente y acotada por usuario y sincronizarlos automáticamente con Firestore. El documento SHALL contener el ID, la fecha y el valor. Firestore SHALL restringir el acceso al propietario.

#### Scenario: Corte de Internet con enlace serial disponible

- **WHEN** Android recibe un dato sin Internet y luego recupera la conexión
- **THEN** Firestore confirma un único documento con el valor recibido

#### Scenario: Acceso ajeno

- **WHEN** otra cuenta intenta leer o escribir el documento
- **THEN** Firestore rechaza la operación

#### Scenario: Cola móvil llena

- **WHEN** Android carece de espacio en su cola de pendientes
- **THEN** muestra el límite alcanzado y la VM conserva el dato para entregarlo después

### Requirement: Visualización por suscripción MQTT

Android SHALL suscribirse al tópico autorizado de la sesión. Tras confirmar la escritura en Firestore, SHALL publicar el ID en el tópico. Al recibir el aviso, SHALL consultar el documento correspondiente y mostrar el dato guardado en el historial. El broker SHALL restringir el acceso al canal autorizado y exigir TLS.

#### Scenario: Visualización de un dato guardado

- **WHEN** Android recibe el aviso MQTT de un intercambio confirmado
- **THEN** el historial muestra el ID y el mismo valor que figura en Firestore

#### Scenario: Aviso repetido o conexión recuperada

- **WHEN** llega un aviso repetido o Android se vuelve a suscribir tras un corte
- **THEN** el historial recupera los registros guardados y muestra una fila por ID

#### Scenario: Cuenta sin permiso para el tópico

- **WHEN** una cuenta intenta suscribirse a un canal ajeno
- **THEN** el broker rechaza la suscripción
