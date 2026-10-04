# 🐾 DogFellas

<p align="center">
  <strong>Conectando personas para ayudar, atender y encontrar un hogar para animales callejeros.</strong>
</p>

<p align="center">
  Aplicación móvil Android enfocada en el reporte, rescate, atención médica, seguimiento y adopción de animales en situación de calle.
</p>

---

## 📱 Sobre el proyecto

**DogFellas** es una aplicación móvil desarrollada para apoyar la atención de animales callejeros y facilitar la participación de ciudadanos y voluntarios.

La aplicación permite registrar animales encontrados, consultar reportes cercanos, realizar seguimiento de su estado y gestionar información relacionada con su atención médica y proceso de recuperación.

El objetivo es conectar diferentes etapas del proceso en una sola aplicación:

```text
🐕 Reportar
      ↓
📍 Localizar
      ↓
🤝 Ayudar
      ↓
🏥 Atender
      ↓
❤️ Recuperar
      ↓
🏠 Adoptar
```

---

## 🎯 Objetivo

Crear una herramienta tecnológica que contribuya a la atención de animales callejeros mediante:

- 📍 Geolocalización de animales reportados.
- 🐕 Registro de animales encontrados.
- 🚨 Identificación de casos urgentes.
- 🏥 Seguimiento de atención médica.
- 🤝 Participación de voluntarios.
- 🔄 Seguimiento del proceso de recuperación.
- 🏠 Difusión de animales disponibles para adopción.
- 🔔 Notificaciones sobre eventos importantes.

---

# ✨ Funcionalidades

## 🔐 Autenticación

Los usuarios pueden:

- Crear una cuenta.
- Iniciar sesión.
- Recuperar su contraseña.
- Mantener una sesión autenticada.

**Tecnología:** Firebase Authentication.

---

## 🐕 Reporte de animales

Los usuarios pueden registrar animales encontrados en situación de calle.

Cada reporte puede contener:

- Especie.
- Condición.
- Nivel de urgencia.
- Descripción.
- Fotografía.
- Ubicación.
- Fecha del reporte.
- Usuario que realizó el reporte.
- Estado actual.

---

## 📍 Animales cercanos

La aplicación permite consultar animales reportados de acuerdo con la ubicación del usuario.

Incluye:

- Animales cercanos.
- Filtros por especie.
- Filtro de urgencia.
- Ordenamiento por cercanía.
- Ordenamiento por urgencia.
- Ordenamiento por fecha.

---

## 🗺️ Geolocalización y mapas

DogFellas utiliza servicios de ubicación y mapas para facilitar la localización de animales y la coordinación de actividades de ayuda.

Se contempla:

- Ubicación actual del usuario.
- Ubicación de animales reportados.
- Visualización mediante mapas.
- Cálculo de cercanía.
- Navegación hacia ubicaciones.
- Seguimiento de ubicaciones en tiempo real.

---

# 🏥 Atención médica

Cada animal reportado puede tener información médica asociada.

El sistema contempla:

| Módulo | Descripción |
|---|---|
| 📋 Historial médico | Registro de evaluaciones y tratamientos |
| 💊 Medicamentos | Medicamentos, dosis y frecuencia |
| 💉 Vacunaciones | Vacunas y próximas dosis |
| 🪱 Desparasitaciones | Registro y seguimiento |
| 📅 Citas veterinarias | Programación y seguimiento |
| 🚨 Emergencias | Registro de atenciones de emergencia |

La información médica está relacionada directamente con el animal reportado.

---

# 🔄 Seguimiento del animal

DogFellas maneja un ciclo de vida para representar el proceso de atención:

```text
REPORTADO
    │
    ▼
EN_ATENCION
    │
    ▼
RESCATADO
    │
    ▼
EN_RECUPERACION
    │
    ▼
EN_ADOPCION
    │
    ▼
ADOPTADO
```

Esto permite conocer el estado actual del animal y facilitar su seguimiento.

---

# 🤝 Voluntariado

DogFellas busca facilitar la participación de personas que quieran ayudar.

Entre las actividades contempladas se encuentran:

- 🐕 Apoyo en rescates.
- 🚗 Transporte de animales.
- 🏥 Apoyo en atención médica.
- 🏠 Apoyo durante la recuperación.
- 📢 Difusión de animales.
- ❤️ Apoyo en procesos de adopción.

