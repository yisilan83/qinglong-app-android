plugins {
    alias(libs.plugins.ql.android.application)
}

android {
    namespace = "com.qinglong.app"
}

dependencies {
    // Feature modules
    implementation(project(":feature:login"))
    implementation(project(":core:data"))
    implementation(project(":core:ui"))

    // Compose BOM
    val composeBom = platform(libs.compose.bom)
    implementation(composeBom)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    debugImplementation(libs.compose.ui.tooling)

    // Activity
    implementation(libs.androidx.activity.compose)

    // Lifecycle
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Navigation
    implementation(libs.compose.navigation)
    implementation(libs.compose.hilt.navigation)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.android.testing)

    // DataStore
    implementation(libs.datastore.preferences)

    // Core
    implementation(libs.androidx.core.ktx)

    // Testing
    testImplementation(libs.junit)
}
