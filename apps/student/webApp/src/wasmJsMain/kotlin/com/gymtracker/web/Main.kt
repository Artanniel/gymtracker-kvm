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

        val body = document.body!!
        body.setAttribute("style", "margin:0;padding:0;width:100%;height:100%;overflow:hidden;")

        ComposeViewport(body) {
            App()
        }
    }
}
