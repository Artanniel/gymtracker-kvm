@file:OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.androidLibrary)
    id("io.gitlab.arturbosch.detekt") version "1.23.7"
}

detekt {
    buildUponDefaultConfig = true
    allRules = false
    config.setFrom("$rootDir/config/detekt/detekt.yml")
    source.setFrom(
        files(
            "src/commonMain/kotlin",
            "src/androidMain/kotlin",
            "src/iosMain/kotlin",
            "src/jvmMain/kotlin",
            "src/wasmJsMain/kotlin"
        )
    )
}

kotlin {
    androidTarget()

    jvm()

    iosArm64()
    iosSimulatorArm64()

    wasmJs {
        browser()
        binaries.executable()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)

                api(libs.lifecycle.viewmodel)
                implementation(libs.lifecycle.viewmodel.compose)
                implementation(libs.lifecycle.runtime.compose)

                implementation(libs.coroutines.core)
                implementation(libs.serialization.json)
                implementation(libs.datetime)

                implementation(libs.sqldelight.runtime)
                implementation(libs.sqldelight.coroutines)

                implementation(libs.koalaplot.core)
                
                // Ktor (HTTP Client)
                implementation(libs.ktor.core)
                implementation(libs.ktor.content.negotiation)
                implementation(libs.ktor.serialization.json)
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(libs.coroutines.android)
                implementation(libs.sqldelight.android.driver)
                implementation(compose.preview)
                implementation(libs.ktor.client.okhttp)
            }
        }

        val iosMain by creating {
            dependsOn(commonMain)
            dependencies {
                implementation(libs.sqldelight.native.driver)
                implementation(libs.ktor.client.darwin)
            }
        }

        val iosArm64Main by getting { dependsOn(iosMain) }
        val iosSimulatorArm64Main by getting { dependsOn(iosMain) }

        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(libs.sqldelight.sqlite.driver)
                implementation(libs.coroutines.core)
                implementation(libs.ktor.client.cio)
            }
        }

        val wasmJsMain by getting {
            dependencies {
                implementation(libs.sqldelight.web.worker)
                implementation(libs.ktor.client.cio)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

sqldelight {
    databases {
        create("GymDatabase") {
            packageName.set("com.gymtracker.db")
            generateAsync.set(true)
        }
    }
}

android {
    namespace = "com.gymtracker.shared"
    compileSdk = 35
    defaultConfig {
        minSdk = 26
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
