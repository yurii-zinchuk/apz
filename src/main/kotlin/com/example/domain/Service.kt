package com.example.domain

enum class Service(val port: Int, val host: String = "localhost") {
    FACADE(8085),
    MESSAGING(8084),
    LOGGING(8086);
}
