package com.example.service.facade

import com.example.data.HazelcastManager
import com.example.data.LoggingServiceConfigManager
import com.example.data.MessagingServiceConfigManager
import com.example.domain.Service
import com.example.domain.models.Log
import com.example.domain.models.Message
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
        url.port = getRandomLoggingServicePort()
        setBody(Log(uuid, message.message))
    }

    HazelcastManager.pushMessage(message.message)

    call.respond(HttpStatusCode.OK)
}

private fun Routing.handleGetRequest(loggingClient: HttpClient, messagingClient: HttpClient) = get("/") {
    val logs: Deferred<String> = async {
        loggingClient.get {
            url.port = getRandomLoggingServicePort()
        }.body()
    }
    val messages: Deferred<String> = async {
        messagingClient.get {
            url.port = getRandomMessagingServicePort()
        }.body()
    }

    call.respondText("${logs.await()} ${messages.await()}")
}

private fun getRandomLoggingServicePort(): Int {
    val instances = LoggingServiceConfigManager.getLoggingServiceInstancesNumber()
    return Service.LOGGING.port + (0..<instances).random()
}

private fun getRandomMessagingServicePort(): Int {
    val instances = MessagingServiceConfigManager.getMessagingServiceInstancesNumber()
    return Service.MESSAGING.port - (0..<instances).random()
}
