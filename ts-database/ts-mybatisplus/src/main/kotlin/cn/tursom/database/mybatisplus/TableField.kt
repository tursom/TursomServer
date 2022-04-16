/**
 * SQL 访问增强工具，实现从属性到数据库字段的自动映射
 * @author 王景阔
 * 例：
 * Files::name.tableField
 * 可获得 Files 的 name 属性对应的字段名。
 */

@file:Suppress("unused")

package cn.tursom.database.mybatisplus

import cn.tursom.core.*
import cn.tursom.core.reference.StrongReference
import cn.tursom.core.reflect.getAnnotation
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableName
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper
import java.lang.reflect.Field
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KProperty
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.javaField

data class FieldData(
  val field: Field,
  val name: String,
  val selectionName: String,
  val tableField: TableField?,
  val column: String,
  val exist: Boolean = !field.transient && !field.static && tableField?.exist ?: true,
)

val String.sqlName: String
  get() {
    val sb = StringBuilder()
    val iterator = iterator()
    sb.append(iterator.nextChar().lowercaseChar())
    iterator.forEach {
      if (it.isUpperCase()) {
        sb.append('_')
        sb.append(it.lowercaseChar())
      } else {
        sb.append(it)
      }
    }
    return sb.toString()
  }

val Class<*>.tableName
  get() = getAnnotation<TableName>()?.value ?: simpleName.sqlName

internal val allSelectionFieldMap = ConcurrentHashMap<Class<*>, Array<out String>>()
internal val fieldDataMap = ConcurrentHashMap<Field, FieldData>()
internal val kPropertyFieldDataMap = ConcurrentHashMap<KProperty<*>, StrongReference<FieldData?>>()

val Iterable<Field>.filterNotExists
  get() = asSequence().filter {
    it.getFieldData().exist
  }

@get:JvmName("filterNotExistsField")
val Sequence<Field>.filterNotExists
  get() = filter {
    it.getFieldData().exist
  }

@get:JvmName("filterNotExistsKProperty")
val <T : KProperty<*>> Sequence<T>.filterNotExists
  get() = filter {
    it.getFieldData()?.exist ?: false
  }

fun Field.getFieldData(): FieldData = fieldDataMap.getOrPut(this) {
  val tableField = getAnnotation<TableField>()
  try {
    val tableInfo = TableInfoHelper.getTableInfo(declaringClass)
    tableInfo?.fieldList?.firstOrNull {
      it.field == this
    }?.let { tableFieldInfo ->
      FieldData(
        field = tableFieldInfo.field,
        name = "${tableInfo.tableName}.${tableFieldInfo.column}",
        selectionName = "${tableInfo.tableName}.${tableFieldInfo.column} as ${name.sqlName}",
        tableField = getAnnotation(),
        column = tableFieldInfo.column,
      )
    } ?: run {
      if (tableInfo != null) {
        FieldData(
          field = this,
          name = "${tableInfo.tableName}.${name.sqlName}",
          selectionName = "${tableInfo.tableName}.${name.sqlName}",
          tableField = tableField,
          column = tableField?.value ?: name.sqlName,
        )
      } else null
    }
  } catch (e: Throwable) {
    null
  } ?: run {
    val tableName = declaringClass.tableName
    FieldData(
      field = this,
      name = "$tableName.${tableField?.value ?: name.sqlName}",
      selectionName = if (tableField == null) {
        "$tableName.${name.sqlName}"
      } else {
        "$tableName.${tableField.value} as ${name.sqlName}"
      },
      tableField = tableField,
      column = tableField?.value ?: name.sqlName,
    )
  }
}

fun KProperty<*>.getFieldData(): FieldData? {
  return kPropertyFieldDataMap.getOrPut(this) {
    StrongReference(javaField?.getFieldData() ?: run {
      val owner = owner ?: return@run null
      val enhanceField = findAnnotation<EnhanceField>() ?: return@run null
      if (enhanceField.field.isBlank()) {
        return@run null
      }
      owner.kotlin.allMemberPropertiesSequence.firstOrNull {
        it.name == enhanceField.field
      }?.getFieldData()
    })
  }?.r
}

fun getAllSelectionFieldArray(clazz: Class<*>): Array<out String> = allSelectionFieldMap.getOrPut(clazz) {
  clazz.allFieldsSequence.filterNotExists.map {
    it.getFieldData().column
  }.toList().toTypedArray()
}

fun getAllSelectionField(clazz: Class<*>): List<String> = allSelectionFieldMap.getOrPut(clazz) {
  clazz.allFieldsSequence.filterNotExists.map {
    it.getFieldData().column
  }.toList().toTypedArray()
}.asList()

fun getSelectionField(vararg columns: KProperty<*>): Sequence<String> = getSelectionField(columns.asSequence())
fun getSelectionField(columns: Iterable<KProperty<*>>): Sequence<String> = getSelectionField(columns.asSequence())

fun getSelectionField(columns: Sequence<KProperty<*>>): Sequence<String> = columns.map {
  it.getFieldData()
}.filterNotNull().map {
  it.column
}
