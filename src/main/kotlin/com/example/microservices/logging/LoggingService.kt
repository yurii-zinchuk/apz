package com.example.microservices.logging

import com.example.common.Service
import com.example.common.io.HazelcastManager
import com.example.common.io.LoggingServiceConfigManager
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*

fun main() {
    val instances = LoggingServiceConfigManager.getLoggingServiceInstancesNumber()

    embeddedServer(
        Netty,
        port = Service.LOGGING.port + instances,
        host = Service.LOGGING.host,
        module = Application::moduleLogging
    ).start(wait = true)
}

private fun Application.moduleLogging() {
    install(ContentNegotiation) { gson() }
    configureLoggingRouting()
    handleEvents()
}

private fun Application.handleEvents() {
    environment.monitor.subscribe(ApplicationStarted) {
        environment.log.info("Service Started")
        environment.log.info("Starting new Hazelcast instance...")
        LoggingServiceConfigManager.increaseLoggingServiceInstancesNumber()
        HazelcastManager.startHazelcast()
    }
    environment.monitor.subscribe(ApplicationStopped) {
        environment.log.info("Service Stopped")
        environment.log.info("Shutting down Hazelcast instance...")
        LoggingServiceConfigManager.reduceLoggingServiceInstancesNumber()
        HazelcastManager.stopHazelcast()
    }
}
