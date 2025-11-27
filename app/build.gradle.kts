// app/build.gradle.kts

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.android.hijrahapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.android.hijrahapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs += listOf("-opt-in=androidx.compose.material3.ExperimentalMaterial3Api")
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        // VERSIONS DIJADIKAN 1.5.6 UNTUK KOMPATIBILITAS 1.9.21
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    // TAMBAHKAN BARIS INI UNTUK IKON LENGKAP:
    implementation("androidx.compose.material:material-icons-extended")
    // TAMBAHKAN INI UNTUK LOKASI GPS
    implementation("com.google.android.gms:play-services-location:21.0.1")
    // Retrofit (API)
    implementation(libs.retrofit)
    // PASTIKAN BARIS INI ADA
    implementation(libs.converter.gson)
    // ViewModel Compose (untuk 'viewModel')
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    // Navigation Compose (untuk 'rememberNavController', 'NavHost', 'composable')
    implementation(libs.androidx.navigation.compose)
    // This is the core Gson library required for manual JSON parsing
    implementation("com.google.code.gson:gson:2.10.1")

    // (Your existing Retrofit dependencies should also be here)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
}