## Purpose

Permite solicitar datos ficticios desde Android y verificar el enlace serial bidireccional con la VM del profesor.

## ADDED Requirements

### Requirement: Intercambio serial seguro e íntegro

Android y la VM SHALL enviar y recibir tramas por el enlace serial. Android SHALL validar el tipo, el ID y el valor, y el tramo de red hacia el puente serial SHALL autenticar el servidor y cifrar el tráfico.

#### Scenario: Respuesta válida

- **WHEN** Android solicita un dato y la VM devuelve el valor ficticio con el ID correspondiente
- **THEN** Android acepta el intercambio y conserva el valor recibido

#### Scenario: Respuesta o certificado inválido

- **WHEN** el ID, la trama o el certificado TLS es inválido
- **THEN** Android rechaza el intercambio y muestra el fallo

### Requirement: Reconexión y cola de la VM

La VM SHALL conservar los datos pendientes en una cola persistente y acotada hasta que Android confirme su recepción local. Android SHALL reintentar con espera progresiva y un límite, y SHALL permitir reanudar la prueba después de agotarlo.

#### Scenario: Se corta el enlace antes del acuse

- **WHEN** el enlace se pierde antes del ACK y luego se restablece
- **THEN** la VM reenvía el dato con el mismo ID y Android conserva una sola copia

#### Scenario: Cola llena

- **WHEN** la cola de la VM alcanza su capacidad
- **THEN** la VM informa que no puede aceptar nuevos intercambios y conserva los pendientes
