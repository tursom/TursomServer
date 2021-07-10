package cn.tursom.database.mongodb.spring

import org.bson.BsonRegularExpression
import org.bson.Document
import org.bson.types.ObjectId
import org.springframework.data.domain.Example
import org.springframework.data.geo.Circle
import org.springframework.data.geo.Point
import org.springframework.data.geo.Shape
import org.springframework.data.mongodb.core.geo.GeoJson
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.schema.MongoJsonSchema
import java.util.regex.Pattern
import kotlin.reflect.KProperty

@Suppress("MemberVisibilityCanBePrivate", "unused", "HasPlatformType")
object CriteriaBuilder : MongoName, BsonConverter {
  const val where = "\$where"
  fun String.asJs() = where equal this
  fun String.asObjectId() = ObjectId(this)

  infix fun where(where: String) = Criteria.where(where)
  infix fun where(where: KProperty<*>) = Criteria.where(where.mongoName)
  fun String.asWhere() = Criteria.where(this)
  fun KProperty<*>.asWhere() = mongoName.asWhere()

  infix fun Criteria.and(where: Criteria) = Criteria().andOperator(this, where)
  infix operator fun Criteria.plus(where: Criteria) = and(where)

  private val orOperator: Criteria.(Array<out Criteria>) -> Criteria = Criteria::orOperator
  private val andOperator: Criteria.(Array<out Criteria>) -> Criteria = Criteria::andOperator

  fun or(vararg where: Criteria) = Criteria().orOperator(where)
  val or: (Array<out Criteria>) -> Criteria = CriteriaBuilder::or
  fun and(vararg where: Criteria) = Criteria().andOperator(where)
  val and: (Array<out Criteria>) -> Criteria = CriteriaBuilder::and

  infix fun Criteria.or(where: Criteria) = or(this, where)

  infix fun Criteria.gt(where: Any) = gt(where.bsonValue()) // >
  infix fun Criteria.gte(where: Any) = gte(where.bsonValue()) // >=

  infix fun Criteria.`in`(where: Collection<Any?>) = `in`(where.mapNotNull { it?.bsonValue() }) // contains
  infix fun Criteria.`is`(where: Any?) = `is`(where?.bsonValue()) // ==
  infix fun Criteria.ne(where: Any?) = ne(where?.bsonValue()) // !=
  infix fun Criteria.lt(where: Any) = lt(where.bsonValue()) // <
  infix fun Criteria.lte(where: Any) = lte(where.bsonValue()) // <=
  infix fun Criteria.nin(where: Collection<Any?>) = nin(where.bsonValue()) // not contains

  infix fun Criteria.all(where: Any?) = all(where?.bsonValue())
  infix fun Criteria.all(where: Collection<Any?>) = all(where.bsonValue())
  infix fun Criteria.size(s: Int) = size(s)
  infix fun Criteria.exists(b: Boolean) = exists(b)
  infix fun Criteria.type(t: Int) = type(t)
  operator fun Criteria.not() = not()
  infix fun Criteria.regex(re: String) = regex(re)
  infix fun Criteria.regex(pattern: Pattern) = regex(pattern)
  infix fun Criteria.regex(pattern: Regex) = regex(pattern.toPattern())
  infix fun Criteria.regex(regex: BsonRegularExpression) = regex(regex)
  infix fun Criteria.withinSphere(circle: Circle) = withinSphere(circle)
  infix fun Criteria.within(shape: Shape) = within(shape)
  infix fun Criteria.near(point: Point) = near(point)
  infix fun Criteria.nearSphere(point: Point) = nearSphere(point)
  infix fun Criteria.intersects(point: GeoJson<*>) = intersects(point)
  infix fun Criteria.maxDistance(maxDistance: Double) = maxDistance(maxDistance)
  infix fun Criteria.minDistance(maxDistance: Double) = minDistance(maxDistance)
  infix fun Criteria.elemMatch(c: Criteria) = elemMatch(c)
  infix fun Criteria.alike(sample: Example<*>) = alike(sample)
  infix fun Criteria.andDocumentStructureMatches(schema: MongoJsonSchema) = andDocumentStructureMatches(schema)

  infix fun Criteria.eq(where: Any?) = `is`(where?.bsonValue())
  infix fun Criteria.equal(where: Any?) = `is`(where?.bsonValue())

