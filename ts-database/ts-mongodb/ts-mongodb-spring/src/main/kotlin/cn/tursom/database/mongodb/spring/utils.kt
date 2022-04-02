@file:Suppress("unused")

package cn.tursom.database.mongodb.spring

import cn.tursom.core.clone.clone
import cn.tursom.core.toSetNotNull
import cn.tursom.core.uncheckedCast
import com.mongodb.client.model.FindOneAndUpdateOptions
import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.UpdateResult
import org.bson.Document
import org.bson.conversions.Bson
import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.AggregationExpression
import org.springframework.data.mongodb.core.getCollectionName
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Field
import org.springframework.data.mongodb.core.query.Query
import kotlin.reflect.KProperty

internal val criteriaField: java.lang.reflect.Field? = try {
  val field = Criteria::class.java.getDeclaredField("criteria")
  field.isAccessible = true
  field
} catch (e: Exception) {
  null
}
internal val isValueField: java.lang.reflect.Field? = try {
  val field = Criteria::class.java.getDeclaredField("isValue")
  field.isAccessible = true
  field
} catch (e: Exception) {
  null
}
internal val criteriaChainField: java.lang.reflect.Field? = try {
  val field = Criteria::class.java.getDeclaredField("criteriaChain")
  field.isAccessible = true
  field
} catch (e: Exception) {
  null
}

fun List<Criteria>.toAndCriteria(
  uniqueField: Boolean = toSetNotNull { it.key }.size == size
) = when (size) {
  0 -> Criteria()
  1 -> first()
  else -> if (uniqueField && criteriaField != null && isValueField != null) {
    var base = Criteria()
    forEach {
      if (it.key != null) {
        base = base.and(it.key!!)
        criteriaField.set(base, criteriaField.get(it))
        isValueField.set(base, isValueField.get(it))
      } else {
        criteriaChainField?.get(base)?.uncheckedCast<MutableList<Any>>()
          ?.addAll(criteriaChainField.get(it).uncheckedCast())
      }
    }
    base
  } else {
    Criteria().andOperator(*toTypedArray())
  }
}!!

fun List<Criteria>.toOrCriteria() = when (size) {
  0 -> Criteria()
  1 -> first()
  else -> Criteria().orOperator(*toTypedArray())
}!!

fun AggregationExpression.toDocument(): Document = toDocument(DirectAggregationOperationContext)

fun <T : Any> MongoTemplate.read(bson: Bson, type: Class<T>): T = converter.read(type, bson)
inline fun <reified T : Any> MongoTemplate.read(bson: Bson): T = read(bson, T::class.java)

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
fun <T : Any> MongoTemplate.readNullable(bson: Bson?, type: Class<T>): T? = converter.read(type, bson)
inline fun <reified T : Any> MongoTemplate.readNullable(bson: Bson?): T? = readNullable(bson, T::class.java)

inline fun <reified T : Any> MongoTemplate.count(query: Query) = count(query, T::class.java)
inline fun <reified T : Any> MongoTemplate.count(
  operator: QueryBuilder.() -> Unit,
) = count(QueryBuilder(operator), T::class.java)

inline fun <reified T : Any> MongoTemplate.count(
  collectionName: String,
  operator: QueryBuilder.() -> Unit,
) = count(QueryBuilder(operator), T::class.java, collectionName)

inline fun <reified T : Any> MongoTemplate.find(query: Query): List<T> = find(query, T::class.java)
inline fun <reified T : Any> MongoTemplate.find(
  operator: QueryBuilder.() -> Unit,
): List<T> = find(QueryBuilder(operator), T::class.java)

inline fun <reified T : Any> MongoTemplate.find(
  collectionName: String,
  operator: QueryBuilder.() -> Unit,
): List<T> = find(QueryBuilder(operator), T::class.java, collectionName)

