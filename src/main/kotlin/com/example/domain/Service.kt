package com.example.domain

enum class Service(val port: Int, val host: String = "localhost") {
    FACADE(8081),
    MESSAGING(8082),
    LOGGING(8083);
}
