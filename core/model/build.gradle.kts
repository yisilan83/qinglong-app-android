plugins {
    alias(libs.plugins.ql.android.library)
}

android {
    namespace = "com.qinglong.core.model"
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
}
