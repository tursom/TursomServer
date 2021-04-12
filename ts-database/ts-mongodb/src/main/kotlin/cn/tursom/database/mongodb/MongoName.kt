package cn.tursom.database.mongodb

import org.bson.Document
import java.lang.reflect.Field
import kotlin.reflect.KProperty

interface MongoName {
  val Class<*>.collectionName: String get() = MongoUtil.collectionName(this)

  val Field.fieldName: String get() = MongoUtil.fieldName(this)

  val KProperty<*>.fieldName get() = MongoUtil.fieldName(this)

  val Any.convertToBson: Document get() = MongoUtil.convertToBson(this)

  operator fun String.rem(field: String) = "$this.$field"
  operator fun String.rem(field: KProperty<*>) = "$this.${field.fieldName}"
  operator fun String.rem(field: Field) = "$this.${field.fieldName}"

  operator fun KProperty<*>.rem(field: String) = "$fieldName.$field"
  operator fun KProperty<*>.rem(field: KProperty<*>) = "$fieldName.${field.fieldName}"
  operator fun KProperty<*>.rem(field: Field) = "$fieldName.${field.fieldName}"

  operator fun Field.rem(field: String) = "$fieldName.$field"
  operator fun Field.rem(field: KProperty<*>) = "$fieldName.${field.fieldName}"
  operator fun Field.rem(field: Field) = "$fieldName.${field.fieldName}"
}