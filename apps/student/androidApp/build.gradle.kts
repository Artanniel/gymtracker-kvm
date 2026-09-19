plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    androidTarget()
}

android {
    namespace = "com.gymtracker.android"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.gymtracker.android"
        minSdk = 26
        targetSdk = 35
        versionCode = 3
        versionName = "1.1.1"
    }
    buildTypes {
        release { isMinifyEnabled = false }
    }
    lint {
        abortOnError = false
        checkReleaseBuilds = false
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(project(":shared"))
    implementation(libs.androidx.activity.compose)
    implementation(libs.sqldelight.android.driver)
}
