# Acceso IoT

Aplicación Android nativa en Kotlin y Jetpack Compose para la asignatura Aplicaciones Móviles para IoT. Usa Firebase Authentication para iniciar sesión con correo y contraseña o con Google. Después del acceso muestra **Página en desarrollo**, con un botón **Cerrar sesión** arriba a la izquierda.

## Abrir el proyecto

1. Abre esta carpeta en Android Studio y espera la sincronización de Gradle.
2. En Settings > Build, Execution, Deployment > Build Tools > Gradle selecciona un **JDK 17 o 21** como Gradle JDK. Gradle 8.13 no es compatible con JDK 25.
3. Usa un dispositivo o emulador Android 6.0 (API 23) o superior. Para Google necesita Google Play Services y una cuenta Google.
4. Ejecuta la configuración `app`.

Paquete Android: `cl.iot.tracker`. Proyecto Firebase: `app-android-iot-a4212`. Número: `1034577935400`.

Si clonas el repositorio, descarga `google-services.json` desde tu proyecto Firebase y colócalo en `app/` antes de sincronizar. El archivo de configuración, las contraseñas y los certificados privados no se incluyen en Git.

## Configurar Firebase Authentication

Correo electrónico/contraseña y Google quedaron habilitados en la consola del proyecto. Los pasos siguientes sirven para revisar o reproducir esa configuración.

La configuración recibida se copió a `app/google-services.json`, que es el archivo utilizado al compilar. El archivo original en la raíz no se usa en la compilación. No cambies el paquete de Android sin registrar otra aplicación en Firebase.

