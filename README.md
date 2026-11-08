# 🍯 Honey — Smartwatch Couples Tracker

> **Aplicación nativa para relojes inteligentes (Wear OS / Android Wear)** diseñada para parejas. Comunicación emocional, discreta, háptica y en tiempo real directamente desde tu muñeca.

---

## 📱 Descripción del Proyecto

**Honey** es una interfaz de usuario optimizada para la ergonomía de pantallas de smartwatch (Samsung Galaxy Watch, Google Pixel Watch, TicWatch y emuladores Wear OS). A diferencia de una aplicación de mensajería tradicional, Honey prioriza interacciones rápidas, toques hápticos expresivos, sincronización de estados emocionales y compartición de ubicación por GPS en tiempo real.

---

## ✨ Funcionalidades Principales

### 1. 🔐 Autenticación Obligatoria (Login y Registro)
* Pantalla de inicio de sesión y registro adaptada a la micro-interfaz de reloj inteligente.
* Entrada ágil de credenciales y PIN numérico de 4 dígitos.
* Acceso directo de prueba para evaluación académica rápida.

### 2. 💑 Configuración Inicial & Emparejamiento (Match)
* Selector de avatar para el perfil del usuario (🌸, 🐻, 🐱, 🦊, 🌙, 🐝).
* **Generación de código de vinculación único** (ejemplo: `HNY-8492`).
* Ingreso de código de la pareja para enlace bidireccional instantáneo.
* **Selector de fecha de inicio de noviazgo** (Día, Mes y Año con controles táctiles Wear OS) para el cálculo automático y continuo de días juntos.

### 3. ⌚ Pantalla Principal (Watchface Dashboard)
* Reloj digital con hora y segundos en vivo.
* **Contador dinámico de días juntos** (ej. `💞 428 días juntos`).
* **Indicador de estado de ánimo mutuo**: Visualización en vivo del emoji de ánimo de tu pareja y acceso directo para actualizar el tuyo.
* Barra de estado con indicador de sincronización activa y modo No Molestar.
* Chips de acceso rápido a las funciones clave.

### 4. 💓 Enviar Cariño (Toque Háptico en 3 Modos)
Cada modo cuenta con una animación visual inmersiva a pantalla completa y un patrón de vibración física diferenciado (`Vibrator` / `VibrationEffect`):
1. **Cariño (Toque suave)**: Simula un pulso cardíaco rítmico con vibración suave doble `[70ms, 110ms]` y ondas de amor concéntricas.
2. **Abrazo (Cálido y continuo)**: Vibración prolongada y profunda `[400ms]` con destello envolvente dorado/naranja a pantalla completa.
3. **Beso (Juguetón)**: Cascada de partículas de labios 💋 y destellos flotantes con vibración rítmica juguetona `[60ms, 60ms, 140ms]`.
* Incluye botones de prueba en pantalla para simular la recepción del toque en el mismo reloj y evaluar la reacción háptica.

### 5. 💬 Mensajes Rápidos Predefinidos
Envío inmediato a un toque de las 6 frases esenciales:
* *"Te quiero"* ❤️
* *"Voy en camino"* 🏃‍♂️
* *"Ya llegué"* 📍
* *"Pensando en ti"* 💭
* *"Llámame"* 📞
* *"Buenas noches"* 🌙
* Feedback háptico de confirmación al enviar.

### 6. 📍 Ubicación GPS en Tiempo Real
* **Acceso nativo al hardware GPS** mediante Google Play Services `FusedLocationProviderClient`.
* Permisos de alta precisión `ACCESS_FINE_LOCATION`.
* Radar animado con barrido continuo de 360°, anillos concéntricos y pines de ubicación.
* Cálculo dinámico de distancia en metros o kilómetros (`"a 180 m de ti"`).
* Interruptor para compartir o pausar la transmisión de ubicación.

### 7. 😊 Gestor de Estado de Ánimo (Mood Tracker)
* Selector táctil de emociones: *Feliz* 😊, *Enamorado/a* 🥰, *Cansado/a* 😴, *Necesito mimos* 🥺, *Estresado/a* 🤯, *Juguetón/a* 😏.
* Actualización inmediata en la carátula principal del reloj de la pareja.

### 8. ⚙️ Pantalla de Ajustes y Personalización
* **Selector de Idioma**: Soporte completo en **Español** e **Inglés** (`res/values/strings.xml` y `res/values-en/strings.xml`).
* **Modo Oscuro OLED & Modo Claro**: Optimización con negros puros `#000000` para ahorro de batería en pantallas AMOLED de relojes.
* **8 Temas de Personalización**:
  * 🍯 Amarillo Miel (*Honey Gold*)
  * 🔷 Azul Zafiro (*Sapphire*)
  * 🌸 Rosado Dulce (*Sweet Pink*)
  * 🟣 Morado Cyber (*Cyber Violet*)
  * 🟢 Verde Esmeralda (*Emerald*)
  * 🪸 Coral Atardecer (*Sunset Coral*)
  * ⚡ Neón Turquesa (*Cyan*)
  * 🔴 Rojo Rubí (*Ruby*)
* **Modo No Molestar (DND)**:
  * Silencia los toques y mensajes entrantes.
  * Los almacena en una cola silenciosa.
  * Al desactivarlo, muestra un diálogo de resumen con lo recibido mientras descansabas.
