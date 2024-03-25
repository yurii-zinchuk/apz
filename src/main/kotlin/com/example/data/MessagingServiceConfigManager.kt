@file:Suppress("MemberVisibilityCanBePrivate", "Unused")

package com.example.data

import java.io.File

private const val FILE_PATH = "messaging_instances.txt"

object MessagingServiceConfigManager : DataManager() {
    fun getMessagingServiceInstancesNumber(): Int {
        val file = File(LOCAL_DATA_ROOT_DIR + FILE_PATH)
        return if (file.exists()) file.readText().toInt() else 0
    }

    fun saveMessagingServiceInstancesNumber(instances: Int) {
        val file = File(LOCAL_DATA_ROOT_DIR + FILE_PATH)
        file.writeText(instances.toString())
    }

    fun reduceMessagingServiceInstancesNumber() {
        val instances = getMessagingServiceInstancesNumber().let { if (it > 0) it - 1 else 0 }
        saveMessagingServiceInstancesNumber(instances)
    }

    fun increaseMessagingServiceInstancesNumber() {
        val instances = getMessagingServiceInstancesNumber() + 1
        saveMessagingServiceInstancesNumber(instances)
    }
}