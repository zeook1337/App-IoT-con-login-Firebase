# Acceso IoT 1.0.0

Primera versión release de la aplicación Android conectada a Firebase Authentication.

- Inicio de sesión con correo y contraseña y con Google.
- Registro de usuarios y recuperación de contraseña.
- Pantalla Página en desarrollo con Cerrar sesión arriba a la izquierda.
- Sesión persistente y comunicación HTTPS; tráfico HTTP sin cifrar deshabilitado.
- APK y AAB firmados con certificado release propio, con depuración deshabilitada.

## Archivos

- `Acceso-IoT-1.0.0.apk`: instalación directa en Android 6.0 o superior.
- `Acceso-IoT-1.0.0.aab`: Android App Bundle para distribución mediante Google Play; no se instala directamente.
- `SHA256SUMS.txt`: hashes de integridad de ambos archivos.

## Validación

Compilación de APK y AAB correcta, firmas verificadas, 4 pruebas unitarias aprobadas y lint sin errores. La autenticación se había comprobado en la variante debug; falta una prueba manual de acceso en la release firmada.

Las huellas SHA-1 y SHA-256 del certificado release están registradas en Firebase para `cl.iot.tracker`.

## Instalación

Si ya tienes la versión debug, desinstálala antes de instalar esta release porque usan certificados diferentes. Esto borra la sesión local; las cuentas de Firebase se conservan.

Para Google, el dispositivo necesita Google Play Services y una cuenta de Google. Si se distribuye posteriormente mediante Play App Signing, registra también en Firebase las huellas del certificado de firma de Google Play.
