package com.example.service.messaging

import com.example.data.HazelcastManager
import com.example.data.MessagesStore
import com.example.data.MessagingServiceConfigManager
import com.example.domain.Service
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

fun main() {
    val instances = MessagingServiceConfigManager.getMessagingServiceInstancesNumber()

    embeddedServer(
        Netty,
        port = Service.MESSAGING.port - instances,
        host = Service.MESSAGING.host,
        module = Application::moduleMessaging
    ).start(wait = true)
}

private fun Application.moduleMessaging() {
    install(ContentNegotiation) { gson() }
    configureMessagingRouting()
    handleEvents()
    listenMessages()
}

private fun Application.handleEvents() {
    environment.monitor.subscribe(ApplicationStarted) {
        environment.log.info("Service Started")
        MessagingServiceConfigManager.increaseMessagingServiceInstancesNumber()
    }
    environment.monitor.subscribe(ApplicationStopped) {
        environment.log.info("Service Stopped")
        MessagingServiceConfigManager.reduceMessagingServiceInstancesNumber()
    }
}

private fun Application.listenMessages() = MainScope().launch(Dispatchers.Default) {
    while (true) {
        val message = HazelcastManager.readMessage()
        MessagesStore.saveMessage(message)
        environment.log.info("Received Message: $message")
    }
}
