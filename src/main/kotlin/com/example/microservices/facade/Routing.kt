package com.example.microservices.facade

import com.example.common.models.Log
import com.example.common.models.Message
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

fun Application.configureFacadeRouting(loggingClient: HttpClient, messagingClient: HttpClient) {
    routing {
        handlePostRequest(loggingClient)
        handleGetRequest(loggingClient, messagingClient)
    }
}

private fun Routing.handlePostRequest(loggingClient: HttpClient) = post("/") {
    val message: Message = call.receive()
    val uuid = java.util.UUID.randomUUID().toString()

    loggingClient.post {
        setBody(Log(uuid, message.message))
    }

    call.respond(HttpStatusCode.OK)
}

private fun Routing.handleGetRequest(loggingClient: HttpClient, messagingClient: HttpClient) = get("/") {
    val logs: Deferred<String> = async { loggingClient.get {}.body() }
    val messagingResponse: Deferred<String> = async { messagingClient.get {}.body() }

    call.respondText("${logs.await()} ${messagingResponse.await()}")
}
