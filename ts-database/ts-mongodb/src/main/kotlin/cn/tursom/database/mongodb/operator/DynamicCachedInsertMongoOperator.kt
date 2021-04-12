package cn.tursom.database.mongodb.operator

import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

@Suppress("FunctionName", "unused")
fun <T : Any> DynamicCachedInsertMongoOperator(
  mongoOperator: MongoOperator<T>,
  executorService: ScheduledExecutorService,
  loadCheckDelay: Long = 100,
  loadCheckDelayTimeUnit: TimeUnit = TimeUnit.MILLISECONDS,
  ordered: Boolean = true,
  maxSaveCount: Int = 4096,
  maxDelayTimeMS: Long = 100,
  mainTransLoad: Int = 10,
  spareTransLoad: Int = 50,
  transWaitCount: Int = 10,
) = DynamicInsertMongoOperator(
  mongoOperator,
  CachedInsertMongoOperator(mongoOperator, ordered, maxSaveCount, maxDelayTimeMS),
  executorService,
  loadCheckDelay,
  loadCheckDelayTimeUnit,
  object : (DynamicInsertMongoOperator.WorkType, Int) -> DynamicInsertMongoOperator.WorkType {
    private var workLoop = AtomicInteger(0)
    override fun invoke(workType: DynamicInsertMongoOperator.WorkType, load: Int): DynamicInsertMongoOperator.WorkType {
      when (workType) {
        DynamicInsertMongoOperator.WorkType.MAIN -> if (load > spareTransLoad) {
          if (workLoop.incrementAndGet() > transWaitCount) {
            workLoop.set(0)
            return DynamicInsertMongoOperator.WorkType.SPARE
          }
        }
        DynamicInsertMongoOperator.WorkType.SPARE -> if (load < mainTransLoad) {
          if (workLoop.incrementAndGet() > transWaitCount) {
            workLoop.set(0)
            return DynamicInsertMongoOperator.WorkType.MAIN
          }
        }
      }
      return workType
    }
  }
)
