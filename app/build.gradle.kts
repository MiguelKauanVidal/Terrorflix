plugins {
    // Aplica o plugin de aplicação do Android
    id("com.android.application")
    // Aplica o plugin do Kotlin para Android
    id("org.jetbrains.kotlin.android")
    alias(libs.plugins.kotlin.compose)
}

android {
    // Configurações do SDK
    namespace = "com.terrorflix"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.terrorflix"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    // Configurações de compilação
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    // Configuração para usar Jetpack Compose
    buildFeatures {
        compose = true
    }
    composeOptions {
        // Assegure-se de usar a versão mais recente
        kotlinCompilerExtensionVersion = "1.5.1" // Use a versão compatível com seu Kotlin
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

// Definição das dependências
dependencies {
    // --- Dependências do Android Studio e Kotlin ---
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")

    // --- Dependências do Jetpack Compose UI ---
    // UI e Ferramentas
    implementation(platform("androidx.compose:compose-bom:2023.08.00")) // Plataforma para gerenciar versões
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")

    // --- Dependências do Material Design 3 (Corrigindo Unresolved References) ---
    // IMPORTANTE: Este pacote contém Column, Row, Button, Scaffold, OutlinedTextField, etc.
    // Ele é fundamental para resolver os erros que você está vendo.
    implementation("androidx.compose.material3:material3")

    // --- Dependências do ViewModel e LiveData (Para arquitetura MVVM) ---
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

    // --- Dependências de Teste ---
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
