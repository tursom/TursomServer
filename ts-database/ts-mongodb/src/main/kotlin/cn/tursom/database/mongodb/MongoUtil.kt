package cn.tursom.database.mongodb

import cn.tursom.core.isStatic
import cn.tursom.core.isTransient
import cn.tursom.core.uncheckedCast
import cn.tursom.database.mongodb.annotation.Collection
import cn.tursom.database.mongodb.annotation.Ignore
import org.bson.BsonValue
import org.bson.Document
import org.bson.conversions.Bson
import java.lang.reflect.Field
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.LinkedTransferQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.min
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.javaField

object MongoUtil {
  private val fieldNameCache = ConcurrentHashMap<Field, String>()
  private val mongoThreadId = AtomicInteger(0)
  val mongoExecutor = getThreadPool(min(4, Runtime.getRuntime().availableProcessors()))

  fun collectionName(clazz: Class<*>): String {
    return clazz.getAnnotation(Collection::class.java)?.name ?: clazz.simpleName
  }

  fun fieldName(field: Field): String {
    var fieldName = fieldNameCache[field]
    if (fieldName == null) {
      fieldName = field.getAnnotation(cn.tursom.database.mongodb.annotation.Field::class.java)?.name ?: field.name
      fieldNameCache[field] = fieldName!!
    }
    return fieldName
  }

  fun fieldName(property: KProperty<*>): String {
    val javaField = property.javaField
    return if (javaField != null) fieldName(javaField) else property.name
  }

  fun convertToBson(entity: Any): Document {
    return when (entity) {
      is Map<*, *> -> entity.convert()
      else -> {
        val bson = Document()
        entity.javaClass.declaredFields.filter {
          it.isAccessible = true
          !it.isStatic()
            && !it.isTransient()
            && it.getAnnotation(Ignore::class.java) == null
            && (it.type != Lazy::class.java || it.get(entity).uncheckedCast<Lazy<*>>().isInitialized())
        }.forEach {
          injectValue(bson, it.get(entity) ?: return@forEach, it)
        }
        bson
      }
    }
  }

  fun injectValue(bson: Document, value: Any, field: Field) {
    when (value) {
      is Pair<*, *> -> bson[value.first?.toString() ?: return] = convertToBson(value.second ?: return)
      is Map.Entry<*, *> -> bson[value.key?.toString() ?: return] =
        convertToBson(value.value ?: return)
      else -> bson[fieldName(field)] = value.convert() ?: return
    }
  }

  private fun Iterator<*>.convert(): List<*> {
    val list = ArrayList<Any?>()
    forEach {
      list.add(it.convert() ?: return@forEach)
    }
    return list
  }

  private fun Map<*, *>.convert(): Document {
    val doc = Document()
    forEach { any, u ->
      any ?: return@forEach
      doc[any.toString()] = u.convert() ?: return@forEach
    }
    return doc
  }

  private fun Any?.convert() = when (this) {
    null -> null
    is Enum<*> -> name
    is Boolean, is Number, is String, is Bson, is BsonValue, is ByteArray -> this
    is ShortArray -> asList()
    is IntArray -> asList()
    is LongArray -> asList()
    is FloatArray -> asList()
    is DoubleArray -> asList()
    is BooleanArray -> asList()
    is Map<*, *> -> this.convert()
    is Iterator<*> -> this.convert()
    is Iterable<*> -> this.iterator().convert()
    else -> convertToBson(this)
  }

  private fun getThreadPool(nThreads: Int) = ThreadPoolExecutor(
    nThreads, nThreads,
    0L, TimeUnit.MILLISECONDS,
    LinkedTransferQueue(),
    {
      val thread = Thread(it)
      thread.isDaemon = true
      thread.name = "mongo-worker-${mongoThreadId.incrementAndGet()}"
      thread
    },
    ThreadPoolExecutor.CallerRunsPolicy()
  )
}