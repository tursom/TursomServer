package cn.tursom.database.mongodb.spring

import org.bson.Document
import org.springframework.data.mongodb.core.query.Update
import kotlin.reflect.KProperty

@Suppress("unused", "MemberVisibilityCanBePrivate", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "HasPlatformType")
class UpdateBuilder(val update: Update = Update()) : MongoName, BsonConverter {
  companion object {
    inline infix operator fun invoke(operator: UpdateBuilder.() -> Unit): Update =
      UpdateBuilder(Update()).also(operator).update

    inline infix fun updateObject(operator: UpdateBuilder.() -> Unit): Document =
      UpdateBuilder(Update()).also(operator).update.updateObject

    private val addMultiFieldOperationMethod = Update::class.java.getDeclaredMethod(
      "addMultiFieldOperation",
      String::class.java,
      String::class.java,
      Any::class.java
    )

    init {
      addMultiFieldOperationMethod.isAccessible = true
    }

    private fun Update.addMultiFieldOperation(operator: String, key: String, value: Any?) {
      addMultiFieldOperationMethod.invoke(this, operator, key, value)
    }
  }

  infix fun String.set(value: Any?) = update.set(this, value?.bsonValue())
  infix fun String.setOnInsert(value: Any?) = update.setOnInsert(this, value?.bsonValue())

  fun String.unset() = update.unset(this)
  infix fun String.inc(inc: Number) = update.inc(this, inc)
  fun String.inc() = update.inc(this, 1)
  infix fun String.push(value: Any?) = update.push(this, value?.bsonValue())
  fun String.push() = update.push(this)

  @Suppress("DEPRECATION")
  @Deprecated(
    "as of MongoDB 2.4. Removed in MongoDB 3.6. Use {@link #push(String) \$push \$each} instead.",
    ReplaceWith("push")
  )
  infix fun String.pushAll(values: Array<out Any?>) = update.pushAll(this, values)

  fun String.addToSet() = update.addToSet(this)
  infix fun String.addToSet(value: Any?) = update.addToSet(this, value?.bsonValue())
  infix fun String.pop(pos: Update.Position) = update.pop(this, pos)
  infix fun String.pull(value: Any?) = update.pull(this, value?.bsonValue())
  infix fun String.pull(query: QueryBuilder.() -> Unit) = update.pull(this, QueryBuilder queryObject query)
  infix fun String.pullAll(values: Array<out Any?>) = update.pullAll(this, values)
  infix fun String.pullAll(values: Iterable<Any?>) = update.addMultiFieldOperation("\$pullAll", this, values)
  infix fun String.rename(newName: String) = update.rename(this, newName)
  fun String.currentDate() = update.currentDate(this)
  fun String.currentTimestamp() = update.currentTimestamp(this)
  infix fun String.multiply(multiplier: Number) = update.multiply(this, multiplier)
  infix fun String.max(value: Any) = update.max(this, value.bsonValue())
  infix fun String.min(value: Any) = update.min(this, value.bsonValue())
  fun String.bitwise() = update.bitwise(this)
  fun String.modifies() = update.modifies(this)

  infix fun <T> KProperty<T?>.set(value: T) = mongoName set value
  infix fun <T> KProperty<T?>.update(value: T) = mongoName set value
  infix fun <T> KProperty<T?>.setOnInsert(value: T) = mongoName setOnInsert value
  fun KProperty<*>.unset() = mongoName.unset()
  infix fun KProperty<*>.inc(inc: Number) = mongoName inc inc
  fun KProperty<*>.inc() = mongoName inc (1)
  infix fun KProperty<*>.push(value: Any) = mongoName push value
  fun KProperty<*>.push() = mongoName.push()

  @Suppress("DEPRECATION")
  @Deprecated(
    "as of MongoDB 2.4. Removed in MongoDB 3.6. Use {@link #push(String) \$push \$each} instead.",
    ReplaceWith("push")
  )
  infix fun <T> KProperty<T>.pushAll(values: Array<T>) = mongoName pushAll values

  fun KProperty<*>.addToSet() = mongoName.addToSet()
  infix fun <T : Any> KProperty<Iterable<T>>.addToSet(value: T) = mongoName addToSet value

  @JvmName("addToSetArray")
  infix fun <T> KProperty<Array<T>>.addToSet(value: T) = mongoName addToSet value
  infix fun KProperty<*>.pop(pos: Update.Position) = mongoName pop pos
  infix fun <T> KProperty<Iterable<T>>.pull(value: T) = mongoName pull value

  @JvmName("pullArray")
  infix fun <T> KProperty<Array<T>>.pull(value: T) = mongoName pull value
  infix fun KProperty<*>.pull(query: QueryBuilder.() -> Unit) =
    update.pull(mongoName, QueryBuilder queryObject query)

  infix fun <T> KProperty<Array<T>>.pullAll(values: Array<out T>) = mongoName pullAll values
  infix fun <T> KProperty<Array<T>>.pullAll(values: Iterable<T>) = mongoName pullAll values

  @JvmName("pullAllIterable")
  infix fun <T> KProperty<Iterable<T>>.pullAll(values: Array<out T>) = mongoName pullAll values

  @JvmName("pullAllIterable")
  infix fun <T> KProperty<Iterable<T>>.pullAll(values: Iterable<T>) = mongoName pullAll values
  infix fun KProperty<*>.rename(newName: String) = mongoName rename newName
  fun KProperty<*>.currentDate() = mongoName.currentDate()
  fun KProperty<*>.currentTimestamp() = mongoName.currentTimestamp()
  infix fun KProperty<*>.multiply(multiplier: Number) = mongoName multiply multiplier
  infix fun KProperty<*>.max(value: Any) = mongoName max value
  infix fun KProperty<*>.min(value: Any) = mongoName min value
  fun KProperty<*>.bitwise() = mongoName.bitwise()
  fun KProperty<*>.modifies() = mongoName.modifies()
}