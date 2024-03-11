package com.example.data

import com.hazelcast.config.FileSystemXmlConfig
import com.hazelcast.core.Hazelcast
import com.hazelcast.core.HazelcastInstance
import com.hazelcast.map.IMap

private const val CONFIG_PATH = "hazelcast.xml"
private const val LOGS_MAP_NAME = "logs"

object HazelcastManager : DataManager() {
    private lateinit var hazelcastInstance: HazelcastInstance
    private lateinit var logs: IMap<String, String>

    fun startHazelcast() {
        hazelcastInstance = Hazelcast.newHazelcastInstance(
            FileSystemXmlConfig(LOCAL_DATA_ROOT_DIR + CONFIG_PATH)
        )
        logs = hazelcastInstance.getMap(LOGS_MAP_NAME)
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
}
