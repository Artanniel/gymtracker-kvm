package com.gymtracker.util

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

actual fun decodeBase64ToImageBitmap(base64: String): ImageBitmap? {
    return try {
        val cleanBase64 = if (base64.contains(",")) {
            base64.substringAfter(",")
        } else {
            base64
        }
        val decoded = Base64.decode(cleanBase64, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(decoded, 0, decoded.size)
        bitmap?.asImageBitmap()
    } catch (e: Exception) {
        null
    }
}
