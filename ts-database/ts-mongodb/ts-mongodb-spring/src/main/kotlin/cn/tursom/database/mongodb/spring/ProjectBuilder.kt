package cn.tursom.database.mongodb.spring

import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators
import org.springframework.data.mongodb.core.aggregation.Fields
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation.ProjectionOperationBuilder
import kotlin.reflect.KProperty

@Suppress("unused", "MemberVisibilityCanBePrivate")
object ProjectBuilder : MongoName, BsonConverter {
  inline infix operator fun invoke(
    operator: ProjectBuilder.() -> ProjectionOperation
  ): ProjectionOperation = this.operator()

  fun project(): ProjectionOperation = Aggregation.project()
  fun project(vararg fields: String): ProjectionOperation = Aggregation.project(*fields)
  fun project(vararg fields: KProperty<*>): ProjectionOperation =
    Aggregation.project(*fields.map { it.mongoName }.toTypedArray())

  infix fun ProjectionOperation.and(name: String): ProjectionOperationBuilder = and(name)
  infix fun ProjectionOperation.and(name: KProperty<*>): ProjectionOperationBuilder = and(name.mongoName)

  infix fun ProjectionOperation.andExclude(fields: KProperty<*>): ProjectionOperation = andExclude(listOf(fields))
  fun ProjectionOperation.andExclude(vararg fields: KProperty<*>): ProjectionOperation = andExclude(fields.asList())
  infix fun ProjectionOperation.andExclude(fields: Collection<KProperty<*>>): ProjectionOperation =
    andExclude(*fields.map { it.mongoName }.toTypedArray())

  infix fun ProjectionOperation.andInclude(fields: KProperty<*>): ProjectionOperation = andInclude(listOf(fields))
  fun ProjectionOperation.andInclude(vararg fields: KProperty<*>): ProjectionOperation = andInclude(fields.asList())
  infix fun ProjectionOperation.andInclude(fields: Collection<KProperty<*>>): ProjectionOperation =
    andInclude(*fields.map { it.mongoName }.toTypedArray())


  infix fun ProjectionOperationBuilder.nested(fields: Fields): ProjectionOperation = nested(fields)
  infix fun ProjectionOperationBuilder.`as`(alias: String): ProjectionOperation = `as`(alias)
  infix fun ProjectionOperationBuilder.alias(alias: String): ProjectionOperation = `as`(alias)
  infix fun ProjectionOperationBuilder.`as`(alias: KProperty<*>): ProjectionOperation = `as`(alias.mongoName)
  infix fun ProjectionOperationBuilder.alias(alias: KProperty<*>): ProjectionOperation = `as`(alias.mongoName)
  infix fun ProjectionOperationBuilder.applyCondition(cond: ConditionalOperators.Cond): ProjectionOperation =
    applyCondition(cond)

  infix fun ProjectionOperationBuilder.applyCondition(ifNull: ConditionalOperators.IfNull): ProjectionOperation =
    applyCondition(ifNull)

  infix operator fun ProjectionOperationBuilder.plus(number: Number): ProjectionOperationBuilder = plus(number)
  infix operator fun ProjectionOperationBuilder.plus(fieldReference: String): ProjectionOperationBuilder =
    plus(fieldReference)

  infix operator fun ProjectionOperationBuilder.plus(fieldReference: KProperty<*>): ProjectionOperationBuilder =
    plus(fieldReference.mongoName)

  infix operator fun ProjectionOperationBuilder.minus(number: Number): ProjectionOperationBuilder = minus(number)
  infix operator fun ProjectionOperationBuilder.minus(fieldReference: String): ProjectionOperationBuilder =
    minus(fieldReference)

  infix operator fun ProjectionOperationBuilder.minus(fieldReference: KProperty<*>): ProjectionOperationBuilder =
    minus(fieldReference.mongoName)

  infix fun ProjectionOperationBuilder.multiply(number: Number): ProjectionOperationBuilder = multiply(number)
  infix fun ProjectionOperationBuilder.multiply(fieldReference: String): ProjectionOperationBuilder =
    multiply(fieldReference)

  infix fun ProjectionOperationBuilder.multiply(fieldReference: KProperty<*>): ProjectionOperationBuilder =
    multiply(fieldReference.mongoName)

