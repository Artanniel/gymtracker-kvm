package com.gymtracker.desktop

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.gymtracker.AppDependencies
import com.gymtracker.data.db.DatabaseDriverFactory
import com.gymtracker.ui.App
import kotlinx.coroutines.runBlocking

fun main() = application {
    runBlocking { AppDependencies.init(DatabaseDriverFactory()) }
    Window(onCloseRequest = ::exitApplication, title = "GymTracker") {
        App()
    }
}
