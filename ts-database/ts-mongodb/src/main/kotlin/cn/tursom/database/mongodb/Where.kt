package cn.tursom.database.mongodb

import com.mongodb.client.model.Filters
import com.mongodb.client.model.TextSearchOptions
import org.bson.BsonType
import org.bson.conversions.Bson
import kotlin.reflect.KProperty

@Suppress("unused")
object Where : MongoName {
  inline operator fun invoke(action: Where.() -> Bson) = this.action()

  infix fun String.eq(value: Any): Bson = Filters.eq(this, value)
  infix fun String.ne(value: Any): Bson = Filters.ne(this, value)
  infix fun String.gt(value: Any): Bson = Filters.gt(this, value)
  infix fun String.lt(value: Any): Bson = Filters.lt(this, value)
  infix fun String.gte(value: Any): Bson = Filters.gte(this, value)
  infix fun String.lte(value: Any): Bson = Filters.lte(this, value)
  fun String.`in`(vararg value: Any): Bson = Filters.`in`(this, *value)
  fun String.nin(vararg value: Any): Bson = Filters.nin(this, *value)
  fun String.all(vararg value: Any): Bson = Filters.all(this, *value)
  infix fun String.all(value: Iterable<Any>): Bson = Filters.all(this, value)
  fun String.exists(exists: Boolean = true): Bson = Filters.exists(this, exists)

  infix fun String.type(type: String): Bson = Filters.type(this, type)
  infix fun String.type(type: BsonType): Bson = Filters.type(this, type)
  fun String.mod(divisor: Long, remainder: Long): Bson = Filters.mod(this, divisor, remainder)
  infix fun String.regex(regex: String): Bson = Filters.regex(this, regex)
  infix fun String.regex(regex: Regex): Bson = Filters.regex(this, regex.pattern)
  fun String.regex(regex: String, option: String): Bson = Filters.regex(this, regex, option)
  fun String.regex(regex: Regex, option: String): Bson = Filters.regex(this, regex.pattern, option)

  infix fun String.elemMatch(where: Bson): Bson = Filters.elemMatch(this, where)
  infix fun String.elemMatch(where: Where.() -> Bson): Bson = Filters.elemMatch(this, Where.where())

  infix fun KProperty<*>.eq(value: Any): Bson = Filters.eq(MongoUtil.fieldName(this), value)
  infix fun KProperty<*>.ne(value: Any): Bson = Filters.ne(MongoUtil.fieldName(this), value)
  infix fun KProperty<*>.gt(value: Any): Bson = Filters.gt(MongoUtil.fieldName(this), value)
  infix fun KProperty<*>.lt(value: Any): Bson = Filters.lt(MongoUtil.fieldName(this), value)
  infix fun KProperty<*>.gte(value: Any): Bson = Filters.gte(MongoUtil.fieldName(this), value)
  infix fun KProperty<*>.lte(value: Any): Bson = Filters.lte(MongoUtil.fieldName(this), value)
  fun KProperty<*>.`in`(vararg value: Any): Bson = Filters.`in`(MongoUtil.fieldName(this), *value)
  fun KProperty<*>.nin(vararg value: Any): Bson = Filters.nin(MongoUtil.fieldName(this), *value)
  fun KProperty<*>.all(vararg value: Any): Bson = Filters.all(MongoUtil.fieldName(this), *value)
  infix fun KProperty<*>.all(value: Iterable<Any>): Bson = Filters.all(MongoUtil.fieldName(this), value)
  fun KProperty<*>.exists(exists: Boolean = true): Bson = Filters.exists(MongoUtil.fieldName(this), exists)

  infix fun KProperty<*>.type(type: String): Bson = Filters.type(MongoUtil.fieldName(this), type)
  infix fun KProperty<*>.type(type: BsonType): Bson = Filters.type(MongoUtil.fieldName(this), type)
  fun KProperty<*>.mod(divisor: Long, remainder: Long): Bson =
    Filters.mod(MongoUtil.fieldName(this), divisor, remainder)

  infix fun KProperty<*>.regex(regex: String): Bson = Filters.regex(MongoUtil.fieldName(this), regex)
  infix fun KProperty<*>.regex(regex: Regex): Bson = Filters.regex(MongoUtil.fieldName(this), regex.pattern)
  fun KProperty<*>.regex(regex: String, option: String): Bson = Filters.regex(MongoUtil.fieldName(this), regex, option)
  fun KProperty<*>.regex(regex: Regex, option: String): Bson =
    Filters.regex(MongoUtil.fieldName(this), regex.pattern, option)

  infix fun KProperty<*>.elemMatch(where: Bson): Bson = MongoUtil.fieldName(this) elemMatch where
  infix fun KProperty<*>.elemMatch(where: Where.() -> Bson): Bson = MongoUtil.fieldName(this) elemMatch where

  fun text(search: String): Bson = Filters.text(search)
  fun text(search: String, option: TextSearchOptions): Bson = Filters.text(search, option)
  fun where(javaScriptExpression: String): Bson = Filters.where(javaScriptExpression)
  fun expr(expression: Any): Bson = Filters.expr(expression)

  fun and(vararg bson: Bson): Bson = Filters.and(*bson)
  infix fun Bson.and(bson: Bson): Bson = Filters.and(this, bson)
  fun or(vararg bson: Bson): Bson = Filters.or(*bson)
  infix fun Bson.or(bson: Bson): Bson = Filters.or(this, bson)
  fun nor(vararg bson: Bson): Bson = Filters.or(*bson)
  infix fun Bson.nor(bson: Bson): Bson = Filters.nor(this, bson)

  operator fun Bson.not(): Bson = Filters.not(this)
}