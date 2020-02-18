package cn.tursom.mongodb

import com.mongodb.client.model.Filters
import com.mongodb.client.model.TextSearchOptions
import org.bson.BsonType
import org.bson.conversions.Bson
import kotlin.reflect.KProperty

@Suppress("unused")
object Where {
  operator fun invoke(action: Where.() -> Bson) = this.action()

  infix fun KProperty<*>.eq(value: Any): Bson = Filters.eq(MongoUtil.fieldName(this), value)
  infix fun KProperty<*>.ne(value: Any): Bson = Filters.ne(MongoUtil.fieldName(this), value)
  infix fun KProperty<*>.gt(value: Any): Bson = Filters.gt(MongoUtil.fieldName(this), value)
  infix fun KProperty<*>.lt(value: Any): Bson = Filters.lt(MongoUtil.fieldName(this), value)
  infix fun KProperty<*>.gte(value: Any): Bson = Filters.gte(MongoUtil.fieldName(this), value)
  infix fun KProperty<*>.lte(value: Any): Bson = Filters.lte(MongoUtil.fieldName(this), value)
  fun KProperty<*>.`in`(vararg value: Any): Bson = Filters.`in`(MongoUtil.fieldName(this), *value)
  fun KProperty<*>.nin(vararg value: Any): Bson = Filters.nin(MongoUtil.fieldName(this), *value)
  fun KProperty<*>.all(vararg value: Any): Bson = Filters.all(MongoUtil.fieldName(this), *value)
  fun KProperty<*>.exists(exists: Boolean = true): Bson = Filters.exists(MongoUtil.fieldName(this), exists)

  infix fun KProperty<*>.type(type: String): Bson = Filters.type(MongoUtil.fieldName(this), type)
  infix fun KProperty<*>.type(type: BsonType): Bson = Filters.type(MongoUtil.fieldName(this), type)
  fun KProperty<*>.mod(divisor: Long, remainder: Long): Bson = Filters.mod(MongoUtil.fieldName(this), divisor, remainder)
  infix fun KProperty<*>.regex(regex: String): Bson = Filters.regex(MongoUtil.fieldName(this), regex)
  infix fun KProperty<*>.regex(regex: Regex): Bson = Filters.regex(MongoUtil.fieldName(this), regex.pattern)
  fun KProperty<*>.regex(regex: String, option: String): Bson = Filters.regex(MongoUtil.fieldName(this), regex, option)
  fun KProperty<*>.regex(regex: Regex, option: String): Bson = Filters.regex(MongoUtil.fieldName(this), regex.pattern, option)
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