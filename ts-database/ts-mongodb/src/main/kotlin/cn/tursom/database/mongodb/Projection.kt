package cn.tursom.database.mongodb

import com.mongodb.client.model.Projections
import org.bson.conversions.Bson
import kotlin.reflect.KProperty1

object Projection {
  operator fun invoke(action: Projection.() -> Bson) = this.action()

  fun <T, TExpression> computed(field: KProperty1<out T, *>, expression: TExpression): Bson =
    computed(MongoUtil.fieldName(field), expression)

  fun <TExpression> computed(fieldName: String, expression: TExpression): Bson =
    Projections.computed(fieldName, expression)

  fun <T> include(vararg fieldNames: KProperty1<out T, *>): Bson =
    Projections.include(fieldNames.map { MongoUtil.fieldName(it) })

  fun <T> include(fieldNames: Collection<KProperty1<out T, *>>): Bson =
    Projections.include(fieldNames.map { MongoUtil.fieldName(it) })

  fun include(vararg fieldNames: String): Bson = Projections.include(fieldNames.asList())
  fun include(fieldNames: List<String>): Bson = Projections.include(fieldNames)

  fun <T> exclude(vararg fieldNames: KProperty1<out T, *>): Bson =
    Projections.exclude(fieldNames.map { MongoUtil.fieldName(it) })

  fun <T> exclude(fieldNames: Collection<KProperty1<out T, *>>): Bson =
    Projections.exclude(fieldNames.map { MongoUtil.fieldName(it) })

  fun exclude(vararg fieldNames: String): Bson = Projections.exclude(fieldNames.asList())
  fun exclude(fieldNames: List<String>): Bson = Projections.exclude(fieldNames)

  fun excludeId(): Bson = Projections.excludeId()

  fun <T> exclude(field: KProperty1<out T, *>): Bson = Projections.elemMatch(MongoUtil.fieldName(field))
  fun <T> exclude(field: KProperty1<out T, *>, filter: Bson): Bson =
    Projections.elemMatch(MongoUtil.fieldName(field), filter)

  fun elemMatch(fieldName: String): Bson = Projections.elemMatch(fieldName)
  fun elemMatch(fieldName: String, filter: Bson): Bson = Projections.elemMatch(fieldName, filter)

  fun <T> metaTextScore(field: KProperty1<out T, *>): Bson = Projections.metaTextScore(MongoUtil.fieldName(field))
  fun metaTextScore(fieldName: String): Bson = Projections.metaTextScore(fieldName)

  fun <T> slice(field: KProperty1<out T, *>, limit: Int): Bson = Projections.slice(MongoUtil.fieldName(field), limit)
  fun <T> slice(field: KProperty1<out T, *>, skip: Int, limit: Int): Bson =
    Projections.slice(MongoUtil.fieldName(field), skip, limit)

  fun slice(fieldName: String, limit: Int): Bson = Projections.slice(fieldName, limit)
  fun slice(fieldName: String, skip: Int, limit: Int): Bson = Projections.slice(fieldName, skip, limit)

  fun fields(vararg projections: Bson): Bson = Projections.fields(projections.asList())
  fun fields(projections: List<Bson>): Bson = Projections.fields(projections)
}