1. Entra a [Firebase Console](https://console.firebase.google.com/project/app-android-iot-a4212/authentication/providers).
2. Abre Authentication, pulsa Comenzar si corresponde y ve a Sign-in method o Método de acceso.
3. Habilita **Correo electrónico/contraseña**. No es necesario habilitar el enlace por correo sin contraseña.
4. Puedes crear usuarios desde la propia aplicación con **Regístrate**, o desde Authentication > Users > Agregar usuario.
5. Inicia sesión con un usuario real. La contraseña no se guarda en archivos ni se escribe en registros. Firebase mantiene la sesión entre aperturas.

No se necesita Firestore, Realtime Database, servidor propio ni reglas de base de datos para esta aplicación.

## Habilitar Google

El código de Google está implementado con Credential Manager. Se habilitó el proveedor con el correo de asistencia confirmado y se registraron las huellas SHA-1 y SHA-256 de depuración. El archivo actualizado ya está incorporado en `app/google-services.json`, con los clientes OAuth de Android y web; se verificó que el cliente Android coincide con el certificado de depuración de este equipo. Para reproducir la configuración o usar otro certificado:

1. Ejecuta `./gradlew.bat :app:signingReport` para obtener SHA-1 y SHA-256 de la variante debug. Configura antes `JAVA_HOME` a un JDK 17 o 21.
2. En Firebase > Configuración del proyecto > General > Tus apps > `cl.iot.tracker`, agrega las huellas de depuración.
3. En Authentication > Sign-in method habilita **Google**, selecciona el correo de soporte y guarda.
4. Descarga de nuevo **google-services.json** y reemplaza **app/google-services.json**. Debe incluir un cliente OAuth de tipo 3 (web). El plugin genera `default_web_client_id` automáticamente.
5. Sincroniza y recompila; prueba Continuar con Google en un dispositivo con Google Play Services.

Si distribuyes una versión firmada de producción, registra también las huellas del certificado de esa versión y, si corresponde, las de Play App Signing.

Referencias: [Firebase con correo y contraseña](https://firebase.google.com/docs/auth/android/password-auth) y [Firebase con Google y Credential Manager](https://firebase.google.com/docs/auth/android/google-signin).

## Funcionamiento

- Una pantalla de autenticación con modos Iniciar sesión y Crear cuenta.
- Validación de correo, campos vacíos, contraseña de registro y confirmación.
- Botón Mostrar/Ocultar contraseña, recuperación de contraseña y mensajes en español.
- Los controles se bloquean durante una solicitud para evitar envíos repetidos.
- La pantalla autenticada contiene el texto solicitado y un botón Cerrar sesión arriba a la izquierda.
- Cerrar sesión termina la sesión de Firebase, limpia el estado de Credential Manager y devuelve al formulario. No elimina la cuenta ni la cuenta Google del dispositivo.

## Compilar y verificar en Windows

```powershell
$env:JAVA_HOME = 'C:\Users\zeokx\.jdks\jbr-21.0.11'
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest :app:lintDebug
```

Si usas otro equipo, ajusta JAVA_HOME y deja que Android Studio genere `local.properties` con la ruta de tu SDK. El APK se genera en `app/build/outputs/apk/debug/app-debug.apk`.

## Release 1.0.0

Repositorio privado: [App IoT con login Firebase](https://github.com/zeook1337/App-IoT-con-login-Firebase).

La [release v1.0.0](https://github.com/zeook1337/App-IoT-con-login-Firebase/releases/tag/v1.0.0) incluye el APK instalable, el Android App Bundle (AAB) y sus sumas SHA-256. La versión release está firmada con un certificado propio y no permite depuración.

Las huellas del certificado release ya están registradas en la aplicación `cl.iot.tracker` de Firebase para el acceso con Google:

```text
SHA-1:   2A:95:8B:65:44:EC:CE:13:5F:C2:D1:1F:56:A0:F9:2B:72:89:69:94
SHA-256: F1:E4:FD:08:1B:64:5F:CC:5F:31:33:18:3E:E9:B4:E0:39:9C:74:CE:DC:FC:5F:97:CA:3B:F8:3D:13:E9:29:D2
```

Para volver a compilar una release firmada en este equipo, se usa `signing.properties` en la raíz y la clave local `%USERPROFILE%/.android/keystores/app-iot-firebase-release.jks`. Conserva ambos archivos en un respaldo privado para poder firmar futuras actualizaciones. Están excluidos del repositorio. `signing.properties.example` muestra el formato sin contraseñas reales.

```powershell
.\gradlew.bat :app:assembleRelease :app:bundleRelease :app:testReleaseUnitTest :app:lintRelease
```

Sin `signing.properties`, Gradle no firma la variante release. El APK firmado se genera en `app/build/outputs/apk/release/app-release.apk` y el AAB en `app/build/outputs/bundle/release/app-release.aab`.

La firma release es distinta de la firma debug. Para instalarla en un dispositivo que tiene la versión debug, desinstala primero la anterior; se eliminará la sesión local y tendrás que volver a iniciar sesión. Las cuentas en Firebase se conservan.

## Prueba manual de aceptación

1. Activa los proveedores en Firebase antes de probar.
2. Introduce un correo inválido o deja campos vacíos: debe mostrarse un mensaje sin autenticar.
3. Registra una cuenta con confirmación distinta: debe rechazarla. Corrige y registra: debe abrir Página en desarrollo y aparecer un usuario en Firebase.
4. Cierra y vuelve a abrir la app: debe conservar la sesión.
5. Pulsa Cerrar sesión arriba a la izquierda: debe volver al formulario. Cierra y abre la app para comprobar que no regresa a la pantalla autenticada; inicia sesión con la cuenta creada. Una contraseña incorrecta debe mostrar un error y mantener el formulario.
6. En el formulario introduce el correo y pulsa ¿Olvidaste tu contraseña?: comprueba el correo recibido.
7. Tras configurar OAuth, prueba Google; cancelar el selector debe devolver al formulario sin un error de acceso. Después de acceder, cierra sesión y comprueba que puedes volver a elegir una cuenta.
8. Desconecta Internet e intenta acceder: debe indicar un problema de conexión sin entrar.

## Alcance de la entrega

Se implementó el alcance solicitado: autenticación Firebase y pantalla de desarrollo. La guía de Unidad 3 también describe comunicación con microcontrolador, sensores y actuadores; estas funciones no forman parte de esta aplicación. La consulta al docente se omitió según tu indicación. Esta app por sí sola no implementa todos los requisitos de integración IoT de la guía.
