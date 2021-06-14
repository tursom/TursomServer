package cn.tursom.database.mongodb

import com.mongodb.client.model.Sorts
import org.bson.conversions.Bson
import kotlin.reflect.KProperty1

object Sort {
  operator fun invoke(action: Sort.() -> Bson) = this.action()

  fun <T> ascending(vararg fieldNames: KProperty1<out T, *>): Bson =
    Sorts.ascending(fieldNames.map { MongoUtil.fieldName(it) })

  fun <T> ascending(fieldNames: Collection<KProperty1<out T, *>>): Bson =
    Sorts.ascending(fieldNames.map { MongoUtil.fieldName(it) })

  fun ascending(vararg fieldNames: String): Bson = Sorts.ascending(fieldNames.asList())
  fun ascending(fieldNames: List<String>): Bson = Sorts.ascending(fieldNames)
  fun <T> descending(vararg fieldNames: KProperty1<out T, *>): Bson =
    Sorts.descending(fieldNames.map { MongoUtil.fieldName(it) })

  fun <T> descending(fieldNames: Collection<KProperty1<out T, *>>): Bson =
    Sorts.descending(fieldNames.map { MongoUtil.fieldName(it) })

  fun descending(vararg fieldNames: String): Bson = Sorts.descending(fieldNames.asList())
  fun descending(fieldNames: List<String>): Bson = Sorts.descending(fieldNames)
  fun <T> KProperty1<out T, *>.metaTextScore(): Bson = Sorts.metaTextScore(MongoUtil.fieldName(this))
  fun metaTextScore(fieldName: String): Bson = Sorts.metaTextScore(fieldName)
  fun orderBy(vararg sorts: Bson): Bson = Sorts.orderBy(sorts.asList())
  fun orderBy(sorts: List<Bson>): Bson = Sorts.orderBy(sorts)
}