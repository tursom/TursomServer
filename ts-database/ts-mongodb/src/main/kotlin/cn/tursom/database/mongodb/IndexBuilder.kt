package cn.tursom.database.mongodb

import com.mongodb.client.model.Collation
import com.mongodb.client.model.IndexOptions
import org.bson.Document
import org.bson.conversions.Bson
import java.lang.reflect.Field
import java.util.concurrent.TimeUnit
import kotlin.reflect.KProperty

@Suppress("unused")
class IndexBuilder {
  val indexDocument = Document()
  val indexOption = IndexOptions()

  var background
    get() = indexOption.isBackground
    set(value) {
      indexOption.background(value)
    }
  var unique
    get() = indexOption.isUnique
    set(value) {
      indexOption.unique(value)
    }
  var name: String?
    get() = indexOption.name
    set(value) {
      indexOption.name(value)
    }
  var sparse
    get() = indexOption.isSparse
    set(value) {
      indexOption.sparse(value)
    }
  var expireAfterSeconds: Long?
    get() = indexOption.getExpireAfter(TimeUnit.SECONDS)
    set(value) {
      indexOption.expireAfter(value, TimeUnit.SECONDS)
    }

  fun getExpireAfter(timeUnit: TimeUnit): Long? = indexOption.getExpireAfter(timeUnit)
  fun expireAfter(expireAfter: Long?, timeUnit: TimeUnit): IndexOptions? =
    indexOption.expireAfter(expireAfter, timeUnit)

  var version: Int?
    get() = indexOption.version
    set(value) {
      indexOption.version(value)
    }
  var weights: Bson?
    get() = indexOption.weights
    set(value) {
      indexOption.weights(value)
    }
  var defaultLanguage: String?
    get() = indexOption.defaultLanguage
    set(value) {
      indexOption.defaultLanguage(value)
    }
  var languageOverride: String?
    get() = indexOption.languageOverride
    set(value) {
      indexOption.languageOverride(value)
    }
  var textVersion: Int?
    get() = indexOption.textVersion
    set(value) {
      indexOption.textVersion(value)
    }
  var sphereVersion: Int?
    get() = indexOption.sphereVersion
    set(value) {
      indexOption.sphereVersion(value)
    }
  var bits: Int?
    get() = indexOption.bits
    set(value) {
      indexOption.bits(value)
    }
  var min: Double?
    get() = indexOption.min
    set(value) {
      indexOption.min(value)
    }
  var max: Double?
    get() = indexOption.max
    set(value) {
      indexOption.max(value)
    }
  var bucketSize: Double?
    get() = indexOption.bucketSize
    set(value) {
      indexOption.bucketSize(value)
    }
  var storageEngine: Bson?
    get() = indexOption.storageEngine
    set(value) {
      indexOption.storageEngine(value)
    }
  var partialFilterExpression: Bson?
    get() = indexOption.partialFilterExpression
    set(value) {
      indexOption.partialFilterExpression(value)
    }
  var collation: Collation?
    get() = indexOption.collation
    set(value) {
      indexOption.collation(value)
    }
  var wildcardProjection: Bson
    get() = indexOption.wildcardProjection
    set(value) {
      indexOption.wildcardProjection(value)
    }


  enum class SortEnum(val code: Int) {
    DSC(1), DESC(-1)
  }

  infix fun String.index(sort: SortEnum) {
    indexDocument[this] = sort.code
  }

  operator fun String.unaryPlus() = this index SortEnum.DSC
  operator fun String.unaryMinus() = this index SortEnum.DESC

  infix fun KProperty<*>.index(sort: SortEnum) = MongoUtil.fieldName(this) index sort
  operator fun KProperty<*>.unaryPlus() = +MongoUtil.fieldName(this)
  operator fun KProperty<*>.unaryMinus() = -MongoUtil.fieldName(this)

  infix fun Field.index(sort: SortEnum) = MongoUtil.fieldName(this) index sort
  operator fun Field.unaryPlus() = +MongoUtil.fieldName(this)
  operator fun Field.unaryMinus() = -MongoUtil.fieldName(this)
}