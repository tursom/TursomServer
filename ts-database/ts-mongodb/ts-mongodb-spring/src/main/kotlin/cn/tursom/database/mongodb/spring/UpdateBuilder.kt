package cn.tursom.database.mongodb.spring

import cn.tursom.core.util.isNotNullOrEmpty
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

  infix fun String.set(value: ConcatBuilder.() -> Unit) =
    update.set(this, ConcatBuilder(value).toDocument())

  fun String.set(vararg value: Any?) = this set {
    for (any in value) {
      +any
    }
  }

  fun String.set(value: Iterable<*>) = this set {
    for (any in value) {
      +any
    }
  }

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
  infix fun String.pushAll(values: List<Any?>) = update.push(this).each(values.toTypedArray())
  infix fun String.pushAll(values: Sequence<Any?>) = this pushAll values.toList()

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

  infix fun StringBuilder.set(value: Any?) = update.set(toString(), value?.bsonValue())
  infix fun StringBuilder.setOnInsert(value: Any?) = update.setOnInsert(toString(), value?.bsonValue())

  infix fun StringBuilder.set(value: ConcatBuilder.() -> Unit) =
    update.set(toString(), ConcatBuilder(value).toDocument())

  fun StringBuilder.set(vararg value: Any?) = this set {
    for (any in value) {
      +any
    }
  }

  fun StringBuilder.set(value: Iterable<*>) = this set {
    for (any in value) {
      +any
    }
  }

  fun StringBuilder.unset() = update.unset(toString())
  infix fun StringBuilder.inc(inc: Number) = update.inc(toString(), inc)
  fun StringBuilder.inc() = update.inc(toString(), 1)
  infix fun StringBuilder.push(value: Any?) = update.push(toString(), value?.bsonValue())
  fun StringBuilder.push() = update.push(toString())

  @Suppress("DEPRECATION")
  @Deprecated(
    "as of MongoDB 2.4. Removed in MongoDB 3.6. Use {@link #push(String) \$push \$each} instead.",
    ReplaceWith("push")
  )
  infix fun StringBuilder.pushAll(values: Array<out Any?>) = update.pushAll(toString(), values)
  infix fun StringBuilder.pushAll(values: List<Any?>) = update.push(toString()).each(values.toTypedArray())
  infix fun StringBuilder.pushAll(values: Sequence<Any?>) = toString() pushAll values.toList()

  fun StringBuilder.addToSet() = update.addToSet(toString())
  infix fun StringBuilder.addToSet(value: Any?) = update.addToSet(toString(), value?.bsonValue())
  infix fun StringBuilder.pop(pos: Update.Position) = update.pop(toString(), pos)
  infix fun StringBuilder.pull(value: Any?) = update.pull(toString(), value?.bsonValue())
  infix fun StringBuilder.pull(query: QueryBuilder.() -> Unit) =
    update.pull(toString(), QueryBuilder queryObject query)

  infix fun StringBuilder.pullAll(values: Array<out Any?>) = update.pullAll(toString(), values)
  infix fun StringBuilder.pullAll(values: Iterable<Any?>) =
    update.addMultiFieldOperation("\$pullAll", toString(), values)

  infix fun StringBuilder.rename(newName: String) = update.rename(toString(), newName)
  fun StringBuilder.currentDate() = update.currentDate(toString())
  fun StringBuilder.currentTimestamp() = update.currentTimestamp(toString())
  infix fun StringBuilder.multiply(multiplier: Number) = update.multiply(toString(), multiplier)
  infix fun StringBuilder.max(value: Any) = update.max(toString(), value.bsonValue())
  infix fun StringBuilder.min(value: Any) = update.min(toString(), value.bsonValue())
  fun StringBuilder.bitwise() = update.bitwise(toString())
  fun StringBuilder.modifies() = update.modifies(toString())

  infix fun <T> KProperty<T?>.set(value: T) = mongoName set value
  infix fun <T> KProperty<T?>.update(value: T) = mongoName set value
  infix fun <T> KProperty<T?>.setOnInsert(value: T) = mongoName setOnInsert value
  infix fun KProperty<*>.set(value: ConcatBuilder.() -> Unit) = mongoName set value
  fun KProperty<*>.set(vararg value: Any?) = mongoName set {
    for (any in value) {
      +any
    }
  }

  fun KProperty<*>.set(value: Iterable<*>) = mongoName set {
    for (any in value) {
      +any
    }
  }

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
  inline infix fun <reified T> KProperty<T>.pushAll(values: List<T>) = mongoName.push().each(*values.toTypedArray())
  inline infix fun <reified T> KProperty<T>.pushAll(values: Sequence<T>) = mongoName pushAll values.toList()

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

  infix fun KProperty<*>.setIfNotEmpty(value: String?) = mongoName setIfNotEmpty value
  infix fun String.setIfNotEmpty(value: String?): Update {
    return if (value.isNotNullOrEmpty()) {
      update.set(this, value)
    } else {
      update
    }
  }

  infix fun KProperty<*>.setIfNotBlank(value: String?) = mongoName setIfNotBlank value
  infix fun String.setIfNotBlank(value: String?): Update {
    return if (value != null && value.isNotBlank()) {
      update.set(this, value)
    } else {
      update
    }
  }

  infix fun KProperty<*>.setIfNotNull(value: Any?) = mongoName setIfNotNull value
  infix fun String.setIfNotNull(value: Any?): Update {
    return if (value != null) {
      this set value
    } else {
      update
    }
  }
}