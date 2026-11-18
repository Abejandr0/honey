# Honey para Wear OS

Honey conecta dos relojes para compartir ánimo, mensajes, toques hápticos y ubicación. No incluye usuarios, parejas, códigos, fechas ni coordenadas precargadas.

## Funciones

- Identidad anónima de Firebase Authentication creada automáticamente. Solo se pide el nombre que verá la pareja.
- Vinculación mediante un código aleatorio de un solo uso, válido durante 24 horas. Firestore crea la pareja y vincula al segundo reloj en una transacción; el reloj que compartió el código confirma el vínculo al recibirlo en tiempo real.
- Sincronización en tiempo real de perfiles, estados de ánimo, mensajes, toques y ubicaciones con Cloud Firestore.
- Mapa real con MapLibre y datos de OpenStreetMap a través de OpenFreeMap. La aplicación muestra la atribución del mapa.
- Compartición de ubicación voluntaria con un servicio de primer plano de Android y una notificación permanente. El usuario puede detenerla desde Honey.
- Ajustes persistentes en el reloj: español o inglés, modo claro u oscuro, ocho acentos de color, No molestar y vibración.
- Interfaz Wear OS con panel de cuatro accesos, controles de ánimo que caben en pantalla e iconos vectoriales.

## Vista previa en Android Studio

La compilación `debug` abre el panel directamente cuando Firebase aún no está conectado. Si Firebase está conectado pero no hay pareja vinculada, **Explorar sin vincular** permite entrar desde la pantalla de vinculación. La vista previa indica que no hay pareja real: no inventa mensajes, fechas ni coordenadas, y no envía toques, mensajes ni ubicación. La compilación `release` conserva el flujo real de Firebase y vinculación.

## Conectar Firebase

El código Android se compila sin credenciales, pero Honey espera la conexión a Firebase para usar datos compartidos. El plan Spark no requiere tarjeta ni Cloud Functions:

1. Crea un proyecto en Firebase con el plan **Spark** y activa **Authentication → Sign-in method → Anonymous**.
2. Crea la base **Cloud Firestore** en modo producción.
3. Registra las aplicaciones Android `com.honey.wear` y `com.honey.wear.debug`. Descarga el archivo `google-services.json` y guárdalo en `app/` (o configura archivos por variante en `app/src/release/` y `app/src/debug/`).
4. Instala Firebase CLI, inicia sesión y selecciona el proyecto. Publica las reglas de Firestore:

   ```powershell
   firebase login
   firebase use --add
   firebase deploy --only firestore:rules
   ```

5. Compila y ejecuta `app` en Android Studio o genera el APK con `.\gradlew.bat assembleDebug`.

El plan Spark tiene cuotas de Firestore. Consulta el panel de uso antes de distribuir la app. `firestore.rules` limita los perfiles y los datos compartidos a los miembros de cada pareja; publica estas reglas antes de probar el match.

La identidad anónima permanece en el reloj mientras se conserven los datos de la aplicación. Si se desinstala Honey o se borran sus datos, no hay forma de recuperar esa identidad ni la pareja vinculada. Antes de distribuir la app, conviene añadir un método de recuperación de cuenta.

## Privacidad de ubicación

Honey no solicita ubicación al iniciar. Al activar **Compartir mi ubicación**, Android pide permiso y Honey inicia un servicio visible con notificación. La ubicación se envía mientras el uso compartido está activo. Desactívalo desde la pantalla de ubicación para detener el servicio y retirar el último punto compartido.

El mapa se centra en la ubicación más reciente que haya compartido la otra cuenta. La frecuencia de actualización depende del permiso de ubicación, las condiciones del reloj, la conectividad y las restricciones de batería de Wear OS.

## Estructura

- `app/`: aplicación Wear OS, cliente Firebase, interfaz y seguimiento de ubicación.
- `firestore.rules`: permisos para la creación de códigos y el match sin Cloud Functions.
- `.firebaserc.example`: plantilla local de selección de proyecto. No contiene un proyecto real.
