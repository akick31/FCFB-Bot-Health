package com.fcfb.discord.health.api

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fcfb.discord.health.model.health.BotHealth
import com.fcfb.discord.health.utils.Logger
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.endpoint
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.jackson.jackson
import java.util.Properties

class RotomClient {
    private val baseUrl: String
    private val httpClient =
        HttpClient(CIO) {
            engine {
                maxConnectionsCount = 64
                endpoint {
                    maxConnectionsPerRoute = 8
                    connectTimeout = 10_000
                    requestTimeout = 60_000
                }
            }

            install(ContentNegotiation) {
                jackson {} // Configure Jackson for JSON serialization
            }
        }

    init {
        val stream =
            this::class.java.classLoader.getResourceAsStream("application.properties")
                ?: throw RuntimeException("application.properties file not found")
        val properties = Properties()
        properties.load(stream)
        baseUrl = properties.getProperty("rotom.url")
    }

    /**
     * Get Rotom health
     * @return BotHealth
     */
    internal suspend fun getHealth(): BotHealth? {
        val endpointUrl = "$baseUrl/health"
        return getRequest(endpointUrl)
    }

    /**
     * Call a get request to the endpoint and return a string
     * @param endpointUrl
     * @return Game
     */
    private suspend fun getRequest(endpointUrl: String): BotHealth? {
        return try {
            val response: HttpResponse =
                httpClient.get(endpointUrl) {
                    contentType(ContentType.Application.Json)
                }
            val jsonResponse = response.bodyAsText()
            val objectMapper = jacksonObjectMapper()
            objectMapper.readValue(jsonResponse, BotHealth::class.java)
        } catch (e: Exception) {
            Logger.error(e.message ?: "Unknown error occurred while making a get request to the Rotom endpoint")
            null
        }
    }
}