  infix operator fun ProjectionOperationBuilder.times(number: Number): ProjectionOperationBuilder = multiply(number)
  infix operator fun ProjectionOperationBuilder.times(fieldReference: String): ProjectionOperationBuilder =
    multiply(fieldReference)

  infix operator fun ProjectionOperationBuilder.times(fieldReference: KProperty<*>): ProjectionOperationBuilder =
    multiply(fieldReference.mongoName)

  infix fun ProjectionOperationBuilder.divide(number: Number): ProjectionOperationBuilder = divide(number)
  infix fun ProjectionOperationBuilder.divide(fieldReference: String): ProjectionOperationBuilder =
    divide(fieldReference)

  infix fun ProjectionOperationBuilder.divide(fieldReference: KProperty<*>): ProjectionOperationBuilder =
    divide(fieldReference.mongoName)

  infix operator fun ProjectionOperationBuilder.div(number: Number): ProjectionOperationBuilder = divide(number)
  infix operator fun ProjectionOperationBuilder.div(fieldReference: String): ProjectionOperationBuilder =
    divide(fieldReference)

  infix operator fun ProjectionOperationBuilder.div(fieldReference: KProperty<*>): ProjectionOperationBuilder =
    divide(fieldReference.mongoName)

  infix fun ProjectionOperationBuilder.mod(number: Number): ProjectionOperationBuilder = mod(number)
  infix fun ProjectionOperationBuilder.mod(fieldReference: String): ProjectionOperationBuilder = mod(fieldReference)
  infix fun ProjectionOperationBuilder.mod(fieldReference: KProperty<*>): ProjectionOperationBuilder =
    mod(fieldReference.mongoName)

  infix fun ProjectionOperationBuilder.cmp(compareValue: Any): ProjectionOperationBuilder = cmp(compareValue)
  infix fun ProjectionOperationBuilder.eq(compareValue: Any): ProjectionOperationBuilder = eq(compareValue)
  infix fun ProjectionOperationBuilder.gt(compareValue: Any): ProjectionOperationBuilder = gt(compareValue)
  infix fun ProjectionOperationBuilder.gte(compareValue: Any): ProjectionOperationBuilder = gte(compareValue)
  infix fun ProjectionOperationBuilder.lt(compareValue: Any): ProjectionOperationBuilder = lt(compareValue)
  infix fun ProjectionOperationBuilder.lte(compareValue: Any): ProjectionOperationBuilder = lte(compareValue)
  infix fun ProjectionOperationBuilder.ne(compareValue: Any): ProjectionOperationBuilder = ne(compareValue)

  infix fun ProjectionOperationBuilder.slice(count: Int): ProjectionOperationBuilder = slice(count)

  infix fun ProjectionOperationBuilder.log(baseFieldRef: String): ProjectionOperationBuilder = log(baseFieldRef)
  infix fun ProjectionOperationBuilder.log(baseFieldRef: KProperty<*>): ProjectionOperationBuilder =
    log(baseFieldRef.mongoName)

  infix fun ProjectionOperationBuilder.log(base: Number): ProjectionOperationBuilder = log(base)

  infix fun ProjectionOperationBuilder.pow(exponentFieldRef: String): ProjectionOperationBuilder = pow(exponentFieldRef)
  infix fun ProjectionOperationBuilder.pow(exponentFieldRef: KProperty<*>): ProjectionOperationBuilder =
    pow(exponentFieldRef.mongoName)

  infix fun ProjectionOperationBuilder.pow(exponent: Number): ProjectionOperationBuilder = pow(exponent)

  infix fun ProjectionOperationBuilder.strCaseCmpValueOf(fieldRef: String): ProjectionOperationBuilder =
    strCaseCmpValueOf(fieldRef)

  infix fun ProjectionOperationBuilder.strCaseCmpValueOf(fieldRef: KProperty<*>): ProjectionOperationBuilder =
    strCaseCmpValueOf(fieldRef.mongoName)

  @JvmName("concatArrays_")
  fun ProjectionOperationBuilder.concatArrays(fields: Collection<String>): ProjectionOperationBuilder =
    concatArrays(*fields.toTypedArray())

