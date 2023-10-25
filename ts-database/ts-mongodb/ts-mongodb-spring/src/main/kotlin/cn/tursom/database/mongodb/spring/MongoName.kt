package cn.tursom.database.mongodb.spring

import cn.tursom.core.util.StringBuilder
import cn.tursom.database.mongodb.spring.function.Function1
import cn.tursom.database.mongodb.spring.function.field
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Field
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.javaField


@Suppress("MemberVisibilityCanBePrivate")
interface MongoName {
  companion object {
    const val dollar = "$"
    const val dotDollar = "$"
    inline operator fun <T> invoke(builder: Companion.() -> T) = this.builder()

    inline val KProperty<*>.mongoName: String
      get() = mongoName(this)

    fun arrayElement(elementName: String = "") = if (elementName.isBlank()) {
      "$"
    } else {
      "$.$elementName"
    }

    fun mongoName(kProperty: KProperty<*>): String {
      if (kProperty.javaField?.getAnnotation(Id::class.java) != null) {
        return "_id"
      }
      val field = kProperty.javaField?.getAnnotation(Field::class.java)
      if (field != null) {
        if (field.value.isNotEmpty()) {
          return field.value
        }
      }
      return kProperty.name
    }

    fun mongoName(field: java.lang.reflect.Field): String {
      if (field.getAnnotation(Id::class.java) != null) {
        return "_id"
      }
      val fieldAnnotation = field.getAnnotation(Field::class.java)
      if (fieldAnnotation != null) {
        if (fieldAnnotation.value.isNotEmpty()) {
          return fieldAnnotation.value
        }
      }
      return field.name
    }

    fun <T> mongoName(getter: Function1<*, T>): String = mongoName(getter.field!!)

    infix operator fun KProperty<*>.plus(kProperty: KProperty<*>) =
      StringBuilder(mongoName, ".", kProperty.mongoName)

    infix operator fun KProperty<*>.plus(field: String) = StringBuilder(mongoName, ".", field)
    infix operator fun KProperty<*>.plus(field: StringBuilder) = StringBuilder(mongoName, ".").append(field)

    infix operator fun String.plus(kProperty: KProperty<*>) = StringBuilder(this, ".", kProperty.mongoName)
    infix operator fun String.plus(field: String) = StringBuilder(this, ".", field)
    infix operator fun String.plus(field: StringBuilder) = StringBuilder(this, ".").append(field)

    infix operator fun StringBuilder.plus(kProperty: KProperty<*>) = append(".", kProperty.mongoName)
    infix operator fun StringBuilder.plus(field: String) = append(".", field)
    infix operator fun StringBuilder.plus(field: StringBuilder) = append(".", field)

    fun KProperty<*>.arrayElement(kProperty: KProperty<*>) =
      StringBuilder(mongoName, ".", dollar, ".", kProperty.mongoName)

    fun KProperty<*>.arrayElement(field: String) =
      StringBuilder(mongoName, ".", dollar, ".", field)

    fun KProperty<*>.arrayElement(field: StringBuilder) =
      StringBuilder(mongoName, ".", dollar, ".").append(field)

    fun String.arrayElement(kProperty: KProperty<*>) =
      StringBuilder(this, ".", dollar, ".", kProperty.mongoName)

    fun String.arrayElement(field: String) =
      StringBuilder(this, ".", dollar, ".", field)

    fun String.arrayElement(field: StringBuilder) =
      StringBuilder(this, ".", dollar, ".").append(field)

    fun StringBuilder.arrayElement(kProperty: KProperty<*>) =
      append(".", dollar, ".", kProperty.mongoName)

    fun StringBuilder.arrayElement(field: String) =
      append(".", dollar, ".", field)

    fun StringBuilder.arrayElement(field: StringBuilder) =
      append(".", dollar, ".", field)

    infix operator fun KProperty<*>.get(kProperty: KProperty<*>) = arrayElement(kProperty)
    infix operator fun KProperty<*>.get(field: String) = arrayElement(field)
    infix operator fun KProperty<*>.get(field: StringBuilder) = arrayElement(field)
    infix operator fun String.get(kProperty: KProperty<*>) = arrayElement(kProperty)
    infix operator fun String.get(field: String) = arrayElement(field)
    infix operator fun String.get(field: StringBuilder) = arrayElement(field)
    infix operator fun StringBuilder.get(kProperty: KProperty<*>) = arrayElement(kProperty)
    infix operator fun StringBuilder.get(field: String) = arrayElement(field)
    infix operator fun StringBuilder.get(field: StringBuilder) = arrayElement(field)

    infix operator fun KProperty<*>.rem(kProperty: KProperty<*>) = this plus kProperty
    infix operator fun KProperty<*>.rem(field: String) = this plus field
    infix operator fun String.rem(kProperty: KProperty<*>) = this plus kProperty
    infix operator fun String.rem(field: String) = this plus field
    infix operator fun StringBuilder.rem(kProperty: KProperty<*>) = this plus kProperty
    infix operator fun StringBuilder.rem(field: String) = this plus field
  }

