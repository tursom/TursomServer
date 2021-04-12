package cn.tursom.database.mongodb.operator

import com.mongodb.client.model.InsertOneOptions
import com.mongodb.client.result.InsertOneResult
import org.bson.Document
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class DynamicInsertMongoOperator<T : Any>(
  private val mainMongoOperator: MongoOperator<T>,
  private val spareMongoOperator: MongoOperator<T>,
  private val executorService: ScheduledExecutorService,
  loadCheckDelay: Long = 100,
  loadCheckDelayTimeUnit: TimeUnit = TimeUnit.MILLISECONDS,
  private val workTypeCheck: (workType: WorkType, load: Int) -> WorkType,
) : MongoOperator<T> by mainMongoOperator {
  enum class WorkType {
    MAIN, SPARE
  }

  var workType = WorkType.MAIN
  private val workLoad = AtomicInteger(0)

  init {
    executorService.scheduleWithFixedDelay({
      workType = workTypeCheck(workType, workLoad.getAndSet(0))
    }, loadCheckDelay, loadCheckDelay, loadCheckDelayTimeUnit)
  }

  private val workMongoOperator
    get() = when (workType) {
      WorkType.MAIN -> mainMongoOperator
      WorkType.SPARE -> spareMongoOperator
    }

  override suspend fun save(entity: T, options: InsertOneOptions): Boolean {
    workLoad.incrementAndGet()
    return workMongoOperator.save(entity, options)
  }

  override suspend fun saveDocument(document: Document, options: InsertOneOptions): InsertOneResult? {
    workLoad.incrementAndGet()
    return workMongoOperator.saveDocument(document, options)
  }

  override suspend fun insertOne(entity: T, options: InsertOneOptions): InsertOneResult? {
    workLoad.incrementAndGet()
    return workMongoOperator.insertOne(entity, options)
  }

  override fun close() {
    try {
      mainMongoOperator.close()
    } catch (e: Exception) {
    }
    try {
      spareMongoOperator.close()
    } catch (e: Exception) {
    }
  }
}