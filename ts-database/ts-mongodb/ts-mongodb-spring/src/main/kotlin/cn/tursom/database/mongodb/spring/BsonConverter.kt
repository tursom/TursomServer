package cn.tursom.database.mongodb.spring

import org.bson.types.Decimal128
import java.math.BigDecimal

/**
 * 用来将Object类型转换为Document类型的转换器
 * 设计成接口与单例对象共存的方式，即
 *   既可以用 BsonConverter.bsonValue
 *   也可以让类继承于 BsonConverter 以直接调用 Any.bsonValue 方法
 */
interface BsonConverter {
  companion object {
    fun bsonValue(obj: Any): Any = when (obj) {
      is BigDecimal -> Decimal128(obj)
      is String, is Number, is Boolean -> obj
      is Map<*, *> -> MongoUtil.convertToBson(obj)
      is Iterable<*> -> obj.mapNotNull { bsonValue(it ?: return@mapNotNull null) }
      is Array<*> -> obj.mapNotNull { bsonValue(it ?: return@mapNotNull null) }
      is ByteArray -> obj
      is ShortArray -> obj.asList()
      is IntArray -> obj.asList()
      is LongArray -> obj.asList()
      is FloatArray -> obj.asList()
      is DoubleArray -> obj.asList()
      is BooleanArray -> obj.asList()
      is CharArray -> obj.asList()
      else -> MongoUtil.convertToBson(obj)
    }
  }

  fun Any.bsonValue(): Any = Companion.bsonValue(this)
}