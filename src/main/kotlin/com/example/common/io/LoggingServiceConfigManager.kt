@file:Suppress("MemberVisibilityCanBePrivate", "Unused")

package com.example.common.io

import java.io.File

private const val FILE_PATH = "src/main/resources/logging_instances.txt"

object LoggingServiceConfigManager {
    fun getLoggingServiceInstancesNumber(): Int {
        val file = File(FILE_PATH)
        return if (file.exists()) file.readText().toInt() else 0
    }

    fun saveLoggingServiceInstancesNumber(instances: Int) {
        val file = File(FILE_PATH)
        file.writeText(instances.toString())
    }

    fun reduceLoggingServiceInstancesNumber() {
        val instances = getLoggingServiceInstancesNumber().let { if (it > 0) it - 1 else 0 }
        saveLoggingServiceInstancesNumber(instances)
    }

    fun increaseLoggingServiceInstancesNumber() {
        val instances = getLoggingServiceInstancesNumber() + 1
        saveLoggingServiceInstancesNumber(instances)
    }
}
