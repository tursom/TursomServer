package cn.tursom.mongodb

import com.mongodb.client.model.Updates
import org.bson.conversions.Bson
import kotlin.reflect.KProperty

object Update {
  operator fun invoke(action: Update.() -> Bson) = this.action()

  infix fun Bson.and(update: Bson): Bson = Updates.combine(this, update)
  fun combine(vararg updates: Bson): Bson = Updates.combine(*updates)

  infix fun KProperty<*>.set(value: Any): Bson = Updates.set(MongoUtil.fieldName(this), value)
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