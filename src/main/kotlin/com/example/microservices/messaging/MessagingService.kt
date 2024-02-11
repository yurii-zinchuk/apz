package com.example.microservices.messaging

import com.example.common.Service
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*

fun main() {
    embeddedServer(
        Netty,
        port = Service.MESSAGING.port,
        host = Service.MESSAGING.host,
        module = Application::moduleMessaging
    ).start(wait = true)
}

private fun Application.moduleMessaging() {
    install(ContentNegotiation) { gson() }
    configureMessagingRouting()
}
