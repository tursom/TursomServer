package cn.tursom.mongodb

import com.mongodb.MongoNamespace
import com.mongodb.client.model.*
import org.bson.conversions.Bson
import kotlin.reflect.KProperty1


@Suppress("unused")
object Aggregate {
  operator fun invoke(action: Aggregate.() -> Bson) = this.action()

  fun addFields(vararg fields: Field<*>): Bson = Aggregates.addFields(fields.asList())
  fun addFields(fields: List<Field<*>>): Bson? = Aggregates.addFields(fields)

  fun <TExpression, Boundary> bucket(
    groupBy: TExpression,
    boundaries: List<Boundary>
  ): Bson = Aggregates.bucket(groupBy, boundaries)

  fun <TExpression, TBoundary> bucket(
    groupBy: TExpression,
    boundaries: List<TBoundary>,
    options: BucketOptions
  ): Bson = Aggregates.bucket(groupBy, boundaries, options)


  fun <TExpression> bucketAuto(groupBy: TExpression, buckets: Int): Bson = Aggregates.bucketAuto(groupBy, buckets)

  fun <TExpression> bucketAuto(
    groupBy: TExpression,
    buckets: Int,
    options: BucketAutoOptions
  ): Bson = Aggregates.bucketAuto(groupBy, buckets, options)

  fun count(): Bson = Aggregates.count()
  fun count(field: String): Bson = Aggregates.count(field)

  fun match(filter: Bson): Bson = Aggregates.match(filter)
  fun match(value: Any): Bson = Aggregates.match(MongoUtil.convertToBson(value))
  fun project(projection: Bson): Bson = Aggregates.project(projection)
  fun sort(sort: Bson): Bson = Aggregates.sort(sort)
  fun <TExpression> sortByCount(filter: TExpression): Bson = Aggregates.sortByCount(filter)
  fun skip(skip: Int): Bson = Aggregates.skip(skip)
  fun limit(limit: Int): Bson = Aggregates.limit(limit)

  fun lookup(
    from: String,
    localField: String,
    foreignField: String,
    `as`: String
  ): Bson = Aggregates.lookup(from, localField, foreignField, `as`)

  fun lookup(from: String, pipeline: List<Bson?>, `as`: String): Bson = Aggregates.lookup(from, pipeline, `as`)
  fun <TExpression> lookup(
    from: String,
    let: List<Variable<TExpression>>? = null,
    pipeline: List<Bson>,
    `as`: String
  ): Bson = Aggregates.lookup(from, let, pipeline, `as`)


  fun facet(facets: List<Facet>): Bson = Aggregates.facet(facets)
  fun facet(vararg facets: Facet): Bson = Aggregates.facet(facets.asList())

  fun <TExpression> graphLookup(
    from: String,
    startWith: TExpression,
    connectFromField: String,
    connectToField: String,
    `as`: String
  ): Bson = Aggregates.graphLookup(from, startWith, connectFromField, connectToField, `as`)

  fun <TExpression> graphLookup(
    from: String,
    startWith: TExpression,
    connectFromField: String,
    connectToField: String,
    `as`: String,
    options: GraphLookupOptions
  ): Bson = Aggregates.graphLookup(from, startWith, connectFromField, connectToField, `as`, options)


  fun <TExpression> group(
    id: TExpression? = null,
    vararg fieldAccumulators: BsonField
  ): Bson = Aggregates.group(id, fieldAccumulators.asList())

  fun <TExpression> group(
    id: TExpression? = null,
    fieldAccumulators: List<BsonField>
  ): Bson = Aggregates.group(id, fieldAccumulators)

  fun unwind(field: KProperty1<*, *>): Bson = Aggregates.unwind(MongoUtil.fieldName(field))
  fun unwind(field: KProperty1<*, *>, unwindOptions: UnwindOptions): Bson = Aggregates.unwind(MongoUtil.fieldName(field), unwindOptions)
  fun unwind(fieldName: String): Bson = Aggregates.unwind(fieldName)
  fun unwind(fieldName: String, unwindOptions: UnwindOptions): Bson = Aggregates.unwind(fieldName, unwindOptions)

  fun out(collectionName: String): Bson = Aggregates.out(collectionName)

  fun merge(collectionName: String): Bson = Aggregates.merge(collectionName)
  fun merge(namespace: MongoNamespace): Bson = Aggregates.merge(namespace)
  fun merge(collectionName: String, options: MergeOptions): Bson = Aggregates.merge(collectionName, options)
  fun merge(namespace: MongoNamespace, options: MergeOptions): Bson = Aggregates.merge(namespace, options)

  fun <TExpression> replaceRoot(value: TExpression): Bson = Aggregates.replaceRoot(value)
  fun <TExpression> replaceWith(value: TExpression): Bson = Aggregates.replaceWith(value)
  fun sample(size: Int): Bson = Aggregates.sample(size)
}