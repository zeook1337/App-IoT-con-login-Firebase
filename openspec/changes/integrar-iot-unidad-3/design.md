# Diseño de la integración IoT

## Context

El hito en AGENTS.md confirma PING/PONG desde Windows usando COM1, TCP 2323 y /dev/ttyS0 en la VM Debian. La primera prueba desde el emulador reutilizará el receptor existente en 10.0.2.2:2323.

## Decisions

- Intercambiar tramas JSON delimitadas por salto de línea con tipo, UUID y valor ficticio. Validar esquema, tamaño e ID. En la entrega, Android se conecta por TLS a un puente del anfitrión que reenvía al puerto TCP de VirtualBox, restringido localmente. Validar el certificado del puente.
- La VM guarda cada respuesta en SQLite por ID antes de enviarla y la conserva hasta recibir ACK. Android envía ACK tras persistir el dato en su cola local por usuario. Al reconectar, puede recuperar pendientes del mismo usuario. Definir límites de ambas colas y avisar al llenarse, conservando los datos ya aceptados.
- Android guarda ID, valor y fecha en Firestore bajo `users/{uid}/exchanges/{id}`. La cola de Android conserva el trabajo hasta la confirmación de Firestore y de la publicación MQTT, con reintentos independientes. Un ID estable permite reintentar sin duplicar registros.
- Después de confirmar Firestore, Android publica el ID con QoS 1 en `unidad3/{canal}/datos`. Al iniciar sesión, Android se suscribe al tópico autorizado. Cada aviso dispara una lectura por ID en Firestore y actualiza el historial por ID. Al iniciar o recuperar la suscripción, cargar el historial desde Firestore para recuperar avisos perdidos.
- Usar un broker accesible por Internet con TLS y ACL por canal, con credenciales de prueba configuradas fuera del repositorio. Asignar un canal opaco por cuenta de prueba y validar su correspondencia con la sesión. El mensaje MQTT contiene el ID del registro. Firestore limita lectura y escritura al propietario y valida los campos.

## Risks / Trade-offs

- Los enlaces serial, Firestore y MQTT pueden fallar por separado. Mostrar sus estados y reintentar con espera progresiva. Al cambiar de cuenta, cerrar la suscripción anterior y aislar colas e historial.
- La VM y el móvil pueden quedarse sin capacidad temporal. Registrar el límite, el comportamiento al llenarse y el procedimiento para reanudar las entregas.
- El flujo publica el aviso desde Android después de persistir en Firestore. Si se exige que la VM publique directamente al broker según el paso 5 de la guía, esa variante queda por validar con el docente.

## Migration Plan

Probar primero con el receptor existente. Después configurar el puente TLS, el receptor actualizado, Firestore y el broker. Para revertir, restaurar el APK y receptor previos y retirar el acceso de prueba al broker.
