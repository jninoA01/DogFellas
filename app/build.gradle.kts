plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.dogfellas.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.dogfellas.app"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        // ✅ Kotlin DSL correcto
        multiDexEnabled = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false

            proguardFiles(
                getDefaultProguardFile(
                    "proguard-android-optimize.txt"
                ),
                "proguard-rules.pro"
            )
        }
    }

    // ✅ Java 17
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }
}

dependencies {

    // ✅ Multidex
    implementation("androidx.multidex:multidex:2.0.1")

    // 🔥 Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))

    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")

    // ✅ Compose BOM
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))

    // ✅ Core Android
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.2")

    // ✅ Compose UI
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")

    // ✅ Material 3
    implementation("androidx.compose.material3:material3")

    // ✅ Material Icons Extended
    implementation("androidx.compose.material:material-icons-extended")

    // ✅ Navigation
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // ✅ ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    // 📍 Ubicación
    implementation("com.google.android.gms:play-services-location:21.1.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // 🔄 Corrutinas
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    // 🌐 Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.11.0")

    // 🧩 Gson
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

    // 🖼️ Coil
    implementation("io.coil-kt:coil-compose:2.6.0")

    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.maps.android:maps-compose:4.3.3")
    implementation("com.google.android.libraries.places:places:3.5.0")

    // 🛠️ Debug
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}