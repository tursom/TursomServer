package cn.tursom.database.mongodb.spring

import org.bson.BsonRegularExpression
import org.bson.Document
import org.bson.types.ObjectId
import org.springframework.data.domain.Example
import org.springframework.data.domain.Sort
import org.springframework.data.geo.Circle
import org.springframework.data.geo.Point
import org.springframework.data.geo.Shape
import org.springframework.data.mongodb.core.geo.GeoJson
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Field
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.schema.MongoJsonSchema
import java.util.*
import java.util.regex.Pattern
import kotlin.reflect.KProperty

@Suppress("MemberVisibilityCanBePrivate", "unused", "HasPlatformType")
class QueryBuilder : MongoName {
  companion object {
    const val where = "\$where"

    inline infix operator fun invoke(operator: QueryBuilder.() -> Unit): Query = QueryBuilder()(operator)

    inline infix fun queryObject(operator: QueryBuilder.() -> Unit): Document = this(operator).queryObject
    inline infix fun fieldsObject(operator: QueryBuilder.() -> Unit): Document = this(operator).fieldsObject
    inline infix fun sortObject(operator: QueryBuilder.() -> Unit): Document = this(operator).sortObject

    inline infix fun criteria(operator: QueryBuilder.() -> Unit): Criteria = QueryBuilder().apply(operator).and
  }

  inline infix operator fun invoke(operator: QueryBuilder.() -> Unit): Query {
    this.operator()
    val query = Query(and)
    queryHandler.forEach { handler ->
      query.handler()
    }
    return query
  }

  val queryHandler = LinkedList<Query.() -> Unit>()
  private var uniqueField = true
  private val fieldSet = HashSet<String>()

  val and: Criteria
    get() = criteriaList.toAndCriteria(uniqueField)

  val or: Criteria
    get() = criteriaList.toOrCriteria()

  private val criteriaList = ArrayList<Criteria>()

  fun query(handler: Query.() -> Unit) {
    queryHandler.add(handler)
  }

  fun String.asJs() = where equal this
  fun String.asObjectId() = ObjectId(this)

  private fun String.asWhere() = Criteria.where(this).also { uniqueField = uniqueField && fieldSet.add(this) }
  private fun KProperty<*>.asWhere() = mongoName.asWhere()

  //infix fun Criteria.or(operator: QueryBuilder.() -> Unit) {
  //    criteriaList.add(QueryBuilder().also(operator).criteria)
  //}

  fun or(operator: QueryBuilder.() -> Unit) {
    QueryBuilder().also(operator).or.also(criteriaList::add)
  }

  fun and(operator: QueryBuilder.() -> Unit) {
    QueryBuilder().also(operator).and.also(criteriaList::add)
  }

  infix fun KProperty<*>.gt(where: Any) = asWhere() gt where // >
  infix fun KProperty<*>.gte(where: Any) = asWhere() gte where // >=
  fun KProperty<*>.`in`(vararg where: Any?) = asWhere() `in` where  // contains
  infix fun KProperty<*>.`in`(where: Collection<Any?>) = asWhere() `in` (where) // contains

  infix operator fun <T> Collection<T>.contains(p: KProperty<T>): Boolean {
    p `in` this
    return true
  }

  infix fun KProperty<*>.`is`(where: Any?) = asWhere() `is` where // ==
  infix fun KProperty<*>.ne(where: Any?) = asWhere() ne where // !=
  infix fun KProperty<*>.lt(where: Any) = asWhere() lt where // <
  infix fun KProperty<*>.lte(where: Any) = asWhere() lte where // <=
  fun KProperty<*>.nin(vararg where: Any?) = asWhere() nin where // not contains
  infix fun KProperty<*>.nin(where: Collection<Any?>) = asWhere() nin where // not contains

