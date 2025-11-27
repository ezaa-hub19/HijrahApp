// build.gradle.kts (Project: HijrahApp)

plugins {
    // Mendeklarasikan Plugin Android Aplikasi
    alias(libs.plugins.android.application) apply false

    // Mendeklarasikan Plugin Kotlin Android
    alias(libs.plugins.kotlin.android) apply false

    // Plugin Compose Compiler tidak dideklarasikan di sini karena ia terintegrasi
    // dengan Plugin Kotlin Android dan dikelola di composeOptions.
}

// Tidak perlu blok "dependencies" di file root ini.