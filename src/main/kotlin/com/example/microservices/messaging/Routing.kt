package com.example.microservices.messaging

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureMessagingRouting() {
    routing {
        handleGetRequest()
    }
}

@Suppress("UNUSED")
private fun handlePostRequest() {
    TODO("Not yet implemented!")
}

private fun Routing.handleGetRequest() = get("/") {
    call.respondText("Not yet implemented!")
}