inline fun <reified T : Any> MongoTemplate.findOne(query: Query): T? = findOne(query, T::class.java)
inline fun <reified T : Any> MongoTemplate.findOne(
  operator: QueryBuilder.() -> Unit,
): T? = findOne(QueryBuilder(operator), T::class.java)

inline fun <reified T : Any> MongoTemplate.findOne(
  collectionName: String,
  operator: QueryBuilder.() -> Unit,
): T? = findOne(QueryBuilder(operator), T::class.java, collectionName)

inline fun <reified T : Any> MongoTemplate.upsert(
  query: QueryBuilder.() -> Unit,
  update: UpdateBuilder.() -> Unit,
): UpdateResult = upsert(QueryBuilder(query), UpdateBuilder(update), T::class.java)

inline fun <reified T : Any> MongoTemplate.upsert(
  collectionName: String,
  query: QueryBuilder.() -> Unit,
  update: UpdateBuilder.() -> Unit,
): UpdateResult = upsert(QueryBuilder(query), UpdateBuilder(update), T::class.java, collectionName)

inline fun <reified T : Any> MongoTemplate.updateFirst(
  query: QueryBuilder.() -> Unit,
  update: UpdateBuilder.() -> Unit,
): UpdateResult = updateFirst(QueryBuilder(query), UpdateBuilder(update), T::class.java)

inline fun <reified T : Any> MongoTemplate.updateFirst(
  collectionName: String,
  query: QueryBuilder.() -> Unit,
  update: UpdateBuilder.() -> Unit,
): UpdateResult = updateFirst(QueryBuilder(query), UpdateBuilder(update), T::class.java, collectionName)

inline fun <reified T : Any> MongoTemplate.updateFirst(
  query: QueryBuilder.() -> Unit,
  update: UpdateBuilder.() -> Unit,
  options: UpdateOption.() -> Unit,
): UpdateResult = updateFirst<T>(getCollectionName(T::class.java), query, update, options)

inline fun <reified T : Any> MongoTemplate.updateFirst(
  collectionName: String,
  query: QueryBuilder.() -> Unit,
  update: UpdateBuilder.() -> Unit,
  options: UpdateOption.() -> Unit,
): UpdateResult = db.getCollection(collectionName).updateOne(
  QueryBuilder queryObject query,
  UpdateBuilder updateObject update,
  UpdateOption(options)
)

inline fun <reified T : Any> MongoTemplate.aggregationUpdateFirst(
  query: QueryBuilder.() -> Unit,
  vararg update: UpdateBuilder.() -> Unit,
): UpdateResult = aggregationUpdateFirst<T>(getCollectionName(T::class.java), query, update = update)

inline fun <reified T : Any> MongoTemplate.aggregationUpdateFirst(
  collectionName: String,
  query: QueryBuilder.() -> Unit,
  vararg update: UpdateBuilder.() -> Unit,
): UpdateResult = db.getCollection(collectionName).updateOne(
  QueryBuilder queryObject query,
  update.map { UpdateBuilder updateObject it },
)

inline fun <reified T : Any> MongoTemplate.aggregationUpdateFirst(
  query: QueryBuilder.() -> Unit,
  update: UpdateBuilder.() -> Unit,
): UpdateResult = db.getCollection(getCollectionName(T::class.java)).updateOne(
  QueryBuilder queryObject query,
  listOf(UpdateBuilder updateObject update)
)

inline fun <reified T : Any> MongoTemplate.aggregationUpdateFirst(
  collectionName: String,
  query: QueryBuilder.() -> Unit,
  update: UpdateBuilder.() -> Unit,
): UpdateResult = db.getCollection(collectionName).updateOne(
  QueryBuilder queryObject query,
  listOf(UpdateBuilder updateObject update)
)

inline fun <reified T : Any> MongoTemplate.updateMulti(
  query: QueryBuilder.() -> Unit,
  update: UpdateBuilder.() -> Unit,
): UpdateResult = updateMulti(QueryBuilder(query), UpdateBuilder(update), T::class.java)

