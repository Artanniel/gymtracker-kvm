package com.gymtracker.data.sync

import io.ktor.client.engine.*

/**
 * Engine do Ktor para Android (OkHttp)
 */
actual fun createPlatformEngine(): HttpClientEngine {
    return io.ktor.client.engine.okhttp.OkHttp.create()
}
