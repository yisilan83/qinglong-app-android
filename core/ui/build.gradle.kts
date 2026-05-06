plugins {
    alias(libs.plugins.ql.android.library)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.qinglong.core.ui"
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(project(":core:model"))

    // Compose BOM
    val composeBom = platform(libs.compose.bom)
    implementation(composeBom)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.extended)
    debugImplementation(libs.compose.ui.tooling)

    // Lifecycle
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Coil
    implementation(libs.coil.compose)

    // DataStore (Theme preferences)
    implementation(libs.datastore.preferences)
}
