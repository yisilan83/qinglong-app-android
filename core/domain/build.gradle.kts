plugins {
    alias(libs.plugins.ql.android.library)
}

android {
    namespace = "com.qinglong.core.domain"
}

dependencies {
    implementation(project(":core:model"))

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Hilt (for @Inject)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}
