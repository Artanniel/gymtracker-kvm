package com.gymtracker.util

import androidx.compose.ui.graphics.ImageBitmap

expect fun decodeBase64ToImageBitmap(base64: String): ImageBitmap?