inline fun <reified T : Any> MongoTemplate.updateMulti(
  collectionName: String,
  query: QueryBuilder.() -> Unit,
  update: UpdateBuilder.() -> Unit,
): UpdateResult = updateMulti(QueryBuilder(query), UpdateBuilder(update), T::class.java, collectionName)

inline fun <reified T : Any> MongoTemplate.updateMulti(
  query: QueryBuilder.() -> Unit,
  update: UpdateBuilder.() -> Unit,
  options: UpdateOption.() -> Unit,
): UpdateResult = updateMulti<T>(getCollectionName(T::class.java), query, update, options)

inline fun <reified T : Any> MongoTemplate.updateMulti(
  collectionName: String,
  query: QueryBuilder.() -> Unit,
  update: UpdateBuilder.() -> Unit,
  options: UpdateOption.() -> Unit,
): UpdateResult = db.getCollection(collectionName).updateMany(
  QueryBuilder queryObject query,
  UpdateBuilder updateObject update,
  UpdateOption(options)
)

inline fun <reified T : Any> MongoTemplate.aggregationUpdateMulti(
  query: QueryBuilder.() -> Unit,
  vararg update: UpdateBuilder.() -> Unit,
): UpdateResult = aggregationUpdateMulti<T>(getCollectionName<T>(), query, update = update)

inline fun <reified T : Any> MongoTemplate.aggregationUpdateMulti(
  collectionName: String,
  query: QueryBuilder.() -> Unit,
  vararg update: UpdateBuilder.() -> Unit,
): UpdateResult = db.getCollection(collectionName).updateMany(
  QueryBuilder queryObject query,
  update.map { UpdateBuilder updateObject it }
)

inline fun <reified T : Any> MongoTemplate.aggregationUpdateMulti(
  query: QueryBuilder.() -> Unit,
  update: UpdateBuilder.() -> Unit,
): UpdateResult = db.getCollection(getCollectionName(T::class.java)).updateMany(
  QueryBuilder queryObject query,
  listOf(UpdateBuilder updateObject update)
)

inline fun <reified T : Any> MongoTemplate.aggregationUpdateMulti(
  collectionName: String,
  query: QueryBuilder.() -> Unit,
  update: UpdateBuilder.() -> Unit,
): UpdateResult = db.getCollection(collectionName).updateMany(
  QueryBuilder queryObject query,
  listOf(UpdateBuilder updateObject update)
)

inline fun <reified T : Any> MongoTemplate.findAndModify(
  query: QueryBuilder.() -> Unit,
  update: UpdateBuilder.() -> Unit,
): T? = findAndModify(QueryBuilder(query), UpdateBuilder(update), T::class.java)

inline fun <reified T : Any> MongoTemplate.findAndModify(
  collectionName: String,
  query: QueryBuilder.() -> Unit,
  update: UpdateBuilder.() -> Unit,
): T? = findAndModify(QueryBuilder(query), UpdateBuilder(update), T::class.java, collectionName)

inline fun <reified T : Any> MongoTemplate.findAndModify(
  collectionName: String,
  options: FindAndModifyOptions,
  query: QueryBuilder.() -> Unit,
  update: UpdateBuilder.() -> Unit,
): T? = findAndModify(QueryBuilder(query), UpdateBuilder(update), options, T::class.java, collectionName)

inline fun <reified T : Any> MongoTemplate.findAndModify(
  collectionName: String,
  query: QueryBuilder.() -> Unit,
  update: UpdateBuilder.() -> Unit,
  options: FindAndModifyOptions.() -> Unit,
): T? = findAndModify(
  QueryBuilder(query),
  UpdateBuilder(update),
  FindAndModifyOptions().apply(options),
  T::class.java,
  collectionName
)

