@file:Suppress("unused")

package cn.tursom.database.mongodb.spring

import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.UpdateResult
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Field
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import kotlin.reflect.KProperty

inline fun <reified T : Any> MongoTemplate.count(query: Query) = count(query, T::class.java)
inline fun <reified T : Any> MongoTemplate.count(
  noinline operator: QueryBuilder.() -> Unit,
) = count(QueryBuilder(operator), T::class.java)

inline fun <reified T : Any> MongoTemplate.find(query: Query): List<T> = find(query, T::class.java)
inline fun <reified T : Any> MongoTemplate.find(
  noinline operator: QueryBuilder.() -> Unit,
): List<T> = find(QueryBuilder(operator), T::class.java)

inline fun <reified T : Any> MongoTemplate.findOne(query: Query): T? = findOne(query, T::class.java)
inline fun <reified T : Any> MongoTemplate.findOne(
  noinline operator: QueryBuilder.() -> Unit,
): T? = findOne(QueryBuilder(operator), T::class.java)

//inline fun <T, R> T.use(action: (T) -> R): T {
//    action(this)
//    return this
//}

inline fun <T, R> T.last(action: (T) -> R) {
  action(this)
}

inline fun <reified T : Any> MongoTemplate.upsert(
  query: Query,
  update: Update,
): UpdateResult = upsert(query, update, T::class.java)

inline fun <reified T : Any> MongoTemplate.upsert(
  collectionName: String,
  query: Query,
  update: Update,
): UpdateResult = upsert(query, update, T::class.java, collectionName)

inline fun <reified T : Any> MongoTemplate.updateFirst(
  query: Query,
  update: Update,
): UpdateResult = updateFirst(query, update, T::class.java)

inline fun <reified T : Any> MongoTemplate.updateFirst(
  collectionName: String,
  query: Query,
  update: Update,
): UpdateResult = updateFirst(query, update, T::class.java, collectionName)

inline fun <reified T : Any> MongoTemplate.updateMulti(
  query: Query,
  update: Update,
): UpdateResult = updateMulti(query, update, T::class.java)

inline fun <reified T : Any> MongoTemplate.updateMulti(
  collectionName: String,
  query: Query,
  update: Update,
): UpdateResult = updateMulti(query, update, T::class.java, collectionName)

@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
inline fun <reified T : Any> MongoTemplate.remove(
  queryBuilder: QueryBuilder.() -> Unit,
): DeleteResult = remove(QueryBuilder(queryBuilder), T::class.java)

infix fun Query.sort(builder: SortBuilder.() -> Unit) {
  with(SortBuilder(builder))
}

infix fun Field.include(key: KProperty<*>) = include(MongoName.mongoName(key))
infix fun Field.exclude(key: KProperty<*>) = exclude(MongoName.mongoName(key))
fun Field.slice(key: KProperty<*>, size: Int) = slice(MongoName.mongoName(key), size)
fun Field.slice(key: KProperty<*>, offset: Int, size: Int) = slice(MongoName.mongoName(key), offset, size)
fun Field.elemMatch(key: KProperty<*>, elemMatchCriteria: Criteria) =
  elemMatch(MongoName.mongoName(key), elemMatchCriteria)

fun Field.position(field: KProperty<*>, value: Int) = position(MongoName.mongoName(field), value)