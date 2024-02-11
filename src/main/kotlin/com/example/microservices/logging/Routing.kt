package com.example.microservices.logging

import com.example.common.models.Log
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.concurrent.ConcurrentHashMap

fun Application.configureLoggingRouting(logs: ConcurrentHashMap<String, String>) {
    routing {
        handlePostRequest(logs)
        handleGetRequest(logs)
    }
}

private fun Routing.handlePostRequest(logs: ConcurrentHashMap<String, String>) = post("/") {
    val (uuid, text) = call.receive<Log>()
    logs.saveLog(uuid, text)

    call.application.environment.log.info("Received Message: $text")

    call.respond(HttpStatusCode.OK)
}

private fun Routing.handleGetRequest(logs: ConcurrentHashMap<String, String>) = get("/") {
    call.respond<String>(logs.getLogs().toString())
}

private fun ConcurrentHashMap<String, String>.saveLog(uuid: String, text: String) {
    this@saveLog[uuid] = text
}

private fun ConcurrentHashMap<String, String>.getLogs(): List<String> {
    return values.toList()
}
