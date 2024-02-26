import com.hazelcast.client.HazelcastClient
import com.hazelcast.client.config.ClientConfig
import com.hazelcast.collection.IQueue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

suspend fun main() {
    val client = HazelcastClient.newHazelcastClient(ClientConfig().apply { clusterName = "dev" })
    val queue = client.getQueue<String>("b_queue").apply { clear() }

    val writeJob = writeToQueue(queue)
    val readJob = readFromQueue(queue, readers = 2)

    joinAll(writeJob, readJob)
    client.shutdown()
}

private fun writeToQueue(queue: IQueue<String>) = MainScope().launch(Dispatchers.IO) {
    for (i in 0..<100) {
        queue.put("item$i")
    }
    queue.put("done")
}

@Suppress("SameParameterValue")
private fun readFromQueue(queue: IQueue<String>, readers: Int) = MainScope().launch(Dispatchers.IO) {
    repeat(readers) {
        launch {
            while (true) {
                val item = queue.take()
                if (item == "done") {
                    queue.put("done")
                    break
                }
                println("Read $item")
            }
        }
    }
}
