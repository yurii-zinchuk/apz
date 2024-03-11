package com.example.service.logging

import com.example.data.HazelcastManager
import com.example.domain.models.Log
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureLoggingRouting() {
    routing {
        handlePostRequest()
        handleGetRequest()
    }
}

private fun Routing.handlePostRequest() = post("/") {
    val (uuid, text) = call.receive<Log>()
    HazelcastManager.saveLog(uuid, text)

    call.application.environment.log.info("Received Message: $text")

    call.respond(HttpStatusCode.OK)
}

private fun Routing.handleGetRequest() = get("/") {
    call.respond<String>(HazelcastManager.getLogs().toString())
}