  val dollar get() = Companion.dollar
  val dotDollar get() = Companion.dotDollar
  val KProperty<*>.mongoName: String
    get() = mongoName(this)
  val arrayElements: String get() = "$[]"

  fun arrayElement(elementName: String = "") = Companion.arrayElement(elementName)
  fun arrayElement(kProperty: KProperty<*>) = Companion.arrayElement(kProperty.mongoName)
  fun mongoName(kProperty: KProperty<*>): String = Companion.mongoName(kProperty)
  fun mongoName(field: java.lang.reflect.Field): String = Companion.mongoName(field)
  fun <T> mongoName(getter: Function1<*, T>): String = Companion.mongoName(getter)

  val KProperty<*>.arrayFilter: StringBuilder
    get() = Companion {
      StringBuilder(mongoName, ".$[", mongoName, "]")
    }

  fun KProperty<*>.arrayFilter(field: String): StringBuilder = Companion {
    StringBuilder(mongoName, ".$[", field, "]")
  }

  infix operator fun KProperty<*>.get(kProperty: KProperty<*>) = Companion { this@get get kProperty }
  infix operator fun KProperty<*>.get(field: String) = Companion { this@get get field }
  infix operator fun KProperty<*>.get(field: StringBuilder) = Companion { this@get get field }
  infix operator fun String.get(kProperty: KProperty<*>) = Companion { this@get get kProperty }
  infix operator fun String.get(field: String) = Companion { this@get get field }
  infix operator fun String.get(field: StringBuilder) = Companion { this@get get field }
  infix operator fun StringBuilder.get(kProperty: KProperty<*>) = Companion { this@get get kProperty }
  infix operator fun StringBuilder.get(field: String) = Companion { this@get get field }
  infix operator fun StringBuilder.get(field: StringBuilder) = Companion { this@get get field }

  infix operator fun KProperty<*>.rem(kProperty: KProperty<*>) = Companion { this@rem % kProperty }
  infix operator fun KProperty<*>.rem(field: String) = Companion { this@rem % field }
  infix operator fun KProperty<*>.rem(field: StringBuilder) = Companion { this@rem % field.toString() }
  infix operator fun String.rem(kProperty: KProperty<*>) = Companion { this@rem % kProperty }
  infix operator fun String.rem(field: String) = Companion { this@rem % field }
  infix operator fun String.rem(field: StringBuilder) = Companion { this@rem % field.toString() }
  infix operator fun StringBuilder.rem(kProperty: KProperty<*>) = Companion { this@rem % kProperty }
  infix operator fun StringBuilder.rem(field: String) = Companion { this@rem % field }
  infix operator fun StringBuilder.rem(field: StringBuilder) = Companion { this@rem % field.toString() }
}