/**
 * SQL 访问增强工具，实现从属性到数据库字段的自动映射
 * @author 王景阔
 * 例：
 * Files::name.tableField
 * 可获得 Files 的 name 属性对应的字段名。
 */

@file:Suppress("unused")

package cn.tursom.database.ktorm

import cn.tursom.database.ktorm.annotations.KtormTableField
import cn.tursom.database.ktorm.annotations.KtormTableName
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.javaGetter

/**
 * 数据库访问增强接口
 * @author 王景阔
 * 设计上使用代理模式实现字段名缓存
 */
interface TableField<T> {
  var tableName: String
  val fieldMap: Map<KProperty1<out T, *>, String>
  val simpFieldMap: Map<KProperty1<out T, *>, String>
  val fullFieldMap: Map<KProperty1<out T, *>, String>
  val properties: Array<out KProperty1<out T, *>>
  val allField: Array<out String>
  val fullNameField: Array<out String>

  //operator fun get(field: KProperty<*>): String = fieldMap[field] ?: field.tableField
  operator fun get(field: KProperty1<out T, *>): String = fieldMap[field] ?: field.simpTableField
}

val <T> Iterable<KProperty1<out T, *>>.filterNotExists: List<KProperty1<out T, *>>
  get() = filter {
    it.javaField != null &&
      (it.findAnnotation() ?: it.javaField?.getAnnotation(Transient::class.java)) == null &&
      !Modifier.isTransient(it.javaField?.modifiers ?: Modifier.TRANSIENT) &&
      it.javaField?.getAnnotation(KtormTableField::class.java)?.exist != false
  }

@get:JvmName("filterNotExistsPair")
val <T> Iterable<Pair<KProperty1<T, *>, *>>.filterNotExists: List<Pair<KProperty1<T, *>, *>>
  get() = filter { (it, _) ->
    it.javaField != null &&
      (it.findAnnotation() ?: it.javaField?.getAnnotation(Transient::class.java)) == null &&
      !Modifier.isTransient(it.javaField?.modifiers ?: Modifier.TRANSIENT) &&
      it.javaField?.getAnnotation(KtormTableField::class.java)?.exist != false
  }

@get:JvmName("filterNotExistsKProperty")
val Iterable<KProperty<*>>.filterNotExists: List<KProperty<*>>
  get() = filter {
    it.javaField != null &&
      (it.findAnnotation() ?: it.javaField?.getAnnotation(Transient::class.java)) == null &&
      !Modifier.isTransient(it.javaField?.modifiers ?: Modifier.TRANSIENT) &&
      it.javaField?.getAnnotation(KtormTableField::class.java)?.exist != false
  }

@get:JvmName("filterNotExistsKPropertyPair")
val Iterable<Pair<KProperty<*>, *>>.filterNotExists: List<Pair<KProperty<*>, *>>
  get() = filter { (it, _) ->
    it.javaField != null &&
      (it.findAnnotation() ?: it.javaField?.getAnnotation(Transient::class.java)) == null &&
      !Modifier.isTransient(it.javaField?.modifiers ?: Modifier.TRANSIENT) &&
      it.javaField?.getAnnotation(KtormTableField::class.java)?.exist != false
  }

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

val KClass<*>.tableName: String get() = findAnnotation<KtormTableName>()?.name ?: simpleName!!.sqlName
val Class<*>.tableName: String get() = getAnnotation(KtormTableName::class.java)?.name ?: simpleName.sqlName
val KProperty<*>.tableFieldName: String? get() = javaField?.getAnnotation(KtormTableField::class.java)?.name
val KProperty<*>.simpTableField: String get() = tableFieldName ?: name.sqlName
val KProperty<*>.selectionTableField: String
  get() = tableFieldName?.let { if (it.isNotEmpty()) "$it as ${name.sqlName}" else null } ?: name.sqlName

inline val <reified T> KProperty1<out T, *>.tableField: String
  get() {
    val companion = T::class.companionObjectInstance
    return if (companion is TableField<*>) {
      @Suppress("UNCHECKED_CAST")
      companion as TableField<T>
      companion[this]
    } else {
      selectionTableField
    }
  }

inline val <reified T : Any> KProperty1<out T, *>.fullTableField: String
  get() {
    val companion = T::class.companionObjectInstance
    return if (companion is TableField<*>) {
      @Suppress("UNCHECKED_CAST")
      companion as TableField<T>
      companion.fullFieldMap[this] ?: "${companion.tableName}.${companion[this]}"
    } else {
      "${T::class.tableName}.$selectionTableField"
    }
  }

