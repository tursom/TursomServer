package cn.tursom.mongodb.async

import cn.tursom.core.Disposable
import cn.tursom.mongodb.BsonFactoryImpl
import cn.tursom.mongodb.MongoUtil
import cn.tursom.mongodb.Update
import cn.tursom.utils.AsyncIterator
import cn.tursom.utils.forEach
import com.mongodb.MongoClientSettings
import com.mongodb.ServerAddress
import com.mongodb.client.model.DeleteOptions
import com.mongodb.client.model.InsertManyOptions
import com.mongodb.client.model.InsertOneOptions
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.result.InsertManyResult
import com.mongodb.client.result.InsertOneResult
import com.mongodb.client.result.UpdateResult
import com.mongodb.reactivestreams.client.MongoClients
import com.mongodb.reactivestreams.client.MongoCollection
import com.mongodb.reactivestreams.client.MongoDatabase
import kotlinx.coroutines.runBlocking
import org.bson.Document
import org.bson.conversions.Bson
import org.reactivestreams.Publisher
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.reflect.KProperty1

class AsyncMongoOperator<T : Any>(
  @Suppress("MemberVisibilityCanBePrivate") val collection: MongoCollection<Document>,
  clazz: Class<T>
) : MongoCollection<Document> by collection, BsonFactoryImpl<T>(clazz) {
  constructor(clazz: Class<T>, db: MongoDatabase) : this(db.getCollection(MongoUtil.collectionName(clazz)), clazz)

  suspend fun save(entity: T, options: InsertOneOptions = InsertOneOptions()) {
    val publisher = collection.insertOne(convertToBson(entity), options)
    suspendCoroutine<Unit> { cont ->
      publisher.subscribe(object : Subscriber<InsertOneResult> {
        override fun onComplete() = cont.resume(Unit)
        override fun onSubscribe(s: Subscription?) {}
        override fun onNext(t: InsertOneResult?) {}
        override fun onError(t: Throwable) = cont.resumeWithException(t)
      })
    }
  }


  suspend fun save(entities: Collection<T>, options: InsertManyOptions = InsertManyOptions()) {
    val publisher = collection.insertMany(entities.map { convertToBson(it) }, options)
    suspendCoroutine<Unit> { cont ->
      publisher.subscribe(object : Subscriber<InsertManyResult> {
        override fun onComplete() = cont.resume(Unit)
        override fun onSubscribe(s: Subscription?) {}
        override fun onNext(t: InsertManyResult) {}
        override fun onError(t: Throwable) = cont.resumeWithException(t)
      })
    }
  }

  suspend fun update(update: Bson, where: Bson, options: UpdateOptions = UpdateOptions()): UpdateResult {
    val publisher = collection.updateOne(where, update, options)
    return suspendCoroutine { cont ->
      publisher.subscribe(object : Subscriber<UpdateResult> {
        override fun onComplete() {}
        override fun onSubscribe(s: Subscription) {}
        override fun onNext(t: UpdateResult) = cont.resume(t)
        override fun onError(t: Throwable) = cont.resumeWithException(t)
      })
    }
  }

  suspend fun update(entity: T, where: Bson, options: UpdateOptions = UpdateOptions()): UpdateResult {
    return update(convertToBson(entity), where, options)
  }

  @Suppress("SpellCheckingInspection")
  suspend fun upsert(entity: T, where: Bson, options: UpdateOptions = UpdateOptions()): UpdateResult {
    return update(entity, where, options.upsert(true))
  }

  @Suppress("SpellCheckingInspection")
  suspend fun upsert(update: Bson, where: Bson, options: UpdateOptions = UpdateOptions()): UpdateResult {
    return update(update, where, options.upsert(true))
  }

  suspend fun add(field: KProperty1<T, Number?>, value: Number, where: Bson, options: UpdateOptions = UpdateOptions()): UpdateResult {
    return upsert(
      Update { field inc value },
      where, options
    )
  }

  suspend fun inc(field: KProperty1<T, Number?>, where: Bson): UpdateResult {
    return add(field, 1, where)
  }

  suspend fun getOne(where: Bson? = null): T? {
    val publisher = if (where == null) find() else find(where)
    return suspendCoroutine { cont ->
      publisher.subscribe(object : AbstractSubscriber<Document>() {
        var resumed = false
        override fun onComplete() {
          super.onComplete()
          if (!resumed) cont.resume(null)
        }

        override fun onSubscribe(s: Subscription) = s.request(1)
        override fun onNext(t: Document) = cont.resume(parse(t))
        override fun onError(t: Throwable) = cont.resumeWithException(t)
      })
    }
  }

  suspend fun list(where: Bson? = null): List<T> {
    val publisher = if (where == null) find() else find(where)
    val resultList = ArrayList<T>()
    suspendCoroutine<Unit> { cont ->
      publisher.subscribe(object : AbstractSubscriber<Document>() {
        override fun onComplete() {
          super.onComplete()
          cont.resume(Unit)
        }

        override fun onSubscribe(s: Subscription) = s.request(Int.MAX_VALUE.toLong())
        override fun onNext(t: Document) {
          resultList.add(parse(t))
        }

        override fun onError(t: Throwable) = cont.resumeWithException(t)
      })
    }
    return resultList
  }

  fun get(where: Bson? = null, bufSize: Int = 32): AsyncIterator<T> {
    val find = if (where == null) find() else find(where)
    return iterator(find, bufSize)
  }

  fun aggregate(vararg pipeline: Bson, bufSize: Int = 32) = iterator(aggregate(pipeline.asList()), bufSize)

  private fun iterator(publisher: Publisher<Document>, bufSize: Int = 32): AsyncIterator<T> {
    val subscriber = object : AbstractSubscriber<Document>(), AsyncIterator<T> {
      private var cont = Disposable<Continuation<T>>()
      private var notify = Disposable<Continuation<Unit>>()
      private val cache = ConcurrentLinkedQueue<T>()

      override fun onComplete() {
        super.onComplete()
        cont.get()?.resumeWithException(Exception())
        notify.get()?.resume(Unit)
      }

      override fun onNext(t: Document) {
        val entity = parse(t)
        cont.get()?.resume(entity) ?: cache.add(entity)
        notify.get()?.resume(Unit)
      }

      override fun onError(t: Throwable) {
        cont.get()?.resumeWithException(t) ?: t.printStackTrace()
      }

      override suspend fun next(): T {
        return cache.poll() ?: suspendCoroutine { cont ->
          this.cont.set(cont)
          subscription.request(bufSize.toLong())
        }
      }

      override suspend fun hasNext(): Boolean {
        if (cache.isEmpty() && !compete) {
          suspendCoroutine<Unit> {
            notify.set(it)
            subscription.request(bufSize.toLong())
          }
        }
        return cache.isNotEmpty()
      }
    }
    publisher.subscribe(subscriber)
    return subscriber
  }

  fun delete(entity: T, options: DeleteOptions = DeleteOptions()) {
    deleteOne(convertToBson(entity), options)
  }

  suspend fun saveIfNotExists(entity: T) {
    val document = convertToBson(entity)
    upsert(document, document)
  }
}