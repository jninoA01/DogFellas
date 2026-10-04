# DogFellas — Localiza a tu manada

App Android desarrollada con **Kotlin 2.1.20 + Jetpack Compose 1.8.1**.

---

## 🗂 Estructura del proyecto

```
app/src/main/java/com/dogfellas/app/
├── MainActivity.kt
├── navigation/
│   ├── Screen.kt          ← Rutas selladas (sealed class)
│   └── NavGraph.kt        ← NavHost con todas las rutas
├── model/
│   └── MenuItems.kt       ← Datos de todos los menús y submenús
├── service/
│   └── DogFellasFCMService.kt   ← Firebase Cloud Messaging
└── ui/
    ├── theme/
    │   ├── Color.kt       ← Paleta de colores DogFellas
    │   └── Theme.kt       ← MaterialTheme
    ├── components/
    │   └── Components.kt  ← Header, MenuItem, Modal, Footer (reutilizables)
    └── screens/
        ├── SplashScreen.kt
        ├── HomeScreen.kt
        └── SubMenuScreens.kt   ← Todas las pantallas de submenú
```

---

## 🛠 Herramientas y versiones

| Herramienta                  | Versión     |
|-----------------------------|-------------|
| Kotlin                      | 2.1.20      |
| Jetpack Compose             | 1.8.1       |
| Navigation Compose          | 2.8.5       |
| Firebase Authentication     | 22.1.1      |
| Firebase Realtime Database  | 21.0.0      |
| Firebase Cloud Messaging    | 24.1.1      |
| Google Maps SDK             | 19.0.0      |
| JUnit                       | 5.10.2      |
| Espresso                    | 3.5.1       |

---

## 🚀 Pasos para abrir en IntelliJ IDEA / Android Studio

1. Descomprime el ZIP en tu workspace.
2. Abre la carpeta `DogFellas/` como proyecto existente.
3. Agrega tu archivo `google-services.json` de Firebase en `app/`.
4. Sincroniza Gradle (`File → Sync Project with Gradle Files`).
5. Ejecuta en emulador o dispositivo físico (API 26+).

---

## 📋 Convenciones

- Las opciones marcadas con **`*`** al final del nombre **no se implementan este semestre**.
- Al pulsar cualquier opción de menú aparece el diálogo **"En Construcción"**.
- Para agregar una nueva pantalla:
  1. Añade la ruta en `Screen.kt`
  2. Agrega el `composable()` en `NavGraph.kt`
  3. Crea la lista de ítems en `MenuItems.kt`
  4. Agrega la pantalla en `SubMenuScreens.kt`