  infix fun KProperty<*>.all(where: Any?) = asWhere() all where
  infix fun KProperty<*>.all(where: Collection<Any?>) = asWhere() all where
  infix fun KProperty<*>.size(s: Int) = asWhere() size s
  infix fun KProperty<*>.exists(b: Boolean) = asWhere() exists b
  infix fun KProperty<*>.type(t: Int) = asWhere() type t
  operator fun KProperty<*>.not() = asWhere().not().run(criteriaList::add)
  infix fun KProperty<*>.regex(re: String) = asWhere() regex re
  infix fun KProperty<*>.regex(pattern: Pattern) = asWhere() regex pattern
  infix fun KProperty<*>.regex(pattern: Regex) = asWhere() regex pattern
  infix fun KProperty<*>.regex(regex: BsonRegularExpression) = asWhere() regex regex
  infix fun KProperty<*>.withinSphere(circle: Circle) = asWhere() withinSphere circle
  infix fun KProperty<*>.within(shape: Shape) = asWhere() within shape
  infix fun KProperty<*>.near(point: Point) = asWhere() near point
  infix fun KProperty<*>.nearSphere(point: Point) = asWhere() nearSphere point
  infix fun KProperty<*>.intersects(point: GeoJson<*>) = asWhere() intersects point
  infix fun KProperty<*>.maxDistance(maxDistance: Double) = asWhere() maxDistance maxDistance
  infix fun KProperty<*>.minDistance(maxDistance: Double) = asWhere() minDistance maxDistance
  infix fun KProperty<*>.elemMatch(c: QueryBuilder.() -> Unit) = asWhere() elemMatch c
  infix fun KProperty<*>.alike(sample: Example<*>) = asWhere() alike sample
  infix fun KProperty<*>.andDocumentStructureMatches(schema: MongoJsonSchema) =
    asWhere() andDocumentStructureMatches schema

  infix fun KProperty<*>.equal(where: Any?) = asWhere() equal where
  infix fun KProperty<*>.eq(where: Any?) = asWhere() equal where

  infix fun KProperty<*>.equal(where: QueryBuilder.() -> Unit) = asWhere() equal QueryBuilder(where).queryObject
  infix fun KProperty<*>.eq(where: QueryBuilder.() -> Unit) = asWhere() equal QueryBuilder(where).queryObject

  infix fun StringBuilder.gt(where: Any) = toString() gt where // >
  infix fun StringBuilder.gte(where: Any) = toString() gte where // >=
  fun StringBuilder.`in`(vararg where: Any?) = toString().`in`(where = where) // contains
  infix fun StringBuilder.`in`(where: Collection<Any?>) = toString() `in` where // contains
  infix fun StringBuilder.`is`(where: Any?) = toString() `is` where // ==
  infix fun StringBuilder.ne(where: Any?) = toString() ne where // !=
  infix fun StringBuilder.lt(where: Any) = toString() lt where // <
  infix fun StringBuilder.lte(where: Any) = toString() lte where // <=
  fun StringBuilder.nin(vararg where: Any?) = toString().nin(where = where) // not contains
  infix fun StringBuilder.nin(where: Collection<Any?>) = toString() nin where // not contains

  infix fun StringBuilder.all(where: Any?) = toString() all where
  infix fun StringBuilder.all(where: Collection<Any?>) = toString() all where
  infix fun StringBuilder.size(s: Int) = toString() size s
  infix fun StringBuilder.exists(b: Boolean) = toString() exists b
  infix fun StringBuilder.type(t: Int) = toString() type t
  operator fun StringBuilder.not() = toString().not()
  infix fun StringBuilder.regex(re: String) = toString() regex re
  infix fun StringBuilder.regex(pattern: Pattern) = toString() regex pattern
  infix fun StringBuilder.regex(pattern: Regex) = toString() regex pattern.toPattern()
  infix fun StringBuilder.regex(regex: BsonRegularExpression) = toString() regex regex
  infix fun StringBuilder.withinSphere(circle: Circle) = toString() withinSphere circle
  infix fun StringBuilder.within(shape: Shape) = toString() within shape
  infix fun StringBuilder.near(point: Point) = toString() near point
  infix fun StringBuilder.nearSphere(point: Point) = toString() nearSphere point
  infix fun StringBuilder.intersects(point: GeoJson<*>) = toString() intersects point
  infix fun StringBuilder.maxDistance(maxDistance: Double) = toString() maxDistance maxDistance
  infix fun StringBuilder.minDistance(maxDistance: Double) = toString() minDistance maxDistance
  infix fun StringBuilder.elemMatch(c: QueryBuilder.() -> Unit) = toString() elemMatch c
  infix fun StringBuilder.alike(sample: Example<*>) = toString() alike sample
  infix fun StringBuilder.andDocumentStructureMatches(schema: MongoJsonSchema) =
    toString() andDocumentStructureMatches schema

