package com.example.service.messaging

import com.example.data.MessagesStore
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
    call.respondText(MessagesStore.getMessages().toString())
}