* **Switch de Vibración del Dispositivo**: Permite activar o desactivar el motor físico de vibración háptica.
* **Cerrar Sesión / Desvincular**.

---

## 🛠️ Arquitectura y Tecnologías

| Componente | Tecnología |
|---|---|
| **Lenguaje** | Kotlin 1.9.24 (JVM 17) |
| **Framework UI** | Jetpack Compose for Wear OS 1.3.1 |
| **Componentes Wearable** | `ScalingLazyColumn`, `Chip`, `CompactChip`, `ToggleChip`, `MaterialTheme` |
| **Localización GPS** | Google Play Services Location (`FusedLocationProviderClient` 21.2.0) |
| **Hápticos** | Android `Vibrator` & `VibrationEffect` con waveforms personalizados |
| **Build System** | Gradle 8.7 & Android Gradle Plugin 8.4.1 |
| **CI / CD** | GitHub Actions (`build-apk.yml`) para compilación automatizada del APK |

---

## 📦 Cómo obtener y compilar el archivo `.apk`

### Opción 1: Descarga directa desde GitHub Actions (Recomendada)
Gracias al flujo de integración continua incluido (`.github/workflows/build-apk.yml`), GitHub compila automáticamente el archivo instalable en cada cambio utilizando el toolcache optimizado y CDN directo de Gradle:
1. Dirígete a la pestaña **Actions** en tu repositorio de GitHub: `https://github.com/Abejandr0/honey/actions`.
2. Selecciona la ejecución más reciente del flujo **"Build Honey Wear OS APK"**.
3. En la sección **Artifacts**, descarga el archivo **`Honey-WearOS-v1.0-debug`**.
4. ¡Listo! Ya tienes el archivo `.apk` para instalar en cualquier reloj inteligente o emulador.

### Opción 2: Compilación local con Gradle
Si tienes el Android SDK instalado en tu máquina o Android Studio:
```bash
# Otorgar permisos de ejecución
chmod +x gradlew

# Compilar el APK en modo Debug
./gradlew assembleDebug

# El archivo se genera en:
# app/build/outputs/apk/debug/app-debug.apk
```

### Opción 3: Abrir en Android Studio / IntelliJ IDEA
1. Abre **Android Studio** o **IntelliJ IDEA**.
2. Selecciona **Open** y elige la carpeta de este repositorio.
3. Espera la sincronización automática de Gradle.
4. Selecciona un dispositivo Wear OS (físico o emulador circular Wear OS) y presiona **Run ▶**.

### Instalación en un Smartwatch mediante ADB
Para instalar el `.apk` compilado en un reloj Wear OS por Wi-Fi o USB:
```bash
adb connect <IP_DEL_RELOJ>:5555
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

---

## 📂 Estructura del Repositorio

```text
honey/
├── .github/
│   └── workflows/
│       └── build-apk.yml         # Compilación automática del APK en la nube
├── app/
│   ├── src/main/
│   │   ├── AndroidManifest.xml   # Permisos de GPS, Vibración y flag de reloj standalone
│   │   ├── java/com/honey/wear/
│   │   │   ├── MainActivity.kt   # Orquestador principal y GPS FusedLocation
│   │   │   ├── data/
│   │   │   │   ├── HoneyModels.kt     # Modelos de datos (Usuario, Pareja, Temas)
│   │   │   │   └── HoneyRepository.kt # Lógica de negocio, hápticos y estado
│   │   │   ├── ui/
│   │   │   │   ├── components/
│   │   │   │   │   └── TouchReactionOverlay.kt # Animación a pantalla completa
│   │   │   │   ├── screens/
│   │   │   │   │   ├── AuthScreen.kt          # Login y Registro obligatorio
│   │   │   │   │   ├── SetupScreen.kt         # Match y fecha de aniversario
│   │   │   │   │   ├── HomeScreen.kt          # Watchface y accesos
│   │   │   │   │   ├── TouchScreen.kt         # Cariño, Abrazo y Beso
│   │   │   │   │   ├── QuickMessagesScreen.kt # 6 mensajes predefinidos
│   │   │   │   │   ├── LocationScreen.kt      # Radar y GPS en tiempo real
│   │   │   │   │   ├── MoodScreen.kt          # Selector de estados de ánimo
│   │   │   │   │   └── SettingsScreen.kt      # Temas, DND, Vibración, Idioma
│   │   │   │   └── theme/
│   │   │   │       ├── Color.kt               # 8 paletas de color y OLED Dark
│   │   │   │       └── Theme.kt               # Wear Compose Material Theme
│   │   └── res/
│   │       ├── values/strings.xml             # Recursos en Español
│   │       └── values-en/strings.xml          # Recursos en Inglés
│   └── build.gradle.kts          # Dependencias Wear OS, GPS y Compose
├── demo/                         # Demo previa preservada intacta
│   ├── app.js
│   ├── index.html
│   └── styles.css
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── gradlew
└── README.md
```

---

## 👩‍❤️‍👨 Desarrollado para
* **Materia**: Interfaces de Usuario (Sexto Semestre)
* **Universidad**: Universidad de Nariño
* **Proyecto**: Honey — Wearable Couple Interface