  fun ProjectionOperationBuilder.concatArrays(vararg fields: KProperty<*>): ProjectionOperationBuilder =
    concatArrays(*fields.map { it.mongoName }.toTypedArray())

  fun ProjectionOperationBuilder.concatArrays(fields: Collection<KProperty<*>>): ProjectionOperationBuilder =
    concatArrays(*fields.map { it.mongoName }.toTypedArray())


  infix fun String.nested(fields: Fields): ProjectionOperation = project() and this nested (fields)
  infix fun String.`as`(alias: String): ProjectionOperation = project() and this `as` (alias)
  infix fun String.alias(alias: String): ProjectionOperation = project() and this `as` (alias)
  infix fun String.`as`(alias: KProperty<*>): ProjectionOperation = project() and this `as` (alias.mongoName)
  infix fun String.alias(alias: KProperty<*>): ProjectionOperation = project() and this `as` (alias.mongoName)
  infix fun String.applyCondition(cond: ConditionalOperators.Cond): ProjectionOperation =
    project() and this applyCondition (cond)

  infix fun String.applyCondition(ifNull: ConditionalOperators.IfNull): ProjectionOperation =
    project() and this applyCondition (ifNull)

  infix operator fun String.plus(number: Number): ProjectionOperationBuilder = project() and this plus (number)
  infix operator fun String.plus(fieldReference: String): ProjectionOperationBuilder =
    project() and this plus (fieldReference)

  infix operator fun String.plus(fieldReference: KProperty<*>): ProjectionOperationBuilder =
    project() and this plus (fieldReference.mongoName)

  infix operator fun String.minus(number: Number): ProjectionOperationBuilder = project() and this minus (number)
  infix operator fun String.minus(fieldReference: String): ProjectionOperationBuilder =
    project() and this minus (fieldReference)

  infix operator fun String.minus(fieldReference: KProperty<*>): ProjectionOperationBuilder =
    project() and this minus (fieldReference.mongoName)

  infix fun String.multiply(number: Number): ProjectionOperationBuilder = project() and this multiply (number)
  infix fun String.multiply(fieldReference: String): ProjectionOperationBuilder =
    project() and this multiply (fieldReference)

  infix fun String.multiply(fieldReference: KProperty<*>): ProjectionOperationBuilder =
    project() and this multiply (fieldReference.mongoName)

  infix operator fun String.times(number: Number): ProjectionOperationBuilder = project() and this multiply (number)
  infix operator fun String.times(fieldReference: String): ProjectionOperationBuilder =
    project() and this multiply (fieldReference)

  infix operator fun String.times(fieldReference: KProperty<*>): ProjectionOperationBuilder =
    project() and this multiply (fieldReference.mongoName)

  infix fun String.divide(number: Number): ProjectionOperationBuilder = project() and this divide (number)
  infix fun String.divide(fieldReference: String): ProjectionOperationBuilder =
    project() and this divide (fieldReference)

  infix fun String.divide(fieldReference: KProperty<*>): ProjectionOperationBuilder =
    project() and this divide (fieldReference.mongoName)

  infix operator fun String.div(number: Number): ProjectionOperationBuilder = project() and this divide (number)
  infix operator fun String.div(fieldReference: String): ProjectionOperationBuilder =
    project() and this divide (fieldReference)

  infix operator fun String.div(fieldReference: KProperty<*>): ProjectionOperationBuilder =
    project() and this divide (fieldReference.mongoName)

  infix fun String.mod(number: Number): ProjectionOperationBuilder = project() and this mod (number)
  infix fun String.mod(fieldReference: String): ProjectionOperationBuilder = project() and this mod (fieldReference)
  infix fun String.mod(fieldReference: KProperty<*>): ProjectionOperationBuilder =
    project() and this mod (fieldReference.mongoName)

  infix fun String.cmp(compareValue: Any): ProjectionOperationBuilder = project() and this cmp (compareValue)
  infix fun String.eq(compareValue: Any): ProjectionOperationBuilder = project() and this eq (compareValue)
  infix fun String.gt(compareValue: Any): ProjectionOperationBuilder = project() and this gt (compareValue)
  infix fun String.gte(compareValue: Any): ProjectionOperationBuilder = project() and this gte (compareValue)
  infix fun String.lt(compareValue: Any): ProjectionOperationBuilder = project() and this lt (compareValue)
  infix fun String.lte(compareValue: Any): ProjectionOperationBuilder = project() and this lte (compareValue)
  infix fun String.ne(compareValue: Any): ProjectionOperationBuilder = project() and this ne (compareValue)

