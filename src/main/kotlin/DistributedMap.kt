import com.hazelcast.client.HazelcastClient
import com.hazelcast.client.config.ClientConfig
import com.hazelcast.map.IMap
import kotlinx.coroutines.*

private const val NUM_INCREMENT_CLIENTS = 3

suspend fun main() {
    val client = HazelcastClient.newHazelcastClient(ClientConfig().apply { clusterName = "dev" })
    val map1K = client.getMap<String, String>("map").apply { clear() }

    for (i in 0..<1000)
        map1K.put("$i", "value$i")

    val mapIncrement = client.getMap<String, String>("map_increment")
    val jobs = mutableListOf<Job>()

    mapIncrement.clear()
    for (i in 0..<NUM_INCREMENT_CLIENTS) {
        increment(mapIncrement).also { jobs.add(it) }
    }
    jobs.apply {
        joinAll()
        clear()
    }
    println("Final value no lock: ${mapIncrement["key"]}")

    mapIncrement.clear()
    for (i in 0..<NUM_INCREMENT_CLIENTS) {
        incrementPessimisticLock(mapIncrement).also { jobs.add(it) }
    }
    jobs.apply {
        joinAll()
        clear()
    }
    println("Final value pessimistic lock: ${mapIncrement["key"]}")

    mapIncrement.clear()
    for (i in 0..<NUM_INCREMENT_CLIENTS) {
        incrementOptimisticLock(mapIncrement).also { jobs.add(it) }
    }
    jobs.apply {
        joinAll()
        clear()
    }
    println("Final value optimistic lock: ${mapIncrement["key"]}")

    client.shutdown()
}

private fun increment(map: IMap<String, String>) = MainScope().launch(Dispatchers.Default) {
    map.putIfAbsent("key", "0")
    for (i in 0..9_999) {
        val value = map["key"]?.toInt() ?: 0
        map.put("key", "${value + 1}")
    }
}

private fun incrementPessimisticLock(map: IMap<String, String>) = MainScope().launch(Dispatchers.Default) {
    map.putIfAbsent("key", "0")
    for (i in 0..9_999) {
        map.lock("key")
        try {
            val value = map["key"]?.toInt() ?: 0
            map.put("key", "${value + 1}")
        } finally {
            map.unlock("key")
        }
    }
}

private fun incrementOptimisticLock(map: IMap<String, String>) = MainScope().launch(Dispatchers.Default) {
    map.putIfAbsent("key", "0")
    for (i in 0..9_999) {
        while (true) {
            val value = map["key"]?.toInt() ?: 0
            if (map.replace("key", value.toString(), "${value + 1}")) {
                break
            }
        }
    }
}
