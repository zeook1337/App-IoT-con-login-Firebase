# Tareas

## 1. Enlace serial y continuidad

- [ ] 1.1 Reutilizar el receptor de la VM y comprobar PONG desde Android en 10.0.2.2:2323. Registrar envío y recepción en ambos extremos (3.1.2.5–6 y 3.1.3.8–9).
- [ ] 1.2 Añadir el valor ficticio, ID, validación de tramas y puente TLS. Probar una respuesta válida y rechazar una trama inválida, un ID incorrecto y un certificado no confiable (3.1.1.1–2 y 3.1.4.11).
- [ ] 1.3 Añadir colas persistentes acotadas en VM y Android, ACK y reintentos. Probar corte antes del ACK, reinicio, cola llena y reanudación sin duplicados. Documentar la capacidad de ambas colas y el plan de contingencia (3.1.1.3–4 y 3.1.4.13).

## 2. Firestore y suscripción MQTT

- [ ] 2.1 Aplicar las reglas de Firestore y sincronizar por ID. Cortar Internet con el enlace local activo y verificar que el dato pendiente llega una sola vez al volver la conexión (3.1.2.7).
- [ ] 2.2 Configurar el broker MQTT con TLS y ACL. Verificar con dos cuentas que los documentos y tópicos ajenos están restringidos y que los mensajes y registros de diagnóstico omiten datos personales y credenciales (3.1.4.12).
- [ ] 2.3 Publicar el ID tras la confirmación de Firestore y actualizar el historial mediante la suscripción. Mostrar el mismo dato en VM, Firestore y Android. Probar corte del broker, reconexión, aviso duplicado y cambio de cuenta (3.1.1.2 y 3.1.4.13).

## 3. Evaluación y entrega

- [ ] 3.1 Comparar el consumo estimado de Android con reintentos frecuentes y con espera progresiva, durante el mismo tiempo y bajo las mismas condiciones. Registrar método, resultados y ajuste elegido (3.1.3.10).
- [ ] 3.2 Documentar las pruebas de cifrado, integridad, control de acceso y protección de datos con referencia a la familia ISO/IEC 27000. Verificar cada control en el entorno de demostración (3.1.4.11–12).
- [ ] 3.3 Grabar el intercambio, el dato guardado y su aparición en Android por tópico. Mostrar recuperación ante cortes y registrar la retroalimentación y el ajuste final (3.1.4.14).
