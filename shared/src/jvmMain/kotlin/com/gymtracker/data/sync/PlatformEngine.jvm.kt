package com.gymtracker.data.sync

import io.ktor.client.engine.*

/**
 * Engine do Ktor para Desktop (JVM - CIO)
 */
actual fun createPlatformEngine(): HttpClientEngine {
    return io.ktor.client.engine.cio.CIO.create()
}
