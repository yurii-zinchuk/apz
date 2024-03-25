package com.example.data

import com.hazelcast.client.HazelcastClient
import com.hazelcast.collection.IQueue
import com.hazelcast.config.FileSystemXmlConfig
import com.hazelcast.core.Hazelcast
import com.hazelcast.core.HazelcastInstance
import com.hazelcast.map.IMap
import kotlinx.coroutines.delay

private const val CONFIG_PATH = "hazelcast.xml"
private const val LOGS_MAP_NAME = "logs"
private const val MESSAGES_QUEUE_NAME = "messages"

object HazelcastManager : DataManager() {
    private lateinit var hazelcastInstance: HazelcastInstance
    private lateinit var logs: IMap<String, String>
    private lateinit var messages: IQueue<String>

    fun startHazelcast() {
        hazelcastInstance = Hazelcast.newHazelcastInstance(
            FileSystemXmlConfig(LOCAL_DATA_ROOT_DIR + CONFIG_PATH)
        )
        logs = hazelcastInstance.getMap(LOGS_MAP_NAME)
        messages = hazelcastInstance.getQueue(MESSAGES_QUEUE_NAME)
    }

    fun stopHazelcast() {
        hazelcastInstance.shutdown()
    }

    fun getLogs(): List<String> {
        return logs.values.toList()
    }

    fun saveLog(uuid: String, text: String) {
        logs[uuid] = text
    }

    fun pushMessage(message: String) {
        if (!this::messages.isInitialized) {
            val client = HazelcastClient.newHazelcastClient()
            messages = client.getQueue(MESSAGES_QUEUE_NAME)
        }

        messages.add(message)
    }

    suspend fun readMessage(): String {
        if (!this::messages.isInitialized) {
            val client = HazelcastClient.newHazelcastClient()
            messages = client.getQueue(MESSAGES_QUEUE_NAME)
        }

        while (true) {
            val message = messages.poll()
            if (message != null) return message
            delay(100L)
        }
    }
}
