# Honey para Wear OS

Honey conecta dos cuentas reales para compartir ánimo, mensajes, toques hápticos y ubicación. No incluye usuarios, parejas, códigos, fechas ni coordenadas precargadas.

## Funciones

- Registro e inicio de sesión con correo y contraseña de Firebase Authentication. El registro guarda el nombre que escribe la persona.
- Match de dos cuentas mediante un código aleatorio de un solo uso, válido durante 24 horas. La vinculación se ejecuta en una Cloud Function y actualiza ambas cuentas en una sola transacción.
- Sincronización en tiempo real de perfiles, estados de ánimo, mensajes, toques y ubicaciones con Cloud Firestore.
- Mapa real con MapLibre y datos de OpenStreetMap a través de OpenFreeMap. La aplicación muestra la atribución del mapa.
- Compartición de ubicación voluntaria con un servicio de primer plano de Android y una notificación permanente. El usuario puede detenerla desde Honey.
- Ajustes persistentes en el reloj: español o inglés, modo claro u oscuro, ocho acentos de color, No molestar y vibración.
- Interfaz Wear OS con panel de cuatro accesos, controles de ánimo que caben en pantalla e iconos vectoriales.

## Conectar Firebase

El código Android se compila sin credenciales, pero Honey bloquea el acceso hasta conectar Firebase. Para usar cuentas y datos compartidos:

1. Crea un proyecto en Firebase y activa **Authentication → Email/Password**.
2. Crea la base **Cloud Firestore** en modo producción.
3. Registra las aplicaciones Android `com.honey.wear` y `com.honey.wear.debug`. Descarga el archivo `google-services.json` y guárdalo en `app/` (o configura archivos por variante en `app/src/release/` y `app/src/debug/`).
4. Instala Node.js 22 y Firebase CLI, inicia sesión y selecciona el proyecto:

   ```powershell
   npm install --prefix functions
   firebase login
   firebase use --add
   firebase deploy --only firestore:rules,functions
   ```

5. Compila y ejecuta `app` en Android Studio o genera el APK con `.\gradlew.bat assembleDebug`.

Las Cloud Functions requieren que el proyecto Firebase tenga facturación habilitada (plan Blaze). Firestore tiene cuotas gratuitas; consulta el panel de uso antes de distribuir la app. No despliegues reglas abiertas: `firestore.rules` limita el acceso a cuentas y miembros de cada pareja.

## Privacidad de ubicación

Honey no solicita ubicación al iniciar. Al activar **Compartir mi ubicación**, Android pide permiso y Honey inicia un servicio visible con notificación. La ubicación se envía mientras el uso compartido está activo. Desactívalo desde la pantalla de ubicación para detener el servicio y retirar el último punto compartido.

El mapa se centra en la ubicación más reciente que haya compartido la otra cuenta. La frecuencia de actualización depende del permiso de ubicación, las condiciones del reloj, la conectividad y las restricciones de batería de Wear OS.

## Estructura

- `app/`: aplicación Wear OS, cliente Firebase, interfaz y seguimiento de ubicación.
- `functions/`: generación de códigos y match atómico en Cloud Functions.
- `firestore.rules`: reglas para proteger perfiles, mensajes, ubicaciones y datos de pareja.
- `.firebaserc.example`: plantilla local de selección de proyecto. No contiene un proyecto real.
