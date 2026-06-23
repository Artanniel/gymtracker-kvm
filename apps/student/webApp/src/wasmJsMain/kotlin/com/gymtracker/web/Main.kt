package com.gymtracker.web

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.gymtracker.AppDependencies
import com.gymtracker.data.db.DatabaseDriverFactory
import com.gymtracker.ui.App
import kotlinx.browser.document
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val scope = MainScope()
    scope.launch {
        AppDependencies.init(DatabaseDriverFactory())
        ComposeViewport(document.body!!) {
            App()
        }
    }
}
