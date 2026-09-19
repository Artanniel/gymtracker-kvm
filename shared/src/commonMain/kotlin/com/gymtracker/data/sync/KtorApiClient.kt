package com.gymtracker.data.sync

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

expect fun createPlatformEngine(): HttpClientEngine

class KtorApiClient(
    private val baseUrl: String = AppConfig.DEFAULT_API_URL,
    private val tokenProvider: (() -> String?)? = null
) : ApiClient {

    private val client = HttpClient(createPlatformEngine()) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                encodeDefaults = true
            })
        }

        install(HttpTimeout) {
            requestTimeoutMillis = SyncConfig.REQUEST_TIMEOUT
            connectTimeoutMillis = 10_000
            socketTimeoutMillis = 10_000
        }

        install(HttpRequestLifecycle)

        defaultRequest {
            url(baseUrl)
            header("Content-Type", "application/json")
            tokenProvider?.let { provider ->
                provider()?.let { token ->
                    header("Authorization", "Bearer $token")
                }
            }
        }
    }

    fun withTokenProvider(provider: () -> String?): KtorApiClient {
        return KtorApiClient(baseUrl, provider)
    }

    override suspend fun post(entityType: String, payload: String) {
        val response = client.post("/api/$entityType") {
            setBody(payload)
        }
        handleAuthError(response)
        if (!response.status.isSuccess()) {
            throw SyncException("POST failed: ${response.status} - ${response.bodyAsText()}")
        }
    }

    override suspend fun put(entityType: String, entityId: String, payload: String) {
        val response = client.put("/api/$entityType/$entityId") {
            setBody(payload)
        }
        handleAuthError(response)
        if (!response.status.isSuccess()) {
            throw SyncException("PUT failed: ${response.status} - ${response.bodyAsText()}")
        }
    }

    override suspend fun delete(entityType: String, entityId: String) {
        val response = client.delete("/api/$entityType/$entityId")
        handleAuthError(response)
        if (!response.status.isSuccess()) {
            throw SyncException("DELETE failed: ${response.status} - ${response.bodyAsText()}")
        }
    }

    override suspend fun getAll(entityType: String): String {
        val response = client.get("/api/$entityType")
        handleAuthError(response)
        if (!response.status.isSuccess()) {
            throw SyncException("GET failed: ${response.status} - ${response.bodyAsText()}")
        }
        return response.bodyAsText()
    }

    suspend fun getById(entityType: String, entityId: String): String {
        val response = client.get("/api/$entityType/$entityId")
        handleAuthError(response)
        if (!response.status.isSuccess()) {
            throw SyncException("GET by ID failed: ${response.status} - ${response.bodyAsText()}")
        }
        return response.bodyAsText()
    }

    suspend fun login(email: String, password: String): String {
        val response = client.post("/api/auth/login") {
            setBody(mapOf("email" to email, "password" to password))
        }
        if (!response.status.isSuccess()) {
            throw SyncException("Login failed: ${response.status} - ${response.bodyAsText()}")
        }
        return response.bodyAsText()
    }

    suspend fun register(name: String, email: String, password: String): String {
        val response = client.post("/api/auth/register") {
            setBody(mapOf("name" to name, "email" to email, "password" to password))
        }
        if (!response.status.isSuccess()) {
            throw SyncException("Register failed: ${response.status} - ${response.bodyAsText()}")
        }
        return response.bodyAsText()
    }

    suspend fun refreshToken(refreshToken: String): String {
        val response = client.post("/api/auth/refresh") {
            setBody(mapOf("refreshToken" to refreshToken))
        }
        if (!response.status.isSuccess()) {
            throw SyncException("Refresh token failed: ${response.status} - ${response.bodyAsText()}")
        }
        return response.bodyAsText()
    }

    suspend fun healthCheck(): Boolean {
        return try {
            val response = client.get("/api/health")
            response.status.isSuccess()
        } catch (e: Exception) {
            false
        }
    }

    private suspend fun handleAuthError(response: HttpResponse) {
        if (response.status == HttpStatusCode.Unauthorized) {
            throw SyncException("Session expired. Please login again.")
        }
    }

    fun close() {
        client.close()
    }
}

class SyncException(message: String, cause: Throwable? = null) : Exception(message, cause)