  infix fun StringBuilder.equal(where: Any?) = toString() equal where
  infix fun StringBuilder.eq(where: Any?) = toString() equal where

  infix fun StringBuilder.equal(where: QueryBuilder.() -> Unit) = toString() equal QueryBuilder(where).queryObject
  infix fun StringBuilder.eq(where: QueryBuilder.() -> Unit) = toString() equal QueryBuilder(where).queryObject

  infix fun String.gt(where: Any) = asWhere() gt where // >
  infix fun String.gte(where: Any) = asWhere() gte where // >=
  fun String.`in`(vararg where: Any?) = asWhere() `in` where // contains
  infix fun String.`in`(where: Collection<Any?>) = asWhere() `in` where // contains
  infix fun String.`is`(where: Any?) = asWhere() `is` where // ==
  infix fun String.ne(where: Any?) = asWhere() ne where // !=
  infix fun String.lt(where: Any) = asWhere() lt where // <
  infix fun String.lte(where: Any) = asWhere() lte where // <=
  fun String.nin(vararg where: Any?) = asWhere() nin where // not contains
  infix fun String.nin(where: Collection<Any?>) = asWhere() nin where // not contains

  infix fun String.all(where: Any?) = asWhere() all where
  infix fun String.all(where: Collection<Any?>) = asWhere() all where
  infix fun String.size(s: Int) = asWhere() size s
  infix fun String.exists(b: Boolean) = asWhere() exists b
  infix fun String.type(t: Int) = asWhere() type t
  operator fun String.not() = asWhere().not().run(criteriaList::add)
  infix fun String.regex(re: String) = asWhere() regex re
  infix fun String.regex(pattern: Pattern) = asWhere() regex pattern
  infix fun String.regex(pattern: Regex) = asWhere() regex pattern.toPattern()
  infix fun String.regex(regex: BsonRegularExpression) = asWhere() regex regex
  infix fun String.withinSphere(circle: Circle) = asWhere() withinSphere circle
  infix fun String.within(shape: Shape) = asWhere() within shape
  infix fun String.near(point: Point) = asWhere() near point
  infix fun String.nearSphere(point: Point) = asWhere() nearSphere point
  infix fun String.intersects(point: GeoJson<*>) = asWhere() intersects point
  infix fun String.maxDistance(maxDistance: Double) = asWhere() maxDistance maxDistance
  infix fun String.minDistance(maxDistance: Double) = asWhere() minDistance maxDistance
  infix fun String.elemMatch(c: QueryBuilder.() -> Unit) = asWhere() elemMatch c
  infix fun String.alike(sample: Example<*>) = asWhere() alike sample
  infix fun String.andDocumentStructureMatches(schema: MongoJsonSchema) = asWhere() andDocumentStructureMatches schema

  infix fun String.equal(where: Any?) = asWhere() equal where
  infix fun String.eq(where: Any?) = asWhere() equal where

  infix fun String.equal(where: QueryBuilder.() -> Unit) = asWhere() equal QueryBuilder(where).queryObject
  infix fun String.eq(where: QueryBuilder.() -> Unit) = asWhere() equal QueryBuilder(where).queryObject

  private infix fun Criteria.gt(where: Any) = gt(where).run(criteriaList::add)

  private infix fun Criteria.gte(where: Any) = gte(where).run(criteriaList::add)

  private infix fun Criteria.`in`(where: Array<out Any?>) = `in`(where).run(criteriaList::add)
  private infix fun Criteria.`in`(where: Collection<Any?>) = `in`(where).run(criteriaList::add)

