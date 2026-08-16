package com.gymtracker.util

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.skia.Image
import java.util.Base64

actual fun decodeBase64ToImageBitmap(base64: String): ImageBitmap? {
    return try {
        val cleanBase64 = if (base64.contains(",")) {
            base64.substringAfter(",")
        } else {
            base64
        }
        val decoded = Base64.getDecoder().decode(cleanBase64)
        Image.makeFromEncoded(decoded).toComposeImageBitmap()
    } catch (e: Exception) {
        null
    }
}