La integración de ubicación en tiempo real permitirá mejorar la coordinación entre voluntarios.

---

# 🏠 Adopciones

Los animales que hayan completado su proceso de recuperación podrán pasar al estado:

```text
EN_ADOPCION
```

Desde este módulo se busca facilitar:

- Consulta de animales disponibles.
- Visualización de información.
- Difusión de casos.
- Contacto relacionado con procesos de adopción.

---

# 🔔 Notificaciones

Se contempla utilizar **Firebase Cloud Messaging (FCM)** para informar a los usuarios sobre eventos importantes.

Ejemplos:

- 🚨 Nuevos reportes cercanos.
- 🔄 Cambios en el estado de un animal.
- 🏥 Actualizaciones médicas.
- 📅 Recordatorios de citas.
- 🏠 Cambios relacionados con adopciones.
- 🤝 Actividades de voluntariado.

---

# 🔥 Arquitectura de datos

DogFellas utiliza diferentes servicios dependiendo del tipo de información.

### Firebase Authentication

Gestiona la autenticación de los usuarios.

### Firebase Firestore

Se utiliza para información persistente y estructurada:

```text
animales_reportados
        │
        ├── historial_medico
        ├── medicamentos
        ├── vacunaciones
        ├── desparasitaciones
        ├── citas_veterinarias
        └── emergencias_atencion
```

### Firebase Realtime Database

Se utilizará para información que necesita actualizarse constantemente, principalmente ubicaciones:

```text
ubicaciones/
│
├── usuarios/
├── voluntarios/
└── animales/
```

Esto permitirá recibir actualizaciones sin necesidad de recargar manualmente la aplicación.

### Firebase Cloud Messaging

Se utilizará para el sistema de notificaciones.

---

# 🏗️ Arquitectura de la aplicación

El proyecto utiliza una separación por responsabilidades:

```text
┌─────────────────────────────┐
│             UI              │
│       Jetpack Compose       │
└──────────────┬──────────────┘
               │
               ▼
┌─────────────────────────────┐
│         ViewModels          │
│      Estado y lógica UI     │
└──────────────┬──────────────┘
               │
               ▼
┌─────────────────────────────┐
│        Repositories         │
│      Acceso a servicios     │
└──────────────┬──────────────┘
               │
       ┌───────┼────────┐
       ▼       ▼        ▼
   Firebase   Maps     GPS
```

---

# 🛠️ Tecnologías

| Tecnología | Versión |
|---|---:|
| Kotlin | `2.1.20` |
| Jetpack Compose | `1.8.1` |
| Navigation Compose | `2.8.5` |
| Firebase Authentication | `22.1.1` |
| Firebase Realtime Database | `21.0.0` |
| Firebase Cloud Messaging | `24.1.1` |
| Google Maps SDK | `19.0.0` |
| JUnit | `5.10.2` |
| Espresso | `3.5.1` |

---

# 🎨 Diseño

La interfaz de DogFellas utiliza una identidad visual inspirada en el cuidado y bienestar animal.

### Paleta principal

- 🤎 Marrón oscuro
- 🤎 Marrón
- 🟤 Marrón claro
- 🟨 Beige
- 🟫 Crema

La aplicación utiliza:

- Jetpack Compose.
- Material 3.
- Tarjetas.
- Indicadores de estado.
- Botones de acción.
- Componentes adaptables.

El diseño está pensado para funcionar en diferentes tamaños de pantalla, incluyendo dispositivos pequeños.

---

# 📂 Estructura del proyecto

```text
DogFellas/
│
├── app/
│   └── src/
│       └── main/
│           ├── java/
│           │   └── com/dogfellas/app/
│           │       ├── model/
│           │       ├── repository/
│           │       ├── ui/
│           │       │   ├── screens/
│           │       │   └── viewmodel/
│           │       └── navigation/
│           │
│           └── res/
│
├── build.gradle
├── settings.gradle
├── gradle.properties
└── README.md
```

---

# 🧪 Pruebas

## JUnit

Se utilizará para pruebas unitarias de:

- ViewModels.
- Repositories.
- Validaciones.
- Lógica de negocio.
- Funciones auxiliares.

