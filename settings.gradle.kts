pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        ivy("https://nodejs.org/dist") {
            patternLayout {
                artifact("v[revision]/node-v[revision]-[classifier].[ext]")
            }
            metadataSources {
                artifact()
            }
            content {
                includeModule("org.nodejs", "node")
            }
        }
        ivy("https://github.com") {
            patternLayout {
                artifact("yarnpkg/yarn/releases/download/v[revision]/yarn-v[revision].[ext]")
            }
            metadataSources {
                artifact()
            }
            content {
                includeModule("com.yarnpkg", "yarn")
            }
        }
    }
}

rootProject.name = "GymTrackerKMP"
include(":shared", ":apps:student:androidApp", ":apps:student:desktopApp", ":apps:student:webApp")
// include(":backend") // Desabilitado: Quarkus 3.15.1 incompatível com Gradle 9.3 (detachedConfiguration error)
