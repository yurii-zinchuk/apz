@file:Suppress("MemberVisibilityCanBePrivate", "Unused")

package com.example.data

import java.io.File

private const val FILE_PATH = "logging_instances.txt"

object LoggingServiceConfigManager : DataManager() {
    fun getLoggingServiceInstancesNumber(): Int {
        val file = File(LOCAL_DATA_ROOT_DIR + FILE_PATH)
        return if (file.exists()) file.readText().toInt() else 0
    }

    fun saveLoggingServiceInstancesNumber(instances: Int) {
        val file = File(LOCAL_DATA_ROOT_DIR + FILE_PATH)
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
