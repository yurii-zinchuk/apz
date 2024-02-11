package com.example.microservices.facade

import com.example.common.Service
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

private lateinit var loggingClient: HttpClient
private lateinit var messagingClient: HttpClient

fun main() {
    embeddedServer(
        Netty,
        port = Service.FACADE.port,
        host = Service.FACADE.host,
        module = Application::moduleFacade
    ).start(wait = true)
}

private fun Application.moduleFacade() {
    install(io.ktor.server.plugins.contentnegotiation.ContentNegotiation) { gson() }
    configureHttpClients()
    configureFacadeRouting(loggingClient, messagingClient)
}

private fun configureHttpClients() {
    loggingClient = HttpClient {
        install(ContentNegotiation) { gson() }

        defaultRequest {
            url {
                port = Service.LOGGING.port
                host = Service.LOGGING.host
                protocol = URLProtocol.HTTP
            }
            contentType(ContentType.Application.Json)
        }
    }

    messagingClient = HttpClient {
        install(ContentNegotiation) { gson() }

        defaultRequest {
            url {
                port = Service.MESSAGING.port
                host = Service.MESSAGING.host
                protocol = URLProtocol.HTTP
            }
            contentType(ContentType.Application.Json)
        }
    }
}
