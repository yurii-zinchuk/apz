package com.example.common

enum class Service(val port: Int, val host: String = "localhost") {
    FACADE(8080),
    LOGGING(8081),
    MESSAGING(8082);
}