package com.gymtracker.util

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import kotlinx.browser.window
import org.jetbrains.skia.Image

actual fun decodeBase64ToImageBitmap(base64: String): ImageBitmap? {
    return try {
        val cleanBase64 = if (base64.contains(",")) {
            base64.substringAfter(",")
        } else {
            base64
        }
        val decoded = window.atob(cleanBase64)
        val bytes = ByteArray(decoded.length) { decoded[it].code.toByte() }
        Image.makeFromEncoded(bytes).toComposeImageBitmap()
    } catch (e: Exception) {
        null
    }
}
