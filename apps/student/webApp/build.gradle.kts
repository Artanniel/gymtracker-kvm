@file:OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    wasmJs {
        browser {
            commonWebpackConfig {
                outputFileName = "gymtracker.js"
            }
        }
        binaries.executable()
    }
    sourceSets {
        val wasmJsMain by getting {
            dependencies {
                implementation(project(":shared"))
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.components.uiToolingPreview)
                implementation(npm("@cashapp/sqldelight-sqljs-worker", "2.3.2"))
            }
        }
    }
}

val skikoRuntimeVersion = "0.9.4-2"

afterEvaluate {
    val processTask = tasks.findByName("processSkikoRuntimeForKWasm") ?: return@afterEvaluate
    val syncTask = tasks.findByName("wasmJsDevelopmentExecutableCompileSync") ?: return@afterEvaluate

    val patchTask = tasks.register("patchSkikoRuntime") {
        dependsOn(syncTask)
        doLast {
            // 1. Patch skiko runtime
            val npmDir = rootProject.layout.buildDirectory.dir("js/packages_imported/skiko-js-wasm-runtime/0.9.4-2").get().asFile
            val wasmFile = npmDir.resolve("skiko.wasm")
            val mjsFile = npmDir.resolve("skiko.mjs")
            if (!wasmFile.exists() || !mjsFile.exists()) {
                throw GradleException("npm skiko 0.9.4-2 not found at ${npmDir.absolutePath}")
            }
            val dirs = listOf(
                layout.buildDirectory.dir("compose/skiko-runtime-processed-wasmjs").get().asFile,
                layout.buildDirectory.dir("compose/skiko-for-web-runtime").get().asFile,
                layout.buildDirectory.dir("compileSync/wasmJs/main/developmentExecutable/kotlin").get().asFile,
                rootProject.layout.buildDirectory.dir("js/packages/GymTrackerKMP-apps-student-webApp-wasm-js/kotlin").get().asFile,
            )
            dirs.filter { it.exists() }.forEach { dir ->
                val targetWasm = dir.resolve("skiko.wasm")
                val targetMjs = dir.resolve("skiko.mjs")
                if (targetWasm.exists()) {
                    wasmFile.copyTo(targetWasm, overwrite = true)
                }
                if (targetMjs.exists()) {
                    mjsFile.copyTo(targetMjs, overwrite = true)
                }
                logger.lifecycle("Patched skiko runtime in ${dir.absolutePath}")
            }

            // 2. Fix SQLDelight worker - replace ES module import with importScripts
            val nodeModulesDir = rootProject.layout.buildDirectory.dir("js/node_modules").get().asFile
            val workerFile = nodeModulesDir.resolve("@cashapp/sqldelight-sqljs-worker/sqljs.worker.js")
            val sqlJsFile = nodeModulesDir.resolve("sql.js/dist/sql-wasm.js")
            val sqlJsWasmFile = nodeModulesDir.resolve("sql.js/dist/sql-wasm.wasm")

            if (workerFile.exists() && sqlJsFile.exists() && sqlJsWasmFile.exists()) {
                // Read and modify worker file
                var workerContent = workerFile.readText()
                if ("import initSqlJs from" in workerContent) {
                    workerContent = workerContent.replace(
                        "import initSqlJs from \"sql.js\";",
                        "importScripts(\"/sql-wasm.js\");"
                    )
                    workerFile.writeText(workerContent)
                    logger.lifecycle("Patched sqljs.worker.js to use importScripts")
                }

                // Copy sql.js to processed resources dir
                val processedResourcesDir = layout.buildDirectory.dir("processedResources/wasmJs/main").get().asFile
                if (processedResourcesDir.exists()) {
                    sqlJsFile.copyTo(processedResourcesDir.resolve("sql-wasm.js"), overwrite = true)
                    sqlJsWasmFile.copyTo(processedResourcesDir.resolve("sql-wasm.wasm"), overwrite = true)
                    logger.lifecycle("Copied sql.js to processedResources")
                }
            }
        }
    }
    tasks.matching { it.name.startsWith("wasmJsBrowser") }.configureEach {
        dependsOn(patchTask)
    }
}
