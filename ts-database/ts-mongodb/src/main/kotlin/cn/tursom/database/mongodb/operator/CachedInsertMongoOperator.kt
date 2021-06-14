package cn.tursom.database.mongodb.operator

import com.mongodb.client.model.InsertManyOptions
import com.mongodb.client.model.InsertOneOptions
import com.mongodb.client.result.InsertOneResult
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import org.bson.Document
import java.util.concurrent.atomic.AtomicInteger

class CachedInsertMongoOperator<T : Any>(
  private val mongoOperator: MongoOperator<T>,
  private val ordered: Boolean = true,
  private val maxSaveCount: Int = 4096,
  private val maxDelayTimeMS: Long = 100,
) : MongoOperator<T> by mongoOperator {
  companion object {
    private val unacknowledged = InsertOneResult.unacknowledged()
  }

  private val documentChannel: Channel<Pair<Channel<InsertOneResult?>, Document>> = Channel(1024 * 256)
  private val wroteAtomic = AtomicInteger(0)
  private val received = AtomicInteger(0)
  val wrote get() = wroteAtomic.get()

  private val job = GlobalScope.launch {
    repeat(32) {
      launch {
        var list = ArrayList<Pair<Channel<InsertOneResult?>, Document>>()
        var lastSave = System.currentTimeMillis()
        suspend fun save() {
          var insertManyResult = saveDocument(
            list.map(Pair<Channel<InsertOneResult?>, Document>::second),
            InsertManyOptions().ordered(ordered)
          )
          var retryTimes = 0
          while (insertManyResult?.wasAcknowledged() != true && retryTimes < 3) {
            retryTimes++
            delay(100)
            insertManyResult = saveDocument(
              list.map(Pair<Channel<InsertOneResult?>, Document>::second),
              InsertManyOptions().ordered(ordered)
            )
          }
          if (insertManyResult?.wasAcknowledged() == true) {
            wroteAtomic.addAndGet(insertManyResult.insertedIds.size)
            list.forEachIndexed { index, (channel) ->
              channel.send(InsertOneResult.acknowledged(insertManyResult.insertedIds[index]))
            }
          } else {
            list.forEach { (channel) ->
              channel.send(unacknowledged)
            }
          }
          list = ArrayList()
          lastSave = System.currentTimeMillis()
        }
        @Suppress("EXPERIMENTAL_API_USAGE")
        while (!documentChannel.isClosedForReceive) {
          try {
            list.add(documentChannel.poll() ?: withTimeout(maxDelayTimeMS) { documentChannel.receive() })
            received.incrementAndGet()
          } catch (e: TimeoutCancellationException) {
          } catch (e: ClosedReceiveChannelException) {
            //println("closed")
            break
          }
          if (list.size >= maxSaveCount || System.currentTimeMillis() - lastSave > maxDelayTimeMS) {
            save()
          }
        }
        if (list.size > 0) {
          save()
        }
      }
    }
  }

  override suspend fun save(entity: T, options: InsertOneOptions): Boolean {
    return insertOne(entity, options)?.wasAcknowledged() ?: false
  }

  override suspend fun saveDocument(document: Document, options: InsertOneOptions): InsertOneResult? {
    val channel = Channel<InsertOneResult?>()
    documentChannel.send(channel to document)
    return channel.receive()
  }

  override suspend fun insertOne(entity: T, options: InsertOneOptions): InsertOneResult? {
    val channel = Channel<InsertOneResult?>()
    documentChannel.send(channel to convertToBson(entity))
    return channel.receive()
  }

  override fun close() {
    documentChannel.close()
    mongoOperator.close()
  }

  suspend fun join() {
    job.join()
  }
}