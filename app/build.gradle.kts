// app/build.gradle.kts
// Pastikan google-services.json sudah di-download dari Firebase Console
// dan diletakkan di folder app/

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")   // Firebase
}

android {
    namespace = "com.example.ecoswap"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.ecoswap"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
    }

    // ── FIX: Paksa versi activity yang konsisten ──────────────────────────────
    // Error "activity-compose:1.8.2" muncul karena ada konflik versi transitive.
    // resolutionStrategy ini memaksa semua dependency pakai versi yang sama.
    configurations.all {
        resolutionStrategy {
            force("androidx.activity:activity:1.9.3")
            force("androidx.activity:activity-ktx:1.9.3")
            force("androidx.activity:activity-compose:1.9.3")
            force("com.google.guava:guava:32.0.1-android")
        }
    }
}

dependencies {
    // ── Compose BOM ──────────────────────────────────────────────────────────
    // Gunakan 2024.12.01 — stabil dan kompatibel dengan activity 1.9.3
    val composeBom = platform("androidx.compose:compose-bom:2024.12.01")
    implementation(composeBom)

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.compose.material:material-icons-extended")

    // Versi eksplisit — jangan biarkan transitive dependency override ini
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")

    // ── Navigation ───────────────────────────────────────────────────────────
    implementation("androidx.navigation:navigation-compose:2.8.4")

    // ── Firebase BOM ─────────────────────────────────────────────────────────
    val firebaseBom = platform("com.google.firebase:firebase-bom:33.6.0")
    implementation(firebaseBom)
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")

    // ── Coroutines ───────────────────────────────────────────────────────────
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    // ── CameraX (untuk AI Developer) ─────────────────────────────────────────
    // NOTE FOR AI DEVELOPER: Uncomment baris ini saat mengintegrasikan kamera
     implementation("androidx.camera:camera-core:1.3.4")
     implementation("androidx.camera:camera-camera2:1.3.4")
     implementation("androidx.camera:camera-lifecycle:1.3.4")
     implementation("androidx.camera:camera-view:1.3.4")
     implementation("androidx.concurrent:concurrent-futures-ktx:1.1.0")
     implementation("com.google.guava:guava:32.0.1-android")
    // TFLite
    implementation("org.tensorflow:tensorflow-lite:2.14.0")
    implementation("org.tensorflow:tensorflow-lite-support:0.4.4")
    implementation("com.google.accompanist:accompanist-permissions:0.32.0")

    // ── Debug ─────────────────────────────────────────────────────────────────
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}