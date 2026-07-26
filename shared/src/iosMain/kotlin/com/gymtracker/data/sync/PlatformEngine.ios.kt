package com.gymtracker.data.sync

import io.ktor.client.engine.*

/**
 * Engine do Ktor para iOS (Darwin)
 */
actual fun createPlatformEngine(): HttpClientEngine {
    return io.ktor.client.engine.darwin.Darwin.create()
}
