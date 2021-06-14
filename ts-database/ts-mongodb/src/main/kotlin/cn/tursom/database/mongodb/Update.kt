package cn.tursom.database.mongodb

import com.mongodb.client.model.Updates
import org.bson.conversions.Bson
import kotlin.reflect.KProperty

object Update : MongoName {
  operator fun invoke(action: Update.() -> Bson) = this.action()

  fun and(vararg update: Bson): Bson = Updates.combine(*update)
  fun or(vararg update: Bson): Bson = Updates.combine(*update)
  infix fun Bson.and(update: Bson): Bson = Updates.combine(this, update)
  infix fun Bson.and(update: Update.() -> Bson): Bson = and(Update.update())
  fun combine(vararg updates: Bson): Bson = Updates.combine(*updates)


  infix fun String.set(value: Any): Bson = when (value) {
    is String, is Number, is Boolean -> Updates.set(this, value)
    else -> Updates.set(this, MongoUtil.convertToBson(value))
  }

  infix fun KProperty<*>.set(value: Any): Bson = MongoUtil.fieldName(this) set value

  fun KProperty<*>.unset(): Bson = Updates.unset(MongoUtil.fieldName(this))
  fun setOnInsert(value: Bson): Bson = Updates.setOnInsert(value)
  fun setOnInsert(value: Any): Bson = Updates.setOnInsert(MongoUtil.convertToBson(value))
  infix fun KProperty<*>.setOnInsert(value: Any): Bson = Updates.setOnInsert(MongoUtil.fieldName(this), value)
  infix fun KProperty<*>.rename(value: String): Bson = Updates.rename(MongoUtil.fieldName(this), value)
  infix fun String.rename(value: String): Bson = Updates.rename(this, value)
  infix fun KProperty<*>.inc(value: Number): Bson = Updates.inc(MongoUtil.fieldName(this), value)
  infix fun KProperty<*>.mul(value: Number): Bson = Updates.mul(MongoUtil.fieldName(this), value)
  infix fun KProperty<*>.min(value: Any): Bson = Updates.min(MongoUtil.fieldName(this), value)
  infix fun KProperty<*>.max(value: Any): Bson = Updates.max(MongoUtil.fieldName(this), value)
}