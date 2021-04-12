package cn.tursom.database.mongodb.spring

import cn.tursom.database.mongodb.spring.function.Function1
import cn.tursom.database.mongodb.spring.function.field
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Field
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.javaField


interface MongoName {
  companion object {
    const val dollar = '$'
    inline operator fun <T> invoke(builder: Companion.() -> T) = this.builder()

    inline val KProperty<*>.mongoName: String
      get() = mongoName(this)

    fun arrayElement(elementName: String = "") = if (elementName.isBlank()) {
      "$"
    } else {
      "$[$elementName]"
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

    infix operator fun KProperty<*>.plus(kProperty: KProperty<*>) = "$mongoName.${kProperty.mongoName}"
    infix operator fun KProperty<*>.plus(field: String) = "$mongoName.${field}"
    infix operator fun String.plus(kProperty: KProperty<*>) = "$this.${kProperty.mongoName}"
    infix operator fun String.plus(field: String) = "$this.${field}"

    infix operator fun KProperty<*>.get(kProperty: KProperty<*>) = plus(kProperty)
    infix operator fun KProperty<*>.get(field: String) = plus(field)
    infix operator fun String.get(kProperty: KProperty<*>) = plus(kProperty)
    infix operator fun String.get(field: String) = plus(field)

    infix operator fun KProperty<*>.rem(kProperty: KProperty<*>) = plus(kProperty)
    infix operator fun KProperty<*>.rem(field: String) = plus(field)
    infix operator fun String.rem(kProperty: KProperty<*>) = plus(kProperty)
    infix operator fun String.rem(field: String) = plus(field)
  }

  val dollar get() = Companion.dollar
  val KProperty<*>.mongoName: String
    get() = mongoName(this)

  fun arrayElement(elementName: String = "") = Companion.arrayElement(elementName)
  fun mongoName(kProperty: KProperty<*>): String = Companion.mongoName(kProperty)
  fun mongoName(field: java.lang.reflect.Field): String = Companion.mongoName(field)
  fun <T> mongoName(getter: Function1<*, T>): String = Companion.mongoName(getter)

  infix operator fun KProperty<*>.get(kProperty: KProperty<*>) = Companion { this@get plus kProperty }
  infix operator fun KProperty<*>.get(field: String) = Companion { this@get plus field }
  infix operator fun String.get(kProperty: KProperty<*>) = Companion { this@get plus kProperty }
  infix operator fun String.get(field: String) = Companion { this@get plus field }

  infix operator fun KProperty<*>.rem(kProperty: KProperty<*>) = Companion { this@rem plus kProperty }
  infix operator fun KProperty<*>.rem(field: String) = Companion { this@rem plus field }
  infix operator fun String.rem(kProperty: KProperty<*>) = Companion { this@rem plus kProperty }
  infix operator fun String.rem(field: String) = Companion { this@rem plus field }
}