## Espresso

Se utilizará para pruebas de interacción de la aplicación:

- Inicio de sesión.
- Navegación.
- Formularios.
- Reporte de animales.
- Consulta de animales.
- Flujos principales.

---

# 📊 Estado del proyecto

## 🟢 Implementado

- [x] Registro de usuarios.
- [x] Inicio de sesión.
- [x] Recuperación de contraseña.
- [x] Reporte de animales.
- [x] Registro de ubicación.
- [x] Consulta de animales cercanos.
- [x] Filtros de animales.
- [x] Ordenamiento de reportes.
- [x] Detalle del animal.
- [x] Seguimiento del estado.
- [x] Atención médica individual.
- [x] Historial médico.
- [x] Medicamentos.
- [x] Vacunaciones.
- [x] Desparasitaciones.
- [x] Citas veterinarias.
- [x] Registro de emergencias.

## 🟡 En desarrollo

- [ ] Diseño responsive completo.
- [ ] Adopciones.
- [ ] Voluntariado.
- [ ] Ubicación en tiempo real.
- [ ] Firebase Realtime Database.
- [ ] Notificaciones con FCM.
- [ ] Integración completa de Google Maps.
- [ ] Mejoras de experiencia de usuario.

## 🔴 Pendiente

- [ ] Pruebas unitarias con JUnit.
- [ ] Pruebas de interfaz con Espresso.
- [ ] Pruebas de integración.
- [ ] Pruebas en diferentes tamaños de dispositivos.
- [ ] Revisión final de rendimiento.
- [ ] Preparación de versión final.

---

# 🚀 Roadmap

### Fase 1 — Base de la aplicación

- [x] Autenticación.
- [x] Reporte de animales.
- [x] Firebase.
- [x] Geolocalización.

### Fase 2 — Atención

- [x] Detalle del animal.
- [x] Seguimiento.
- [x] Atención médica.
- [x] Historial médico.

### Fase 3 — Ayuda y recuperación

- [ ] Voluntariado.
- [ ] Ubicación en tiempo real.
- [ ] Notificaciones.
- [ ] Coordinación de rescates.

### Fase 4 — Adopción

- [ ] Animales disponibles.
- [ ] Difusión.
- [ ] Flujo de adopción.

### Fase 5 — Calidad

- [ ] JUnit.
- [ ] Espresso.
- [ ] Pruebas de integración.
- [ ] Optimización.
- [ ] Versión final.

---

# ⚙️ Requisitos

Para ejecutar el proyecto se recomienda contar con:

- Android Studio.
- JDK 17.
- Android SDK.
- Un dispositivo Android físico o emulador.
- Proyecto configurado en Firebase.
- Configuración de Google Maps.
- Conexión a Internet.

---

# 🔧 Configuración

## Firebase

Agregar el archivo:

```text
google-services.json
```

dentro de:

```text
app/
```

## Google Maps

Configurar la API Key de Google Maps utilizando el mecanismo de configuración definido por el proyecto.

> ⚠️ No publicar API Keys, contraseñas, tokens ni credenciales privadas en el repositorio.

---

# ▶️ Ejecución

Clonar el repositorio:

```bash
git clone URL_DEL_REPOSITORIO
```

Entrar al proyecto:

```bash
cd DogFellas
```

Dar permisos al Gradle Wrapper si es necesario:

```bash
chmod +x gradlew
```

Compilar:

```bash
./gradlew assembleDebug
```

Instalar el APK:

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

También es posible ejecutar el proyecto directamente desde Android Studio.

---

# 🔐 Seguridad

El proyecto debe evitar publicar:

- API Keys privadas.
- Credenciales de Firebase.
- Tokens.
- Contraseñas.
- Archivos de configuración sensibles.

Las credenciales y configuraciones sensibles deben mantenerse fuera del repositorio cuando corresponda.

---

# 👨‍💻 Proyecto académico

**DogFellas**

Aplicación móvil Android orientada al apoyo de animales callejeros mediante tecnología, geolocalización, atención médica, voluntariado y adopción.

---

<p align="center">
  🐾 <strong>DogFellas</strong>
  <br>
  <em>Una oportunidad para cada animal.</em>
</p>
