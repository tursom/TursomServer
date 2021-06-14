package cn.tursom.database.mongodb.spring

import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.GroupOperation
import kotlin.reflect.KProperty

@Suppress("unused")
object GroupBuilder : MongoName, BsonConverter {
  inline infix operator fun invoke(operator: GroupBuilder.() -> GroupOperation): GroupOperation = this.operator()

  fun group(vararg fields: String): GroupOperation = Aggregation.group(*fields)
  fun group(vararg fields: KProperty<*>): GroupOperation =
    Aggregation.group(*fields.map { it.mongoName }.toTypedArray())

  infix fun GroupOperation.sum(reference: String): GroupOperation.GroupOperationBuilder = sum(reference)
  infix fun GroupOperation.sum(reference: KProperty<*>): GroupOperation.GroupOperationBuilder = sum(reference.mongoName)

  infix fun GroupOperation.GroupOperationBuilder.`as`(alias: String): GroupOperation = `as`(alias)
  infix fun GroupOperation.GroupOperationBuilder.`as`(alias: KProperty<*>): GroupOperation = `as`(alias.mongoName)

  infix fun GroupOperation.addToSet(reference: String): GroupOperation.GroupOperationBuilder = addToSet(reference)
  infix fun GroupOperation.addToSet(reference: KProperty<*>): GroupOperation.GroupOperationBuilder =
    addToSet(reference.mongoName)

  infix fun GroupOperation.addToSet(value: Any): GroupOperation.GroupOperationBuilder = addToSet(value)

  infix fun GroupOperation.last(reference: String): GroupOperation.GroupOperationBuilder = last(reference)
  infix fun GroupOperation.last(reference: KProperty<*>): GroupOperation.GroupOperationBuilder =
    last(reference.mongoName)

  infix fun GroupOperation.first(reference: String): GroupOperation.GroupOperationBuilder = first(reference)
  infix fun GroupOperation.first(reference: KProperty<*>): GroupOperation.GroupOperationBuilder =
    first(reference.mongoName)

  infix fun GroupOperation.avg(reference: String): GroupOperation.GroupOperationBuilder = avg(reference)
  infix fun GroupOperation.avg(reference: KProperty<*>): GroupOperation.GroupOperationBuilder = avg(reference.mongoName)

  infix fun GroupOperation.push(reference: String): GroupOperation.GroupOperationBuilder = push(reference)
  infix fun GroupOperation.push(reference: KProperty<*>): GroupOperation.GroupOperationBuilder =
    push(reference.mongoName)

  infix fun GroupOperation.push(value: Any): GroupOperation.GroupOperationBuilder = push(value)

  infix fun GroupOperation.min(reference: String): GroupOperation.GroupOperationBuilder = min(reference)
  infix fun GroupOperation.min(reference: KProperty<*>): GroupOperation.GroupOperationBuilder = min(reference.mongoName)

  infix fun GroupOperation.max(reference: String): GroupOperation.GroupOperationBuilder = max(reference)
  infix fun GroupOperation.max(reference: KProperty<*>): GroupOperation.GroupOperationBuilder = max(reference.mongoName)

  infix fun GroupOperation.stdDevSamp(reference: String): GroupOperation.GroupOperationBuilder = stdDevSamp(reference)
  infix fun GroupOperation.stdDevSamp(reference: KProperty<*>): GroupOperation.GroupOperationBuilder =
    stdDevSamp(reference.mongoName)

  infix fun GroupOperation.stdDevPop(reference: String): GroupOperation.GroupOperationBuilder = stdDevPop(reference)
  infix fun GroupOperation.stdDevPop(reference: KProperty<*>): GroupOperation.GroupOperationBuilder =
    stdDevPop(reference.mongoName)
}