inline fun <reified T : Any> MongoTemplate.findAndModify(
  query: QueryBuilder.() -> Unit,
  update: UpdateBuilder.() -> Unit,
  options: FindAndModifyOptions.() -> Unit,
): T? = findAndModify(
  QueryBuilder(query),
  UpdateBuilder(update),
  FindAndModifyOptions().apply(options),
  T::class.java,
  getCollectionName(T::class.java)
)

/**
 * 提供 spring data mongo 无法提供的某些功能，比如 arrayFilter
 */
inline fun <reified T : Any> MongoTemplate.findOneAndUpdate(
  collectionName: String,
  query: QueryBuilder.() -> Unit,
  update: UpdateBuilder.() -> Unit,
  updateOption: FindOneAndUpdateOptions.() -> Unit,
): T? = readNullable(
  db.getCollection(collectionName).findOneAndUpdate(
    QueryBuilder queryObject query,
    UpdateBuilder updateObject update,
    FindOneAndUpdateOptions().also(updateOption)
  )
)

inline fun <reified T : Any> MongoTemplate.findOneAndUpdate(
  collectionName: String,
  query: QueryBuilder.() -> Unit,
  vararg update: UpdateBuilder.() -> Unit,
  updateOption: FindOneAndUpdateOptions.() -> Unit,
): T? = readNullable(
  db.getCollection(collectionName).findOneAndUpdate(
    QueryBuilder queryObject query,
    update.map { UpdateBuilder updateObject it },
    FindOneAndUpdateOptions().also(updateOption)
  )
)

inline fun <reified T : Any> MongoTemplate.aggregationFindAndModify(
  query: QueryBuilder.() -> Unit,
  update: UpdateBuilder.() -> Unit,
): T? = db.getCollection(getCollectionName(T::class.java)).findOneAndUpdate(
  QueryBuilder queryObject query,
  listOf(UpdateBuilder updateObject update)
)?.clone()

inline fun <reified T : Any> MongoTemplate.aggregationFindAndModify(
  collectionName: String,
  query: QueryBuilder.() -> Unit,
  update: UpdateBuilder.() -> Unit,
): T? = db.getCollection(collectionName).findOneAndUpdate(
  QueryBuilder queryObject query,
  listOf(UpdateBuilder updateObject update)
)?.clone()

inline fun <reified T : Any> MongoTemplate.aggregationFindAndModify(
  collectionName: String,
  options: FindOneAndUpdateOptions,
  query: QueryBuilder.() -> Unit,
  update: UpdateBuilder.() -> Unit,
): T? = db.getCollection(collectionName).findOneAndUpdate(
  QueryBuilder queryObject query,
  listOf(UpdateBuilder updateObject update),
  options
)?.clone()

inline fun <reified T : Any> MongoTemplate.aggregationFindAndModify(
  collectionName: String,
  query: QueryBuilder.() -> Unit,
  update: UpdateBuilder.() -> Unit,
  options: FindOneAndUpdateOptions.() -> Unit,
): T? = db.getCollection(collectionName).findOneAndUpdate(
  QueryBuilder queryObject query,
  listOf(UpdateBuilder updateObject update),
  FindOneAndUpdateOptions().apply(options)
)?.clone()

inline fun <reified T : Any> MongoTemplate.aggregationFindAndModify(
  query: QueryBuilder.() -> Unit,
  update: UpdateBuilder.() -> Unit,
  options: FindOneAndUpdateOptions.() -> Unit,
): T? = db.getCollection(getCollectionName(T::class.java)).findOneAndUpdate(
  QueryBuilder queryObject query,
  listOf(UpdateBuilder updateObject update),
  FindOneAndUpdateOptions().apply(options)
)?.clone()

@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
inline fun <reified T : Any> MongoTemplate.remove(
  queryBuilder: QueryBuilder.() -> Unit,
): DeleteResult = remove(QueryBuilder(queryBuilder), T::class.java)

@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
fun MongoTemplate.remove(
  collectionName: String,
  queryBuilder: QueryBuilder.() -> Unit,
): DeleteResult = remove(QueryBuilder(queryBuilder), collectionName)

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
