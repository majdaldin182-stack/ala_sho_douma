plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.douma.ala_sho"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.douma.ala_sho"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }
}
kotlin {
    jvmToolchain(17)
}

tasks.withType<JavaCompile>().configureEach {
    sourceCompatibility = "17"
    targetCompatibility = "17"
}