  private infix fun Criteria.`is`(where: Any?) = `is`(where).run(criteriaList::add)
  private infix fun Criteria.ne(where: Any?) = ne(where).run(criteriaList::add)
  private infix fun Criteria.lt(where: Any) = lt(where).run(criteriaList::add)
  private infix fun Criteria.lte(where: Any) = lte(where).run(criteriaList::add)
  private infix fun Criteria.nin(where: Array<out Any?>) = nin(where).run(criteriaList::add)
  private infix fun Criteria.nin(where: Collection<Any?>) = nin(where).run(criteriaList::add)

  private infix fun Criteria.all(where: Any?) = all(where).run(criteriaList::add)
  private infix fun Criteria.all(where: Collection<Any?>) = all(where).run(criteriaList::add)
  private infix fun Criteria.size(s: Int) = size(s).run(criteriaList::add)
  private infix fun Criteria.exists(b: Boolean) = exists(b).run(criteriaList::add)
  private infix fun Criteria.type(t: Int) = type(t).run(criteriaList::add)

  //private operator fun Criteria.not() = not().run(criteriaList::add)
  private infix fun Criteria.regex(re: String) = regex(re).run(criteriaList::add)
  private infix fun Criteria.regex(pattern: Pattern) = regex(pattern).run(criteriaList::add)
  private infix fun Criteria.regex(pattern: Regex) = regex(pattern.toPattern()).run(criteriaList::add)
  private infix fun Criteria.regex(regex: BsonRegularExpression) = regex(regex).run(criteriaList::add)
  private infix fun Criteria.withinSphere(circle: Circle) = withinSphere(circle).run(criteriaList::add)
  private infix fun Criteria.within(shape: Shape) = within(shape).run(criteriaList::add)
  private infix fun Criteria.near(point: Point) = near(point).run(criteriaList::add)
  private infix fun Criteria.nearSphere(point: Point) = nearSphere(point).run(criteriaList::add)
  private infix fun Criteria.intersects(point: GeoJson<*>) = intersects(point).run(criteriaList::add)
  private infix fun Criteria.maxDistance(maxDistance: Double) = maxDistance(maxDistance).run(criteriaList::add)
  private infix fun Criteria.minDistance(maxDistance: Double) = minDistance(maxDistance).run(criteriaList::add)
  private infix fun Criteria.elemMatch(c: QueryBuilder.() -> Unit) =
    elemMatch(QueryBuilder criteria c).run(criteriaList::add)

  private infix fun Criteria.alike(sample: Example<*>) = alike(sample).run(criteriaList::add)
  private infix fun Criteria.andDocumentStructureMatches(schema: MongoJsonSchema) =
    andDocumentStructureMatches(schema).run(criteriaList::add)

  private infix fun Criteria.equal(where: Any?) = `is`(where).run(criteriaList::add)

  /**
   * 根据 builder 所指定的规则排序
   */
  infix fun Query.sort(builder: SortBuilder.() -> Unit) = with(SortBuilder(builder))

  /**
   * 取以 field 为基准逆序排序的第一个元素
   */
  infix fun Query.run(field: KProperty<*>) = apply {
    sort { field order Sort.Direction.DESC }
    limit(1)
  }

  /**
   * 取以所有 field 为基准逆序排序的第一个元素
   */
  fun Query.last(vararg field: KProperty<*>) = last(field.iterator())

  /**
   * 取以所有 field 为基准逆序排序的第一个元素
   */
  @JvmName("lastFields")
  fun Query.last(fields: Iterable<KProperty<*>>) = last(fields.iterator())

  /**
   * 取以所有 field 为基准逆序排序的第一个元素
   */
  @JvmName("lastFields")
  fun Query.last(fields: Iterator<KProperty<*>>) = apply {
    sort { fields.forEach { it order Sort.Direction.DESC } }
    limit(1)
  }

  /**
   * 取以 field 为基准顺序排序的第一个元素
   */
  infix fun Query.first(field: KProperty<*>) = apply {
    sort { field order Sort.Direction.ASC }
    limit(1)
  }

