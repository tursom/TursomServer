package cn.tursom.database

import cn.tursom.database.annotations.TableField
import cn.tursom.database.annotations.TableName
import java.lang.reflect.Field
import java.sql.Types
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.javaField

@Suppress("MemberVisibilityCanBePrivate", "unused")
object SqlUtils {
  inline operator fun <T> invoke(action: SqlUtils.() -> T) = this.action()

  val String.sqlName: String
    get() {
      val sb = StringBuilder()
      val iterator = iterator()
      sb.append(iterator.nextChar().toLowerCase())
      iterator.forEach {
        if (it.isUpperCase()) {
          sb.append('_')
          sb.append(it.toLowerCase())
        } else {
          sb.append(it)
        }
      }
      return sb.toString()
    }

  val KProperty<*>.tableField: String
    get() {
      val tableField = findAnnotation() ?: javaField?.getAnnotation(TableField::class.java)
      return tableField?.fieldName ?: name.sqlName
    }

  val KProperty<*>.selectField: String
    get() {
      val tableField = findAnnotation() ?: javaField?.getAnnotation(TableField::class.java)
      return if (tableField != null) {
        "${tableField.fieldName} as ${name.sqlName}"
      } else {
        name.sqlName
      }
    }

  val Field.tableField: String
    get() {
      val tableField = getAnnotation(TableField::class.java)
      return tableField?.fieldName ?: name.sqlName
    }

  val Field.selectField: String
    get() {
      val tableField = getAnnotation(TableField::class.java)
      return if (tableField != null) {
        "${tableField.fieldName} as ${name.sqlName}"
      } else {
        name.sqlName
      }
    }

  val KClass<*>.tableName: String get() = findAnnotation<TableName>()?.value ?: simpleName!!.sqlName
  val Class<*>.tableName: String get() = getAnnotation(TableName::class.java)?.value ?: simpleName!!.sqlName
}