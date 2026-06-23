import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    jvm()
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(project(":shared"))
                implementation(compose.desktop.currentOs)
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.10.2")
            }
        }
    }
}

// Força o runtime nativo do Skiko a usar a mesma versão do JAR (0.9.x),
// evitando UnsatisfiedLinkError causado por mismatch entre versões.
configurations.all {
    resolutionStrategy {
        force("org.jetbrains.skiko:skiko-awt-runtime-linux-x64:0.9.4.2")
        force("org.jetbrains.skiko:skiko-awt-runtime-linux-arm64:0.9.4.2")
        force("org.jetbrains.skiko:skiko:0.9.4.2")
        force("org.jetbrains.skiko:skiko-awt:0.9.4.2")
    }
}

compose.desktop {
    application {
        mainClass = "com.gymtracker.desktop.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "GymTracker"
            packageVersion = "1.0.0"
            description = "Acompanhamento de treino e dieta"
        }
    }
}
