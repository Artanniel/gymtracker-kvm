package com.gymtracker.data.sync

import io.ktor.client.engine.*

/**
 * Engine do Ktor para Web (WasmJS) - usa CIO (suporta WasmJS)
 */
actual fun createPlatformEngine(): HttpClientEngine {
    return io.ktor.client.engine.cio.CIO.create()
}
