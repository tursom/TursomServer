package cn.tursom.database.redis

import cn.tursom.core.ScheduledExecutorPool
import cn.tursom.core.uncheckedCast
import cn.tursom.log.Slf4j
import cn.tursom.log.impl.Slf4jImpl
import redis.clients.jedis.*
import redis.clients.jedis.commands.ProtocolCommand
import redis.clients.jedis.params.GeoRadiusParam
import redis.clients.jedis.params.SetParams
import redis.clients.jedis.params.ZAddParams
import redis.clients.jedis.params.ZIncrByParams
import java.io.Closeable
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * 将Jedis的pipeline封装成协程操作的类
 * Jedis的pipeline可以在巨量（十万以上）并发时提供极高（6至7倍）的吞吐量，这对提高系统并发非常有帮助
 */
@Suppress("unused", "SpellCheckingInspection", "MemberVisibilityCanBePrivate", "CanBeParameter")
class AsyncJedis(
  val jedisPool: JedisPool,
  @Volatile
  var syncDelay: Long = SYNC_DELAY,
  var maxDelayCount: Int = MAX_DELAY_COUNT,
  val syncCycle: Long = 1000,
) : Closeable {
  companion object : Slf4j by Slf4jImpl() {
    const val SYNC_DELAY = 0L
    const val MAX_DELAY_COUNT = 1000

    fun Pipeline.getPipelinedResponseLength(): Int = getPipelinedResponseLength.invoke(this).uncheckedCast()

    private val threadId = AtomicInteger(0)
    private val threadPool = ScheduledExecutorPool {
      val thread = Thread(it)
      thread.name = "AsyncJedisWorker-${threadId.incrementAndGet()}"
      thread.isDaemon = true
      thread
    }
    private val responseSet = Response::class.java.getDeclaredField("set").apply {
      isAccessible = true
    }
    private val getPipelinedResponseLength =
      Queable::class.java.getDeclaredMethod("getPipelinedResponseLength").apply {
        isAccessible = true
      }
    private val Response<*>.set: Boolean get() = responseSet[this].uncheckedCast()
    private val pipelineClientField = MultiKeyPipelineBase::class.java.getDeclaredField("client").apply {
      isAccessible = true
    }
    private val Pipeline.client get() = pipelineClientField.get(this).uncheckedCast<Client>()
  }

  private val syncThread: Thread
  private val syncExecutor: ScheduledExecutorService
  private val workExecutor: ScheduledExecutorService
  private val taskLock = ReentrantLock()
  private val pipelines = ConcurrentLinkedDeque<Pipeline>()
  private val tasks = ConcurrentLinkedDeque<() -> Boolean>()
  private val handleRequest = AtomicInteger(0)

  init {
    val workThreadAndExecutor = threadPool.get()
    syncThread = workThreadAndExecutor.first
    syncExecutor = workThreadAndExecutor.second
    workExecutor = syncExecutor
    //workExecutor = threadPool.get().second
    pipelines.add(jedisPool.resource.pipelined())
  }

  private val updateSyncDelayFuture =
    syncExecutor.scheduleWithFixedDelay(::updateSyncDelay, 100, 100, TimeUnit.MILLISECONDS)
  private val scheduledFuture =
    syncExecutor.scheduleWithFixedDelay(::sync, syncCycle, syncCycle, TimeUnit.MILLISECONDS)
  private val syncing = AtomicBoolean(false)

  override fun close() {
    syncNow()
    scheduledFuture.cancel(false)
    updateSyncDelayFuture.cancel(true)
  }

  /**
   * 通知AsyncJedis进行管道数据刷新同步，非阻塞
   */
  fun sync() {
    // 判断是否有正在运行中的任务
    if (tasks.isEmpty() && pipelines.none { pipeline -> pipeline.getPipelinedResponseLength() != 0 }) {
      if (Thread.currentThread() == syncThread) {
        syncing.set(false)
      }
      return
    }
    // 判断当前线程是否是工作线程
    // 管道的同步是线程不安全的代码，只能在单线程中运行
    if (Thread.currentThread() != syncThread) {
      // 判断是否已经有同步任务存在
      if (syncing.compareAndSet(false, true)) {
        // 提交同步请求
        syncExecutor.execute(::sync)
      }
      return
    }
    // 计算正在提交的任务数是否超过缓冲上限
    if (syncDelay != 0L && pipelines.sumOf { pipeline -> pipeline.getPipelinedResponseLength() } < maxDelayCount) {
      // 没有超过缓冲上限，则延迟后进行刷新
      syncExecutor.schedule(::syncNow, syncDelay, TimeUnit.SECONDS)
    } else {
      // 已经超过缓冲上限，马上进行刷新
      syncNow()
    }
  }

  /**
   * 立即进行数据同步操作
   * 线程不安全，只能在工作线程中运行
   */
  private fun syncNow() {
    trace("sync now ${Thread.currentThread().name} ${syncThread.name} ${Thread.currentThread() == syncThread} ${pipelines.size}")
    // 如果当前线程不是工作线程，则向工作线程提交任务
    if (Thread.currentThread() != syncThread) {
      trace("current thread is not work thread")
      syncExecutor.execute(::syncNow)
      return
    }
    taskLock.withLock {
      debug("sync get lock ${Thread.currentThread().name} ${syncThread.name} ${Thread.currentThread() == syncThread} ${pipelines.size}")
      try {
        val resource = jedisPool.resource
        pipelines.addFirst(resource.pipelined())
        resource.close()
        val iterator = pipelines.iterator()
        iterator.next()
        // 遍历管道列表，同步数据
        iterator.forEach { pipeline ->
          debug(
            "AsyncJedis command syncing, pipeline remain {}, tasks size: {} ",
            pipeline.getPipelinedResponseLength(),
            tasks.size
          )
          pipeline.sync()
          debug("pipeline sync finished, remain {}", pipeline.getPipelinedResponseLength())
          // 如果所有数据皆已同步
          if (pipeline.getPipelinedResponseLength() == 0) {
            iterator.remove()
            pipeline.close()
          }
        }
      } catch (e: Exception) {
        error("an exception caused on pipeline sync", e)
      }
    }
    //workExecutor.execute {
    // 遍历任务列表，清除已完成的任务
    val notHandledTasks = ArrayList<() -> Boolean>()
    var task = tasks.poll()
    while (task != null) {
      debug("task {} handle", task)
      if (!task()) {
        notHandledTasks.add(task)
      }
      task = tasks.poll()
    }
    tasks.addAll(notHandledTasks)

    debug("sync finished")
    syncing.set(false)
    //}
  }

  private var lowerTime = 0
  private fun updateSyncDelay() {
    val handleRequest = handleRequest.getAndSet(0)
    if (handleRequest > 10000) {
      syncDelay = 5
      lowerTime = 0
    } else {
      if (++lowerTime > 10) {
        syncDelay = 0
      }
    }
    //println("${ThreadLocalSimpleDateFormat.cn.format(System.currentTimeMillis())} handle request per second: $handleRequest, sync delay: $syncDelay")
  }

  suspend operator fun <T> invoke(action: (Pipeline) -> Response<T>): T {
    return suspendCoroutine { cont ->
      taskLock.withLock {
        val pipeline = pipelines.first
        val response = action(pipeline)
        tasks.add {
          if (response.set) {
            handleRequest.incrementAndGet()
            cont.resume(response.get())
            true
          } else {
            false
          }
        }
        sync()
      }
    }
  }

  operator fun <T> invoke(action: (Pipeline) -> Response<T>, onComplete: (T) -> Unit) {
    taskLock.withLock {
      val pipeline = pipelines.first
      val response = action(pipeline)
      tasks.add {
        if (response.set) {
          handleRequest.incrementAndGet()
          onComplete(response.get())
          true
        } else {
          false
        }
      }
      sync()
    }
  }

  suspend fun append(key: String, value: String): Long? = invoke {
    it.append(key, value)
  }

  suspend fun append(key: ByteArray, value: ByteArray): Long? = invoke {
    it.append(key, value)
  }

  suspend fun blpop(key: String): List<String>? = invoke {
    it.blpop(key)
  }

  suspend fun brpop(key: String): List<String>? = invoke {
    it.blpop(key)
  }

  suspend fun blpop(key: ByteArray): List<ByteArray>? = invoke {
    it.blpop(key)
  }

  suspend fun brpop(key: ByteArray): List<ByteArray>? = invoke {
    it.blpop(key)
  }

  suspend fun decr(key: String): Long? = invoke {
    it.decr(key)
  }

  suspend fun decr(key: ByteArray): Long? = invoke {
    it.decr(key)
  }

  suspend fun decrBy(key: String, decrement: Long): Long? = invoke {
    it.decrBy(key, decrement)
  }

  suspend fun decrBy(key: ByteArray, decrement: Long): Long? = invoke {
    it.decrBy(key, decrement)
  }

  suspend fun del(key: String): Long? = invoke {
    it.del(key)
  }

  suspend fun del(key: ByteArray): Long? = invoke {
    it.del(key)
  }

  suspend fun unlink(key: String): Long? = invoke {
    it.unlink(key)
  }

  suspend fun unlink(key: ByteArray): Long? = invoke {
    it.unlink(key)
  }

  suspend fun echo(string: String): String? = invoke {
    it.echo(string)
  }

  suspend fun echo(string: ByteArray): ByteArray? = invoke {
    it.echo(string)
  }

  suspend fun exists(key: String): Boolean? = invoke {
    it.exists(key)
  }

  suspend fun exists(key: ByteArray): Boolean? = invoke {
    it.exists(key)
  }

  suspend fun expire(key: String, seconds: Int): Long? = invoke {
    it.expire(key, seconds)
  }

  suspend fun expire(key: ByteArray, seconds: Int): Long? = invoke {
    it.expire(key, seconds)
  }

  suspend fun expireAt(key: String, unixTime: Long): Long? = invoke {
    it.expireAt(key, unixTime)
  }

  suspend fun expireAt(key: ByteArray, unixTime: Long): Long? = invoke {
    it.expireAt(key, unixTime)
  }

  suspend fun get(key: String): String? = invoke {
    it.get(key)
  }

  suspend fun get(key: ByteArray): ByteArray? = invoke {
    it.get(key)
  }

  suspend fun getbit(key: String, offset: Long): Boolean? = invoke {
    it.getbit(key, offset)
  }

  suspend fun getbit(key: ByteArray, offset: Long): Boolean? = invoke {
    it.getbit(key, offset)
  }

  suspend fun bitpos(key: String, value: Boolean): Long? = invoke {
    it.bitpos(key, value)
  }

  suspend fun bitpos(key: String, value: Boolean, params: BitPosParams): Long? = invoke {
    it.bitpos(key, value, params)
  }

  suspend fun bitpos(key: ByteArray, value: Boolean): Long? = invoke {
    it.bitpos(key, value)
  }

  suspend fun bitpos(key: ByteArray, value: Boolean, params: BitPosParams): Long? = invoke {
    it.bitpos(key, value, params)
  }

  suspend fun getrange(key: String, startOffset: Long, endOffset: Long): String? = invoke {
    it.getrange(key, startOffset, endOffset)
  }

  suspend fun getSet(key: String, value: String): String? = invoke {
    it.getSet(key, value)
  }

  suspend fun getSet(key: ByteArray, value: ByteArray): ByteArray? = invoke {
    it.getSet(key, value)
  }

  suspend fun getrange(key: ByteArray, startOffset: Long, endOffset: Long): ByteArray? = invoke {
    it.getrange(key, startOffset, endOffset)
  }

  suspend fun hdel(key: String, vararg field: String): Long? = invoke {
    it.hdel(key, *field)
  }

  suspend fun hdel(key: ByteArray, vararg field: ByteArray): Long? = invoke {
    it.hdel(key, *field)
  }

  suspend fun hexists(key: String, field: String): Boolean? = invoke {
    it.hexists(key, field)
  }

  suspend fun hexists(key: ByteArray, field: ByteArray): Boolean? = invoke {
    it.hexists(key, field)
  }

  suspend fun hget(key: String, field: String): String? = invoke {
    it.hget(key, field)
  }

  suspend fun hget(key: ByteArray, field: ByteArray): ByteArray? = invoke {
    it.hget(key, field)
  }

  suspend fun hgetAll(key: String): Map<String, String>? = invoke {
    it.hgetAll(key)
  }

  suspend fun hgetAll(key: ByteArray): Map<ByteArray, ByteArray>? = invoke {
    it.hgetAll(key)
  }

  suspend fun hincrBy(key: String, field: String, value: Long): Long? = invoke {
    it.hincrBy(key, field, value)
  }

  suspend fun hincrBy(key: ByteArray, field: ByteArray, value: Long): Long? = invoke {
    it.hincrBy(key, field, value)
  }

  suspend fun hkeys(key: String): Set<String>? = invoke {
    it.hkeys(key)
  }

  suspend fun hkeys(key: ByteArray): Set<ByteArray>? = invoke {
    it.hkeys(key)
  }

  suspend fun hlen(key: String): Long? = invoke {
    it.hlen(key)
  }

  suspend fun hlen(key: ByteArray): Long? = invoke {
    it.hlen(key)
  }

  suspend fun hmget(key: String, vararg fields: String): List<String>? = invoke {
    it.hmget(key, *fields)
  }

  suspend fun hmget(key: ByteArray, vararg fields: ByteArray): List<ByteArray>? = invoke {
    it.hmget(key, *fields)
  }

  suspend fun hmset(key: String, hash: Map<String, String>): String? = invoke {
    it.hmset(key, hash)
  }

  suspend fun hmset(key: ByteArray, hash: Map<ByteArray, ByteArray>): String? = invoke {
    it.hmset(key, hash)
  }

  suspend fun hset(key: String, field: String, value: String): Long? = invoke {
    it.hset(key, field, value)
  }

  suspend fun hset(key: ByteArray, field: ByteArray, value: ByteArray): Long? = invoke {
    it.hset(key, field, value)
  }

  suspend fun hset(key: String, hash: Map<String, String>): Long? = invoke {
    it.hset(key, hash)
  }

  suspend fun hset(key: ByteArray, hash: Map<ByteArray, ByteArray>): Long? = invoke {
    it.hset(key, hash)
  }

  suspend fun hsetnx(key: String, field: String, value: String): Long? = invoke {
    it.hsetnx(key, field, value)
  }

  suspend fun hsetnx(key: ByteArray, field: ByteArray, value: ByteArray): Long? = invoke {
    it.hsetnx(key, field, value)
  }

  suspend fun hvals(key: String): List<String>? = invoke {
    it.hvals(key)
  }

  suspend fun hvals(key: ByteArray): List<ByteArray>? = invoke {
    it.hvals(key)
  }

  suspend fun incr(key: String): Long? = invoke {
    it.incr(key)
  }

  suspend fun incr(key: ByteArray): Long? = invoke {
    it.incr(key)
  }

  suspend fun incrBy(key: String, increment: Long): Long? = invoke {
    it.incrBy(key, increment)
  }

  suspend fun incrBy(key: ByteArray, increment: Long): Long? = invoke {
    it.incrBy(key, increment)
  }

  suspend fun lindex(key: String, index: Long): String? = invoke {
    it.lindex(key, index)
  }

  suspend fun lindex(key: ByteArray, index: Long): ByteArray? = invoke {
    it.lindex(key, index)
  }

  suspend fun linsert(key: String, where: ListPosition, pivot: String, value: String): Long? = invoke {
    it.linsert(key, where, pivot, value)
  }

  suspend fun linsert(key: ByteArray, where: ListPosition, pivot: ByteArray, value: ByteArray): Long? = invoke {
    it.linsert(key, where, pivot, value)
  }

  suspend fun llen(key: String): Long? = invoke {
    it.llen(key)
  }

  suspend fun llen(key: ByteArray): Long? = invoke {
    it.llen(key)
  }

  suspend fun lpop(key: String): String? = invoke {
    it.lpop(key)
  }

  suspend fun lpop(key: ByteArray): ByteArray? = invoke {
    it.lpop(key)
  }

  suspend fun lpush(key: String, vararg string: String): Long? = invoke {
    it.lpush(key, *string)
  }

  suspend fun lpush(key: ByteArray, vararg string: ByteArray): Long? = invoke {
    it.lpush(key, *string)
  }

  suspend fun lpushx(key: String, vararg string: String): Long? = invoke {
    it.lpushx(key, *string)
  }

  suspend fun lpushx(key: ByteArray, vararg bytes: ByteArray): Long? = invoke {
    it.lpushx(key, *bytes)
  }

  suspend fun lrange(key: String, start: Long, stop: Long): List<String>? = invoke {
    it.lrange(key, start, stop)
  }

  suspend fun lrange(key: ByteArray, start: Long, stop: Long): List<ByteArray>? = invoke {
    it.lrange(key, start, stop)
  }

  suspend fun lrem(key: String, count: Long, value: String): Long? = invoke {
    it.lrem(key, count, value)
  }

  suspend fun lrem(key: ByteArray, count: Long, value: ByteArray): Long? = invoke {
    it.lrem(key, count, value)
  }

  suspend fun lset(key: String, index: Long, value: String): String? = invoke {
    it.lset(key, index, value)
  }

  suspend fun lset(key: ByteArray, index: Long, value: ByteArray): String? = invoke {
    it.lset(key, index, value)
  }

  suspend fun ltrim(key: String, start: Long, stop: Long): String? = invoke {
    it.ltrim(key, start, stop)
  }

  suspend fun ltrim(key: ByteArray, start: Long, stop: Long): String? = invoke {
    it.ltrim(key, start, stop)
  }

  suspend fun move(key: String, dbIndex: Int): Long? = invoke {
    it.move(key, dbIndex)
  }

  suspend fun move(key: ByteArray, dbIndex: Int): Long? = invoke {
    it.move(key, dbIndex)
  }

  suspend fun persist(key: String): Long? = invoke {
    it.persist(key)
  }

  suspend fun persist(key: ByteArray): Long? = invoke {
    it.persist(key)
  }

  suspend fun rpop(key: String): String? = invoke {
    it.rpop(key)
  }

  suspend fun rpop(key: ByteArray): ByteArray? = invoke {
    it.rpop(key)
  }

  suspend fun rpush(key: String, vararg string: String): Long? = invoke {
    it.rpush(key, *string)
  }

  suspend fun rpush(key: ByteArray, vararg string: ByteArray): Long? = invoke {
    it.rpush(key, *string)
  }

  suspend fun rpushx(key: String, vararg string: String): Long? = invoke {
    it.rpushx(key, *string)
  }

  suspend fun rpushx(key: ByteArray, vararg string: ByteArray): Long? = invoke {
    it.rpushx(key, *string)
  }

  suspend fun sadd(key: String, vararg member: String): Long? = invoke {
    it.sadd(key, *member)
  }

  suspend fun sadd(key: ByteArray, vararg member: ByteArray): Long? = invoke {
    it.sadd(key, *member)
  }

  suspend fun scard(key: String): Long? = invoke {
    it.scard(key)
  }

  suspend fun scard(key: ByteArray): Long? = invoke {
    it.scard(key)
  }

  suspend fun set(key: String, value: String): String? = invoke {
    it.set(key, value)
  }

  suspend fun set(key: ByteArray, value: ByteArray): String? = invoke {
    it.set(key, value)
  }

  suspend fun set(key: String, value: String, params: SetParams): String? = invoke {
    it.set(key, value, params)
  }

  suspend fun set(key: ByteArray, value: ByteArray, params: SetParams): String? = invoke {
    it.set(key, value, params)
  }

  suspend fun setbit(key: String, offset: Long, value: Boolean): Boolean? = invoke {
    it.setbit(key, offset, value)
  }

  suspend fun setbit(key: ByteArray, offset: Long, value: ByteArray): Boolean? = invoke {
    it.setbit(key, offset, value)
  }

  suspend fun setex(key: String, seconds: Int, value: String): String? = invoke {
    it.setex(key, seconds, value)
  }

  suspend fun setex(key: ByteArray, seconds: Int, value: ByteArray): String? = invoke {
    it.setex(key, seconds, value)
  }

  suspend fun setnx(key: String, value: String): Long? = invoke {
    it.setnx(key, value)
  }

  suspend fun setnx(key: ByteArray, value: ByteArray): Long? = invoke {
    it.setnx(key, value)
  }

  suspend fun setrange(key: String, offset: Long, value: String): Long? = invoke {
    it.setrange(key, offset, value)
  }

  suspend fun setrange(key: ByteArray, offset: Long, value: ByteArray): Long? = invoke {
    it.setrange(key, offset, value)
  }

  suspend fun sismember(key: String, member: String): Boolean? = invoke {
    it.sismember(key, member)
  }

  suspend fun sismember(key: ByteArray, member: ByteArray): Boolean? = invoke {
    it.sismember(key, member)
  }

  suspend fun smembers(key: String): Set<String>? = invoke {
    it.smembers(key)
  }

  suspend fun smembers(key: ByteArray): Set<ByteArray>? = invoke {
    it.smembers(key)
  }

  suspend fun sort(key: String): List<String>? = invoke {
    it.sort(key)
  }

  suspend fun sort(key: ByteArray): List<ByteArray>? = invoke {
    it.sort(key)
  }

  suspend fun sort(key: String, sortingParameters: SortingParams): List<String>? = invoke {
    it.sort(key, sortingParameters)
  }

  suspend fun sort(key: ByteArray, sortingParameters: SortingParams): List<ByteArray>? = invoke {
    it.sort(key, sortingParameters)
  }

  suspend fun spop(key: String): String? = invoke {
    it.spop(key)
  }

  suspend fun spop(key: String, count: Long): Set<String>? = invoke {
    it.spop(key, count)
  }

  suspend fun spop(key: ByteArray): ByteArray? = invoke {
    it.spop(key)
  }

  suspend fun spop(key: ByteArray, count: Long): Set<ByteArray>? = invoke {
    it.spop(key, count)
  }

  suspend fun srandmember(key: String): String? = invoke {
    it.srandmember(key)
  }

  suspend fun srandmember(key: String, count: Int): List<String>? = invoke {
    it.srandmember(key, count)
  }

  suspend fun srandmember(key: ByteArray): ByteArray? = invoke {
    it.srandmember(key)
  }

  suspend fun srandmember(key: ByteArray, count: Int): List<ByteArray>? = invoke {
    it.srandmember(key, count)
  }

  suspend fun srem(key: String, vararg member: String): Long? = invoke {
    it.srem(key, *member)
  }

  suspend fun srem(key: ByteArray, vararg member: ByteArray): Long? = invoke {
    it.srem(key, *member)
  }

  suspend fun strlen(key: String): Long? = invoke {
    it.strlen(key)
  }

  suspend fun strlen(key: ByteArray): Long? = invoke {
    it.strlen(key)
  }

  suspend fun substr(key: String, start: Int, end: Int): String? = invoke {
    it.substr(key, start, end)
  }

  suspend fun substr(key: ByteArray, start: Int, end: Int): String? = invoke {
    it.substr(key, start, end)
  }

  suspend fun touch(key: String): Long? = invoke {
    it.touch(key)
  }

  suspend fun touch(key: ByteArray): Long? = invoke {
    it.touch(key)
  }

  suspend fun ttl(key: String): Long? = invoke {
    it.ttl(key)
  }

  suspend fun ttl(key: ByteArray): Long? = invoke {
    it.ttl(key)
  }

  suspend fun type(key: String): String? = invoke {
    it.type(key)
  }

  suspend fun type(key: ByteArray): String? = invoke {
    it.type(key)
  }

  suspend fun zadd(key: String, score: Double, member: String): Long? = invoke {
    it.zadd(key, score, member)
  }

  suspend fun zadd(key: String, score: Double, member: String, params: ZAddParams): Long? = invoke {
    it.zadd(key, score, member, params)
  }

  suspend fun zadd(key: String, scoreMembers: Map<String, Double>): Long? = invoke {
    it.zadd(key, scoreMembers)
  }

  suspend fun zadd(key: String, scoreMembers: Map<String, Double>, params: ZAddParams): Long? = invoke {
    it.zadd(key, scoreMembers, params)
  }

  suspend fun zadd(key: ByteArray, score: Double, member: ByteArray): Long? = invoke {
    it.zadd(key, score, member)
  }

  suspend fun zadd(key: ByteArray, score: Double, member: ByteArray, params: ZAddParams): Long? = invoke {
    it.zadd(key, score, member, params)
  }

  suspend fun zadd(key: ByteArray, scoreMembers: Map<ByteArray, Double>): Long? = invoke {
    it.zadd(key, scoreMembers)
  }

  suspend fun zadd(key: ByteArray, scoreMembers: Map<ByteArray, Double>, params: ZAddParams): Long? = invoke {
    it.zadd(key, scoreMembers, params)
  }

  suspend fun zcard(key: String): Long? = invoke {
    it.zcard(key)
  }

  suspend fun zcard(key: ByteArray): Long? = invoke {
    it.zcard(key)
  }

  suspend fun zcount(key: String, min: Double, max: Double): Long? = invoke {
    it.zcount(key, min, max)
  }

  suspend fun zcount(key: String, min: String, max: String): Long? = invoke {
    it.zcount(key, min, max)
  }

  suspend fun zcount(key: ByteArray, min: Double, max: Double): Long? = invoke {
    it.zcount(key, min, max)
  }

  suspend fun zcount(key: ByteArray, min: ByteArray, max: ByteArray): Long? = invoke {
    it.zcount(key, min, max)
  }

  suspend fun zincrby(key: String, increment: Double, member: String): Double? = invoke {
    it.zincrby(key, increment, member)
  }

  suspend fun zincrby(key: String, increment: Double, member: String, params: ZIncrByParams): Double? = invoke {
    it.zincrby(key, increment, member, params)
  }

  suspend fun zincrby(key: ByteArray, increment: Double, member: ByteArray): Double? = invoke {
    it.zincrby(key, increment, member)
  }

  suspend fun zincrby(key: ByteArray, increment: Double, member: ByteArray, params: ZIncrByParams): Double? = invoke {
    it.zincrby(key, increment, member, params)
  }

  suspend fun zrange(key: String, start: Long, stop: Long): Set<String>? = invoke {
    it.zrange(key, start, stop)
  }

  suspend fun zrange(key: ByteArray, start: Long, stop: Long): Set<ByteArray>? = invoke {
    it.zrange(key, start, stop)
  }

  suspend fun zrangeByScore(key: String, min: Double, max: Double): Set<String>? = invoke {
    it.zrangeByScore(key, min, max)
  }

  suspend fun zrangeByScore(key: ByteArray, min: Double, max: Double): Set<ByteArray>? = invoke {
    it.zrangeByScore(key, min, max)
  }

  suspend fun zrangeByScore(key: String, min: String, max: String): Set<String>? = invoke {
    it.zrangeByScore(key, min, max)
  }

  suspend fun zrangeByScore(key: ByteArray, min: ByteArray, max: ByteArray): Set<ByteArray>? = invoke {
    it.zrangeByScore(key, min, max)
  }

  suspend fun zrangeByScore(
    key: String, min: Double, max: Double, offset: Int,
    count: Int,
  ): Set<String>? = invoke {
    it.zrangeByScore(key, min, max, offset, count)
  }

  suspend fun zrangeByScore(
    key: String, min: String, max: String, offset: Int,
    count: Int,
  ): Set<String>? = invoke {
    it.zrangeByScore(key, min, max, offset, count)
  }

  suspend fun zrangeByScore(
    key: ByteArray, min: Double, max: Double, offset: Int,
    count: Int,
  ): Set<ByteArray>? = invoke {
    it.zrangeByScore(key, min, max, offset, count)
  }

  suspend fun zrangeByScore(
    key: ByteArray, min: ByteArray, max: ByteArray, offset: Int,
    count: Int,
  ): Set<ByteArray>? = invoke {
    it.zrangeByScore(key, min, max, offset, count)
  }

  suspend fun zrangeByScoreWithScores(key: String, min: Double, max: Double): Set<Tuple>? = invoke {
    it.zrangeByScoreWithScores(key, min, max)
  }

  suspend fun zrangeByScoreWithScores(key: String, min: String, max: String): Set<Tuple>? = invoke {
    it.zrangeByScoreWithScores(key, min, max)
  }

  suspend fun zrangeByScoreWithScores(key: ByteArray, min: Double, max: Double): Set<Tuple>? = invoke {
    it.zrangeByScoreWithScores(key, min, max)
  }

  suspend fun zrangeByScoreWithScores(key: ByteArray, min: ByteArray, max: ByteArray): Set<Tuple>? = invoke {
    it.zrangeByScoreWithScores(key, min, max)
  }

  suspend fun zrangeByScoreWithScores(
    key: String, min: Double, max: Double,
    offset: Int, count: Int,
  ): Set<Tuple>? = invoke {
    it.zrangeByScoreWithScores(key, min, max, offset, count)
  }

  suspend fun zrangeByScoreWithScores(
    key: String, min: String, max: String,
    offset: Int, count: Int,
  ): Set<Tuple>? = invoke {
    it.zrangeByScoreWithScores(key, min, max, offset, count)
  }

  suspend fun zrangeByScoreWithScores(
    key: ByteArray, min: Double, max: Double,
    offset: Int, count: Int,
  ): Set<Tuple>? = invoke {
    it.zrangeByScoreWithScores(key, min, max, offset, count)
  }

  suspend fun zrangeByScoreWithScores(
    key: ByteArray, min: ByteArray, max: ByteArray,
    offset: Int, count: Int,
  ): Set<Tuple>? = invoke {
    it.zrangeByScoreWithScores(key, min, max, offset, count)
  }

  suspend fun zrevrangeByScore(key: String, max: Double, min: Double): Set<String>? = invoke {
    it.zrevrangeByScore(key, max, min)
  }

  suspend fun zrevrangeByScore(key: ByteArray, max: Double, min: Double): Set<ByteArray>? = invoke {
    it.zrevrangeByScore(key, max, min)
  }

  suspend fun zrevrangeByScore(key: String, max: String, min: String): Set<String>? = invoke {
    it.zrevrangeByScore(key, max, min)
  }

  suspend fun zrevrangeByScore(key: ByteArray, max: ByteArray, min: ByteArray): Set<ByteArray>? = invoke {
    it.zrevrangeByScore(key, max, min)
  }

  suspend fun zrevrangeByScore(
    key: String, max: Double, min: Double, offset: Int,
    count: Int,
  ): Set<String>? = invoke {
    it.zrevrangeByScore(key, max, min, offset, count)
  }

  suspend fun zrevrangeByScore(
    key: String, max: String, min: String, offset: Int,
    count: Int,
  ): Set<String>? = invoke {
    it.zrevrangeByScore(key, max, min, offset, count)
  }

  suspend fun zrevrangeByScore(
    key: ByteArray, max: Double, min: Double, offset: Int,
    count: Int,
  ): Set<ByteArray>? = invoke {
    it.zrevrangeByScore(key, max, min, offset, count)
  }

  suspend fun zrevrangeByScore(
    key: ByteArray, max: ByteArray, min: ByteArray, offset: Int,
    count: Int,
  ): Set<ByteArray>? = invoke {
    it.zrevrangeByScore(key, max, min, offset, count)
  }

  suspend fun zrevrangeByScoreWithScores(key: String, max: Double, min: Double): Set<Tuple>? = invoke {
    it.zrevrangeByScoreWithScores(key, max, min)
  }

  suspend fun zrevrangeByScoreWithScores(key: String, max: String, min: String): Set<Tuple>? = invoke {
    it.zrevrangeByScoreWithScores(key, max, min)
  }

  suspend fun zrevrangeByScoreWithScores(key: ByteArray, max: Double, min: Double): Set<Tuple>? = invoke {
    it.zrevrangeByScoreWithScores(key, max, min)
  }

  suspend fun zrevrangeByScoreWithScores(key: ByteArray, max: ByteArray, min: ByteArray): Set<Tuple>? = invoke {
    it.zrevrangeByScoreWithScores(key, max, min)
  }

  suspend fun zrevrangeByScoreWithScores(
    key: String, max: Double, min: Double,
    offset: Int, count: Int,
  ): Set<Tuple>? = invoke {
    it.zrevrangeByScoreWithScores(key, max, min, offset, count)
  }

  suspend fun zrevrangeByScoreWithScores(
    key: String, max: String, min: String,
    offset: Int, count: Int,
  ): Set<Tuple>? = invoke {
    it.zrevrangeByScoreWithScores(key, max, min, offset, count)
  }

  suspend fun zrevrangeByScoreWithScores(
    key: ByteArray, max: Double, min: Double,
    offset: Int, count: Int,
  ): Set<Tuple>? = invoke {
    it.zrevrangeByScoreWithScores(key, max, min, offset, count)
  }

  suspend fun zrevrangeByScoreWithScores(
    key: ByteArray, max: ByteArray, min: ByteArray,
    offset: Int, count: Int,
  ): Set<Tuple>? = invoke {
    it.zrevrangeByScoreWithScores(key, max, min, offset, count)
  }

  suspend fun zrangeWithScores(key: String, start: Long, stop: Long): Set<Tuple>? = invoke {
    it.zrangeWithScores(key, start, stop)
  }

  suspend fun zrangeWithScores(key: ByteArray, start: Long, stop: Long): Set<Tuple>? = invoke {
    it.zrangeWithScores(key, start, stop)
  }

  suspend fun zrank(key: String, member: String): Long? = invoke {
    it.zrank(key, member)
  }

  suspend fun zrank(key: ByteArray, member: ByteArray): Long? = invoke {
    it.zrank(key, member)
  }

  suspend fun zrem(key: String, vararg members: String): Long? = invoke {
    it.zrem(key, *members)
  }

  suspend fun zrem(key: ByteArray, vararg members: ByteArray): Long? = invoke {
    it.zrem(key, *members)
  }

  suspend fun zremrangeByRank(key: String, start: Long, stop: Long): Long? = invoke {
    it.zremrangeByRank(key, start, stop)
  }

  suspend fun zremrangeByRank(key: ByteArray, start: Long, stop: Long): Long? = invoke {
    it.zremrangeByRank(key, start, stop)
  }

  suspend fun zremrangeByScore(key: String, min: Double, max: Double): Long? = invoke {
    it.zremrangeByScore(key, min, max)
  }

  suspend fun zremrangeByScore(key: String, min: String, max: String): Long? = invoke {
    it.zremrangeByScore(key, min, max)
  }

  suspend fun zremrangeByScore(key: ByteArray, min: Double, max: Double): Long? = invoke {
    it.zremrangeByScore(key, min, max)
  }

  suspend fun zremrangeByScore(key: ByteArray, min: ByteArray, max: ByteArray): Long? = invoke {
    it.zremrangeByScore(key, min, max)
  }

  suspend fun zrevrange(key: String, start: Long, stop: Long): Set<String>? = invoke {
    it.zrevrange(key, start, stop)
  }

  suspend fun zrevrange(key: ByteArray, start: Long, stop: Long): Set<ByteArray>? = invoke {
    it.zrevrange(key, start, stop)
  }

  suspend fun zrevrangeWithScores(key: String, start: Long, stop: Long): Set<Tuple>? = invoke {
    it.zrevrangeWithScores(key, start, stop)
  }

  suspend fun zrevrangeWithScores(key: ByteArray, start: Long, stop: Long): Set<Tuple>? = invoke {
    it.zrevrangeWithScores(key, start, stop)
  }

  suspend fun zrevrank(key: String, member: String): Long? = invoke {
    it.zrevrank(key, member)
  }

  suspend fun zrevrank(key: ByteArray, member: ByteArray): Long? = invoke {
    it.zrevrank(key, member)
  }

  suspend fun zscore(key: String, member: String): Double? = invoke {
    it.zscore(key, member)
  }

  suspend fun zscore(key: ByteArray, member: ByteArray): Double? = invoke {
    it.zscore(key, member)
  }

  suspend fun zpopmax(key: String): Tuple? = invoke {
    it.zpopmax(key)
  }

  suspend fun zpopmax(key: ByteArray): Tuple? = invoke {
    it.zpopmax(key)
  }

  suspend fun zpopmax(key: String, count: Int): Set<Tuple>? = invoke {
    it.zpopmax(key, count)
  }

  suspend fun zpopmax(key: ByteArray, count: Int): Set<Tuple>? = invoke {
    it.zpopmax(key, count)
  }

  suspend fun zpopmin(key: String): Tuple? = invoke {
    it.zpopmin(key)
  }

  suspend fun zpopmin(key: ByteArray): Tuple? = invoke {
    it.zpopmin(key)
  }

  suspend fun zpopmin(key: ByteArray, count: Int): Set<Tuple>? = invoke {
    it.zpopmin(key, count)
  }

  suspend fun zpopmin(key: String, count: Int): Set<Tuple>? = invoke {
    it.zpopmin(key, count)
  }

  suspend fun zlexcount(key: ByteArray, min: ByteArray, max: ByteArray): Long? = invoke {
    it.zlexcount(key, min, max)
  }

  suspend fun zlexcount(key: String, min: String, max: String): Long? = invoke {
    it.zlexcount(key, min, max)
  }

  suspend fun zrangeByLex(key: ByteArray, min: ByteArray, max: ByteArray): Set<ByteArray>? = invoke {
    it.zrangeByLex(key, min, max)
  }

  suspend fun zrangeByLex(key: String, min: String, max: String): Set<String>? = invoke {
    it.zrangeByLex(key, min, max)
  }

  suspend fun zrangeByLex(
    key: ByteArray, min: ByteArray, max: ByteArray,
    offset: Int, count: Int,
  ): Set<ByteArray>? = invoke {
    it.zrangeByLex(key, min, max, offset, count)
  }

  suspend fun zrangeByLex(
    key: String, min: String, max: String,
    offset: Int, count: Int,
  ): Set<String>? = invoke {
    it.zrangeByLex(key, min, max, offset, count)
  }

  suspend fun zrevrangeByLex(key: ByteArray, max: ByteArray, min: ByteArray): Set<ByteArray>? = invoke {
    it.zrevrangeByLex(key, max, min)
  }

  suspend fun zrevrangeByLex(key: String, max: String, min: String): Set<String>? = invoke {
    it.zrevrangeByLex(key, max, min)
  }

  suspend fun zrevrangeByLex(
    key: ByteArray, max: ByteArray, min: ByteArray,
    offset: Int, count: Int,
  ): Set<ByteArray>? = invoke {
    it.zrevrangeByLex(key, max, min, offset, count)
  }

  suspend fun zrevrangeByLex(
    key: String, max: String, min: String,
    offset: Int, count: Int,
  ): Set<String>? = invoke {
    it.zrevrangeByLex(key, max, min, offset, count)
  }

  suspend fun zremrangeByLex(key: ByteArray, min: ByteArray, max: ByteArray): Long? = invoke {
    it.zremrangeByLex(key, min, max)
  }

  suspend fun zremrangeByLex(key: String, min: String, max: String): Long? = invoke {
    it.zremrangeByLex(key, min, max)
  }

  suspend fun bitcount(key: String): Long? = invoke {
    it.bitcount(key)
  }

  suspend fun bitcount(key: String, start: Long, end: Long): Long? = invoke {
    it.bitcount(key, start, end)
  }

  suspend fun bitcount(key: ByteArray): Long? = invoke {
    it.bitcount(key)
  }

  suspend fun bitcount(key: ByteArray, start: Long, end: Long): Long? = invoke {
    it.bitcount(key, start, end)
  }

  suspend fun dump(key: String): ByteArray? = invoke {
    it.dump(key)
  }

  suspend fun dump(key: ByteArray): ByteArray? = invoke {
    it.dump(key)
  }

  suspend fun migrate(
    host: String, port: Int,
    key: String, destinationDb: Int, timeout: Int,
  ): String? = invoke {
    it.migrate(host, port, key, destinationDb, timeout)
  }

  suspend fun migrate(
    host: String, port: Int,
    key: ByteArray, destinationDb: Int, timeout: Int,
  ): String? = invoke {
    it.migrate(host, port, key, destinationDb, timeout)
  }

  suspend fun objectRefcount(key: String): Long? = invoke {
    it.objectRefcount(key)
  }

  suspend fun objectRefcount(key: ByteArray): Long? = invoke {
    it.objectRefcount(key)
  }

  suspend fun objectEncoding(key: String): String? = invoke {
    it.objectEncoding(key)
  }

  suspend fun objectEncoding(key: ByteArray): ByteArray? = invoke {
    it.objectEncoding(key)
  }

  suspend fun objectIdletime(key: String): Long? = invoke {
    it.objectIdletime(key)
  }

  suspend fun objectIdletime(key: ByteArray): Long? = invoke {
    it.objectIdletime(key)
  }

  suspend fun objectFreq(key: ByteArray): Long? = invoke {
    it.objectFreq(key)
  }

  suspend fun objectFreq(key: String): Long? = invoke {
    it.objectFreq(key)
  }

  suspend fun pexpire(key: String, milliseconds: Long): Long? = invoke {
    it.pexpire(key, milliseconds)
  }

  suspend fun pexpire(key: ByteArray, milliseconds: Long): Long? = invoke {
    it.pexpire(key, milliseconds)
  }

  suspend fun pexpireAt(key: String, millisecondsTimestamp: Long): Long? = invoke {
    it.pexpireAt(key, millisecondsTimestamp)
  }

  suspend fun pexpireAt(key: ByteArray, millisecondsTimestamp: Long): Long? = invoke {
    it.pexpireAt(key, millisecondsTimestamp)
  }

  suspend fun pttl(key: String): Long? = invoke {
    it.pttl(key)
  }

  suspend fun pttl(key: ByteArray): Long? = invoke {
    it.pttl(key)
  }

  suspend fun restore(key: String, ttl: Int, serializedValue: ByteArray): String? = invoke {
    it.restore(key, ttl, serializedValue)
  }

  suspend fun restore(key: ByteArray, ttl: Int, serializedValue: ByteArray): String? = invoke {
    it.restore(key, ttl, serializedValue)
  }

  suspend fun restoreReplace(key: String, ttl: Int, serializedValue: ByteArray): String? = invoke {
    it.restoreReplace(key, ttl, serializedValue)
  }

  suspend fun restoreReplace(key: ByteArray, ttl: Int, serializedValue: ByteArray): String? = invoke {
    it.restoreReplace(key, ttl, serializedValue)
  }

  suspend fun incrByFloat(key: String, increment: Double): Double? = invoke {
    it.incrByFloat(key, increment)
  }

  suspend fun incrByFloat(key: ByteArray, increment: Double): Double? = invoke {
    it.incrByFloat(key, increment)
  }

  suspend fun psetex(key: String, milliseconds: Long, value: String): String? = invoke {
    it.psetex(key, milliseconds, value)
  }

  suspend fun psetex(key: ByteArray, milliseconds: Long, value: ByteArray): String? = invoke {
    it.psetex(key, milliseconds, value)
  }

  suspend fun hincrByFloat(key: String, field: String, increment: Double): Double? = invoke {
    it.hincrByFloat(key, field, increment)
  }

  suspend fun hincrByFloat(key: ByteArray, field: ByteArray, increment: Double): Double? = invoke {
    it.hincrByFloat(key, field, increment)
  }

  suspend fun pfadd(key: ByteArray, vararg elements: ByteArray): Long? = invoke {
    it.pfadd(key, *elements)
  }

  suspend fun pfcount(key: ByteArray): Long? = invoke {
    it.pfcount(key)
  }

  suspend fun pfadd(key: String, vararg elements: String): Long? = invoke {
    it.pfadd(key, *elements)
  }

  suspend fun pfcount(key: String): Long? = invoke {
    it.pfcount(key)
  }

  suspend fun geoadd(key: ByteArray, longitude: Double, latitude: Double, member: ByteArray): Long? = invoke {
    it.geoadd(key, longitude, latitude, member)
  }

  suspend fun geoadd(key: ByteArray, memberCoordinateMap: Map<ByteArray, GeoCoordinate>): Long? = invoke {
    it.geoadd(key, memberCoordinateMap)
  }

  suspend fun geoadd(key: String, longitude: Double, latitude: Double, member: String): Long? = invoke {
    it.geoadd(key, longitude, latitude, member)
  }

  suspend fun geoadd(key: String, memberCoordinateMap: Map<String, GeoCoordinate>): Long? = invoke {
    it.geoadd(key, memberCoordinateMap)
  }

  suspend fun geodist(key: ByteArray, member1: ByteArray, member2: ByteArray): Double? = invoke {
    it.geodist(key, member1, member2)
  }

  suspend fun geodist(key: ByteArray, member1: ByteArray, member2: ByteArray, unit: GeoUnit): Double? = invoke {
    it.geodist(key, member1, member2, unit)
  }

  suspend fun geodist(key: String, member1: String, member2: String): Double? = invoke {
    it.geodist(key, member1, member2)
  }

  suspend fun geodist(key: String, member1: String, member2: String, unit: GeoUnit): Double? = invoke {
    it.geodist(key, member1, member2, unit)
  }

  suspend fun geohash(key: ByteArray, vararg members: ByteArray): List<ByteArray>? = invoke {
    it.geohash(key, *members)
  }

  suspend fun geohash(key: String, vararg members: String): List<String>? = invoke {
    it.geohash(key, *members)
  }

  suspend fun geopos(key: ByteArray, vararg members: ByteArray): List<GeoCoordinate>? = invoke {
    it.geopos(key, *members)
  }

  suspend fun geopos(key: String, vararg members: String): List<GeoCoordinate>? = invoke {
    it.geopos(key, *members)
  }

  suspend fun georadius(
    key: ByteArray, longitude: Double, latitude: Double,
    radius: Double, unit: GeoUnit,
  ): List<GeoRadiusResponse>? = invoke {
    it.georadius(key, longitude, latitude, radius, unit)
  }

  suspend fun georadiusReadonly(
    key: ByteArray, longitude: Double, latitude: Double,
    radius: Double, unit: GeoUnit,
  ): List<GeoRadiusResponse>? = invoke {
    it.georadiusReadonly(key, longitude, latitude, radius, unit)
  }

  suspend fun georadius(
    key: ByteArray, longitude: Double, latitude: Double,
    radius: Double, unit: GeoUnit, param: GeoRadiusParam,
  ): List<GeoRadiusResponse>? = invoke {
    it.georadius(key, longitude, latitude, radius, unit, param)
  }

  suspend fun georadiusReadonly(
    key: ByteArray, longitude: Double, latitude: Double,
    radius: Double, unit: GeoUnit, param: GeoRadiusParam,
  ): List<GeoRadiusResponse>? = invoke {
    it.georadiusReadonly(key, longitude, latitude, radius, unit, param)
  }

  suspend fun georadius(
    key: String, longitude: Double, latitude: Double,
    radius: Double, unit: GeoUnit,
  ): List<GeoRadiusResponse>? = invoke {
    it.georadius(key, longitude, latitude, radius, unit)
  }

  suspend fun georadiusReadonly(
    key: String, longitude: Double, latitude: Double,
    radius: Double, unit: GeoUnit,
  ): List<GeoRadiusResponse>? = invoke {
    it.georadiusReadonly(key, longitude, latitude, radius, unit)
  }

  suspend fun georadius(
    key: String, longitude: Double, latitude: Double,
    radius: Double, unit: GeoUnit, param: GeoRadiusParam,
  ): List<GeoRadiusResponse>? = invoke {
    it.georadius(key, longitude, latitude, radius, unit, param)
  }

  suspend fun georadiusReadonly(
    key: String, longitude: Double, latitude: Double,
    radius: Double, unit: GeoUnit, param: GeoRadiusParam,
  ): List<GeoRadiusResponse>? = invoke {
    it.georadiusReadonly(key, longitude, latitude, radius, unit, param)
  }

  suspend fun georadiusByMember(
    key: ByteArray, member: ByteArray,
    radius: Double, unit: GeoUnit,
  ): List<GeoRadiusResponse>? = invoke {
    it.georadiusByMember(key, member, radius, unit)
  }

  suspend fun georadiusByMemberReadonly(
    key: ByteArray, member: ByteArray,
    radius: Double, unit: GeoUnit,
  ): List<GeoRadiusResponse>? = invoke {
    it.georadiusByMemberReadonly(key, member, radius, unit)
  }

  suspend fun georadiusByMember(
    key: ByteArray, member: ByteArray,
    radius: Double, unit: GeoUnit, param: GeoRadiusParam,
  ): List<GeoRadiusResponse>? = invoke {
    it.georadiusByMember(key, member, radius, unit, param)
  }

  suspend fun georadiusByMemberReadonly(
    key: ByteArray, member: ByteArray,
    radius: Double, unit: GeoUnit, param: GeoRadiusParam,
  ): List<GeoRadiusResponse>? = invoke {
    it.georadiusByMemberReadonly(key, member, radius, unit, param)
  }

  suspend fun georadiusByMember(
    key: String, member: String,
    radius: Double, unit: GeoUnit,
  ): List<GeoRadiusResponse>? = invoke {
    it.georadiusByMember(key, member, radius, unit)
  }

  suspend fun georadiusByMemberReadonly(
    key: String, member: String,
    radius: Double, unit: GeoUnit,
  ): List<GeoRadiusResponse>? = invoke {
    it.georadiusByMemberReadonly(key, member, radius, unit)
  }

  suspend fun georadiusByMember(
    key: String, member: String,
    radius: Double, unit: GeoUnit, param: GeoRadiusParam,
  ): List<GeoRadiusResponse>? = invoke {
    it.georadiusByMember(key, member, radius, unit, param)
  }

  suspend fun georadiusByMemberReadonly(
    key: String, member: String,
    radius: Double, unit: GeoUnit, param: GeoRadiusParam,
  ): List<GeoRadiusResponse>? = invoke {
    it.georadiusByMemberReadonly(key, member, radius, unit, param)
  }

  suspend fun bitfield(key: String, vararg elements: String): List<Long>? = invoke {
    it.bitfield(key, *elements)
  }

  suspend fun bitfield(key: ByteArray, vararg elements: ByteArray): List<Long>? = invoke {
    it.bitfield(key, *elements)
  }

  suspend fun bitfieldReadonly(key: ByteArray, vararg arguments: ByteArray): List<Long>? = invoke {
    it.bitfieldReadonly(key, *arguments)
  }

  suspend fun bitfieldReadonly(key: String, vararg arguments: String): List<Long>? = invoke {
    it.bitfieldReadonly(key, *arguments)
  }

  suspend fun hstrlen(key: String, field: String): Long? = invoke {
    it.hstrlen(key, field)
  }

  suspend fun hstrlen(key: ByteArray, field: ByteArray): Long? = invoke {
    it.hstrlen(key, field)
  }

  suspend fun xadd(key: String, id: StreamEntryID, hash: Map<String, String>) =
    xadd(key, id, hash, Long.MAX_VALUE, true)

  suspend fun xadd(key: ByteArray, id: ByteArray, hash: Map<ByteArray, ByteArray>) =
    xadd(key, id, hash, Long.MAX_VALUE, true)

  suspend fun xadd(
    key: String,
    id: StreamEntryID,
    hash: Map<String, String>,
    maxLen: Long,
    approximateLength: Boolean,
  ): StreamEntryID? = invoke {
    it.xadd(key, id, hash, maxLen, approximateLength)
  }

  suspend fun xadd(
    key: ByteArray,
    id: ByteArray,
    hash: Map<ByteArray, ByteArray>,
    maxLen: Long,
    approximateLength: Boolean,
  ): ByteArray? = invoke {
    it.xadd(key, id, hash, maxLen, approximateLength)
  }

  suspend fun xlen(key: String): Long? = invoke {
    it.xlen(key)
  }

  suspend fun xlen(key: ByteArray): Long? = invoke {
    it.xlen(key)
  }

  suspend fun xrange(key: String, start: StreamEntryID, end: StreamEntryID, count: Int): List<StreamEntry>? = invoke {
    it.xrange(key, start, end, count)
  }

  suspend fun xrange(key: ByteArray, start: ByteArray, end: ByteArray, count: Int): List<ByteArray>? = invoke {
    it.xrange(key, start, end, count)
  }

  suspend fun xrevrange(key: String, end: StreamEntryID, start: StreamEntryID, count: Int): List<StreamEntry>? =
    invoke {
      it.xrevrange(key, start, end, count)
    }

  suspend fun xrevrange(key: ByteArray, end: ByteArray, start: ByteArray, count: Int): List<ByteArray>? = invoke {
    it.xrevrange(key, start, end, count)
  }

  suspend fun xack(key: String, group: String, vararg ids: StreamEntryID): Long? = invoke {
    it.xack(key, group, *ids)
  }

  suspend fun xack(key: ByteArray, group: ByteArray, vararg ids: ByteArray): Long? = invoke {
    it.xack(key, group, *ids)
  }

  suspend fun xgroupCreate(key: String, groupname: String, id: StreamEntryID, makeStream: Boolean): String? = invoke {
    it.xgroupCreate(key, groupname, id, makeStream)
  }

  suspend fun xgroupCreate(key: ByteArray, groupname: ByteArray, id: ByteArray, makeStream: Boolean): String? =
    invoke {
      it.xgroupCreate(key, groupname, id, makeStream)
    }

  suspend fun xgroupSetID(key: String, groupname: String, id: StreamEntryID): String? = invoke {
    it.xgroupSetID(key, groupname, id)
  }

  suspend fun xgroupSetID(key: ByteArray, groupname: ByteArray, id: ByteArray): String? = invoke {
    it.xgroupSetID(key, groupname, id)
  }

  suspend fun xgroupDestroy(key: String, groupname: String): Long? = invoke {
    it.xgroupDestroy(key, groupname)
  }

  suspend fun xgroupDestroy(key: ByteArray, groupname: ByteArray): Long? = invoke {
    it.xgroupDestroy(key, groupname)
  }

  suspend fun xgroupDelConsumer(key: String, groupname: String, consumername: String): Long? = invoke {
    it.xgroupDelConsumer(key, groupname, consumername)
  }

  suspend fun xgroupDelConsumer(key: ByteArray, groupname: ByteArray, consumername: ByteArray): Long? = invoke {
    it.xgroupDelConsumer(key, groupname, consumername)
  }

  suspend fun xpending(
    key: String,
    groupname: String,
    start: StreamEntryID,
    end: StreamEntryID,
    count: Int,
    consumername: String,
  ): List<StreamPendingEntry>? = invoke {
    it.xpending(key, groupname, start, end, count, consumername)
  }

  suspend fun xpending(
    key: ByteArray,
    groupname: ByteArray,
    start: ByteArray,
    end: ByteArray,
    count: Int,
    consumername: ByteArray,
  ): List<StreamPendingEntry>? = invoke {
    it.xpending(key, groupname, start, end, count, consumername)
  }

  suspend fun xdel(key: String, vararg ids: StreamEntryID): Long? = invoke {
    it.xdel(key, *ids)
  }

  suspend fun xdel(key: ByteArray, vararg ids: ByteArray): Long? = invoke {
    it.xdel(key, *ids)
  }

  suspend fun xtrim(key: String, maxLen: Long, approximateLength: Boolean): Long? = invoke {
    it.xtrim(key, maxLen, approximateLength)
  }

  suspend fun xtrim(key: ByteArray, maxLen: Long, approximateLength: Boolean): Long? = invoke {
    it.xtrim(key, maxLen, approximateLength)
  }

  suspend fun xclaim(
    key: String, group: String, consumername: String, minIdleTime: Long,
    newIdleTime: Long, retries: Int, force: Boolean, vararg ids: StreamEntryID,
  ): List<StreamEntry>? = invoke {
    it.xclaim(key, group, consumername, minIdleTime, newIdleTime, retries, force, *ids)
  }

  suspend fun xclaim(
    key: ByteArray, group: ByteArray, consumername: ByteArray, minIdleTime: Long,
    newIdleTime: Long, retries: Int, force: Boolean, vararg ids: ByteArray,
  ): List<ByteArray>? = invoke {
    it.xclaim(key, group, consumername, minIdleTime, newIdleTime, retries, force, *ids)
  }

  suspend fun sendCommand(sampleKey: String, cmd: ProtocolCommand, vararg args: String): Any? = invoke {
    it.sendCommand(sampleKey, cmd, *args)
  }

  suspend fun sendCommand(sampleKey: ByteArray, cmd: ProtocolCommand, vararg args: ByteArray): Any? = invoke {
    it.sendCommand(sampleKey, cmd, *args)
  }
}
