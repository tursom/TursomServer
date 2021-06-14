package cn.tursom.database.mongodb.spring

import cn.tursom.core.uncheckedCast
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

@Suppress("MemberVisibilityCanBePrivate", "unused")
class QueryBuilder : MongoName {
  companion object {
    const val where = "\$where"
    private val criteriaField: java.lang.reflect.Field? = try {
      Criteria::class.java.getDeclaredField("criteria")
    } catch (e: Exception) {
      null
    }
    private val isValueField: java.lang.reflect.Field? = try {
      Criteria::class.java.getDeclaredField("isValue")
    } catch (e: Exception) {
      null
    }
    private val criteriaChainField: java.lang.reflect.Field? = try {
      Criteria::class.java.getDeclaredField("criteriaChain")
    } catch (e: Exception) {
      null
    }

    init {
      criteriaField?.isAccessible = true
      isValueField?.isAccessible = true
      criteriaChainField?.isAccessible = true
    }

    inline infix operator fun invoke(operator: QueryBuilder.() -> Unit): Query = QueryBuilder().let { builder ->
      builder.operator()
      val query = Query(builder.and)
      builder.queryHandler.forEach { handler ->
        query.handler()
      }
      query
    }

    infix fun queryObject(operator: QueryBuilder.() -> Unit): Document = this(operator).queryObject
    infix fun fieldsObject(operator: QueryBuilder.() -> Unit): Document = this(operator).fieldsObject
    infix fun sortObject(operator: QueryBuilder.() -> Unit): Document = this(operator).sortObject

    infix fun criteria(operator: QueryBuilder.() -> Unit): Criteria = QueryBuilder().apply(operator).and
  }

  val queryHandler = LinkedList<Query.() -> Unit>()
  private var uniqueField = true
  private val fieldSet = HashSet<String>()

  val and: Criteria
    get() = when (criteriaList.size) {
      0 -> Criteria()
      1 -> criteriaList.first()
      else -> if (uniqueField && criteriaField != null && isValueField != null) {
        var base = Criteria()
        criteriaList.forEach {
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
        Criteria().andOperator(*criteriaList.toTypedArray())
      }
    }

  val or: Criteria
    get() = when (criteriaList.size) {
      0 -> Criteria()
      1 -> criteriaList.first()
      else -> Criteria().orOperator(*criteriaList.toTypedArray())
    }

  private val criteriaList = ArrayList<Criteria>()

  fun query(handler: Query.() -> Unit) {
    queryHandler.add(handler)
  }

  fun String.asJs() = where equal this
  fun String.asObjectId() = ObjectId(this)

  private fun String.asWhere() = Criteria.where(this).also { uniqueField = uniqueField && fieldSet.add(this) }
  private fun KProperty<*>.asWhere() =
    mongoName.asWhere().also { uniqueField = uniqueField && fieldSet.add(mongoName) }

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
  operator fun KProperty<*>.not() = asWhere().not().last(criteriaList::add)
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
  operator fun String.not() = asWhere().not().last(criteriaList::add)
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

  private infix fun Criteria.gt(where: Any) = gt(where).last(criteriaList::add)

  private infix fun Criteria.gte(where: Any) = gte(where).last(criteriaList::add)

  private infix fun Criteria.`in`(where: Array<out Any?>) = `in`(where).last(criteriaList::add)
  private infix fun Criteria.`in`(where: Collection<Any?>) = `in`(where).last(criteriaList::add)

  private infix fun Criteria.`is`(where: Any?) = `is`(where).last(criteriaList::add)
  private infix fun Criteria.ne(where: Any?) = ne(where).last(criteriaList::add)
  private infix fun Criteria.lt(where: Any) = lt(where).last(criteriaList::add)
  private infix fun Criteria.lte(where: Any) = lte(where).last(criteriaList::add)
  private infix fun Criteria.nin(where: Array<out Any?>) = nin(where).last(criteriaList::add)
  private infix fun Criteria.nin(where: Collection<Any?>) = nin(where).last(criteriaList::add)

  private infix fun Criteria.all(where: Any?) = all(where).last(criteriaList::add)
  private infix fun Criteria.all(where: Collection<Any?>) = all(where).last(criteriaList::add)
  private infix fun Criteria.size(s: Int) = size(s).last(criteriaList::add)
  private infix fun Criteria.exists(b: Boolean) = exists(b).last(criteriaList::add)
  private infix fun Criteria.type(t: Int) = type(t).last(criteriaList::add)

  //private operator fun Criteria.not() = not().last(criteriaList::add)
  private infix fun Criteria.regex(re: String) = regex(re).last(criteriaList::add)
  private infix fun Criteria.regex(pattern: Pattern) = regex(pattern).last(criteriaList::add)
  private infix fun Criteria.regex(pattern: Regex) = regex(pattern.toPattern()).last(criteriaList::add)
  private infix fun Criteria.regex(regex: BsonRegularExpression) = regex(regex).last(criteriaList::add)
  private infix fun Criteria.withinSphere(circle: Circle) = withinSphere(circle).last(criteriaList::add)
  private infix fun Criteria.within(shape: Shape) = within(shape).last(criteriaList::add)
  private infix fun Criteria.near(point: Point) = near(point).last(criteriaList::add)
  private infix fun Criteria.nearSphere(point: Point) = nearSphere(point).last(criteriaList::add)
  private infix fun Criteria.intersects(point: GeoJson<*>) = intersects(point).last(criteriaList::add)
  private infix fun Criteria.maxDistance(maxDistance: Double) = maxDistance(maxDistance).last(criteriaList::add)
  private infix fun Criteria.minDistance(maxDistance: Double) = minDistance(maxDistance).last(criteriaList::add)
  private infix fun Criteria.elemMatch(c: QueryBuilder.() -> Unit) =
    elemMatch(QueryBuilder criteria c).last(criteriaList::add)

  private infix fun Criteria.alike(sample: Example<*>) = alike(sample).last(criteriaList::add)
  private infix fun Criteria.andDocumentStructureMatches(schema: MongoJsonSchema) =
    andDocumentStructureMatches(schema).last(criteriaList::add)

  private infix fun Criteria.equal(where: Any?) = `is`(where).last(criteriaList::add)

  /**
   * 根据 builder 所指定的规则排序
   */
  infix fun Query.sort(builder: SortBuilder.() -> Unit) = with(SortBuilder(builder))

  /**
   * 取以 field 为基准逆序排序的第一个元素
   */
  infix fun Query.last(field: KProperty<*>) = apply {
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

  infix fun Query.last(field: String) = apply {
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

  val Query.fields get() = fields()
  infix fun Field.include(key: KProperty<*>) = include(key.mongoName)
  infix fun Field.exclude(key: KProperty<*>) = exclude(key.mongoName)
  fun Field.slice(key: KProperty<*>, size: Int) = slice(key.mongoName, size)
  fun Field.slice(key: KProperty<*>, offset: Int, size: Int) = slice(key.mongoName, offset, size)
  fun Field.elemMatch(key: KProperty<*>, elemMatchCriteria: Criteria) = elemMatch(key.mongoName, elemMatchCriteria)
  fun Field.position(field: KProperty<*>, value: Int) = position(mongoName(field), value)
}