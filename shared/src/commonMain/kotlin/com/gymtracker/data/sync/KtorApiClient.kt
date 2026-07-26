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

/**
 * Configuração do Ktor HttpClient para sync
 */
expect fun createPlatformEngine(): HttpClientEngine

/**
 * Implementação do ApiClient usando Ktor HTTP Client.
 * Suporta todas as plataformas KMP (Android, iOS, Desktop, Web).
 */
class KtorApiClient(
    private val baseUrl: String = AppConfig.DEFAULT_API_URL
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
        
        defaultRequest {
            url(baseUrl)
            header("Content-Type", "application/json")
            // TODO: Adicionar JWT token quando auth estiver implementada
            // bearer { token = AuthConfig.getToken() }
        }
    }
    
    /**
     * POST - Criar nova entidade
     */
    override suspend fun post(entityType: String, payload: String) {
        val response = client.post("/api/$entityType") {
            setBody(payload)
        }
        
        if (!response.status.isSuccess()) {
            throw SyncException("POST failed: ${response.status} - ${response.bodyAsText()}")
        }
    }
    
    /**
     * PUT - Atualizar entidade existente
     */
    override suspend fun put(entityType: String, entityId: String, payload: String) {
        val response = client.put("/api/$entityType/$entityId") {
            setBody(payload)
        }
        
        if (!response.status.isSuccess()) {
            throw SyncException("PUT failed: ${response.status} - ${response.bodyAsText()}")
        }
    }
    
    /**
     * DELETE - Remover entidade
     */
    override suspend fun delete(entityType: String, entityId: String) {
        val response = client.delete("/api/$entityType/$entityId")
        
        if (!response.status.isSuccess()) {
            throw SyncException("DELETE failed: ${response.status} - ${response.bodyAsText()}")
        }
    }
    
    /**
     * GET - Buscar todas as entidades
     */
    override suspend fun getAll(entityType: String): String {
        val response = client.get("/api/$entityType")
        
        if (!response.status.isSuccess()) {
            throw SyncException("GET failed: ${response.status} - ${response.bodyAsText()}")
        }
        
        return response.bodyAsText()
    }
    
    /**
     * GET - Buscar entidade por ID
     */
    suspend fun getById(entityType: String, entityId: String): String {
        val response = client.get("/api/$entityType/$entityId")
        
        if (!response.status.isSuccess()) {
            throw SyncException("GET by ID failed: ${response.status} - ${response.bodyAsText()}")
        }
        
        return response.bodyAsText()
    }
    
    /**
     * POST - Login/autenticação
     */
    suspend fun login(email: String, password: String): String {
        val response = client.post("/api/auth/login") {
            setBody(mapOf("email" to email, "password" to password))
        }
        
        if (!response.status.isSuccess()) {
            throw SyncException("Login failed: ${response.status} - ${response.bodyAsText()}")
        }
        
        return response.bodyAsText()
    }
    
    /**
     * POST - Refresh token
     */
    suspend fun refreshToken(refreshToken: String): String {
        val response = client.post("/api/auth/refresh") {
            setBody(mapOf("refreshToken" to refreshToken))
        }
        
        if (!response.status.isSuccess()) {
            throw SyncException("Refresh token failed: ${response.status} - ${response.bodyAsText()}")
        }
        
        return response.bodyAsText()
    }
    
    /**
     * Health check do backend
     */
    suspend fun healthCheck(): Boolean {
        return try {
            val response = client.get("/api/health")
            response.status.isSuccess()
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Fecha o cliente HTTP
     */
    fun close() {
        client.close()
    }
}

/**
 * Exceção específica de sync
 */
class SyncException(message: String, cause: Throwable? = null) : Exception(message, cause)
