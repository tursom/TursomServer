package cn.tursom.database.mongodb.operator

import cn.tursom.log.Slf4j
import cn.tursom.log.impl.Slf4jImpl
import com.mongodb.client.model.InsertManyOptions
import com.mongodb.client.model.InsertOneOptions
import com.mongodb.client.result.InsertOneResult
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import org.bson.Document
import java.util.concurrent.atomic.AtomicInteger

class AsyncInsertMongoOperator<T : Any>(
  private val mongoOperator: MongoOperator<T>,
) : MongoOperator<T> by mongoOperator {
  companion object : Slf4j by Slf4jImpl() {
    private val acknowledged = InsertOneResult.acknowledged(null)
  }

  private val wroteAtomic = AtomicInteger(0)
  private val documentChannel: Channel<Document> = Channel(1024 * 256)

  val wrote get() = wroteAtomic.get()

  private val job = GlobalScope.launch {
    repeat(32) {
      launch {
        var list = ArrayList<Document>()
        var lastSave = System.currentTimeMillis()
        suspend fun save() {
          if (list.isEmpty()) return
          if (logger.isDebugEnabled) {
            logger.debug("begin to save document: {}", list)
          }
          var saveDocument = mongoOperator.saveDocument(list, InsertManyOptions().ordered(false))
          var retryTimes = 0
          while (saveDocument?.wasAcknowledged() != true && retryTimes < 3) {
            retryTimes++
            delay(100)
            saveDocument = mongoOperator.saveDocument(list)
          }
          list = ArrayList()
          lastSave = System.currentTimeMillis()
          saveDocument ?: return
          wroteAtomic.addAndGet(saveDocument.insertedIds.size)
          if (logger.isDebugEnabled) {
            logger.debug("save result: {}", saveDocument)
          }
        }
        @Suppress("EXPERIMENTAL_API_USAGE")
        while (!documentChannel.isClosedForReceive) {
          try {
            list.add(withTimeout(100) { documentChannel.receive() })
          } catch (e: TimeoutCancellationException) {
          } catch (e: ClosedReceiveChannelException) {
            break
          }
          if (list.size >= 1024 || System.currentTimeMillis() - lastSave > 100) {
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
    //insertOne(entity, options)
    if (logger.isDebugEnabled) {
      logger.debug("save object: {}, options: {}, result: {}", entity, options, insertOne(entity, options))
    }
    return true
  }

  override suspend fun saveDocument(document: Document, options: InsertOneOptions): InsertOneResult? {
    documentChannel.send(document)
    return acknowledged
  }

  override suspend fun insertOne(entity: T, options: InsertOneOptions): InsertOneResult? {
    documentChannel.send(convertToBson(entity))
    return acknowledged
  }

  override fun close() {
    documentChannel.close()
  }

  suspend fun join() {
    job.join()
  }
}