  infix fun String.slice(count: Int): ProjectionOperationBuilder = project() and this slice (count)
  fun String.slice(count: Int, offset: Int): ProjectionOperationBuilder = (project() and this).slice(count, offset)

  infix fun String.log(baseFieldRef: String): ProjectionOperationBuilder = project() and this log (baseFieldRef)
  infix fun String.log(baseFieldRef: KProperty<*>): ProjectionOperationBuilder =
    project() and this log (baseFieldRef.mongoName)

  infix fun String.log(base: Number): ProjectionOperationBuilder = project() and this log (base)

  infix fun String.pow(exponentFieldRef: String): ProjectionOperationBuilder = project() and this pow (exponentFieldRef)
  infix fun String.pow(exponentFieldRef: KProperty<*>): ProjectionOperationBuilder =
    project() and this pow (exponentFieldRef.mongoName)

  infix fun String.pow(exponent: Number): ProjectionOperationBuilder = project() and this pow (exponent)

  infix fun String.strCaseCmpValueOf(fieldRef: String): ProjectionOperationBuilder =
    project() and this strCaseCmpValueOf (fieldRef)

  infix fun String.strCaseCmpValueOf(fieldRef: KProperty<*>): ProjectionOperationBuilder =
    project() and this strCaseCmpValueOf (fieldRef.mongoName)


  infix fun KProperty<*>.nested(fields: Fields): ProjectionOperation = project() and this nested (fields)
  infix fun KProperty<*>.`as`(alias: String): ProjectionOperation = project() and this `as` (alias)
  infix fun KProperty<*>.alias(alias: String): ProjectionOperation = project() and this `as` (alias)
  infix fun KProperty<*>.`as`(alias: KProperty<*>): ProjectionOperation = project() and this `as` (alias.mongoName)
  infix fun KProperty<*>.alias(alias: KProperty<*>): ProjectionOperation = project() and this `as` (alias.mongoName)
  infix fun KProperty<*>.applyCondition(cond: ConditionalOperators.Cond): ProjectionOperation =
    project() and this applyCondition (cond)

  infix fun KProperty<*>.applyCondition(ifNull: ConditionalOperators.IfNull): ProjectionOperation =
    project() and this applyCondition (ifNull)

  infix operator fun KProperty<*>.plus(number: Number): ProjectionOperationBuilder = project() and this plus (number)
  infix operator fun KProperty<*>.plus(fieldReference: String): ProjectionOperationBuilder =
    project() and this plus (fieldReference)

  infix operator fun KProperty<*>.plus(fieldReference: KProperty<*>): ProjectionOperationBuilder =
    project() and this plus (fieldReference.mongoName)

  infix operator fun KProperty<*>.minus(number: Number): ProjectionOperationBuilder = project() and this minus (number)
  infix operator fun KProperty<*>.minus(fieldReference: String): ProjectionOperationBuilder =
    project() and this minus (fieldReference)

  infix operator fun KProperty<*>.minus(fieldReference: KProperty<*>): ProjectionOperationBuilder =
    project() and this minus (fieldReference.mongoName)

  infix fun KProperty<*>.multiply(number: Number): ProjectionOperationBuilder = project() and this multiply (number)
  infix fun KProperty<*>.multiply(fieldReference: String): ProjectionOperationBuilder =
    project() and this multiply (fieldReference)

  infix fun KProperty<*>.multiply(fieldReference: KProperty<*>): ProjectionOperationBuilder =
    project() and this multiply (fieldReference.mongoName)

  infix operator fun KProperty<*>.times(number: Number): ProjectionOperationBuilder =
    project() and this multiply (number)

  infix operator fun KProperty<*>.times(fieldReference: String): ProjectionOperationBuilder =
    project() and this multiply (fieldReference)

  infix operator fun KProperty<*>.times(fieldReference: KProperty<*>): ProjectionOperationBuilder =
    project() and this multiply (fieldReference.mongoName)