val KProperty<*>.fullTableField: String
  get() {
    val kotlin = (javaGetter?.declaringClass ?: javaField?.declaringClass)?.kotlin!!
    return "${kotlin.tableName}.$selectionTableField"
  }

val Field.tableField: String
  get() {
    val tableField = getAnnotation(KtormTableField::class.java)
    return tableField?.name?.let { if (it.isNotEmpty()) "$it as ${name.sqlName}" else null } ?: name.sqlName
  }

val KProperty<*>.directTableField: String
  get() = simpTableField

inline val <reified T> KProperty1<out T, *>.directTableField: String
  get() {
    val companion = T::class.companionObjectInstance
    return if (companion is TableField<*>) {
      @Suppress("UNCHECKED_CAST")
      companion as TableField<T>
      companion.simpFieldMap[this] ?: simpTableField
    } else {
      simpTableField
    }
  }

inline val <reified T> Array<out KProperty1<out T, *>>.directTableField: Array<out String> get() = asList().directTableField
inline val <reified T> Collection<KProperty1<out T, *>>.directTableField: Array<out String>
  get() {
    val companion = T::class.companionObjectInstance
    val fieldList = arrayOfNulls<String>(size)
    filterNotExists.forEach {
      if (companion is TableField<*>) {
        @Suppress("UNCHECKED_CAST")
        companion as TableField<T>
        companion.simpFieldMap[it] ?: it.simpTableField
      } else {
        it.simpTableField
      }
    }
    @Suppress("UNCHECKED_CAST")
    return fieldList as Array<out String>
  }

val Field.directTableField: String
  get() {
    val tableField = getAnnotation(KtormTableField::class.java)
    return tableField?.name?.ifEmpty { null } ?: name.sqlName
  }

inline val <reified T>  Array<out KProperty1<T, *>>.tableField: Array<out String> get() = asList().tableField
inline val <reified T>  Collection<KProperty1<T, *>>.tableField: Array<out String>
  get() {
    val companion = T::class.companionObjectInstance
    return if (companion is TableField<*>) {
      @Suppress("UNCHECKED_CAST")
      companion as TableField<T>
      filterNotExists.map { companion[it] }
    } else {
      filterNotExists.map { it.simpTableField }
    }.toTypedArray()
  }

inline val <reified T> Map<out KProperty1<T, *>, *>.tableField: Map<String, *>
  get() {
    val companion = T::class.companionObjectInstance
    return if (companion is TableField<*>) {
      @Suppress("UNCHECKED_CAST")
      companion as TableField<T>
      mapKeys { companion.simpFieldMap[it.key] ?: it.key.directTableField }
    } else {
      mapKeys { it.key.directTableField }
    }
  }

inline val <reified T> Array<out Pair<KProperty1<T, *>, *>>.tableField: Map<String, *> get() = asList().tableField
inline val <reified T> Collection<Pair<KProperty1<T, *>, *>>.tableField: Map<String, *>
  get() {
    val companion = T::class.companionObjectInstance
    return if (companion is TableField<*>) {
      @Suppress("UNCHECKED_CAST")
      companion as TableField<T>
      filterNotExists.associate { (companion.simpFieldMap[it.first] ?: it.first.directTableField) to it.second }
    } else {
      filterNotExists.associate { it.first.directTableField to it.second }
    }
  }


val Array<out Pair<KProperty<*>, *>>.fullTableField: Map<String, *> get() = asList().fullTableField
val Collection<Pair<KProperty<*>, *>>.fullTableField: Map<String, *>
  get() {
    val map = HashMap<String, Any?>(size)
    filterNotExists.forEach { (property, value) ->
      map[property.fullTableField] = value
    }
    return map
  }

val Array<out KProperty<*>>.fullTableField: Array<out String> get() = asList().fullTableField

@get:JvmName("getKPropertyFullTableField")
val Collection<KProperty<*>>.fullTableField: Array<out String>
  get() = filterNotExists.map { it.fullTableField }.toTypedArray()


inline val <reified T>  Array<out KProperty1<T, *>>.fullTableField: Array<out String> get() = asList().fullTableField
inline val <reified T>  Collection<KProperty1<T, *>>.fullTableField: Array<out String>
  get() {
    val tableName = T::class.tableName
    val companion = T::class.companionObjectInstance
    return if (companion is TableField<*>) {
      @Suppress("UNCHECKED_CAST")
      companion as TableField<T>
      filterNotExists.map { "$tableName.${companion[it]}" }
    } else {
      filterNotExists.map { "$tableName.${it.simpTableField}" }
    }.toTypedArray()
  }