  /**
   * 取以所有 field 为基准顺序排序的第一个元素
   */
  fun Query.first(vararg field: KProperty<*>) = first(field.iterator())

  /**
   * 取以所有 field 为基准顺序排序的第一个元素
   */
  @JvmName("firstFields")
  fun Query.first(fields: Iterable<KProperty<*>>) = first(fields.iterator())

  /**
   * 取以所有 field 为基准顺序排序的第一个元素
   */
  @JvmName("firstFields")
  fun Query.first(fields: Iterator<KProperty<*>>) = apply {
    sort { fields.forEach { it order Sort.Direction.ASC } }
    limit(1)
  }

  infix fun Query.run(field: String) = apply {
    sort { field order Sort.Direction.DESC }
    limit(1)
  }

  fun Query.last(vararg field: String) = last(field.iterator())
  fun Query.last(fields: Iterable<String>) = last(fields.iterator())
  fun Query.last(fields: Iterator<String>) = apply {
    sort { fields.forEach { it order Sort.Direction.DESC } }
    limit(1)
  }

  infix fun Query.first(field: String) = apply {
    sort { field order Sort.Direction.ASC }
    limit(1)
  }

  fun Query.first(vararg field: String) = first(field.iterator())
  fun Query.first(fields: Iterable<String>) = first(fields.iterator())
  fun Query.first(fields: Iterator<String>) = apply {
    sort { fields.forEach { it order Sort.Direction.ASC } }
    limit(1)
  }

  fun Query.page(page: Long = 0, pageSize: Int = 0, defaultPageSize: Int = 50) {
    @Suppress("NAME_SHADOWING")
    val pageSize = if (pageSize > 0) pageSize else defaultPageSize
    if (page > 1) {
      skip((page - 1) * pageSize)
    }
    limit(pageSize)
  }

  val Query.fields get() = fields()

  fun Query.include(vararg keys: KProperty<*>): Field = fields.include(keys = keys)
  fun Query.exclude(vararg keys: KProperty<*>): Field = fields.exclude(keys = keys)
  fun Query.include(key: KProperty<*>) = fields.include(key.mongoName)
  fun Query.exclude(key: KProperty<*>) = fields.exclude(key.mongoName)
  fun Query.slice(key: KProperty<*>, size: Int) = fields.slice(key.mongoName, size)
  fun Query.slice(key: KProperty<*>, offset: Int, size: Int) = fields.slice(key.mongoName, offset, size)
  fun Query.position(field: KProperty<*>, value: Int) = fields.position(mongoName(field), value)
  fun Query.elemMatch(key: KProperty<*>, elemMatchCriteria: Criteria) =
    fields.elemMatch(key.mongoName, elemMatchCriteria)

  fun Field.include(vararg keys: KProperty<*>): Field {
    keys.forEach { key ->
      include(key)
    }
    return this
  }

  fun Field.exclude(vararg keys: KProperty<*>): Field {
    keys.forEach { key ->
      exclude(key)
    }
    return this
  }

  infix fun Field.include(key: KProperty<*>) = include(key.mongoName)
  infix fun Field.exclude(key: KProperty<*>) = exclude(key.mongoName)
  fun Field.slice(key: KProperty<*>, size: Int) = slice(key.mongoName, size)
  fun Field.slice(key: KProperty<*>, offset: Int, size: Int) = slice(key.mongoName, offset, size)
  fun Field.elemMatch(key: KProperty<*>, elemMatchCriteria: Criteria) = elemMatch(key.mongoName, elemMatchCriteria)
  fun Field.position(field: KProperty<*>, value: Int) = position(mongoName(field), value)

  infix fun KProperty<*>.regexIfNotBlank(re: String?) = mongoName regexIfNotEmpty re
  infix fun String.regexIfNotBlank(re: String?) = if (re != null && re.isNotBlank()) {
    this regex re
    true
  } else {
    false
  }

  infix fun KProperty<*>.regexIfNotEmpty(re: String?) = mongoName regexIfNotEmpty re
  infix fun String.regexIfNotEmpty(re: String?) = if (re != null && re.isNotBlank()) {
    this regex re
    true
  } else {
    false
  }
}