  infix fun KProperty<*>.gt(where: Any) = asWhere() gt where // >
  infix fun KProperty<*>.gte(where: Any) = asWhere() gte where // >=
  fun KProperty<*>.`in`(vararg where: Any?) = asWhere() `in` where.asList() // contains
  infix fun KProperty<*>.`in`(where: Collection<Any?>) = asWhere() `in` where // contains
  infix fun KProperty<*>.`is`(where: Any?) = asWhere() `is` where // ==
  infix fun KProperty<*>.ne(where: Any?) = asWhere() ne where // !=
  infix fun KProperty<*>.lt(where: Any) = asWhere() lt where // <
  infix fun KProperty<*>.lte(where: Any) = asWhere() lte where // <=
  fun KProperty<*>.nin(vararg where: Any?) = asWhere() nin where.asList() // not contains
  infix fun KProperty<*>.nin(where: Collection<Any?>) = asWhere() nin where // not contains

  infix fun KProperty<*>.all(where: Any?) = asWhere() all where
  infix fun KProperty<*>.all(where: Collection<Any?>) = asWhere() all where
  infix fun KProperty<*>.size(s: Int) = asWhere() size s
  infix fun KProperty<*>.exists(b: Boolean) = asWhere() exists b
  infix fun KProperty<*>.type(t: Int) = asWhere() type t
  operator fun KProperty<*>.not() = asWhere().not()
  infix fun KProperty<*>.regex(re: String) = asWhere() regex re
  infix fun KProperty<*>.regex(pattern: Pattern) = asWhere() regex pattern
  infix fun KProperty<*>.regex(pattern: Regex) = asWhere() regex pattern.toPattern()
  infix fun KProperty<*>.regex(regex: BsonRegularExpression) = asWhere() regex regex
  infix fun KProperty<*>.withinSphere(circle: Circle) = asWhere() withinSphere circle
  infix fun KProperty<*>.within(shape: Shape) = asWhere() within shape
  infix fun KProperty<*>.near(point: Point) = asWhere() near point
  infix fun KProperty<*>.nearSphere(point: Point) = asWhere() nearSphere point
  infix fun KProperty<*>.intersects(point: GeoJson<*>) = asWhere() intersects point
  infix fun KProperty<*>.maxDistance(maxDistance: Double) = asWhere() maxDistance maxDistance
  infix fun KProperty<*>.minDistance(maxDistance: Double) = asWhere() minDistance maxDistance
  infix fun KProperty<*>.elemMatch(c: Criteria) = asWhere() elemMatch c
  infix fun KProperty<*>.alike(sample: Example<*>) = asWhere() alike sample
  infix fun KProperty<*>.andDocumentStructureMatches(schema: MongoJsonSchema) =
    asWhere() andDocumentStructureMatches schema

  infix fun KProperty<*>.eq(where: Any?) = asWhere() equal where
  infix fun KProperty<*>.equal(where: Any?) = asWhere() equal where

  infix fun String.gt(where: Any) = asWhere() gt where // >
  infix fun String.gte(where: Any) = asWhere() gte where // >=
  fun String.`in`(vararg where: Any?) = asWhere() `in` where.asList() // contains
  infix fun String.`in`(where: Collection<Any?>) = asWhere() `in` where // contains
  infix fun String.`is`(where: Any?) = asWhere() `is` where // ==
  infix fun String.ne(where: Any?) = asWhere() ne where // !=
  infix fun String.lt(where: Any) = asWhere() lt where // <
  infix fun String.lte(where: Any) = asWhere() lte where // <=
  fun String.nin(vararg where: Any?) = asWhere() nin where.asList() // not contains
  infix fun String.nin(where: Collection<Any?>) = asWhere() nin where // not contains

  infix fun String.all(where: Any?) = asWhere() all where
  infix fun String.all(where: Collection<Any?>) = asWhere() all where
  infix fun String.size(s: Int) = asWhere() size s
  infix fun String.exists(b: Boolean) = asWhere() exists b
  infix fun String.type(t: Int) = asWhere() type t
  operator fun String.not() = asWhere().not()
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
  infix fun String.elemMatch(c: Criteria) = asWhere() elemMatch c
  infix fun String.alike(sample: Example<*>) = asWhere() alike sample
  infix fun String.andDocumentStructureMatches(schema: MongoJsonSchema) = asWhere() andDocumentStructureMatches schema

  infix fun String.eq(where: Any?) = asWhere() equal where
  infix fun String.equal(where: Any?) = asWhere() equal where

  inline infix operator fun invoke(operator: CriteriaBuilder.() -> Criteria): Criteria = this.operator()
  inline infix fun query(operator: CriteriaBuilder.() -> Criteria): Query = Query(this.operator())
  inline infix fun queryObject(operator: CriteriaBuilder.() -> Criteria): Document =
    Query(this.operator()).queryObject

  inline infix fun fieldsObject(operator: CriteriaBuilder.() -> Criteria): Document =
    Query(this.operator()).fieldsObject

  inline infix fun sortObject(operator: CriteriaBuilder.() -> Criteria): Document = Query(this.operator()).sortObject
}
