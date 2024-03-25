package com.example.service.facade

import com.example.data.HazelcastManager
import com.example.domain.Service
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
    handleEvents()
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

private fun Application.handleEvents() {
    environment.monitor.subscribe(ApplicationStarted) {
        environment.log.info("Service Started")
        environment.log.info("Starting new Hazelcast instance...")
        HazelcastManager.startHazelcast()
    }
    environment.monitor.subscribe(ApplicationStopped) {
        environment.log.info("Service Stopped")
        environment.log.info("Shutting down Hazelcast instance...")
        HazelcastManager.stopHazelcast()
    }
}