  infix fun KProperty<*>.divide(number: Number): ProjectionOperationBuilder = project() and this divide (number)
  infix fun KProperty<*>.divide(fieldReference: String): ProjectionOperationBuilder =
    project() and this divide (fieldReference)

  infix fun KProperty<*>.divide(fieldReference: KProperty<*>): ProjectionOperationBuilder =
    project() and this divide (fieldReference.mongoName)

  infix operator fun KProperty<*>.div(number: Number): ProjectionOperationBuilder = project() and this divide (number)
  infix operator fun KProperty<*>.div(fieldReference: String): ProjectionOperationBuilder =
    project() and this divide (fieldReference)

  infix operator fun KProperty<*>.div(fieldReference: KProperty<*>): ProjectionOperationBuilder =
    project() and this divide (fieldReference.mongoName)

  infix fun KProperty<*>.mod(number: Number): ProjectionOperationBuilder = project() and this mod (number)
  infix fun KProperty<*>.mod(fieldReference: String): ProjectionOperationBuilder =
    project() and this mod (fieldReference)

  infix fun KProperty<*>.mod(fieldReference: KProperty<*>): ProjectionOperationBuilder =
    project() and this mod (fieldReference.mongoName)

  infix fun KProperty<*>.cmp(compareValue: Any): ProjectionOperationBuilder = project() and this cmp (compareValue)
  infix fun KProperty<*>.eq(compareValue: Any): ProjectionOperationBuilder = project() and this eq (compareValue)
  infix fun KProperty<*>.gt(compareValue: Any): ProjectionOperationBuilder = project() and this gt (compareValue)
  infix fun KProperty<*>.gte(compareValue: Any): ProjectionOperationBuilder = project() and this gte (compareValue)
  infix fun KProperty<*>.lt(compareValue: Any): ProjectionOperationBuilder = project() and this lt (compareValue)
  infix fun KProperty<*>.lte(compareValue: Any): ProjectionOperationBuilder = project() and this lte (compareValue)
  infix fun KProperty<*>.ne(compareValue: Any): ProjectionOperationBuilder = project() and this ne (compareValue)

  infix fun KProperty<*>.slice(count: Int): ProjectionOperationBuilder = project() and this slice (count)
  fun KProperty<*>.slice(count: Int, offset: Int): ProjectionOperationBuilder =
    (project() and this).slice(count, offset)

  infix fun KProperty<*>.log(baseFieldRef: String): ProjectionOperationBuilder = project() and this log (baseFieldRef)
  infix fun KProperty<*>.log(baseFieldRef: KProperty<*>): ProjectionOperationBuilder =
    project() and this log (baseFieldRef.mongoName)

  infix fun KProperty<*>.log(base: Number): ProjectionOperationBuilder = project() and this log (base)

  infix fun KProperty<*>.pow(exponentFieldRef: String): ProjectionOperationBuilder =
    project() and this pow (exponentFieldRef)

  infix fun KProperty<*>.pow(exponentFieldRef: KProperty<*>): ProjectionOperationBuilder =
    project() and this pow (exponentFieldRef.mongoName)

  infix fun KProperty<*>.pow(exponent: Number): ProjectionOperationBuilder = project() and this pow (exponent)

  infix fun KProperty<*>.strCaseCmpValueOf(fieldRef: String): ProjectionOperationBuilder =
    project() and this strCaseCmpValueOf (fieldRef)

  infix fun KProperty<*>.strCaseCmpValueOf(fieldRef: KProperty<*>): ProjectionOperationBuilder =
    project() and this strCaseCmpValueOf (fieldRef.mongoName)

  @JvmName("concatArrays_")
  fun String.concatArrays(fields: Collection<String>): ProjectionOperationBuilder =
    (project() and this).concatArrays(*fields.toTypedArray())

  fun String.concatArrays(vararg fields: KProperty<*>): ProjectionOperationBuilder =
    (project() and this).concatArrays(*fields.map { it.mongoName }.toTypedArray())

  fun String.concatArrays(fields: Collection<KProperty<*>>): ProjectionOperationBuilder =
    (project() and this).concatArrays(*fields.map { it.mongoName }.toTypedArray())

}