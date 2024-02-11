package com.example.microservices.logging

import com.example.common.Service
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import java.util.concurrent.ConcurrentHashMap

private val logs = ConcurrentHashMap<String, String>()

fun main() {
    embeddedServer(
        Netty,
        port = Service.LOGGING.port,
        host = Service.LOGGING.host,
        module = Application::moduleLogging
    ).start(wait = true)
}

private fun Application.moduleLogging() {
    install(ContentNegotiation) { gson() }
    configureLoggingRouting(logs)
}
