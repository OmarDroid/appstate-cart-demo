plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.example.appstatecart"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.example.appstatecart"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }

    // Compose runtime's Android artifact calls android.os.Trace, which is a stub in local JVM tests.
    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(libs.androidx.appstate.transform)
    implementation(libs.androidx.appstate.statestore)
    implementation(libs.androidx.appstate.statestore.datastore)
    // Used directly in StateStorePersistence.kt to build the DataStore with a corruption handler.
    implementation(libs.androidx.datastore.core.okio)
    implementation(libs.okio)
    implementation(libs.kotlinx.serialization.core)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    testImplementation(libs.androidx.appstate.transform.testing)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.junit)
    testImplementation(libs.truth)
    testImplementation(libs.turbine)
}
