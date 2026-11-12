# Contexto del proyecto

## Unidad 3 — Comunicación serial con la VM

- Hito confirmado por el usuario: Windows envió `PING\n` a `127.0.0.1:2323` y recibió `PONG\n` desde el receptor de la VM del profesor. La prueba verificó el enlace serial virtual en ambos sentidos.
- La VM `RASPBIAN-IOT` ejecuta Debian 11 (Bullseye) en `x86_64`. En VirtualBox usa el puerto serie 1 (`COM1`) en modo TCP servidor, con «Conectar a tubería/socket existente» desmarcado y puerto `2323`. Debian detecta la UART 16550A como `/dev/ttyS0`. El receptor de prueba en Python se inició por SSH y usó 9600 baudios, 8N1 y modo raw.
- Alcance acordado: comunicación bidireccional entre Android y la VM, con registro del intercambio en Cloud Firestore. Los sensores y actuadores quedan fuera del alcance.
- Pendiente: repetir el intercambio desde Android y guardar el resultado en Firestore. Para el emulador de Android Studio, el primer destino a probar es `10.0.2.2:2323`. La app ya usa Firebase Authentication.
